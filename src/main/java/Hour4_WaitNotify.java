import java.util.LinkedList;
import java.util.Queue;

public class Hour4_WaitNotify {

    // The Shared Resource
    private static class SimpleBlockingQueue<T> {
        private Queue<T> queue = new LinkedList<>();
        private int capacity;

        public SimpleBlockingQueue(int capacity) {
            this.capacity = capacity;
        }

        // PRODUCER adds item
        public synchronized void put(T item) throws InterruptedException {
            // STEP 1: Check condition using WHILE (not IF)
            while (queue.size() == capacity) {
                System.out.println("Queue full! Producer waiting...");
                wait(); // Releases lock, sleeps. Re-acquires lock upon waking.
            }

            // STEP 2: Do the work
            queue.add(item);
            System.out.println("Produced: " + item);

            // STEP 3: Notify others
            notifyAll(); // Wake up consumers who might be waiting
        }

        // CONSUMER takes item
        public synchronized T take() throws InterruptedException {
            while (queue.isEmpty()) {
                System.out.println("Queue empty! Consumer waiting...");
                wait();
            }

            T item = queue.remove();
            System.out.println("Consumed: " + item);

            notifyAll(); // Wake up producers who might be waiting
            return item;
        }
    }

    public static void main(String[] args) {
        SimpleBlockingQueue<Integer> buffer = new SimpleBlockingQueue<>(2);

        // Producer Thread
        new Thread(() -> {
            try {
                for (int i = 0; i < 5; i++) {
                    buffer.put(i);
                    Thread.sleep(500); // Simulate work
                }
            } catch (InterruptedException e) { e.printStackTrace(); }
        }).start();

        // Consumer Thread
        new Thread(() -> {
            try {
                for (int i = 0; i < 5; i++) {
                    buffer.take();
                    Thread.sleep(2000); // Consumer is slower
                }
            } catch (InterruptedException e) { e.printStackTrace(); }
        }).start();
    }
}