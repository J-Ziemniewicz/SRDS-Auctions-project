package auction.project;

import auction.project.backend.BackendException;
import auction.project.backend.BackendSession;


import java.io.IOException;
import java.time.LocalTime;
import java.util.List;
import java.util.Properties;
import java.util.Random;
import java.util.UUID;

//TODO: Randomize price and buy_out
//TODO: [if enough time] Bid on multiple products
//TODO: When won biding start bid on new product

public class Bot implements Runnable{
    private static final String PROPERTIES_FILENAME = "config.properties";
    private static final String PRODUCT_FORMAT = "- %-10s | %-10s | %-8s | %-8s | %-8s | %-8s\n";

    private Product pickRandom(List<Product> productList){
        Random rand = new Random();
        Product prod = productList.get(rand.nextInt(productList.size()));

        return prod;
    }

    private void printProduct(Product prod){
        StringBuilder builder = new StringBuilder();
        UUID ruuid = prod.getProduct_id();
        int rbuy_out_price = prod.getBuy_out_price();
        int rbuyer_id = prod.getBuyer_id();
        int rcurrent_price = prod.getCurrent_price();
        boolean ris_sold = prod.isIs_sold();
        LocalTime rauction_end_conv = prod.getAuction_end();
        builder.append(String.format(PRODUCT_FORMAT, ruuid, rauction_end_conv, rbuy_out_price, rbuyer_id,rcurrent_price,ris_sold));
         System.out.println(builder.toString());
    }


    @Override
    public void run() {
        try {
            System.out.println(Thread.currentThread().getName());
            String contactPoint = null;
            String keyspace = null;

            Properties properties = new Properties();
            try {
                properties.load(Auction.class.getClassLoader().getResourceAsStream(PROPERTIES_FILENAME));

                contactPoint = properties.getProperty("contact_point");
                keyspace = properties.getProperty("keyspace");
            } catch (IOException ex) {
                ex.printStackTrace();
            }
            BackendSession session = new BackendSession(contactPoint, keyspace);

            List<Product> productList = session.selectAll();
            Thread.sleep(1000);
            System.out.println("\n\nThread: " + Thread.currentThread().getName() + " chosen product:");
            if(productList.size()>0) {
                Product chosenProduct = pickRandom(productList);
                printProduct(chosenProduct);
            }
            else {
                System.out.println("No products on auction");
            }


            //Operation for testing purpose
            session.upsertProduct(157,15,"16:00:00");
            Thread.sleep(1000);



        } catch (BackendException | InterruptedException backEx) {
            backEx.printStackTrace();
        }
        System.exit(0);
    }

}
