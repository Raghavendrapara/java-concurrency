import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Hour7_Latch {
    public static void main(String[] args) throws InterruptedException {
        int workers = 3;
        CountDownLatch latch = new CountDownLatch(workers);
        try (ExecutorService pool = Executors.newFixedThreadPool(workers)) {

            System.out.println("Main: Waiting for services to initialize...");

            // Simulate 3 services starting up
            for (int i = 0; i < workers; i++) {
                pool.submit(() -> {
                    try {
                        long duration = (long) (Math.random() * 2000);
                        System.out.println(Thread.currentThread().getName() + " working for " + duration + "ms");
                        Thread.sleep(duration);
                    } catch (InterruptedException e) {
                    } finally {
                        System.out.println(Thread.currentThread().getName() + " DONE.");
                        latch.countDown(); // Decrement count
                    }
                });
            }

            // Main thread freezes here. It consumes 0 CPU while waiting.
            latch.await();

            System.out.println("Main: All services ready. Starting Server!");
        }
    }
}