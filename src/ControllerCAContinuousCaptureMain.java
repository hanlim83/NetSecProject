import Model.ContinuousNetworkCapture;
import Model.NetworkCapture;
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXDrawer;
import com.jfoenix.controls.JFXHamburger;
import com.jfoenix.controls.JFXTextArea;
import com.jfoenix.transitions.hamburger.HamburgerBackArrowBasicTransition;
import com.nexmo.client.NexmoClient;
import com.nexmo.client.auth.AuthMethod;
import com.nexmo.client.auth.TokenAuthMethod;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import org.pcap4j.core.PcapAddress;
import org.pcap4j.core.PcapNetworkInterface;
import org.pcap4j.core.Pcaps;
import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;
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
    private Label totalPacketsLabel;

    @FXML
    private Label totalDroppedLabel;

    @FXML
    private Label totalAlertsLabel;

    @FXML
    private JFXTextArea loggingTextArea;

    private Scene myScene;
    public static AnchorPane rootP;
    private ContinuousNetworkCapture Ccapture;
    private String exportFilePath;
    private ScheduledFuture updateStatsFuture;
    private Runnable updateStats;
    private int Threshold;
    //Imported from previous screens
    private PcapNetworkInterface Odevice;
    private ScheduledExecutorService service;
    private NetworkCapture Ncapture;
    private PcapNetworkInterface device;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        hamburgerBar();
        AuthMethod auth = new TokenAuthMethod("6198ebad", "fSNE4MWRtzpNxeGV");
        NexmoClient client = new NexmoClient(auth);
    }

    @FXML
    public void stopCapture(ActionEvent event) {
        Ccapture.stopSniffing();
        updateStatsFuture.cancel(true);
        StopBtn.setDisable(true);
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getResource("CAContinuousCaptureLanding.fxml"));
        myScene = (Scene) ((Node) event.getSource()).getScene();
        Stage stage = (Stage) (myScene).getWindow();
        Parent nextView = null;
        try {
            nextView = loader.load();
            ControllerCAContinuousCaptureLanding controller = loader.<ControllerCAContinuousCaptureLanding>getController();
            controller.passVariables(device,service,Ncapture);
        } catch (IOException e) {
            e.printStackTrace();
        }
        stage.setScene(new Scene(nextView));
        stage.show();
    }

    public void startCapture(PcapNetworkInterface nif, ScheduledExecutorService service, NetworkCapture Capture, PcapNetworkInterface device, String filePath, String Threshold){
        this.Odevice = nif;
        this.service = service;
        this.Ncapture = Capture;
        this.device = device;
        this.exportFilePath = filePath;
        int Cthreshold;
        if (Threshold.equals("None")){
            Cthreshold = 0;
        }
        else {
            Cthreshold = Integer.parseInt(Threshold);
        }
        this.Ccapture = new ContinuousNetworkCapture(device,exportFilePath, Cthreshold);
        updateStats = new Runnable() {
            @Override
            public void run() {
                //Ccapture.printStat();
                Platform.runLater(() -> {
                    System.err.println("Running");
                    totalPacketsLabel.setText(Integer.toString(Ccapture.getPacketCount()));
                    //totalDroppedLabel.setText(Long.toString(Ccapture.getPacketsDropped()));
                });
            }
        };
        loggingTextArea.setText(Pcaps.libVersion()+"\n"+"Starting Capture on "+device.getName()+"\n");
        List<PcapAddress> addresses = device.getAddresses();
        for (PcapAddress p: addresses){
            loggingTextArea.setText(loggingTextArea.getText()+"IP Address Assigned on Interface: "+p.toString()+"\n");
        }
        updateStatsFuture = service.scheduleAtFixedRate(updateStats,2,1,TimeUnit.SECONDS);
        service.schedule(new Runnable() {
            @Override
            public void run() {
                Ccapture.startSniffing();
            }
        }, 2, TimeUnit.SECONDS);
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