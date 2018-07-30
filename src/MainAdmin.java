import Model.NetworkCapture;
import Model.SMS;
import Model.ScheduledExecutorServiceHandler;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import org.pcap4j.core.PcapNetworkInterface;

import java.io.IOException;
import java.util.Optional;

public class MainAdmin extends Application {
    ScheduledExecutorServiceHandler handler = new ScheduledExecutorServiceHandler();
    NetworkCapture capture = null;
    PcapNetworkInterface device = null;
    String directoryPath = null;
    Integer threshold = null;
    SMS SMSHandler = null;
    private Scene myScene;
    private static Stage primaryStage;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        MainAdmin.primaryStage = primaryStage;
        try {
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(getClass().getResource("AdminLoginPage.fxml"));
            Parent root = loader.load();
            Scene scene = new Scene(root, 1056, 600);
            scene.getStylesheets().add("Style.css");
            scene.getStylesheets().add("IntTreeTableViewStyle.css");
            loadAdminSideTabCtrl();
            MainAdmin.primaryStage.setResizable(false);
            MainAdmin.primaryStage.setScene(scene);
//            this.primaryStage.getIcons().add(new Image("FireIcon.png"));
            MainAdmin.primaryStage.setTitle("NSPJ");
            MainAdmin.primaryStage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void stop() {
//        File file = new File(System.getProperty("user.home") + "\\" + ".store\\oauth2_sample\\StoredCredential");
//        file.delete();
        getVariables();
        if (handler.getStatuscaptureRunnable()) {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Confirmation Dialog");
            alert.setHeaderText("Network Capture is still running");
            alert.setContentText("The network capture is still running, do you wish to stop the capture or keep the capture running?");

            ButtonType buttonTypeOne = new ButtonType("Cancel Capture");
            ButtonType buttonTypeTwo = new ButtonType("Cancel Capture but don't exit");
            ButtonType buttonTypeCancel = new ButtonType("Do not Exit", ButtonBar.ButtonData.CANCEL_CLOSE);

            alert.getButtonTypes().setAll(buttonTypeOne, buttonTypeTwo, buttonTypeCancel);

            Optional<ButtonType> result = alert.showAndWait();
            if (result.get() == buttonTypeOne) {
                capture.stopSniffing();
                if (handler.getStatuscaptureRunnable())
                    handler.cancelcaptureRunnable();

            } else if (result.get() == buttonTypeTwo) {
                FXMLLoader loader = new FXMLLoader();
                loader.setLocation(getClass().getResource("CAMainPackets.fxml"));
                Parent root = null;
                try {
                    root = loader.load();
                    ControllerCAMainPackets ctrl = loader.getController();
                    ctrl.passVariables(device, handler, capture, directoryPath, threshold, SMSHandler);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                Scene scene = new Scene(root, 1056, 600);
                scene.getStylesheets().add("Style.css");
                scene.getStylesheets().add("IntTreeTableViewStyle.css");
                primaryStage.setResizable(false);
                primaryStage.setScene(scene);
                primaryStage.getIcons().add(new Image("FireIcon.png"));
                primaryStage.setTitle("Capture - Packets View");
                primaryStage.show();
            } else {
                Alert alert1 = new Alert(Alert.AlertType.INFORMATION);
                alert1.setTitle("Information Dialog");
                alert1.setHeaderText("FireE will minimize to Tray Icon");
                alert1.setContentText("Fire will be minimize to tray icon. To manage FireE, please find the Tray Icon.");
                alert1.showAndWait();
            }
        } else {
            handler.shutdownService();
            if (!ScheduledExecutorServiceHandler.getService().isShutdown())
                handler.forceShutdownService();
        }
    }

    public void loadAdminSideTabCtrl() {
        try {
            FXMLLoader loader = new FXMLLoader();
            loader.load(getClass().getResource("AdminSideTab.fxml").openStream());
            ControllerAdminSideTab ctrl = loader.getController();
            ctrl.getVariables(this.device, this.handler, this.capture, this.directoryPath, this.threshold, this.SMSHandler);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void getVariables() {
        try {
            FXMLLoader loader = new FXMLLoader();
            loader.load(getClass().getResource("AdminSideTab.fxml").openStream());
            ControllerAdminSideTab ctrl = loader.getController();
            this.handler = ControllerAdminSideTab.getHandler();
            this.capture = ControllerAdminSideTab.getCapture();
            this.device = ControllerAdminSideTab.getDevice();
            this.directoryPath = ControllerAdminSideTab.getDirectoryPath();
            this.SMSHandler = ControllerAdminSideTab.getSMSHandler();
            this.threshold = ControllerAdminSideTab.getThreshold();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}