import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Hour7_HorseLatch {
    public static void main(String[] args) throws InterruptedException {
        CountDownLatch startGun = new CountDownLatch(1);
        CountDownLatch race = new CountDownLatch(5);
        System.out.println("Start race Ready!");

        try (ExecutorService pool = Executors.newFixedThreadPool(5)) {
            for (int i = 0; i < 5; i++) {
                pool.submit(() -> {
                    try {
                        startGun.await();
                        long duration = (long) (Math.random() * 2000);
                        System.out.println(Thread.currentThread().getName() + " working for " + duration + "ms");
                        Thread.sleep(duration);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    } finally {
                        System.out.println(Thread.currentThread().getName() + " DONE.");
                        race.countDown(); // Decrement count
                    }
                });
                startGun.countDown();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        System.out.println("Race over");
    }
}
