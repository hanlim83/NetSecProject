import Model.OAuth2Login;
import com.google.api.client.auth.oauth2.Credential;
import com.google.api.gax.paging.Page;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import com.google.auth.oauth2.AccessToken;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.storage.*;
import com.jfoenix.controls.JFXButton;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.*;
import java.net.*;
import java.util.ResourceBundle;

import static com.google.api.client.util.Charsets.UTF_8;

public class ControllerLoginPage implements Initializable {
    @FXML
    private JFXButton LoginButton;

    private OAuth2Login login=new OAuth2Login();
    private Scene myScene;
    private Credential credential;

    @Override
    public void initialize(URL location, ResourceBundle resources) {

    }

    @FXML
    void onClickLoginButton(ActionEvent event) throws Exception {
        //WindowsVersionNo();
        try {
            // authorization
            credential=login.login();
            // set up global Oauth2 instance

            // authorization + Get Buckets
//            Storage storage = StorageOptions.newBuilder().setCredentials(GoogleCredentials.create(new AccessToken(credential.getAccessToken(),null))).build().getService();
//            //Testing for storage
//            Page<Bucket> buckets = storage.list();
//            for (Bucket bucket : buckets.iterateAll()) {
//                System.out.println(bucket.toString());
//            }
//
//            for (Bucket bucket : buckets.iterateAll()) {
//                Page<Blob> blobs = bucket.list();
//                for (Blob blob : blobs.iterateAll()) {
//                    // do something with the blob
//                    System.out.println(blob);
//                    System.out.println(blob.getName());
//                }
//            }
//            String filename= "TestFILENEW1";
//            if (checkNameTaken(filename)==true){
//                System.out.println("Change NAME!!!!");
//            } else{
//                uploadFile(filename);
//            }
        } catch (Throwable t) {
            t.printStackTrace();
        }
        //System.exit(1);
        //If not sign up go to sign up page
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getResource("SignUpPage.fxml"));
        myScene = (Scene) ((Node) event.getSource()).getScene();
        Stage stage = (Stage) (myScene).getWindow();
        Parent nextView = loader.load();


        ControllerSignUpPage controller = loader.<ControllerSignUpPage>getController();
        controller.passData(login.getEmail());

        stage.setScene(new Scene(nextView));
        stage.setTitle("NSPJ");
        stage.show();
        //else goToDeviceCheck
    }

//    public boolean checkNameTaken(String fileName){
//        Storage storage = StorageOptions.newBuilder().setCredentials(GoogleCredentials.create(new AccessToken(credential.getAccessToken(),null))).build().getService();
//        Page<Blob> blobs = storage.list("hr_dept");
//        for (Blob blob : blobs.iterateAll()) {
//            // do something with the blob
//            System.out.println("FROM METHOD"+blob);
//            System.out.println("FROM METHOD"+blob.getName());
//            if(fileName.equals(blob.getName())){
//                System.out.println("Choose Different NAME!");
//                return true;
//            }
//        }
//        return false;
//    }
//
//    public void uploadFile(String filename) throws FileNotFoundException {
//        Storage storage = StorageOptions.newBuilder().setCredentials(GoogleCredentials.create(new AccessToken(credential.getAccessToken(),null))).build().getService();
//        Page<Bucket> buckets = storage.list();
//        for (Bucket bucket : buckets.iterateAll()) {
//            System.out.println(bucket.toString());
//            File initialFile = new File("C:\\Users\\hugoc\\Desktop\\NSPJ Logs\\Latest Login method logs.txt");
//            InputStream targetStream = new FileInputStream(initialFile);
//            InputStream content = new ByteArrayInputStream("Hello, World!".getBytes(UTF_8));
//            Blob blob = bucket.create(filename, targetStream, "text/plain");
//        }
//    }

//    public void WindowsVersionNo(){
//        System.out.println("os.name: " + System.getProperty("os.name"));
//        System.out.println("os.version: " + System.getProperty("os.version"));
//        WindowsUtils utils= new WindowsUtils();
//        utils.getEdition();
//    }
}