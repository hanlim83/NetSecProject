package Model;

import com.sun.jna.Platform;
import org.pcap4j.core.*;
import org.pcap4j.core.PcapNetworkInterface.PromiscuousMode;
import org.pcap4j.packet.Packet;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Calendar;

public class ContinuousNetworkCapture {
    //Pre-Defined Variables
    private static final String COUNT_KEY  = ContinuousNetworkCapture.class.getName() + ".count";
    private static final int COUNT  = Integer.getInteger(COUNT_KEY, -1); // -1 -> loop infinite
    private static final String READ_TIMEOUT_KEY  = ContinuousNetworkCapture.class.getName() + ".readTimeout";
    private static final int READ_TIMEOUT  = Integer.getInteger(READ_TIMEOUT_KEY, 100); // [ms]
    private static final String SNAPLEN_KEY = ContinuousNetworkCapture.class.getName() + ".snaplen";
    private static final int SNAPLEN = Integer.getInteger(SNAPLEN_KEY, 65536); // [bytes]
    private PcapNetworkInterface Netinterface;
    private PcapHandle Phandle;
    private PcapDumper dumper;
    private int pktCount = 0;

    //Data Variables
    private long PacketsReceived,PacketsDropped;
    private String filePath;

    public ContinuousNetworkCapture(PcapNetworkInterface nif, String filePath) {
        this.Netinterface = nif;
        this.filePath = filePath;
    }

    //Overrides default packet handling
    PacketListener listener = new PacketListener() {
        @Override
        public void gotPacket(Packet packet) {
            try {
                dumper.dump(packet);
            } catch (NotOpenException e) {
                e.printStackTrace();
            }
        }
    };
    public long getPacketsReceived() {
        return PacketsReceived;
    }

    public long getPacketsDropped() {
        return PacketsDropped;
    }

    //Packet count increment
    synchronized private void incrementCount(){
        pktCount++;
    }

    //get the packet count
    synchronized private int getPacketCount(){
        return pktCount;
    }

    public void printStat(){
        PcapStat ps;
        try {
            ps = Phandle.getStats();
            PacketsReceived = ps.getNumPacketsReceived();
            PacketsDropped = ps.getNumPacketsDropped();
        } catch (PcapNativeException | NotOpenException e) {
            // Auto-generated catch block
            e.printStackTrace();
        }
    }

    //start capturing the packets
    public void startSniffing(){
                try{
                    Phandle = Netinterface.openLive(SNAPLEN, PromiscuousMode.PROMISCUOUS, READ_TIMEOUT);
                    dumper = Phandle.dumpOpen(filePath);
                    Phandle.loop(COUNT, listener);
                } catch(PcapNativeException e){
                    e.printStackTrace();
                }
        catch (InterruptedException e) {
            System.err.println("Stopped successfully");
                }
        catch (NotOpenException e) {
                    e.printStackTrace();
                }
        finally{
                    printStat();
                    dumper.close();
                    Phandle.close();
                }
            }

    //stop capturing the packets
    public void stopSniffing(){
        System.out.println("\nStopping Sniffing...\n");
        try {
            Phandle.breakLoop();
            printStat();
        } catch (NotOpenException e) {
            e.printStackTrace();
        }
    }

    public boolean isRunning() {
        return Phandle.isOpen();
    }

}
