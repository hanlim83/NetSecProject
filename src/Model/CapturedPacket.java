package Model;

import org.pcap4j.packet.*;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.sql.Timestamp;

public class CapturedPacket {
    private int srcPort, dstPort, length, number;
    private String srcIP, destIP, Protocol, srcMac, destMac;
    private String information, timestamp;
    private Packet originalPacket;
    private Timestamp orignalTimeStamp;

    public CapturedPacket(Packet originalPacket, int packetNumber, Timestamp timestamp) {
        this.originalPacket = originalPacket;
        this.number = packetNumber;
        this.orignalTimeStamp = timestamp;
        this.timestamp = this.orignalTimeStamp.toString();
        if (originalPacket.contains(ArpPacket.class)) {
            ArpPacket arpPacket = this.originalPacket.get(ArpPacket.class);
            this.srcIP = arpPacket.getHeader().getSrcProtocolAddr().getHostAddress();
            this.destIP = arpPacket.getHeader().getDstProtocolAddr().getHostAddress();
            this.Protocol = "ARP";
            this.length = arpPacket.getHeader().length();
            if (arpPacket.getHeader().getOperation().value() == 1)
                this.information = "ARP Request";
            else if (arpPacket.getHeader().getOperation().value() == 2)
                this.information = "ARP Reply";
            else if (arpPacket.getHeader().getOperation().value() == 3)
                this.information = "Reverse ARP Request";
            else if (arpPacket.getHeader().getOperation().value() == 4)
                this.information = "Reverse ARP Reply";
        } else if (!originalPacket.contains(IpPacket.class)) {
            this.information = "Not a Layer 2 (IP) Packet";
            return;
        } else {
            IpPacket ipPacket = this.originalPacket.get(IpPacket.class);
            this.srcIP = ipPacket.getHeader().getSrcAddr().getHostAddress();
            this.destIP = ipPacket.getHeader().getDstAddr().getHostAddress();
            this.Protocol = ipPacket.getHeader().getProtocol().name();
            if (this.Protocol.equals("TCP")) {
                TcpPacket tcpPkt = this.originalPacket.get(TcpPacket.class);
                srcPort = tcpPkt.getHeader().getSrcPort().valueAsInt();
                dstPort = tcpPkt.getHeader().getDstPort().valueAsInt();
            } else if (this.Protocol.equals("UDP")) {
                UdpPacket udpPkt = this.originalPacket.get(UdpPacket.class);
                srcPort = udpPkt.getHeader().getSrcPort().valueAsInt();
                dstPort = udpPkt.getHeader().getDstPort().valueAsInt();
            } else if (this.Protocol.equals("ICMPv4")) {
                IcmpV4CommonPacket icmp4pkt = this.originalPacket.get(IcmpV4CommonPacket.class);
                this.information = icmp4pkt.getHeader().getType().name() + "," + icmp4pkt.getHeader().getCode().name();

            } else if (this.Protocol.equals("ICMPv6")) {
                IcmpV6CommonPacket icmp6pkt = this.originalPacket.get(IcmpV6CommonPacket.class);
                this.information = icmp6pkt.getHeader().getType().name() + "," + icmp6pkt.getHeader().getCode().name();
            } else {
                return;
            }
            length = this.originalPacket.length();
            EthernetPacket ethernetPacket = this.originalPacket.get(EthernetPacket.class);
            srcMac = ethernetPacket.getHeader().getSrcAddr().toString();
            destMac = ethernetPacket.getHeader().getDstAddr().toString();
            this.Protocol = identifyProtocol();
            //this.information = identifyHost();
            if (originalPacket.contains(EncryptedPacket.class)) {
                EncryptedPacket encrypted = originalPacket.get(EncryptedPacket.class);
                length = encrypted.getHeader().length();
                if (this.information.isEmpty())
                    this.information = "Encrypted Packet";
                else
                    this.information = this.information + ", Encrypted Packet";
            }
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
                identifiedProtocol = "SSH/SFTP";
            else if (this.srcPort == 23 || this.dstPort == 23)
                identifiedProtocol = "TELNET";
            else if (this.srcPort == 25 || this.dstPort == 25)
                identifiedProtocol = "SMTP";
            else if (this.srcPort == 80 || this.dstPort == 80)
                identifiedProtocol = "HTTP";
            else if (this.srcPort == 110 || this.dstPort == 110)
                identifiedProtocol = "POP3";
            else if (this.srcPort == 143 || this.dstPort == 143)
                identifiedProtocol = "IMAP";
            else if (this.srcPort == 179 || this.dstPort == 179)
                identifiedProtocol = "BGP";
            else if (this.srcPort == 443 || this.dstPort == 443)
                identifiedProtocol = "HTTPS";
            else if (this.srcPort == 989 || this.dstPort == 989 || this.srcPort == 990 || this.dstPort == 990)
                identifiedProtocol = "FTPS";
            else if ((this.srcPort >= 66660 && this.srcPort <= 6669) || (this.dstPort >= 66660 && this.dstPort <= 6669))
                identifiedProtocol = "IRC";
            else if (this.srcPort == 6697 || this.dstPort == 6697 || this.srcPort == 6679 || this.dstPort == 6679)
                identifiedProtocol = "SIRC";
        } else if (this.Protocol.equals("UDP")) {
            if (this.srcPort == 9 || this.dstPort == 9)
                identifiedProtocol = "WOL";
            else if (this.srcPort == 67 || this.dstPort == 67 || this.srcPort == 68 || this.dstPort == 68)
                identifiedProtocol = "DHCP";
            else if (this.srcPort == 69 || this.dstPort == 69)
                identifiedProtocol = "TFTP";
            else if (this.srcPort == 123 || this.dstPort == 123)
                identifiedProtocol = "NTP";
            else if (this.srcPort == 443 || this.dstPort == 443)
                identifiedProtocol = "QUIC";
            else if (this.srcPort == 5353 || this.dstPort == 5353)
                identifiedProtocol = "MDNS";
        } else if (this.Protocol.equals("TCP") || this.Protocol.equals("UDP")) {
            if (this.srcPort == 7 || this.dstPort == 7)
                identifiedProtocol = "ECHO";
            else if (this.srcPort == 13 || this.dstPort == 13)
                identifiedProtocol = "DAYTIME";
            else if (this.srcPort == 37 || this.dstPort == 37)
                identifiedProtocol = "TIME";
            else if (this.srcPort == 53 || this.dstPort == 53)
                identifiedProtocol = "DNS";
            else if (this.srcPort == 107 || this.dstPort == 107)
                identifiedProtocol = "RTELNET";
            else if (this.srcPort == 137 || this.dstPort == 137 || this.srcPort == 138 || this.dstPort == 138 || this.srcPort == 139 || this.dstPort == 139)
                identifiedProtocol = "NETBIOS";
            else if (this.srcPort == 161 || this.dstPort == 161 || this.srcPort == 162 || this.dstPort == 162)
                identifiedProtocol = "SNMP";
            else if (this.srcPort == 194 || this.dstPort == 194)
                identifiedProtocol = "IRC";
            else if (this.srcPort == 389 || this.dstPort == 389)
                identifiedProtocol = "LDAP";
            else if (this.srcPort == 636 || this.dstPort == 636)
                identifiedProtocol = "LDAPS";
        }
        return identifiedProtocol;
    }

    public String identifyHost() {
        if (this.srcPort > 0 && this.srcPort <= 1023) {
            try {
                InetAddress host = InetAddress.getByName(this.srcIP);
                if (host.getCanonicalHostName().equals(srcIP))
                    return null;
                else
                    return "Identified Source Host: " + host.getCanonicalHostName();
            } catch (UnknownHostException ex) {
                return null;
            }
        } else if (this.dstPort > 0 && this.dstPort <= 1023) {
            try {
                InetAddress host = InetAddress.getByName(this.destIP);
                if (host.getCanonicalHostName().equals(destIP))
                    return null;
                else
                    return "Identified Destination Host: " + host.getCanonicalHostName();
            } catch (UnknownHostException ex) {
                return null;
            }
        } else
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

    public Timestamp getOrignalTimeStamp() {
        return orignalTimeStamp;
    }

    @Override
    public String toString() {
        return "Packet Number: " + this.number + " | Source IP Address: " + this.srcIP + " | Source Port: " + this.srcPort + " | Source MAC Address: " + this.srcMac + " | Destination IP Address: " + destIP + " | Destination Port:" + dstPort + " | Destination MAC Address: " + this.destMac;
    }
}
