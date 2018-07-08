import Model.TextAuthentication;
import com.jfoenix.controls.JFXTextField;
import com.nexmo.client.NexmoClientException;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

import java.io.IOException;
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
    void adminResend(ActionEvent event) {

    }

    @FXML
    void adminVerifyConfirm(ActionEvent event) throws NexmoClientException {

        TextAuthentication textAuth = new TextAuthentication();

        System.out.println("\nCode input: " + adminverifyField.getText());

        boolean check = textAuth.checkAuth(adminverifyField.getText());

        if (check == true) {

            FXMLLoader loader = new FXMLLoader(getClass().getResource("DeviceCheck.fxml"));
            myScene = anchorPane.getScene();
            Stage stage = (Stage) (myScene).getWindow();
            Parent nextView = null;

            try {
                nextView = loader.load();
                ControllerDeviceCheck controller = loader.<ControllerDeviceCheck>getController();
                controller.runCheck();
//              controller.passData(login.getEmail());

            } catch (IOException u) {
                u.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (SQLException e) {
                e.printStackTrace();
            }

            stage.setScene(new Scene(nextView));
            stage.setTitle("NSPJ");
            stage.show();

        } else {

            System.out.print("Sorry, wrong pin!");


        }

    }

}

