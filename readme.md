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

### await() try with resources deadlock

```
Deadlock created by the try-with-resources block.
This is a subtle behavior change in modern Java.
The Reason It Hangs
In Java 21+, the closing brace } of a try (ExecutorService pool ...) block automatically blocks and waits for all tasks to finish (it calls close(), which calls awaitTermination()).
Main Thread: Reaches the closing brace }. It says: "I cannot leave this block until all 5 tasks are finished."
Worker Threads: Are sitting inside the block at startGun.await(). They say: "We cannot finish until the Main Thread fires the gun."
The Gun: The startGun.countDown() line is outside the block. The Main Thread can never reach it because it is stuck at the closing brace.
```
```java
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Hour7_HorseLatch_Fixed {
    public static void main(String[] args) throws InterruptedException {
        // 1. Setup the Latches
        CountDownLatch startGun = new CountDownLatch(1);
        CountDownLatch finishLine = new CountDownLatch(5);

        System.out.println("Horses are walking to the gate...");

        // Note: We remove the 'try-with-resources' block here just to prove
        // that the 'finishLine.await()' is what actually makes us wait.
        ExecutorService pool = Executors.newFixedThreadPool(5);

        for (int i = 0; i < 5; i++) {
            pool.submit(() -> {
                try {
                    // STEP A: Wait for the gun
                    startGun.await(); 
                    
                    // STEP B: Run!
                    long duration = (long) (Math.random() * 2000);
                    System.out.println(Thread.currentThread().getName() + " is running...");
                    Thread.sleep(duration);
                    
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } finally {
                    System.out.println(Thread.currentThread().getName() + " CROSSED THE LINE.");
                    // STEP C: Signal completion
                    finishLine.countDown(); 
                }
            });
        }

        // 2. The Main Thread pauses briefly to ensure all threads are blocked on await()
        Thread.sleep(100); 
        System.out.println("Ready...");
        System.out.println("Set...");
        
        // 3. BANG! (Now they all start exactly here)
        startGun.countDown(); 

        // 4. Wait for everyone to finish
        finishLine.await(); 
        System.out.println("Race Over! Shutting down.");
        
        pool.shutdown();
    }
}
```

### Concurrent Collections
```java

```

### Atomic usage

```
//EAGER - for primitives/avoiding specific locks
map.putIfAbsent("key", 1);
// THE DEADLOCK TRAP

Map<String, Integer> map = new ConcurrentHashMap<>();
//use putIfAbsent
map.computeIfAbsent("A", k -> {
    // CRITICAL ERROR: Accessing the map inside the computation!
    // If "B" hashes to the same bucket as "A", this thread hangs forever.
    map.put("B", 1); 
    return 2;
});

computeIfAbsent locks the bucket while the lambda runs:

If your computation takes 5 seconds (e.g., a DB call),
you are blocking all other threads that want to read/write any key in that same bucket for 5 seconds.

//LAZY -> use for heavy tasks to reduce latency
map.computeIfAbsent("key", k -> 1);
```



