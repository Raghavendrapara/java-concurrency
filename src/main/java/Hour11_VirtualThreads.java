import java.time.Duration;
import java.time.Instant;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;

public class Hour11_VirtualThreads {
    public static void main(String[] args) throws InterruptedException {
        long startTime = System.currentTimeMillis();

        // New factory in Java 21
        ThreadFactory vFactory = Thread.ofVirtual().name("virtual-", 0).factory();

        int taskCount = 100_000; // Let's start with 100k
        var threads = new Thread[taskCount];

        System.out.println("Starting " + taskCount + " threads...");

        for (int i = 0; i < taskCount; i++) {
            threads[i] = vFactory.newThread(() -> {
                try {
                    // When this sleeps, the OS thread is RELEASED to do other work.
                    Thread.sleep(1000);
                } catch (InterruptedException e) {}
            });
            threads[i].start();
        }

        // Wait for them
        for (Thread t : threads) {
            t.join();
        }

        long endTime = System.currentTimeMillis();
        System.out.println("Finished in: " + (endTime - startTime) + "ms");
    }
}