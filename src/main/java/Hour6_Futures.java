import java.util.concurrent.*;

public class Hour6_Futures {
    public static void main(String[] args) throws ExecutionException, InterruptedException {
        try (ExecutorService executor = Executors.newFixedThreadPool(2)) {
            Future<String> userFuture = executor.submit(
                    ()->{
                        System.out.println(" Fetching user from DB.....");
                        Thread.sleep(20000);
                        return "User: Gemma";
                    }
            );
            Future<Double> stockPrice = executor.submit(
                    () -> {
                        System.out.println("Getting stonks pmrice...");
                        Thread.sleep(10000);
                        return 150.5;
                    }
            );
            Thread.sleep(500);
            try{
                String user = userFuture.get(1,TimeUnit.SECONDS);
                Double stonk = stockPrice.get(2,TimeUnit.SECONDS);
                System.out.println(user + "   "+stonk);
            } catch (TimeoutException e) {
                userFuture.cancel(true);
                stockPrice.cancel(true);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}