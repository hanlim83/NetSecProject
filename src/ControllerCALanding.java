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
import javafx.scene.control.Alert;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import org.pcap4j.core.PcapAddress;
import org.pcap4j.core.PcapNativeException;
import org.pcap4j.core.PcapNetworkInterface;
import org.pcap4j.core.Pcaps;

import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;
import java.util.concurrent.ScheduledExecutorService;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

public class ControllerCALanding implements Initializable {
    @FXML
    private AnchorPane anchorPane;
    @FXML
    private JFXHamburger hamburger;
    @FXML
    private JFXButton StartBtn;
    @FXML
    private JFXComboBox<String> InterfaceChooser;
    @FXML
    private JFXTextField InterfaceName;
    @FXML
    private JFXTextField InterfaceAddress1;
    @FXML
    private JFXTextField InterfaceAddress2;
    @FXML
    private JFXTextField InterfaceAddress3;
    @FXML
    private JFXTextField InterfaceAddress4;
    @FXML
    private JFXTextField InterfacePhysicalAddress;
    @FXML
    private JFXDrawer drawer;

    private List<PcapNetworkInterface> devices;
    private PcapNetworkInterface device;
    private Scene myScene;
    public static AnchorPane rootP;
    private ScheduledExecutorService service;

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
            List<String> idS = devices.stream().map(PcapNetworkInterface::getName).collect(Collectors.toList());
            ObservableList<String> intidS = FXCollections.observableList(idS);
            InterfaceChooser.setItems(intidS);
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
    }
    public void passVariables(ScheduledExecutorService service) {
        this.service = service;
        try {
            FXMLLoader loader = new FXMLLoader();
            loader.load(getClass().getResource("AdminSideTab.fxml").openStream());
            ControllerAdminSideTab ctrl = loader.<ControllerAdminSideTab>getController();
            ctrl.getVariables(null,this.service,null);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    @FXML
    public void populateInformation(ActionEvent event) {
        device = devices.get(InterfaceChooser.getSelectionModel().getSelectedIndex());
        List<PcapAddress> interfaceAddresses = device.getAddresses();
        InterfaceName.setText(device.getDescription());
        InterfacePhysicalAddress.setText(device.getLinkLayerAddresses().get(0).toString().toUpperCase());
        InterfaceAddress1.setText(interfaceAddresses.get(0).getAddress().getHostAddress().toUpperCase());
        if (interfaceAddresses.size() >= 2){
            InterfaceAddress2.setText(interfaceAddresses.get(1).getAddress().getHostAddress().toUpperCase());
        }
        else {
            InterfaceAddress2.setText("No Address Assigned");
        }
        if (interfaceAddresses.size() >= 3){
            InterfaceAddress3.setText(interfaceAddresses.get(2).getAddress().getHostAddress().toUpperCase());
        }
        else {
            InterfaceAddress3.setText("No Address Assigned");
        }
        if (interfaceAddresses.size() >= 4){
            InterfaceAddress4.setText(interfaceAddresses.get(3).getAddress().getHostAddress().toUpperCase());
        }
        else {
            InterfaceAddress4.setText("No Address Assigned");
        }
        StartBtn.setDisable(false);
    }
    @FXML
    public void capture(ActionEvent event) {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("CAMainPackets.fxml"));
        myScene = anchorPane.getScene();
        Stage stage = (Stage) (myScene).getWindow();
        Parent nextView = null;
        try {
            nextView = loader.load();
            ControllerCAMainPackets controller = loader.<ControllerCAMainPackets>getController();
            controller.passVariables(device,service,null);
        } catch (IOException e) {
            e.printStackTrace();
        }
        stage.setScene(new Scene(nextView));
        stage.setTitle("NSPJ");
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

