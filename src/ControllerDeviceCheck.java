import com.jfoenix.animation.alert.JFXAlertAnimation;
import com.jfoenix.controls.*;
import com.jfoenix.transitions.hamburger.HamburgerBackArrowBasicTransition;
import javafx.application.Platform;
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

    private Scene myScene;

    public static AnchorPane rootP;
    WindowsUtils utils = new WindowsUtils();

    @Override
    public void initialize(URL location, ResourceBundle resources) {
//        try {
//            runCheck();
//        } catch (IOException e) {
//            e.printStackTrace();
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        } catch (SQLException e) {
//            e.printStackTrace();
//        }
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

    private void runCheck() throws IOException, InterruptedException, SQLException {
        localDeviceFirewallCheck();
        checkWindowsApproved();
    }

    String DomainFirewall = null;
    String PrivateFirewall = null;
    String PublicFirewall = null;

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

        Platform.runLater(() -> {
            if (DomainFirewall.equals("OFF") || PrivateFirewall.equals("OFF") || PublicFirewall.equals("OFF")) {
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
            }
        });
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
        }else{
            System.out.println("Not supported VERSION!");
        }
    }
}
