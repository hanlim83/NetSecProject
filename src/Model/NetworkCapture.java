package Model;

import com.sun.jna.Platform;
import org.pcap4j.core.*;
import org.pcap4j.core.PcapNetworkInterface.PromiscuousMode;
import org.pcap4j.packet.ArpPacket;
import org.pcap4j.packet.EthernetPacket;
import org.pcap4j.packet.IpPacket;
import org.pcap4j.packet.Packet;

import java.io.File;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class NetworkCapture {
    //Pre-Defined Variables
    private static final String COUNT_KEY = NetworkCapture.class.getName() + ".count";
    private static final int COUNT = Integer.getInteger(COUNT_KEY, -1); // -1 -> loop infinite
    private static final String READ_TIMEOUT_KEY = NetworkCapture.class.getName() + ".readTimeout";
    private static final int READ_TIMEOUT = Integer.getInteger(READ_TIMEOUT_KEY, 100); // [ms]
    private static final String SNAPLEN_KEY = NetworkCapture.class.getName() + ".snaplen";
    private static final int SNAPLEN = Integer.getInteger(SNAPLEN_KEY, 65536); // [bytes]
    private static final int ALERT_LIMIT = 10;
    private static final int INCERMENT_LIMIT = 1;
    private static final int MINUTE_TO_MILISECONDS = 60000;
    private static final int RECORD_RANGE = 10;
    private static final int TPSRange = 3;
    //Data Variables
    public ArrayList<CapturedPacket> packets;
    public ArrayList<Integer> PreviousTPS;
    public ArrayList<Integer> ProtocolMakeupData;
    public ArrayList<String> ProtocolMakeupProtocols;
    public ArrayList<TopIPObject> Top5IPMakeup;
    private ArrayList<ARPObject> ARPDatabase;
    private PcapNetworkInterface Netinterface;
    private PcapHandle Phandle;
    private PcapDumper dumper;
    private Timer timer = new Timer(true);
    private TimerTask sendExpiry;
    private TimerTask countExpiry;
    private long PacketsReceived = 0, PacketsDroppedByInt = 0, PacketsCaptured = 0;
    private String directoryPath;
    private int Threshold, perMinutePktCount = 0;
    private Timestamp TrackAheadTimeStamp = null, TrackCurrentTimeStamp = null, lastTimeStamp = null, alertBeforeTimeStamp = null, alertAfterTimeStamp = null;
    private int eventCount = 0;
    private boolean sendLimit = false, renewCount = true, ARPSpoofing = false;
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
            processARP(packet);
        }
    };
    private String specficExportFilePath;
    private int TPSSize = 0;
    private String GeneralExportFileName;
    private String SpecificExportFileName;

    public NetworkCapture(PcapNetworkInterface nif, String directoryPath, int Threshold) {
        this.Netinterface = nif;
        this.directoryPath = directoryPath;
        this.Threshold = Threshold;
        packets = new ArrayList<CapturedPacket>();
        PreviousTPS = new ArrayList<Integer>();
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
        ProtocolMakeupData = new ArrayList<Integer>();
        ProtocolMakeupProtocols = new ArrayList<String>();
        Top5IPMakeup = new ArrayList<TopIPObject>();
        ARPDatabase = new ArrayList<ARPObject>();
    }

    public long getPacketsReceived() {
        return PacketsReceived;
    }

