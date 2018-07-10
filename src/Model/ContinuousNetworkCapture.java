package Model;

import org.pcap4j.core.*;
import org.pcap4j.core.PcapNetworkInterface.PromiscuousMode;
import org.pcap4j.packet.Packet;
import java.sql.Timestamp;
import java.util.Calendar;
import java.util.Timer;
import java.util.TimerTask;

public class ContinuousNetworkCapture {
    //Pre-Defined Variables
    private static final String COUNT_KEY = ContinuousNetworkCapture.class.getName() + ".count";
    private static final int COUNT = Integer.getInteger(COUNT_KEY, -1); // -1 -> loop infinite
    private static final String READ_TIMEOUT_KEY = ContinuousNetworkCapture.class.getName() + ".readTimeout";
    private static final int READ_TIMEOUT = Integer.getInteger(READ_TIMEOUT_KEY, 100); // [ms]
    private static final String SNAPLEN_KEY = ContinuousNetworkCapture.class.getName() + ".snaplen";
    private static final int SNAPLEN = Integer.getInteger(SNAPLEN_KEY, 65536); // [bytes]
    private PcapNetworkInterface Netinterface;
    private PcapHandle Phandle;
    private PcapDumper dumper;
    private Timer timer = new Timer(true);
    private TimerTask sendExpiry;
    private TimerTask countExpiry;

    //Data Variables
    private long PacketsReceived, PacketsDropped;
    private String filePath;
    private int Threshold, perMinutePktCount = 0;
    private Timestamp TrackAheadTimeStamp = null, TrackCurrentTimeStamp = null;
    private int eventCount = 0;
    private boolean sendLimit = false, renewCount = true;
    private int pktCount = 0;
    private String PhoneNumber;
    //Overrides default packet handling
    PacketListener listener = new PacketListener() {
        @Override
        public void gotPacket(Packet packet) {
            try {
                dumper.dump(packet);
                incrementCount();
                if (TrackCurrentTimeStamp == null && TrackAheadTimeStamp == null)
                    getAheadTimeStamp();
                else if (new Timestamp(System.currentTimeMillis()).after(TrackAheadTimeStamp) || TrackAheadTimeStamp == null) {
                    perMinutePktCount = 0;
                    getCurrentTimeStamp();
                    getAheadTimeStamp();
                }
                if (Phandle.getTimestamp().before(TrackAheadTimeStamp) && Phandle.getTimestamp().after(TrackCurrentTimeStamp))
                    ++perMinutePktCount;
            } catch (NotOpenException e) {
                e.printStackTrace();
            }
        }
    };

    public ContinuousNetworkCapture(PcapNetworkInterface nif, String filePath, int Threshold) {
        this.Netinterface = nif;
        this.filePath = filePath;
        this.Threshold = Threshold;
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
        } catch (PcapNativeException | NotOpenException e) {
            // Auto-generated catch block
            e.printStackTrace();
        }
    }

    //start capturing the packets
    public void startSniffing() {
        try {
            Phandle = Netinterface.openLive(SNAPLEN, PromiscuousMode.PROMISCUOUS, READ_TIMEOUT);
            dumper = Phandle.dumpOpen(filePath);
            Phandle.loop(COUNT, listener);
        } catch (PcapNativeException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            System.err.println("Stopped successfully");
        } catch (NotOpenException e) {
            e.printStackTrace();
        } finally {
            printStat();
            dumper.close();
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

    public boolean isRunning() {
        return Phandle.isOpen();
    }

}
