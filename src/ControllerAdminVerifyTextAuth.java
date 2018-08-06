import Database.User_InfoDB;
import Model.OAuth2Login;
import Model.TextAuthentication;
import com.google.api.client.auth.oauth2.Credential;
import com.jfoenix.animation.alert.JFXAlertAnimation;
import com.jfoenix.controls.JFXAlert;
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXDialogLayout;
import com.jfoenix.controls.JFXTextField;
import com.nexmo.client.NexmoClientException;
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

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.sql.SQLException;
import java.util.ResourceBundle;

public class ControllerAdminVerifyTextAuth implements Initializable {

    private Scene myScene;
    private Credential credential;
    private OAuth2Login login = new OAuth2Login();

    @FXML
    private AnchorPane anchorPane;

    @FXML
    private JFXTextField adminverifyField;

    @FXML
    private Label viewerText;

    @FXML
    private Label numberViewer;

    @FXML
    void adminResend(ActionEvent event) throws MalformedURLException {

        TextAuthentication resend = new TextAuthentication();
        resend.adminSendNew();

    }

    @FXML
    void adminVerifyConfirm(ActionEvent event) throws NexmoClientException {

        TextAuthentication textAuth = new TextAuthentication();

        System.out.println("\nCode input: " + adminverifyField.getText());

        boolean check = textAuth.checkAuth(adminverifyField.getText());

        if (check == true) {

            FXMLLoader loader = new FXMLLoader(getClass().getResource("AdminDeviceCheck.fxml"));
            myScene = anchorPane.getScene();
            Stage stage = (Stage) (myScene).getWindow();
            Parent nextView = null;

            try {
                nextView = loader.load();
                ControllerAdminDeviceCheck controller = loader.<ControllerAdminDeviceCheck>getController();
                controller.runCheck();
//              controller.passData(login.getEmail());

            } catch (IOException u) {
                u.printStackTrace();
            }

            stage.setScene(new Scene(nextView));
            stage.setTitle("NSPJ");
            stage.show();

        } else {

            myScene = anchorPane.getScene();
            Stage stage = (Stage) (myScene).getWindow();

            String title = "Invalid!";
            String content = "OTP entered is invalid!";

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
            System.out.print("Sorry, wrong pin!");
        }

    }

    public void adminAuth(String phoneNo){

        TextAuthentication auth = new TextAuthentication();
        auth.adminSendAuth(phoneNo);
//        numberViewer.setText(phoneNo);
        System.out.println(" Correct phone number input");

    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {

        try {
            credential = login.login();

        User_InfoDB user = new User_InfoDB();
        String number = user.getPhoneNumber(user.getPhoneNumber(login.getEmail()));

        viewerText.setText("Please enter the OTP sent to this number: ");
        numberViewer.setText(number);


        } catch (Exception e) {
            e.printStackTrace();
        }


    }
}

