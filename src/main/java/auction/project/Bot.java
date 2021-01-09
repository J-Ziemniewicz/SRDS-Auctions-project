package auction.project;

import auction.project.backend.BackendException;
import auction.project.backend.BackendSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


import java.io.IOException;
import java.time.LocalTime;
import java.util.*;


public class Bot implements Runnable {
    private static final Logger logger = LoggerFactory.getLogger(Bot.class);
    private BackendSession session;
    private static final String PROPERTIES_FILENAME = "config.properties";
    private static final String PRODUCT_FORMAT = "- %-10s | %-10s | %-8s | %-8s | %-8s | %-8s";

    private int pickRandom(List<Product> productList) {
        Product prod;
        Random rand = new Random();
        LocalTime time = java.time.LocalTime.now();
        do {
            int randomIdx =  rand.nextInt(productList.size());
            prod = productList.get(randomIdx);
            if(prod.isIs_sold() || time.isAfter(prod.getAuction_end())){
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
        long rbuyer_id = prod.getBuyer_id();
        int rcurrent_price = prod.getCurrent_price();
        boolean ris_sold = prod.isIs_sold();
        LocalTime rauction_end_conv = prod.getAuction_end();
        builder.append(String.format(PRODUCT_FORMAT, ruuid, rauction_end_conv, rbuy_out_price, rbuyer_id, rcurrent_price, ris_sold));
        logger.info("Choosen product "+builder.toString());

    }
//TODO: biding function
    private void startBiding(Product product, User user) throws BackendException, InterruptedException {
        UUID prod_id = product.getProduct_id();
        long user_id = user.getId();
        do{

            while (product.getBuyer_id()== user_id){
                Thread.sleep(500);
                product = session.selectProduct(prod_id);
            }

            Random rand = new Random();
            boolean result;
            int if_buy_out = rand.nextInt(10);

            if (if_buy_out==9){
                logger.info("Buying out "+prod_id);
                result = user.buyOutProduct(product);
                if(result){

                    logger.info("Product bought successfully");
                }else {

                    logger.info("Unsuccessful bought");
                }
            }
            else {
                int price = product.getCurrent_price();
                int upBid = rand.nextInt(product.getBuy_out_price() / 5);

                logger.info("Biding price " + (price + upBid));
                result = user.bidTheProduct(price + upBid, product);
                if (result) {

                    logger.info("Product bid successfully");
                } else {

                    logger.info("Unsuccessful bid");
                }
            }

            Thread.sleep(500);
            product = session.selectProduct(prod_id);

        }while (!product.isIs_sold());


    }
    @Override
    public void run() {
        try {

            logger.info("Thread " + Thread.currentThread().getId()+" is up...");
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

            if(productList.size()>0) {
                int prodIdx = pickRandom(productList);
                if(prodIdx>-1) {

                    Product chosenProduct = productList.get(prodIdx);

                    printProduct(chosenProduct);
                    Product updateProduct = session.selectProduct(chosenProduct.getProduct_id());
                    if(!updateProduct.isIs_sold()) {
                        User botUser = new User(session, Thread.currentThread().getId());
                        startBiding(updateProduct, botUser);
                    }else {
                        logger.info("Product "+updateProduct.getProduct_id()+" already sold");
                    }
                }
                else {
                    logger.info("No available products on auction");
                }
            }
            else {
                logger.info("No available products on auction");
            }





        } catch (BackendException | InterruptedException backEx) {
            backEx.printStackTrace();
        }
        logger.info("Ending biding");
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.exit(0);
    }

}
