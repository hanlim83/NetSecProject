import Model.AWSSMS;
import Model.ExecutorServiceHandler;
import Model.NetworkCapture;
import Model.OutlookEmail;
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
    private static ExecutorServiceHandler handler;
    private static NetworkCapture capture;
    private static boolean ARPDetection;
    private static Integer threshold;
    private static AWSSMS SMSHandler;
    private static OutlookEmail EmailHandler;

    @FXML
    private JFXButton databasePage;
    @FXML
    private JFXButton listCreate;
    @FXML
    private JFXButton pktCapture;
    @FXML
    private JFXButton captureAnalysis;
    @FXML
    private JFXButton logsButton;
    @FXML
    private JFXButton deviceButton;
    @FXML
    private JFXButton FileExtensionManager;
    private Scene myScene;

    public static AWSSMS getSMSHandler() {
        return SMSHandler;
    }

    public static PcapNetworkInterface getDevice() {
        return device;
    }

    public static ExecutorServiceHandler getHandler() {
        return handler;
    }

    public static NetworkCapture getCapture() {
        return capture;
    }

    public static boolean getARPDetection() {
        return ARPDetection;
    }

    public static Integer getThreshold() {
        return threshold;
    }

    public static OutlookEmail getEmailHandler() {
        return EmailHandler;
    }

    public void getVariables(PcapNetworkInterface nif, ExecutorServiceHandler handler, NetworkCapture capture, boolean ARPDetection, Integer threshold, AWSSMS SMSHandler, OutlookEmail EmailHandler) {
        device = nif;
        ControllerAdminSideTab.handler = handler;
        ControllerAdminSideTab.capture = capture;
        ControllerAdminSideTab.ARPDetection = ARPDetection;
        ControllerAdminSideTab.threshold = threshold;
        ControllerAdminSideTab.SMSHandler = SMSHandler;
        ControllerAdminSideTab.EmailHandler = EmailHandler;
    }

    public void StopTimeLineCtrl() {
        try {
            FXMLLoader loader = new FXMLLoader();
            loader.load(getClass().getResource("CAMainDashboard.fxml").openStream());
            ControllerCAMainDashboard ctrl = loader.getController();
            ctrl.TimelineCtrl(false);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    void goToCaptureAnalysis(ActionEvent event) {
        if (handler.getTableviewRunnable() != null && handler.getStatusTableviewRunnable())
            handler.cancelTableviewRunnable();
        if (handler.getchartDataRunnable() != null && handler.getStatuschartDataRunnable())
            handler.cancelchartDataRunnable();
        StopTimeLineCtrl();
        if (device == null) {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("CALandingSelectInt.fxml"));
            myScene = ((Node) event.getSource()).getScene();
            Stage stage = (Stage) (myScene).getWindow();
            Parent nextView = null;
            try {
                nextView = loader.load();
                ControllerCALandingSelectInt controller = loader.getController();
                controller.passVariables(handler, null, false, 0, SMSHandler, null, false, EmailHandler);
            } catch (IOException e) {
                e.printStackTrace();
            }
            stage.setScene(new Scene(nextView));
            stage.setTitle("Interface Selection");
            stage.show();
        } else if (capture == null) {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("CAMainDashboard.fxml"));
            myScene = ((Node) event.getSource()).getScene();
            Stage stage = (Stage) (myScene).getWindow();
            Parent nextView = null;
            try {
                nextView = loader.load();
                ControllerCAMainDashboard controller = loader.getController();
                controller.passVariables(device, handler, null, ARPDetection, threshold, SMSHandler, EmailHandler);
            } catch (IOException e) {
                e.printStackTrace();
            }
            stage.setScene(new Scene(nextView));
            stage.setTitle("Capture - Statistics View");
            stage.show();
        } else {
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
        }
    }

    @FXML
    void goToPacketCapture(ActionEvent event) {
        if (handler.getTableviewRunnable() != null && handler.getStatusTableviewRunnable())
            handler.cancelTableviewRunnable();
        if (handler.getchartDataRunnable() != null && handler.getStatuschartDataRunnable())
            handler.cancelchartDataRunnable();
        StopTimeLineCtrl();
        if (device == null) {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("CALandingSelectInt.fxml"));
            myScene = ((Node) event.getSource()).getScene();
            Stage stage = (Stage) (myScene).getWindow();
            Parent nextView = null;
            try {
                nextView = loader.load();
                ControllerCALandingSelectInt controller = loader.getController();
                controller.passVariables(handler, null, false, 0, SMSHandler, null, true, EmailHandler);
            } catch (IOException e) {
                e.printStackTrace();
            }
            stage.setScene(new Scene(nextView));
            stage.setTitle("Interface Selection");
            stage.show();
        } else if (capture == null) {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("CAMainPackets.fxml"));
            myScene = ((Node) event.getSource()).getScene();
            Stage stage = (Stage) (myScene).getWindow();
            Parent nextView = null;
            try {
                nextView = loader.load();
                ControllerCAMainPackets controller = loader.getController();
                controller.passVariables(device, handler, null, ARPDetection, threshold, SMSHandler, EmailHandler);
            } catch (IOException e) {
                e.printStackTrace();
            }
            stage.setScene(new Scene(nextView));
            stage.setTitle("Capture - Packets View");
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
            stage.setTitle("Capture - Packets View");
            stage.show();
        }
    }

    @FXML
    void onClickBuckets(MouseEvent event) {
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getResource("BucketsPage.fxml"));
        myScene = ((Node) event.getSource()).getScene();
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
        myScene = ((Node) event.getSource()).getScene();
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
    void onClickDatabase(MouseEvent event) {
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getResource("EmployeePage.fxml"));
        myScene = ((Node) event.getSource()).getScene();
        Stage stage = (Stage) (myScene).getWindow();
        Parent nextView = null;
        try {
            nextView = loader.load();
        } catch (IOException e) {
            e.printStackTrace();
        }

        stage.setScene(new Scene(nextView));
        stage.setTitle("Database Page");
        stage.show();
    }

    @FXML
    void onClickDevice(MouseEvent event) {
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getResource("DeviceDatabasePage.fxml"));
        myScene = ((Node) event.getSource()).getScene();
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
    void goToFileExtensionManager(ActionEvent event) {

    }
}
