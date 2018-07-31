import Model.AWSSMS;
import Model.NetworkCapture;
import Model.OutlookEmail;
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

public class MainAdmin extends Application {
    private ScheduledExecutorServiceHandler handler = new ScheduledExecutorServiceHandler();
    private NetworkCapture capture = null;
    private PcapNetworkInterface device = null;
    private boolean ARPDetection = false;
    private Integer threshold = null;
    private AWSSMS SMSHandler = null;
    private OutlookEmail EmailHandler = null;
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
//            loader.setLocation(getClass().getResource("AdminLoginPage.fxml"));
            loader.setLocation(getClass().getResource("AdminHome.fxml"));
            Parent root = loader.load();
            Scene scene = new Scene(root, 1056, 600);
            scene.getStylesheets().add("Style.css");
            scene.getStylesheets().add("IntTreeTableViewStyle.css");
            loadAdminSideTabCtrl();
            if (!new File("PcapExport").exists()) {
                FileUtils.forceMkdir(new File("PcapExport"));
            }
            MainAdmin.primaryStage.setResizable(false);
            MainAdmin.primaryStage.setScene(scene);
            MainAdmin.primaryStage.getIcons().add(new Image("FireIcon.png"));
            MainAdmin.primaryStage.setTitle("NSPJ");
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
                                controller.passVariables(device, handler, capture, ARPDetection, threshold, SMSHandler, EmailHandler);
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
                                                controller.passVariables(device, handler, capture, ARPDetection, threshold, SMSHandler, EmailHandler);
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
                                                trayIcon.getVariables(device, handler, capture, ARPDetection, threshold, SMSHandler, EmailHandler);
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
                                trayIcon.getVariables(device, handler, capture, ARPDetection, threshold, SMSHandler, EmailHandler);
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    } else {
                        Platform.exit();
                    }
                }
            });
            Platform.setImplicitExit(false);
            MainAdmin.primaryStage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void stop() {
//        File file = new File(System.getProperty("user.home") + "\\" + ".store\\oauth2_sample\\StoredCredential");
//        file.delete();
        handler.shutdownService();
        if (!ScheduledExecutorServiceHandler.getService().isShutdown())
            handler.forceShutdownService();
        try {
            FileUtils.cleanDirectory(new File("PcapExport"));
        } catch (IOException e) {
            e.printStackTrace();
        }
        /*}*/
    }

    public void loadAdminSideTabCtrl() {
        try {
            FXMLLoader loader = new FXMLLoader();
            loader.load(getClass().getResource("AdminSideTab.fxml").openStream());
            ControllerAdminSideTab ctrl = loader.getController();
            ctrl.getVariables(this.device, this.handler, this.capture, this.ARPDetection, this.threshold, this.SMSHandler, EmailHandler);
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
            this.ARPDetection = ControllerAdminSideTab.getARPDetection();
            this.SMSHandler = ControllerAdminSideTab.getSMSHandler();
            this.threshold = ControllerAdminSideTab.getThreshold();
            this.EmailHandler = ControllerAdminSideTab.getEmailHandler();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

}