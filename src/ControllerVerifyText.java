import Model.TextAuthentication;
import com.jfoenix.animation.alert.JFXAlertAnimation;
import com.jfoenix.controls.*;
import com.nexmo.client.NexmoClient;
import com.nexmo.client.NexmoClientException;
import com.nexmo.client.auth.AuthMethod;
import com.nexmo.client.auth.TokenAuthMethod;
import com.nexmo.client.verify.CheckResult;
import com.nexmo.client.verify.VerifyResult;
import javafx.application.Platform;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.UnknownHostException;
import java.sql.SQLException;
import java.util.ResourceBundle;

//import Model.TextAuthentication.checkAuth;
//import Model.TextAuthentication.sendAuth;

public class ControllerVerifyText {

//    private static String getSendAuth;

    private Scene myScene;

    @FXML
    private AnchorPane anchorPane;

    @FXML
    private JFXHamburger hamburger;

    @FXML
    private JFXDrawer drawer;

    @FXML
    private JFXTextField verifyField;

    @FXML
    private Label viewerText;

    @FXML
    private Label numberViewer;

    @FXML
    private JFXButton verifyMe;

    @FXML
    private JFXButton resendText;

    private String phoneNo;

    void sendNew(String phoneNo){
        numberViewer.setText(phoneNo);
        this.phoneNo=phoneNo;
        process.start();
        process.setOnSucceeded(e -> {
            process.reset();
            Platform.runLater(() -> {
                JFXSnackbar snackbar = new JFXSnackbar(anchorPane);
                snackbar.show("OTP sent", 3000);
                snackbar.getStylesheets().add("Style.css");
            });
        });
        process.setOnCancelled(e -> {
            process.reset();
        });
        process.setOnFailed(e -> {
            process.reset();
        });

    }

    private Service process = new Service() {
        @Override
        protected Task createTask() {
            return new Task() {
                @Override
                protected Void call() throws Exception {
                    TextAuthentication auth = new TextAuthentication();
                    auth.sendAuth(phoneNo);
//                    Platform.runLater(() -> {
//
//                    });
                    System.out.println(" Correct phone number input");
                    return null;
                }
            };
        }
    };

    @FXML
    void resendText(ActionEvent event) throws MalformedURLException {

        String phoneNo = numberViewer.getText();
        TextAuthentication auth = new TextAuthentication();

        auth.sendNew();

    }

    @FXML
    void verifyConfirm(ActionEvent event) throws SQLException, InterruptedException, IOException, NexmoClientException {

        TextAuthentication textAuth = new TextAuthentication();

        System.out.println("\nCode input: " + verifyField.getText());

        boolean check = textAuth.checkAuth(verifyField.getText());

        if (check == true) {

            FXMLLoader loader = new FXMLLoader(getClass().getResource("DeviceCheck.fxml"));
            myScene = anchorPane.getScene();
            Stage stage = (Stage) (myScene).getWindow();
            Parent nextView = null;

            try {
                nextView = loader.load();
                ControllerDeviceCheck controller = loader.<ControllerDeviceCheck>getController();
                controller.runCheck();

            } catch (IOException u) {
                u.printStackTrace();
            }

            stage.setScene(new Scene(nextView));
            stage.setTitle("NSPJ");
            stage.show();

        } else {

            System.out.print("Sorry, wrong pin!");

        }

    }

        @FXML
        void backLogin (ActionEvent event) throws MalformedURLException {

        TextAuthentication auth = new TextAuthentication();
        auth.cancelAuth();

            File file= new File(System.getProperty("user.home")+"\\"+".store\\oauth2_sample\\StoredCredential");
            file.delete();

            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(getClass().getResource("LoginPage.fxml"));
            myScene = anchorPane.getScene();
            Stage stage = (Stage) (myScene).getWindow();
            Parent nextView = null;
            try {
                nextView = loader.load();
            } catch (IOException e) {
                e.printStackTrace();
            }

            ControllerLoginPage controller = loader.<ControllerLoginPage>getController();

            stage.setScene(new Scene(nextView));
            stage.setTitle("NSPJ");
            stage.show();

        }

    }