//    public long getPacketsDropped() {
//        return PacketsDropped;
//    }

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

    public int getTPSSize() {
        return TPSSize;
    }

    public long getPacketsDroppedByInt() {
        return PacketsDroppedByInt;
    }

    public long getPacketsCaptured() {
        return PacketsCaptured;
    }

    public String getGeneralExportFileName() {
        return GeneralExportFileName;
    }

    public String getSpecificExportFileName() {
        return SpecificExportFileName;
    }

    public String getSpecficExportFilePath() {
        return specficExportFilePath;
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
            timer.schedule(countExpiry, (INCERMENT_LIMIT * MINUTE_TO_MILISECONDS));
            return false;
        } else if (sendLimit == true && renewCount == false)
            return false;
        else if (sendLimit == false && renewCount == false) {
            System.out.println("Sent Alert");
            sendLimit = true;
            timer.schedule(sendExpiry, (ALERT_LIMIT * MINUTE_TO_MILISECONDS));
            return true;
        } else {
            System.out.println("Incremented and sent alert");
            incrementEvents();
            sendLimit = true;
            renewCount = false;
            timer.schedule(sendExpiry, (ALERT_LIMIT * MINUTE_TO_MILISECONDS));
            timer.schedule(countExpiry, (INCERMENT_LIMIT * MINUTE_TO_MILISECONDS));
            Calendar cal = Calendar.getInstance();
            cal.setTimeInMillis(System.currentTimeMillis());
            cal.add(Calendar.MINUTE, RECORD_RANGE);
            alertAfterTimeStamp = new Timestamp(cal.getTime().getTime());
            cal = Calendar.getInstance();
            cal.setTimeInMillis(System.currentTimeMillis());
            cal.add(Calendar.MINUTE, -RECORD_RANGE);
            alertBeforeTimeStamp = new Timestamp(cal.getTime().getTime());
            return true;
        }
    }

    public boolean checkARP() {
        if (ARPSpoofing == false)
            return false;
        else if (sendLimit == true) {
            System.out.println("Incremented");
            incrementEvents();
            return false;
        } else {
            System.out.println("Incremented and sent alert");
            incrementEvents();
            sendLimit = true;
            renewCount = false;
            timer.schedule(sendExpiry, (ALERT_LIMIT * MINUTE_TO_MILISECONDS));
            Calendar cal = Calendar.getInstance();
            cal.setTimeInMillis(System.currentTimeMillis());
            cal.add(Calendar.MINUTE, RECORD_RANGE);
            alertAfterTimeStamp = new Timestamp(cal.getTime().getTime());
            cal = Calendar.getInstance();
            cal.setTimeInMillis(System.currentTimeMillis());
            cal.add(Calendar.MINUTE, -RECORD_RANGE);
            alertBeforeTimeStamp = new Timestamp(cal.getTime().getTime());
            return true;
        }
    }

    public void ProtocolMakeup() {
        boolean found = false;
        for (CapturedPacket p : packets) {
            if (ProtocolMakeupData.isEmpty() && ProtocolMakeupProtocols.isEmpty()) {
                ProtocolMakeupData.add(1);
                ProtocolMakeupProtocols.add(p.identifyProtocol());
            } else {
                for (int i = 0; i < ProtocolMakeupProtocols.size(); i++) {
                    String proto = ProtocolMakeupProtocols.get(i);
                    if (proto.equals(p.identifyProtocol())) {
                        ProtocolMakeupData.set(i, ProtocolMakeupData.get(i) + 1);
                        found = true;
                        break;
                    }
                }
                if (found == false) {
                    ProtocolMakeupData.add(1);
                    ProtocolMakeupProtocols.add(p.identifyProtocol());
                }
            }
        }
    }

    public void printStat() {
        PcapStat ps;
        try {
            ps = Phandle.getStats();
            PacketsReceived += ps.getNumPacketsReceived();
//            PacketsDropped = ps.getNumPacketsDropped();
            PacketsDroppedByInt += ps.getNumPacketsDroppedByIf();
            if (Platform.isWindows()) {
                PacketsCaptured += ps.getNumPacketsCaptured();
            }
        } catch (PcapNativeException | NotOpenException e) {
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

    //Specific Export to pcap file
    public boolean Specficexport() {
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyyMMdd HHmmss");
        LocalDateTime now = LocalDateTime.now();
        try {
            if (!Phandle.isOpen())
                Phandle = Netinterface.openLive(SNAPLEN, PromiscuousMode.PROMISCUOUS, READ_TIMEOUT);
            SpecificExportFileName = "Partial Capture for Alert " + dtf.format(now) + ".pcap";
            specficExportFilePath = directoryPath + "\\" + SpecificExportFileName;
            dumper = Phandle.dumpOpen(specficExportFilePath);
            for (CapturedPacket p : packets) {
                if (p.getOrignalTimeStamp().after(alertBeforeTimeStamp) && p.getOrignalTimeStamp().before(alertAfterTimeStamp))
                    dumper.dump(p.getOriginalPacket(), p.getOrignalTimeStamp());
            }
            dumper.close();
            System.out.println(new File("PcapExport").getAbsolutePath() + "\\" + SpecificExportFileName);
            dumper = Phandle.dumpOpen(new File("PcapExport").getAbsolutePath() + "\\" + SpecificExportFileName);
            for (CapturedPacket p : packets) {
                if (p.getOrignalTimeStamp().after(alertBeforeTimeStamp) && p.getOrignalTimeStamp().before(alertAfterTimeStamp))
                    dumper.dump(p.getOriginalPacket(), p.getOrignalTimeStamp());
            }
            dumper.close();
            return true;
        } catch (PcapNativeException e) {
            e.printStackTrace();
            return false;
        } catch (NotOpenException e) {
            e.printStackTrace();
            return false;
        }
    }

    //General Export to pcap file
    public boolean Generalexport() {
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyyMMdd HHmmss");
        LocalDateTime now = LocalDateTime.now();
        try {
            if (!Phandle.isOpen())
                Phandle = Netinterface.openLive(SNAPLEN, PromiscuousMode.PROMISCUOUS, READ_TIMEOUT);
            GeneralExportFileName = "Complete Network Capture " + dtf.format(now) + ".pcap";
            dumper = Phandle.dumpOpen(directoryPath + "\\" + GeneralExportFileName);
            for (CapturedPacket p : packets) {
                dumper.dump(p.getOriginalPacket(), p.getOrignalTimeStamp());
            }
            dumper.close();
            Phandle.close();
            Phandle = Netinterface.openLive(SNAPLEN, PromiscuousMode.PROMISCUOUS, READ_TIMEOUT);
            System.out.println(new File("PcapExport").getAbsolutePath() + "\\" + GeneralExportFileName);
            dumper = Phandle.dumpOpen(new File("PcapExport").getAbsolutePath() + "\\" + GeneralExportFileName);
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

    public void getTrafficPerSecond() {
        Timestamp original = new Timestamp(System.currentTimeMillis());
        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(original.getTime());
        cal.add(Calendar.SECOND, TPSRange);
        Timestamp later = new Timestamp(cal.getTime().getTime());
        int packetCount = 0;
        for (CapturedPacket packet : packets) {
            if (packet.getOrignalTimeStamp().before(later) && lastTimeStamp == null)
                ++packetCount;
            else if (packet.getOrignalTimeStamp().before(later) && packet.getOrignalTimeStamp().after(lastTimeStamp))
                ++packetCount;
        }
        PreviousTPS.add(packetCount);
        ++TPSSize;
        lastTimeStamp = new Timestamp(cal.getTime().getTime());
        System.out.println(packetCount);
    }

    public void getTop5IP() {
        TopIPObject tempt = null;
        int recordedIndex = 0;
        for (CapturedPacket p : packets) {
            if (Top5IPMakeup.isEmpty()) {
                Top5IPMakeup.add(new TopIPObject(p.getSrcIP(), 1));
            } else {
                for (int i = 0; i < Top5IPMakeup.size(); i++) {
                    if (!p.getSrcIP().isEmpty() && p.getSrcIP().equals(Top5IPMakeup.get(i).getKey())) {
                        tempt = new TopIPObject(Top5IPMakeup.get(i).getKey(), Top5IPMakeup.get(i).getValue() + 1);
                        recordedIndex = i;
                        break;
                    }
                }
                if (tempt != null) {
                    Top5IPMakeup.set(recordedIndex, tempt);
                    recordedIndex = 0;
                    tempt = null;
                } else {
                    Top5IPMakeup.add(new TopIPObject(p.getSrcIP(), 1));
                }
            }
        }
        if (!Top5IPMakeup.isEmpty() && Top5IPMakeup.size() > 5) {
            Collections.sort(Top5IPMakeup);
            ArrayList<TopIPObject> temporary = new ArrayList<TopIPObject>();
            for (int j = 0; j < 5; j++) {
                temporary.add(Top5IPMakeup.get(j));
            }
            Top5IPMakeup = temporary;
        }
    }

    public void printARPDatabase() {
        for (ARPObject O : ARPDatabase) {
            System.out.println(O.getIPAddress() + " : " + O.getMACAddress());
        }
    }

    public void processARP(Packet receivedPacket) {
        String ipAddr, macAddr;
        if (receivedPacket.contains(IpPacket.class) && receivedPacket.contains(EthernetPacket.class)) {
            IpPacket IPpkt = receivedPacket.get(IpPacket.class);
            ipAddr = IPpkt.getHeader().getSrcAddr().getHostAddress();
            EthernetPacket Epkt = receivedPacket.get(EthernetPacket.class);
            macAddr = Epkt.getHeader().getSrcAddr().toString();
        } else if (receivedPacket.contains(ArpPacket.class)) {
            ArpPacket pkt = receivedPacket.get(ArpPacket.class);
            ipAddr = pkt.getHeader().getSrcProtocolAddr().getHostAddress();
            macAddr = pkt.getHeader().getSrcHardwareAddr().toString();
        } else {
            return;
        }
        if (ARPDatabase.isEmpty()) {
            ARPObject newObject = new ARPObject(macAddr, ipAddr);
            ARPDatabase.add(newObject);
            return;
        } else {
            for (ARPObject o : ARPDatabase) {
                if (o.getIPAddress().equals(ipAddr) && o.getMACAddress().equals(macAddr)) {
                    return;
                } else if (o.getIPAddress().equals(ipAddr) && !o.getMACAddress().equals(macAddr)) {
                    ARPSpoofing = true;
                    return;
                }
            }
            if (!ARPDatabase.contains(new ARPObject(macAddr, ipAddr))) {
                ARPObject newObject = new ARPObject(macAddr, ipAddr);
                ARPDatabase.add(newObject);
                return;
            }
        }
    }

    public boolean isRunning() {
        return Phandle.isOpen();
    }

    public boolean hasExistingPackets() {
        return !packets.isEmpty();
    }

}
