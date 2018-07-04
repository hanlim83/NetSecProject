import Model.ContinuousNetworkCapture;
import Model.NetworkCapture;
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXDrawer;
import com.jfoenix.controls.JFXHamburger;
import com.jfoenix.controls.JFXTextArea;
import com.jfoenix.transitions.hamburger.HamburgerBackArrowBasicTransition;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import org.pcap4j.core.PcapAddress;
import org.pcap4j.core.PcapNetworkInterface;
import org.pcap4j.core.Pcaps;

import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ControllerCAContinuousCaptureMain implements Initializable {

    @FXML
    private AnchorPane anchorPane;

    @FXML
    private JFXHamburger hamburger;

    @FXML
    private JFXDrawer drawer;

    @FXML
    private JFXButton StopBtn;

    @FXML
    private Text totalPacketsText;

    @FXML
    private Text totalDroppedText;

    @FXML
    private Text totalAlertsText;

    @FXML
    private JFXTextArea loggingTextArea;

    private Scene myScene;
    public static AnchorPane rootP;
    private ContinuousNetworkCapture Ccapture;
    private String exportFilePath;
    private ScheduledFuture continuousNetworkCaptureFuture;
    private ScheduledFuture updateStatsFuture;
    private Runnable continuousNetworkCapture;
    private Runnable updateStats;
    //Imported from previous screens
    private PcapNetworkInterface Odevice;
    private ScheduledExecutorService service;
    private NetworkCapture Ncapture;
    private PcapNetworkInterface device;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        hamburgerBar();
    }

    @FXML
    public void stopCapture(ActionEvent event) {
        Ccapture.stopSniffing();
    }



    public void startCapture(PcapNetworkInterface nif, ScheduledExecutorService service, NetworkCapture Capture, PcapNetworkInterface device, String filePath){
        this.Odevice = nif;
        this.service = service;
        this.Ncapture = Capture;
        this.device = device;
        this.exportFilePath = filePath;
        this.Ccapture = new ContinuousNetworkCapture(device,exportFilePath);
        continuousNetworkCapture = new Runnable() {
            @Override
            public void run() {
                Ccapture.startSniffing();
            }
        };
        updateStats = new Runnable() {
            @Override
            public void run() {
                Ccapture.printStat();
                totalPacketsText.setText(Long.toString(Ccapture.getPacketsReceived()));
                totalDroppedText.setText(Long.toString(Ccapture.getPacketsDropped()));
            }
        };
        loggingTextArea.setText(Pcaps.libVersion()+"\n"+"Starting Capture on "+device.getName()+"\n");
        List<PcapAddress> addresses = device.getAddresses();
        for (PcapAddress p: addresses){
            loggingTextArea.setText(loggingTextArea.getText()+"IP Address Assigned on Interface: "+p.toString()+"\n");
        }
        continuousNetworkCaptureFuture = service.schedule(continuousNetworkCapture,0,TimeUnit.SECONDS);
        updateStatsFuture = service.scheduleAtFixedRate(updateStats,0,2,TimeUnit.SECONDS);
    }

    public void hamburgerBar() {
        rootP = anchorPane;
        try {
            VBox box = FXMLLoader.load(getClass().getResource("AdminSideTab.fxml"));
            drawer.setSidePane(box);
            drawer.setVisible(false);
            drawer.setDefaultDrawerSize(219);
        } catch (IOException ex) {
            Logger.getLogger(ControllerCAContinuousCaptureMain.class.getName()).log(Level.SEVERE, null, ex);
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
