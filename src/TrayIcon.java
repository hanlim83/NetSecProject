import Model.AWSSMS;
import Model.NetworkCapture;
import Model.OutlookEmail;
import Model.ScheduledThreadPoolExecutorHandler;
import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import org.pcap4j.core.PcapNetworkInterface;

import javax.imageio.ImageIO;
import java.awt.*;
import java.io.IOException;
import java.io.InputStream;
import java.util.ConcurrentModificationException;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

public class TrayIcon {

    private static PcapNetworkInterface device;
    private static ScheduledThreadPoolExecutorHandler handler;
    private static NetworkCapture capture;
    private static boolean ARPDetection;
    private static Integer threshold;
    private static AWSSMS SMSHandler;
    private static OutlookEmail EmailHandler;
    final SystemTray tray = SystemTray.getSystemTray();
    String TOOL_TIP = "FireE Tool Tip";
    java.awt.TrayIcon processTrayIcon = null;
    private static boolean running = false;
    private Stage primaryStage;
    private Scene myScene;
    private backgroundChecking bgC;

    public void getVariables(PcapNetworkInterface nif, ScheduledThreadPoolExecutorHandler handler, NetworkCapture capture, boolean ARPDetection, Integer threshold, AWSSMS SMSHandler, OutlookEmail EmailHandler) {
        device = nif;
        TrayIcon.handler = handler;
        TrayIcon.capture = capture;
        TrayIcon.ARPDetection = ARPDetection;
        TrayIcon.threshold = threshold;
        TrayIcon.SMSHandler = SMSHandler;
        TrayIcon.EmailHandler = EmailHandler;
    }

    public void startBackgroundCheck() {
        bgC = new backgroundChecking();
        handler.setbackgroundRunnable(ScheduledThreadPoolExecutorHandler.getService().scheduleAtFixedRate(bgC, 2, 4, TimeUnit.SECONDS));
    }

    public void resetPrimaryStage() {
        this.primaryStage = new Stage();
    }

