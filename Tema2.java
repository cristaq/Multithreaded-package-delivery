import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

public class Tema2 {
    public static void main(String[] args) {

        int numberOfThreads = Integer.parseInt(args[1]);
        File input = new File(args[0]);
        ExecutorService orderPool = Executors.newFixedThreadPool(numberOfThreads);
        ExecutorService productPool = Executors.newFixedThreadPool(numberOfThreads);
        AtomicInteger inQueueOrder = new AtomicInteger(0);
        try {
            BufferedWriter ordersWriter = new BufferedWriter(new FileWriter(new File("orders_out.txt")));
            BufferedWriter productsWriter = new BufferedWriter(new FileWriter(new File("order_products_out.txt")));
            for(int i = 0; i < numberOfThreads; i++) {
                inQueueOrder.incrementAndGet();
                orderPool.submit(new Order(i, numberOfThreads, input, orderPool,
                        inQueueOrder, productPool, ordersWriter, productsWriter));
            }
            
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
}
