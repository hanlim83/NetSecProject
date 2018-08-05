import Model.AWSSMS;
import Model.NetworkCapture;
import Model.OutlookEmail;
import Model.ScheduledThreadPoolExecutorHandler;
import com.jfoenix.controls.JFXButton;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import org.pcap4j.core.PcapNetworkInterface;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.util.Optional;
import java.util.ResourceBundle;

public class ControllerAdminSideTab implements Initializable {

    private static PcapNetworkInterface device;
    private static ScheduledThreadPoolExecutorHandler handler;
    private static NetworkCapture capture;
    private static boolean ARPDetection;
    private static Integer threshold;
    private static AWSSMS SMSHandler;
    private static OutlookEmail EmailHandler;
    private static boolean blockOtherPages = false;

    @FXML
    private JFXButton databasePage;
    @FXML
    private JFXButton logsButton;
    @FXML
    private JFXButton deviceButton;
    @FXML
    private JFXButton listCreate;
    @FXML
    private JFXButton FileExtensionManager;
    @FXML
    private JFXButton pktCapture;
    @FXML
    private JFXButton captureAnalysis;
    @FXML
    private JFXButton LogoutButton;
    private Scene myScene;

    public static AWSSMS getSMSHandler() {
        return SMSHandler;
    }

    public static PcapNetworkInterface getDevice() {
        return device;
    }

