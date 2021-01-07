package auction.project;

import auction.project.backend.BackendException;
import auction.project.backend.BackendSession;

import java.io.IOException;
import java.util.Properties;

//TODO: Bot should randomly choose product to bid  -> selectAll operation and choose from products
//TODO: Randomize price and buy_out
//TODO: [if enough time] Bid on multiple products
//TODO: When won biding start bid on new product

public class Bot implements Runnable{
    private static final String PROPERTIES_FILENAME = "config.properties";

    private void start() throws IOException, BackendException {

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

        //Operation for testing purpose

        session.upsertProduct(156,15,"16:00:00");

        String output = session.selectAll();

        System.out.println("Bot products: \n" + output);


        System.exit(0);
    }

    @Override
    public void run() {
        try {
            this.start();
        } catch (BackendException | IOException backEx) {
            backEx.printStackTrace();
        }
    }

}
