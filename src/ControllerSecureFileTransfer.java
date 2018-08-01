import Database.User_InfoDB;
import Model.OAuth2Login;
import com.google.api.client.auth.oauth2.Credential;
import com.google.api.gax.paging.Page;
import com.google.auth.oauth2.AccessToken;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.storage.Blob;
import com.google.cloud.storage.Bucket;
import com.google.cloud.storage.Storage;
import com.google.cloud.storage.StorageOptions;
import com.jfoenix.controls.*;
import com.jfoenix.transitions.hamburger.HamburgerBackArrowBasicTransition;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.io.*;
import java.net.URL;
import java.security.*;
import java.util.ResourceBundle;
import java.util.Scanner;
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
    private AnchorPane TableAnchor;

    @FXML
    private JFXTextField searchBar;

    @FXML
    private JFXButton transfer;

    @FXML
    private JFXComboBox<String> emailBox;

    //    private KeyPairGenerator keyGen;
//    private KeyPair pair;
//    private PrivateKey privateKey;
//    private PublicKey publicKey;
//    private Scene myScene;
    public static AnchorPane rootP;
    private String privateBucketName;
    private Storage storage;
    private Credential credential;
    private OAuth2Login login = new OAuth2Login();
    private Cipher cipher;

    public ControllerSecureFileTransfer () throws NoSuchAlgorithmException, NoSuchPaddingException {
        this.cipher = Cipher.getInstance("RSA");
    }
    ObservableList<String> Email = FXCollections.observableArrayList("fenderxrs@gmail.com", "hugochiaxyz@gmail.com", "hansenhappy83@gmail.com", "winstonlim2000@gmail.com");


    @Override
    public void initialize(URL location, ResourceBundle resources) {
        hamburgerBar();
        emailBox.setItems(Email);
    }



    @FXML
    void transferFile(ActionEvent event) throws Exception {

        Scanner sc = new Scanner(emailBox.getValue());
        sc.useDelimiter("@gmail.com");
        String sending = sc.next();

        this.privateBucketName = "inbox-" + sending;

        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Open Resource File");
        File file = fileChooser.showOpenDialog(null);

        String AbsolutePath = file.getAbsolutePath();
        String filename = file.getName();
        User_InfoDB user = new User_InfoDB();

        System.out.println(emailBox.getValue());

        encryptFile(getFileInBytes(file),file, user.getPublicKey(emailBox.getValue()));
        uploadFile(filename,getFileInBytes(file));
    }

    private void uploadFile(String filename, byte[] out) throws Exception {

        Page<Bucket> buckets = storage.list();
        for (Bucket bucket : buckets.iterateAll()) {
            if (bucket.toString().contains(privateBucketName)) {
                System.out.println(bucket.toString());
                InputStream input = new ByteArrayInputStream(out);
//                InputStream targetStream = new FileInputStream(initialFile);
//            InputStream content = new ByteArrayInputStream("Hello, World!".getBytes(UTF_8));
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


    public void encryptFile(byte[] input, File output, PublicKey key)
            throws IOException, GeneralSecurityException {
        this.cipher.init(Cipher.ENCRYPT_MODE, key);
        writeToFile(output, this.cipher.doFinal(input));
    }


    public byte[] getFileInBytes(File f) throws IOException {
        FileInputStream fis = new FileInputStream(f);
        byte[] fbytes = new byte[(int) f.length()];
        fis.read(fbytes);
        fis.close();
        return fbytes;
    }




    public void hamburgerBar() {
        rootP = anchorPane;

        try {
            VBox box = FXMLLoader.load(getClass().getResource("UserSideTab.fxml"));
            drawer.setSidePane(box);
            drawer.setVisible(false);
            drawer.setDefaultDrawerSize(219);
        } catch (IOException ex) {
            Logger.getLogger(ControllerSecureCloudStorage.class.getName()).log(Level.SEVERE, null, ex);
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