import Model.*;
import com.jfoenix.controls.*;
import com.jfoenix.controls.datamodels.treetable.RecursiveTreeObject;
import com.jfoenix.transitions.hamburger.HamburgerBackArrowBasicTransition;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeTableCell;
import javafx.scene.control.TreeTableColumn;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.util.Callback;
import org.pcap4j.core.PcapNetworkInterface;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;

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

    public static AnchorPane rootP;
    @FXML
    private JFXButton homeButton;
    private Scene myScene;
    private PcapNetworkInterface device;
    private NetworkCapture capture;
    private ScheduledThreadPoolExecutorHandler handler;
    private boolean ARPDetection;
    private Integer threshold;
    private AWSSMS SMSHandler;
    private OutlookEmail EmailHandler;
    private JFXTreeTableView<Alert> alertsView;
    private JFXTreeTableColumn<Alert, String> typeColumn;
    private JFXTreeTableColumn<Alert, String> tdColumn;
    private JFXTreeTableColumn<Alert, String> gtColumn;
    private TreeItem<Alert> root;
    private ObservableList<Alert> OLalerts;


    public void passVariables(PcapNetworkInterface nif, ScheduledThreadPoolExecutorHandler handler, NetworkCapture capture, boolean ARPDetection, Integer threshold, AWSSMS SMSHandler, OutlookEmail EmailHandler) {
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
        alertsView.getColumns().setAll(typeColumn, tdColumn, gtColumn);
        groupToggle.selectedProperty().addListener((observable, oldValue, newValue) -> {
            if (groupToggle.isSelected())
                ScheduledThreadPoolExecutorHandler.getService().execute(() -> alertsView.group(typeColumn));
            else
                ScheduledThreadPoolExecutorHandler.getService().execute(() -> alertsView.unGroup(typeColumn));
        });
        alertCount.setText("Suspicious Events Count: " + capture.getEvents());
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
        gtColumn = new JFXTreeTableColumn<>("Go to Alert Dashboard");
        gtColumn.setPrefWidth(300);
        Callback<TreeTableColumn<Alert, String>, TreeTableCell<Alert, String>> cellFactory
                =
                new Callback<TreeTableColumn<Alert, String>, TreeTableCell<Alert, String>>() {
                    @Override
                    public TreeTableCell call(final TreeTableColumn<Alert, String> param) {
                        final TreeTableCell<Alert, String> cell = new TreeTableCell<Alert, String>() {
                            JFXButton btn = new JFXButton();
                            @Override
                            public void updateItem(String item, boolean empty) {
                                super.updateItem(item, empty);
                                if (empty) {
                                    setGraphic(null);
                                    setText(null);
                                } else {
                                    System.out.println(item);
                                    int selectIndex = getTreeTableRow().getIndex();
                                    System.out.println(selectIndex);
                                    try {
                                        Alert rItem = alertsView.getSelectionModel().getModelItem(selectIndex).getValue();
                                        btn.setText("Go to Alert Dashboard");
                                        btn.setOnAction(event -> {
                                            FXMLLoader loader = new FXMLLoader(getClass().getResource("CAAlertDashboard.fxml"));
                                            myScene = ((Node) event.getSource()).getScene();
                                            Stage stage = (Stage) (myScene).getWindow();
                                            Parent nextView = null;
                                            try {
                                                nextView = loader.load();
                                                ControllerCAAlertDashboard controller = loader.getController();
                                                controller.passVariables(device, handler, capture, ARPDetection, threshold, SMSHandler, rItem.getPcapPath(), EmailHandler);
                                            } catch (IOException e) {
                                                e.printStackTrace();
                                            }
                                            stage.setScene(new Scene(nextView));
                                            stage.setTitle("Alert Dashboard");
                                            stage.show();
                                        });
                                        btn.setId("tableJFXButton");
                                        btn.getStylesheets().add("Style.css");
                                        setGraphic(btn);
                                        setText(null);
                                    } catch (ClassCastException e) {
                                        alertsView.refresh();
                                        return;
                                    } catch (NullPointerException e1) {
                                        alertsView.refresh();
                                        return;
                                    }
                                }
                            }
                        };
                        return cell;
                    }
                };

        gtColumn.setCellFactory(cellFactory);
    }

    @FXML
    public void onClickHomeButton(ActionEvent event) throws IOException {
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
