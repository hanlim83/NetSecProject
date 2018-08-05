import Database.admin_DB;
import Model.*;
import com.jfoenix.animation.alert.JFXAlertAnimation;
import com.jfoenix.controls.*;
import com.jfoenix.transitions.hamburger.HamburgerBackArrowBasicTransition;
import javafx.animation.AnimationTimer;
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
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.PieChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
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
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ControllerCAMainDashboard implements Initializable {
    public static AnchorPane rootP;
    @FXML
    private AnchorPane anchorPane;
    @FXML
    private JFXHamburger hamburger;
    @FXML
    private JFXToggleButton captureToggle;
    @FXML
    private JFXButton clearCaptureBtn;
    @FXML
    private LineChart<Number, Number> networkTrafficChart;
    @FXML
    private AnchorPane LineChartAnchorPane;
    @FXML
    private PieChart protocolChart;
    @FXML
    private PieChart top10IPChart;
    @FXML
    private Label alertCount;
    @FXML
    private JFXDrawer drawer;
    @FXML
    private JFXSpinner spinner;
    @FXML
    private Label ipAddr;
    @FXML
    private JFXButton homeButton;

    private static final int MAX_DATA_POINTS = 50;
    private Scene myScene;
    private PcapNetworkInterface device;
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
    private ArrayList<Integer> TPS;
    private int xSeriesData = 0;
    private XYChart.Series<Number, Number> dataSeries = new XYChart.Series<>();
    private XYChart.Series<Number, Number> thresholdSeries = new XYChart.Series<>();
    private ConcurrentLinkedQueue<Number> data = new ConcurrentLinkedQueue<>();
    private NumberAxis xAxis;
    private AnimationTimer timer;
    private charts chartRunnable;
    private capture captureRunnable;
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
        xAxis = new NumberAxis(0, MAX_DATA_POINTS, MAX_DATA_POINTS / 10);
        xAxis.setForceZeroInRange(false);
        xAxis.setAutoRanging(false);
        xAxis.setTickLabelsVisible(false);
        xAxis.setTickMarkVisible(false);
        xAxis.setMinorTickVisible(false);
        NumberAxis yAxis = new NumberAxis();
        networkTrafficChart = new LineChart<Number, Number>(xAxis, yAxis) {
            @Override
            protected void dataItemAdded(Series<Number, Number> series, int itemIndex, Data<Number, Number> item) {
            }
        };
        networkTrafficChart.setAnimated(false);
        networkTrafficChart.setTitle("Network Traffic Chart");
        networkTrafficChart.setHorizontalGridLinesVisible(true);
        dataSeries.setName("Current Network Traffic");
        networkTrafficChart.getData().add(dataSeries);
        thresholdSeries.setName("Threshold");
        networkTrafficChart.getData().add(thresholdSeries);
        networkTrafficChart.setPrefWidth(1051);
        networkTrafficChart.setPrefHeight(334);
        LineChartAnchorPane.getChildren().add(networkTrafficChart);
        chartRunnable = new charts();
        captureRunnable = new capture();
        sendFull = new sendEmailF();
        createTPS = new createTPS();
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
            clearCaptureBtn.setDisable(true);
            alertCount.setText("Suspicious Events Count: 0");
            data.clear();
            dataSeries.getData().clear();
            thresholdSeries.getData().clear();
            xSeriesData = 0;
            protocolChart.getData().clear();
            top10IPChart.getData().clear();
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
            try {
                FXMLLoader loader = new FXMLLoader();
                loader.load(getClass().getResource("AdminSideTab.fxml").openStream());
                ControllerAdminSideTab ctrl = loader.getController();
                ctrl.getVariables(this.device, this.handler, this.capture, ARPDetection, threshold, SMSHandler, EmailHandler);
            } catch (IOException e) {
                e.printStackTrace();
            }
            FXMLLoader loader = new FXMLLoader(getClass().getResource("CALandingSelectInt.fxml"));
            myScene = ((Node) event.getSource()).getScene();
            Parent nextView = null;
            try {
                nextView = loader.load();
                ControllerCALandingSelectInt controller = loader.getController();
                controller.passVariables(handler, null, false, 0, SMSHandler, null, false, EmailHandler);
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
                        close.setOnAction(__ -> alert.hideWithAnimation());
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
            addExistingTPS();
            handler.setchartDataRunnable(ScheduledThreadPoolExecutorHandler.getService().scheduleAtFixedRate(chartRunnable, 3, 6, TimeUnit.SECONDS));
            TimelineCtrl(true);
        } else if (capture != null) {
            this.capture = capture;
            addExistingTPS();
            ProtoMakeup();
            TopIPMakeup();
            clearCaptureBtn.setDisable(false);
        }
        if (this.capture != null)
            alertCount.setText("Suspicious Events Count: " + Integer.toString(this.capture.getEvents()));
        else
            alertCount.setText("Suspicious Events Count: 0");
        try {
            FXMLLoader loader = new FXMLLoader();
            loader.load(getClass().getResource("AdminSideTab.fxml").openStream());
            ControllerAdminSideTab ctrl = loader.getController();
            ctrl.getVariables(this.device, this.handler, this.capture, this.ARPDetection, this.threshold, SMSHandler, EmailHandler);
            ctrl.checktokenFileExists();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void addToQueue() {
        TPS = capture.PreviousTPS;
        try {
            data.add(TPS.get(xSeriesData));
        } catch (IndexOutOfBoundsException e) {
            data.add(TPS.get(xSeriesData - 1));
        }
    }

    public void TimelineCtrl(boolean flag) {
        if (timer == null)
            timer = new AnimationTimer() {
                @Override
                public void handle(long now) {
                    addDataToSeries();
                }
            };
        if (flag)
            timer.start();
        else
            timer.stop();
    }

    private void addDataToSeries() {
        Platform.runLater(() -> {
            for (int i = 0; i < 20; i++) { //-- add 20 numbers to the plot+
                if (data.isEmpty()) break;
                dataSeries.getData().add(new XYChart.Data<>(xSeriesData, data.remove()));
                thresholdSeries.getData().add(new XYChart.Data<>(xSeriesData++, threshold));
            }
            if (dataSeries.getData().size() > MAX_DATA_POINTS) {
                dataSeries.getData().remove(0, dataSeries.getData().size() - MAX_DATA_POINTS);
                thresholdSeries.getData().remove(0, thresholdSeries.getData().size() - MAX_DATA_POINTS);
            }
            xAxis.setLowerBound(xSeriesData - MAX_DATA_POINTS);
            xAxis.setUpperBound(xSeriesData - 1);
        });
    }

    public void ProtoMakeup() {
        int index = 0, max = 0;
        capture.ProtocolMakeup();
        ArrayList<String> protoMakeUp = capture.ProtocolMakeupProtocols;
        ArrayList<Integer> valueMakeup = capture.ProtocolMakeupData;
        if (protoMakeUp.size() != valueMakeup.size())
            return;
        max = valueMakeup.size();
        ObservableList<PieChart.Data> data = FXCollections.observableArrayList();
        for (index = 0; index < max; index++) {
            data.add(new PieChart.Data(protoMakeUp.get(index), valueMakeup.get(index)));
        }
        Platform.runLater(() -> protocolChart.setData(data));
    }

    public void TopIPMakeup() {
        capture.getTop5IP();
        ArrayList<TopIPObject> IPData = capture.Top5IPMakeup;
        if (IPData.isEmpty())
            return;
        ObservableList<PieChart.Data> data = FXCollections.observableArrayList();
        for (TopIPObject d : IPData) {
            data.add(new PieChart.Data(d.getKey(), d.getValue()));
        }
        Platform.runLater(() -> top10IPChart.setData(data));
    }

    public void addExistingTPS() {
        TPS = capture.PreviousTPS;
        for (Integer i : TPS) {
            data.add(i);
        }
        addDataToSeries();
    }

    public void startCapturing() {
        if (capture == null)
            capture = new NetworkCapture(device, threshold);
        handler.setchartDataRunnable(ScheduledThreadPoolExecutorHandler.getService().scheduleAtFixedRate(chartRunnable, 3, 6, TimeUnit.SECONDS));
        if (!capture.isRunning())
            ScheduledThreadPoolExecutorHandler.getService().execute(captureRunnable);
        clearCaptureBtn.setDisable(true);
        if (handler.getStatusbackgroundRunnable()) {
            handler.cancelbackgroundRunnable();
            handler.setbackgroundRunnable(ScheduledThreadPoolExecutorHandler.getService().scheduleAtFixedRate(createTPS, 2, 4, TimeUnit.SECONDS));
        } else {
            handler.setbackgroundRunnable(ScheduledThreadPoolExecutorHandler.getService().scheduleAtFixedRate(createTPS, 2, 4, TimeUnit.SECONDS));
        }
        TimelineCtrl(true);
        try {
            FXMLLoader loader = new FXMLLoader();
            loader.load(getClass().getResource("AdminSideTab.fxml").openStream());
            ControllerAdminSideTab ctrl = loader.getController();
            ctrl.getVariables(this.device, this.handler, this.capture, ARPDetection, this.threshold, this.SMSHandler, EmailHandler);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void stopCapturing() {
        capture.stopSniffing();
        handler.cancelbackgroundRunnable();
        handler.cancelchartDataRunnable();
        TimelineCtrl(false);
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
        close.setOnAction(__ -> alert.hideWithAnimation());
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

    private class charts implements Runnable {
        @Override
        public void run() {
            if (capture.checkThreshold()) {
                capture.stopSniffing();
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
                                handler.cancelchartDataRunnable();
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
                ScheduledThreadPoolExecutorHandler.getService().execute(captureRunnable);
            } else if (capture.checkARP() && ARPDetection != false) {
                capture.stopSniffing();
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
                                handler.cancelchartDataRunnable();
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
                ScheduledThreadPoolExecutorHandler.getService().execute(captureRunnable);
            }
            addToQueue();
            ProtoMakeup();
            TopIPMakeup();
            Platform.runLater(() -> alertCount.setText("Suspicious Events Count: " + Integer.toString(capture.getEvents())));
        }
    }

    private class capture implements Runnable {
        @Override
        public void run() {
            capture.startSniffing();
        }
    }

    private class sendEmailF implements Runnable {
        @Override
        public void run() {
            String pcapFilePath = capture.getFullPcapExportPath();
            EmailHandler.sendFullPcap(pcapFilePath);
        }
    }

    private class createTPS implements Runnable {
        @Override
        public void run() {
            capture.getTrafficPerSecond();
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
            Logger.getLogger(ControllerCAMainDashboard.class.getName()).log(Level.SEVERE, null, ex);
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
        if (!lookup) {
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
