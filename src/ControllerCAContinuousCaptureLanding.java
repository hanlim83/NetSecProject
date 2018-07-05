import Model.NetworkCapture;
import com.jfoenix.animation.alert.JFXAlertAnimation;
import com.jfoenix.controls.*;
import com.jfoenix.transitions.hamburger.HamburgerBackArrowBasicTransition;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;
import org.pcap4j.core.PcapAddress;
import org.pcap4j.core.PcapNativeException;
import org.pcap4j.core.PcapNetworkInterface;
import org.pcap4j.core.Pcaps;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import java.util.concurrent.ScheduledExecutorService;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

public class ControllerCAContinuousCaptureLanding implements Initializable {

    @FXML
    private AnchorPane anchorPane;

    @FXML
    private JFXHamburger hamburger;

    @FXML
    private JFXTextField phoneNumberField;

    @FXML
    private JFXTextField pcapFilePathField;

    @FXML
    private JFXTextField pcapFileNameField;

    @FXML
    private JFXComboBox<String> InterfaceChooser;

    @FXML
    private JFXTextField interfaceIPAddress1;

    @FXML
    private JFXTextField interfaceIPAddress2;

    @FXML
    private JFXTextField interfaceIPAddress3;

    @FXML
    private JFXTextField interfaceIPAddress4;

    @FXML
    private JFXDrawer drawer;

    @FXML
    private JFXButton StartBtn;

    @FXML
    private JFXComboBox<String> ThresholdChooser;

