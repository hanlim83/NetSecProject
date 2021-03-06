import Model.AWSSMS;
import Model.IPAddressPolicy;
import Model.OutlookEmail;
import Model.ScheduledThreadPoolExecutorHandler;
import com.jfoenix.animation.alert.JFXAlertAnimation;
import com.jfoenix.controls.*;
import com.jfoenix.transitions.hamburger.HamburgerBackArrowBasicTransition;
import javafx.beans.property.SimpleStringProperty;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Modality;
import javafx.stage.Stage;
import org.pcap4j.core.PcapAddress;
import org.pcap4j.core.PcapNativeException;
import org.pcap4j.core.PcapNetworkInterface;
import org.pcap4j.core.Pcaps;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

public class ControllerCALandingSelectInt implements Initializable {
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
    private TreeTableView<String> intDisplay;

    @FXML
    private TreeTableColumn<String, String> intDisplayCol;

    @FXML
    private JFXButton nextBtn;
    @FXML
    private Label ipAddr;
    @FXML
    private JFXButton homeButton;


    private List<PcapNetworkInterface> devices;
    private PcapNetworkInterface device;
    private Scene myScene;
    private ScheduledThreadPoolExecutorHandler handler;
    private boolean ARPDetection;
    private Integer threshold;
    private AWSSMS SMSHandler;
    private OutlookEmail EmailHandler;
    private String intDisplayedName = null;
    private ArrayList<String> duplicateAdapterName;
    private ArrayList<Integer> duplicateValuses;
    private boolean CaptureType;
    private TreeItem previousTreeItem = null;
    private boolean BlockHome = false;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        hamburgerBar();
        try {
            String whatismyIP = IPAddressPolicy.getIp();
            ipAddr.setText(whatismyIP);
            Boolean validityIP = IPAddressPolicy.isValidRange(whatismyIP);
            if (validityIP == true) {
                ipAddr.setTextFill(Color.rgb(1, 0, 199));
            } else {
                ipAddr.setTextFill(Color.rgb(255, 0, 0));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        Path path2 = FileSystems.getDefault().getPath("src/View/baseline_home_white_18dp.png");
        File file2 = new File(path2.toUri());
        Image imageForFile2;
        try {
            imageForFile2 = new Image(file2.toURI().toURL().toExternalForm());
            ImageView imageView1 = new ImageView(imageForFile2);
//            imageView.setFitHeight(24.5);
//            imageView.setFitWidth(35);
            homeButton.setGraphic(imageView1);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        nextBtn.setDisable(true);
        try {
            System.out.println("Pcap Info: " + Pcaps.libVersion());
            devices = Pcaps.findAllDevs();
            if (devices == null) {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("No Network Interface Found");
                alert.setHeaderText("No Network Interface Found");
                alert.setContentText("Ooops, Pcap4j can't find any Network Interfaces! Please check your interfaces or reinstall WinPcap to continue!");
                alert.showAndWait();
            }
            TreeItem<String> dummyRoot = new TreeItem<String>();
            List<String> idS = devices.stream().map(PcapNetworkInterface::getName).collect(Collectors.toList());
            List<String> names = devices.stream().map(PcapNetworkInterface::getDescription).collect(Collectors.toList());
            duplicateAdapterName = new ArrayList<String>();
            duplicateValuses = new ArrayList<Integer>();
            String adapterName = null;
            for (PcapNetworkInterface i : devices) {
                if (duplicateAdapterName.isEmpty() || !duplicateAdapterName.contains(i.getDescription())) {
                    duplicateAdapterName.add(i.getDescription());
                    duplicateValuses.add(0);
                    adapterName = i.getDescription();
                } else {
                    int index = duplicateAdapterName.indexOf(i.getDescription());
                    duplicateValuses.set(index, duplicateValuses.get(index) + 1);
                    adapterName = i.getDescription() + " " + duplicateValuses.get(index);
                }
                TreeItem<String> Interface = new TreeItem<String>(adapterName);
                TreeItem<String> InterfaceID = new TreeItem<String>("Interface ID: " + i.getName());
                TreeItem<String> InterfaceDescription = new TreeItem<String>("Interface Description: " + i.getDescription());
                TreeItem<String> InterfaceType = null;
                if (i.isLocal())
                    InterfaceType = new TreeItem<String>("Interface Type: Local Network Adapter");
                else if (i.isLoopBack())
                    InterfaceType = new TreeItem<String>("Interface Type: Loopback Network Adapter");
                TreeItem<String> InterfaceStatus = null;
                if (i.isUp() && i.isRunning())
                    InterfaceStatus = new TreeItem<String>("Network Adapter is enabled and connected to a network");
                else if (i.isRunning())
                    InterfaceStatus = new TreeItem<String>("Network Adapter is enabled but not connected to a network");
                Interface.getChildren().add(InterfaceID);
                if (InterfaceType == null && InterfaceStatus == null)
                    Interface.getChildren().add(InterfaceDescription);
                else if (InterfaceStatus == null) {
                    Interface.getChildren().add(InterfaceDescription);
                    Interface.getChildren().add(InterfaceType);
                } else if (InterfaceType == null) {
                    Interface.getChildren().add(InterfaceDescription);
                    Interface.getChildren().add(InterfaceStatus);
                } else {
                    Interface.getChildren().add(InterfaceDescription);
                    Interface.getChildren().add(InterfaceType);
                    Interface.getChildren().add(InterfaceStatus);
                }
                List<PcapAddress> interfaceAddresses = i.getAddresses();
                for (PcapAddress a : interfaceAddresses) {
                    TreeItem<String> InterfaceAddress = new TreeItem<String>("Assigned IP Address: " + a.getAddress().getHostAddress().toUpperCase());
                    Interface.getChildren().add(InterfaceAddress);
                }
                dummyRoot.getChildren().add(Interface);
            }
            intDisplay.setRoot(dummyRoot);
            intDisplay.setShowRoot(false);
            for (TreeItem t : dummyRoot.getChildren()) {
                t.setExpanded(true);
            }
            intDisplay.getStylesheets().add("IntTreeTableViewStyle.css");
            intDisplay.applyCss();
            intDisplayCol.setCellValueFactory(param -> new SimpleStringProperty(param.getValue().getValue()));
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
    }

    @FXML
    void getSelectInt(MouseEvent event) {
        TreeItem<String> selected = intDisplay.getSelectionModel().getSelectedItem();
        if (selected == null || selected.getValue().contains("Interface ID: ") || selected.getValue().contains("Interface Description: ") || selected.getValue().contains("Interface Type: ") || selected.getValue().contains("Network Adapter is enabled") || selected.getValue().contains("Assigned IP Address:"))
            return;
        else {
            TreeItem<String> intID = selected.getChildren().get(0);
            for (PcapNetworkInterface p : devices) {
                if (p.getName().equals(intID.getValue().substring(14))) {
                    device = p;
                    intDisplayedName = selected.getValue();
                }
            }
            if (previousTreeItem == null) {
                selected.setGraphic(new ImageView(new Image(getClass().getResourceAsStream("baseline_check_black_18dp.png"))));
                previousTreeItem = selected;
            } else {
                previousTreeItem.setGraphic(null);
                selected.setGraphic(new ImageView(new Image(getClass().getResourceAsStream("baseline_check_black_18dp.png"))));
                previousTreeItem = selected;
                intDisplay.refresh();
            }

            nextBtn.setDisable(false);
        }
    }

    public void passVariables(ScheduledThreadPoolExecutorHandler handler, PcapNetworkInterface device, boolean ARPDetection, Integer threshold, AWSSMS SMSHandler, String intDisplayedName, boolean Capturetype, OutlookEmail EmailHandler) {
        this.handler = handler;
        this.device = device;
        this.ARPDetection = ARPDetection;
        this.threshold = threshold;
        this.SMSHandler = SMSHandler;
        this.EmailHandler = EmailHandler;
        this.CaptureType = Capturetype;
        if (intDisplayedName != null || intDisplayedName == null) {
            this.device = null;
            nextBtn.setDisable(true);
        } else if (device != null) {
            this.device = device;
            nextBtn.setDisable(false);
        }
        try {
            FXMLLoader loader = new FXMLLoader();
            loader.load(getClass().getResource("AdminSideTab.fxml").openStream());
            ControllerAdminSideTab ctrl = loader.getController();
            ctrl.getVariables(null, this.handler, null, this.ARPDetection, 0, this.SMSHandler, this.EmailHandler);
            BlockHome = ctrl.checktokenFileExists();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    public void goToNextScreen(ActionEvent event) {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("CALandingSetOptions.fxml"));
        myScene = anchorPane.getScene();
        Stage stage = (Stage) (myScene).getWindow();
        Parent nextView = null;
        try {
            nextView = loader.load();
            ControllerCALandingSetOptions controller = loader.getController();
            controller.passVariables(handler, device, ARPDetection, threshold, SMSHandler, intDisplayedName, CaptureType, EmailHandler);
        } catch (IOException e) {
            e.printStackTrace();
        }
        stage.setScene(new Scene(nextView));
        stage.setTitle("Set Options");
        stage.show();
    }

    public void hamburgerBar() {
        rootP = anchorPane;

        try {
            VBox box = FXMLLoader.load(getClass().getResource("AdminSideTab.fxml"));
            drawer.setSidePane(box);
            drawer.setVisible(false);
            drawer.setDefaultDrawerSize(219);
        } catch (IOException ex) {

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

    @FXML
    public void onClickHomeButton(ActionEvent event) throws IOException {
        if (!BlockHome) {
            javafx.scene.control.Alert alert = new javafx.scene.control.Alert(javafx.scene.control.Alert.AlertType.CONFIRMATION);
            alert.setTitle("Access Blocked");
            alert.setHeaderText("Access Blocked");
            alert.setContentText("You have minimized FireE and as a safety precaution, you need to sign in to FireE again to access other functions of the app. Do you want to log in again?");
            Optional<ButtonType> result = alert.showAndWait();
            if (result.get() == ButtonType.OK) {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("AdminLoginPage.fxml"));
                myScene = ((Node) event.getSource()).getScene();
                Stage stage = (Stage) (myScene).getWindow();
                Parent nextView = null;
                try {
                    nextView = loader.load();
                    ControllerAdminLoginPage controller = loader.getController();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                stage.setScene(new Scene(nextView));
                stage.setTitle("Login Page");
                stage.show();
            } else {
                // ... user chose CANCEL or closed the dialog
            }
        } else {
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(getClass().getResource("AdminHome.fxml"));
            Scene myScene = ((Node) event.getSource()).getScene();
            Stage stage = (Stage) (myScene).getWindow();
            Parent nextView = loader.load();
            ControllerAdminHome controller = loader.getController();
            //controller.passData(admin);
            stage.setScene(new Scene(nextView));
            stage.setTitle("Home Page");
            stage.show();
        }
    }
}

