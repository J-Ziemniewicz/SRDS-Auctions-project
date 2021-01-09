package auction.project;

import auction.project.backend.BackendException;
import auction.project.backend.BackendSession;


import java.io.IOException;
import java.time.LocalTime;
import java.util.*;

//TODO: Randomize price and buy_out
//TODO: [if enough time] Bid on multiple products
//TODO: When won biding start bid on new product

public class Bot implements Runnable {
    private BackendSession session;
    private static final String PROPERTIES_FILENAME = "config.properties";
    private static final String PRODUCT_FORMAT = "- %-10s | %-10s | %-8s | %-8s | %-8s | %-8s\n";

    //TODO: finish pickRandom function
    private int pickRandom(List<Product> productList) {
        Product prod;
        Random rand = new Random();
        do {
            int randomIdx =  rand.nextInt(productList.size());
            Date date= new Date();
            prod = productList.get(randomIdx);
            if(prod.isIs_sold()){
                productList.remove(randomIdx);
            }
            else {
                return randomIdx;
            }
        }while(productList.size()>0);
        return -1;
    }

    private void printProduct(Product prod) {
        StringBuilder builder = new StringBuilder();
        UUID ruuid = prod.getProduct_id();
        int rbuy_out_price = prod.getBuy_out_price();
        int rbuyer_id = prod.getBuyer_id();
        int rcurrent_price = prod.getCurrent_price();
        boolean ris_sold = prod.isIs_sold();
        LocalTime rauction_end_conv = prod.getAuction_end();
        builder.append(String.format(PRODUCT_FORMAT, ruuid, rauction_end_conv, rbuy_out_price, rbuyer_id, rcurrent_price, ris_sold));
        System.out.println(builder.toString());
    }
//TODO: biding function
    private void startBiding(Product product){
        int currentPrice = product.getCurrent_price();

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
            session = new BackendSession(contactPoint, keyspace);

            List<Product> productList = session.selectAll();
            Thread.sleep(500);
            System.out.println("\n\nThread: " + Thread.currentThread().getName() + " chosen product:");
            if(productList.size()>0) {
                int prodIdx = pickRandom(productList);
                if(prodIdx>-1) {

                    Product chosenProduct = productList.get(prodIdx);
                    printProduct(chosenProduct);
                    User botUser = new User(session, Thread.currentThread().getId());
                    botUser.bidTheProduct(60, chosenProduct);
                }
                else {
                    System.out.println("No products on auction");
                }
            }
            else {
                System.out.println("No products on auction");
            }





            //Operation for testing purpose
//            session.upsertProduct(157,15,"16:00:00");
            Thread.sleep(500);



        } catch (BackendException | InterruptedException backEx) {
            backEx.printStackTrace();
        }
        System.exit(0);
    }

}
