import java.util.Random;

public class UnstoppableExercise {
    public static void main(String[] args) throws InterruptedException {
        Thread worker = new Thread(() -> {
            Random random = new Random();

            while (true) {
                // Heavy CPU work
                Math.sin(random.nextDouble());

                if (Thread.currentThread().isInterrupted()) {
                    System.out.println("Worker: Signal received! Stopping.");
                    break;
                }
            }
        });

        worker.start();

        System.out.println("Main: Let it run for 10ms...");
        Thread.sleep(10);

        System.out.println("Main: Interrupting now!");
        worker.interrupt(); // This sets a boolean flag = true
    }
}