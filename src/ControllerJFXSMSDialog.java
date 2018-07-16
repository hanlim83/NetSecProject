import Model.SignUpPage;
import Model.TextAuthentication;
import Database.User_InfoDB;
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXTextField;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ResourceBundle;

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

    private Scene myScene;

    private TextAuthentication verifyText = new TextAuthentication();

    private WindowsUtils utils = new WindowsUtils();
    private User_InfoDB user_infoDB = new User_InfoDB();

    private String hashPassword;
    private String publicKey;
    private String encryptedPrivateKey;

    private String email;
    private String password;
    private String phoneNo;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        SignUpPage signUpPage=new SignUpPage();
        PhoneNumberLabel.setText("96588071");
    }

//    public void setPhoneNo(){
//        SignUpPage signUpPage=new SignUpPage();
//        PhoneNumberLabel.setText(signUpPage.getPhoneNo());
//    }

    @FXML
    void onActionCancelButton(ActionEvent event) {
        //Close Window
        Stage stage = (Stage) CancelButton.getScene().getWindow();
        stage.close();
    }

    @FXML
    void onActionResendButton(ActionEvent event) {
        verifyText.sendNew();
//        SignUpPage signUpPage=new SignUpPage();
//        this.email=signUpPage.getEmail();
//        this.password=signUpPage.getPassword();
//        this.phoneNo=signUpPage.getPhoneNo();
//        //verifyText.sendAuth();
//        System.out.println(email+password+phoneNo);
    }

    @FXML
    void onActionSubmitButton(ActionEvent event) throws Exception {
        if (verifyText.checkAuth(OTPField.getText()) == true) {
//            ControllerSignUpPage signUpPage = new ControllerSignUpPage();
//            signUpPage.changePage();
            SignUpPage signUpPage=new SignUpPage();
            this.email=signUpPage.getEmail();
            this.password=signUpPage.getPassword();
            this.phoneNo=signUpPage.getPhoneNo();
            System.out.println("Phone number"+phoneNo);

//            Move Key Gen and stuff to the OTP CONTROLLER INSTEAD
            keyGenerator();
            //Compute Hash of Password
            hashPassword = get_SHA_512_SecurePassword(password, email);
            System.out.println(hashPassword);
//            utils.setUserKeyInfo(hashPassword, publicKey, encryptedPrivateKey, email);
            user_infoDB.setUserKeyInfo(hashPassword, publicKey, encryptedPrivateKey, phoneNo, email);

            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(getClass().getResource("DeviceCheck.fxml"));
            myScene = (Scene) ((Node) event.getSource()).getScene();
            Stage stage = (Stage) (myScene).getWindow();
            Parent nextView = loader.load();

            ControllerDeviceCheck controller = loader.<ControllerDeviceCheck>getController();
            controller.runCheck();

            stage.setScene(new Scene(nextView));
            stage.setTitle("NSPJ");
            stage.show();
        } else {
            //show some wrong otp warning
        }
    }

    private String get_SHA_512_SecurePassword(String passwordToHash, String salt) {
        String generatedPassword = null;
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-512");
            md.update(salt.getBytes(StandardCharsets.UTF_8));
            byte[] bytes = md.digest(passwordToHash.getBytes(StandardCharsets.UTF_8));
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < bytes.length; i++) {
                sb.append(Integer.toString((bytes[i] & 0xff) + 0x100, 16).substring(1));
            }
            generatedPassword = sb.toString();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return generatedPassword;
    }

    private void keyGenerator() throws Exception {
        //Once password is secure enough do necessary stuff. Compute Hash, generate KeyPair, encrypt the private key and upload everything into cloud SQL
//        //Compute Hash of Password
//        String HashPassword = get_SHA_512_SecurePassword(PasswordField.getText(), email);
//        System.out.println(HashPassword);
        System.out.println(password);
        RSAKeyGenerator rsaKeyGenerator = new RSAKeyGenerator();
        rsaKeyGenerator.buildKeyPair();
        publicKey = rsaKeyGenerator.getPublicKeyString();
        System.out.println(publicKey);
        String privateKey = rsaKeyGenerator.getPrivateKeyString();
        System.out.println(privateKey);
        encryptedPrivateKey = rsaKeyGenerator.getEncryptedPrivateKeyString(password, privateKey);
        System.out.println(encryptedPrivateKey);
        //For testing purpose only
        String decryptedPrivateKey = rsaKeyGenerator.getPrivateKeyString(password, encryptedPrivateKey);
        System.out.println(decryptedPrivateKey);
        System.out.println(privateKey.equals(decryptedPrivateKey));
        //For testing purpose only
        //upload all the info to the cloud
    }
}
