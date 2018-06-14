import java.io.*;
import java.util.Scanner;

public class WindowsUtils {
    private static final String[] EDITIONS = {
            "Basic", "Home", "Professional", "Enterprise"
    };

    public static void main(String[] args) {
        System.out.printf("The edition of Windows you are using is: %s%n", getEdition());
    }

    public static String findSysInfo(String term) {
        try {
            Runtime rt = Runtime.getRuntime();
            Process pr = rt.exec("CMD /C SYSTEMINFO | FINDSTR /B /C:\"" + term + "\"");
            BufferedReader in = new BufferedReader(new InputStreamReader(pr.getInputStream()));
            return in.readLine();
        } catch (IOException e) {
            System.err.println(e.getMessage());
        }
        return "";
    }

//    public static String findSysInfo2(String term) {
//        try {
//            Runtime rt = Runtime.getRuntime();
//            Process pr = rt.exec("systeminfo | findstr /B /C:\"OS Name\" /C:\"OS Version\"");
//            BufferedReader in = new BufferedReader(new InputStreamReader(pr.getInputStream()));
//            return in.readLine();
//        } catch (IOException e) {
//            System.err.println(e.getMessage());
//        }
//        return "";
//    }

    public static String getEdition() {
        String osName = findSysInfo("OS Version:");
//        if (!osName.isEmpty()) {
//            for (String edition : EDITIONS) {
//                if (osName.contains(edition)) {
//                    return edition;
//                }
//            }
//        }
        Scanner s = new Scanner(osName).useDelimiter("                ");
        String firstLine=s.next();
        String osBuildNoStr=s.next();
        //System.out.println("OS version is " + osName);
        Scanner sc = new Scanner(osBuildNoStr).useDelimiter(" ");
        String osBuildNo=sc.next();
        System.out.println(osBuildNo);
        return null;
    }
}