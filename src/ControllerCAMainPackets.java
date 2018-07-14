import Database.admin_DB;
import Model.CapturedPacket;
import Model.NetworkCapture;
import Model.SMS;
import Model.ScheduledExecutorServiceHandler;
import com.jfoenix.animation.alert.JFXAlertAnimation;
import com.jfoenix.controls.*;
import com.jfoenix.transitions.hamburger.HamburgerBackArrowBasicTransition;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import org.pcap4j.core.PcapNetworkInterface;

import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.ResourceBundle;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

import static java.util.concurrent.TimeUnit.SECONDS;

public class ControllerCAMainPackets implements Initializable {

    public static AnchorPane rootP;
    @FXML
    private AnchorPane anchorPane;
    @FXML
    private JFXHamburger hamburger;
    @FXML
    private JFXDrawer drawer;
    @FXML
    private TableView packetstable;
    @FXML
    private TableColumn<CapturedPacket, Integer> tableColPN;
    @FXML
    private TableColumn<CapturedPacket, String> tableColSrcIP;
    @FXML
    private TableColumn<CapturedPacket, Integer> tableColSrcPort;
    @FXML
    private TableColumn<CapturedPacket, String> tableColDstIP;
    @FXML
    private TableColumn<CapturedPacket, Integer> tableColDstPort;
    @FXML
    private TableColumn<CapturedPacket, String> tableColProtocol;
    @FXML
    private TableColumn<CapturedPacket, Integer> tableColLength;
    @FXML
    private TableColumn<CapturedPacket, String> tableColInfo;
    @FXML
    private JFXToggleButton captureToggle;
    @FXML
    private JFXButton clearCaptureBtn;
    @FXML
    private Label alertCount;
    @FXML
    private JFXSpinner spinner;

