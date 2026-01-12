package trials.wait_notify;

public class Main {
    static void main() {
        Object lock = new Object();
        Thread1 thread1 = new Thread1(lock);
        Thread2 thread2 = new Thread2(lock);
        Thread3 thread3 = new Thread3(lock);
        Thread4 thread4 = new Thread4(lock);
        thread1.start();
        thread2.start();
        thread3.start();
        thread4.start();
    }
}
