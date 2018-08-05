import Database.Device_Build_NumberDB;
import Database.OSVersion;

import java.io.*;
import java.sql.*;
import java.util.ArrayList;
import java.util.Scanner;

public class WindowsUtils{
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