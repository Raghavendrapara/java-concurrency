package trials.wait_notify;

public class Thread4 extends Thread{

    private final Object lock;

    public Thread4(Object lock) {
        this.lock = lock;
    }

    @Override
    public void run() {

        synchronized (lock) {
            try {
                System.out.println(Thread.currentThread().getName()+" Thread 4 is waiting");
                lock.wait();
                System.out.println(Thread.currentThread().getName()+" Thread 4 is serving");
            } catch (InterruptedException e) {
                e.printStackTrace();
                Thread.currentThread().interrupt();
            }
        }
    }
}
