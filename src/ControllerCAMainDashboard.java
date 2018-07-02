import Model.CapturedPacket;
import Model.LineChartObject;
import Model.NetworkCapture;
import com.jfoenix.animation.alert.JFXAlertAnimation;
import com.jfoenix.controls.*;
import com.jfoenix.transitions.hamburger.HamburgerBackArrowBasicTransition;
import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
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
import javafx.util.Duration;
import org.pcap4j.core.PcapNetworkInterface;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ControllerCAMainDashboard implements Initializable {
    @FXML
    private AnchorPane anchorPane;
    @FXML
    private JFXHamburger hamburger;
    @FXML
    private JFXDrawer drawer;
    @FXML
    private JFXToggleButton captureToggle;
    @FXML
    private JFXButton exportPcapBtn;
    @FXML
    private JFXButton clearCaptureBtn;
    @FXML
    private LineChart<Number,Number> networkTrafficChart;
    @FXML
    private PieChart protocolChart;
    @FXML
    private PieChart top10IPChart;

    private PcapNetworkInterface device;
    private Scene myScene;
    public static AnchorPane rootP;
    private ArrayList<CapturedPacket>packets;
    private NetworkCapture capture;
    private Thread captureThread;
    private ScheduledExecutorService service;
    private XYChart.Series series;
    private NumberAxis chartXAxis;
    private NumberAxis chartYAxis;
    private Timeline animation;
    private double y = 10;
    private final int MAX_DATA_POINTS = 24, MAX = 34, MIN = 5;
    public ArrayList<LineChartObject> packetsLineChart;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        hamburgerBar();
        animation = new Timeline();
        animation.setCycleCount(Animation.INDEFINITE);
        chartXAxis = new NumberAxis(0, MAX_DATA_POINTS + 1, 2);
        chartYAxis = new NumberAxis(MIN - 1, MAX + 1, 1);
        networkTrafficChart = new LineChart<>(chartXAxis, chartYAxis);
        series = new XYChart.Series();
        packetsLineChart = new ArrayList<LineChartObject>();
    }

    public void plotCaptureLine() {
        LineChartObject data = capture.getTrafficPerSecond();
        series.getData().add(new XYChart.Data<Number, Number>(data.getLocation(), data.getData()));
        if (data.getCount() > MAX_DATA_POINTS) {
            series.getData().remove(0);
        }
        if (data.getCount() > MAX_DATA_POINTS - 1) {
            chartXAxis.setLowerBound(chartXAxis.getLowerBound() + 1);
            chartXAxis.setUpperBound(chartXAxis.getUpperBound() + 1);
        }
    }

    public void passVariables(PcapNetworkInterface nif, ScheduledExecutorService service, NetworkCapture Capture){
        this.device = nif;
        this.service = service;
        this.capture = Capture;
        if (capture == null){
            clearCaptureBtn.setDisable(true);
            exportPcapBtn.setDisable(true);
        } else if (capture != null){
            clearCaptureBtn.setDisable(false);
            exportPcapBtn.setDisable(false);
            packets = capture.packets;
        }
        try {
            FXMLLoader loader = new FXMLLoader();
            loader.load(getClass().getResource("AdminSideTab.fxml").openStream());
            ControllerAdminSideTab ctrl = loader.<ControllerAdminSideTab>getController();
            ctrl.getVariables(this.device,this.service,this.capture);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void startCapturing(){
        if (capture == null)
            capture = new NetworkCapture(device);
        animation.play();
        animation.getKeyFrames()
                .add(new KeyFrame(Duration.millis(10000),
                        (ActionEvent actionEvent) -> plotCaptureLine()));
        Runnable task = () -> {
            capture.startSniffing();
            packets = capture.packets;
        };
        captureThread = new Thread(task);
        captureThread.setDaemon(true);
        captureThread.start();
        exportPcapBtn.setDisable(true);
        clearCaptureBtn.setDisable(true);
    }
    public void stopCapturing(){
        capture.stopSniffing();
        animation.pause();
        //Alert below
        myScene = anchorPane.getScene();
        Stage stage = (Stage) (myScene).getWindow();
        String title = "Packet Capturing Summary";
        String content;
        if (com.sun.jna.Platform.isWindows())
            content = "Packets Received By Interface: "+capture.getPacketsReceived()+"\nPackets Dropped: "+capture.getPacketsDropped()+"\nPackets Dropped By Interface: "+capture.getPacketsDroppedByInt()+"\nTotal Packets Captured: "+capture.getPacketsCaptured();
        else
            content = "Packets Received By Interface: "+capture.getPacketsReceived()+"\nPackets Dropped: "+capture.getPacketsDropped()+"\nPackets Dropped By Interface: "+capture.getPacketsDroppedByInt()+"\nTotal Packets Captured: "+capture.getPktCount();
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
        exportPcapBtn.setDisable(false);
        try {
            FXMLLoader loader = new FXMLLoader();
            loader.load(getClass().getResource("AdminSideTab.fxml").openStream());
            ControllerAdminSideTab ctrl = loader.<ControllerAdminSideTab>getController();
            ctrl.getVariables(this.device,this.service,this.capture);
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
        layout.setActions(clearCapture,clearCaptureAndInt);
        JFXAlert<Void> alert = new JFXAlert<>(stage);
        alert.setOverlayClose(true);
        alert.setAnimation(JFXAlertAnimation.CENTER_ANIMATION);
        alert.setContent(layout);
        alert.initModality(Modality.APPLICATION_MODAL);
        clearCapture.setOnAction(addEvent -> {
            capture = null;
            clearCaptureBtn.setDisable(true);
            exportPcapBtn.setDisable(true);
            try {
                FXMLLoader loader = new FXMLLoader();
                loader.load(getClass().getResource("AdminSideTab.fxml").openStream());
                ControllerAdminSideTab ctrl = loader.<ControllerAdminSideTab>getController();
                ctrl.getVariables(this.device,this.service,this.capture);
            } catch (IOException e) {
                e.printStackTrace();
            }
            alert.hideWithAnimation();
        });
        clearCaptureAndInt.setOnAction(addEvent -> {
            capture = null;
            FXMLLoader loader = new FXMLLoader(getClass().getResource("CALanding.fxml"));
            myScene = (Scene) ((Node) event.getSource()).getScene();
            Parent nextView = null;
            try {
                nextView = loader.load();
                ControllerCALanding controller = loader.<ControllerCALanding>getController();
                controller.passVariables(service);
            } catch (IOException e) {
                e.printStackTrace();
            }
            stage.setScene(new Scene(nextView));
            alert.close();
            stage.show();
        });
        alert.showAndWait();
    }
    @FXML
    public void launchPcapExport(ActionEvent event) {
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
        if (capture.export(f.getAbsolutePath())) {
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