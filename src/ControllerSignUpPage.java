import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXDialog;
import com.jfoenix.controls.JFXDialogLayout;
import com.jfoenix.controls.JFXPasswordField;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.layout.StackPane;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
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

    @FXML
    private StackPane StackPane;

    private Scene myScene;
    WindowsUtils utils = new WindowsUtils();

    //public static AnchorPane rootP;

    @Override
    public void initialize(URL location, ResourceBundle resources) {

    }

    void passData(String email) {
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
//            StackPane.setVisible(true);
//
//            String title = "";
//
//            String content = "Please fill up all fields!";
//
//            JFXDialogLayout dialogContent = new JFXDialogLayout();
//
//            dialogContent.setHeading(new Text(title));
//
//            dialogContent.setBody(new Text(content));
//
//            JFXButton close = new JFXButton("Close");
//
//            close.setButtonType(JFXButton.ButtonType.RAISED);
//
//            close.setStyle("-fx-background-color: #00bfff;");
//
//            dialogContent.setActions(close);
//
//            JFXDialog dialog = new JFXDialog(StackPane, dialogContent, JFXDialog.DialogTransition.BOTTOM);
//
//            close.setOnAction(new EventHandler<ActionEvent>() {
//                @Override
//                public void handle(ActionEvent __) {
//                    dialog.close();
//                    StackPane.setVisible(false);
//                }
//            });
//            dialog.show();
        } else if (!PasswordField.getText().equals((ConfirmPasswordField).getText())) {
            System.out.println("Passwords do not match!");
        }else{
            if (validate(PasswordField.getText())==false){
                System.out.println("Use stronger password");
            }else{
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
    private boolean validate(String password){
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

    private void keyGenerator() {

    }
}

