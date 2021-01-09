package auction.project.backend;

import auction.project.Product;
import com.datastax.driver.core.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.datastax.driver.extras.codecs.jdk8.LocalTimeCodec;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;


public class BackendSession {
    private static final Logger logger = LoggerFactory.getLogger(BackendSession.class);

    public static BackendSession instance = null;

    private Session session;

    public BackendSession(String contactPoint, String keyspace) throws BackendException {

        Cluster cluster = Cluster.builder().addContactPoint(contactPoint).withQueryOptions(new QueryOptions().setConsistencyLevel(ConsistencyLevel.ONE)).build();
        cluster.getConfiguration().getCodecRegistry().register(LocalTimeCodec.instance);
        try {
            session = cluster.connect(keyspace);
        } catch (Exception e) {
            throw new BackendException("Could not connect to the cluster. " + e.getMessage() + ".", e);
        }
        prepareStatements();
    }

    private static PreparedStatement INSERT_INTO_AUCTIONS;
    private static PreparedStatement DELETE_ALL_FROM_AUCTIONS;
    private static PreparedStatement SELECT_ALL_FROM_AUCTIONS;
    private static PreparedStatement SELECT_PRODUCT_AUCTIONS;
    private static PreparedStatement UPDATE_PRICE_AUCTIONS;
    private static PreparedStatement UPDATE_SOLD_AUCTIONS;
    private static PreparedStatement UPDATE_BUYOUT_AUCTIONS;

    private static final String AUCTION_FORMAT = "- %-10s  %-10s %-8s %-8s %-8s %-8s\n";

    private void prepareStatements() throws BackendException {
        try {
            SELECT_ALL_FROM_AUCTIONS = session.prepare("SELECT * FROM auctions;");
            SELECT_PRODUCT_AUCTIONS = session.prepare("SELECT * FROM auctions where product_id = ?;");
            DELETE_ALL_FROM_AUCTIONS = session.prepare("TRUNCATE auctions;");
            INSERT_INTO_AUCTIONS = session.prepare("INSERT INTO auctions (product_id,auction_end,buy_out_price,current_price,is_sold,buyer_id) VALUES (?,?,?,?,?,-1);");
            UPDATE_PRICE_AUCTIONS = session.prepare("UPDATE auctions set current_price = ?,buyer_id = ? WHERE product_id = ?;");
            UPDATE_SOLD_AUCTIONS = session.prepare("UPDATE auctions set is_sold = ?,buyer_id = ? WHERE product_id = ?;");
            UPDATE_BUYOUT_AUCTIONS = session.prepare("UPDATE auctions set is_sold = ?, current_price = ?,buyer_id = ? WHERE product_id = ?;");
        } catch (Exception e) {
            throw new BackendException("Could not prepare statements. " + e.getMessage() + ".", e);
        }

        logger.info("Statements prepared");
    }

    public void upsertProduct(int buyOutPrice, int startingPrice, String auctionEnd) throws BackendException {
        BoundStatement bs = new BoundStatement(INSERT_INTO_AUCTIONS);
        UUID uuid = UUID.randomUUID();
        java.time.LocalTime auctionEndTime = java.time.LocalTime.parse(auctionEnd);
        bs.bind(uuid,auctionEndTime, buyOutPrice, startingPrice,  false);

        try {
            session.execute(bs);
        } catch (Exception e) {
            throw new BackendException("Could not perform an upsert. " + e.getMessage() + ".", e);
        }

        logger.info("Auction product " + uuid + " upserted");
    }

    public Product selectProduct(UUID product_id) throws BackendException {
        Product fetchedProduct = null;
        BoundStatement bs = new BoundStatement(SELECT_PRODUCT_AUCTIONS);
        bs.bind(product_id);

        ResultSet rs = null;

        try {
            rs = session.execute(bs);
        } catch (Exception e) {
            throw new BackendException("Could not perform a query. " + e.getMessage() + ".", e);
        }

        for (Row row : rs) {

            UUID ruuid = row.getUUID("product_id");
            int rbuy_out_price = row.getInt("buy_out_price");
            long rbuyer_id = row.getLong("buyer_id");
            int rcurrent_price = row.getInt("current_price");
            boolean ris_sold = row.getBool("is_sold");
            LocalTime rauction_end_conv = LocalTime.ofNanoOfDay(row.getTime("auction_end"));
            fetchedProduct = new Product(ruuid, rauction_end_conv, rbuy_out_price, rcurrent_price, ris_sold, rbuyer_id);
        }
        return fetchedProduct;
    }

