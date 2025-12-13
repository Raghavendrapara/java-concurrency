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


### Singleton modern methodology

```java
public enum DatabaseSingleton {
    INSTANCE;

    private String connectionString;

    DatabaseSingleton() {
        System.out.println("Initializing Database Connection...");
        this.connectionString = "jdbc:mysql://localhost:3306/mydb";
    }

    public void executeQuery(String query) {
        System.out.println("Executing '" + query + "' on " + connectionString);
    }
    
    public void setConnectionString(String conn) {
        this.connectionString = conn;
    }
}
```

### Wait inside loops not if block

```java
public synchronized void put(T item) throws InterruptedException {
            // STEP 1: Check condition using WHILE (not IF)
            // IF can cause issues with the OS scheduler
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
```


### ExecutorService

```java
public class Executor {
    static void main(String[] args) {
        ExecutorService executor = Executors.newFixedThreadPool(17);

        for (int i = 0; i < 20; i++) {
            int taskId = i;
            executor.submit(() -> {
                String threadName = Thread.currentThread().getName();

                try {
                    Thread.sleep(10); // Simulate 1 sec work
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }

            });
        }

        // 3. Shutdown (Stop accepting new tasks)
        executor.shutdown();

        // 4. Wait for completion (Optional but common)
        try {
            // "Blocks" until all tasks are done or timeout occurs
            if (!executor.awaitTermination(60, TimeUnit.SECONDS)) {
                executor.shutdownNow(); // Force kill if taking too long
            }
        } catch (
                InterruptedException e) {
            executor.shutdownNow();
        }
    }
}

```


### SingleThreadExecutor
```
Self-Healing background processor. This is perfect for things like:
Event logging agents.
File system watchers.
Keep-alive heartbeats.
Even if a bad plugin or a corrupted file crashes the current operation, the service stays alive to process the next one.
```
```java
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class SingleThreadSurvival {
    public static void main(String[] args) {
        ExecutorService executor = Executors.newSingleThreadExecutor();

        // TASK A: The Saboteur
        executor.submit(() -> {
            String name = Thread.currentThread().getName();
            System.out.println(name + ": Task A started.");
            throw new RuntimeException("CRASH! Task A died.");
        });

        // TASK B: The Survivor
        executor.submit(() -> {
            String name = Thread.currentThread().getName();
            System.out.println(name + ": Task B started. I am a new thread!");
        });

        executor.shutdown();
    }
}
```




