package Model;
import java.util.logging.Logger;

public class Quickstart {

    private static final Logger logger = Logger.getLogger(Quickstart.class.getName());

    public static void main(String[] args) {
        logger.info("Logging INFO with java.util.logging");
        logger.severe("Logging ERROR with java.util.logging");
    }
}
// [END logging_jul_quickstart]
