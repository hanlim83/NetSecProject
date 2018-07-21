import Model.*;
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXDrawer;
import com.jfoenix.controls.JFXHamburger;
import com.jfoenix.controls.JFXSpinner;
import com.jfoenix.transitions.hamburger.HamburgerBackArrowBasicTransition;
import javafx.animation.AnimationTimer;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
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
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import org.pcap4j.core.*;
import org.pcap4j.packet.Packet;

import java.io.EOFException;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.ResourceBundle;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.TimeoutException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ControllerCAMainAlertDashboard implements Initializable {
    private static final int MAX_DATA_POINTS = 25;
    public static AnchorPane rootP;
    public ArrayList<CapturedPacket> packets = new ArrayList<CapturedPacket>();
    public ArrayList<Integer> TPS = new ArrayList<Integer>();
    public ArrayList<Integer> ProtocolMakeupData = new ArrayList<Integer>();
    public ArrayList<String> ProtocolMakeupProtocols = new ArrayList<String>();
    public ArrayList<TopIPObject> Top5IPMakeup = new ArrayList<TopIPObject>();
    @FXML
    private AnchorPane anchorPane;
    @FXML
    private JFXHamburger hamburger;
    @FXML
    private PieChart protocolChart;
    @FXML
    private PieChart top10IPChart;
    @FXML
    private LineChart<Number, Number> networkTrafficChart;
    @FXML
    private AnchorPane LineChartAnchorPane;
    @FXML
    private JFXDrawer drawer;
    @FXML
    private JFXSpinner spinner;
    @FXML
    private JFXButton returnToCaptureBtn;
    private Scene myScene;
    private PcapNetworkInterface device;
    private NetworkCapture capture;
    private ScheduledExecutorServiceHandler handler;
    private String directoryPath;
    private Integer threshold;
    private SMS SMSHandler;
    private int xSeriesData = 0;
    private XYChart.Series<Number, Number> series = new XYChart.Series<>();
    private ConcurrentLinkedQueue<Number> data = new ConcurrentLinkedQueue<>();
    private NumberAxis xAxis;
    private AnimationTimer timer;
    private boolean captureType;
    private String partialCaptureFilePath;
    private PcapHandle handle;
    private int pktCount = 0;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        hamburgerBar();
        hamburger.setDisable(true);
        returnToCaptureBtn.setDisable(true);
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
        series.setName("Network Traffic");
        networkTrafficChart.getData().add(series);
        networkTrafficChart.setPrefWidth(1051);
        networkTrafficChart.setPrefHeight(334);
        LineChartAnchorPane.getChildren().add(networkTrafficChart);
    }

    public void passVariables(PcapNetworkInterface nif, ScheduledExecutorServiceHandler handler, NetworkCapture capture, String directoryPath, Integer threshold, SMS USMSHandler, String absolutePath, boolean captureType) {
        this.device = nif;
        this.handler = handler;
        this.directoryPath = directoryPath;
        this.threshold = threshold;
        this.SMSHandler = USMSHandler;
        this.capture = capture;
        this.captureType = captureType;
        this.captureType = captureType;
        this.partialCaptureFilePath = absolutePath;
        try {
            FXMLLoader loader = new FXMLLoader();
            loader.load(getClass().getResource("AdminSideTab.fxml").openStream());
            ControllerAdminSideTab ctrl = loader.getController();
            ctrl.getVariables(this.device, this.handler, this.capture, this.directoryPath, this.threshold, SMSHandler);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    synchronized private void incrementCount() {
        ++pktCount;
    }

    synchronized public int getPacketCount() {
        return pktCount;
    }

    @FXML
    public void returnToCaptureScreen(ActionEvent event) {
        if (captureType) {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("CAMainDashboard.fxml"));
            myScene = ((Node) event.getSource()).getScene();
            Stage stage = (Stage) (myScene).getWindow();
            Parent nextView = null;
            try {
                nextView = loader.load();
                ControllerCAMainDashboard controller = loader.getController();
                controller.passVariables(device, handler, capture, directoryPath, threshold, SMSHandler);
            } catch (IOException e) {
                e.printStackTrace();
            }
            stage.setScene(new Scene(nextView));
            stage.setTitle("Capture - Statistics View");
            stage.show();
        } else {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("CAMainPackets.fxml"));
            myScene = ((Node) event.getSource()).getScene();
            Stage stage = (Stage) (myScene).getWindow();
            Parent nextView = null;
            try {
                nextView = loader.load();
                ControllerCAMainPackets controller = loader.getController();
                controller.passVariables(device, handler, capture, directoryPath, threshold, SMSHandler);
            } catch (IOException e) {
                e.printStackTrace();
            }
            stage.setScene(new Scene(nextView));
            stage.setTitle("Capture - Packet View");
            stage.show();
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

    private class ShowData implements Runnable {
        @Override
        public void run() {
            try {
                handle = Pcaps.openOffline(partialCaptureFilePath, PcapHandle.TimestampPrecision.NANO);
                for (; ; ) {
                    try {
                        Packet packet = handle.getNextPacketEx();
                        incrementCount();
                        CapturedPacket cPacket = new CapturedPacket(packet, getPacketCount(), handle.getTimestamp());
                        packets.add(cPacket);
                    } catch (TimeoutException e) {
                    } catch (EOFException e) {
                        System.out.println("EOF");
                        break;
                    }
                }
                handle.close();
            } catch (PcapNativeException e) {
                try {
                    handle = Pcaps.openOffline(partialCaptureFilePath);
                } catch (PcapNativeException e1) {

                }
            } catch (NotOpenException e) {
                e.printStackTrace();
            }
            //Extracted from ProtocolMaekup
            boolean found = false;
            for (CapturedPacket p : packets) {
                if (ProtocolMakeupData.isEmpty() && ProtocolMakeupProtocols.isEmpty()) {
                    ProtocolMakeupData.add(1);
                    ProtocolMakeupProtocols.add(p.identifyProtocol());
                } else {
                    for (int i = 0; i < ProtocolMakeupProtocols.size(); i++) {
                        String proto = ProtocolMakeupProtocols.get(i);
                        if (proto.equals(p.identifyProtocol())) {
                            ProtocolMakeupData.set(i, ProtocolMakeupData.get(i) + 1);
                            found = true;
                            break;
                        }
                    }
                    if (found == false) {
                        ProtocolMakeupData.add(1);
                        ProtocolMakeupProtocols.add(p.identifyProtocol());
                    }
                }
            }
            //Extracted from ProtoMakeup
            int index = 0, max = 0;
            if (ProtocolMakeupProtocols.size() != ProtocolMakeupData.size())
                return;
            max = ProtocolMakeupData.size();
            ObservableList<PieChart.Data> data = FXCollections.observableArrayList();
            for (index = 0; index < max; index++) {
                data.add(new PieChart.Data(ProtocolMakeupProtocols.get(index), ProtocolMakeupData.get(index)));
            }
            Platform.runLater(new Runnable() {
                @Override
                public void run() {
                    protocolChart.setData(data);
                }
            });
            //Extracted from getTop5IP
            TopIPObject tempt = null;
            int recordedIndex = 0;
            for (CapturedPacket p : packets) {
                if (Top5IPMakeup.isEmpty()) {
                    Top5IPMakeup.add(new TopIPObject(p.getSrcIP(), 1));
                } else {
                    for (int i = 0; i < Top5IPMakeup.size(); i++) {
                        if (!p.getSrcIP().isEmpty() && p.getSrcIP().equals(Top5IPMakeup.get(i).getKey())) {
                            tempt = new TopIPObject(Top5IPMakeup.get(i).getKey(), Top5IPMakeup.get(i).getValue() + 1);
                            recordedIndex = i;
                            break;
                        }
                    }
                    if (tempt != null) {
                        Top5IPMakeup.set(recordedIndex, tempt);
                        recordedIndex = 0;
                        tempt = null;
                    } else {
                        Top5IPMakeup.add(new TopIPObject(p.getSrcIP(), 1));
                    }
                }
            }
            if (!Top5IPMakeup.isEmpty() && Top5IPMakeup.size() > 5) {
                Collections.sort(Top5IPMakeup);
                ArrayList<TopIPObject> temporary = new ArrayList<TopIPObject>();
                for (int j = 0; j < 5; j++) {
                    temporary.add(Top5IPMakeup.get(j));
                }
                Top5IPMakeup = temporary;
            }
            //Extracted from TopIPMakeup
            if (Top5IPMakeup.isEmpty())
                return;
            ObservableList<PieChart.Data> data1 = FXCollections.observableArrayList();
            for (TopIPObject d : Top5IPMakeup) {
                data1.add(new PieChart.Data(d.getKey(), d.getValue()));
            }
            Platform.runLater(new Runnable() {
                @Override
                public void run() {
                    top10IPChart.setData(data1);
                }
            });
            System.err.println("LineChart Not Yet Handled");
            spinner.setVisible(false);
            hamburger.setDisable(false);
            returnToCaptureBtn.setDisable(false);
        }
    }
}