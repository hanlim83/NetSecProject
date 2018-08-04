import Database.User_InfoDB;
import Model.FileScanner;
import Model.Inbox;
import Model.OAuth2Login;
import com.google.api.client.auth.oauth2.Credential;
import com.google.api.gax.paging.Page;
import com.google.auth.oauth2.AccessToken;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.ReadChannel;
import com.google.cloud.storage.*;
import com.jfoenix.animation.alert.JFXAlertAnimation;
import com.jfoenix.controls.*;
import com.jfoenix.controls.cells.editors.TextFieldEditorBuilder;
import com.jfoenix.controls.cells.editors.base.GenericEditableTreeTableCell;
import com.jfoenix.controls.datamodels.treetable.RecursiveTreeObject;
import com.jfoenix.transitions.hamburger.HamburgerBackArrowBasicTransition;
import com.sun.jna.platform.win32.Netapi32Util;
import com.sun.jna.platform.win32.WinNT;
import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Bounds;
import javafx.geometry.Insets;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Callback;

import javax.crypto.*;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;
import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.UnknownHostException;
import java.nio.ByteBuffer;
import java.nio.channels.Channels;
import java.nio.channels.WritableByteChannel;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.*;
import java.security.spec.KeySpec;
import java.sql.SQLException;
import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;


public class ControllerSecureFileTransfer implements Initializable {

    @FXML
    private AnchorPane anchorPane;

    @FXML
    private JFXHamburger hamburger;

    @FXML
    private JFXDrawer drawer;

    @FXML
    private JFXButton transfer;

    @FXML
    private JFXComboBox<String> emailBox;

    @FXML
    private TableView userInbox;

    @FXML
    private TableColumn<Inbox, String> filename;

    @FXML
    private TableColumn<Inbox, String> date;

    @FXML
    private TableColumn<Inbox, String> action;


    private ObservableList <Inbox> thisInbox;

    private Scene myScene;
    public static AnchorPane rootP;
    private String privateBucketName;
    private Storage storage;
    private Credential credential;
    private OAuth2Login login = new OAuth2Login();
    private Cipher cipher;

    public ControllerSecureFileTransfer () throws NoSuchAlgorithmException, NoSuchPaddingException {
        this.cipher = Cipher.getInstance("RSA");
    }



    @Override
    public void initialize(URL location, ResourceBundle resources) {

        User_InfoDB user = new User_InfoDB();
        ObservableList<String> Email = null;

        try {

            credential = login.login();
            ArrayList <String> newEmail = user.getAllEmail();
            newEmail.remove(login.getEmail());
            for (int i=0;i<newEmail.size();i++) {

                if (user.getAccStatus(newEmail.get(i)).equals("Inactive")){

                    newEmail.remove(newEmail.get(i));

                }

            }

            Email = FXCollections.observableArrayList(newEmail);


        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }

        hamburgerBar();
        emailBox.setItems(Email);

    }



    @FXML
    void transferFile(ActionEvent event) throws Exception {

        process.start();
        process.setOnSucceeded(e -> {
            process.reset();
            Platform.runLater(() -> {
                JFXSnackbar snackbar = new JFXSnackbar(anchorPane);
                snackbar.getStylesheets().add("Style.css");
                snackbar.show("Upload success", 3000);
            });

        });
        process.setOnFailed(e -> {
            process.reset();
            Platform.runLater(() -> {
                JFXSnackbar snackbar = new JFXSnackbar(anchorPane);
                snackbar.getStylesheets().add("Style.css");
                snackbar.show("Updating", 3000);

            });
        });

        process.setOnCancelled(e -> {
            process.reset();
            Platform.runLater(() -> {
                JFXSnackbar snackbar = new JFXSnackbar(anchorPane);
                snackbar.getStylesheets().add("Style.css");
                snackbar.show("Updating The Logs", 3000);

            });
        });



        }




    private void uploadFile(String filename, byte[] out) throws Exception {

        getStorage();
        Page <Bucket> buckets = storage.list();
        for (Bucket bucket : buckets.iterateAll()) {
            if (bucket.toString().contains(privateBucketName)) {
                System.out.println(bucket.toString());
                InputStream input = new ByteArrayInputStream(out);
                Blob blob = bucket.create(filename, input, "text/plain");
            }
        }
    }

