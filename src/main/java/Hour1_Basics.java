import java.util.concurrent.CountDownLatch;

public class Hour1_Basics {

    public static void main(String[] args) {

        CountDownLatch startGate = new CountDownLatch(1);
        Runnable heavyTask = () -> {
            try {
                startGate.await();

                System.out.println(Thread.currentThread().getName() + "Running heavy task");
                for (int i = 0; i < 5; i++) {
                    if(Thread.currentThread().isInterrupted()){
                        System.out.println(Thread.currentThread().getName() + " Interrupted : Cleaning Up");
                        return;
                    }
                    System.out.println(Thread.currentThread().getName()+"Processing chunk "+i);
                    Thread.sleep(1000);

                }
            } catch (InterruptedException e) {
                System.out.println(Thread.currentThread().getName()+" Woke up by interrupt : exiting");
            }
        };
        Thread worker = new Thread(heavyTask, "Worker-1");
        worker.start();
        Thread worker2 = new Thread(heavyTask, "Worker-2");
        worker2.start();

        System.out.println("Main waiting for 2.5 seconds....");
        startGate.countDown();
        try{
            Thread.sleep(2500);
            System.out.println("Main tired of waiting");
            worker.interrupt();
            worker.join();
            worker2.join();
            System.out.println("Main: worker is dead, System exiting");
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}