import Database.admin_DB;
import Model.NetworkCapture;
import Model.SMS;
import Model.ScheduledExecutorServiceHandler;
import Model.TopIPObject;
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
import javafx.scene.control.Label;
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
import java.util.ConcurrentModificationException;
import java.util.ResourceBundle;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

import static java.util.concurrent.TimeUnit.SECONDS;

public class ControllerCAMainDashboard implements Initializable {
    private static final int MAX_DATA_POINTS = 50;
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
    private Scene myScene;
    private PcapNetworkInterface device;
    private NetworkCapture capture;
    private ScheduledExecutorServiceHandler handler;
    private String directoryPath;
    private Integer threshold;
    private ArrayList<String> adminPN;
    private admin_DB db;
    private SMS SMSHandler;
    private ArrayList<Integer> TPS;
    private int xSeriesData = 0;
    private XYChart.Series<Number, Number> series = new XYChart.Series<>();
    private ConcurrentLinkedQueue<Number> data = new ConcurrentLinkedQueue<>();
    private NumberAxis xAxis;
    private AnimationTimer timer;

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
            // Override to remove symbols on each data point
            @Override
            protected void dataItemAdded(Series<Number, Number> series, int itemIndex, Data<Number, Number> item) {
            }
        };
        networkTrafficChart.setAnimated(false);
        networkTrafficChart.setTitle("Network Traffic Chart");
        networkTrafficChart.setHorizontalGridLinesVisible(true);
        series.setName("Current Network Traffic");
        networkTrafficChart.getData().add(series);
        networkTrafficChart.setPrefWidth(1051);
        networkTrafficChart.setPrefHeight(334);
        LineChartAnchorPane.getChildren().add(networkTrafficChart);
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
            series.getData().clear();
            protocolChart.getData().clear();
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
                controller.passVariables(handler, null, null, 0, SMSHandler, null);
            } catch (IOException e) {
                e.printStackTrace();
            }
            stage.setScene(new Scene(nextView));
            alert.close();
            stage.show();
        });
        alert.showAndWait();
    }

    public void passVariables(PcapNetworkInterface nif, ScheduledExecutorServiceHandler handler, NetworkCapture capture, String directoryPath, Integer threshold, SMS USMSHandler) {
        this.device = nif;
        this.handler = handler;
        this.directoryPath = directoryPath;
        this.threshold = threshold;
        this.SMSHandler = USMSHandler;
        if (SMSHandler == null && threshold != 0) {
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
                }
            }, 2, TimeUnit.SECONDS));
        } else if (threshold != 0) {
            this.SMSHandler = USMSHandler;
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
                }
            }, 1, TimeUnit.SECONDS));
        } else {
            spinner.setVisible(false);
            captureToggle.setDisable(false);
            hamburger.setDisable(false);
        }
        if (capture == null)
            clearCaptureBtn.setDisable(true);
        else if (capture.isRunning()) {
            this.capture = capture;
            captureToggle.setSelected(true);
            handler.setchartDataRunnable(ScheduledExecutorServiceHandler.getService().scheduleAtFixedRate(new Runnable() {
                @Override
                public void run() {
                    boolean flag = capture.checkThreshold();
                    try {
                        addToQueue();
                        ProtoMakeup();
                        addDataToSeries();
                        if (flag) {
                            SMSHandler.sendAlert();
                   /* if (!timerTaskinProgress) {
                        timer.schedule(exportTask,(RECORD_DURATION * MINUITE_TO_MILISECONDS));
                        timerTaskinProgress = true;
                    }*/
                            capture.Specficexport();
                            Platform.runLater(new Runnable() {
                                @Override
                                public void run() {
                                    myScene = anchorPane.getScene();
                                    Stage stage = (Stage) (myScene).getWindow();
                                    String title = "Suspicious Network Event Detected!";
                                    String content = "A Suspicious network event has been detected! Current network traffic has exceeded the threshold. A pcap file (" + capture.getSpecificExportFileName() + ") containing packets before the alerts has been generated for you.";
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
                            });
                        }
                        Platform.runLater(new Runnable() {
                            @Override
                            public void run() {
                                alertCount.setText("Suspicious Events Count: " + Integer.toString(capture.getEvents()));
                            }
                        });
                    } catch (ConcurrentModificationException e) {
                        System.err.println("ConcurrentModification Detected");
                        capture.stopSniffing();
                        addToQueue();
                        ProtoMakeup();
                        addDataToSeries();
                        if (flag) {
                            SMSHandler.sendAlert();
                   /* if (!timerTaskinProgress) {
                        timer.schedule(exportTask,(RECORD_DURATION * MINUITE_TO_MILISECONDS));
                        timerTaskinProgress = true;
                    }*/
                            capture.Specficexport();
                            Platform.runLater(new Runnable() {
                                @Override
                                public void run() {
                                    myScene = anchorPane.getScene();
                                    Stage stage = (Stage) (myScene).getWindow();
                                    String title = "Suspicious Network Event Detected!";
                                    String content = "A Suspicious network event has been detected! Current network traffic has exceeded the threshold. A pcap file (" + capture.getSpecificExportFileName() + ") containing packets before the event has been generated for you.";
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
                            });
                        }
                        handler.setcaptureRunnable(ScheduledExecutorServiceHandler.getService().schedule(new Runnable() {
                            @Override
                            public void run() {
                                capture.startSniffing();
                            }
                        }, 1, SECONDS));
                        Platform.runLater(new Runnable() {
                            @Override
                            public void run() {
                                alertCount.setText("Suspicious Events Count: " + Integer.toString(capture.getEvents()));
                            }
                        });
                    }
                }
            }, 2, 6, TimeUnit.SECONDS));
            TimelineCtrl(true);
        } else if (capture != null) {
            this.capture = capture;
            clearCaptureBtn.setDisable(false);
        }
        try {
            FXMLLoader loader = new FXMLLoader();
            loader.load(getClass().getResource("AdminSideTab.fxml").openStream());
            ControllerAdminSideTab ctrl = loader.getController();
            ctrl.getVariables(this.device, this.handler, this.capture, directoryPath, threshold, SMSHandler);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void addToQueue() {
        capture.getTrafficPerSecond();
        TPS = capture.PreviousTPS;
        data.add(TPS.get(xSeriesData));
    }

    private void TimelineCtrl(boolean flag) {
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
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                for (int i = 0; i < 20; i++) { //-- add 20 numbers to the plot+
                    if (data.isEmpty()) break;
                    series.getData().add(new XYChart.Data<>(xSeriesData++, data.remove()));
                }
                if (series.getData().size() > MAX_DATA_POINTS) {
                    series.getData().remove(0, series.getData().size() - MAX_DATA_POINTS);
                }
                xAxis.setLowerBound(xSeriesData - MAX_DATA_POINTS);
                xAxis.setUpperBound(xSeriesData - 1);
            }
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
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                protocolChart.setData(data);
            }
        });
    }

    public void TopIPMakeup() {
        int index = 0, max = 0;
        capture.getTop5IP();
        ArrayList<TopIPObject> IPData = capture.Top5IPMakeup;
        if (IPData.isEmpty())
            return;
        max = IPData.size();
        ObservableList<PieChart.Data> data = FXCollections.observableArrayList();
        for (index = 0; index < max; index++) {
            data.add(new PieChart.Data(IPData.get(index).getKey(), IPData.get(index).getValue()));
        }
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                top10IPChart.setData(data);
            }
        });
    }

    public void startCapturing() {
        if (capture == null)
            capture = new NetworkCapture(device, directoryPath, threshold);
        handler.setchartDataRunnable(ScheduledExecutorServiceHandler.getService().scheduleAtFixedRate(new Runnable() {
            @Override
            public void run() {
                boolean flag = capture.checkThreshold();
                try {
                    addToQueue();
                    ProtoMakeup();
                    addDataToSeries();
                    TopIPMakeup();
                    if (flag) {
                        SMSHandler.sendAlert();
                   /* if (!timerTaskinProgress) {
                        timer.schedule(exportTask,(RECORD_DURATION * MINUITE_TO_MILISECONDS));
                        timerTaskinProgress = true;
                    }*/
                        capture.Specficexport();
                        Platform.runLater(new Runnable() {
                            @Override
                            public void run() {
                                myScene = anchorPane.getScene();
                                Stage stage = (Stage) (myScene).getWindow();
                                String title = "Suspicious Network Event Detected!";
                                String content = "A Suspicious network event has been detected! Current network traffic has exceeded the threshold. A pcap file (" + capture.getSpecificExportFileName() + ") containing packets before the alerts has been generated for you.";
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
                        });
                    }
                    Platform.runLater(new Runnable() {
                        @Override
                        public void run() {
                            alertCount.setText("Suspicious Events Count: " + Integer.toString(capture.getEvents()));
                        }
                    });
                } catch (ConcurrentModificationException e) {
                    System.err.println("ConcurrentModification Detected");
                    capture.stopSniffing();
                    addToQueue();
                    ProtoMakeup();
                    addDataToSeries();
                    TopIPMakeup();
                    if (flag) {
                        SMSHandler.sendAlert();
                   /* if (!timerTaskinProgress) {
                        timer.schedule(exportTask,(RECORD_DURATION * MINUITE_TO_MILISECONDS));
                        timerTaskinProgress = true;
                    }*/
                        capture.Specficexport();
                        Platform.runLater(new Runnable() {
                            @Override
                            public void run() {
                                myScene = anchorPane.getScene();
                                Stage stage = (Stage) (myScene).getWindow();
                                String title = "Suspicious Network Event Detected!";
                                String content = "A Suspicious network event has been detected! Current network traffic has exceeded the threshold. A pcap file (" + capture.getSpecificExportFileName() + ") containing packets before the event has been generated for you.";
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
                        });
                    }
                    handler.setcaptureRunnable(ScheduledExecutorServiceHandler.getService().schedule(new Runnable() {
                        @Override
                        public void run() {
                            capture.startSniffing();
                        }
                    }, 1, SECONDS));
                    Platform.runLater(new Runnable() {
                        @Override
                        public void run() {
                            alertCount.setText("Suspicious Events Count: " + Integer.toString(capture.getEvents()));
                        }
                    });
                }
            }
        }, 2, 6, TimeUnit.SECONDS));
        if (handler.getcaptureRunnable() == null || !handler.getStatuscaptureRunnable()) {
            handler.setcaptureRunnable(ScheduledExecutorServiceHandler.getService().schedule(new Runnable() {
                @Override
                public void run() {
                    capture.startSniffing();
                }
            }, 1, SECONDS));
        }
        clearCaptureBtn.setDisable(true);
        TimelineCtrl(true);
        try {
            FXMLLoader loader = new FXMLLoader();
            loader.load(getClass().getResource("AdminSideTab.fxml").openStream());
            ControllerAdminSideTab ctrl = loader.getController();
            ctrl.getVariables(this.device, this.handler, this.capture, this.directoryPath, this.threshold, this.SMSHandler);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void stopCapturing() {
        capture.stopSniffing();
        handler.cancelchartDataRunnable();
        TimelineCtrl(false);
        capture.Generalexport();
        //Alert below
        myScene = anchorPane.getScene();
        Stage stage = (Stage) (myScene).getWindow();
        String title = "Packet Capturing Summary";
        String content;
        if (com.sun.jna.Platform.isWindows())
            content = "Packets Received By Interface: " + capture.getPacketsReceived() + "\nPackets Dropped By Interface: " + capture.getPacketsDroppedByInt() + "\nTotal Packets Captured: " + capture.getPacketsCaptured() + "\nNetwork Capture File Name: " + capture.getGeneralExportFileName();
        else
            content = "Packets Received By Interface: " + capture.getPacketsReceived() + "\nPackets Dropped By Interface: " + capture.getPacketsDroppedByInt() + "\nTotal Packets Captured: " + capture.getPacketCount() + "\nNetwork Capture File Name: " + capture.getGeneralExportFileName();
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
}
