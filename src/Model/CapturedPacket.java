package Model;

import org.pcap4j.packet.IpPacket;
import org.pcap4j.packet.Packet;
import org.pcap4j.packet.TcpPacket;
import org.pcap4j.packet.UdpPacket;

import java.net.InetAddress;
import java.net.UnknownHostException;

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
            this.Protocol = identifyProtocol();
//            this.information = identifyHost();
        }
    }

    public String identifyProtocol() {
        String identifiedProtocol = this.Protocol;
        if (this.srcPort == 0 && this.dstPort == 0)
            return identifiedProtocol;
        else if (!this.Protocol.equals("TCP") || !this.Protocol.equals("UDP"))
            return identifiedProtocol;
        else if (this.Protocol.equals("TCP")) {
            if (this.srcPort == 20 || this.dstPort == 20 || this.srcPort == 21 || this.dstPort == 21)
                identifiedProtocol = "FTP";
            else if (this.srcPort == 22 || this.dstPort == 22)
                identifiedProtocol = "SSH";
            else if (this.srcPort == 23 || this.dstPort == 23)
                identifiedProtocol = "Telnet";
            else if (this.srcPort == 25 || this.dstPort == 25)
                identifiedProtocol = "SMTP";
            else if (this.srcPort == 80 || this.dstPort == 80)
                identifiedProtocol = "HTTP";
            else if (this.srcPort == 443 || this.dstPort == 443)
                identifiedProtocol = "HTTPS";
            else if (this.srcPort == 110 || this.dstPort == 110)
                identifiedProtocol = "POP3";
            else if (this.srcPort == 143 || this.dstPort == 143)
                identifiedProtocol = "IMAP";
            else if (this.srcPort == 179 || this.dstPort == 179)
                identifiedProtocol = "BGP";
            if (this.srcPort == 989 || this.dstPort == 989 || this.srcPort == 990 || this.dstPort == 990)
                identifiedProtocol = "FTPS";
        } else if (this.Protocol.equals("UDP")) {
            if (this.srcPort == 67 || this.dstPort == 67 || this.srcPort == 68 || this.dstPort == 68)
                identifiedProtocol = "DHCP";
            else if (this.srcPort == 69 || this.dstPort == 69)
                identifiedProtocol = "TFTP";
            else if (this.srcPort == 123 || this.dstPort == 123)
                identifiedProtocol = "NTP";
        } else if (this.Protocol.equals("TCP") || this.Protocol.equals("UDP")) {
            if (this.srcPort == 53 || this.dstPort == 53)
                identifiedProtocol = "DNS";
            else if (this.srcPort == 137 || this.dstPort == 137 || this.srcPort == 138 || this.dstPort == 138 || this.srcPort == 139 || this.dstPort == 139)
                identifiedProtocol = "NETBIOS";
            if (this.srcPort == 161 || this.dstPort == 161 || this.srcPort == 162 || this.dstPort == 162)
                identifiedProtocol = "SNMP";
            else if (this.srcPort == 389 || this.dstPort == 389)
                identifiedProtocol = "LDAP";
            else if (this.srcPort == 636 || this.dstPort == 636)
                identifiedProtocol = "LDAPS";
        }
        return identifiedProtocol;
    }

    public String identifyHost() {
        if (this.srcPort > 0 && this.srcPort <= 1023){
            try {
                InetAddress host = InetAddress.getByName(this.srcIP);
                if (host.getCanonicalHostName().equals(srcIP))
                    return null;
                else
                    return "Identified Source Host: "+host.getCanonicalHostName();
            } catch (UnknownHostException ex) {
                return null;
            }
        }
        else if (this.dstPort > 0 && this.dstPort <= 1023){
            try {
                InetAddress host = InetAddress.getByName(this.destIP);
                if (host.getCanonicalHostName().equals(destIP))
                    return null;
                else
                    return "Identified Destination Host: "+host.getCanonicalHostName();
            } catch (UnknownHostException ex) {
                return null;
            }
        }
        else
            return null;
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
