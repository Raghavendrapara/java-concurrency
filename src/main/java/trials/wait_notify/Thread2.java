package trials.wait_notify;

public class Thread2 extends Thread{
    private final Object lock;

    public Thread2(Object lock) {
        this.lock = lock;
    }

    @Override
    public void run() {
        try {
            Thread.sleep(3000);

            synchronized (lock) {
                System.out.println(Thread.currentThread().getName()+" Notfiying waiter 2");
                lock.notifyAll();
            }
        } catch (InterruptedException e){
            e.printStackTrace();
        }

    }
}
