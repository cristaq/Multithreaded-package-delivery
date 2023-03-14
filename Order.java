import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Semaphore;
import java.util.concurrent.atomic.AtomicInteger;

public class Order implements Runnable {
    private int lineNumber;
    private int numberOfThreads;
    private File directory;
    private ExecutorService orderPool;
    private ExecutorService productPool;
    private AtomicInteger inQueueOrder;
    private BufferedWriter ordersWriter;
    private BufferedWriter productsWriter;

    public Order(int lineNumber, int numberOfThreads, File directory,
                 ExecutorService orderPool, AtomicInteger inQueueOrder,
                 ExecutorService productPool, BufferedWriter ordersWriter, 
                 BufferedWriter productsWriter) {
        this.lineNumber = lineNumber;
        this.numberOfThreads = numberOfThreads;
        this.directory = directory;
        this.orderPool = orderPool;
        this.inQueueOrder = inQueueOrder;
        this.productPool = productPool;;
        this.ordersWriter = ordersWriter;
        this.productsWriter = productsWriter;
    }

    @Override
    public void run() {
        File orders = new File(directory + "/orders.txt");
        BufferedReader reader;
        try{
            reader = new BufferedReader(new FileReader(orders));
            for(int i = 0; i < lineNumber; i++) {
                reader.readLine();
            }

            String line = reader.readLine();
            if(line == null) {
                int left = inQueueOrder.decrementAndGet();
                if (left == 0) {
                    reader.close();
                    productPool.shutdown();
                    productsWriter.close();
                    ordersWriter.close();
                    orderPool.shutdown();
                }
                
                
                return;
            }

            String[] temp = line.split(",");
            Semaphore semaphore = new Semaphore(1 - Integer.parseInt(temp[1]));

            inQueueOrder.incrementAndGet();
            orderPool.submit(new Order(numberOfThreads + lineNumber, numberOfThreads,
                    directory, orderPool, inQueueOrder, productPool, ordersWriter, productsWriter));

            for(int i = 1; i <= Integer.parseInt(temp[1]); i++) {
                productPool.submit(new Product(temp[0], i, new File(directory + "/order_products.txt"), 
                                        semaphore, productsWriter));
            }

            reader.close();

            semaphore.acquire();
            if(Integer.parseInt(temp[1]) != 0) {
                ordersWriter.write(line + ",shipped\n");
                ordersWriter.flush();
            }

            int left = inQueueOrder.decrementAndGet();
            if (left == 0) {
                productPool.shutdown();
                productsWriter.close();
                ordersWriter.close();
                orderPool.shutdown();
            }
        }
        catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }      
}
