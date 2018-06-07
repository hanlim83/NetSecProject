import java.io.IOException;
import org.pcap4j.core.NotOpenException;
import org.pcap4j.core.PacketListener;
import org.pcap4j.core.PcapHandle;
import org.pcap4j.core.PcapNativeException;
import org.pcap4j.core.PcapNetworkInterface;
import org.pcap4j.core.PcapNetworkInterface.PromiscuousMode;
import org.pcap4j.packet.Packet;
import org.pcap4j.packet.TcpPacket;
import org.pcap4j.packet.UdpPacket;
import org.pcap4j.util.NifSelector;

public class pcapTesting {
    static PcapNetworkInterface getNetworkDevice() {
        PcapNetworkInterface device = null;
        try {
            device = new NifSelector().selectNetworkInterface();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return device;
    }

    public static void main(String[] args) throws PcapNativeException, NotOpenException {
        // The code we had before
        PcapNetworkInterface device = getNetworkDevice();
        System.out.println("You chose: " + device);

        // New code below here
        if (device == null) {
            System.out.println("No device chosen.");
            System.exit(1);
        }

        // Open the device and get a handle
        int snapshotLength = 65536; // in bytes
        int readTimeout = 50; // in milliseconds
        final PcapHandle handle;
        handle = device.openLive(snapshotLength, PromiscuousMode.PROMISCUOUS, readTimeout);

        // Create a listener that defines what to do with the received packets
        PacketListener listener = new PacketListener() {
            @Override
            public void gotPacket(Packet packet) {
                // Override the default gotPacket() function and process packet
                if (packet instanceof TcpPacket) {
                    System.out.println("Packet Type: TCP");
                    System.out.println((packet.get(TcpPacket.class).getHeader().getSrcPort()));
                    System.out.println((packet.get(TcpPacket.class).getHeader().getDstPort()));
                    System.out.println((packet.get(TcpPacket.class).getHeader().getSequenceNumber()));
                    System.out.println((packet.get(TcpPacket.class).getHeader().getAcknowledgmentNumber()));
                    System.out.println((packet.get(TcpPacket.class).getHeader().getSyn()));
                    System.out.println((packet.get(TcpPacket.class).getHeader().getRst()));
                    System.out.println((packet.get(TcpPacket.class).getHeader().getFin()));
                }
                else if (packet instanceof UdpPacket){
                    System.out.println("Packet Type: UDP");
                    System.out.println((packet.get(UdpPacket.class).getHeader().getSrcPort()));
                    System.out.println((packet.get(UdpPacket.class).getHeader().getDstPort()));
                    System.out.println((packet.get(UdpPacket.class).getHeader().getLength()));
                }
                else {
                    System.out.println(packet.getHeader());
                    System.out.println(packet.getPayload());
                }
            }
        };

        // Tell the handle to loop using the listener we created
        try {
            int maxPackets = 50;
            handle.loop(maxPackets, listener);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        // Cleanup when complete
        handle.close();
    }
}

