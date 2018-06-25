package Model;

import com.sun.jna.Platform;
import org.pcap4j.core.*;
import org.pcap4j.core.PcapNetworkInterface.PromiscuousMode;
import org.pcap4j.packet.Packet;
import java.util.ArrayList;

public class NetworkCapture {
    //Pre-Defined Variables
    private static final String COUNT_KEY  = NetworkCapture.class.getName() + ".count";
    private static final int COUNT  = Integer.getInteger(COUNT_KEY, -1); // -1 -> loop infinite
    private static final String READ_TIMEOUT_KEY  = NetworkCapture.class.getName() + ".readTimeout";
    private static final int READ_TIMEOUT  = Integer.getInteger(READ_TIMEOUT_KEY, 100); // [ms]
    private static final String SNAPLEN_KEY = NetworkCapture.class.getName() + ".snaplen";
    private static final int SNAPLEN = Integer.getInteger(SNAPLEN_KEY, 65536); // [bytes]
    private PcapNetworkInterface Netinterface;
    private PcapHandle Phandle;
    private int pktCount = 0;

    //Data Variables
    private long PacketsReceived,PacketsDropped,PacketsDroppedByInt,PacketsCaptured;
    public ArrayList<CapturedPacket> packets;

    public NetworkCapture(PcapNetworkInterface nif) {
        this.Netinterface = nif;
        packets = new ArrayList<CapturedPacket>();
    }

    //Overrides default packet handling
    PacketListener listener = new PacketListener() {
        @Override
        public void gotPacket(Packet packet) {
            incrementCount();
            CapturedPacket cPacket = new CapturedPacket(packet,getPacketCount(),Phandle.getTimestamp());
            packets.add(cPacket);
            /*System.out.println(cPacket.toString());
            System.out.println(cPacket.getProtocol());
            System.out.println(cPacket.getLength());
            System.out.println(cPacket.getInformation());*/
        }
    };
    public int getPktCount() {
        return pktCount;
    }

    public long getPacketsReceived() {
        return PacketsReceived;
    }

    public long getPacketsDropped() {
        return PacketsDropped;
    }

    public long getPacketsDroppedByInt() {
        return PacketsDroppedByInt;
    }

    public long getPacketsCaptured() {
        return PacketsCaptured;
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
            PacketsDroppedByInt =  ps.getNumPacketsDroppedByIf();
            if (Platform.isWindows()) {
                PacketsCaptured = ps.getNumPacketsCaptured();
            }
        } catch (PcapNativeException | NotOpenException e) {
            // Auto-generated catch block
            e.printStackTrace();
        }
    }

    //start capturing the packets
    public void startSniffing(){
                try{
                    Phandle = Netinterface.openLive(SNAPLEN, PromiscuousMode.PROMISCUOUS, READ_TIMEOUT);
                    Phandle.loop(COUNT, listener);
//                    Phandle.loop(5000, listener);
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
            // Auto-generated catch block
            e.printStackTrace();
        }
    }
    //Export to pcap file
    public void export(String filepath){
        try {
            PcapDumper dumper = Phandle.dumpOpen("");
            for (CapturedPacket p : packets){
                dumper.dump(p.getOriginalPacket(), p.getOrignalTimeStamp());
            }
            dumper.close();
        } catch (PcapNativeException e) {
            e.printStackTrace();
        } catch (NotOpenException e) {
            e.printStackTrace();
        }
    }

    public boolean isRunning() {
        return Phandle.isOpen();
    }

}