    public List<Product> selectAll() throws  BackendException {
        List<Product> fetchedProducts = new ArrayList<>();
        BoundStatement bs = new BoundStatement(SELECT_ALL_FROM_AUCTIONS);

        ResultSet rs = null;

        try {
            rs = session.execute(bs);
        } catch (Exception e) {
            throw new BackendException("Could not perform a query. " + e.getMessage() + ".", e);
        }

        for (Row row : rs) {

            UUID ruuid = row.getUUID("product_id");
            int rbuy_out_price = row.getInt("buy_out_price");
            long rbuyer_id = row.getLong("buyer_id");
            int rcurrent_price = row.getInt("current_price");
            boolean ris_sold = row.getBool("is_sold");
            LocalTime rauction_end_conv = LocalTime.ofNanoOfDay(row.getTime("auction_end"));
            Product tmpProd = new Product(ruuid, rauction_end_conv, rbuy_out_price, rcurrent_price, ris_sold, rbuyer_id);
            fetchedProducts.add(tmpProd);
        }
        return fetchedProducts;
    }

    public String printAll() throws BackendException {
        StringBuilder builder = new StringBuilder();
        BoundStatement bs = new BoundStatement(SELECT_ALL_FROM_AUCTIONS);

        ResultSet rs = null;

        try {
            rs = session.execute(bs);
        } catch (Exception e) {
            throw new BackendException("Could not perform a query. " + e.getMessage() + ".", e);
        }

        for (Row row : rs) {
            UUID ruuid = row.getUUID("product_id");
            int rbuy_out_price = row.getInt("buy_out_price");
            long rbuyer_id = row.getLong("buyer_id");
            int rcurrent_price = row.getInt("current_price");
            boolean ris_sold = row.getBool("is_sold");
            LocalTime rauction_end_conv = LocalTime.ofNanoOfDay(row.getTime("auction_end"));

            builder.append(String.format(AUCTION_FORMAT, ruuid, rauction_end_conv, rbuy_out_price, rbuyer_id, rcurrent_price, ris_sold));
        }

        return builder.toString();
    }

    public void updateProductPrice(int current_price, UUID product_id, long user_id) throws BackendException {
        BoundStatement bs = new BoundStatement(UPDATE_PRICE_AUCTIONS);
        bs.bind(current_price,user_id, product_id);

        try {
            session.execute(bs);
        } catch (Exception e) {
            throw new BackendException("Could not perform an update of current price on " + product_id + ". " + e.getMessage() + ".", e);
        }

        logger.info("Auction product " + product_id + " current price updated: " + current_price);
    }

    public void updateProductSold(boolean is_sold, UUID product_id) throws BackendException {
        BoundStatement bs = new BoundStatement(UPDATE_SOLD_AUCTIONS);
        bs.bind(is_sold, product_id);

        try {
            session.execute(bs);
        } catch (Exception e) {
            throw new BackendException("Could not perform an update of sold status on " + product_id + ". " + e.getMessage() + ".", e);
        }

        logger.info("Auction product " + product_id + " sold status updated.");
    }

    public void updateProductBuyOut(boolean is_sold, int buy_out_price, UUID product_id, long user_id) throws BackendException {
        BoundStatement bs = new BoundStatement(UPDATE_BUYOUT_AUCTIONS);
        bs.bind(is_sold, buy_out_price, user_id, product_id);

        try {
            session.execute(bs);
        } catch (Exception e) {
            throw new BackendException("Could not perform an update of sold status on " + product_id + ". " + e.getMessage() + ".", e);
        }

        logger.info("Auction product " + product_id + " sold status updated.");
    }


    public void deleteAll() throws BackendException {
        BoundStatement bs = new BoundStatement(DELETE_ALL_FROM_AUCTIONS);

        try {
            session.execute(bs);
        } catch (Exception e) {
            throw new BackendException("Could not perform a delete operation. " + e.getMessage() + ".", e);
        }

        logger.info("All products deleted from auction");
    }

//    public void close(){
//        try {
//            if (session != null) {
//                session.getCluster().close();
//            }
//        } catch (Exception e) {
//            logger.error("Could not close existing cluster", e);
//        }
//    }

    protected void finalize() {
        try {
            if (session != null) {
                session.getCluster().close();
            }
        } catch (Exception e) {
            logger.error("Could not close existing cluster", e);
        }
    }

}
