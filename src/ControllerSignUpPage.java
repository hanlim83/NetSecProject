import com.jfoenix.controls.JFXButton;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.util.ResourceBundle;

public class ControllerSignUpPage implements Initializable{
    @FXML
    private AnchorPane anchorPane;

    @FXML
    private TextField EmailTextField;

    @FXML
    private JFXButton ConfirmButton;

    private Scene myScene;
    WindowsUtils utils=new WindowsUtils();

    //public static AnchorPane rootP;

    @Override
    public void initialize(URL location, ResourceBundle resources) {

    }

    void passData(String email){
        EmailTextField.setText(email);
    }


    @FXML
    void onClickConfirmButton(ActionEvent event) throws IOException, SQLException {
        if (utils.checkWindowsApproved()==true) {
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(getClass().getResource("UserHome.fxml"));
            myScene = (Scene) ((Node) event.getSource()).getScene();
            Stage stage = (Stage) (myScene).getWindow();
            Parent nextView = loader.load();

            //Will change to Device Checking Page next time
            ControllerUserHome controller = loader.<ControllerUserHome>getController();
            //controller.passData(login.getEmail());

            stage.setScene(new Scene(nextView));
            stage.setTitle("NSPJ");
            stage.show();
        }else{
            System.out.println("Not supported VERSION!");
        }
    }
}

