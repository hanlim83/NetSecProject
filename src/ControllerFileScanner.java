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


