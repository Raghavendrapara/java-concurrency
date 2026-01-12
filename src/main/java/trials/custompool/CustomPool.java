package trials.custompool;

import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class CustomPool {

    static void main() {
        try (ThreadPoolExecutor threadPoolExecutor = new ThreadPoolExecutor(
                2, 3, 60, TimeUnit.SECONDS, new LinkedBlockingQueue<>(2)
        )) {
            threadPoolExecutor.submit(()->{
                try {
                    Thread.sleep(2000);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
                System.out.println(Thread.currentThread().getName() +" : "+threadPoolExecutor.getQueue().size());
            });
            threadPoolExecutor.submit(()->{
                try {
                    Thread.sleep(2000);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
                System.out.println(Thread.currentThread().getName() +" : "+threadPoolExecutor.getQueue().size());
            });
            threadPoolExecutor.submit(()->{
                try {
                    Thread.sleep(2000);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
                System.out.println(Thread.currentThread().getName() +" : "+threadPoolExecutor.getQueue().size());
            });
            threadPoolExecutor.submit(()->{
                try {
                    Thread.sleep(2000);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
                System.out.println(Thread.currentThread().getName() +" : "+threadPoolExecutor.getQueue().size());
            });
            threadPoolExecutor.submit(()->{
                try {
                    Thread.sleep(2000);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
                System.out.println(Thread.currentThread().getName() +" : "+threadPoolExecutor.getQueue().size());
            });

        }
    }
}
