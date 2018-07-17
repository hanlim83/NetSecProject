import Model.SMS;
import Model.ScheduledExecutorServiceHandler;
import com.jfoenix.controls.*;
import com.jfoenix.transitions.hamburger.HamburgerBackArrowBasicTransition;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;
import org.pcap4j.core.PcapNetworkInterface;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ControllerCALandingSetOptions implements Initializable {
    public static AnchorPane rootP;
    @FXML
    private AnchorPane anchorPane;

    @FXML
    private JFXHamburger hamburger;

    @FXML
    private JFXDrawer drawer;

    @FXML
    private JFXProgressBar progressBar;

    @FXML
    private JFXTextField pcapFilesDirectoryField;

    @FXML
    private JFXComboBox<String> ThresholdChooser;

    @FXML
    private JFXButton previousBtn;

    @FXML
    private JFXButton nextBtn;

    private PcapNetworkInterface device;
    private Scene myScene;
    private ScheduledExecutorServiceHandler handler;
    private String directoryPath;
    private SMS SMSHandler;
    private String intDisplayName;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        hamburgerBar();
        nextBtn.setDisable(true);
        ArrayList<String> thresholds = new ArrayList<String>();
        thresholds.add("None");
        thresholds.add("100");
        thresholds.add("500");
        thresholds.add("1000");
        thresholds.add("2500");
        thresholds.add("5000");
        thresholds.add("10000");
        ObservableList<String> thresholdList = FXCollections.observableArrayList(thresholds);
        ThresholdChooser.setItems(thresholdList);
        ThresholdChooser.setValue("Select Threshold");
    }

    public void passVariables(ScheduledExecutorServiceHandler handler, PcapNetworkInterface device, String directoryPath, Integer threshold, SMS SMSHandler, String intDisplayName) {
        this.handler = handler;
        this.device = device;
        this.SMSHandler = SMSHandler;
        if (directoryPath != null) {
            this.directoryPath = directoryPath;
            pcapFilesDirectoryField.setText(this.directoryPath);
        }
        if (threshold != null && threshold != 0) {
            ThresholdChooser.setValue(Integer.toString(threshold));
            if (checkFields())
                nextBtn.setDisable(false);
            else {
                nextBtn.setDisable(true);
            }
        }
        try {
            FXMLLoader loader = new FXMLLoader();
            loader.load(getClass().getResource("AdminSideTab.fxml").openStream());
            ControllerAdminSideTab ctrl = loader.getController();
            ctrl.getVariables(this.device, this.handler, null, null, 0, this.SMSHandler);
        } catch (IOException e) {
            e.printStackTrace();
        }
        this.intDisplayName = intDisplayName;
    }

    @FXML
    public void checkThresholdValue(ActionEvent event) {
        if (checkFields())
            nextBtn.setDisable(false);
        else {
            nextBtn.setDisable(true);
        }
    }

    public boolean checkFields() {
        return !pcapFilesDirectoryField.getText().isEmpty() && !ThresholdChooser.getSelectionModel().getSelectedItem().equals("Select Threshold");
    }

    @FXML
    public void goBackPreviousScreen(ActionEvent event) {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("CALandingSelectInt.fxml"));
        myScene = anchorPane.getScene();
        Stage stage = (Stage) (myScene).getWindow();
        Parent nextView = null;
        try {
            nextView = loader.load();
            ControllerCALandingSelectInt controller = loader.getController();
            if (ThresholdChooser.getSelectionModel().getSelectedItem().equals("Select Threshold") || ThresholdChooser.getSelectionModel().getSelectedItem().equals("None"))
                controller.passVariables(handler, device, directoryPath, 0, SMSHandler, intDisplayName);
            else
                controller.passVariables(handler, device, directoryPath, Integer.parseInt(ThresholdChooser.getSelectionModel().getSelectedItem()), SMSHandler, intDisplayName);
        } catch (IOException e) {
            e.printStackTrace();
        }
        stage.setScene(new Scene(nextView));
        stage.show();
    }

    @FXML
    public void goToNextScreen(ActionEvent event) {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("CALandingVerifyDetails.fxml"));
        myScene = anchorPane.getScene();
        Stage stage = (Stage) (myScene).getWindow();
        Parent nextView = null;
        try {
            nextView = loader.load();
            ControllerCALandingVerifyDetails controller = loader.getController();
            if (ThresholdChooser.getSelectionModel().getSelectedItem().equals("Select Threshold") || ThresholdChooser.getSelectionModel().getSelectedItem().equals("None"))
                controller.passVariables(handler, device, directoryPath, 0, SMSHandler, intDisplayName);
            else
                controller.passVariables(handler, device, directoryPath, Integer.parseInt(ThresholdChooser.getSelectionModel().getSelectedItem()), SMSHandler, intDisplayName);
        } catch (IOException e) {
            e.printStackTrace();
        }
        stage.setScene(new Scene(nextView));
        stage.show();
    }

    @FXML
    public void selectPcapLocation(MouseEvent event) {
        myScene = anchorPane.getScene();
        Stage stage = (Stage) (myScene).getWindow();
        DirectoryChooser chooser = new DirectoryChooser();
        chooser.setTitle("Choose Pcap Files Save Directory");
        File selectedDirectory = chooser.showDialog(stage);
        if (selectedDirectory == null)
            return;
        directoryPath = selectedDirectory.getAbsolutePath();
        pcapFilesDirectoryField.setText(directoryPath);
        if (checkFields())
            nextBtn.setDisable(false);
        else {
            nextBtn.setDisable(true);
        }
    }

    public void hamburgerBar() {
        rootP = anchorPane;

        try {
            VBox box = FXMLLoader.load(getClass().getResource("AdminSideTab.fxml"));
            drawer.setSidePane(box);
            drawer.setVisible(false);
            drawer.setDefaultDrawerSize(219);
        } catch (IOException ex) {
            Logger.getLogger(ControllerBaseLayoutNew.class.getName()).log(Level.SEVERE, null, ex);
        }

        HamburgerBackArrowBasicTransition transition = new HamburgerBackArrowBasicTransition(hamburger);
        transition.setRate(-1);
        hamburger.addEventHandler(MouseEvent.MOUSE_PRESSED, (e) -> {
            transition.setRate(transition.getRate() * -1);
            transition.play();

            if (drawer.isOpened()) {
                drawer.close();
                drawer.setDisable(true);
                //drawer.setVisible(false);
            } else {
                drawer.open();
                drawer.setVisible(true);
                drawer.setDisable(false);
            }
        });
    }
}

