import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.concurrent.Semaphore;

public class Product implements Runnable {
    private String id;
    private int productNumber;
    private File input;
    private Semaphore semaphore;
    private BufferedWriter productsWriter;

    public Product(String id, int productNumber, File input, Semaphore semaphore, BufferedWriter productsWriter) {
        this.id = id;
        this.productNumber = productNumber;
        this.input = input;
        this.semaphore = semaphore;
        this.productsWriter = productsWriter;
    }

    @Override
    public void run() {
        BufferedReader reader;
        try {
            reader = new BufferedReader(new FileReader(input));
            int i = 0;
            String line = "";
            while (i != productNumber && line != null) {
                line = reader.readLine();
                String[] temp = line.split(",");
                if (temp[0].equals(id)) {
                    i++;
                }
            }
            
            productsWriter.write(line + ",shipped\n");
            productsWriter.flush();
            
            reader.close();
            semaphore.release();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
