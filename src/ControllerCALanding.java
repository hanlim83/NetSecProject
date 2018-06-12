import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXComboBox;
import com.jfoenix.controls.JFXTextField;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import org.pcap4j.core.PcapAddress;
import org.pcap4j.core.PcapNativeException;
import org.pcap4j.core.PcapNetworkInterface;
import org.pcap4j.core.Pcaps;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

public class ControllerCALanding implements Initializable {
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

    private List<PcapNetworkInterface> devices;
    private PcapNetworkInterface device;
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        try {
            System.out.println("Pcap Info: " + Pcaps.libVersion());
            devices = Pcaps.findAllDevs();
            List<String> names = devices.stream().map(PcapNetworkInterface::getDescription).collect(Collectors.toList());
            ObservableList<String> intnames = FXCollections.observableList(names);
            InterfaceChooser.setItems(intnames);
            InterfaceChooser.setValue("Select an Interface");
        } catch (PcapNativeException e) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Pcap Error Occurred");
            alert.setHeaderText("Pcap Error Detected");
            alert.setContentText("Ooops, there is an error with Pcap. The agent depends on Pcap to capture Packets! Please correct the problem before continuing!");
            alert.showAndWait();
        }
    }
    @FXML
    public void populateInformation(ActionEvent event) {
        PcapNetworkInterface device = devices.get(InterfaceChooser.getSelectionModel().getSelectedIndex());
        List<PcapAddress> interfaceAddresses = device.getAddresses();
        InterfaceName.setText(device.getName());
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

    }
}

