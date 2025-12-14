import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

public class Hour8_Collections {
    public static void main(String[] args) {
        // Queue with capacity 10. Fair = true (FIFO ordering for waiting threads)
        BlockingQueue<String> queue = new ArrayBlockingQueue<>(10, true);

        // Producer
        new Thread(() -> {
            try {
                for (int i = 0; i < 20; i++) {
                    String msg = "Message-" + i;
                    // Blocks if full. No synchronized, no wait/notify needed.
                    queue.put(msg);
                    System.out.println("Produced: " + msg);
                    Thread.sleep(100);
                }
                queue.put("POISON_PILL"); // Standard pattern to stop consumer
            } catch (InterruptedException e) { e.printStackTrace(); }
        }).start();

        // Consumer
        new Thread(() -> {
            try {
                while (true) {
                    // Blocks if empty.
                    String msg = queue.take();

                    if (msg.equals("POISON_PILL")) {
                        System.out.println("Consumer: Received stop signal.");
                        break;
                    }

                    System.out.println("Consumer: Processed " + msg);
                    Thread.sleep(500); // Simulate slow processing
                }
            } catch (InterruptedException e) { e.printStackTrace(); }
        }).start();
    }
}