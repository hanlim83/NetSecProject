import Model.SignUpPage;
import Model.TextAuthentication;
import com.jfoenix.animation.alert.JFXAlertAnimation;
import com.jfoenix.controls.*;
import com.sun.org.apache.xpath.internal.operations.Bool;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class ControllerSignUpPage implements Initializable {
    @FXML
    private AnchorPane anchorPane;

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

    @FXML
    private Label emailLabel;

    @FXML
    private Label passwordLengthLabel;

    @FXML
    private Label upperCaseLabel;

    @FXML
    private Label lowerCaseLabel;

    @FXML
    private Label numberSpecialCharLabel;

    private static Scene myScene;
    public static String email;

    //TODO Code CLEANUP AND IMPLEMENT MULTITHREADING
    void passData(String email) {
        this.email = email;
        emailLabel.setText(email);
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        try {
            OTPAlert();
        } catch (IOException e) {
            e.printStackTrace();
        }
        PasswordField.setFocusColor(Color.BLUE);
        ConfirmPasswordField.setFocusColor(Color.BLUE);
        PhoneNoField.setFocusColor(Color.BLUE);
        PhoneNoField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (checkPhoneNoRequirements(PhoneNoField.getText()) ==  true){
                PhoneNoField.setFocusColor(Color.GREEN);
            } else{
                PhoneNoField.setFocusColor(Color.RED);
            }
        });
        PasswordField.textProperty().addListener((observable, oldValue, newValue) -> {
            String password=PasswordField.getText();
            if (validate(PasswordField.getText()) == true) {
                PasswordField.setFocusColor(Color.GREEN);
            }
            else{
                System.out.println("Use stronger password");
                PasswordField.setFocusColor(Color.RED);
                ConfirmPasswordField.setFocusColor(Color.RED);
            }
            if (!PasswordField.getText().equals((ConfirmPasswordField).getText())){
                ConfirmPasswordField.setFocusColor(Color.RED);
            }else{
                ConfirmPasswordField.setFocusColor(Color.GREEN);
            }


            //New Algo
            String upperCaseChars = "(.*[A-Z].*)";
            String lowerCaseChars = "(.*[a-z].*)";
            String numbers = "(.*[0-9].*)";
            String specialChars = "(.*[,~,!,@,#,$,%,^,&,*,(,),-,_,=,+,[,{,],},|,;,:,<,>,/,?].*$)";
            if (password == null || password.length() < 8) {
                passwordLengthLabel.setTextFill(Color.RED);
            } else{
                passwordLengthLabel.setTextFill(Color.GREEN);
            }
            if (!password.matches(upperCaseChars ))
            {
                System.out.println("Password should contain at least one upper case alphabet");
                upperCaseLabel.setTextFill(Color.RED);
            }else{
                upperCaseLabel.setTextFill(Color.GREEN);
            }
            if (!password.matches(lowerCaseChars ))
            {
                System.out.println("Password should contain at least one lower case alphabet");
                lowerCaseLabel.setTextFill(Color.RED);
            }else{
                lowerCaseLabel.setTextFill(Color.GREEN);
            }
            if (!password.matches(numbers ))
            {
                System.out.println("Password should contain atleast one number.");
                numberSpecialCharLabel.setTextFill(Color.RED);
            }
            if (!password.matches(specialChars ))
            {
                System.out.println("Password should contain atleast one special character");
                numberSpecialCharLabel.setTextFill(Color.RED);
            } else{
                numberSpecialCharLabel.setTextFill(Color.GREEN);
            }
        });
        ConfirmPasswordField.textProperty().addListener((observable, oldValue, newValue) -> {
//            System.out.println("textfield changed from " + oldValue + " to " + newValue);
            if (PasswordField.getText().equals((ConfirmPasswordField).getText())) {
                ConfirmPasswordField.setFocusColor(Color.GREEN);
            }else{
                System.out.println("Passwords do not match!");
                ConfirmPasswordField.setFocusColor(Color.RED);
            }
        });

        myScene=anchorPane.getScene();
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
        passwordValidationAlert();
    }

    private void passwordValidationAlert() throws Exception {
        if (PasswordField.getText().isEmpty() || ConfirmPasswordField.getText().isEmpty() || PhoneNoField.getText().isEmpty()) {
            System.out.println("Fill up all fields!");
            showAlert(anchorPane.getScene(), "", "Please fill up all fields", "Close");
        }
        else if(checkPhoneNoRequirements(PhoneNoField.getText())==false){
            showAlert(anchorPane.getScene(), "", "Phone number is not valid", "Close");
        }
        else if(PasswordField.getText().equals(email)){
            showAlert(anchorPane.getScene(), "", "Cannot use email as your password", "Close");
        }
        else if (validate(PasswordField.getText()) == false) {
            System.out.println("Use stronger password");
            showAlert(anchorPane.getScene(), "", "Use stronger password. Minimum 8 characters, must have letters and numbers. Will increase security in the future", "Close");
        }
        //Consider checking password strength before checking whether it matches
        else if (!PasswordField.getText().equals((ConfirmPasswordField).getText())) {
            System.out.println("Passwords do not match!");
            showAlert(anchorPane.getScene(), "", "Passwords do not match!", "Close");
        }
        else {
            TextAuthentication verifyText=new TextAuthentication();
            verifyText.sendAuth(PhoneNoField.getText());

            OTPAlert();
        }
    }

    private int counter=0;
    private static JFXAlert<Void> alert = null;
    private void OTPAlert() throws IOException {
        AnchorPane layout = FXMLLoader.load(getClass().getResource("JFXSMSDialog.fxml"));
        if (counter!=0) {
            SignUpPage signUpPage=new SignUpPage(email,PasswordField.getText(),PhoneNoField.getText());
            myScene = anchorPane.getScene();
            Stage stage = (Stage) (myScene).getWindow();
            alert = new JFXAlert<>(stage);
            alert.setOverlayClose(false);
            alert.setAnimation(JFXAlertAnimation.CENTER_ANIMATION);
            alert.setContent(layout);
            alert.initModality(Modality.NONE);
            alert.show();
            ControllerJFXSMSDialog jfxsmsDialog=new ControllerJFXSMSDialog();
//            jfxsmsDialog.setPhoneNo();
        }
        counter++;
    }

    public void changePage() throws IOException {
        alert.hideWithAnimation();
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getResource("DeviceCheck.fxml"));
//        myScene = (Scene) ((Node) event.getSource()).getScene();
//        myScene=anchorPane.getScene();
        Stage stage = (Stage) (myScene).getWindow();
        Parent nextView = loader.load();

        ControllerDeviceCheck controller = loader.<ControllerDeviceCheck>getController();
        controller.runCheck();

        stage.setScene(new Scene(nextView));
        stage.setTitle("NSPJ");
        stage.show();
    }

    //Will strengthen password validator
    private boolean validate(String password) {
        if (password == null || password.length() < 8) {
//            passwordLengthLabel.setTextFill(Color.RED);
            return false;
        }
//        else{
//            passwordLengthLabel.setTextFill(Color.GREEN);
//        }
        //New Algo
        String upperCaseChars = "(.*[A-Z].*)";
        if (!password.matches(upperCaseChars ))
        {
            System.out.println("Password should contain at least one upper case alphabet");
//            upperCaseLabel.setTextFill(Color.RED);
            return false;
        }
//        else{
//            upperCaseLabel.setTextFill(Color.GREEN);
//        }
        String lowerCaseChars = "(.*[a-z].*)";
        if (!password.matches(lowerCaseChars ))
        {
            System.out.println("Password should contain at least one lower case alphabet");
//            lowerCaseLabel.setTextFill(Color.RED);
            return false;
        }
//        else{
//            lowerCaseLabel.setTextFill(Color.GREEN);
//        }
        String numbers = "(.*[0-9].*)";
        if (!password.matches(numbers ))
        {
            System.out.println("Password should contain atleast one number.");
//            numberSpecialCharLabel.setTextFill(Color.RED);
            return false;
        }
        String specialChars = "(.*[,~,!,@,#,$,%,^,&,*,(,),-,_,=,+,[,{,],},|,;,:,<,>,/,?].*$)";
        if (!password.matches(specialChars ))
        {
            System.out.println("Password should contain atleast one special character");
//            numberSpecialCharLabel.setTextFill(Color.RED);
            return false;
        }
//        else{
//            numberSpecialCharLabel.setTextFill(Color.GREEN);
//        }
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

