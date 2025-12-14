import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.Random;

public class Hour7_Locks {
    private static final Lock lock1 = new ReentrantLock();
    private static final Lock lock2 = new ReentrantLock();

    public static void main(String[] args) {
        Thread t1 = new Thread(() -> transfer(lock1, lock2, "Thread-1"));
        Thread t2 = new Thread(() -> transfer(lock2, lock1, "Thread-2"));
        Thread t3 = new Thread(() -> transfer(lock2, lock1, "Thread-3"));


        t1.start();
        t2.start();
    }

    private static void transfer(Lock fromLock, Lock toLock, String name) {
        boolean gotFrom = false;
        boolean gotTo = false;

        try {
            // Try to get first lock immediately
            gotFrom = fromLock.tryLock();
            // Simulate work
            Thread.sleep(10);

            if (gotFrom) {
                // Try to get second lock immediately
                gotTo = toLock.tryLock();
            }

            if (gotFrom && gotTo) {
                System.out.println(name + ": Transfer SUCCESS!");
            } else {
                System.out.println(name + ": Failed to acquire both locks. Retrying later...");
                // In real life, you would loop and retry here
            }

        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        } finally {
            // CRITICAL: Only unlock what you actually locked
            if (gotTo) toLock.unlock();
            if (gotFrom) fromLock.unlock();
        }
    }
}