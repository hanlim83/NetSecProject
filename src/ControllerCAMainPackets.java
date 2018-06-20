import Model.CapturedPacket;
import Model.NetworkCapture;
import com.jfoenix.controls.JFXDrawer;
import com.jfoenix.controls.JFXHamburger;
import com.jfoenix.controls.JFXToggleButton;
import com.jfoenix.transitions.hamburger.HamburgerBackArrowBasicTransition;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import org.pcap4j.core.PcapNetworkInterface;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ControllerCAMainPackets implements Initializable {

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

    private PcapNetworkInterface device;
    private Scene myScene;
    public static AnchorPane rootP;
    private ArrayList<CapturedPacket>packets;
    private ObservableList<CapturedPacket> OLpackets;
    private NetworkCapture capture;
    private Thread captureThread;
    private ScheduledExecutorService service;
    private ScheduledFuture tableviewRunnable;
    private boolean FirstRun = true;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        hamburgerBar();
        tableColPN.setCellValueFactory(new PropertyValueFactory<CapturedPacket,Integer>("number"));
        tableColSrcIP.setCellValueFactory(new PropertyValueFactory<CapturedPacket,String>("srcIP"));
        tableColSrcPort.setCellValueFactory(new PropertyValueFactory<CapturedPacket,Integer>("srcPort"));
        tableColDstIP.setCellValueFactory(new PropertyValueFactory<CapturedPacket,String>("destIP"));
        tableColDstPort.setCellValueFactory(new PropertyValueFactory<CapturedPacket,Integer>("dstPort"));
        tableColProtocol.setCellValueFactory(new PropertyValueFactory<CapturedPacket,String>("Protocol"));
        tableColLength.setCellValueFactory(new PropertyValueFactory<CapturedPacket,Integer>("length"));
        tableColInfo.setCellValueFactory(new PropertyValueFactory<CapturedPacket,String>("information"));
        captureToggle.selectedProperty().addListener(new ChangeListener<Boolean>() {
            @Override
            public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
                if (captureToggle.isSelected()){
                    startCapturing();
                }
                else {
                    stopCapturing();
                }
            }
        });
    }
    public void passVariables(PcapNetworkInterface nif, ScheduledExecutorService service){
        this.device = nif;
        this.service = service;
    }

    public void startCapturing(){
        if (capture == null) {
            capture = new NetworkCapture(device);
            captureToggle.setSelected(true);
        }
//        if (FirstRun == true){
            tableviewRunnable = service.scheduleAtFixedRate(new Runnable() {
                @Override
                public void run() {
                    Platform.runLater(new Runnable() {
                        @Override public void run() {
                            packets = capture.packets;
                            OLpackets = FXCollections.observableArrayList(packets);
                            packetstable.setItems(OLpackets);
                            packetstable.refresh();
                        }
                    });
                }
            }, 2, 1, TimeUnit.SECONDS);
            /*FirstRun = false;
        }*/
        Runnable task = () -> {
                capture.startSniffing();
                packets = capture.packets;
                for (CapturedPacket p : packets) {
                    System.out.println(p.toString());
                }
        };
        captureThread = new Thread(task);
        captureThread.setDaemon(true);
        captureThread.start();
    }
    public void stopCapturing(){
        capture.stopSniffing();
        tableviewRunnable.cancel(true);
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Capture Summary");
        alert.setHeaderText("Capture Summary Details");
        if (com.sun.jna.Platform.isWindows())
            alert.setContentText("Packets Received By Interface: "+capture.getPacketsReceived()+"\nPackets Dropped: "+capture.getPacketsDropped()+"\nPackets Dropped By Interface: "+capture.getPacketsDroppedByInt()+"\nTotal Packets Captured: "+capture.getPacketsCaptured());
        else
            alert.setContentText("Packets Received By Interface: "+capture.getPacketsReceived()+"\nPackets Dropped: "+capture.getPacketsDropped()+"\nPackets Dropped By Interface: "+capture.getPacketsDroppedByInt()+"\nTotal Packets Captured: "+capture.getPktCount());
        alert.showAndWait();
    }
    public void hamburgerBar() {
        rootP = anchorPane;

        try {
            VBox box = FXMLLoader.load(getClass().getResource("SideTab.fxml"));
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

    }
}
