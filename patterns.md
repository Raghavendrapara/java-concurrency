## Concurrency Patterns
### StartGate pattern
```Java
import java.util.concurrent.CountDownLatch;

public class Hour1_Basics {

    public static void main(String[] args) {
        CountDownLatch startGate = new CountDownLatch(1);

        Runnable heavyTask = () -> {
            try {
                startGate.await(); 
                
                System.out.println(Thread.currentThread().getName() + " Running heavy task");

            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        };

        Thread worker = new Thread(heavyTask, "Worker-1");
        Thread worker2 = new Thread(heavyTask, "Worker-2");
        
        worker.start();
        worker2.start();

        System.out.println("Main waiting for 2.5 seconds...."); 

        startGate.countDown(); 

        try {
            Thread.sleep(2500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}

```
### Two Phase termination Pattern
```java 
public void processHeavyData() {

    while (hasMoreData()) {
        
        if (Thread.currentThread().isInterrupted()) {
            cleanUpPartialWork();
            return; 
        }

        try {
            Step1_Calculate();
            
            Thread.sleep(100); 
            
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt(); 
            
            cleanUpPartialWork();
            return; // STOP!
        }
    }
}
```