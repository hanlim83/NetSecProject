package Model;

import org.pcap4j.packet.IpPacket;
import org.pcap4j.packet.Packet;
import org.pcap4j.packet.TcpPacket;
import org.pcap4j.packet.UdpPacket;

public class CapturedPacket {
    private int srcPort,dstPort,length,number;
    private String srcIP,destIP,Protocol;
    private String information,timestamp;
    private Packet originalPacket;

    public CapturedPacket(Packet originalPacket, int packetNumber, String timestamp){
        this.originalPacket = originalPacket;
        this.number = packetNumber;
        this.timestamp = timestamp;
        if (!originalPacket.contains(IpPacket.class)) {
            this.information ="Not a Layer 2 (IP) Packet";
            return;
        }
        else {
            IpPacket ipPacket = this.originalPacket.get(IpPacket.class);
            this.srcIP = ipPacket.getHeader().getSrcAddr().getHostAddress().toString();
            this.destIP = ipPacket.getHeader().getDstAddr().getHostAddress().toString();
            this.Protocol = ipPacket.getHeader().getProtocol().name();
            if (this.Protocol.equals("TCP") ){
                TcpPacket tcpPkt = this.originalPacket.get(TcpPacket.class);
                srcPort = tcpPkt.getHeader().getSrcPort().valueAsInt();
                dstPort = tcpPkt.getHeader().getDstPort().valueAsInt();
            }else if (this.Protocol.equals("UDP") ){
                UdpPacket udpPkt = this.originalPacket.get(UdpPacket.class);
                srcPort = udpPkt.getHeader().getSrcPort().valueAsInt();
                dstPort = udpPkt.getHeader().getDstPort().valueAsInt();
            }else{
                return;
            }
            length = this.originalPacket.length();
        }
    }

    public int getSrcPort() {
        return srcPort;
    }

    public int getDstPort() {
        return dstPort;
    }

    public int getLength() {
        return length;
    }

    public int getNumber() {
        return number;
    }

    public String getSrcIP() {
        return srcIP;
    }

    public String getDestIP() {
        return destIP;
    }

    public String getProtocol() {
        return Protocol;
    }

    public String getInformation() {
        return information;
    }

    public Packet getOriginalPacket() {
        return originalPacket;
    }
    public String getTimestamp() {
        return timestamp;
    }
    @Override
    public String toString(){
        return "Packet Number: "+this.number+" | Source IP Address: "+this.srcIP+" | Source Port: "+this.srcPort+" | Destination IP Address: "+destIP+" | Destination Port:"+dstPort;
    }
}
