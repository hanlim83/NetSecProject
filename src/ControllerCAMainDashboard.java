import Database.admin_DB;
import Model.*;
import com.jfoenix.animation.alert.JFXAlertAnimation;
import com.jfoenix.controls.*;
import com.jfoenix.transitions.hamburger.HamburgerBackArrowBasicTransition;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
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
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;
import org.pcap4j.core.PcapNetworkInterface;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.ResourceBundle;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

import static java.util.concurrent.TimeUnit.SECONDS;

public class ControllerCAMainDashboard implements Initializable {
    public static AnchorPane rootP;
    private final int MAX_DATA_POINTS = 24, MAX = 39, MIN = 1;
    public ArrayList<LineChartObject> packetsLineChart;
    @FXML
    private AnchorPane anchorPane;
    @FXML
    private JFXHamburger hamburger;
    @FXML
    private JFXDrawer drawer;
    @FXML
    private JFXToggleButton captureToggle;
    @FXML
    private JFXButton clearCaptureBtn;
    @FXML
    private Label alertCount;
    @FXML
    private LineChart<Number, Number> networkTrafficChart;
    @FXML
    private PieChart protocolChart;
    @FXML
    private PieChart top10IPChart;
    @FXML
    private JFXSpinner spinner;

    private PcapNetworkInterface device;
    private Scene myScene;
    private ArrayList<CapturedPacket> packets;
    private NetworkCapture capture;
    private ScheduledExecutorServiceHandler handler;
    private String directoryPath;
    private Integer threshold;
    private XYChart.Series series;
    private NumberAxis chartXAxis;
    private NumberAxis chartYAxis;
    private Timeline animation;
    private double y = 10;
    private ArrayList<String> adminPN;
    private admin_DB db;
    private SMS SMSHandler;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        hamburgerBar();
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
        /*animation = new Timeline();
        animation.getKeyFrames()
                .add(new KeyFrame(Duration.millis(10000),
                        (ActionEvent actionEvent) -> plotCaptureLine()));
        animation.setCycleCount(Animation.INDEFINITE);*/
        chartXAxis = new NumberAxis(0, MAX_DATA_POINTS + 1, 10);
        chartYAxis = new NumberAxis(MIN - 1, MAX + 1, 25);
        networkTrafficChart = new LineChart<>(chartXAxis, chartYAxis);
        series = new XYChart.Series();
        packetsLineChart = new ArrayList<LineChartObject>();
        networkTrafficChart.getXAxis().setAutoRanging(true);
        networkTrafficChart.getYAxis().setAutoRanging(true);
        db = new admin_DB();
        hamburger.setDisable(true);
        captureToggle.setDisable(true);
    }

    public void plotCaptureLine() {
        LineChartObject data = capture.getTrafficPerSecond();
        series.getData().add(new XYChart.Data<Number, Number>(data.getLocation(), data.getData()));
        System.out.println(data.getLocation() + "," + data.getData());
        if (LineChartObject.getCount() > MAX_DATA_POINTS) {
            series.getData().remove(0);
        }
        if (LineChartObject.getCount() > MAX_DATA_POINTS - 1) {
            chartXAxis.setLowerBound(chartXAxis.getLowerBound() + 1);
            chartXAxis.setUpperBound(chartXAxis.getUpperBound() + 1);
        }
    }

    public void passVariables(PcapNetworkInterface nif, ScheduledExecutorServiceHandler handler, NetworkCapture capture, String directoryPath, Integer threshold, SMS USMSHandler) {
        this.device = nif;
        this.handler = handler;
        this.capture = capture;
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
                /*handler.setalertsNotAvailRunnable(ScheduledExecutorServiceHandler.getService().schedule(new Runnable() {
                    @Override
                    public void run() {
                        Platform.runLater(new Runnable() {
                            @Override
                            public void run() {
                                spinner.setVisible(false);
                                myScene = anchorPane.getScene();
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
                                alert.showAndWait();
                                captureToggle.setDisable(false);
                                hamburger.setDisable(false);
                            }
                        });
                    }
                }, 1, TimeUnit.SECONDS));*/
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
                        spinner.setVisible(false);
                        captureToggle.setDisable(false);
                        hamburger.setDisable(false);
                    } catch (SQLException e) {
                        System.err.println("SQL Error");
                        spinner.setVisible(false);
                        captureToggle.setDisable(false);
                        hamburger.setDisable(false);

                    }
                }
            }, 1, TimeUnit.SECONDS));
        }
        if (capture == null) {
            clearCaptureBtn.setDisable(true);
        } else if (capture.isRunning()) {
            captureToggle.setSelected(true);
            //play();

        } else if (capture != null) {
            clearCaptureBtn.setDisable(false);
            packets = capture.packets;
        }
        try {
            FXMLLoader loader = new FXMLLoader();
            loader.load(getClass().getResource("AdminSideTab.fxml").openStream());
            ControllerAdminSideTab ctrl = loader.getController();
            ctrl.getVariables(this.device, this.handler, this.capture, this.directoryPath, this.threshold, this.SMSHandler);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /*public void play() {
        animation.play();
    }

    public void stop() {
        animation.pause();
    }*/

    public void startCapturing() {
        if (capture == null)
            capture = new NetworkCapture(device, directoryPath, threshold);
        if (handler.getcaptureRunnable() == null || !handler.getStatuscaptureRunnable()) {
            handler.setcaptureRunnable(ScheduledExecutorServiceHandler.getService().schedule(new Runnable() {
                @Override
                public void run() {
                    capture.startSniffing();
                }
            }, 1, SECONDS));
        }
        //play();
        try {
            FXMLLoader loader = new FXMLLoader();
            loader.load(getClass().getResource("AdminSideTab.fxml").openStream());
            ControllerAdminSideTab ctrl = loader.getController();
            ctrl.getVariables(this.device, this.handler, this.capture, directoryPath, threshold, SMSHandler);
        } catch (IOException e) {
            e.printStackTrace();
        }
        clearCaptureBtn.setDisable(true);
    }

    public void stopCapturing() {
        capture.stopSniffing();
        //stop();
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

    public void launchPcapExport() {
        myScene = anchorPane.getScene();
        Stage stage = (Stage) (myScene).getWindow();
        FileChooser choose = new FileChooser();
        choose.getExtensionFilters().add(new FileChooser.ExtensionFilter("Network Packet Capture File (*.pcap)", "*.pcap"));
        File f = choose.showSaveDialog(stage);
        if (f == null)
            return;
        else if (!f.getName().contains(".")) {
            f = new File(f.getAbsolutePath() + ".pcap");
        }
        if (capture.Generalexport()) {
            String title = "Packet Capture Exported Sucessfully";
            String content = "Packet Capture has been exported sucessfully! You may open this export file with WireShark or other tools for further analysis.";
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
        } else {
            String title = "Packet Capture Exported Failed";
            String content = "Oops something happened and the export failed.";
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
        }
    }
}