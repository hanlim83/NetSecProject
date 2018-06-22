import com.jfoenix.animation.alert.JFXAlertAnimation;
import com.jfoenix.controls.*;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.SQLException;
import java.util.ResourceBundle;

public class ControllerSignUpPage implements Initializable {
    @FXML
    private AnchorPane anchorPane;

    @FXML
    private TextField EmailTextField;

    @FXML
    private JFXButton ConfirmButton;

    @FXML
    private JFXPasswordField PasswordField;

    @FXML
    private JFXPasswordField ConfirmPasswordField;

    private Scene myScene;
    WindowsUtils utils = new WindowsUtils();
    private String email;

    //public static AnchorPane rootP;

    @Override
    public void initialize(URL location, ResourceBundle resources) {

    }

    void passData(String email) {
        this.email = email;
        EmailTextField.setText(email);
    }


    @FXML
    void onClickConfirmButton(ActionEvent event) throws IOException, SQLException {
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

    private void passwordValidation() throws IOException {
        if (PasswordField.getText().isEmpty() || ConfirmPasswordField.getText().isEmpty()) {
            System.out.println("Fill up all fields!");
            //Known bug. Clicking the grey area causes the whole screen to be blocked
            myScene = anchorPane.getScene();
            Stage stage = (Stage) (myScene).getWindow();

            String title = "";
            String content = "Please fill up all fields!";
//
//            JFXDialogLayout dialogContent = new JFXDialogLayout();
//
//            dialogContent.setHeading(new Text(title));
//
//            dialogContent.setBody(new Text(content));
//
            JFXButton close = new JFXButton("Close");

            close.setButtonType(JFXButton.ButtonType.RAISED);

            close.setStyle("-fx-background-color: #00bfff;");
//
//            dialogContent.setActions(close);
//
//            JFXDialog dialog = new JFXDialog(StackPane, dialogContent, JFXDialog.DialogTransition.BOTTOM);
//
//            dialog.show();

            JFXDialogLayout layout = new JFXDialogLayout();
            layout.setHeading(new Label(title));
            layout.setBody(new Label(content));
            layout.setActions(close);
            JFXAlert<Void> alert = new JFXAlert<>(stage);
            alert.setOverlayClose(true);
            alert.setAnimation(JFXAlertAnimation.CENTER_ANIMATION);
            alert.setContent(layout);
            alert.initModality(Modality.NONE);
            close.setOnAction(new EventHandler<ActionEvent>() {
                @Override
                public void handle(ActionEvent __) {
                    alert.hideWithAnimation();
                }
            });
            alert.show();
        } else if (!PasswordField.getText().equals((ConfirmPasswordField).getText())) {
            System.out.println("Passwords do not match!");
            //Alert below
            myScene = anchorPane.getScene();
            Stage stage = (Stage) (myScene).getWindow();

            String title = "";
            String content = "Passwords do not match";
//
//            JFXDialogLayout dialogContent = new JFXDialogLayout();
//
//            dialogContent.setHeading(new Text(title));
//
//            dialogContent.setBody(new Text(content));
//
            JFXButton close = new JFXButton("Close");

            close.setButtonType(JFXButton.ButtonType.RAISED);

            close.setStyle("-fx-background-color: #00bfff;");
//
//            dialogContent.setActions(close);
//
//            JFXDialog dialog = new JFXDialog(StackPane, dialogContent, JFXDialog.DialogTransition.BOTTOM);
//
//            dialog.show();

            JFXDialogLayout layout = new JFXDialogLayout();
            layout.setHeading(new Label(title));
            layout.setBody(new Label(content));
            layout.setActions(close);
            JFXAlert<Void> alert = new JFXAlert<>(stage);
            alert.setOverlayClose(true);
            alert.setAnimation(JFXAlertAnimation.CENTER_ANIMATION);
            alert.setContent(layout);
            alert.initModality(Modality.NONE);
            close.setOnAction(new EventHandler<ActionEvent>() {
                @Override
                public void handle(ActionEvent __) {
                    alert.hideWithAnimation();
                }
            });
            alert.show();
        } else {
            if (validate(PasswordField.getText()) == false) {
                System.out.println("Use stronger password");
                //Alert below
                myScene = anchorPane.getScene();
                Stage stage = (Stage) (myScene).getWindow();

                String title = "";
                String content = "Use stronger password. Minimum 8 characters, must have letters and numbers. Will increase security in the future";
//
//            JFXDialogLayout dialogContent = new JFXDialogLayout();
//
//            dialogContent.setHeading(new Text(title));
//
//            dialogContent.setBody(new Text(content));
//
                JFXButton close = new JFXButton("Close");

                close.setButtonType(JFXButton.ButtonType.RAISED);

                close.setStyle("-fx-background-color: #00bfff;");
//
//            dialogContent.setActions(close);
//
//            JFXDialog dialog = new JFXDialog(StackPane, dialogContent, JFXDialog.DialogTransition.BOTTOM);
//
//            dialog.show();

                JFXDialogLayout layout = new JFXDialogLayout();
                layout.setHeading(new Label(title));
                layout.setBody(new Label(content));
                layout.setActions(close);
                JFXAlert<Void> alert = new JFXAlert<>(stage);
                alert.setOverlayClose(true);
                alert.setAnimation(JFXAlertAnimation.CENTER_ANIMATION);
                alert.setContent(layout);
                alert.initModality(Modality.NONE);
                close.setOnAction(new EventHandler<ActionEvent>() {
                    @Override
                    public void handle(ActionEvent __) {
                        alert.hideWithAnimation();
                    }
                });
                alert.show();
            } else {
                //Compute Hash of Password
                String HashPassword = get_SHA_512_SecurePassword(PasswordField.getText(), email);
                System.out.println(HashPassword);
                FXMLLoader loader = new FXMLLoader();
                loader.setLocation(getClass().getResource("UserHome.fxml"));
                myScene = anchorPane.getScene();
                Stage stage = (Stage) (myScene).getWindow();
                Parent nextView = loader.load();

                //Will change to Device Checking Page next time
                ControllerUserHome controller = loader.<ControllerUserHome>getController();
                //controller.passData(login.getEmail());

                stage.setScene(new Scene(nextView));
                stage.setTitle("NSPJ");
                stage.show();
            }
        }
    }

    //Will strengthen password validator
    private boolean validate(String password) {
        if (password == null || password.length() < 8) {
            return false;
        }
        boolean containsChar = false;
        boolean containsDigit = false;
        for (char c : password.toCharArray()) {
            if (Character.isLetter(c)) {
                containsChar = true;
            } else if (Character.isDigit(c)) {
                containsDigit = true;
            }
            if (containsChar && containsDigit) {
                return true;
            }
        }
        return false;
    }

    public String get_SHA_512_SecurePassword(String passwordToHash, String salt) {
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

    private void keyGenerator() {

    }
}

