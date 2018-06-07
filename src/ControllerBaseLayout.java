import com.jfoenix.controls.JFXButton;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class ControllerBaseLayout implements Initializable {
    @FXML
    private JFXButton Button;
    @FXML
    private JFXButton LogOutButton;

    private Scene myScene;

    @Override
    public void initialize(URL location, ResourceBundle resources) {

    }

    @FXML
    void onClickButton(ActionEvent event) throws IOException {
        //test
        File tmpDir = new File(System.getProperty("user.home")+"/"+".store/oauth2_sample");
        boolean exists = tmpDir.exists();
        System.out.println(exists);
        //test
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getResource("LoginPage.fxml"));
        System.out.println(getClass().getResource("LoginPage.fxml"));
        myScene = (Scene) ((Node) event.getSource()).getScene();
        Stage stage = (Stage) (myScene).getWindow();
        Parent nextView = loader.load();


        ControllerLoginPage controller = loader.<ControllerLoginPage>getController();
        //controller.passData(admin);

        stage.setScene(new Scene(nextView));
        stage.setTitle("NSPJ");
        stage.show();
    }

    @FXML
    void onClickLogOut(ActionEvent event) {
        //String filepath=System.getProperty("user.home")+"\\"+".store\\oauth2_sample\\StoredCredential";
        //System.out.println(filepath);
        File file= new File(System.getProperty("user.home")+"\\"+".store\\oauth2_sample\\StoredCredential");
        //File file1=new File("C:/Users/hugoc/.store/oauth2_sample/StoredCredential");
        file.delete();
        //file1.delete();
    }
}
