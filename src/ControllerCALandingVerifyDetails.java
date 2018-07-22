import Model.SMS;
import Model.ScheduledExecutorServiceHandler;
import com.jfoenix.controls.*;
import com.jfoenix.transitions.hamburger.HamburgerBackArrowBasicTransition;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
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
import java.util.logging.Level;
import java.util.logging.Logger;

public class ControllerCALandingVerifyDetails implements Initializable {
    public static AnchorPane rootP;
    @FXML
    private AnchorPane anchorPane;

    @FXML
    private JFXHamburger hamburger;

    @FXML
    private JFXDrawer drawer;

    @FXML
    private JFXProgressBar progressBar;

    @FXML
    private JFXTextField chosenInterface;

    @FXML
    private JFXTextField chosenDirectory;

    @FXML
    private JFXTextField chosenThreshold;

    @FXML
    private JFXButton previousBtn;

    @FXML
    private JFXButton saveBtn;

    private PcapNetworkInterface device;
    private Scene myScene;
    private ScheduledExecutorServiceHandler handler;
    private String directoryPath;
    private int threshold;
    private SMS SMSHandler;
    private String intDisplayName;
    private boolean CaptureType;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        hamburgerBar();
    }

    public void passVariables(ScheduledExecutorServiceHandler handler, PcapNetworkInterface device, String directoryPath, Integer threshold, SMS SMSHandler, String intDisplayName, boolean CaptureType) {
        this.handler = handler;
        this.device = device;
        this.directoryPath = directoryPath;
        this.threshold = threshold;
        this.SMSHandler = SMSHandler;
        this.CaptureType = CaptureType;
        chosenInterface.setText(intDisplayName);
//        chosenInterface.setText(device.getName() + " (" + this.intDiplayName + ")");
        chosenDirectory.setText(directoryPath);
        if (threshold == 0)
            chosenThreshold.setText("None");
        else
            chosenThreshold.setText(Integer.toString(threshold));
        this.intDisplayName = intDisplayName;
        try {
            FXMLLoader loader = new FXMLLoader();
            loader.load(getClass().getResource("AdminSideTab.fxml").openStream());
            ControllerAdminSideTab ctrl = loader.getController();
            ctrl.getVariables(this.device, this.handler, null, null, 0, this.SMSHandler);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    public void goBackPreviousScreen(ActionEvent event) {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("CALandingSetOptions.fxml"));
        myScene = anchorPane.getScene();
        Stage stage = (Stage) (myScene).getWindow();
        Parent nextView = null;
        try {
            nextView = loader.load();
            ControllerCALandingSetOptions controller = loader.getController();
            controller.passVariables(handler, device, directoryPath, threshold, SMSHandler, intDisplayName, CaptureType);
        } catch (IOException e) {
            e.printStackTrace();
        }
        stage.setScene(new Scene(nextView));
        stage.setTitle("Set Options");
        stage.show();
    }

    @FXML
    public void transferToCaptureScreen(ActionEvent event) {
        if (CaptureType) {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("CAMainPackets.fxml"));
            myScene = anchorPane.getScene();
            Stage stage = (Stage) (myScene).getWindow();
            Parent nextView = null;
            try {
                nextView = loader.load();
                ControllerCAMainPackets controller = loader.getController();
                controller.passVariables(device, handler, null, directoryPath, threshold, SMSHandler);
            } catch (IOException e) {
                e.printStackTrace();
            }
            stage.setScene(new Scene(nextView));
            stage.setTitle("Capture - Packets View");
            stage.show();
        } else {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("CAMainDashboard.fxml"));
            myScene = ((Node) event.getSource()).getScene();
            Stage stage = (Stage) (myScene).getWindow();
            Parent nextView = null;
            try {
                nextView = loader.load();
                ControllerCAMainDashboard controller = loader.getController();
                controller.passVariables(device, handler, null, directoryPath, threshold, SMSHandler);
            } catch (IOException e) {
                e.printStackTrace();
            }
            stage.setScene(new Scene(nextView));
            stage.setTitle("Capture - Statistics View");
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
}

