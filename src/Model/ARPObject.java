package Model;

public class ARPObject {
    private String MACAddress;
    private String IPAddress;

    public ARPObject(String MACAddress, String IPAddress) {
        this.MACAddress = MACAddress;
        this.IPAddress = IPAddress;
    }

    public String getMACAddress() {
        return MACAddress;
    }

    public void setMACAddress(String MACAddress) {
        this.MACAddress = MACAddress;
    }

    public String getIPAddress() {
        return IPAddress;
    }

    public void setIPAddress(String IPAddress) {
        this.IPAddress = IPAddress;
    }
}
