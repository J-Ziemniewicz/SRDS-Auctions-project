package auction.project;

import java.io.IOException;
import java.util.Properties;

import auction.project.backend.BackendException;
import auction.project.backend.BackendSession;


public class Auction {

    private static final String PROPERTIES_FILENAME = "config.properties";


    public static void main(String[] args) throws  BackendException, InterruptedException {
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

//       session.upsertProduct(156,50,"21:10:00");
//        session.upsertProduct(250,70,"21:56:00");
//        session.upsertProduct(250,70,"21:56:00");
//        session.upsertProduct(250,70,"21:56:00");
        String output = session.printAll();

        System.out.println("Auction products: \n" + output);



        Thread.sleep(500);

        Thread bot1 = new Thread(new Bot());
        Thread bot2 = new Thread(new Bot());
        Thread bot3 = new Thread(new Bot());
        Thread bot4 = new Thread(new Bot());

        bot1.start();
        bot2.start();
        bot3.start();
        bot4.start();

        bot1.join();
        bot2.join();
        bot3.join();
        bot4.join();
//        session.deleteAll();

        System.exit(0);
    }
}



