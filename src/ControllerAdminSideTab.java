import Model.ContinuousNetworkCapture;
import Model.NetworkCapture;
import Model.ScheduledExecutorServiceHandler;
import com.jfoenix.controls.JFXButton;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import org.pcap4j.core.PcapNetworkInterface;

import java.io.IOException;

public class ControllerAdminSideTab {

    private static PcapNetworkInterface device;
    private static ScheduledExecutorServiceHandler handler;
    private static NetworkCapture Ncapture;
    private static ContinuousNetworkCapture Ccapture;
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
    @FXML
    private JFXButton deviceButton;
    private Scene myScene;

    public void getVariables(PcapNetworkInterface nif, ScheduledExecutorServiceHandler handler, NetworkCapture Ncapture, ContinuousNetworkCapture Ccapture) {
        this.device = nif;
        this.handler = handler;
        this.Ncapture = Ncapture;
        this.Ccapture = Ccapture;
    }

    @FXML
    void goToCaptureAnalysis(ActionEvent event) {
        if (device == null) {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("CALanding.fxml"));
            myScene = (Scene) ((Node) event.getSource()).getScene();
            Stage stage = (Stage) (myScene).getWindow();
            Parent nextView = null;
            try {
                nextView = loader.load();
                ControllerCALanding controller = loader.<ControllerCALanding>getController();
                controller.passVariables(handler, Ccapture);
            } catch (IOException e) {
                e.printStackTrace();
            }
            stage.setScene(new Scene(nextView));
            stage.show();
        } else if (Ncapture == null) {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("CAMainDashboard.fxml"));
            myScene = (Scene) ((Node) event.getSource()).getScene();
            Stage stage = (Stage) (myScene).getWindow();
            Parent nextView = null;
            try {
                nextView = loader.load();
                ControllerCAMainDashboard controller = loader.<ControllerCAMainDashboard>getController();
                controller.passVariables(device, handler, null, Ccapture);
            } catch (IOException e) {
                e.printStackTrace();
            }
            stage.setScene(new Scene(nextView));
            stage.show();
        } else {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("CAMainDashboard.fxml"));
            myScene = (Scene) ((Node) event.getSource()).getScene();
            Stage stage = (Stage) (myScene).getWindow();
            Parent nextView = null;
            try {
                nextView = loader.load();
                ControllerCAMainDashboard controller = loader.<ControllerCAMainDashboard>getController();
                controller.passVariables(device, handler, Ncapture, Ccapture);
            } catch (IOException e) {
                e.printStackTrace();
            }
            stage.setScene(new Scene(nextView));
            stage.show();
        }
    }

    @FXML
    void goToPacketCapture(ActionEvent event) {
        if (handler.getTableviewRunnable() != null && handler.getStatusTableviewRunnable())
            handler.cancelTableviewRunnable();
        if (device == null) {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("CALanding.fxml"));
            myScene = (Scene) ((Node) event.getSource()).getScene();
            Stage stage = (Stage) (myScene).getWindow();
            Parent nextView = null;
            try {
                nextView = loader.load();
                ControllerCALanding controller = loader.<ControllerCALanding>getController();
                controller.passVariables(handler, Ccapture);
            } catch (IOException e) {
                e.printStackTrace();
            }
            stage.setScene(new Scene(nextView));
            stage.show();
        } else if (Ncapture == null) {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("CAMainPackets.fxml"));
            myScene = (Scene) ((Node) event.getSource()).getScene();
            Stage stage = (Stage) (myScene).getWindow();
            Parent nextView = null;
            try {
                nextView = loader.load();
                ControllerCAMainPackets controller = loader.<ControllerCAMainPackets>getController();
                controller.passVariables(device, handler, Ncapture, Ccapture);
            } catch (IOException e) {
                e.printStackTrace();
            }
            stage.setScene(new Scene(nextView));
            stage.show();
        } else {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("CAMainPackets.fxml"));
            myScene = (Scene) ((Node) event.getSource()).getScene();
            Stage stage = (Stage) (myScene).getWindow();
            Parent nextView = null;
            try {
                nextView = loader.load();
                ControllerCAMainPackets controller = loader.<ControllerCAMainPackets>getController();
                controller.passVariables(device, handler, Ncapture, Ccapture);
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
        loader.setLocation(getClass().getResource("LoggingPage.fxml"));
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
    void onClickDevice(MouseEvent event) {
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getResource("DeviceDatabasePage.fxml"));
        myScene = (Scene) ((Node) event.getSource()).getScene();
        Stage stage = (Stage) (myScene).getWindow();
        Parent nextView = null;
        try {
            nextView = loader.load();
        } catch (IOException e) {
            e.printStackTrace();
        }

        stage.setScene(new Scene(nextView));
        stage.setTitle("Device Build Number Page");
        stage.show();
    }

    @FXML
    public void goToContiouousCapture(ActionEvent event) {
        if (handler.getUpdateStatsFuture() != null && handler.getStatusUpdateStatsFuture())
            handler.cancelUpdateStatsFuture();
        if (Ccapture == null) {
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
        } else {
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(getClass().getResource("CAContinuousCaptureMain.fxml"));
            myScene = (Scene) ((Node) event.getSource()).getScene();
            Stage stage = (Stage) (myScene).getWindow();
            Parent nextView = null;
            try {
                nextView = loader.load();
                ControllerCAContinuousCaptureMain controller = loader.<ControllerCAContinuousCaptureMain>getController();
                controller.passVariables(device, handler, Ncapture, Ccapture);
            } catch (IOException e) {
                e.printStackTrace();
            }

            stage.setScene(new Scene(nextView));
            stage.show();
        }
    }


}
