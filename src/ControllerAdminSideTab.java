import Model.NetworkCapture;
import com.jfoenix.controls.JFXButton;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import org.pcap4j.core.PcapNetworkInterface;

import java.io.IOException;
import java.util.concurrent.ScheduledExecutorService;

public class ControllerAdminSideTab {

    @FXML
    private JFXButton listCreate;

    @FXML
    private JFXButton pktCapture;

    @FXML
    private JFXButton captureAnalysis;

    @FXML
    private JFXButton logsButton;

    @FXML
    private JFXButton continuousCapture;

    private Scene myScene;
    private static PcapNetworkInterface device;
    private static ScheduledExecutorService service;
    private static NetworkCapture capture;

    public void getVariables (PcapNetworkInterface nif, ScheduledExecutorService service, NetworkCapture capture) {
        this.device = nif;
        this.service = service;
        this.capture = capture;
    }

    @FXML
    void goToCaptureAnalysis(ActionEvent event) {
        if (device == null){
            FXMLLoader loader = new FXMLLoader(getClass().getResource("CALanding.fxml"));
            myScene = (Scene) ((Node) event.getSource()).getScene();
            Stage stage = (Stage) (myScene).getWindow();
            Parent nextView = null;
            try {
                nextView = loader.load();
                ControllerCALanding controller = loader.<ControllerCALanding>getController();
                controller.passVariables(service);
            } catch (IOException e) {
                e.printStackTrace();
            }
            stage.setScene(new Scene(nextView));
            stage.show();
        }
        else if (capture == null) {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("CAMainDashboard.fxml"));
            myScene = (Scene) ((Node) event.getSource()).getScene();
            Stage stage = (Stage) (myScene).getWindow();
            Parent nextView = null;
            try {
                nextView = loader.load();
                ControllerCAMainDashboard controller = loader.<ControllerCAMainDashboard>getController();
                controller.passVariables(device,service,null);
            } catch (IOException e) {
                e.printStackTrace();
            }
            stage.setScene(new Scene(nextView));
            stage.show();
        }
        else {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("CAMainDashboard.fxml"));
            myScene = (Scene) ((Node) event.getSource()).getScene();
            Stage stage = (Stage) (myScene).getWindow();
            Parent nextView = null;
            try {
                nextView = loader.load();
                ControllerCAMainDashboard controller = loader.<ControllerCAMainDashboard>getController();
                controller.passVariables(device,service,capture);
            } catch (IOException e) {
                e.printStackTrace();
            }
            stage.setScene(new Scene(nextView));
            stage.show();
        }
    }

    @FXML
    void goToPacketCapture(ActionEvent event) {
        if (device == null){
            FXMLLoader loader = new FXMLLoader(getClass().getResource("CALanding.fxml"));
            myScene = (Scene) ((Node) event.getSource()).getScene();
            Stage stage = (Stage) (myScene).getWindow();
            Parent nextView = null;
            try {
                nextView = loader.load();
                ControllerCALanding controller = loader.<ControllerCALanding>getController();
                controller.passVariables(service);
            } catch (IOException e) {
                e.printStackTrace();
            }
            stage.setScene(new Scene(nextView));
            stage.show();
        }
         else if (capture == null) {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("CAMainPackets.fxml"));
            myScene = (Scene) ((Node) event.getSource()).getScene();
            Stage stage = (Stage) (myScene).getWindow();
            Parent nextView = null;
            try {
                nextView = loader.load();
                ControllerCAMainPackets controller = loader.<ControllerCAMainPackets>getController();
                controller.passVariables(device,service,null);
            } catch (IOException e) {
                e.printStackTrace();
            }
            stage.setScene(new Scene(nextView));
            stage.show();
        }
        else {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("CAMainPackets.fxml"));
            myScene = (Scene) ((Node) event.getSource()).getScene();
            Stage stage = (Stage) (myScene).getWindow();
            Parent nextView = null;
            try {
                nextView = loader.load();
                ControllerCAMainPackets controller = loader.<ControllerCAMainPackets>getController();
                controller.passVariables(device,service,capture);
            } catch (IOException e) {
                e.printStackTrace();
            }
            stage.setScene(new Scene(nextView));
            stage.show();
        }
    }

    @FXML
    void onClickBuckets(MouseEvent event) {
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getResource("BucketsPage.fxml"));
        myScene = (Scene) ((Node) event.getSource()).getScene();
        Stage stage = (Stage) (myScene).getWindow();
        Parent nextView = null;
        try {
            nextView = loader.load();
        } catch (IOException e) {
            e.printStackTrace();
        }

        stage.setScene(new Scene(nextView));
        stage.setTitle("Buckets Page");
        stage.show();
    }

    @FXML
    void onClickLogs(MouseEvent event) {
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getResource("BucketsPage.fxml"));
        myScene = (Scene) ((Node) event.getSource()).getScene();
        Stage stage = (Stage) (myScene).getWindow();
        Parent nextView = null;
        try {
            nextView = loader.load();
        } catch (IOException e) {
            e.printStackTrace();
        }

        stage.setScene(new Scene(nextView));
        stage.setTitle("Logging Page");
        stage.show();
    }

    @FXML
    public void goToContiouousCapture(ActionEvent event) {
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getResource("CAContinuousCaptureLanding.fxml"));
        myScene = (Scene) ((Node) event.getSource()).getScene();
        Stage stage = (Stage) (myScene).getWindow();
        Parent nextView = null;
        try {
            nextView = loader.load();
            ControllerCAContinuousCaptureLanding controller = loader.<ControllerCAContinuousCaptureLanding>getController();
            controller.passVariables(device,service,capture);
        } catch (IOException e) {
            e.printStackTrace();
        }

        stage.setScene(new Scene(nextView));
        stage.show();
    }

}
