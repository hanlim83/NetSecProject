import Model.OAuth2Login;
import com.google.api.client.auth.oauth2.Credential;
import com.google.api.gax.paging.Page;
import com.google.auth.oauth2.AccessToken;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.storage.Blob;
import com.google.cloud.storage.Storage;
import com.google.cloud.storage.StorageOptions;
import com.jfoenix.controls.JFXButton;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.ResourceBundle;
import java.util.Scanner;

public class ControllerUserSideTab implements Initializable {

    @FXML
    private JFXButton testButton;

    @FXML
    private JFXButton SecureCloudStorageButton;

    @FXML
    private JFXButton SecureCloudTransferButton;

    @FXML
    private JFXButton LogoutButton;

    private Scene myScene;

//    private static int SecureCloudStorageCounter=0;

    @FXML
    void onClickSecureCloudStorageButton(ActionEvent event) throws Exception {
        //go back to user home and reference the observablelist
        //pass data to next page

        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getResource("SecureCloudStorage.fxml"));
        myScene = (Scene) ((Node) event.getSource()).getScene();
        Stage stage = (Stage) (myScene).getWindow();
        Parent nextView = loader.load();


        ControllerSecureCloudStorage controller = loader.<ControllerSecureCloudStorage>getController();
//        if (SecureCloudStorageCounter==0) {
//            controller.passData(getObservableList());
//            SecureCloudStorageCounter++;
//        }else {
//            controller.rebuildTable();
//        }
        //controller.passData(admin);

        stage.setScene(new Scene(nextView));
        stage.setTitle("NSPJ");
        stage.show();
    }

    private Credential credential;
    private OAuth2Login login = new OAuth2Login();

//    public static ObservableList<ControllerSecureCloudStorage.TableBlob> blobs = FXCollections.observableArrayList();
//    public ObservableList<ControllerSecureCloudStorage.TableBlob> getObservableList() throws Exception {
//        credential=login.login();
//        ObservableList<ControllerSecureCloudStorage.TableBlob> blobs;
//        blobs = FXCollections.observableArrayList();
//        Storage storage = StorageOptions.newBuilder().setCredentials(GoogleCredentials.create(new AccessToken(credential.getAccessToken(), null))).build().getService();
//        String email = null;
//        try {
//            email = login.getEmail();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//        Scanner s = new Scanner(email).useDelimiter("@");
//        String emailFront = s.next();
//        emailFront = emailFront.replace(".", "");
//        String privateBucketName = emailFront + "nspj";
////        String bucketname="hugochiaxyznspj";
//        Page<Blob> blobList = storage.list(privateBucketName);
//        for (Blob blob : blobList.iterateAll()) {
////            BlobList.add(new MyBlob(blob));
//            blobs.add(new ControllerSecureCloudStorage.TableBlob(blob.getName(), convertTime(blob.getCreateTime()),"General"));
//        }
//        return blobs;
//    }
//
//    public String convertTime(long time) {
//        Date date = new Date(time);
//        Format format = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
//        return format.format(date);
//    }

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
        File file1= new File(System.getProperty("user.home")+"\\"+".store\\oauth2_sample\\StoredCredential");
        file1.delete();

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

//    public void setLogoutImage(){
//
//    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        Path path = FileSystems.getDefault().getPath("src/View/baseline_exit_to_app_black_18dp.png");
        File file = new File(path.toUri());
        Image imageForFile;
        try {
            imageForFile = new Image(file.toURI().toURL().toExternalForm());
            ImageView imageView = new ImageView(imageForFile);
//            imageView.setFitHeight(24.5);
//            imageView.setFitWidth(35);
            LogoutButton.setGraphic(imageView);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
    }
}
