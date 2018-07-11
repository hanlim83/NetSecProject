package Model;

import com.sun.jna.Platform;
import org.pcap4j.core.*;
import org.pcap4j.core.PcapNetworkInterface.PromiscuousMode;
import org.pcap4j.packet.Packet;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Timer;
import java.util.TimerTask;

public class NetworkCapture {
    //Pre-Defined Variables
    private static final String COUNT_KEY = NetworkCapture.class.getName() + ".count";
    private static final int COUNT = Integer.getInteger(COUNT_KEY, -1); // -1 -> loop infinite
    private static final String READ_TIMEOUT_KEY = NetworkCapture.class.getName() + ".readTimeout";
    private static final int READ_TIMEOUT = Integer.getInteger(READ_TIMEOUT_KEY, 100); // [ms]
    private static final String SNAPLEN_KEY = NetworkCapture.class.getName() + ".snaplen";
    private static final int SNAPLEN = Integer.getInteger(SNAPLEN_KEY, 65536); // [bytes]
    private static final int RECORDRANGE = 5;
    public ArrayList<CapturedPacket> packets;
    public ArrayList<LineChartObject> PreviousTPS;
    private PcapNetworkInterface Netinterface;
    private PcapHandle Phandle;
    private PcapDumper dumper;
    private Timer timer = new Timer(true);
    private TimerTask sendExpiry;
    private TimerTask countExpiry;
    //Data Variables
    private long PacketsReceived, PacketsDropped, PacketsDroppedByInt, PacketsCaptured;
    private String directoryPath;
    private int Threshold, perMinutePktCount = 0;
    private Timestamp TrackAheadTimeStamp = null, TrackCurrentTimeStamp = null, lastTimeStamp = null;
    private int eventCount = 0;
    private boolean sendLimit = false, renewCount = true;
    private int pktCount = 0;
    //Overrides default packet handling
    PacketListener listener = new PacketListener() {
        @Override
        public void gotPacket(Packet packet) {
            incrementCount();
            CapturedPacket cPacket = new CapturedPacket(packet, getPacketCount(), Phandle.getTimestamp());
            packets.add(cPacket);
            if (TrackCurrentTimeStamp == null && TrackAheadTimeStamp == null)
                getAheadTimeStamp();
            else if (new Timestamp(System.currentTimeMillis()).after(TrackAheadTimeStamp) || TrackAheadTimeStamp == null) {
                perMinutePktCount = 0;
                getCurrentTimeStamp();
                getAheadTimeStamp();
            }
            if (Phandle.getTimestamp().before(TrackAheadTimeStamp) && Phandle.getTimestamp().after(TrackCurrentTimeStamp))
                ++perMinutePktCount;
        }
    };
    private String PhoneNumber;

    public NetworkCapture(PcapNetworkInterface nif, String directoryPath, int Threshold) {
        this.Netinterface = nif;
        this.directoryPath = directoryPath;
        this.Threshold = Threshold;
        packets = new ArrayList<CapturedPacket>();
        PreviousTPS = new ArrayList<LineChartObject>();
        sendExpiry = new TimerTask() {
            @Override
            public void run() {
                sendLimit = false;
            }
        };
        countExpiry = new TimerTask() {
            @Override
            public void run() {
                renewCount = true;
            }
        };
    }

    public long getPacketsReceived() {
        return PacketsReceived;
    }

    public long getPacketsDropped() {
        return PacketsDropped;
    }

    //Packet count increment
    synchronized private void incrementCount() {
        ++pktCount;
    }

    //get the packet count
    synchronized public int getPacketCount() {
        return pktCount;
    }

    //Events count increment
    synchronized private void incrementEvents() {
        ++eventCount;
    }

    //get the event count
    synchronized public int getEvents() {
        return eventCount;
    }

    public int getPerMinutePktCount() {
        return perMinutePktCount;
    }

    public String getPhoneNumber() {
        return PhoneNumber;
    }

    public long getPacketsDroppedByInt() {
        return PacketsDroppedByInt;
    }

    public long getPacketsCaptured() {
        return PacketsCaptured;
    }

    public void setPhoneNumber(String phoneNumber) {
        PhoneNumber = phoneNumber;
    }

    public void getCurrentTimeStamp() {
        TrackCurrentTimeStamp = new Timestamp(System.currentTimeMillis());
    }

