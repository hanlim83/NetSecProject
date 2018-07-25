import com.jfoenix.controls.JFXButton;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
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
    void onClickSecureCloudStorageButton(ActionEvent event) throws IOException {
        //go back to user home and reference the observablelist
        //pass data to next page

        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getResource("SecureCloudStorage.fxml"));
        myScene = (Scene) ((Node) event.getSource()).getScene();
        Stage stage = (Stage) (myScene).getWindow();
        Parent nextView = loader.load();


        ControllerSecureCloudStorage controller = loader.<ControllerSecureCloudStorage>getController();
        controller.passData(ControllerUserHome.blobs);
        //controller.passData(admin);

        stage.setScene(new Scene(nextView));
        stage.setTitle("NSPJ");
        stage.show();
    }

    @FXML
    void onHoverTest(MouseEvent event) {
//        SecureCloudStorageButton.setStyle("-fx-background-color: red;");
        SecureCloudStorageButton.setRipplerFill(Color.RED);
//        SecureCloudStorageButton.setDisableVisualFocus(false);
        SecureCloudStorageButton.setButtonType(JFXButton.ButtonType.RAISED);
        System.out.println("Hovering");
    }

    @FXML
    void onHoverExitTest(MouseEvent event) {
        SecureCloudStorageButton.setButtonType(JFXButton.ButtonType.FLAT);
        System.out.println("Unhovering");
    }

    @FXML
    void onClickSecureCloudTransferButton(ActionEvent event) throws IOException {

        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getResource("SecureFileTransfer.fxml"));
        myScene = (Scene) ((Node) event.getSource()).getScene();
        Stage stage = (Stage) (myScene).getWindow();
        Parent nextView = loader.load();

        ControllerSecureFileTransfer controller = loader.<ControllerSecureFileTransfer>getController();
        //controller.passData(admin);

        stage.setScene(new Scene(nextView));
        stage.setTitle("NSPJ");
        stage.show();

    }

    @FXML
    void onClickUserSettings (ActionEvent event) throws IOException {
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getResource("UserSettings.fxml"));
        myScene = (Scene) ((Node) event.getSource()).getScene();
        Stage stage = (Stage) (myScene).getWindow();
        Parent nextView = loader.load();

        ControllerUserSettings controller = loader.<ControllerUserSettings>getController();
        //controller.passData(admin);

        stage.setScene(new Scene(nextView));
        stage.setTitle("NSPJ");
        stage.show();
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
