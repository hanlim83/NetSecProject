import Model.TextAuthentication;
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXTextField;
import com.nexmo.client.NexmoClientException;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ControllerJFXSMSDialog implements Initializable {
    @FXML
    private AnchorPane anchorPane;

    @FXML
    private JFXButton SubmitButton;

    @FXML
    private JFXTextField OTPField;

    @FXML
    private JFXButton CancelButton;

    @FXML
    private Label PhoneNumberLabel;

    @FXML
    private JFXButton ResendButton;

    TextAuthentication verifyText=new TextAuthentication();

    @FXML
    void onActionCancelButton(ActionEvent event) {
        //Close Windows
    }

    @FXML
    void onActionResendButton(ActionEvent event) {
        //verifyText.sendAuth();
    }

    @FXML
    void onActionSubmitButton(ActionEvent event) throws InterruptedException, SQLException, NexmoClientException, IOException {
        if(verifyText.checkAuth(OTPField.getText())==true){
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(getClass().getResource("DeviceCheck.fxml"));
            myScene = anchorPane.getScene();
            Stage stage = (Stage) (myScene).getWindow();
            Parent nextView = loader.load();

            ControllerDeviceCheck controller = loader.<ControllerDeviceCheck>getController();
            controller.runCheck();

            stage.setScene(new Scene(nextView));
            stage.setTitle("NSPJ");
            stage.show();
        } else{
            //show some wrong otp warning
        }
    }

    private Scene myScene;

//    public static AnchorPane rootP;

    @Override
    public void initialize(URL location, ResourceBundle resources) {

    }
}
