package Model;

import com.kanishka.virustotal.dto.FileScanReport;
import com.kanishka.virustotal.dto.VirusScanInfo;
import com.kanishka.virustotal.exception.APIKeyNotFoundException;
import com.kanishka.virustotal.exception.QuotaExceededException;
import com.kanishka.virustotal.exception.UnauthorizedAccessException;
import com.kanishka.virustotalv2.VirusTotalConfig;
import com.kanishka.virustotalv2.VirustotalPublicV2;
import com.kanishka.virustotalv2.VirustotalPublicV2Impl;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Map;

public class Report {

    private static ArrayList<Integer> indexNo = new ArrayList();
    private static ArrayList<String> avSoftware = new ArrayList();
    private static ArrayList<String> softwareVersion = new ArrayList();
    private static ArrayList<String> results = new ArrayList();

    private int index;
    private String software;
    private String version;
    private String result;

    private ObservableList<Report> reportLister = FXCollections.observableArrayList();

    public Report(int indexx, String softwaree, String versionn, String resultss) {
        this.index = indexx;
        this.software = softwaree;
        this.version = versionn;
        this.result = resultss;
    }

    public Report() {
    }

    public int getIndex() {
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

                String this1 = key;
                avSoftware.add(this1);

                String this2 = virusInfo.getVersion();
                softwareVersion.add(this2);

                if (virusInfo.isDetected() == true) {
                    String this3 = "Malicious";
                    results.add(this3);
                } else {
                    String this3 = "Safe";
                    results.add(this3);
                }
            }
            int i;
            for (i = 0; i <= report.getTotal(); i++) {


                this.index = i;
                indexNo.add(index);
            }

            System.out.println(indexNo + "\n");
            System.out.println(avSoftware + "\n");
            System.out.println(softwareVersion + "\n");
            System.out.println(results + "\n");



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
        for (i = 0; i < indexNo.size() -1; i++) {


            int ind = indexNo.get(i);
            String soft = avSoftware.get(i);
            String vers = softwareVersion.get(i);
            String resu = results.get(i);
            reportLister.add(new Report(++ind, soft, vers, resu));
        }
        return reportLister;
    }
}