    private Scene myScene;
    public static AnchorPane rootP;
    private List<PcapNetworkInterface> devices;
    private PcapNetworkInterface device;
    //Imported from previous screens
    private PcapNetworkInterface Odevice;
    private ScheduledExecutorService service;
    private NetworkCapture capture;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        hamburgerBar();
        StartBtn.setDisable(true);
        try {
            System.out.println("Pcap Info: " + Pcaps.libVersion());
            devices = Pcaps.findAllDevs();
            if (devices == null){
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("No Network Interface Found");
                alert.setHeaderText("No Network Interface Found");
                alert.setContentText("Ooops, Pcap4j can't find any Network Interfaces! Please check your interfaces or reinstall WinPcap to continue!");
                alert.showAndWait();
            }
            List<String> names = devices.stream().map(PcapNetworkInterface::getDescription).collect(Collectors.toList());
            ObservableList<String> intnames = FXCollections.observableList(names);
            InterfaceChooser.setItems(intnames);
            InterfaceChooser.setValue("Select an Interface");
        } catch (PcapNativeException e) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Pcap4j Error Occurred");
            alert.setHeaderText("Pcap4j Error Detected");
            alert.setContentText("Ooops, there is an error with Pcap4j. The agent depends on Pcap4j and WinPcap to capture packets! Please reinstall WinPcap to continue!");
            alert.showAndWait();
        } catch (UnsatisfiedLinkError e) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Pcap4j Error Occurred");
            alert.setHeaderText("Pcap4j Error Detected");
            alert.setContentText("Ooops, the agent depends on Pcap4j and WinPcap to capture packets! Please reinstall WinPcap to continue!");
            alert.showAndWait();
        }
        phoneNumberField.focusedProperty().addListener((observable, oldValue, newValue) -> {
            if(!newValue) {
                checkPhoneNumber();
            }
        });
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
        phoneNumberField.setFocusTraversable(false);
        pcapFileNameField.setFocusTraversable(false);
        pcapFilePathField.setFocusTraversable(false);
        InterfaceChooser.setFocusTraversable(true);
        interfaceIPAddress1.setFocusTraversable(false);
        interfaceIPAddress2.setFocusTraversable(false);
        interfaceIPAddress3.setFocusTraversable(false);
        interfaceIPAddress4.setFocusTraversable(false);
    }

    public void passVariables(PcapNetworkInterface nif, ScheduledExecutorService service, NetworkCapture Capture){
        this.Odevice = nif;
        this.service = service;
        this.capture = Capture;
    }

    public void checkPhoneNumber() {
        String regexStr = "^[0-9]{8}$";
        if (phoneNumberField.getText().matches(regexStr) && (phoneNumberField.getText().charAt(0) == '8' || phoneNumberField.getText().charAt(0) == '9')) {
            if (checkFields())
                StartBtn.setDisable(false);
            else {
                StartBtn.setDisable(true);
            }
        }
        else {
            myScene = anchorPane.getScene();
            Stage stage = (Stage) (myScene).getWindow();
            String title = "Invaild Phone Number";
            String content = "The Phone Number you entered is invaild, please enter a 8-digit Singapore Telephone Number!";
            JFXButton close = new JFXButton("Close");
            close.setButtonType(JFXButton.ButtonType.RAISED);
            close.setStyle("-fx-background-color: #00bfff;");
            JFXDialogLayout layout = new JFXDialogLayout();
            layout.setHeading(new Label(title));
            layout.setBody(new Label(content));
            layout.setActions(close);
            JFXAlert<Void> alert = new JFXAlert<>(stage);
            alert.setOverlayClose(true);
            alert.setAnimation(JFXAlertAnimation.CENTER_ANIMATION);
            alert.setContent(layout);
            alert.initModality(Modality.NONE);
            close.setOnAction(new EventHandler<ActionEvent>() {
                @Override
                public void handle(ActionEvent __) {
                    alert.hideWithAnimation();
                }
            });
            alert.show();
        }
    }

    @FXML
    public void checkThresholdValue(ActionEvent event) {
        if (checkFields())
            StartBtn.setDisable(false);
        else {
            StartBtn.setDisable(true);
        }
    }

    @FXML
    public void populateInformation(ActionEvent event) {
        device = devices.get(InterfaceChooser.getSelectionModel().getSelectedIndex());
        List<PcapAddress> interfaceAddresses = device.getAddresses();
        interfaceIPAddress1.setText(interfaceAddresses.get(0).getAddress().getHostAddress().toUpperCase());
        if (interfaceAddresses.size() >= 2){
            interfaceIPAddress2.setText(interfaceAddresses.get(1).getAddress().getHostAddress().toUpperCase());
        }
        else {
            interfaceIPAddress2.setText("No Address Assigned");
        }
        if (interfaceAddresses.size() >= 3){
            interfaceIPAddress3.setText(interfaceAddresses.get(2).getAddress().getHostAddress().toUpperCase());
        }
        else {
            interfaceIPAddress3.setText("No Address Assigned");
        }
        if (interfaceAddresses.size() >= 4){
            interfaceIPAddress4.setText(interfaceAddresses.get(3).getAddress().getHostAddress().toUpperCase());
        }
        else {
            interfaceIPAddress4.setText("No Address Assigned");
        }
        if (checkFields())
            StartBtn.setDisable(false);
        else {
            StartBtn.setDisable(true);
        }
    }

    @FXML
    public void selectPcapLocation(MouseEvent event) {
        myScene = anchorPane.getScene();
        Stage stage = (Stage) (myScene).getWindow();
        FileChooser choose = new FileChooser();
        choose.getExtensionFilters().add(new FileChooser.ExtensionFilter("Network Packet Capture File (*.pcap)", "*.pcap"));
        File f = choose.showSaveDialog(stage);
        if (f == null)
            return;
        else if (!f.getName().contains(".")) {
            f = new File(f.getAbsolutePath() + ".pcap");
        }
        pcapFileNameField.setText(f.getName());
        pcapFilePathField.setText(f.getAbsolutePath());
        if (checkFields())
            StartBtn.setDisable(false);
        else {
            StartBtn.setDisable(true);
        }
    }

    @FXML
    public void startCapture(ActionEvent event) {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("CAContinuousCaptureMain.fxml"));
        myScene = anchorPane.getScene();
        Stage stage = (Stage) (myScene).getWindow();
        Parent nextView = null;
        try {
            nextView = loader.load();
            ControllerCAContinuousCaptureMain controller = loader.<ControllerCAContinuousCaptureMain>getController();
            controller.startCapture(Odevice,service,capture,device,pcapFilePathField.getText(),ThresholdChooser.getSelectionModel().getSelectedItem());
        } catch (IOException e) {
            e.printStackTrace();
        }
        stage.setScene(new Scene(nextView));
        stage.show();
    }

    public boolean checkFields (){
        String regexStr = "^[0-9]{8}$";
        if (!phoneNumberField.getText().isEmpty() && phoneNumberField.getText().matches(regexStr) && (phoneNumberField.getText().charAt(0) == '8' || phoneNumberField.getText().charAt(0) == '9') && !pcapFilePathField.getText().isEmpty() && !pcapFileNameField.getText().isEmpty() && !InterfaceChooser.getSelectionModel().getSelectedItem().equals("Select an Interface") && !InterfaceChooser.getSelectionModel().getSelectedItem().isEmpty() && !ThresholdChooser.getSelectionModel().getSelectedItem().equals("Select Threshold"))
            return true;
        else
            return false;
    }

    public void hamburgerBar() {
        rootP = anchorPane;

        try {
            VBox box = FXMLLoader.load(getClass().getResource("AdminSideTab.fxml"));
            drawer.setSidePane(box);
            drawer.setVisible(false);
            drawer.setDefaultDrawerSize(219);
        } catch (IOException ex) {
            Logger.getLogger(ControllerCAContinuousCaptureLanding.class.getName()).log(Level.SEVERE, null, ex);
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
