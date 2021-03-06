package Model;

import com.kanishka.virustotal.dto.FileScanReport;
import com.kanishka.virustotal.dto.ScanInfo;
import com.kanishka.virustotal.dto.VirusScanInfo;
import com.kanishka.virustotal.exception.APIKeyNotFoundException;
import com.kanishka.virustotal.exception.UnauthorizedAccessException;
import com.kanishka.virustotalv2.VirusTotalConfig;
import com.kanishka.virustotalv2.VirustotalPublicV2;
import com.kanishka.virustotalv2.VirustotalPublicV2Impl;
import org.apache.commons.io.FilenameUtils;
import java.io.File;
import java.io.UnsupportedEncodingException;
import java.util.Map;

public class FileScanner {

    private static String resource;

    public String Scanner(String filePath) {


        try {


            VirusTotalConfig.getConfigInstance().setVirusTotalAPIKey("1ac910b3fbfcb977b199e7113a20030386f81ce6ba242d4c56683789f08ae42e");
            VirustotalPublicV2 virusTotalRef = new VirustotalPublicV2Impl();

            ScanInfo scanInformation = virusTotalRef.scanFile(new File(filePath));

            System.out.println("==========SCAN INFORMATION==========");
            System.out.println("MD5 :\t" + scanInformation.getMd5());
            System.out.println("Perma Link :\t" + scanInformation.getPermalink());
            System.out.println("Resource :\t" + scanInformation.getResource());
            System.out.println("Scan Date :\t" + scanInformation.getScanDate());
            System.out.println("Scan Id :\t" + scanInformation.getScanId());
            System.out.println("SHA1 :\t" + scanInformation.getSha1());
            System.out.println("SHA256 :\t" + scanInformation.getSha256());
            System.out.println("Verbose Msg :\t" + scanInformation.getVerboseMessage());
            System.out.println("Response Code :\t" + scanInformation.getResponseCode());
            System.out.println("done.\n");


            String resource = scanInformation.getResource();
            this.resource = resource;

            return filePath;


        } catch (APIKeyNotFoundException ex) {
            System.err.println("API Key not found! " + ex.getMessage());
        } catch (UnsupportedEncodingException ex) {
            System.err.println("Unsupported Encoding Format!" + ex.getMessage());
        } catch (UnauthorizedAccessException ex) {
            System.err.println("Invalid API Key " + ex.getMessage());
        } catch (Exception ex) {
            System.err.println("Something Bad Happened! " + ex.getMessage());
        }

        return null;
    }

    public boolean scannerReport() {

        try {

            VirusTotalConfig.getConfigInstance().setVirusTotalAPIKey("1ac910b3fbfcb977b199e7113a20030386f81ce6ba242d4c56683789f08ae42e");
            VirustotalPublicV2 virusTotalRef = new VirustotalPublicV2Impl();

            FileScanReport report = virusTotalRef.getScanReport(resource);

            int testPositive = report.getPositives();

            System.out.println("Positives :\t" + report.getPositives());
            System.out.println("Total :\t" + report.getTotal() + "\n");


            Map<String, VirusScanInfo> scans = report.getScans();

            for (String key : scans.keySet()) {
                VirusScanInfo virusInfo = scans.get(key);
                System.out.println("Scanner : " + key);
                System.out.println("\t\t Result : " + virusInfo.getResult());
                System.out.println("\t\t Update : " + virusInfo.getUpdate());
                System.out.println("\t\t Version :" + virusInfo.getVersion() + "\n\n");
            }

            if (testPositive >= 3) {

//                Alert alert = new Alert(Alert.AlertType.WARNING);
//                alert.setTitle("FireE");
//                alert.setHeaderText("This file is malicious!");
//                alert.setContentText("This file will not be pushed to the cloud as our systems detects this file to be malicious.");
//                alert.showAndWait();

                System.out.println("\033[31;1mThis file is malicious!\033[0m");

                return false;

            } else {

//                Alert alert = new Alert(Alert.AlertType.INFORMATION);
//                alert.setTitle("FireE");
//                alert.setHeaderText("This file is safe!");
//                alert.setContentText("This file will be pushed to our cloud servers momentarily.");
//                alert.showAndWait();

                System.out.println("\033[32mThis file is safe!\033[0m");
                return true;

            }


        } catch (APIKeyNotFoundException ex) {
            System.err.println("API Key not found! " + ex.getMessage());
        } catch (UnsupportedEncodingException ex) {
            System.err.println("Unsupported Encoding Format!" + ex.getMessage());
        } catch (UnauthorizedAccessException ex) {
            System.err.println("Invalid API Key " + ex.getMessage());
        } catch (Exception ex) {
            System.err.println("Something Bad Happened! " + ex.getMessage());
        }

        return false;
    }

    public String giveResource() {

        return resource;

    }

    public static boolean extensionCheck(String path) {



        String ext = FilenameUtils.getExtension(path);
        System.out.print("\n\n" + ext + " - \n");

        if (ext.equals("")) {

            return true;

        } else {
            System.out.print("Extention check = ok");
        }

        return false;

    }

    public static void main (String[]args) {

        FileScanner scan = new FileScanner();
        boolean hello = extensionCheck("C:\\Users\\Fende\\Desktop\\virus.txt");
    }

}
