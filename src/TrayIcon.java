import Model.NetworkCapture;
import Model.SMS;
import Model.ScheduledExecutorServiceHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.pcap4j.core.PcapNetworkInterface;

import javax.imageio.ImageIO;
import java.awt.*;
import java.io.IOException;
import java.io.InputStream;

public class TrayIcon {

    private static PcapNetworkInterface device;
    private static ScheduledExecutorServiceHandler handler;
    private static NetworkCapture capture;
    private static String directoryPath;
    private static Integer threshold;
    private static SMS SMSHandler;
    final SystemTray tray = SystemTray.getSystemTray();
    String TOOL_TIP = "FireE Tool Tip";
    String MESSAGE_HEADER = "FireE";
    java.awt.TrayIcon processTrayIcon = null;
    Stage primaryStage;

    public void getVariables(PcapNetworkInterface nif, ScheduledExecutorServiceHandler handler, NetworkCapture capture, String directoryPath, Integer threshold, SMS SMSHandler) {
        device = nif;
        TrayIcon.handler = handler;
        TrayIcon.capture = capture;
        TrayIcon.directoryPath = directoryPath;
        TrayIcon.threshold = threshold;
        TrayIcon.SMSHandler = SMSHandler;
    }

    private void createAndAddApplicationToSystemTray(Stage primaryStage) throws IOException {
        this.primaryStage = primaryStage;
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
        MenuItem MainScreen = new MenuItem("View Home Screen");
        MenuItem CapturePackets = new MenuItem("View Network Capture (Packets View)");
        MenuItem CaptureDashboard = new MenuItem("View Network Capture (Dashboard View)");
        MenuItem StopCapture = new MenuItem("Stop Network Capture");
        MenuItem Exit = new MenuItem("Exit FireE");
        popup.add(MainScreen);
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
        } catch (AWTException e) {
            System.err.println("TrayIcon could not be added.");
            return;
        }
        // Add listener to trayIcon.
        trayIcon.addActionListener(e -> {

        });
        MainScreen.addActionListener(e -> {
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(getClass().getResource("AdminHome.fxml"));
            Parent root = null;
            try {
                root = loader.load();
            } catch (IOException e2) {
                e2.printStackTrace();
            }
            Scene scene = new Scene(root, 1056, 600);
            scene.getStylesheets().add("Style.css");
            scene.getStylesheets().add("IntTreeTableViewStyle.css");
            primaryStage.setResizable(false);
            primaryStage.setScene(scene);
            primaryStage.getIcons().add(new javafx.scene.image.Image("FireIcon.png"));
            primaryStage.setTitle("Home Page");
            primaryStage.show();
        });
        CapturePackets.addActionListener(e -> {
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(getClass().getResource("CAMainPackets.fxml"));
            Parent root = null;
            try {
                root = loader.load();
                ControllerCAMainPackets ctrl = loader.getController();
                ctrl.passVariables(device, handler, capture, directoryPath, threshold, SMSHandler);
            } catch (IOException e3) {
                e3.printStackTrace();
            }
            Scene scene = new Scene(root, 1056, 600);
            scene.getStylesheets().add("Style.css");
            scene.getStylesheets().add("IntTreeTableViewStyle.css");
            primaryStage.setResizable(false);
            primaryStage.setScene(scene);
            primaryStage.getIcons().add(new javafx.scene.image.Image("FireIcon.png"));
            primaryStage.setTitle("Capture - Packets View");
            primaryStage.show();
        });
        CaptureDashboard.addActionListener(e -> {
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(getClass().getResource("CAMainDashboard.fxml"));
            Parent root = null;
            try {
                root = loader.load();
                ControllerCAMainDashboard ctrl = loader.getController();
                ctrl.passVariables(device, handler, capture, directoryPath, threshold, SMSHandler);
            } catch (IOException e4) {
                e4.printStackTrace();
            }
            Scene scene = new Scene(root, 1056, 600);
            scene.getStylesheets().add("Style.css");
            scene.getStylesheets().add("IntTreeTableViewStyle.css");
            primaryStage.setResizable(false);
            primaryStage.setScene(scene);
            primaryStage.getIcons().add(new javafx.scene.image.Image("FireIcon.png"));
            primaryStage.setTitle("Capture - Dashboard View");
            primaryStage.show();
        });
        StopCapture.addActionListener(e -> {

        });
        Exit.addActionListener(e -> {

        });
    }

    public void removeTrayicon() {
        tray.remove(processTrayIcon);
    }

}