import org.pcap4j.core.PcapNetworkInterface;
import org.pcap4j.util.NifSelector;

import java.io.IOException;

public class NIFSelector {

    static PcapNetworkInterface getNetworkDevice() {
        PcapNetworkInterface device = null;
        try {
            device = new NifSelector().selectNetworkInterface();
            System.out.println(device.isRunning());
            System.out.println(device.isLocal());
            System.out.println(device.isLoopBack());
            System.out.println(device.isUp());
        } catch (IOException e) {
            e.printStackTrace();
        }
        return device;
    }

    public static void main(String[] args) {
        PcapNetworkInterface device = getNetworkDevice();
        System.out.println("You chose: " + device);
    }
}