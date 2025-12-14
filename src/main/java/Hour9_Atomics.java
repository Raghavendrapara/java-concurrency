import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.LongAdder;
import java.util.stream.IntStream;

public class Hour9_Atomics {
    // AtomicInteger holds a volatile int internally
    private static AtomicInteger atomicCounter = new AtomicInteger(0);

    public static void main(String[] args) throws InterruptedException {
        Runnable task = () -> {
            for (int i = 0; i < 10000; i++) {
                // One atomic instruction. No locks.
                atomicCounter.incrementAndGet();
            }
        };
        Hour9_Atomics hour9Atomics = new Hour9_Atomics();
        LongAdder longAdder = new LongAdder();
        IntStream.range(1,12345).parallel().forEach(longAdder::add);
        System.out.println(longAdder.longValue());


        Thread t1 = new Thread(task);
        Thread t2 = new Thread(task);

        t1.start(); t2.start();
        t1.join(); t2.join();
        hour9Atomics.multiplyByTwo();

        System.out.println("Actual: " + atomicCounter.get()); // Always 20000
    }
    public void multiplyByTwo() {
        atomicCounter.updateAndGet(x -> x * 2);
    }
}