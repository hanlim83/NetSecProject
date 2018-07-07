import Model.TextAuthentication;
import com.jfoenix.controls.*;
import com.nexmo.client.NexmoClient;
import com.nexmo.client.NexmoClientException;
import com.nexmo.client.auth.AuthMethod;
import com.nexmo.client.auth.TokenAuthMethod;
import com.nexmo.client.verify.CheckResult;
import com.nexmo.client.verify.VerifyResult;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import java.io.IOException;
import java.sql.SQLException;

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
    private JFXButton backButton;

//    public static void setGetSendAuth(String getSendAuth) {
//        ControllerVerifyText.getSendAuth = getSendAuth;
//    }

    @FXML
    void backToNumber(ActionEvent event) {

    }

    @FXML
    void verifyConfirm(ActionEvent event) throws SQLException, InterruptedException, IOException, NexmoClientException {

        TextAuthentication textAuth = new TextAuthentication();

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

}