import Database.admin_DB;
import Model.*;
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
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Modality;
import javafx.stage.Stage;
import org.pcap4j.core.PcapNetworkInterface;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.UnknownHostException;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.ConcurrentModificationException;
import java.util.ResourceBundle;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

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
    @FXML
    private Label ipAddr;
    @FXML
    private JFXButton homeButton;

    private PcapNetworkInterface device;
    private Scene myScene;
    private ArrayList<CapturedPacket> packets;
    private ObservableList<CapturedPacket> OLpackets;
    private NetworkCapture capture;
    private ScheduledThreadPoolExecutorHandler handler;
    private boolean ARPDetection;
    private Integer threshold;
    private ArrayList<String> adminPN;
    private ArrayList<String> adminEA;
    private OAuth2LoginAdmin oAuth2Login;
    private admin_DB db;
    private AWSSMS SMSHandler;
    private OutlookEmail EmailHandler;
    private tableview tRunnable;
    private capture cRunnable;
    private sendEmailF sendFull;
    private createTPS createTPS;
    private boolean lookup;

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
        tRunnable = new tableview();
        cRunnable = new capture();
        sendFull = new sendEmailF();
        createTPS = new createTPS();
    }

    public void passVariables(PcapNetworkInterface nif, ScheduledThreadPoolExecutorHandler handler, NetworkCapture capture, boolean ARPDetection, Integer threshold, AWSSMS USMSHandler, OutlookEmail OEmailHandler) {
        this.device = nif;
        this.handler = handler;
        this.ARPDetection = ARPDetection;
        this.threshold = threshold;
        this.SMSHandler = USMSHandler;
        this.EmailHandler = OEmailHandler;
        try {
            FXMLLoader loader = new FXMLLoader();
            loader.load(getClass().getResource("AdminSideTab.fxml").openStream());
            ControllerAdminSideTab ctrl = loader.getController();
            lookup = ctrl.checktokenFileExists();
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (threshold != 0 && SMSHandler == null && EmailHandler == null) {
            ScheduledThreadPoolExecutorHandler.getService().execute(() -> {
                try {
                    adminPN = db.getAllPhoneNo();
                    SMSHandler = new AWSSMS(adminPN);
                    for (String s : adminPN) {
                        System.out.println(s);
                    }
                    System.out.println("SMS Created");
                    adminEA = db.getAllEmail();
                    for (String s : adminEA) {
                        System.out.println(s);
                    }
                    EmailHandler = new OutlookEmail(adminEA);
                    System.out.println("Email created");
                    oAuth2Login = new OAuth2LoginAdmin();
                    oAuth2Login.login();
                    EmailHandler.setReceiverAddress(oAuth2Login.getEmail());
                    System.out.println(oAuth2Login.getEmail());
                    System.out.println("Added own email");
                    Platform.runLater(() -> {
                        spinner.setVisible(false);
                        captureToggle.setDisable(false);
                        hamburger.setDisable(false);
                    });
                } catch (SQLException e) {
                    try {
                        oAuth2Login = new OAuth2LoginAdmin();
                        oAuth2Login.login();
                        System.out.println(oAuth2Login.getEmail());
                        EmailHandler = new OutlookEmail(oAuth2Login.getEmail());
                        System.out.println("Email Created");
                    } catch (Exception e1) {
                        e1.printStackTrace();
                    }
                    Platform.runLater(() -> {
                        spinner.setVisible(false);
                        myScene = anchorPane.getScene();
                        Stage stage = (Stage) (myScene).getWindow();
                        String title = "Certain Functions are not available";
                        String content = "FireE is currently unable to retrieve the phone numbers and email addresses that the alerts will be sent to. SMS and email broadcast alerts will not be available. You will still be able to receive Pcap Files";
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
                        alert.showAndWait();
                        captureToggle.setDisable(false);
                        hamburger.setDisable(false);
                    });
                } catch (UnknownHostException e) {
                    e.printStackTrace();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                try {
                    FXMLLoader loader = new FXMLLoader();
                    loader.load(getClass().getResource("AdminSideTab.fxml").openStream());
                    ControllerAdminSideTab ctrl = loader.getController();
                    ctrl.getVariables(device, handler, capture, ARPDetection, threshold, SMSHandler, EmailHandler);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });
        } else if (threshold != 0) {
            this.SMSHandler = USMSHandler;
            ScheduledThreadPoolExecutorHandler.getService().execute(() -> {
                try {
                    adminPN = db.getAllPhoneNo();
                    SMSHandler.setAdminPN(adminPN);
                    for (String s : adminPN) {
                        System.out.println(s);
                    }
                    System.out.println("SMS Updated!");
                    adminEA = db.getAllEmail();
                    for (String s : adminEA) {
                        System.out.println(s);
                    }
                    EmailHandler.setAdminEmailAddresses(adminEA);
                    System.out.println("Email Updated!");
                    Platform.runLater(() -> {
                        spinner.setVisible(false);
                        captureToggle.setDisable(false);
                        hamburger.setDisable(false);
                    });
                } catch (SQLException e) {
                    System.err.println("SQL Error");
                    Platform.runLater(() -> {
                        spinner.setVisible(false);
                        captureToggle.setDisable(false);
                        hamburger.setDisable(false);
                    });
                }
            });
        } else {
            ScheduledThreadPoolExecutorHandler.getService().execute(() -> {
                if (EmailHandler == null) {
                    try {
                        oAuth2Login = new OAuth2LoginAdmin();
                        oAuth2Login.login();
                        EmailHandler = new OutlookEmail(oAuth2Login.getEmail());
                        System.out.println(oAuth2Login.getEmail());
                        System.out.println("Email Created");
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else {
                    try {
                        if (lookup != false) {
                            oAuth2Login = new OAuth2LoginAdmin();
                            oAuth2Login.login();
                            EmailHandler.setReceiverAddress(oAuth2Login.getEmail());
                            System.out.println(oAuth2Login.getEmail());
                            System.out.println("Email Updated");
                        } else
                            System.out.println("Token not found");

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                Platform.runLater(() -> {
                    spinner.setVisible(false);
                    captureToggle.setDisable(false);
                    hamburger.setDisable(false);
                });
            });
        }
        if (capture == null)
            clearCaptureBtn.setDisable(true);
        else if (capture.isRunning()) {
            this.capture = capture;
            captureToggle.setSelected(true);
            handler.setTableviewRunnable(ScheduledThreadPoolExecutorHandler.getService().scheduleAtFixedRate(tRunnable, 2, 1, TimeUnit.SECONDS));

        } else if (capture != null) {
            this.capture = capture;
            clearCaptureBtn.setDisable(false);
            packets = capture.packets;
            OLpackets = FXCollections.observableArrayList(packets);
            packetstable.setItems(OLpackets);
            packetstable.refresh();
        }
        if (this.capture != null)
            alertCount.setText("Suspicious Events Count: " + Integer.toString(this.capture.getEvents()));
        else
            alertCount.setText("Suspicious Events Count: 0");
        try {
            FXMLLoader loader = new FXMLLoader();
            loader.load(getClass().getResource("AdminSideTab.fxml").openStream());
            ControllerAdminSideTab ctrl = loader.getController();
            ctrl.getVariables(device, handler, capture, ARPDetection, threshold, SMSHandler, EmailHandler);
            ctrl.checktokenFileExists();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void startCapturing() {
        if (capture == null)
            capture = new NetworkCapture(device, threshold);
        handler.setTableviewRunnable(ScheduledThreadPoolExecutorHandler.getService().scheduleAtFixedRate(tRunnable, 2, 1, TimeUnit.SECONDS));
        if (!capture.isRunning())
            ScheduledThreadPoolExecutorHandler.getService().execute(cRunnable);
        clearCaptureBtn.setDisable(true);
        if (handler.getStatusbackgroundRunnable()) {
            handler.cancelbackgroundRunnable();
            handler.setbackgroundRunnable(ScheduledThreadPoolExecutorHandler.getService().scheduleAtFixedRate(createTPS, 2, 4, TimeUnit.SECONDS));
        } else {
            handler.setbackgroundRunnable(ScheduledThreadPoolExecutorHandler.getService().scheduleAtFixedRate(createTPS, 2, 4, TimeUnit.SECONDS));
        }
        try {
            FXMLLoader loader = new FXMLLoader();
            loader.load(getClass().getResource("AdminSideTab.fxml").openStream());
            ControllerAdminSideTab ctrl = loader.getController();
            ctrl.getVariables(this.device, this.handler, this.capture, ARPDetection, threshold, SMSHandler, EmailHandler);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void stopCapturing() {
        capture.stopSniffing();
        handler.cancelTableviewRunnable();
        handler.cancelbackgroundRunnable();
        //Refresh TableView for last time
        packets = capture.packets;
        OLpackets = FXCollections.observableArrayList(packets);
        packetstable.setItems(OLpackets);
        packetstable.refresh();
        capture.Generalexport();
        ScheduledThreadPoolExecutorHandler.getService().execute(sendFull);
        //Alert below
        myScene = anchorPane.getScene();
        Stage stage = (Stage) (myScene).getWindow();
        String title = "Packet Capturing Summary";
        String content;
        if (com.sun.jna.Platform.isWindows())
            content = "Packets Received By Interface: " + capture.getPacketsReceived() + "\nPackets Dropped By Interface: " + capture.getPacketsDroppedByInt() + "\nTotal Packets Captured: " + capture.getPacketsCaptured() + "\nThe Network Capture File will be sent to your email shortly";
        else
            content = "Packets Received By Interface: " + capture.getPacketsReceived() + "\nPackets Dropped By Interface: " + capture.getPacketsDroppedByInt() + "\nTotal Packets Captured: " + capture.getPacketCount() + "\nThe Network Capture File will be sent to your email shortly";
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
            ctrl.getVariables(this.device, this.handler, this.capture, ARPDetection, threshold, SMSHandler, EmailHandler);
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
                    controller.passVariables(device, handler, capture, selected, ARPDetection, threshold, SMSHandler, EmailHandler);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                stage.setScene(new Scene(nextView));
                stage.setTitle("Detailed Packet View");
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
                ctrl.getVariables(this.device, this.handler, this.capture, ARPDetection, threshold, SMSHandler, EmailHandler);
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
                controller.passVariables(handler, null, false, 0, SMSHandler, null, true, EmailHandler);
            } catch (IOException e) {
                e.printStackTrace();
            }
            stage.setScene(new Scene(nextView));
            stage.setTitle("Select Interface");
            alert.close();
            stage.show();
        });
        alert.showAndWait();
    }

    private class tableview implements Runnable {
        @Override
        public void run() {
            try {
                if (capture.checkThreshold()) {
                    SMSHandler.sendAlert();
                    capture.Specficexport();
                    String pcapFilePath = capture.getSpecificPcapExportPath();
                    EmailHandler.sendParitalPcap(pcapFilePath);
                    capture.addAlert(false);
                    Platform.runLater(new Runnable() {
                        @Override
                        public void run() {
                            myScene = anchorPane.getScene();
                            Stage stage = (Stage) (myScene).getWindow();
                            String title = "Suspicious Network Event Detected!";
                            String content = "A Suspicious network event has been detected! Current network traffic has exceeded the threshold. A pcap file containing packets before the event has been generated for you and will be sent shortly via email.";
                            JFXButton close = new JFXButton("Close");
                            close.setButtonType(JFXButton.ButtonType.RAISED);
                            close.setStyle("-fx-background-color: #00bfff;");
                            JFXButton show = new JFXButton("Show Alert Dashboard");
                            show.setButtonType(JFXButton.ButtonType.RAISED);
                            show.setStyle("-fx-background-color: #ff90bb;");
                            JFXDialogLayout layout = new JFXDialogLayout();
                            layout.setHeading(new Label(title));
                            layout.setBody(new Label(content));
                            layout.setActions(show, close);
                            JFXAlert<Void> alert = new JFXAlert<>(stage);
                            alert.setOverlayClose(true);
                            alert.setAnimation(JFXAlertAnimation.CENTER_ANIMATION);
                            alert.setContent(layout);
                            alert.initModality(Modality.NONE);
                            close.setOnAction(__ -> alert.hideWithAnimation());
                            show.setOnAction(new EventHandler<ActionEvent>() {
                                @Override
                                public void handle(ActionEvent event) {
                                    handler.cancelTableviewRunnable();
                                    FXMLLoader loader = new FXMLLoader(getClass().getResource("CAMainAlertDashboard.fxml"));
                                    myScene = ((Node) event.getSource()).getScene();
                                    Parent nextView = null;
                                    try {
                                        nextView = loader.load();
                                        ControllerCAMainAlertDashboard controller = loader.getController();
                                        controller.passVariables(device, handler, capture, ARPDetection, threshold, SMSHandler, capture.getSpecificPcapExportPath(), false, EmailHandler);
                                    } catch (IOException e) {
                                        e.printStackTrace();
                                    }
                                    stage.setScene(new Scene(nextView));
                                    stage.setTitle("Alert Dashboard");
                                    alert.close();
                                    stage.show();
                                }
                            });
                            alert.show();
                        }
                    });
                } else if (capture.checkARP() && ARPDetection != false) {
                    SMSHandler.sendAlert();
                    capture.Specficexport();
                    String pcapFilePath = capture.getSpecificPcapExportPath();
                    EmailHandler.sendParitalPcap(pcapFilePath);
                    capture.addAlert(true);
                    Platform.runLater(new Runnable() {
                        @Override
                        public void run() {
                            myScene = anchorPane.getScene();
                            Stage stage = (Stage) (myScene).getWindow();
                            String title = "Suspicious Network Event Detected!";
                            String content = "A Suspicious network event has been detected! ARP spoofing has been detected. A pcap file containing packets before the event has been generated for you and will be sent shortly via email.";
                            JFXButton close = new JFXButton("Close");
                            close.setButtonType(JFXButton.ButtonType.RAISED);
                            close.setStyle("-fx-background-color: #00bfff;");
                            JFXButton show = new JFXButton("Show Alert Dashboard");
                            show.setButtonType(JFXButton.ButtonType.RAISED);
                            show.setStyle("-fx-background-color: #ff90bb;");
                            JFXDialogLayout layout = new JFXDialogLayout();
                            layout.setHeading(new Label(title));
                            layout.setBody(new Label(content));
                            layout.setActions(show, close);
                            JFXAlert<Void> alert = new JFXAlert<>(stage);
                            alert.setOverlayClose(true);
                            alert.setAnimation(JFXAlertAnimation.CENTER_ANIMATION);
                            alert.setContent(layout);
                            alert.initModality(Modality.NONE);
                            close.setOnAction(__ -> alert.hideWithAnimation());
                            show.setOnAction(new EventHandler<ActionEvent>() {
                                @Override
                                public void handle(ActionEvent event) {
                                    handler.cancelTableviewRunnable();
                                    FXMLLoader loader = new FXMLLoader(getClass().getResource("CAMainAlertDashboard.fxml"));
                                    myScene = ((Node) event.getSource()).getScene();
                                    Parent nextView = null;
                                    try {
                                        nextView = loader.load();
                                        ControllerCAMainAlertDashboard controller = loader.getController();
                                        controller.passVariables(device, handler, capture, ARPDetection, threshold, SMSHandler, capture.getSpecificPcapExportPath(), false, EmailHandler);
                                    } catch (IOException e) {
                                        e.printStackTrace();
                                    }
                                    stage.setScene(new Scene(nextView));
                                    stage.setTitle("Alert Dashboard");
                                    alert.close();
                                    stage.show();
                                }
                            });
                            alert.show();
                        }
                    });
                }
                packets = capture.packets;
                OLpackets = FXCollections.observableArrayList(packets);
                Platform.runLater(new Runnable() {
                    @Override
                    public void run() {
                        packetstable.setItems(OLpackets);
                        packetstable.refresh();
                        alertCount.setText("Suspicious Events Count: " + Integer.toString(capture.getEvents()));
                    }
                });
            } catch (ConcurrentModificationException e) {
                try {
                    System.err.println("ConcurrentModification Detected");
                    capture.stopSniffing();
                    if (capture.checkThreshold()) {
                        SMSHandler.sendAlert();
                        capture.Specficexport();
                        String pcapFilePath = capture.getSpecificPcapExportPath();
                        EmailHandler.sendParitalPcap(pcapFilePath);
                        capture.addAlert(false);
                        Platform.runLater(new Runnable() {
                            @Override
                            public void run() {
                                myScene = anchorPane.getScene();
                                Stage stage = (Stage) (myScene).getWindow();
                                String title = "Suspicious Network Event Detected!";
                                String content = "A Suspicious network event has been detected! Current network traffic has exceeded the threshold. A pcap file containing packets before the event has been generated for you and will be sent shortly via email.";
                                JFXButton close = new JFXButton("Close");
                                close.setButtonType(JFXButton.ButtonType.RAISED);
                                close.setStyle("-fx-background-color: #00bfff;");
                                JFXButton show = new JFXButton("Show Alert Dashboard");
                                show.setButtonType(JFXButton.ButtonType.RAISED);
                                show.setStyle("-fx-background-color: #ff90bb;");
                                JFXDialogLayout layout = new JFXDialogLayout();
                                layout.setHeading(new Label(title));
                                layout.setBody(new Label(content));
                                layout.setActions(show, close);
                                JFXAlert<Void> alert = new JFXAlert<>(stage);
                                alert.setOverlayClose(true);
                                alert.setAnimation(JFXAlertAnimation.CENTER_ANIMATION);
                                alert.setContent(layout);
                                alert.initModality(Modality.NONE);
                                close.setOnAction(__ -> alert.hideWithAnimation());
                                show.setOnAction(new EventHandler<ActionEvent>() {
                                    @Override
                                    public void handle(ActionEvent event) {
                                        handler.cancelTableviewRunnable();
                                        FXMLLoader loader = new FXMLLoader(getClass().getResource("CAMainAlertDashboard.fxml"));
                                        myScene = ((Node) event.getSource()).getScene();
                                        Parent nextView = null;
                                        try {
                                            nextView = loader.load();
                                            ControllerCAMainAlertDashboard controller = loader.getController();
                                            controller.passVariables(device, handler, capture, ARPDetection, threshold, SMSHandler, capture.getSpecificPcapExportPath(), false, EmailHandler);
                                        } catch (IOException e) {
                                            e.printStackTrace();
                                        }
                                        stage.setScene(new Scene(nextView));
                                        stage.setTitle("Alert Dashboard");
                                        alert.close();
                                        stage.show();
                                    }
                                });
                                alert.show();
                            }
                        });
                    } else if (capture.checkARP() && ARPDetection != false) {
                        SMSHandler.sendAlert();
                        capture.Specficexport();
                        String pcapFilePath = capture.getSpecificPcapExportPath();
                        EmailHandler.sendParitalPcap(pcapFilePath);
                        capture.addAlert(true);
                        Platform.runLater(new Runnable() {
                            @Override
                            public void run() {
                                myScene = anchorPane.getScene();
                                Stage stage = (Stage) (myScene).getWindow();
                                String title = "Suspicious Network Event Detected!";
                                String content = "A Suspicious network event has been detected! ARP spoofing has been detected. A pcap file containing packets before the event has been generated for you and will be sent shortly via email.";
                                JFXButton close = new JFXButton("Close");
                                close.setButtonType(JFXButton.ButtonType.RAISED);
                                close.setStyle("-fx-background-color: #00bfff;");
                                JFXButton show = new JFXButton("Show Alert Dashboard");
                                show.setButtonType(JFXButton.ButtonType.RAISED);
                                show.setStyle("-fx-background-color: #ff90bb;");
                                JFXDialogLayout layout = new JFXDialogLayout();
                                layout.setHeading(new Label(title));
                                layout.setBody(new Label(content));
                                layout.setActions(show, close);
                                JFXAlert<Void> alert = new JFXAlert<>(stage);
                                alert.setOverlayClose(true);
                                alert.setAnimation(JFXAlertAnimation.CENTER_ANIMATION);
                                alert.setContent(layout);
                                alert.initModality(Modality.NONE);
                                close.setOnAction(__ -> alert.hideWithAnimation());
                                show.setOnAction(new EventHandler<ActionEvent>() {
                                    @Override
                                    public void handle(ActionEvent event) {
                                        handler.cancelTableviewRunnable();
                                        FXMLLoader loader = new FXMLLoader(getClass().getResource("CAMainAlertDashboard.fxml"));
                                        myScene = ((Node) event.getSource()).getScene();
                                        Parent nextView = null;
                                        try {
                                            nextView = loader.load();
                                            ControllerCAMainAlertDashboard controller = loader.getController();
                                            controller.passVariables(device, handler, capture, ARPDetection, threshold, SMSHandler, capture.getSpecificPcapExportPath(), false, EmailHandler);
                                        } catch (IOException e) {
                                            e.printStackTrace();
                                        }
                                        stage.setScene(new Scene(nextView));
                                        stage.setTitle("Alert Dashboard");
                                        alert.close();
                                        stage.show();
                                    }
                                });
                                alert.show();
                            }
                        });
                    }
                    ScheduledThreadPoolExecutorHandler.getService().execute(cRunnable);
                    packets = capture.packets;
                    OLpackets = FXCollections.observableArrayList(packets);
                    Platform.runLater(() -> {
                        packetstable.setItems(OLpackets);
                        packetstable.refresh();
                        alertCount.setText("Suspicious Events Count: " + Integer.toString(capture.getEvents()));
                    });
                } catch (Exception e1) {
                    e1.printStackTrace();
                }
            }
        }
    }

    private class capture implements Runnable {
        @Override
        public void run() {
            capture.startSniffing();
        }
    }

    private class createTPS implements Runnable {
        @Override
        public void run() {
            capture.getTrafficPerSecond();
        }
    }

    private class sendEmailF implements Runnable {
        @Override
        public void run() {
            String pcapFilePath = capture.getFullPcapExportPath();
            EmailHandler.sendFullPcap(pcapFilePath);
        }
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
}