    public static ScheduledThreadPoolExecutorHandler getHandler() {
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

    public void getVariables(PcapNetworkInterface nif, ScheduledThreadPoolExecutorHandler handler, NetworkCapture capture, boolean ARPDetection, Integer threshold, AWSSMS SMSHandler, OutlookEmail EmailHandler) {
        device = nif;
        ControllerAdminSideTab.handler = handler;
        ControllerAdminSideTab.capture = capture;
        ControllerAdminSideTab.ARPDetection = ARPDetection;
        ControllerAdminSideTab.threshold = threshold;
        ControllerAdminSideTab.SMSHandler = SMSHandler;
        ControllerAdminSideTab.EmailHandler = EmailHandler;
    }

    public boolean checktokenFileExists() {
        if (new File(System.getProperty("user.home") + "\\" + ".store\\oauth2_sampleAdmin\\StoredCredential").exists()) {
            blockOtherPages = false;
            return true;
        } else {
            blockOtherPages = true;
            return false;
        }
    }

    public void blockedAccess(ActionEvent event) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Access Blocked");
        alert.setHeaderText("Access Blocked");
        alert.setContentText("You have minimized FireE and as a safety precaution, you need to sign in to FireE again to access other functions of the app. Do you want to log in again?");
        Optional<ButtonType> result = alert.showAndWait();
        if (result.get() == ButtonType.OK) {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("AdminLoginPage.fxml"));
            myScene = ((Node) event.getSource()).getScene();
            Stage stage = (Stage) (myScene).getWindow();
            Parent nextView = null;
            try {
                nextView = loader.load();
                ControllerAdminLoginPage controller = loader.getController();
            } catch (IOException e) {
                e.printStackTrace();
            }
            stage.setScene(new Scene(nextView));
            stage.setTitle("Login Page");
            stage.show();
        } else {
            // ... user chose CANCEL or closed the dialog
        }
    }

    public void blockedAccess(MouseEvent event) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Access Blocked");
        alert.setHeaderText("Access Blocked");
        alert.setContentText("You have minimized FireE and as a safety precaution, you need to sign in to FireE again to access other functions of the app. Do you want to log in again?");
        Optional<ButtonType> result = alert.showAndWait();
        if (result.get() == ButtonType.OK) {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("AdminLoginPage.fxml"));
            myScene = ((Node) event.getSource()).getScene();
            Stage stage = (Stage) (myScene).getWindow();
            Parent nextView = null;
            try {
                nextView = loader.load();
                ControllerAdminLoginPage controller = loader.getController();
            } catch (IOException e) {
                e.printStackTrace();
            }
            stage.setScene(new Scene(nextView));
            stage.setTitle("Login Page");
            stage.show();
        } else {
            // ... user chose CANCEL or closed the dialog
        }
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
    void onClickDatabase(MouseEvent event) {
        if (blockOtherPages)
            blockedAccess(event);
        else {
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
    }

    @FXML
    void onHover(MouseEvent event) {
        databasePage.setButtonType(JFXButton.ButtonType.RAISED);
    }

    @FXML
    void onHoverExit(MouseEvent event) {
        databasePage.setButtonType(JFXButton.ButtonType.FLAT);
    }

    @FXML
    void onClickLogs(MouseEvent event) {
        if (blockOtherPages)
            blockedAccess(event);
        else {
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
    }

    @FXML
    void onHover1(MouseEvent event) {
        logsButton.setButtonType(JFXButton.ButtonType.RAISED);
    }

    @FXML
    void onHoverExit1(MouseEvent event) {
        logsButton.setButtonType(JFXButton.ButtonType.FLAT);
    }

    @FXML
    void onClickDevice(MouseEvent event) {
        if (blockOtherPages)
            blockedAccess(event);
        else {
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
    }

    @FXML
    void onHover2(MouseEvent event) {
        deviceButton.setButtonType(JFXButton.ButtonType.RAISED);
    }

    @FXML
    void onHoverExit2(MouseEvent event) {
        deviceButton.setButtonType(JFXButton.ButtonType.FLAT);
    }

    @FXML
    void onClickBuckets(MouseEvent event) {
        if (blockOtherPages)
            blockedAccess(event);
        else {
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
    }

    @FXML
    void onHover3(MouseEvent event) {
        listCreate.setButtonType(JFXButton.ButtonType.RAISED);
    }

    @FXML
    void onHoverExit3(MouseEvent event) {
        listCreate.setButtonType(JFXButton.ButtonType.FLAT);
    }

    @FXML
    void goToFileExtensionManager(ActionEvent event) {
        if (blockOtherPages)
            blockedAccess(event);
        else {

            try {
                FXMLLoader loader = new FXMLLoader();
                loader.setLocation(getClass().getResource("AdminExtensionBlocker.fxml"));
                myScene = (Scene) ((Node) event.getSource()).getScene();
                Stage stage = (Stage) (myScene).getWindow();
                Parent nextView = null;
                nextView = loader.load();
                ControllerAdminExtensionBlocker controller = loader.<ControllerAdminExtensionBlocker>getController();
                //controller.passData(admin);

                stage.setScene(new Scene(nextView));
                stage.setTitle("NSPJ");
                stage.show();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @FXML
    void onHover4(MouseEvent event) {
        FileExtensionManager.setButtonType(JFXButton.ButtonType.RAISED);
    }

    @FXML
    void onHoverExit4(MouseEvent event) {
        FileExtensionManager.setButtonType(JFXButton.ButtonType.FLAT);
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
    void onHover5(MouseEvent event) {
        pktCapture.setButtonType(JFXButton.ButtonType.RAISED);
    }

    @FXML
    void onHoverExit5(MouseEvent event) {
        pktCapture.setButtonType(JFXButton.ButtonType.FLAT);
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
    void onHover6(MouseEvent event) {
        captureAnalysis.setButtonType(JFXButton.ButtonType.RAISED);
    }

    @FXML
    void onHoverExit6(MouseEvent event) {
        captureAnalysis.setButtonType(JFXButton.ButtonType.FLAT);
    }

    @FXML
    void onClickLogoutButton(ActionEvent event) {
        File file = new File(System.getProperty("user.home") + "\\" + ".store\\oauth2_sampleAdmin\\StoredCredential");
        file.delete();
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getResource("AdminLoginPage.fxml"));
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
    void onHover7(MouseEvent event) {
        LogoutButton.setButtonType(JFXButton.ButtonType.RAISED);
    }

    @FXML
    void onHoverExit7(MouseEvent event) {
        LogoutButton.setButtonType(JFXButton.ButtonType.FLAT);
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        Path path = FileSystems.getDefault().getPath("src/View/database-512.png");
        File file = new File(path.toUri());
        Image imageForFile;
        try {
            imageForFile = new Image(file.toURI().toURL().toExternalForm());
            ImageView imageView = new ImageView(imageForFile);
            imageView.setFitHeight(35);
            imageView.setFitWidth(35);
            databasePage.setGraphic(imageView);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

        Path path1 = FileSystems.getDefault().getPath("src/View/log-512.png");
        File file1 = new File(path1.toUri());
        Image imageForFile1;
        try {
            imageForFile1 = new Image(file1.toURI().toURL().toExternalForm());
            ImageView imageView = new ImageView(imageForFile1);
            imageView.setFitHeight(35);
            imageView.setFitWidth(35);
            logsButton.setGraphic(imageView);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        Path path2 = FileSystems.getDefault().getPath("src/View/sharp_desktop_windows_black_18dp.png");
        File file2 = new File(path2.toUri());
        Image imageForFile2;
        try {
            imageForFile2 = new Image(file2.toURI().toURL().toExternalForm());
            ImageView imageView = new ImageView(imageForFile2);
//            imageView.setFitHeight(24.5);
//            imageView.setFitWidth(35);
            deviceButton.setGraphic(imageView);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        Path path3 = FileSystems.getDefault().getPath("src/View/Bucket-512.png");
        File file3 = new File(path3.toUri());
        Image imageForFile3;
        try {
            imageForFile3 = new Image(file3.toURI().toURL().toExternalForm());
            ImageView imageView = new ImageView(imageForFile3);
            imageView.setFitHeight(35);
            imageView.setFitWidth(35);
            listCreate.setGraphic(imageView);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        Path path4 = FileSystems.getDefault().getPath("src/View/sharp_attachment_black_18dp.png");
        File file4 = new File(path4.toUri());
        Image imageForFile4;
        try {
            imageForFile4 = new Image(file4.toURI().toURL().toExternalForm());
            ImageView imageView = new ImageView(imageForFile4);
            FileExtensionManager.setGraphic(imageView);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        Path path5 = FileSystems.getDefault().getPath("src/View/Envelope-icon.png");
        File file5 = new File(path5.toUri());
        Image imageForFile5;
        try {
            imageForFile5 = new Image(file5.toURI().toURL().toExternalForm());
            ImageView imageView = new ImageView(imageForFile5);
            imageView.setFitHeight(35);
            imageView.setFitWidth(35);
            pktCapture.setGraphic(imageView);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        Path path6 = FileSystems.getDefault().getPath("src/View/BarRandom-512.png");
        File file6 = new File(path6.toUri());
        Image imageForFile6;
        try {
            imageForFile6 = new Image(file6.toURI().toURL().toExternalForm());
            ImageView imageView = new ImageView(imageForFile6);
            imageView.setFitHeight(35);
            imageView.setFitWidth(35);
            captureAnalysis.setGraphic(imageView);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        Path path7 = FileSystems.getDefault().getPath("src/View/baseline_exit_to_app_black_18dp.png");
        File file7 = new File(path7.toUri());
        Image imageForFile7;
        try {
            imageForFile7 = new Image(file7.toURI().toURL().toExternalForm());
            ImageView imageView = new ImageView(imageForFile7);
            LogoutButton.setGraphic(imageView);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
    }
}
