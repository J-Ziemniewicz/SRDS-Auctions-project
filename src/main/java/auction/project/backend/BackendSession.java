package auction.project.backend;

import com.datastax.driver.core.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.datastax.driver.extras.codecs.jdk8.LocalTimeCodec;

import java.time.LocalTime;
import java.util.UUID;

// TODO: UPDATE Price if is_sold == FALSE
// TODO: GDY czas ten sam update'uj ten z wieksza kwota

public class BackendSession {
    private static final Logger logger = LoggerFactory.getLogger(BackendSession.class);

    public static BackendSession instance = null;

    private Session session;

    public BackendSession(String contactPoint, String keyspace) throws BackendException {

        Cluster cluster = Cluster.builder().addContactPoint(contactPoint).build();
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

    private static final String AUCTION_FORMAT = "- %-10s  %-10s %-8s %-8s %-8s %-8s %-8s\n";

    private void prepareStatements() throws BackendException {
        try {
            SELECT_ALL_FROM_AUCTIONS = session.prepare("SELECT * FROM auctions;");
            DELETE_ALL_FROM_AUCTIONS = session.prepare("TRUNCATE auctions;");
            INSERT_INTO_AUCTIONS = session.prepare("INSERT INTO auctions (product_id,auction_end,buy_out_price,current_price,is_sold,starting_price) VALUES (?,?,?,?,?,?);");
        } catch (Exception e) {
            throw new BackendException("Could not prepare statements. " + e.getMessage() + ".", e);
        }

        logger.info("Statements prepared");
    }

    public void upsertProduct(int buyOutPrice, int startingPrice,String auctionEnd) throws BackendException {
        BoundStatement bs = new BoundStatement(INSERT_INTO_AUCTIONS);
        UUID uuid = UUID.randomUUID();
        java.time.LocalTime auctionEndTime = java.time.LocalTime.parse(auctionEnd);
        bs.bind(uuid,auctionEndTime, buyOutPrice, startingPrice,  false , startingPrice);

        try {
            session.execute(bs);
        } catch (Exception e) {
            throw new BackendException("Could not perform an upsert. " + e.getMessage() + ".", e);
        }

        logger.info("Auction product " + uuid + " upserted");
    }

    public String selectAll() throws BackendException {
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
            int rbuyer_id = row.getInt("buyer_id");
            int rcurrent_price = row.getInt("current_price");
            boolean ris_sold = row.getBool("is_sold");
            int rstarting_price = row.getInt("starting_price");
            LocalTime rauction_end_conv = LocalTime.ofNanoOfDay(row.getTime("auction_end"));

            builder.append(String.format(AUCTION_FORMAT, ruuid, rauction_end_conv, rbuy_out_price, rbuyer_id,rcurrent_price,ris_sold,rstarting_price));
        }

        return builder.toString();
    }

//    public void upsertUser(String companyName, String name, int phone, String street) throws BackendException {
//        BoundStatement bs = new BoundStatement(INSERT_INTO_USERS);
//        bs.bind(companyName, name, phone, street);
//
//        try {
//            session.execute(bs);
//        } catch (Exception e) {
//            throw new BackendException("Could not perform an upsert. " + e.getMessage() + ".", e);
//        }
//
//        logger.info("User " + name + " upserted");
//    }

    public void deleteAll() throws BackendException {
        BoundStatement bs = new BoundStatement(DELETE_ALL_FROM_AUCTIONS);

        try {
            session.execute(bs);
        } catch (Exception e) {
            throw new BackendException("Could not perform a delete operation. " + e.getMessage() + ".", e);
        }

        logger.info("All products deleted from auction");
    }

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
