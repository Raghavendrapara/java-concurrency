import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class Hour5_Executors {
    public static void main(String[] args) {
        long startTime = System.currentTimeMillis();

        // 1. Create the Pool (The "Team")
        // We have 10 tasks, but only 2 threads.
        // 8 tasks will have to wait in the queue.
        ExecutorService executor = Executors.newFixedThreadPool(17);

        // 2. Submit Tasks (The "Job Orders")
        for (int i = 1; i <= 2000; i++) {
            int taskId = i;
            executor.submit(() -> {
                String threadName = Thread.currentThread().getName();
                System.out.println("Task " + taskId + " started by " + threadName);

                try {
                    Thread.sleep(10); // Simulate 1 sec work
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }

                System.out.println("Task " + taskId + " finished.");
            });
        }

        // 3. Shutdown (Stop accepting new tasks)
        System.out.println("All tasks submitted.");
        executor.shutdown();

        // 4. Wait for completion (Optional but common)
        try {
            // "Blocks" until all tasks are done or timeout occurs
            if (!executor.awaitTermination(60, TimeUnit.SECONDS)) {
                executor.shutdownNow(); // Force kill if taking too long
            }
        } catch (InterruptedException e) {
            executor.shutdownNow();
        }

        long endTime = System.currentTimeMillis();
        System.out.println("Total Time: " + (endTime - startTime) + "ms");
    }
}