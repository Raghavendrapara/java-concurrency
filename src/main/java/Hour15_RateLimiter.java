import java.util.Map;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicLong;

/**
 * CAPSTONE PROJECT: Production-Grade Rate Limiter
 * * Tech Stack:
 * - Java 21 LTS (Stable Features Only)
 * - Virtual Threads (via ExecutorService)
 * - Semaphore & ConcurrentHashMap
 */
public class Hour15_RateLimiter {

    // --- CONFIGURATION ---
    private static final int MAX_RPS = 5;
    private static final int MAX_BURST = 5;
    private static final int TOTAL_USERS = 10;
    private static final int TOTAL_REQUESTS = 100;

    public static void main(String[] args) throws InterruptedException {
        RateLimiterService service = new RateLimiterService(MAX_RPS, MAX_BURST);

        // STABLE APPROACH: Use ExecutorService with Virtual Threads.
        // This requires NO "--enable-preview" flags. It is standard Java 21.
        try (ExecutorService executor = Executors.newVirtualThreadPerTaskExecutor()) {

            System.out.println("--- Starting Load Test (Stable Virtual Threads) ---");
            long start = System.currentTimeMillis();

            // Latch to wait for all users to finish simulating
            CountDownLatch latch = new CountDownLatch(TOTAL_USERS);

            for (int userId = 1; userId <= TOTAL_USERS; userId++) {
                String userKey = "User-" + userId;

                executor.submit(() -> {
                    try {
                        simulateUserTraffic(service, userKey);
                    } finally {
                        latch.countDown(); // Mark this user as done
                    }
                });
            }

            // Main thread waits here until latch hits 0
            latch.await();

            long end = System.currentTimeMillis();
            System.out.println("--- Load Test Finished ---");
            System.out.println("Total Time: " + (end - start) + "ms");
        }
    }

    private static void simulateUserTraffic(RateLimiterService service, String userId) {
        for (int i = 0; i < TOTAL_REQUESTS; i++) {
            // No changes to logic: just standard Java calls
            if (service.allowRequest(userId)) {
                // Passed
            }
            // Simple jitter
            try { Thread.sleep(10); } catch (InterruptedException e) { Thread.currentThread().interrupt(); }
        }
    }

    // --- CORE SERVICE (Unchanged & Thread-Safe) ---
    static class RateLimiterService {
        private final int ratePerSecond;
        private final int maxBurst;
        private final Map<String, UserBucket> userBuckets = new ConcurrentHashMap<>();

        public RateLimiterService(int ratePerSecond, int maxBurst) {
            this.ratePerSecond = ratePerSecond;
            this.maxBurst = maxBurst;
        }

        public boolean allowRequest(String userId) {
            // computeIfAbsent is atomic and highly optimized in ConcurrentHashMap
            UserBucket bucket = userBuckets.computeIfAbsent(userId, k -> new UserBucket(maxBurst, ratePerSecond));
            return bucket.tryConsume();
        }
    }

    // --- BUCKET LOGIC ---
    static class UserBucket {
        private final Semaphore tokens;
        private final int maxCapacity;
        private final int ratePerSecond;
        private final AtomicLong lastRefillTimestamp;

        public UserBucket(int maxCapacity, int ratePerSecond) {
            this.maxCapacity = maxCapacity;
            this.ratePerSecond = ratePerSecond;
            this.tokens = new Semaphore(maxCapacity);
            this.lastRefillTimestamp = new AtomicLong(System.nanoTime());
        }

        public boolean tryConsume() {
            refillTokens();
            return tokens.tryAcquire();
        }

        // Synchronized per-bucket (Striped Locking)
        private synchronized void refillTokens() {
            long now = System.nanoTime();
            long last = lastRefillTimestamp.get();
            long elapsedNanos = now - last;

            // 1 second in nanoseconds
            if (elapsedNanos > 1_000_000_000) {
                long newTokens = (elapsedNanos / 1_000_000_000) * ratePerSecond;

                if (newTokens > 0) {
                    long available = tokens.availablePermits();
                    long space = maxCapacity - available;
                    long tokensToAdd = Math.min(newTokens, space);

                    if (tokensToAdd > 0) {
                        tokens.release((int) tokensToAdd);
                    }
                    lastRefillTimestamp.set(now);
                }
            }
        }
    }
}