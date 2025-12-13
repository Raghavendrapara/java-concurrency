public class Hour3_Synchronized {
    private static int counter = 0;
    // This object serves ONLY as a lock.
    // Best Practice: Use a dedicated final object, not "this" or "String".
    private static final Object lock = new Object();

    public static void main(String[] args) throws InterruptedException {
        Runnable task = () -> {
            for (int i = 0; i < 10000; i++) {
                // CRITICAL SECTION START
                synchronized (lock) {
                    counter++;
                }
                // CRITICAL SECTION END
            }
        };

        Thread t1 = new Thread(task);
        Thread t2 = new Thread(task);

        t1.start(); t2.start();
        t1.join(); t2.join();

        System.out.println("Actual: " + counter); // Always 20000
    }

}