import com.jfoenix.animation.alert.JFXAlertAnimation;
import com.jfoenix.controls.JFXAlert;
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXDialogLayout;
import com.jfoenix.controls.JFXSpinner;
import javafx.application.Platform;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.sql.SQLException;
import java.util.ResourceBundle;
import java.util.Scanner;

public class ControllerAdminDeviceCheck implements Initializable {
    @FXML
    private AnchorPane anchorPane;

    @FXML
    private JFXButton RestartDeviceCheckButton;

    @FXML
    private JFXSpinner LoadingSpinner;

    private Scene myScene;

    public static AnchorPane rootP;
    private WindowsUtils utils = new WindowsUtils();

    private boolean AllFirewallStatus;
    private boolean WindowsStatus;
    private boolean WirelessEncryption;


    @Override
    public void initialize(URL location, ResourceBundle resources) {

    }

    @FXML
    void onClickRestartDeviceCheckButton(ActionEvent event) {
        process.start();
        LoadingSpinner.setVisible(true);
        RestartDeviceCheckButton.setDisable(true);
    }

    void runCheck(){
        process.start();
        process.setOnSucceeded(e -> {
            process.reset();
            if (!AllFirewallStatus){
                Process p;
                try {
                    p = Runtime.getRuntime().exec("C:\\Program Files\\Windows Defender\\MSASCui.exe");
                    p.waitFor();
                } catch (IOException | InterruptedException e1) {
                    e1.printStackTrace();
                }
                handleAlert("Please turn on your firewall and try again.");
            } else if(!WirelessEncryption) {
                handleAlert("Please connect to a more secure network. DO NOT use open networks.");
            }else if(!WindowsStatus){
                handleAlert("Your device version is not supported. Please update or use a device with a newer software.");
            } else{
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(getClass().getResource("AdminHome.fxml"));
            myScene = anchorPane.getScene();
            Stage stage = (Stage) (myScene).getWindow();
            Parent nextView = null;
            try {
                nextView = loader.load();
                ControllerAdminHome controller = loader.<ControllerAdminHome>getController();
            } catch (IOException u) {
                u.printStackTrace();
            }
            stage.setScene(new Scene(nextView));
            stage.setTitle("NSPJ");
            stage.show();
            }
        });
        process.setOnCancelled(e -> {
            LoadingSpinner.setVisible(false);
            RestartDeviceCheckButton.setVisible(true);
            RestartDeviceCheckButton.setDisable(false);
            Platform.runLater(() -> {
                myScene = anchorPane.getScene();
                Stage stage = (Stage) (myScene).getWindow();

                String title = "";
                String content = "An error occurred. Please try again later";

                JFXButton close = new JFXButton("Close");

                close.setButtonType(JFXButton.ButtonType.RAISED);

                close.setStyle("-fx-background-color: #00bfff;");

                JFXDialogLayout layout = new JFXDialogLayout();
                layout.setHeading(new Label(title));
                layout.setBody(new Label(content));
                layout.setActions(close);
                JFXAlert<Void> alert = new JFXAlert<>(stage);
                alert.setOverlayClose(true);
                alert.setAnimation(JFXAlertAnimation.CENTER_ANIMATION);
                alert.setContent(layout);
                alert.initModality(Modality.NONE);
                close.setOnAction(__ -> alert.hideWithAnimation());
                alert.show();
            });
            process.reset();
            System.out.println("Cancelled");
            RestartDeviceCheckButton.setVisible(true);
        });
        process.setOnFailed(e -> {
                    LoadingSpinner.setVisible(false);
                    RestartDeviceCheckButton.setVisible(true);
                    RestartDeviceCheckButton.setDisable(false);
                    Platform.runLater(() -> {
                                myScene = anchorPane.getScene();
                                Stage stage = (Stage) (myScene).getWindow();

                                String title = "";
                                String content = "An error occurred. Please try again later";

                                JFXButton close = new JFXButton("Close");

                                close.setButtonType(JFXButton.ButtonType.RAISED);

                                close.setStyle("-fx-background-color: #00bfff;");

                                JFXDialogLayout layout = new JFXDialogLayout();
                                layout.setHeading(new Label(title));
                                layout.setBody(new Label(content));
                                layout.setActions(close);
                                JFXAlert<Void> alert = new JFXAlert<>(stage);
                                alert.setOverlayClose(true);
                                alert.setAnimation(JFXAlertAnimation.CENTER_ANIMATION);
                                alert.setContent(layout);
                                alert.initModality(Modality.NONE);
                                close.setOnAction(__ -> alert.hideWithAnimation());
                                alert.show();
                            });
            process.reset();
            System.out.println("Failed");
            RestartDeviceCheckButton.setVisible(true);
        });
    }

