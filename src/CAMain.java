import Model.NetworkCapture;
import Model.SMS;
import Model.ScheduledExecutorServiceHandler;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import org.apache.commons.io.FileUtils;
import org.pcap4j.core.PcapNetworkInterface;

import java.io.File;
import java.io.IOException;
import java.util.Optional;

public class CAMain extends Application {
    private ScheduledExecutorServiceHandler handler;
    private NetworkCapture capture = null;
    private PcapNetworkInterface device = null;
    private String directoryPath = null;
    private Integer threshold = null;
    private SMS SMSHandler = null;
    private static Stage primaryStage;
    private Scene myScene;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        CAMain.primaryStage = primaryStage;
        try {
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(getClass().getResource("CALandingSelectInt.fxml"));
            System.out.println(getClass().getResource("CALandingSelectInt.fxml"));
            Parent root = loader.load();
            ControllerCALandingSelectInt controller = loader.getController();
            handler = new ScheduledExecutorServiceHandler();
            controller.passVariables(handler, null, null, 0, null, null, true);
            Scene scene = new Scene(root, 1056, 600);
            String css = this.getClass().getResource("IntTreeTableViewStyle.css").toExternalForm();
            scene.getStylesheets().add(css);
            primaryStage.setResizable(false);
            primaryStage.setScene(scene);
            primaryStage.getIcons().add(new Image("FireIcon.png"));
            primaryStage.setTitle("Interface Selection");
            if (!new File("PcapExport").exists()) {
                FileUtils.forceMkdir(new File("PcapExport"));
            }
            primaryStage.setOnHiding(new EventHandler<WindowEvent>() {
                @Override
                public void handle(WindowEvent event) {
                    if (handler.getTableviewRunnable() != null)
                        handler.cancelTableviewRunnable();
                    if (handler.getchartDataRunnable() != null)
                        handler.cancelchartDataRunnable();
                    if (handler.getshowDataRunnable() != null)
                        handler.cancelshowDataRunnable();
                    getVariables();
                    if (capture != null && capture.isRunning()) {
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
                            FXMLLoader loader = new FXMLLoader(getClass().getResource("CAMainPackets.fxml"));
                            myScene = primaryStage.getScene();
                            primaryStage.close();
                            Stage stage = new Stage();
                            Parent nextView = null;
                            try {
                                nextView = loader.load();
                                ControllerCAMainPackets controller = loader.getController();
                                controller.passVariables(device, handler, capture, directoryPath, threshold, SMSHandler);
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                            stage.setScene(new Scene(nextView));
                            stage.setTitle("Capture - Packets View");
                            stage.setOnHiding(new EventHandler<WindowEvent>() {
                                @Override
                                public void handle(WindowEvent event) {
                                    if (handler.getTableviewRunnable() != null)
                                        handler.cancelTableviewRunnable();
                                    if (handler.getchartDataRunnable() != null)
                                        handler.cancelchartDataRunnable();
                                    if (handler.getshowDataRunnable() != null)
                                        handler.cancelshowDataRunnable();
                                    getVariables();
                                    if (capture != null && capture.isRunning()) {
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
                                            FXMLLoader loader = new FXMLLoader(getClass().getResource("CAMainPackets.fxml"));
                                            myScene = primaryStage.getScene();
                                            primaryStage.close();
                                            Stage stage = new Stage();
                                            Parent nextView = null;
                                            try {
                                                nextView = loader.load();
                                                ControllerCAMainPackets controller = loader.getController();
                                                controller.passVariables(device, handler, capture, directoryPath, threshold, SMSHandler);
                                            } catch (IOException e) {
                                                e.printStackTrace();
                                            }
                                            stage.setScene(new Scene(nextView));
                                            stage.setTitle("Capture - Packets View");
                                            stage.show();
                                        } else {
                                            Alert alert1 = new Alert(Alert.AlertType.INFORMATION);
                                            alert1.setTitle("Information Dialog");
                                            alert1.setHeaderText("FireE will minimize to Tray Icon");
                                            alert1.setContentText("Fire will be minimize to tray icon. To manage FireE, please find the Tray Icon.");
                                            alert1.showAndWait();
                                            TrayIcon trayIcon = new TrayIcon();
                                            try {
                                                trayIcon.createAndAddApplicationToSystemTray();
                                                trayIcon.getVariables(device, handler, capture, directoryPath, threshold, SMSHandler);
                                            } catch (IOException e) {
                                                e.printStackTrace();
                                            }
                                        }
                                    } else {
                                        Platform.exit();
                                    }
                                }
                            });
                            stage.show();
                        } else {
                            Alert alert1 = new Alert(Alert.AlertType.INFORMATION);
                            alert1.setTitle("Information Dialog");
                            alert1.setHeaderText("FireE will minimize to Tray Icon");
                            alert1.setContentText("Fire will be minimize to tray icon. To manage FireE, please find the Tray Icon.");
                            alert1.showAndWait();
                            TrayIcon trayIcon = new TrayIcon();
                            try {
                                trayIcon.createAndAddApplicationToSystemTray();
                                trayIcon.getVariables(device, handler, capture, directoryPath, threshold, SMSHandler);
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                    else {
                        Platform.exit();
                    }
                }
            });
            Platform.setImplicitExit(false);
            primaryStage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void stop() {
            handler.shutdownService();
            if (!ScheduledExecutorServiceHandler.getService().isShutdown())
                handler.forceShutdownService();
            try {
                FileUtils.cleanDirectory(new File("PcapExport"));
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