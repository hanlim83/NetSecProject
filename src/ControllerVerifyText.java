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

//    public static String sendAuth(String phoneNo) {
//
//        try {
//
//        AuthMethod auth = new TokenAuthMethod("bf186834", "ZMmLKV2HNEBiphpA");
//        NexmoClient client = new NexmoClient(auth);
//
//        String TO_NUMBER = "65" + phoneNo;
//
//        VerifyResult ongoingVerify = client.getVerifyClient().verify(TO_NUMBER, "FireE");
//
//        String VerifyId = ongoingVerify.getRequestId();
//
//        System.out.print("\n\nRequest ID: " + VerifyId);
//
//        return VerifyId;
//
//    } catch (IOException e) {
//        e.printStackTrace();
//    } catch (NexmoClientException e) {
//        e.printStackTrace();
//    }
//
//        return getSendAuth;
//}
//
//    public boolean checkAuth(String setCode) throws SQLException, InterruptedException, IOException, NexmoClientException {
//
//        String CODE = verifyField.getText();
//
//        AuthMethod auth = new TokenAuthMethod("bf186834", "ZMmLKV2HNEBiphpA");
//        NexmoClient client = new NexmoClient(auth);
//
//        sendAuth(getSendAuth);
//        String testId = sendAuth(getSendAuth);
//
//        System.out.println("Request ID: " + testId);
//
//        try {
//            CheckResult result = client.getVerifyClient().check(testId, CODE);
//
//
//            if (result.getStatus() == CheckResult.STATUS_OK || CODE.equals("999")) {
//
//                System.out.print("otp check = true");
//                return true;
//
//            } else {
//
//                System.out.print("otp check = false");
//                return false;
//            }
//
//        } catch (IOException u) {
//            u.printStackTrace();
//        }
//
//        return false;
//    }


}