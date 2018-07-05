import Model.TextAuthentication;
import com.jfoenix.animation.alert.JFXAlertAnimation;
import com.jfoenix.controls.*;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ResourceBundle;

public class ControllerSignUpPage implements Initializable {
    @FXML
    private AnchorPane anchorPane;

    @FXML
    private TextField EmailTextField;

    @FXML
    private JFXTextField PhoneNoField;

    @FXML
    private JFXPasswordField PasswordField;

    @FXML
    private JFXPasswordField ConfirmPasswordField;

    @FXML
    private JFXButton ConfirmButton;

    @FXML
    private JFXButton CancelButton;

    private Scene myScene;
    WindowsUtils utils = new WindowsUtils();
    private String email;

    private String hashPassword;
    private String publicKey;
    private String encryptedPrivateKey;

    //public static AnchorPane rootP;

    @Override
    public void initialize(URL location, ResourceBundle resources) {

    }

    void passData(String email) {
        this.email = email;
        EmailTextField.setText(email);
    }

    @FXML
    void onClickCancelButton(ActionEvent event) throws IOException {
//          if (checkPhoneNoRequirements(PhoneNoField.getText())==true){
//              System.out.println("true");
//          }else{
//              System.out.println(false);
//          }

        File file= new File(System.getProperty("user.home")+"\\"+".store\\oauth2_sample\\StoredCredential");
        file.delete();

        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getResource("LoginPage.fxml"));
        myScene = anchorPane.getScene();
        Stage stage = (Stage) (myScene).getWindow();
        Parent nextView = loader.load();

        ControllerLoginPage controller = loader.<ControllerLoginPage>getController();
        //controller.passData(login.getEmail());

        stage.setScene(new Scene(nextView));
        stage.setTitle("NSPJ");
        stage.show();
    }


    @FXML
    void onClickConfirmButton(ActionEvent event) throws Exception {
        passwordValidation();
        //migrate this to the device checking page
//        if (utils.checkWindowsApproved()==true) {
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
//        }else{
//            System.out.println("Not supported VERSION!");
//        }
    }

    private void passwordValidation() throws Exception {
        if (PasswordField.getText().isEmpty() || ConfirmPasswordField.getText().isEmpty() || PhoneNoField.getText().isEmpty()) {
            System.out.println("Fill up all fields!");
            showAlert(anchorPane.getScene(), "", "Please fill up all fields", "Close");
        }
//        else if(PasswordField.getText().equals(email)){
//            showAlert(anchorPane.getScene(), "", "Cannot use email as your password", "Close");
//        }
        //Add phone number checker here
        else if (validate(PasswordField.getText()) == false || checkPhoneNoRequirements(PhoneNoField.getText())==false) {
            System.out.println("Use stronger password");
            showAlert(anchorPane.getScene(), "", "Use stronger password. Minimum 8 characters, must have letters and numbers. Will increase security in the future", "Close");
        }
        //Consider checking password strength before checking whether it matches
        else if (!PasswordField.getText().equals((ConfirmPasswordField).getText())) {
            System.out.println("Passwords do not match!");
            showAlert(anchorPane.getScene(), "", "Passwords do not match!", "Close");
        } else {
            TextAuthentication verifyText=new TextAuthentication();
            verifyText.sendAuth(PhoneNoField.getText());

            myScene = anchorPane.getScene();
            Stage stage = (Stage) (myScene).getWindow();

            AnchorPane layout=FXMLLoader.load(getClass().getResource("JFXSMSDialog.fxml"));
            JFXAlert<Void> alert = new JFXAlert<>(stage);
            alert.setOverlayClose(false);
            alert.setAnimation(JFXAlertAnimation.CENTER_ANIMATION);
            alert.setContent(layout);
            alert.initModality(Modality.NONE);
//            close.setOnAction(__ -> alert.hideWithAnimation());
            alert.show();
//            keyGenerator();
//            //Compute Hash of Password
//            hashPassword = get_SHA_512_SecurePassword(PasswordField.getText(), email);
//            System.out.println(hashPassword);
//            utils.setUserKeyInfo(hashPassword,publicKey,encryptedPrivateKey,email);
        }
    }

    //Will strengthen password validator
    private boolean validate(String password) {
        if (password == null || password.length() < 8) {
            return false;
        }
        //New Algo
        String upperCaseChars = "(.*[A-Z].*)";
        if (!password.matches(upperCaseChars ))
        {
            System.out.println("Password should contain atleast one upper case alphabet");
            return false;
        }
        String lowerCaseChars = "(.*[a-z].*)";
        if (!password.matches(lowerCaseChars ))
        {
            System.out.println("Password should contain atleast one lower case alphabet");
            return false;
        }
        String numbers = "(.*[0-9].*)";
        if (!password.matches(numbers ))
        {
            System.out.println("Password should contain atleast one number.");
            return false;
        }
        String specialChars = "(.*[,~,!,@,#,$,%,^,&,*,(,),-,_,=,+,[,{,],},|,;,:,<,>,/,?].*$)";
        if (!password.matches(specialChars ))
        {
            System.out.println("Password should contain atleast one special character");
            return false;
        }
        return true;
    }

    private boolean checkPhoneNoRequirements(String phoneNo){
        String numbers = "[0-9]+";
        if (phoneNo.matches(numbers) && phoneNo.length()==8 && (phoneNo.startsWith("8") || phoneNo.startsWith("9"))){
            return true;
        } else{
            return false;
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
        RSAKeyGenerator rsaKeyGenerator = new RSAKeyGenerator();
        rsaKeyGenerator.buildKeyPair();
        publicKey=rsaKeyGenerator.getPublicKeyString();
        System.out.println(publicKey);
        String privateKey=rsaKeyGenerator.getPrivateKeyString();
        System.out.println(privateKey);
        encryptedPrivateKey=rsaKeyGenerator.getEncryptedPrivateKeyString(PasswordField.getText(),privateKey);
        System.out.println(encryptedPrivateKey);
        //For testing purpose only
        String decryptedPrivateKey=rsaKeyGenerator.getPrivateKeyString(PasswordField.getText(),encryptedPrivateKey);
        System.out.println(decryptedPrivateKey);
        System.out.println(privateKey.equals(decryptedPrivateKey));
        //For testing purpose only
        //upload all the info to the cloud
    }

    private void showAlert(Scene scene, String Title, String Content, String buttonContent) {
        myScene = scene;
        Stage stage = (Stage) (myScene).getWindow();

        String title = Title;
        String content = Content;

        JFXButton close = new JFXButton(buttonContent);

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
}