    private PcapNetworkInterface device;
    private Scene myScene;
    private ArrayList<CapturedPacket> packets;
    private ObservableList<CapturedPacket> OLpackets;
    private NetworkCapture capture;
    private ScheduledExecutorServiceHandler handler;
    private String directoryPath;
    private Integer threshold;
    private ArrayList<String> adminPN;
    private admin_DB db;
    private SMS SMSHandler;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        hamburgerBar();
        tableColPN.setCellValueFactory(new PropertyValueFactory<CapturedPacket, Integer>("number"));
        tableColSrcIP.setCellValueFactory(new PropertyValueFactory<CapturedPacket, String>("srcIP"));
        tableColSrcPort.setCellValueFactory(new PropertyValueFactory<CapturedPacket, Integer>("srcPort"));
        tableColDstIP.setCellValueFactory(new PropertyValueFactory<CapturedPacket, String>("destIP"));
        tableColDstPort.setCellValueFactory(new PropertyValueFactory<CapturedPacket, Integer>("dstPort"));
        tableColProtocol.setCellValueFactory(new PropertyValueFactory<CapturedPacket, String>("Protocol"));
        tableColLength.setCellValueFactory(new PropertyValueFactory<CapturedPacket, Integer>("length"));
        tableColInfo.setCellValueFactory(new PropertyValueFactory<CapturedPacket, String>("information"));
        captureToggle.selectedProperty().addListener(new ChangeListener<Boolean>() {
            @Override
            public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
                if (captureToggle.isSelected()) {
                    startCapturing();
                } else {
                    stopCapturing();
                }
            }
        });
        db = new admin_DB();
        hamburger.setDisable(true);
        captureToggle.setDisable(true);
    }

    public void passVariables(PcapNetworkInterface nif, ScheduledExecutorServiceHandler handler, NetworkCapture capture, String directoryPath, Integer threshold, SMS USMSHandler) {
        this.device = nif;
        this.handler = handler;
        this.directoryPath = directoryPath;
        this.threshold = threshold;
        this.SMSHandler = USMSHandler;
        if (SMSHandler == null) {
            handler.setgetSQLRunnable(ScheduledExecutorServiceHandler.getService().schedule(new Runnable() {
                @Override
                public void run() {
                    try {
                        adminPN = db.getAllPhoneNo();
                        SMSHandler = new SMS(adminPN);
                        for (String s : adminPN) {
                            System.out.println(s);
                        }
                        System.out.println("SMS Created");
                        Platform.runLater(new Runnable() {
                            @Override
                            public void run() {
                                spinner.setVisible(false);
                                captureToggle.setDisable(false);
                                hamburger.setDisable(false);
                            }
                        });
                    } catch (SQLException e) {
                        System.err.println("SQL Error");
                        handler.setalertsNotAvailRunnable(ScheduledExecutorServiceHandler.getService().schedule(new Runnable() {
                    @Override
                    public void run() {
                        Platform.runLater(new Runnable() {
                            @Override
                            public void run() {
                                spinner.setVisible(false);
                                /*myScene = anchorPane.getScene();
                                Stage stage = (Stage) (myScene).getWindow();
                                String title = "SMS Alerts are not available";
                                String content = "FireE is currently unable to retrieve the phone numbers that the SMS alerts will be sent to. SMS alerts will not be available";
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
                                alert.showAndWait();*/
                                captureToggle.setDisable(false);
                                hamburger.setDisable(false);
                            }
                        });
                    }
                        }, 1, TimeUnit.SECONDS));
                    }
                    try {
                        FXMLLoader loader = new FXMLLoader();
                        loader.load(getClass().getResource("AdminSideTab.fxml").openStream());
                        ControllerAdminSideTab ctrl = loader.getController();
                        ctrl.getVariables(device, handler, capture, directoryPath, threshold, SMSHandler);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }, 2, TimeUnit.SECONDS));
        } else {
            this.SMSHandler = SMSHandler;
            handler.setgetSQLRunnable(ScheduledExecutorServiceHandler.getService().schedule(new Runnable() {
                @Override
                public void run() {
                    try {
                        adminPN = db.getAllPhoneNo();
                        SMSHandler.setAdminPN(adminPN);
                        for (String s : adminPN) {
                            System.out.println(s);
                        }
                        System.out.println("SMS Updated!");
                        Platform.runLater(new Runnable() {
                            @Override
                            public void run() {
                                spinner.setVisible(false);
                                captureToggle.setDisable(false);
                                hamburger.setDisable(false);
                            }
                        });
                    } catch (SQLException e) {
                        System.err.println("SQL Error");
                        Platform.runLater(new Runnable() {
                            @Override
                            public void run() {
                                spinner.setVisible(false);
                                captureToggle.setDisable(false);
                                hamburger.setDisable(false);
                            }
                        });
                    }
                    try {
                        FXMLLoader loader = new FXMLLoader();
                        loader.load(getClass().getResource("AdminSideTab.fxml").openStream());
                        ControllerAdminSideTab ctrl = loader.getController();
                        ctrl.getVariables(device, handler, capture, directoryPath, threshold, SMSHandler);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }, 1, TimeUnit.SECONDS));
        }
        if (capture == null)
            clearCaptureBtn.setDisable(true);
        else if (capture.isRunning()) {
            this.capture = capture;
            captureToggle.setSelected(true);
            handler.setTableviewRunnable(ScheduledExecutorServiceHandler.getService().scheduleAtFixedRate(new Runnable() {
                @Override
                public void run() {
                    if (capture.checkThreshold()) {
                        capture.Specficexport();
                        SMSHandler.sendAlert();
                    }
                    Platform.runLater(new Runnable() {
                        @Override
                        public void run() {
                            packets = capture.packets;
                            OLpackets = FXCollections.observableArrayList(packets);
                            packetstable.setItems(OLpackets);
                            packetstable.refresh();
                            alertCount.setText("Suspicious Events Count: " + Integer.toString(capture.getEvents()));
                        }
                    });
                }
            }, 2, 1, TimeUnit.SECONDS));

        } else if (capture != null) {
            this.capture = capture;
            clearCaptureBtn.setDisable(false);
            packets = capture.packets;
            OLpackets = FXCollections.observableArrayList(packets);
            packetstable.setItems(OLpackets);
            packetstable.refresh();
        }
    }

    public void startCapturing() {
        if (capture == null)
            capture = new NetworkCapture(device, directoryPath, threshold);
        handler.setTableviewRunnable(ScheduledExecutorServiceHandler.getService().scheduleAtFixedRate(new Runnable() {
            @Override
            public void run() {
                if (capture.checkThreshold()) {
                    capture.Specficexport();
                    SMSHandler.sendAlert();
                }
                Platform.runLater(new Runnable() {
                    @Override
                    public void run() {
                        packets = capture.packets;
                        OLpackets = FXCollections.observableArrayList(packets);
                        packetstable.setItems(OLpackets);
                        packetstable.refresh();
                        alertCount.setText("Suspicious Events Count: " + Integer.toString(capture.getEvents()));
                    }
                });
            }
        }, 2, 1, TimeUnit.SECONDS));
        if (handler.getcaptureRunnable() == null || !handler.getStatuscaptureRunnable()) {
            handler.setcaptureRunnable(ScheduledExecutorServiceHandler.getService().schedule(new Runnable() {
                @Override
                public void run() {
                    capture.startSniffing();
                }
            }, 1, SECONDS));
        }
        clearCaptureBtn.setDisable(true);
        try {
            FXMLLoader loader = new FXMLLoader();
            loader.load(getClass().getResource("AdminSideTab.fxml").openStream());
            ControllerAdminSideTab ctrl = loader.getController();
            ctrl.getVariables(this.device, this.handler, this.capture, directoryPath, threshold, SMSHandler);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void stopCapturing() {
        capture.stopSniffing();
        handler.cancelTableviewRunnable();
        //Refresh TableView for last time
        packets = capture.packets;
        OLpackets = FXCollections.observableArrayList(packets);
        packetstable.setItems(OLpackets);
        packetstable.refresh();
        //Alert below
        myScene = anchorPane.getScene();
        Stage stage = (Stage) (myScene).getWindow();
        String title = "Packet Capturing Summary";
        String content;
        if (com.sun.jna.Platform.isWindows())
            content = "Packets Received By Interface: " + capture.getPacketsReceived() + "\nPackets Dropped: " + capture.getPacketsDropped() + "\nPackets Dropped By Interface: " + capture.getPacketsDroppedByInt() + "\nTotal Packets Captured: " + capture.getPacketsCaptured();
        else
            content = "Packets Received By Interface: " + capture.getPacketsReceived() + "\nPackets Dropped: " + capture.getPacketsDropped() + "\nPackets Dropped By Interface: " + capture.getPacketsDroppedByInt() + "\nTotal Packets Captured: " + capture.getPacketCount();
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
        clearCaptureBtn.setDisable(false);
        try {
            FXMLLoader loader = new FXMLLoader();
            loader.load(getClass().getResource("AdminSideTab.fxml").openStream());
            ControllerAdminSideTab ctrl = loader.getController();
            ctrl.getVariables(this.device, this.handler, this.capture, directoryPath, threshold, SMSHandler);
        } catch (IOException e) {
            e.printStackTrace();
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

    @FXML
    public void ShowpacketDetails(MouseEvent event) {
        if (capture == null) {

        } else if (capture.isRunning()) {

        } else {
            CapturedPacket selected = (CapturedPacket) packetstable.getSelectionModel().getSelectedItem();
            if (selected.getInformation() != null && selected.getInformation().equals("Not a Layer 2 (IP) Packet")) {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Unsupported Packet");
                alert.setHeaderText("Unsupported Packet");
                alert.setContentText("The packet you have selected does not have an IP Header and therefore details cannot be seen!");
                alert.showAndWait();
            } else {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("CADetailedPacket.fxml"));
                myScene = anchorPane.getScene();
                Stage stage = (Stage) (myScene).getWindow();
                Parent nextView = null;
                try {
                    nextView = loader.load();
                    ControllerCADetailedPacket controller = loader.getController();
                    controller.passVariables(device, handler, capture, selected, directoryPath, threshold, SMSHandler);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                stage.setScene(new Scene(nextView));
                stage.setTitle("NSPJ");
                stage.show();
            }
        }
    }

    @FXML
    public void ClearPackets(ActionEvent event) {
        myScene = anchorPane.getScene();
        Stage stage = (Stage) (myScene).getWindow();
        String title = "Confirmation Dialog";
        String content = "Do just want to clear the current capture only or clear the current capture and select a new interface?";
        JFXButton clearCapture = new JFXButton("Clear Capture Only");
        clearCapture.setButtonType(JFXButton.ButtonType.RAISED);
        clearCapture.setStyle("-fx-background-color: #00bfff;-fx-spacing: 10px,20px,10px,20px;");
        JFXButton clearCaptureAndInt = new JFXButton("Clear and choose new interface");
        clearCaptureAndInt.setButtonType(JFXButton.ButtonType.RAISED);
        clearCaptureAndInt.setStyle("-fx-background-color: #00bfff;-fx-spacing: 10px,20px,10px,20px;");
        JFXDialogLayout layout = new JFXDialogLayout();
        layout.setHeading(new Label(title));
        layout.setBody(new Label(content));
        layout.setActions(clearCapture, clearCaptureAndInt);
        JFXAlert<Void> alert = new JFXAlert<>(stage);
        alert.setOverlayClose(true);
        alert.setAnimation(JFXAlertAnimation.CENTER_ANIMATION);
        alert.setContent(layout);
        alert.initModality(Modality.APPLICATION_MODAL);
        clearCapture.setOnAction(addEvent -> {
            capture = null;
            OLpackets = null;
            packetstable.setItems(null);
            packetstable.refresh();
            clearCaptureBtn.setDisable(true);
            alertCount.setText("Suspicious Events Count: 0");
            try {
                FXMLLoader loader = new FXMLLoader();
                loader.load(getClass().getResource("AdminSideTab.fxml").openStream());
                ControllerAdminSideTab ctrl = loader.getController();
                ctrl.getVariables(this.device, this.handler, this.capture, directoryPath, threshold, SMSHandler);
            } catch (IOException e) {
                e.printStackTrace();
            }
            alert.hideWithAnimation();
        });
        clearCaptureAndInt.setOnAction(addEvent -> {
            capture = null;
            OLpackets = null;
            packetstable.setItems(null);
            packetstable.refresh();
            FXMLLoader loader = new FXMLLoader(getClass().getResource("CALandingSelectInt.fxml"));
            myScene = ((Node) event.getSource()).getScene();
            Parent nextView = null;
            try {
                nextView = loader.load();
                ControllerCALandingSelectInt controller = loader.getController();
                controller.passVariables(handler, null, null, 0, SMSHandler);
            } catch (IOException e) {
                e.printStackTrace();
            }
            stage.setScene(new Scene(nextView));
            alert.close();
            stage.show();
        });
        alert.showAndWait();
    }
}