    public void createAndAddApplicationToSystemTray() throws IOException {
        if (running == false) {
            primaryStage = new Stage();
            // Check the SystemTray support
            if (!SystemTray.isSupported()) {
                System.err.println("SystemTray is not supported");
                return;
            }
            final PopupMenu popup = new PopupMenu();
            ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
            InputStream inputStream = classLoader.getResourceAsStream("FireIcon.png");
            Image img = ImageIO.read(inputStream);
            final java.awt.TrayIcon trayIcon = new java.awt.TrayIcon(img, TOOL_TIP);
            this.processTrayIcon = trayIcon;
            MenuItem alertsPage = new MenuItem("Show Alerts Generated");
            MenuItem CapturePackets = new MenuItem("View Network Capture (Packets View)");
            MenuItem CaptureDashboard = new MenuItem("View Network Capture (Dashboard View)");
            MenuItem StopCapture = new MenuItem("Stop Network Capture");
            MenuItem selectInt = new MenuItem("Clear Capture and select new Interface");
            MenuItem Exit = new MenuItem("Exit FireE");
            popup.add(alertsPage);
            popup.addSeparator();
            popup.add(CapturePackets);
            popup.add(CaptureDashboard);
            popup.addSeparator();
            popup.add(StopCapture);
            popup.add(Exit);
            trayIcon.setPopupMenu(popup);
            trayIcon.setImageAutoSize(true);
            try {
                tray.add(trayIcon);
                running = true;
            } catch (AWTException e) {
                System.err.println("TrayIcon could not be added.");
                return;
            }
            // Add listener to trayIcon.
            trayIcon.addActionListener(e -> {

            });
            alertsPage.addActionListener(e -> {
                FXMLLoader loader = new FXMLLoader();
                loader.setLocation(getClass().getResource("CAAlerts.fxml"));
                Parent root = null;
                try {
                    root = loader.load();
                    ControllerCAAlerts ctrl = loader.getController();
                    ctrl.passVariables(device, handler, capture, ARPDetection, threshold, SMSHandler, EmailHandler);
                } catch (IOException e3) {
                    e3.printStackTrace();
                }
                Scene scene = new Scene(root, 1056, 600);
                scene.getStylesheets().add("Style.css");
                scene.getStylesheets().add("IntTreeTableViewStyle.css");
                Platform.runLater(new Runnable() {
                    @Override
                    public void run() {
                        primaryStage.setResizable(false);
                        primaryStage.setScene(scene);
                        primaryStage.getIcons().add(new javafx.scene.image.Image("FireIcon.png"));
                        primaryStage.setTitle("Alerts Page");
                        primaryStage.setOnHiding(new EventHandler<WindowEvent>() {
                            @Override
                            public void handle(WindowEvent event) {
                                if (handler.getTableviewRunnable() != null)
                                    handler.cancelTableviewRunnable();
                                if (handler.getchartDataRunnable() != null)
                                    handler.cancelchartDataRunnable();
                                try {
                                    FXMLLoader loader = new FXMLLoader();
                                    loader.load(getClass().getResource("AdminSideTab.fxml").openStream());
                                    ControllerAdminSideTab ctrl = loader.getController();
                                    handler = ControllerAdminSideTab.getHandler();
                                    capture = ControllerAdminSideTab.getCapture();
                                    device = ControllerAdminSideTab.getDevice();
                                    ARPDetection = ControllerAdminSideTab.getARPDetection();
                                    SMSHandler = ControllerAdminSideTab.getSMSHandler();
                                    threshold = ControllerAdminSideTab.getThreshold();
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                                if (capture != null && capture.isRunning()) {
                                    Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                                    alert.setTitle("Confirmation Dialog");
                                    alert.setHeaderText("Network Capture is still running");
                                    alert.setContentText("The network capture is still running, do you wish to stop the capture or keep the capture running?");
                                    ButtonType buttonTypeOne = new ButtonType("Cancel Capture");
                                    ButtonType buttonTypeTwo = new ButtonType("Cancel Capture but don't exit");
                                    ButtonType buttonTypeCancel = new ButtonType("Continue Capture and Minimize", ButtonBar.ButtonData.CANCEL_CLOSE);
                                    alert.getButtonTypes().setAll(buttonTypeOne, buttonTypeTwo, buttonTypeCancel);

                                    Optional<ButtonType> result = alert.showAndWait();
                                    if (result.get() == buttonTypeOne) {
                                        capture.stopSniffing();
                                        capture.Generalexport();
                                        EmailHandler.sendFullPcap(capture.getFullPcapExportPath());
                                        Platform.exit();

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
                                        handler.cancelbackgroundRunnable();
                                        TrayIcon trayIcon = new TrayIcon();
                                        try {
                                            trayIcon.createAndAddApplicationToSystemTray();
                                            startBackgroundCheck();
                                        } catch (IOException e) {
                                            e.printStackTrace();
                                        }
                                    }
                                } else {
                                    Platform.exit();
                                }
                            }
                        });
                        primaryStage.show();
                        tray.remove(trayIcon);
                        running = false;
                        resetPrimaryStage();
                    }
                });
            });
            CapturePackets.addActionListener(e -> {
                FXMLLoader loader = new FXMLLoader();
                loader.setLocation(getClass().getResource("CAMainPackets.fxml"));
                Parent root = null;
                try {
                    root = loader.load();
                    ControllerCAMainPackets ctrl = loader.getController();
                    ctrl.passVariables(device, handler, capture, ARPDetection, threshold, SMSHandler, EmailHandler);
                } catch (IOException e3) {
                    e3.printStackTrace();
                }
                Scene scene = new Scene(root, 1056, 600);
                scene.getStylesheets().add("Style.css");
                scene.getStylesheets().add("IntTreeTableViewStyle.css");
                Platform.runLater(new Runnable() {
                    @Override
                    public void run() {
                        primaryStage.setResizable(false);
                        primaryStage.setScene(scene);
                        primaryStage.getIcons().add(new javafx.scene.image.Image("FireIcon.png"));
                        primaryStage.setTitle("Capture - Packets View");
                        primaryStage.setOnHiding(new EventHandler<WindowEvent>() {
                            @Override
                            public void handle(WindowEvent event) {
                                if (handler.getTableviewRunnable() != null)
                                    handler.cancelTableviewRunnable();
                                if (handler.getchartDataRunnable() != null)
                                    handler.cancelchartDataRunnable();
                                try {
                                    FXMLLoader loader = new FXMLLoader();
                                    loader.load(getClass().getResource("AdminSideTab.fxml").openStream());
                                    ControllerAdminSideTab ctrl = loader.getController();
                                    handler = ControllerAdminSideTab.getHandler();
                                    capture = ControllerAdminSideTab.getCapture();
                                    device = ControllerAdminSideTab.getDevice();
                                    ARPDetection = ControllerAdminSideTab.getARPDetection();
                                    SMSHandler = ControllerAdminSideTab.getSMSHandler();
                                    threshold = ControllerAdminSideTab.getThreshold();
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                                if (capture != null && capture.isRunning()) {
                                    Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                                    alert.setTitle("Confirmation Dialog");
                                    alert.setHeaderText("Network Capture is still running");
                                    alert.setContentText("The network capture is still running, do you wish to stop the capture or keep the capture running?");
                                    ButtonType buttonTypeOne = new ButtonType("Cancel Capture");
                                    ButtonType buttonTypeTwo = new ButtonType("Cancel Capture but don't exit");
                                    ButtonType buttonTypeCancel = new ButtonType("Continue Capture and Minimize", ButtonBar.ButtonData.CANCEL_CLOSE);
                                    alert.getButtonTypes().setAll(buttonTypeOne, buttonTypeTwo, buttonTypeCancel);

                                    Optional<ButtonType> result = alert.showAndWait();
                                    if (result.get() == buttonTypeOne) {
                                        capture.stopSniffing();
                                        capture.Generalexport();
                                        EmailHandler.sendFullPcap(capture.getFullPcapExportPath());
                                        Platform.exit();

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
                                        handler.cancelbackgroundRunnable();
                                        TrayIcon trayIcon = new TrayIcon();
                                        try {
                                            trayIcon.createAndAddApplicationToSystemTray();
                                            startBackgroundCheck();
                                        } catch (IOException e) {
                                            e.printStackTrace();
                                        }
                                    }
                                } else {
                                    Platform.exit();
                                }
                            }
                        });
                        primaryStage.show();
                        tray.remove(trayIcon);
                        running = false;
                        resetPrimaryStage();
                    }
                });
            });
            CaptureDashboard.addActionListener(e -> {
                FXMLLoader loader = new FXMLLoader();
                loader.setLocation(getClass().getResource("CAMainDashboard.fxml"));
                Parent root = null;
                try {
                    root = loader.load();
                    ControllerCAMainDashboard ctrl = loader.getController();
                    ctrl.passVariables(device, handler, capture, ARPDetection, threshold, SMSHandler, EmailHandler);
                } catch (IOException e4) {
                    e4.printStackTrace();
                }
                Scene scene = new Scene(root, 1056, 600);
                scene.getStylesheets().add("Style.css");
                scene.getStylesheets().add("IntTreeTableViewStyle.css");
                Platform.runLater(new Runnable() {
                    @Override
                    public void run() {
                        primaryStage.setResizable(false);
                        primaryStage.setScene(scene);
                        primaryStage.getIcons().add(new javafx.scene.image.Image("FireIcon.png"));
                        primaryStage.setTitle("Capture - Dashboard View");
                        primaryStage.setOnHiding(new EventHandler<WindowEvent>() {
                            @Override
                            public void handle(WindowEvent event) {
                                if (handler.getTableviewRunnable() != null)
                                    handler.cancelTableviewRunnable();
                                if (handler.getchartDataRunnable() != null)
                                    handler.cancelchartDataRunnable();
                                try {
                                    FXMLLoader loader = new FXMLLoader();
                                    loader.load(getClass().getResource("AdminSideTab.fxml").openStream());
                                    ControllerAdminSideTab ctrl = loader.getController();
                                    handler = ControllerAdminSideTab.getHandler();
                                    capture = ControllerAdminSideTab.getCapture();
                                    device = ControllerAdminSideTab.getDevice();
                                    ARPDetection = ControllerAdminSideTab.getARPDetection();
                                    SMSHandler = ControllerAdminSideTab.getSMSHandler();
                                    threshold = ControllerAdminSideTab.getThreshold();
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                                if (capture != null && capture.isRunning()) {
                                    Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                                    alert.setTitle("Confirmation Dialog");
                                    alert.setHeaderText("Network Capture is still running");
                                    alert.setContentText("The network capture is still running, do you wish to stop the capture or keep the capture running?");
                                    ButtonType buttonTypeOne = new ButtonType("Cancel Capture");
                                    ButtonType buttonTypeTwo = new ButtonType("Cancel Capture but don't exit");
                                    ButtonType buttonTypeCancel = new ButtonType("Continue Capture and Minimize", ButtonBar.ButtonData.CANCEL_CLOSE);
                                    alert.getButtonTypes().setAll(buttonTypeOne, buttonTypeTwo, buttonTypeCancel);

                                    Optional<ButtonType> result = alert.showAndWait();
                                    if (result.get() == buttonTypeOne) {
                                        capture.stopSniffing();
                                        capture.Generalexport();
                                        EmailHandler.sendFullPcap(capture.getFullPcapExportPath());
                                        Platform.exit();

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
                                        handler.cancelbackgroundRunnable();
                                        TrayIcon trayIcon = new TrayIcon();
                                        try {
                                            trayIcon.createAndAddApplicationToSystemTray();
                                            startBackgroundCheck();
                                        } catch (IOException e) {
                                            e.printStackTrace();
                                        }
                                    }
                                } else {
                                    Platform.exit();
                                }
                            }
                        });
                        primaryStage.show();
                        tray.remove(trayIcon);
                        running = false;
                        resetPrimaryStage();
                    }
                });
            });
            StopCapture.addActionListener(e -> {
                capture.stopSniffing();
                handler.cancelbackgroundRunnable();
                capture.printStat();
                trayIcon.displayMessage("Capture Statistics", "Packets Received By Interface: " + capture.getPacketsReceived() + "\nPackets Dropped By Interface: " + capture.getPacketsDroppedByInt() + "\nTotal Packets Captured: " + capture.getPacketCount() + "\nThe Network Capture File will be sent to your email shortly", java.awt.TrayIcon.MessageType.INFO);
                capture.Generalexport();
                ScheduledThreadPoolExecutorHandler.getService().execute(() -> {
                    EmailHandler.sendFullPcap(capture.getFullPcapExportPath());
                });
                try {
                    FXMLLoader loader = new FXMLLoader();
                    loader.load(getClass().getResource("AdminSideTab.fxml").openStream());
                    ControllerAdminSideTab ctrl = loader.getController();
                    ctrl.getVariables(device, handler, capture, ARPDetection, threshold, SMSHandler, EmailHandler);
                } catch (IOException e4) {
                    e4.printStackTrace();
                }
                popup.remove(StopCapture);
                popup.remove(Exit);
                popup.add(selectInt);
                popup.add(Exit);
                tray.remove(trayIcon);
                trayIcon.setPopupMenu(popup);
                try {
                    tray.add(trayIcon);
                } catch (AWTException e4) {
                    System.err.println("TrayIcon could not be added.");
                    return;
                }
            });
            selectInt.addActionListener(e -> {
                FXMLLoader loader = new FXMLLoader();
                loader.setLocation(getClass().getResource("CALandingSelectInt.fxml"));
                Parent root = null;
                try {
                    root = loader.load();
                    ControllerCALandingSelectInt ctrl = loader.getController();
                    ctrl.passVariables(handler, null, false, 0, SMSHandler, null, false, EmailHandler);
                } catch (IOException e4) {
                    e4.printStackTrace();
                }
                Scene scene = new Scene(root, 1056, 600);
                scene.getStylesheets().add("Style.css");
                scene.getStylesheets().add("IntTreeTableViewStyle.css");
                Platform.runLater(new Runnable() {
                    @Override
                    public void run() {
                        primaryStage.setResizable(false);
                        primaryStage.setScene(scene);
                        primaryStage.getIcons().add(new javafx.scene.image.Image("FireIcon.png"));
                        primaryStage.setTitle("Select Interface");
                        primaryStage.setOnHiding(new EventHandler<WindowEvent>() {
                            @Override
                            public void handle(WindowEvent event) {
                                if (handler.getTableviewRunnable() != null)
                                    handler.cancelTableviewRunnable();
                                if (handler.getchartDataRunnable() != null)
                                    handler.cancelchartDataRunnable();
                                try {
                                    FXMLLoader loader = new FXMLLoader();
                                    loader.load(getClass().getResource("AdminSideTab.fxml").openStream());
                                    ControllerAdminSideTab ctrl = loader.getController();
                                    handler = ControllerAdminSideTab.getHandler();
                                    capture = ControllerAdminSideTab.getCapture();
                                    device = ControllerAdminSideTab.getDevice();
                                    ARPDetection = ControllerAdminSideTab.getARPDetection();
                                    SMSHandler = ControllerAdminSideTab.getSMSHandler();
                                    threshold = ControllerAdminSideTab.getThreshold();
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                                if (capture != null && capture.isRunning()) {
                                    Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                                    alert.setTitle("Confirmation Dialog");
                                    alert.setHeaderText("Network Capture is still running");
                                    alert.setContentText("The network capture is still running, do you wish to stop the capture or keep the capture running?");
                                    ButtonType buttonTypeOne = new ButtonType("Cancel Capture");
                                    ButtonType buttonTypeTwo = new ButtonType("Cancel Capture but don't exit");
                                    ButtonType buttonTypeCancel = new ButtonType("Continue Capture and Minimize", ButtonBar.ButtonData.CANCEL_CLOSE);
                                    alert.getButtonTypes().setAll(buttonTypeOne, buttonTypeTwo, buttonTypeCancel);

                                    Optional<ButtonType> result = alert.showAndWait();
                                    if (result.get() == buttonTypeOne) {
                                        capture.stopSniffing();
                                        capture.Generalexport();
                                        EmailHandler.sendFullPcap(capture.getFullPcapExportPath());
                                        Platform.exit();

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
                                        handler.cancelbackgroundRunnable();
                                        TrayIcon trayIcon = new TrayIcon();
                                        try {
                                            trayIcon.createAndAddApplicationToSystemTray();
                                            startBackgroundCheck();
                                        } catch (IOException e) {
                                            e.printStackTrace();
                                        }
                                    }
                                } else {
                                    Platform.exit();
                                }
                            }
                        });
                        primaryStage.show();
                        tray.remove(trayIcon);
                        running = false;
                        resetPrimaryStage();
                    }
                });
            });
            Exit.addActionListener(e -> {
                if (capture != null && capture.isRunning()) {
                    Platform.runLater(new Runnable() {
                        @Override
                        public void run() {
                            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                            alert.setTitle("Confirmation Dialog");
                            alert.setHeaderText("Network Capture is still running");
                            alert.setContentText("The network capture is still running, do you wish to stop the capture and exit?");
                            ButtonType buttonTypeOne = new ButtonType("Cancel Capture and exit");
                            ButtonType buttonTypeCancel = new ButtonType("Continue Capture and Minimize", ButtonBar.ButtonData.CANCEL_CLOSE);
                            alert.getButtonTypes().setAll(buttonTypeOne, buttonTypeCancel);

                            Optional<ButtonType> result = alert.showAndWait();
                            if (result.get() == buttonTypeOne) {
                                capture.stopSniffing();
                                handler.cancelbackgroundRunnable();
                                capture.Generalexport();
                                EmailHandler.sendFullPcap(capture.getFullPcapExportPath());
                                Platform.exit();
                                removeTrayicon();
                            }
                        }
                    });
                } else {
                    Platform.exit();
                    removeTrayicon();
                }
            });
        } else
            System.err.println("Tray Icon Already Running");
    }

    public void removeTrayicon() {
        tray.remove(processTrayIcon);
        running = false;
    }

    private class backgroundChecking implements Runnable {
        @Override
        public void run() {
            try {
                capture.getTrafficPerSecond();
                if (capture.checkThreshold()) {
                    SMSHandler.sendAlert();
                    capture.Specficexport();
                    String pcapFilePath = capture.getSpecificPcapExportPath();
                    EmailHandler.sendParitalPcap(pcapFilePath);
                    capture.addAlert(false);
                    processTrayIcon.displayMessage("Network Traffic Exceeded Threshold", "Current network traffic has exceeded the threshold. A pcap file containing packets before the event has been generated for you and will be sent shortly via email.\nTo Open the alert dashboard, select the tray iocn and select view alerts generated.", java.awt.TrayIcon.MessageType.WARNING);
                } else if (capture.checkARP() && ARPDetection != false) {
                    SMSHandler.sendAlert();
                    capture.Specficexport();
                    String pcapFilePath = capture.getSpecificPcapExportPath();
                    EmailHandler.sendParitalPcap(pcapFilePath);
                    capture.addAlert(true);
                    processTrayIcon.displayMessage("Network Traffic Exceeded Threshold", "ARP Spoofing has been detected. A pcap file containing packets before the event has been generated for you and will be sent shortly via email.\nTo Open the alert dashboard, select the tray iocn and select view alerts generated.", java.awt.TrayIcon.MessageType.WARNING);
                }
            } catch (ConcurrentModificationException e) {
                capture.stopSniffing();
                capture.getTrafficPerSecond();
                if (capture.checkThreshold()) {
                    SMSHandler.sendAlert();
                    capture.Specficexport();
                    String pcapFilePath = capture.getSpecificPcapExportPath();
                    EmailHandler.sendParitalPcap(pcapFilePath);
                    capture.addAlert(false);
                    processTrayIcon.displayMessage("Network Traffic Exceeded Threshold", "Current network traffic has exceeded the threshold. A pcap file containing packets before the event has been generated for you and will be sent shortly via email.\nTo Open the alert dashboard, select the tray iocn and select view alerts generated.", java.awt.TrayIcon.MessageType.WARNING);
                } else if (capture.checkARP() && ARPDetection != false) {
                    SMSHandler.sendAlert();
                    capture.Specficexport();
                    String pcapFilePath = capture.getSpecificPcapExportPath();
                    EmailHandler.sendParitalPcap(pcapFilePath);
                    capture.addAlert(true);
                    processTrayIcon.displayMessage("Network Traffic Exceeded Threshold", "ARP Spoofing has been detected. A pcap file containing packets before the event has been generated for you and will be sent shortly via email.\nTo Open the alert dashboard, select the tray iocn and select view alerts generated.", java.awt.TrayIcon.MessageType.WARNING);
                }
                capture.startSniffing();
            }
        }
    }

}