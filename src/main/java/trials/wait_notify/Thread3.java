package trials.wait_notify;

public class Thread3 extends Thread {
    private final Object lock;

    public Thread3(Object lock) {
        this.lock = lock;
    }

    @Override
    public void run() {
            try {
                Thread.sleep(6000);

                synchronized (lock) {
                    System.out.println(Thread.currentThread().getName()+" Notfiying waiter 3");
                    lock.notify();
                }
            } catch (InterruptedException e){
                e.printStackTrace();
            }

    }
}
