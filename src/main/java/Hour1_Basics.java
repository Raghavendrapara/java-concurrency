public class Hour1_Basics {

    public static void main(String[] args) {
        // The "Job" definition
        Runnable heavyTask = () -> {
            System.out.println(Thread.currentThread().getName() + ": Starting heavy processing...");

            try {
                // Simulate work (e.g., downloading a file)
                for (int i = 0; i < 5; i++) {
                    // Check if someone asked us to stop
                    if (Thread.currentThread().isInterrupted()) {
                        System.out.println(Thread.currentThread().getName() + ": I was interrupted! Cleaning up...");
                        return; // Graceful exit
                    }

                    System.out.println(Thread.currentThread().getName() + ": Processing chunk " + i);
                    Thread.sleep(1000); // Simulate 1 sec work (This throws InterruptedException!)
                }
            } catch (InterruptedException e) {
                // This block runs if we are interrupted WHILE sleeping
                System.out.println(Thread.currentThread().getName() + ": Woke up by interrupt! Exiting.");
            }
        };

        // Create the worker
        Thread worker = new Thread(heavyTask, "Worker-1");

        // Optional: Daemon threads die automatically when the main application finishes.
        // worker.setDaemon(true);

        System.out.println("Main: Starting worker...");
        worker.start(); // Calls the OS to create a thread

        // Main thread continues in parallel
        try {
            System.out.println("Main: Waiting 2.5 seconds...");
            Thread.sleep(2500);

            System.out.println("Main: Tired of waiting. Interrupting worker!");
            worker.interrupt(); // Sets the interrupt flag on the worker

            worker.join(); // Main thread pauses here until worker effectively dies
            System.out.println("Main: Worker is dead. System exiting.");

        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}