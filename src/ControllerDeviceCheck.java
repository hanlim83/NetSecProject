import com.jfoenix.animation.alert.JFXAlertAnimation;
import com.jfoenix.controls.*;
import com.jfoenix.transitions.hamburger.HamburgerBackArrowBasicTransition;
import javafx.application.Platform;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.UnknownHostException;
import java.sql.SQLException;
import java.util.ResourceBundle;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ControllerDeviceCheck implements Initializable {
    @FXML
    private AnchorPane anchorPane;

    @FXML
    private JFXButton tempButton;

    @FXML
    private JFXButton RestartDeviceCheckButton;

    @FXML
    private JFXSpinner LoadingSpinner;

    private Scene myScene;

    public static AnchorPane rootP;
    private WindowsUtils utils = new WindowsUtils();

    private boolean AllFirewallStatus;

    @Override
    public void initialize(URL location, ResourceBundle resources) {

    }

    @FXML
    void onClickRestartDeviceCheckButton(ActionEvent event) {
        process.start();
        LoadingSpinner.setVisible(true);
        RestartDeviceCheckButton.setDisable(true);
    }

    //TODO
    //Remove this in the future
    @FXML
    void onClickTempButton(ActionEvent event) throws IOException, SQLException, InterruptedException {
        runCheck();
//        FXMLLoader loader = new FXMLLoader();
//        loader.setLocation(getClass().getResource("UserHome.fxml"));
//        myScene = anchorPane.getScene();
//        Stage stage = (Stage) (myScene).getWindow();
//        Parent nextView = loader.load();
//
//        //Will change to Device Checking Page next time
//        ControllerUserHome controller = loader.<ControllerUserHome>getController();
//        //controller.passData(login.getEmail());
//
//        stage.setScene(new Scene(nextView));
//        stage.setTitle("NSPJ");
//        stage.show();
    }

    //TODO Include error handling for cloud next time
    public void runCheck() throws IOException, InterruptedException, SQLException {
        process.start();
        process.setOnSucceeded(e -> {
            process.reset();
            if (AllFirewallStatus==false){
                LoadingSpinner.setVisible(false);
                RestartDeviceCheckButton.setVisible(true);
                RestartDeviceCheckButton.setDisable(false);
                Platform.runLater(() -> {
                    myScene = anchorPane.getScene();
                    Stage stage = (Stage) (myScene).getWindow();

                    String title = "";
                    String content = "Please turn on your firewall and try again.";

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
            }else{
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(getClass().getResource("UserHome.fxml"));
            myScene = anchorPane.getScene();
            Stage stage = (Stage) (myScene).getWindow();
            Parent nextView = null;
            try {
                nextView = loader.load();
                ControllerUserHome controller = loader.<ControllerUserHome>getController();
            } catch (IOException u) {
                u.printStackTrace();
            }
            stage.setScene(new Scene(nextView));
            stage.setTitle("NSPJ");
            stage.show();
            }
        });
        process.setOnCancelled(e -> {
            process.reset();
            System.out.println("Cancelled");
            RestartDeviceCheckButton.setVisible(true);
        });
        process.setOnFailed(e -> {
            LoadingSpinner.setVisible(false);
            RestartDeviceCheckButton.setVisible(true);
            RestartDeviceCheckButton.setDisable(false);
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
                    //Error when connecting to cloud. need to handle all errors, and also the button to force reset and stuff after a certain amount of time
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

//        Scanner s = new Scanner(osName).useDelimiter("                ");
//        String firstLine=s.next();
//        String osBuildNoStr=s.next();
//        //System.out.println("OS version is " + osName);
//        Scanner sc = new Scanner(osBuildNoStr).useDelimiter(" ");
//        String osBuildNo=sc.next();
//        System.out.println(osBuildNo);

        System.out.println(output.toString());
        //Desktop.getDesktop().open(new File("C://"));
//        Runtime.getRuntime().exec("explorer.exe /select," + "C://");
        //output.toString() will contain the result of "netsh advfirewall show all profiles state"
        System.out.println(DomainFirewall);
        System.out.println(PrivateFirewall);
        System.out.println(PublicFirewall);

        if (!DomainFirewall.equals("ON")||!PrivateFirewall.equals("ON")||!PublicFirewall.equals("ON")){
            AllFirewallStatus=false;
        }else{
            AllFirewallStatus=true;
        }


    }

    private void checkWindowsApproved() throws SQLException {
        if (utils.checkWindowsApproved()==true) {
//            FXMLLoader loader = new FXMLLoader();
//            loader.setLocation(getClass().getResource("UserHome.fxml"));
//            myScene = (Scene) ((Node) event.getSource()).getScene();
//            Stage stage = (Stage) (myScene).getWindow();
//            Parent nextView = loader.load();
//
//            //Will change to Device Checking Page next time
//            ControllerUserHome controller = loader.<ControllerUserHome>getController();
//            //controller.passData(login.getEmail());
//
//            stage.setScene(new Scene(nextView));
//            stage.setTitle("NSPJ");
//            stage.show();
            System.out.println("SUPPORTED VERSION");

//            FXMLLoader loader = new FXMLLoader();
//            loader.setLocation(getClass().getResource("UserHome.fxml"));
//            myScene = anchorPane.getScene();
//            Stage stage = (Stage) (myScene).getWindow();
//            Parent nextView = null;
//            try {
//                nextView = loader.load();
//                ControllerUserHome controller = loader.<ControllerUserHome>getController();
//            } catch (IOException u) {
//                u.printStackTrace();
//            }
//            stage.setScene(new Scene(nextView));
//            stage.setTitle("NSPJ");
//            stage.show();
        }else{
            System.out.println("Not supported VERSION!");
        }
    }
}
