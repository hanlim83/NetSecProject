import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXDrawer;
import com.jfoenix.controls.JFXHamburger;
import com.jfoenix.controls.JFXTextField;
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

public class ControllerVerifyText {

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

    @FXML
    void backToNumber(ActionEvent event) {

    }

    @FXML
    void verifyConfirm(ActionEvent event) throws SQLException, InterruptedException {


        String CODE = verifyField.getText();


//        ControllerVerifyText ongoingVerify = loader.<ControllerVerifyText>getController();
//        CheckResult result = client.getVerifyClient().check(ongoingVerify.getRequestId(), CODE);


        if (/*result.getStatus() == CheckResult.STATUS_OK ||*/ CODE.equals("999")) {

            FXMLLoader loader = new FXMLLoader(getClass().getResource("DeviceCheck.fxml"));
            myScene = anchorPane.getScene();
            Stage stage = (Stage) (myScene).getWindow();
            Parent nextView = null;
            try {
                nextView = loader.load();
                ControllerDeviceCheck controller = loader.<ControllerDeviceCheck>getController();
                controller.runCheck();
//                controller.passData(login.getEmail());
            } catch (IOException u) {
                u.printStackTrace();
            }
            stage.setScene(new Scene(nextView));
            stage.setTitle("NSPJ");
            stage.show();

        } else if (CODE.equals("")) {

            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("FireE");
            alert.setHeaderText("Invalid Pin!");
            alert.setContentText("The field appears to be empty, please check your phone and enter the correct OTP sent to you!");
            alert.showAndWait();

        } else {

            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("FireE");
            alert.setHeaderText("Invalid Pin!");
            alert.setContentText("The pin you provided does not match our database. please check your phone again and type the pin correctly!");
            alert.showAndWait();

        }


    }

    public void sendAuth() {

        try {

            AuthMethod auth = new TokenAuthMethod("bf186834", "ZMmLKV2HNEBiphpA");
            NexmoClient client = new NexmoClient(auth);

            String TO_NUMBER = "6587170501";

            VerifyResult ongoingVerify = client.getVerifyClient().verify(TO_NUMBER, "FireE");


            System.out.println("Message sent!");

        } catch (IOException e) {
            e.printStackTrace();
        } catch (NexmoClientException e) {
            e.printStackTrace();
        }



    }


}