    Service process = new Service() {
        @Override
        protected Task createTask() {
            return new Task() {
                @Override
                protected Void call() throws Exception {
                    localDeviceFirewallCheck();
                    checkWirelessConnectionEncryption();
                    checkWindowsApproved();
                    return null;
                }
            };
        }
    };


    private String DomainFirewall = null;
    private String PrivateFirewall = null;
    private String PublicFirewall = null;

    private void localDeviceFirewallCheck() throws IOException, InterruptedException {
        int count = 1;
        StringBuilder output = new StringBuilder();
//        String term="state";
        Process p = Runtime.getRuntime().exec("netsh advfirewall show allprofiles state");
        p.waitFor(); //Wait for the process to finish before continuing the Java program.

        BufferedReader reader = new BufferedReader(new InputStreamReader(p.getInputStream()));
        String line = "";
        while ((line = reader.readLine()) != null) {
            //Scanner s = new Scanner(line).useDelimiter();
            if (line.startsWith("State")) {
                Scanner s = new Scanner(line).useDelimiter("                                 ");
                String firstLine = s.next();
                String firewallStatus = s.next();
//                output.append(line + "\n");
                System.out.println(line);
                System.out.println(firewallStatus);
                //System.out.println("Delimit here next time");
                if (count == 1) {
                    DomainFirewall = firewallStatus;
                } else if (count == 2) {
                    PrivateFirewall = firewallStatus;
                } else if (count == 3) {
                    PublicFirewall = firewallStatus;
                }
                count++;
            }
            output.append(line + "\n");
        }

        System.out.println(output.toString());

        System.out.println(DomainFirewall);
        System.out.println(PrivateFirewall);
        System.out.println(PublicFirewall);

        if (!DomainFirewall.equals("ON")||!PrivateFirewall.equals("ON")||!PublicFirewall.equals("ON")){
            AllFirewallStatus=false;
        }else{
            AllFirewallStatus=true;
        }
    }

    private void checkWirelessConnectionEncryption() throws IOException, InterruptedException {
        Process p = Runtime.getRuntime().exec("netsh wlan show interfaces");
        p.waitFor();

        BufferedReader reader = new BufferedReader(new InputStreamReader(p.getInputStream()));
        String line;
        while ((line = reader.readLine()) != null) {
            System.out.println(line);
            if(line.contains("Authentication") && (line.contains("WPA2-Enterprise") || line.contains("WPA2-Personal"))){
                //set global variable
                WirelessEncryption=true;
                System.out.println("Wireless Secure!!!");
                break;
            }
            WirelessEncryption=false;
        }
    }

    private void checkWindowsApproved() throws SQLException {
        if (utils.checkWindowsApproved()==true) {
            System.out.println("SUPPORTED VERSION");
            WindowsStatus=true;
        }else{
            WindowsStatus=false;
            System.out.println("Not supported VERSION!");
        }
    }

    private void handleAlert(String message){
        Platform.runLater(() -> {
            LoadingSpinner.setVisible(false);
            RestartDeviceCheckButton.setVisible(true);
            RestartDeviceCheckButton.setDisable(false);
            Platform.runLater(() -> {
                myScene = anchorPane.getScene();
                Stage stage = (Stage) (myScene).getWindow();

                String title = "";
                String content = message;

                JFXButton close = new JFXButton("Close");

                close.setButtonType(JFXButton.ButtonType.RAISED);

                close.setStyle("-fx-background-color: #00bfff;");

                JFXDialogLayout layout = new JFXDialogLayout();
                layout.setHeading(new Label(title));
                layout.setBody(new Label(content));
                layout.setActions(close);
                JFXAlert<Void> alert = new JFXAlert<>(stage);
                alert.setOverlayClose(true);
                alert.setAnimation(JFXAlertAnimation.CENTER_ANIMATION);
                alert.setContent(layout);
                alert.initModality(Modality.NONE);
                close.setOnAction(__ -> alert.hideWithAnimation());
                alert.show();
            });
        });
    }
}
