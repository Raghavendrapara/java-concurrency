public class Hour2Volatile {
    // WITHOUT volatile, this loop might NEVER end
    private static volatile boolean running = true;

    public static void main(String[] args) throws InterruptedException {
        new Thread(() -> {
            System.out.println("Worker started.");
            while (running) {
                // Busy wait - just spinning
            }
            System.out.println("Worker stopped.");
        }).start();

        Thread.sleep(1000);
        System.out.println("Main: Setting running to false");
        running = false; // Main thread changes the value
    }
}