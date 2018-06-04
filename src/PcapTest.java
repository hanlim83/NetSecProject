import java.io.IOException;
import org.pcap4j.core.PcapNetworkInterface;
import org.pcap4j.util.NifSelector;
public class PcapTest {
    public static void main(String[] args)
    {
        // The class that will store the network device
        // we want to use for capturing.
        PcapNetworkInterface device = null;

        // Pcap4j comes with a convenient method for listing
        // and choosing a network interface from the terminal
        try {
            // List the network devices available with a prompt
            device = new NifSelector().selectNetworkInterface();
        } catch (IOException e) {
            e.printStackTrace();
        }

        System.out.println("You chose: " + device);
    }
}
