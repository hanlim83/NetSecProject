import Model.*;
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXDrawer;
import com.jfoenix.controls.JFXHamburger;
import com.jfoenix.controls.JFXSpinner;
import com.jfoenix.transitions.hamburger.HamburgerBackArrowBasicTransition;
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
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import org.pcap4j.core.*;
import org.pcap4j.packet.Packet;

import java.io.EOFException;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.ResourceBundle;
import java.util.concurrent.TimeoutException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ControllerCAMainAlertDashboard implements Initializable {
    public static AnchorPane rootP;
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
    @FXML
    private Label ipAddr;
    @FXML
    private JFXButton homeButton;

    public static final int LineRange = 5;
    private final int MAX_DATA_POINTS = 25, MAX = 10, MIN = 5;
    public ArrayList<CapturedPacket> packets = new ArrayList<CapturedPacket>();
    public ArrayList<Integer> TPS = new ArrayList<Integer>();
    public ArrayList<Integer> ProtocolMakeupData = new ArrayList<Integer>();
    public ArrayList<String> ProtocolMakeupProtocols = new ArrayList<String>();
    private Scene myScene;
    private PcapNetworkInterface device;
    private NetworkCapture capture;
    private ScheduledThreadPoolExecutorHandler handler;
    private boolean ARPDetection;
    private Integer threshold;
    private AWSSMS SMSHandler;
    private OutlookEmail EmailHandler;
    public ArrayList<TopIPObject> Top5IPMakeup = new ArrayList<TopIPObject>();
    private XYChart.Series<Number, Number> dataSeries;
    private XYChart.Series<Number, Number> dataSeries1;
    private NumberAxis xAxis;
    private double sequence = 0;
    private double y = 10;
    private boolean captureType;
    private String partialCaptureFilePath;
    private PcapHandle handle;
    private int pktCount = 0;
    private ShowData sRunnable = new ShowData();

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
        hamburger.setDisable(true);
        returnToCaptureBtn.setDisable(true);
        xAxis = new NumberAxis();
        NumberAxis yAxis = new NumberAxis();
        xAxis.setForceZeroInRange(false);
        xAxis.setLabel("Duration per 5 Seconds");
        yAxis.setLabel("Packets Recieved");
        yAxis.setTickUnit(LineRange);
        dataSeries = new XYChart.Series<>();
        dataSeries.setName("Network Traffic");
        dataSeries1 = new XYChart.Series<>();
        dataSeries1.setName("Threshold");
        networkTrafficChart = new LineChart<Number, Number>(xAxis, yAxis);
        networkTrafficChart.getData().add(dataSeries);
        networkTrafficChart.getData().add(dataSeries1);
        networkTrafficChart.setPrefWidth(1051);
        networkTrafficChart.setPrefHeight(334);
        LineChartAnchorPane.getChildren().add(networkTrafficChart);
    }

    public void passVariables(PcapNetworkInterface nif, ScheduledThreadPoolExecutorHandler handler, NetworkCapture capture, boolean ARPDetection, Integer threshold, AWSSMS USMSHandler, String absolutePath, boolean captureType, OutlookEmail OEmailHandler) {
        this.device = nif;
        this.handler = handler;
        this.ARPDetection = ARPDetection;
        this.threshold = threshold;
        this.SMSHandler = USMSHandler;
        this.EmailHandler = OEmailHandler;
        this.capture = capture;
        this.captureType = captureType;
        this.captureType = captureType;
        this.partialCaptureFilePath = absolutePath;
        try {
            FXMLLoader loader = new FXMLLoader();
            loader.load(getClass().getResource("AdminSideTab.fxml").openStream());
            ControllerAdminSideTab ctrl = loader.getController();
            ctrl.getVariables(this.device, this.handler, this.capture, this.ARPDetection, this.threshold, SMSHandler, EmailHandler);
        } catch (IOException e) {
            e.printStackTrace();
        }
        ScheduledThreadPoolExecutorHandler.getService().execute(sRunnable);
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
                controller.passVariables(device, handler, capture, ARPDetection, threshold, SMSHandler, EmailHandler);
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
                controller.passVariables(device, handler, capture, ARPDetection, threshold, SMSHandler, EmailHandler);
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
            //Extracted from ProtocolMakeup
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
            //Based from getTrafficPerSecond
            try {
                Timestamp firstPacket = packets.get(0).getOrignalTimeStamp();
                Calendar cal = Calendar.getInstance();
                cal.setTimeInMillis(firstPacket.getTime());
                cal.add(Calendar.SECOND, LineRange);
                Timestamp later = new Timestamp(cal.getTime().getTime());
                Timestamp next = null;
                boolean finished = false;
                while (finished != true) {
                    int packetRecord = 0;
                    for (CapturedPacket c : packets) {
                        if (next == null && c.getOrignalTimeStamp().after(firstPacket) && c.getOrignalTimeStamp().before(later)) {
                            ++packetRecord;
                        } else if (next != null && c.getOrignalTimeStamp().after(next) && c.getOrignalTimeStamp().before(later))
                            ++packetRecord;
                    }
                    if (packetRecord != 0) {
                        TPS.add(packetRecord);
                        next = later;
                        cal = Calendar.getInstance();
                        cal.setTimeInMillis(later.getTime());
                        cal.add(Calendar.SECOND, LineRange);
                        later = new Timestamp(cal.getTime().getTime());
                    } else
                        finished = true;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            Platform.runLater(() -> {
                //Extracted from ProtoMakeup
                protocolChart.setData(data);
                //Extracted from TopIPMakeup
                top10IPChart.setData(data1);
                //Based on Runnable Line Chart
                int Chartindex = 0;
                for (Integer i : TPS) {
                    dataSeries1.getData().add(new XYChart.Data<>(Chartindex, threshold));
                    dataSeries.getData().add(new XYChart.Data<>(Chartindex++, i));
                    if (sequence > MAX_DATA_POINTS) {
                        dataSeries.getData().remove(0);
                        dataSeries1.getData().remove(0);
                    }
                    if (sequence > MAX_DATA_POINTS - 1) {
                        xAxis.setLowerBound(xAxis.getLowerBound() + 1);
                        xAxis.setUpperBound(xAxis.getUpperBound() + 1);
                    }
                }
            });
            spinner.setVisible(false);
            hamburger.setDisable(false);
            returnToCaptureBtn.setDisable(false);
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
