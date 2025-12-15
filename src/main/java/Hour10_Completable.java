import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Hour10_Completable {
    public static void main(String[] args) {
        // ALWAYS use a custom pool for IO. 
        // By default, CF uses ForkJoinPool.commonPool() which is small.
        ExecutorService pool = Executors.newFixedThreadPool(3);

        System.out.println("Main: Order received.");

        CompletableFuture.supplyAsync(() -> {
                    System.out.println("   -> Fetching user...");
                    sleep(1000);
                    return "User:Bob";
                }, pool)

                .thenApply(user -> {
                    System.out.println("   -> Fetching credit score for " + user);
                    sleep(1000);
                    return 750; // Credit Score
                })

                .thenAccept(score -> {
                    if (score > 700) {
                        System.out.println("   -> APPROVED! (Score: " + score + ")");
                    } else {
                        System.out.println("   -> REJECTED.");
                    }
                })

                .join(); // Just for main() to wait. In a real server, you'd return the Future.

        System.out.println("Main: Done.");
        pool.shutdown();
    }

    private static void sleep(int ms) {
        try { Thread.sleep(ms); } catch (Exception e) {}
    }
}