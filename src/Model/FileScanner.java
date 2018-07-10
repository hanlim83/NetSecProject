package Model;

import com.kanishka.virustotal.dto.FileScanReport;
import com.kanishka.virustotal.dto.ScanInfo;
import com.kanishka.virustotal.dto.VirusScanInfo;
import com.kanishka.virustotal.exception.APIKeyNotFoundException;
import com.kanishka.virustotal.exception.QuotaExceededException;
import com.kanishka.virustotal.exception.UnauthorizedAccessException;
import com.kanishka.virustotalv2.VirusTotalConfig;
import com.kanishka.virustotalv2.VirustotalPublicV2;
import com.kanishka.virustotalv2.VirustotalPublicV2Impl;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.Alert;
import javafx.scene.control.TableColumn;
import javafx.scene.control.cell.PropertyValueFactory;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Map;

public class FileScanner {

    private static String resource;
    private static String index;
    private static SimpleStringProperty software;
    private static SimpleStringProperty version;
    private static SimpleStringProperty result;

    public void Scanner (String filePath) {

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


            String resource= scanInformation.getResource();
            this.resource = resource;



//            FileScanReport report = virusTotalRef.getScanReport(resource);
//
//            int testPositive = report.getPositives();
//
//            System.out.println("Positives :\t" + report.getPositives());
//            System.out.println("Total :\t" + report.getTotal() + "\n");
//
//
//            Map<String, VirusScanInfo> scans = report.getScans();
//
//            for (String key : scans.keySet()) {
//                VirusScanInfo virusInfo = scans.get(key);
//                System.out.println("Scanner : " + key);
//                System.out.println("\t\t Result : " + virusInfo.getResult());
//                System.out.println("\t\t Update : " + virusInfo.getUpdate());
//                System.out.println("\t\t Version :" + virusInfo.getVersion() + "\n\n");
//            }
//
//            if (testPositive >= 5) {
//
//                Alert alert = new Alert(Alert.AlertType.WARNING);
//                alert.setTitle("FireE");
//                alert.setHeaderText("This file is malicious!");
//                alert.setContentText("This file will not be pushed to the cloud as our systems detects this file to be malicious.");
//                alert.showAndWait();
//
//                System.out.println("\033[31;1mThis file is malicious!\033[0m");
//
//            }
//
//            else {
//
//                Alert alert = new Alert(Alert.AlertType.INFORMATION);
//                alert.setTitle("FireE");
//                alert.setHeaderText("This file is safe!");
//                alert.setContentText("This file will be pushed to our cloud servers momentarily.");
//                alert.showAndWait();
//
//                System.out.println("\033[32mThis file is safe!\033[0m");
//
//            }
//
        } catch (APIKeyNotFoundException ex) {
            System.err.println("API Key not found! " + ex.getMessage());
        } catch (UnsupportedEncodingException ex) {
            System.err.println("Unsupported Encoding Format!" + ex.getMessage());
        } catch (UnauthorizedAccessException ex) {
            System.err.println("Invalid API Key " + ex.getMessage());
        } catch (Exception ex) {
            System.err.println("Something Bad Happened! " + ex.getMessage());
        }
    }

    public boolean scannerReport(){

    try{

        VirusTotalConfig.getConfigInstance().setVirusTotalAPIKey("1ac910b3fbfcb977b199e7113a20030386f81ce6ba242d4c56683789f08ae42e");
        VirustotalPublicV2 virusTotalRef = new VirustotalPublicV2Impl();

        FileScanReport report = virusTotalRef.getScanReport(resource);

        int testPositive = report.getPositives();

        System.out.println("Positives :\t" + report.getPositives());
        System.out.println("Total :\t" + report.getTotal() + "\n");


        Map<String, VirusScanInfo> scans = report.getScans();

        for (String key : scans.keySet()) {
            VirusScanInfo virusInfo = scans.get(key);
//            System.out.println("Scanner : " + key);
//            System.out.println("\t\t Result : " + virusInfo.getResult());
//            System.out.println("\t\t Update : " + virusInfo.getUpdate());
//            System.out.println("\t\t Version :" + virusInfo.getVersion() + "\n\n");
        }

        if (testPositive >= 5) {

            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("FireE");
            alert.setHeaderText("This file is malicious!");
            alert.setContentText("This file will not be pushed to the cloud as our systems detects this file to be malicious.");
            alert.showAndWait();

            System.out.println("\033[31;1mThis file is malicious!\033[0m");

        }

        else {

            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("FireE");
            alert.setHeaderText("This file is safe!");
            alert.setContentText("This file will be pushed to our cloud servers momentarily.");
            alert.showAndWait();

            System.out.println("\033[32mThis file is safe!\033[0m");

        }

        return true;

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

    public String giveResource (){

      return resource;

    }



//    public void printScan () {
//
//                        try {
//                            VirusTotalConfig.getConfigInstance().setVirusTotalAPIKey("1ac910b3fbfcb977b199e7113a20030386f81ce6ba242d4c56683789f08ae42e");
//                            VirustotalPublicV2 virusTotalRef = new VirustotalPublicV2Impl();
//
//                            FileScanReport report = virusTotalRef.getScanReport(resource);
//
//                            Map<String, VirusScanInfo> scans = report.getScans();
//
//                            for (String key : scans.keySet()) {
//                                VirusScanInfo virusInfo = scans.get(key);
//                System.out.println("Scanner : " + key);
//                System.out.println("\t\t Result : " + virusInfo.getResult());
//                System.out.println("\t\t Update : " + virusInfo.getUpdate());
//                System.out.println("\t\t Version :" + virusInfo.getVersion() + "\n\n");
//
//                                this.software = new SimpleStringProperty(key);
//                                this.version = new SimpleStringProperty(virusInfo.getVersion());
//
//
//                                if (virusInfo.isDetected() == true){
//
//                                    this.result = new SimpleStringProperty("Malicious");
//
//                                } else {
//
//                                    this.result = new SimpleStringProperty("Safe");
//                                }
//
//                System.out.print(index+") ");
//                                int i;
//                                for (i=1;i<=report.getTotal();i++) {
//
//                                    this.index = Integer.toString(i);
//
//
//                    ObservableList <FileScanner> reportView = FXCollections.observableArrayList(
//                    new FileScanner(index1, software1, version1, result1));
//
//                                    TableColumn index = new TableColumn("#");
//                                    TableColumn antiVirus = new TableColumn("Software");
//                                    TableColumn version = new TableColumn("Version");
//                                    TableColumn scanResult = new TableColumn("Result");
//
//
//                                    scanView.getColumns().addAll(index,antiVirus,version,scanResult);
//
//                                    index.setCellValueFactory(new PropertyValueFactory<FileScanner, String>("index"));
//                                    antiVirus.setCellValueFactory(new PropertyValueFactory<FileScanner, String>("antiVirus"));
//                                    version.setCellValueFactory(new PropertyValueFactory<FileScanner, String>("version"));
//                                    scanResult.setCellValueFactory(new PropertyValueFactory<FileScanner, String>("scanResult"));
//
//
//                                    System.out.println(index + ")" + software + "\t\t");
//                                    System.out.print(version + "\t\t");
//                                    System.out.print(result + "\n\n");
//
//                }
//            }
//
//        } catch (APIKeyNotFoundException e) {
//            e.printStackTrace();
//        } catch (IOException e) {
//            e.printStackTrace();
//        } catch (UnauthorizedAccessException e) {
//            e.printStackTrace();
//        } catch (QuotaExceededException e) {
//            e.printStackTrace();
//        }
//
//    }



}
