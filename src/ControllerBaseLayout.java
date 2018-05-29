import com.jfoenix.controls.JFXButton;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class ControllerBaseLayout implements Initializable {
    @FXML
    private JFXButton Button;

    private Scene myScene;

    @Override
    public void initialize(URL location, ResourceBundle resources) {

    }

    @FXML
    void onClickButton(ActionEvent event) throws IOException {
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(ControllerBaseLayout.class.getResource("LoginPage.fxml"));
        System.out.println(ControllerBaseLayout.class.getResource("LoginPage.fxml"));
        myScene = (Scene) ((Node) event.getSource()).getScene();
        Stage stage = (Stage) (myScene).getWindow();
        Parent nextView = loader.load();


        ControllerLoginPage controller = loader.<ControllerLoginPage>getController();
        //controller.passData(admin);

        stage.setScene(new Scene(nextView));
        stage.setTitle("NSPJ");
        stage.show();
    }
}
