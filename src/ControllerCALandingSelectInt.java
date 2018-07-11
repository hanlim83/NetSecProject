import Model.ScheduledExecutorServiceHandler;
import com.jfoenix.controls.*;
import com.jfoenix.transitions.hamburger.HamburgerBackArrowBasicTransition;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeTableColumn;
import javafx.scene.control.TreeTableView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
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
    private JFXComboBox<String> InterfaceChooser;

    @FXML
    private JFXButton nextBtn;
    private List<PcapNetworkInterface> devices;
    private PcapNetworkInterface device;
    private Scene myScene;
    private ScheduledExecutorServiceHandler handler;
    private String directoryPath;
    private Integer threshold;

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
            List<String> idS = devices.stream().map(PcapNetworkInterface::getName).collect(Collectors.toList());
            ObservableList<String> intidS = FXCollections.observableList(idS);
            InterfaceChooser.setItems(intidS);
            InterfaceChooser.setValue("Select an Interface");
            TreeItem<String> dummyRoot = new TreeItem<String>();
            for (PcapNetworkInterface i : devices) {
                TreeItem<String> Interface = new TreeItem<String>(i.getName());
                TreeItem<String> InterfaceID = new TreeItem<String>("Interface ID: "+i.getName());
                TreeItem<String> InterfaceDescription = new TreeItem<String>("Interface Description: "+i.getDescription());
                TreeItem<String> InterfaceAddressesHeader = new TreeItem<String>("Assigned IP Addresses");
                List<PcapAddress> interfaceAddresses = i.getAddresses();
                for (PcapAddress a : interfaceAddresses){
                    TreeItem<String> InterfaceAddress = new TreeItem<String>(a.getAddress().getHostAddress().toUpperCase());
                    InterfaceAddressesHeader.getChildren().add(InterfaceAddress);
                }
                Interface.getChildren().setAll(InterfaceID,InterfaceDescription,InterfaceAddressesHeader);
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

    public void passVariables(ScheduledExecutorServiceHandler handler,PcapNetworkInterface device, String directoryPath, Integer threshold) {
        this.handler = handler;
        this.device = device;
        this.directoryPath = directoryPath;
        this.threshold = threshold;
        if (device !=null) {
            InterfaceChooser.setValue(device.getName());
            nextBtn.setDisable(false);
        }
        try {
            FXMLLoader loader = new FXMLLoader();
            loader.load(getClass().getResource("AdminSideTab.fxml").openStream());
            ControllerAdminSideTab ctrl = loader.<ControllerAdminSideTab>getController();
            ctrl.getVariables(null, this.handler, null,null,0);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    public void enableNextBtn(ActionEvent event) {
        device = devices.get(InterfaceChooser.getSelectionModel().getSelectedIndex());
        nextBtn.setDisable(false);
    }

    @FXML
    public void goToNextScreen(ActionEvent event) {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("CALandingSetOptions.fxml"));
        myScene = anchorPane.getScene();
        Stage stage = (Stage) (myScene).getWindow();
        Parent nextView = null;
        try {
            nextView = loader.load();
            ControllerCALandingSetOptions controller = loader.<ControllerCALandingSetOptions>getController();
            controller.passVariables(handler,device,directoryPath,threshold);
        } catch (IOException e) {
            e.printStackTrace();
        }
        stage.setScene(new Scene(nextView));
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