        private void getStorage() throws Exception {
            if (credential.getExpiresInSeconds() < 250) {
                credential = login.login();
                storage = StorageOptions.newBuilder().setCredentials(GoogleCredentials.create(new AccessToken(credential.getAccessToken(), null))).build().getService();
            }
            if (storage == null) {
                storage = StorageOptions.newBuilder().setCredentials(GoogleCredentials.create(new AccessToken(credential.getAccessToken(), null))).build().getService();
            }
        }



    private void writeToFile(File output, byte[] toWrite) throws IllegalBlockSizeException, BadPaddingException, IOException {
        FileOutputStream fos = new FileOutputStream(output);
        fos.write(toWrite);
        fos.flush();
        fos.close();
    }


    public byte[] encryptFile(byte[] input, PublicKey key)
            throws IOException, GeneralSecurityException {
        this.cipher.init(Cipher.ENCRYPT_MODE, key);

        return this.cipher.doFinal(input);
//      writeToFile(output, this.cipher.doFinal(input));
    }

    public byte [] decryptFile(byte[] input, PrivateKey key)
            throws IOException, GeneralSecurityException {
        this.cipher.init(Cipher.DECRYPT_MODE, key);

        return this.cipher.doFinal(input);

//        writeToFile(output, this.cipher.doFinal(input));
    }


    public byte[] getFileInBytes(File f) throws IOException {
        FileInputStream fis = new FileInputStream(f);
        byte[] fbytes = new byte[(int) f.length()];
        fis.read(fbytes);
        fis.close();
        return fbytes;
    }

    private void popAlert (String message){
        myScene = anchorPane.getScene();
        Stage stage = (Stage) (myScene).getWindow();

        String title = "";
        String content = message;

        JFXButton close = new JFXButton("Close");

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

    Service process = new Service() {
        @Override
        protected Task createTask() {
            return new Task() {
                @Override
                protected Void call() throws Exception {

                    Scanner sc = new Scanner(emailBox.getValue());
                    System.out.println(emailBox.getValue());
                    sc.useDelimiter("@gmail.com");
                    String sending = sc.next();

                    privateBucketName = "inbox-" + sending;

                    System.out.print(sending);


                    File file;

                    Platform.runLater(() -> {

                        FileChooser fileChooser = new FileChooser();
                        fileChooser.setTitle("Open Resource File");
                        file = fileChooser.showOpenDialog(null);


                    });
                    

                    FileScanner scan = new FileScanner();
                    scan.Scanner(file.getAbsolutePath());

                    if (scan.scannerReport() == false) {

                        popAlert("Your file contains virus. It will not be allowed to be uploaded");

                    } else {

                        String filename = file.getName();
                        User_InfoDB user = new User_InfoDB();
                        System.out.println(emailBox.getValue() + " " + filename);

                        uploadFile(filename, encryptFile(getFileInBytes(file), user.getPublicKey(emailBox.getValue())));
                    }

                    return null;
                }
            };
        }
    };


        public void hamburgerBar() {
            rootP = anchorPane;

            try {
                VBox box = FXMLLoader.load(getClass().getResource("UserSideTab.fxml"));
                drawer.setSidePane(box);
                drawer.setVisible(false);
                drawer.setDefaultDrawerSize(219);
            } catch (IOException ex) {
                Logger.getLogger(ControllerSecureFileTransfer.class.getName()).log(Level.SEVERE, null, ex);
            }

            HamburgerBackArrowBasicTransition transition = new HamburgerBackArrowBasicTransition(hamburger);
            transition.setRate(-1);
            hamburger.addEventHandler(MouseEvent.MOUSE_PRESSED, (e) -> {
                transition.setRate(transition.getRate() * -1);
                transition.play();

                if (drawer.isOpened()) {
                    drawer.close();
                    drawer.setDisable(true);
                    //drawer.setVisible(false);
                } else {
                    drawer.open();
                    drawer.setVisible(true);
                    drawer.setDisable(false);
                }
            });
        }

    }