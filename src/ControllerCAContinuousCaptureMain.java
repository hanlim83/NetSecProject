import Model.ContinuousNetworkCapture;
import Model.NetworkCapture;
import Model.ScheduledExecutorServiceHandler;
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
import java.util.logging.Level;
import java.util.logging.Logger;

import static java.util.concurrent.TimeUnit.SECONDS;

public class ControllerCAContinuousCaptureMain implements Initializable {

    public static AnchorPane rootP;
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
    private ContinuousNetworkCapture Ccapture;
    private String exportFilePath;
    private Runnable updateStats;
    private int Threshold;
    private NexmoClient client;
    //Imported from previous screens
    private PcapNetworkInterface Odevice;
    private ScheduledExecutorServiceHandler handler;
    private NetworkCapture Ncapture;
    private PcapNetworkInterface device;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        hamburgerBar();
        AuthMethod auth = new TokenAuthMethod("6198ebad", "fSNE4MWRtzpNxeGV");
        client = new NexmoClient(auth);
    }

    @FXML
    public void stopCapture(ActionEvent event) {
        Ccapture.stopSniffing();
        //handler.cancelUpdateStatsFuture();
        StopBtn.setDisable(true);
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getResource("CAContinuousCaptureLanding.fxml"));
        myScene = (Scene) ((Node) event.getSource()).getScene();
        Stage stage = (Stage) (myScene).getWindow();
        Parent nextView = null;
        try {
            nextView = loader.load();
            ControllerCAContinuousCaptureLanding controller = loader.<ControllerCAContinuousCaptureLanding>getController();
            controller.passVariables(device, handler, Ncapture);
        } catch (IOException e) {
            e.printStackTrace();
        }
        stage.setScene(new Scene(nextView));
        stage.show();
    }

    public void startCapture(PcapNetworkInterface nif, ScheduledExecutorServiceHandler handler, NetworkCapture Ncapture, PcapNetworkInterface device, String filePath, String Threshold, String PhoneNumber) {
        this.Odevice = nif;
        this.handler = handler;
        this.Ncapture = Ncapture;
        this.device = device;
        this.exportFilePath = filePath;
        int Cthreshold;
        if (Threshold.equals("None")) {
            Cthreshold = 0;
        } else {
            Cthreshold = Integer.parseInt(Threshold);
        }
        this.Ccapture = new ContinuousNetworkCapture(device, exportFilePath, Cthreshold);
        this.Ccapture.setPhoneNumber(PhoneNumber);
        updateStats = new Runnable() {
            @Override
            public void run() {
                Ccapture.printStat();
                if (Ccapture.checkThreshold())
                    sendAlert();
                try {
                Platform.runLater(() -> {
                    totalPacketsLabel.setText(Integer.toString(Ccapture.getPacketCount()));
                    totalDroppedLabel.setText(Long.toString(Ccapture.getPacketsDropped()));
                    totalAlertsLabel.setText(Integer.toString(Ccapture.getEvents()));
                });
            } catch (Exception e){
                    e.printStackTrace();
                }
            }
        };
        loggingTextArea.setText(Pcaps.libVersion() + "\n" + "Starting Capture on " + device.getName() + "\n");
        List<PcapAddress> addresses = device.getAddresses();
        for (PcapAddress p : addresses) {
            loggingTextArea.setText(loggingTextArea.getText() + "IP Address Assigned on Interface: " + p.toString() + "\n");
        }
        //handler.setUpdateStatsFuture(handler.getService().scheduleAtFixedRate(updateStats, 2, 1, SECONDS));
//        updateStatsFuture = service.scheduleAtFixedRate(updateStats,2,1,SECONDS);
        //handler.setCcaptureRunnable(handler.getService().schedule(new Runnable() {
            /*@Override
            public void run() {
                Ccapture.startSniffing();
            }
        }, 1, SECONDS));
        try {
            FXMLLoader loader = new FXMLLoader();
            loader.load(getClass().getResource("AdminSideTab.fxml").openStream());
            ControllerAdminSideTab ctrl = loader.<ControllerAdminSideTab>getController();
            //ctrl.getVariables(this.Odevice,this.handler,this.Ncapture,this.Ccapture);
        } catch (IOException e) {
            e.printStackTrace();
        }*/
    }

    public void passVariables(PcapNetworkInterface nif, ScheduledExecutorServiceHandler handler, NetworkCapture Ncapture, ContinuousNetworkCapture Ccapture) {
        this.Odevice = nif;
        this.handler = handler;
        this.Ncapture = Ncapture;
        this.Ccapture = Ccapture;
        updateStats = new Runnable() {
            @Override
            public void run() {
                Ccapture.printStat();
                Platform.runLater(() -> {
                    totalPacketsLabel.setText(Integer.toString(Ccapture.getPacketCount()));
                    totalDroppedLabel.setText(Long.toString(Ccapture.getPacketsDropped()));
                });
                if (Ccapture.checkThreshold()) {
                    sendAlert();
                    Platform.runLater(() -> {
                        totalAlertsLabel.setText(Integer.toString(Ccapture.getEvents()));
                    });
                }
            }
        };
        //handler.setUpdateStatsFuture(handler.getService().scheduleAtFixedRate(updateStats, 2, 1, SECONDS));
        try {
            FXMLLoader loader = new FXMLLoader();
            loader.load(getClass().getResource("AdminSideTab.fxml").openStream());
            ControllerAdminSideTab ctrl = loader.<ControllerAdminSideTab>getController();
           // ctrl.getVariables(this.Odevice,this.handler,this.Ncapture,this.Ccapture);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void sendAlert() {
        System.out.println("Called!");
        /*if (Ccapture.getPhoneNumber() != null) {
            SmsSubmissionResult[] responses = new SmsSubmissionResult[0];
            try {
                responses = client.getSmsClient().submitMessage(new TextMessage(
                        "FireE",
                        "65"+Ccapture.getPhoneNumber(),
                        "A spike of Network traffic activity has been detected! Please check the FireE Admin App for more information"));
            } catch (IOException e) {
                e.printStackTrace();
            } catch (NexmoClientException e) {
                e.printStackTrace();
            }
            for (SmsSubmissionResult response : responses) {
                System.out.println(response);
            }
        }*/
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