import Model.SMS;
import Model.ScheduledExecutorServiceHandler;
import com.jfoenix.animation.alert.JFXAlertAnimation;
import com.jfoenix.controls.*;
import com.jfoenix.transitions.hamburger.HamburgerBackArrowBasicTransition;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Callback;
import org.pcap4j.core.PcapAddress;
import org.pcap4j.core.PcapNativeException;
import org.pcap4j.core.PcapNetworkInterface;
import org.pcap4j.core.Pcaps;

import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
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

    private List<PcapNetworkInterface> devices;
    private PcapNetworkInterface device;
    private Scene myScene;
    private ScheduledExecutorServiceHandler handler;
    private String directoryPath;
    private Integer threshold;
    private SMS SMSHandler;
    private String intDisplayedName = null;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        hamburgerBar();
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
            int duplicate = 1;
            for (PcapNetworkInterface i : devices) {
                TreeItem<String> Interface = new TreeItem<String>(i.getDescription());
                for (int j = 1; j < names.size(); j++) {
                    if (i.getDescription().equals(names.get(j)) && !i.getName().equals(idS.get(j))) {
                        Interface.setValue(i.getDescription() + " " + duplicate);
                        ++duplicate;
                    }
                }
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
            intDisplayCol.setCellValueFactory(new Callback<TreeTableColumn.CellDataFeatures<String, String>, ObservableValue<String>>() {
                @Override
                public ObservableValue<String> call(TreeTableColumn.CellDataFeatures<String, String> param) {
                    return new SimpleStringProperty(param.getValue().getValue());
                }
            });
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
            nextBtn.setDisable(false);
        }
    }

    public void passVariables(ScheduledExecutorServiceHandler handler, PcapNetworkInterface device, String directoryPath, Integer threshold, SMS SMSHandler, String intDisplayedName) {
        this.handler = handler;
        this.device = device;
        this.directoryPath = directoryPath;
        this.threshold = threshold;
        this.SMSHandler = SMSHandler;
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
            ctrl.getVariables(null, this.handler, null, null, 0, this.SMSHandler);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    public void goToNextScreen(ActionEvent event) {
        myScene = anchorPane.getScene();
        Stage stage = (Stage) (myScene).getWindow();
        String title = "Comfirmation of Selection";
        String content = "You have selected " + intDisplayedName + ", are you sure you this is adapter that you have selected?";
        JFXButton close = new JFXButton("Yes");
        close.setButtonType(JFXButton.ButtonType.RAISED);
        close.setStyle("-fx-background-color: #00bfff;");
        JFXButton selectAgain = new JFXButton("Select Again");
        close.setButtonType(JFXButton.ButtonType.RAISED);
        close.setStyle("-fx-background-color: #00bfff;");
        JFXDialogLayout layout = new JFXDialogLayout();
        layout.setHeading(new Label(title));
        layout.setBody(new Label(content));
        layout.setActions(selectAgain, close);
        JFXAlert<Void> alert = new JFXAlert<>(stage);
        alert.setOverlayClose(true);
        alert.setAnimation(JFXAlertAnimation.CENTER_ANIMATION);
        alert.setContent(layout);
        alert.initModality(Modality.NONE);
        selectAgain.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent __) {
                alert.hideWithAnimation();
                device = null;
                nextBtn.setDisable(true);
            }
        });
        close.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                alert.hideWithAnimation();
                FXMLLoader loader = new FXMLLoader(getClass().getResource("CALandingSetOptions.fxml"));
                myScene = anchorPane.getScene();
                Parent nextView = null;
                try {
                    nextView = loader.load();
                    ControllerCALandingSetOptions controller = loader.getController();
                    controller.passVariables(handler, device, directoryPath, threshold, SMSHandler, intDisplayedName);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                stage.setScene(new Scene(nextView));
                stage.setTitle("Set Options");
                stage.show();
            }
        });
        alert.showAndWait();
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

