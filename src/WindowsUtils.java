import Database.Device_Build_NumberDB;
import Database.OSVersion;

import java.io.*;
import java.net.*;
import java.sql.*;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Scanner;

public class WindowsUtils{
    public static String GetAddress(String addressType) {
        String address = "";
        InetAddress lanIp = null;
        try {

            String ipAddress = null;
            Enumeration<NetworkInterface> net = null;
            net = NetworkInterface.getNetworkInterfaces();

            while (net.hasMoreElements()) {
                NetworkInterface element = net.nextElement();
                Enumeration<InetAddress> addresses = element.getInetAddresses();

                while (addresses==null && addresses.hasMoreElements() && element.getHardwareAddress().length > 0 && !isVMMac(element.getHardwareAddress())) {
                    InetAddress ip = addresses.nextElement();
                    if (ip instanceof Inet4Address) {

                        if (ip.isSiteLocalAddress()) {
                            ipAddress = ip.getHostAddress();
                            lanIp = InetAddress.getByName(ipAddress);
                        }

                    }

                }
            }

            if (lanIp == null)
                return null;

            if (addressType.equals("ip")) {

                address = lanIp.toString().replaceAll("^/+", "");

            } else if (addressType.equals("mac")) {

                address = getMacAddress(lanIp);

            } else {

                throw new Exception("Specify \"ip\" or \"mac\"");

            }

        } catch (UnknownHostException ex) {

            ex.printStackTrace();

        } catch (SocketException ex) {

            ex.printStackTrace();

        } catch (Exception ex) {

            ex.printStackTrace();

        }

        return address;

    }

    private static String getMacAddress(InetAddress ip) {
        String address = null;
        try {

            NetworkInterface network = NetworkInterface.getByInetAddress(ip);
            byte[] mac = network.getHardwareAddress();

            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < mac.length; i++) {
                sb.append(String.format("%02X%s", mac[i], (i < mac.length - 1) ? "-" : ""));
            }
            address = sb.toString();

        } catch (SocketException ex) {

            ex.printStackTrace();

        }

        return address;
    }

    private static boolean isVMMac(byte[] mac) {
        if(null == mac) return false;
        byte invalidMacs[][] = {
                {0x00, 0x05, 0x69},             //VMWare
                {0x00, 0x1C, 0x14},             //VMWare
                {0x00, 0x0C, 0x29},             //VMWare
                {0x00, 0x50, 0x56},             //VMWare
                {0x08, 0x00, 0x27},             //Virtualbox
                {0x0A, 0x00, 0x27},             //Virtualbox
                {0x00, 0x03, (byte)0xFF},       //Virtual-PC
                {0x00, 0x15, 0x5D}              //Hyper-V
        };

        for (byte[] invalid: invalidMacs){
            if (invalid[0] == mac[0] && invalid[1] == mac[1] && invalid[2] == mac[2]) return true;
        }

        return false;
    }

//    public static String findSysInfo(String term) {
//        try {
//            Runtime rt = Runtime.getRuntime();
//            Process pr = rt.exec("CMD /C SYSTEMINFO | FINDSTR /B /C:\"" + term + "\"");
//            BufferedReader in = new BufferedReader(new InputStreamReader(pr.getInputStream()));
//            return in.readLine();
//        } catch (IOException e) {
//            System.err.println(e.getMessage());
//        }
//        return "";
//    }
//
//    public String getEdition() {
//        String osName = findSysInfo("OS Version:");
////        if (!osName.isEmpty()) {
////            for (String edition : EDITIONS) {
////                if (osName.contains(edition)) {
////                    return edition;
////                }
////            }
////        }
//        Scanner s = new Scanner(osName).useDelimiter("                ");
//        String firstLine=s.next();
//        String osBuildNoStr=s.next();
//        //System.out.println("OS version is " + osName);
//        Scanner sc = new Scanner(osBuildNoStr).useDelimiter(" ");
//        String osBuildNo=sc.next();
//        System.out.println(osBuildNo);
//        return osBuildNo;
//    }
//
//    private ArrayList<OSVersion> SupportedVersions = new ArrayList<OSVersion>();
//    String currentOSVersion;
//    public boolean checkWindowsApproved() throws SQLException {
//        boolean windowsApproved = false;
//        String currentOSVersion=getEdition();
//        Device_Build_NumberDB device_build_numberDB=new Device_Build_NumberDB();
//        SupportedVersions=device_build_numberDB.CheckSupportedVersion();
////        CheckSupportedVersion();
////        run();
//        for(OSVersion o:SupportedVersions) {
//            if (o.getVersionNumber().equals(currentOSVersion)) {
//                System.out.println("Current Version: "+currentOSVersion+" ArrList: "+o);
//                windowsApproved=true;
//                break;
//            } else {
//                System.out.println(o);
//                windowsApproved=false;
//            }
//        }
//        return windowsApproved;
//    }
}