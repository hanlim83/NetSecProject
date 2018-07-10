package Model;

import com.kanishka.virustotal.dto.FileScanReport;
import com.kanishka.virustotal.dto.VirusScanInfo;
import com.kanishka.virustotal.exception.APIKeyNotFoundException;
import com.kanishka.virustotal.exception.QuotaExceededException;
import com.kanishka.virustotal.exception.UnauthorizedAccessException;
import com.kanishka.virustotalv2.VirusTotalConfig;
import com.kanishka.virustotalv2.VirustotalPublicV2;
import com.kanishka.virustotalv2.VirustotalPublicV2Impl;
import javafx.beans.InvalidationListener;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.scene.control.TableColumn;
import javafx.scene.control.cell.PropertyValueFactory;

import java.io.IOException;
import java.util.*;

public class Report {

    private static String index;
    private static String software;
    private static String version;
    private static String result;

    public String getIndex() {
        return index;
    }

    public String getSoftware() {
        return software;
    }

    public String getVersion() {
        return version;
    }

    public String getResult() {
        return result;
    }

    private static ArrayList<String> indexNo = new ArrayList();
    private static ArrayList<String> avSoftware = new ArrayList();
    private static ArrayList<String> softwareVersion = new ArrayList();
    private static ArrayList<String> results = new ArrayList();

    private ObservableList<Report> reportLister = FXCollections.observableArrayList();

    public Report(String indexx, String softwaree, String versionn, String resultss) {

    }

    public Report() {

    }


    public void assignment() {


        try {
            VirusTotalConfig.getConfigInstance().setVirusTotalAPIKey("1ac910b3fbfcb977b199e7113a20030386f81ce6ba242d4c56683789f08ae42e");
            VirustotalPublicV2 virusTotalRef = new VirustotalPublicV2Impl();

            FileScanner res = new FileScanner();
            String resource = res.giveResource();

            System.out.print(resource + "\n\n");

            FileScanReport report = virusTotalRef.getScanReport(resource);

            Map<String, VirusScanInfo> scans = report.getScans();


            for (String key : scans.keySet()) {


                VirusScanInfo virusInfo = scans.get(key);

                this.software = key;
                String this1 = key;
                avSoftware.add(this1);
                System.out.println(avSoftware + "\n\n\n");

                this.version = virusInfo.getVersion();
                softwareVersion.add(version);
                System.out.println(softwareVersion + "\n\n\n");

                if (virusInfo.isDetected() == true) {

                    this.result = new String("Malicious");
                    results.add(result);
                    System.out.println(results + "\n\n\n");
                } else {

                    this.result = new String("Safe");
                    results.add(result);
                    System.out.println(results + "\n\n\n");

                }
            }
            int i;
            for (i = 0; i <= report.getTotal(); i++) {


                this.index = Integer.toString(i);
                indexNo.add(index);
            }


        } catch (APIKeyNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (UnauthorizedAccessException e) {
            e.printStackTrace();
        } catch (QuotaExceededException e) {
            e.printStackTrace();
        }

    }

    public ObservableList tryThis() {

        int i;
        for (i = 1; i < indexNo.size(); i++) {


            String ind = indexNo.get(i);
            String soft = avSoftware.get(i);
            String vers = softwareVersion.get(i);
            String resu = results.get(i);

            reportLister.add(new Report(ind, soft, vers, resu));

            return reportLister;

        }
        return reportLister;
    }
}