    public void getAheadTimeStamp() {
        if (TrackCurrentTimeStamp == null) {
            TrackCurrentTimeStamp = new Timestamp(System.currentTimeMillis());
            Calendar cal = Calendar.getInstance();
            cal.setTimeInMillis(TrackCurrentTimeStamp.getTime());
            cal.add(Calendar.MINUTE, 1);
            TrackAheadTimeStamp = new Timestamp(cal.getTime().getTime());
        } else {
            Calendar cal = Calendar.getInstance();
            cal.setTimeInMillis(TrackCurrentTimeStamp.getTime());
            cal.add(Calendar.MINUTE, 1);
            TrackAheadTimeStamp = new Timestamp(cal.getTime().getTime());
        }
    }

    public boolean checkThreshold() {
        if (Threshold == 0)
            return false;
        else if (perMinutePktCount < Threshold)
            return false;
        else if (sendLimit == true && renewCount == true) {
            System.out.println("Incremented");
            incrementEvents();
            renewCount = false;
            timer.schedule(countExpiry, 60000);
            return false;
        } else if (sendLimit == true && renewCount == false)
            return false;
        else if (sendLimit == false && renewCount == false) {
            System.out.println("Sent Alert");
            sendLimit = true;
            timer.schedule(sendExpiry, 600000);
            return true;
        } else {
            System.out.println("Incremented and sent alert");
            incrementEvents();
            sendLimit = true;
            renewCount = false;
            timer.schedule(sendExpiry, 600000);
            timer.schedule(countExpiry, 60000);
            return true;
        }
    }

    public void printStat() {
        PcapStat ps;
        try {
            ps = Phandle.getStats();
            PacketsReceived = ps.getNumPacketsReceived();
            PacketsDropped = ps.getNumPacketsDropped();
            PacketsDroppedByInt = ps.getNumPacketsDroppedByIf();
            if (Platform.isWindows()) {
                PacketsCaptured = ps.getNumPacketsCaptured();
            }
        } catch (PcapNativeException | NotOpenException e) {
            // Auto-generated catch block
            e.printStackTrace();
        }
    }

    //start capturing the packets
    public void startSniffing() {
        try {
            Phandle = Netinterface.openLive(SNAPLEN, PromiscuousMode.PROMISCUOUS, READ_TIMEOUT);
            Phandle.loop(COUNT, listener);
        } catch (PcapNativeException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            System.err.println("Stopped successfully");
        } catch (NotOpenException e) {
            e.printStackTrace();
        } finally {
            printStat();
            Phandle.close();
        }
    }

    //stop capturing the packets
    public void stopSniffing() {
        System.out.println("\nStopping Sniffing...\n");
        try {
            Phandle.breakLoop();
            printStat();
        } catch (NotOpenException e) {
            e.printStackTrace();
        }
    }

    //Export to pcap file
    public boolean export(String filepath) {
        try {
            if (!Phandle.isOpen())
                Phandle = Netinterface.openLive(SNAPLEN, PromiscuousMode.PROMISCUOUS, READ_TIMEOUT);
            PcapDumper dumper = Phandle.dumpOpen(filepath);
            for (CapturedPacket p : packets) {
                dumper.dump(p.getOriginalPacket(), p.getOrignalTimeStamp());
            }
            dumper.close();
            Phandle.close();
            return true;
        } catch (PcapNativeException e) {
            e.printStackTrace();
            return false;
        } catch (NotOpenException e) {
            e.printStackTrace();
            return false;
        }
    }

    public LineChartObject getTrafficPerSecond() {
        Timestamp original = new Timestamp(System.currentTimeMillis());
        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(original.getTime());
        cal.add(Calendar.SECOND, 9);
        Timestamp later = new Timestamp(cal.getTime().getTime());
        int packetCount = 0;
        for (CapturedPacket packet : packets) {
            if (packet.getOrignalTimeStamp().before(later) && lastTimeStamp == null)
                packetCount++;
            else if (packet.getOrignalTimeStamp().before(later) && packet.getOrignalTimeStamp().after(lastTimeStamp))
                packetCount++;
        }
        LineChartObject TPS = new LineChartObject(packetCount);
        PreviousTPS.add(TPS);
        lastTimeStamp = later;
        return TPS;
    }

    public boolean isRunning() {
        return Phandle.isOpen();
    }

    public boolean hasExistingPackets() {
        if (!packets.isEmpty())
            return true;
        else
            return false;
    }

}
