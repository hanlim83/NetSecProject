import com.jfoenix.controls.JFXButton;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;

public class ControllerUserSideTab {

    @FXML
    private JFXButton testButton;

    @FXML
    private JFXButton SecureCloudStorageButton;

    @FXML
    private JFXButton SecureCloudTransferButton;

    @FXML
    private JFXButton LogoutButton;

    private Scene myScene;

    @FXML
    void onClickTestButton(ActionEvent event) throws IOException {
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getResource("BaseLayoutNew.fxml"));
        myScene = (Scene) ((Node) event.getSource()).getScene();
        Stage stage = (Stage) (myScene).getWindow();
        Parent nextView = loader.load();


        ControllerBaseLayoutNew controller = loader.<ControllerBaseLayoutNew>getController();
        //controller.passData(admin);

        stage.setScene(new Scene(nextView));
        stage.setTitle("NSPJ");
        stage.show();
    }

    @FXML
    void onClickSecureCloudStorageButton(ActionEvent event) {

    }

    @FXML
    void onClickSecureCloudTransferButton(ActionEvent event) {

    }

    @FXML
    void onClickLogoutButton(ActionEvent event) throws IOException {
        File file= new File(System.getProperty("user.home")+"\\"+".store\\oauth2_sample\\StoredCredential");
        file.delete();

        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getResource("LoginPage.fxml"));
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
