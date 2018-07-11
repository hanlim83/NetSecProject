import Model.FileScanner;
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXDrawer;
import com.jfoenix.controls.JFXHamburger;
import javafx.fxml.FXML;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import java.io.File;
import java.io.IOException;


public class ControllerFileScanner {
        private Scene myScene;

        @FXML
        private AnchorPane anchorPane;

        @FXML
        private JFXHamburger hamburger;

        @FXML
        private JFXDrawer drawer;

        @FXML
        private JFXButton scannerButton;

        @FXML
        private Label browseLabel;

        @FXML
        private JFXButton reportButton;

        @FXML
        private JFXButton browserFile;

        @FXML
        public void Scanner(ActionEvent event) {

            FileScanner fileScanner = new FileScanner();

            fileScanner.Scanner(browseLabel.getText());

            boolean scanValid = fileScanner.scannerReport();
            if (scanValid == true){

                System.out.print("Valid scan = true\n");
                reportButton.setVisible(true);

            } else {

                System.out.print("Valid scan = false");
                reportButton.setVisible(false);

            }



//            try {
//
//
//                VirusTotalConfig.getConfigInstance().setVirusTotalAPIKey("1ac910b3fbfcb977b199e7113a20030386f81ce6ba242d4c56683789f08ae42e");
//                VirustotalPublicV2 virusTotalRef = new VirustotalPublicV2Impl();
//
//                String filePath = browseLabel.getText();
//
//                ScanInfo scanInformation = virusTotalRef.scanFile(new File(filePath));
//
//                System.out.println("==========SCAN INFORMATION==========");
//                System.out.println("MD5 :\t" + scanInformation.getMd5());
//                System.out.println("Perma Link :\t" + scanInformation.getPermalink());
//                System.out.println("Resource :\t" + scanInformation.getResource());
//                System.out.println("Scan Date :\t" + scanInformation.getScanDate());
//                System.out.println("Scan Id :\t" + scanInformation.getScanId());
//                System.out.println("SHA1 :\t" + scanInformation.getSha1());
//                System.out.println("SHA256 :\t" + scanInformation.getSha256());
//                System.out.println("Verbose Msg :\t" + scanInformation.getVerboseMessage());
//                System.out.println("Response Code :\t" + scanInformation.getResponseCode());
//                System.out.println("done.\n");
//
//
//                String resource= scanInformation.getResource();
//                FileScanReport report = virusTotalRef.getScanReport(resource);
//
//                int testPositive = report.getPositives();
//
//                System.out.println("Positives :\t" + report.getPositives());
//                System.out.println("Total :\t" + report.getTotal() + "\n");
//
//
//                Map<String, VirusScanInfo> scans = report.getScans();
//
//                for (String key : scans.keySet()) {
//                    VirusScanInfo virusInfo = scans.get(key);
//                    System.out.println("Scanner : " + key);
//                    System.out.println("\t\t Result : " + virusInfo.getResult());
//                    System.out.println("\t\t Update : " + virusInfo.getUpdate());
//                    System.out.println("\t\t Version :" + virusInfo.getVersion() + "\n\n");
//                    }
//
//                if (testPositive >= 5) {
//
//                    Alert alert = new Alert(Alert.AlertType.WARNING);
//                    alert.setTitle("FireE");
//                    alert.setHeaderText("This file is malicious!");
//                    alert.setContentText("This file will not be pushed to the cloud as our systems detects this file to be malicious.");
//                    alert.showAndWait();
//
//                    System.out.println("\033[31;1mThis file is malicious!\033[0m");
//
//                }
//
//                else {
//
//                    Alert alert = new Alert(Alert.AlertType.INFORMATION);
//                    alert.setTitle("FireE");
//                    alert.setHeaderText("This file is safe!");
//                    alert.setContentText("This file will be pushed to our cloud servers momentarily.");
//                    alert.showAndWait();
//
//                    System.out.println("\033[32mThis file is safe!\033[0m");
//
//                }
//
//            } catch (APIKeyNotFoundException ex) {
//                System.err.println("API Key not found! " + ex.getMessage());
//            } catch (UnsupportedEncodingException ex) {
//                System.err.println("Unsupported Encoding Format!" + ex.getMessage());
//            } catch (UnauthorizedAccessException ex) {
//                System.err.println("Invalid API Key " + ex.getMessage());
//            } catch (Exception ex) {
//                System.err.println("Something Bad Happened! " + ex.getMessage());
//            }
        }

        @FXML

        public void fileBrowser(ActionEvent event) {

            reportButton.setVisible(false);

            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Open Resource File");
            File file=fileChooser.showOpenDialog(null);

            String pathFile = file.getAbsolutePath();

            browseLabel.setText(pathFile);


        }


    @FXML
    public void viewReport(ActionEvent event) {

        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getResource("ScanReport.fxml"));
        myScene = (Scene) ((Node) event.getSource()).getScene();
        Stage stage = (Stage) (myScene).getWindow();
        Parent nextView = null;
        try {
            nextView = loader.load();
        } catch (IOException e) {
            e.printStackTrace();
        }

        stage.setScene(new Scene(nextView));
        stage.setTitle("NSPJ");
        stage.show();


    }


        }


