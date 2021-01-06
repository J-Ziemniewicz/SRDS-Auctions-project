package auction.project;

import java.io.IOException;
import java.util.Properties;

import auction.project.backend.BackendException;
import auction.project.backend.BackendSession;

public class Auction {

    private static final String PROPERTIES_FILENAME = "config.properties";

    public static void main(String[] args) throws IOException, BackendException {
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

        session.upsertProduct(155,52,"16:00:00");

        String output = session.selectAll();

        System.out.println("Auction products: \n" + output);



//        session.deleteAll();

        System.exit(0);
    }
}



