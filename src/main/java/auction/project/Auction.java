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

        session.upsertUser("PP", "Adam", 609, "A St");
        session.upsertUser("PP", "Ola", 509, null);
        session.upsertUser("UAM", "Ewa", 720, "B St");
        session.upsertUser("PP", "Kasia", 713, "C St");

        String output = session.selectAll();
        System.out.println("Users: \n" + output);

//        session.deleteAll();

        System.exit(0);
    }
}


//public class Auction {
//    public static void main(String[] args) {
//        for (int i = 1; i <= 100; i++) {
//            System.out.println(convert(i));
//        }
//    }
//
//    public static String convert(int fizzBuzz) {
//        if (fizzBuzz % 15 == 0) {
//            return "FizzBuzz";
//        }
//        if (fizzBuzz % 3 == 0) {
//            return "Fizz";
//        }
//        if (fizzBuzz % 5 == 0) {
//            return "Buzz";
//        }
//        return String.valueOf(fizzBuzz);
//    }
//}
