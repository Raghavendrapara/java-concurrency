package trials.exceptions;

class WorkerThread implements Runnable {
    @Override
    public void run() {
        try {
            while (!Thread.currentThread().isInterrupted()) {
                System.out.println("Checking for updates...");
                Thread.sleep(10); // Simulating work
            }
        } catch (InterruptedException e) {
            System.out.println("Thread interrupted, shutting down gracefully.");
        }
    }
}

public class ThreadInterruptionExample {
    public static void main(String[] args) throws InterruptedException {
        Thread thread = new Thread(new WorkerThread());
        thread.start();
        Thread.sleep(51); // Let it run for some time
        thread.interrupt(); // Interrupt the thread
    }
}