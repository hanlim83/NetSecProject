import Model.*;
import com.jfoenix.controls.*;
import com.jfoenix.controls.datamodels.treetable.RecursiveTreeObject;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeTableColumn;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.StackPane;
import org.pcap4j.core.PcapNetworkInterface;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class ControllerCAAlerts implements Initializable {

    @FXML
    private AnchorPane anchorPane;

    @FXML
    private JFXHamburger hamburger;

    @FXML
    private Label ipAddr;

    @FXML
    private StackPane stackpane;

    @FXML
    private Label alertCount;

    @FXML
    private JFXDrawer drawer;

    @FXML
    private JFXToggleButton groupToggle;

    private PcapNetworkInterface device;
    private NetworkCapture capture;
    private ExecutorServiceHandler handler;
    private boolean ARPDetection;
    private Integer threshold;
    private AWSSMS SMSHandler;
    private OutlookEmail EmailHandler;
    private JFXTreeTableView<Alert> alertsView;
    private JFXTreeTableColumn<Alert, String> typeColumn;
    private JFXTreeTableColumn<Alert, String> tdColumn;
    private TreeItem<Alert> root;
    private ObservableList<Alert> OLalerts;


    public void passVariables(PcapNetworkInterface nif, ExecutorServiceHandler handler, NetworkCapture capture, boolean ARPDetection, Integer threshold, AWSSMS SMSHandler, OutlookEmail EmailHandler) {
        this.device = nif;
        this.handler = handler;
        this.capture = capture;
        this.ARPDetection = ARPDetection;
        this.threshold = threshold;
        this.SMSHandler = SMSHandler;
        this.EmailHandler = EmailHandler;
        OLalerts = FXCollections.observableArrayList(this.capture.alerts);
        root = new RecursiveTreeItem<Alert>(OLalerts, RecursiveTreeObject::getChildren);
        alertsView = new JFXTreeTableView<Alert>(root);
        alertsView.setEditable(false);
        alertsView.setShowRoot(false);
        stackpane.getChildren().add(alertsView);
        alertsView.getColumns().setAll(typeColumn, tdColumn);
        groupToggle.selectedProperty().addListener((observable, oldValue, newValue) -> {
            if (groupToggle.isSelected())
                ExecutorServiceHandler.getService().execute(() -> alertsView.group(typeColumn));
            else
                ExecutorServiceHandler.getService().execute(() -> alertsView.unGroup(typeColumn));
        });
        try {
            FXMLLoader loader = new FXMLLoader();
            loader.load(getClass().getResource("AdminSideTab.fxml").openStream());
            ControllerAdminSideTab ctrl = loader.getController();
            ctrl.getVariables(device, handler, capture, ARPDetection, threshold, SMSHandler, EmailHandler);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        typeColumn = new JFXTreeTableColumn<>("Alert Type");
        typeColumn.setPrefWidth(300);
        typeColumn.setCellValueFactory((TreeTableColumn.CellDataFeatures<Alert, String> param) -> {
            if (typeColumn.validateValue(param)) {
                return param.getValue().getValue().typeProperty();
            } else {
                return typeColumn.getComputedValue(param);
            }
        });
        tdColumn = new JFXTreeTableColumn<>("Time & Date of Alert Generated");
        tdColumn.setPrefWidth(300);
        tdColumn.setCellValueFactory((TreeTableColumn.CellDataFeatures<Alert, String> param) -> {
            if (tdColumn.validateValue(param)) {
                return param.getValue().getValue().dateTimeProperty();
            } else {
                return tdColumn.getComputedValue(param);
            }
        });
    }
}
