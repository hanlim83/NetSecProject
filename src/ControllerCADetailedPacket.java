import Model.CapturedPacket;
import Model.ContinuousNetworkCapture;
import Model.NetworkCapture;
import Model.ScheduledExecutorServiceHandler;
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXDrawer;
import com.jfoenix.controls.JFXHamburger;
import com.jfoenix.controls.JFXTreeTableView;
import com.jfoenix.transitions.hamburger.HamburgerBackArrowBasicTransition;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import org.pcap4j.core.PcapNetworkInterface;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.concurrent.ScheduledExecutorService;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ControllerCADetailedPacket implements Initializable {

    @FXML
    private AnchorPane anchorPane;
    @FXML
    private JFXHamburger hamburger;
    @FXML
    private JFXTreeTableView<?> packetDetailsTable;
    @FXML
    private JFXDrawer drawer;
    @FXML
    private JFXButton returnCaptureBtn;
    public static AnchorPane rootP;

    private ScheduledExecutorServiceHandler handler;
    private NetworkCapture Ncapture;
    private ContinuousNetworkCapture Ccapture;
    private PcapNetworkInterface device;
    private CapturedPacket packet;
    private Scene myScene;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        hamburgerBar();
    }

    public void passVariables(PcapNetworkInterface nif, ScheduledExecutorServiceHandler handler, NetworkCapture Ncapture, CapturedPacket packet, ContinuousNetworkCapture Ccapture){
        this.device = nif;
        this.handler = handler;
        this.Ncapture = Ncapture;
        this.Ccapture = Ccapture;
        this.packet = packet;
        try {
            FXMLLoader loader = new FXMLLoader();
            loader.load(getClass().getResource("AdminSideTab.fxml").openStream());
            ControllerAdminSideTab ctrl = loader.<ControllerAdminSideTab>getController();
            ctrl.getVariables(this.device,this.handler,this.Ncapture,this.Ccapture);
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
    void returntoCapture(ActionEvent event) {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("CAMainPackets.fxml"));
        myScene = anchorPane.getScene();
        Stage stage = (Stage) (myScene).getWindow();
        Parent nextView = null;
        try {
            nextView = loader.load();
            ControllerCAMainPackets controller = loader.<ControllerCAMainPackets>getController();
            controller.passVariables(device,handler,Ncapture,Ccapture);
        } catch (IOException e) {
            e.printStackTrace();
        }
        stage.setScene(new Scene(nextView));
        stage.setTitle("NSPJ");
        stage.show();
    }
}