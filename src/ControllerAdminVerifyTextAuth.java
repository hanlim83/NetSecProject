import Model.TextAuthentication;
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
import javafx.stage.Stage;

import java.io.IOException;
import java.net.MalformedURLException;
import java.sql.SQLException;

public class ControllerAdminVerifyTextAuth {

    private Scene myScene;

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
            System.out.print("Sorry, wrong pin!");
        }

    }

    public void adminAuth(String phoneNo){

        TextAuthentication auth = new TextAuthentication();
        auth.adminSendAuth(phoneNo);
        numberViewer.setText(phoneNo);
        System.out.println(" Correct phone number input");

    }

}

