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

### Cache Line padding via contended
```Java
import jdk.internal.vm.annotation.Contended;

public class OptimizedCounter {
    
        /*
            Old method
            public class PaddedCounter {
                 volatile long value = 0;
    
                Padding: 7 longs * 8 bytes = 56 bytes + 8 bytes (value) = 64 bytes
                 // This pushes the NEXT variable onto a new cache line.
                long p1, p2, p3, p4, p5, p6, p7; 
            }
     */
    @Contended 
    volatile long value; // JVM puts this on its own isolated cache line
}
```

### Singleton Optimization
```Java
public static DatabaseConnection getInstance() {
    // CHECK 1: The Performance Optimization
    if (instance == null) { 
        
        synchronized (DatabaseConnection.class) {
            
            // CHECK 2: The Safety Guarantee
            if (instance == null) { 
                instance = new DatabaseConnection();
            }
        }
    }
    return instance;
}
```


