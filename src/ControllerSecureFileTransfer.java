import Database.User_InfoDB;
import Model.*;
import com.google.api.client.auth.oauth2.Credential;
import com.google.api.gax.paging.Page;
import com.google.auth.oauth2.AccessToken;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.ReadChannel;
import com.google.cloud.storage.*;
import com.jfoenix.animation.alert.JFXAlertAnimation;
import com.jfoenix.controls.*;
import com.jfoenix.transitions.hamburger.HamburgerBackArrowBasicTransition;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;
import org.json.simple.JSONObject;
import javax.crypto.*;
import javax.crypto.spec.SecretKeySpec;
import java.io.*;
import java.net.URL;
import java.net.UnknownHostException;
import java.nio.ByteBuffer;
import java.nio.channels.Channels;
import java.nio.channels.WritableByteChannel;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.*;
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
    private TableColumn<ControllerSecureFileTransfer, String> filename;

    @FXML
    private TableColumn<ControllerSecureFileTransfer, String> date;

    @FXML
    private TableColumn<ControllerSecureFileTransfer, Button> action;


    private ObservableList <ControllerSecureFileTransfer> thisInbox= FXCollections.observableArrayList();

    private Scene myScene;
    public static AnchorPane rootP;
    private String privateBucketName;
    private Storage storage;
    private static Credential credential;
    private OAuth2Login login = new OAuth2Login();

    private SecretKeySpec secretKey;
    private Cipher cipher;

    private Cipher RSAcipher;

    private String name;
    private String detail;
    private Button button;

    private static String ownBucketName;
    private static String downloadThis;
    private static String password = "Pass123!";
    private static String ownEmail;

    public ControllerSecureFileTransfer() throws NoSuchAlgorithmException, NoSuchPaddingException {
        this.RSAcipher = Cipher.getInstance("RSA");
    }

//    public ControllerSecureFileTransfer(String secret)
//            throws UnsupportedEncodingException, NoSuchAlgorithmException, NoSuchPaddingException {
//        byte[] key = new byte[128];
//        key = fixSecret(secret, 128);
//        this.secretKey = new SecretKeySpec(key, "AES");
//        this.cipher = Cipher.getInstance("AES");
//    }
//
//    private byte[] fixSecret(String s, int length) throws UnsupportedEncodingException {
//        if (s.length() < length) {
//            int missingLength = length - s.length();
//            for (int i = 0; i < missingLength; i++) {
//                s += " ";
//            }
//        }
//        return s.substring(0, length).getBytes("UTF-8");
//    }
//
//    public void encryptFile(File f)
//            throws InvalidKeyException, IOException, IllegalBlockSizeException, BadPaddingException {
//        System.out.println("Encrypting file: " + f.getName());
//        this.cipher.init(Cipher.ENCRYPT_MODE, this.secretKey);
//    }
//
//    public void decryptFile(File f)
//            throws InvalidKeyException, IOException, IllegalBlockSizeException, BadPaddingException {
//        System.out.println("Decrypting file: " + f.getName());
//        this.cipher.init(Cipher.DECRYPT_MODE, this.secretKey);
//        writeToFile(f, getFileInBytes(f));
//    }




    public ControllerSecureFileTransfer (String filename, String date) {

        this.name = filename;
        this.detail = date;
        this.button = new Button("Download");

        button.setOnAction(e -> {

           downloadThis = getName();
           System.out.println(downloadThis);


           try {

               getStorage();
                downloadFileNew(storage,ownBucketName,downloadThis);

                deleteFile(ownBucketName, downloadThis);


            } catch (Exception e1) {
                e1.printStackTrace();
            }

        });
    }


    public String getName() { return name; }

    public void setName(String name) { this.name = name; }

    public String getDetail() { return detail; }

    public void setDetail(String detail) { this.detail = detail; }

    public Button getButton() { return button; }

    public void setButton(Button button) { this.button = button; }

    @Override
    public void initialize(URL location, ResourceBundle resources) {

        User_InfoDB user = new User_InfoDB();
        ObservableList<String> Email = null;

        try {

            credential = login.login();

            ownEmail = login.getEmail();

            Scanner sc = new Scanner(ownEmail);
            System.out.println(ownEmail);
            sc.useDelimiter("@gmail.com");
            String send = sc.next();

            ownBucketName = "inbox-" + send;

            System.out.println(ownBucketName);

            thisInbox = assign(ownBucketName);

            System.out.println(thisInbox);

            filename.setCellValueFactory(new PropertyValueFactory<ControllerSecureFileTransfer, String>("name"));
            date.setCellValueFactory(new PropertyValueFactory<ControllerSecureFileTransfer, String>("detail"));
            action.setCellValueFactory(new PropertyValueFactory<ControllerSecureFileTransfer, Button>("button"));

            for (ControllerSecureFileTransfer r : thisInbox) {
                r.toString();
            }
            userInbox.setItems(thisInbox);


        } catch (UnknownHostException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {

            ArrayList<String> newEmail = user.getAllEmail();
//            newEmail.remove(ownEmail);
            for (int i = 0; i < newEmail.size(); i++) {

                if (user.getAccStatus(newEmail.get(i)).equals("Inactive")) {

                    newEmail.remove(newEmail.get(i));

                }

            }

            Email = FXCollections.observableArrayList(newEmail);


        } catch (Exception e) {
            e.printStackTrace();
        }

        hamburgerBar();
        emailBox.setItems(Email);

    }


    @FXML
    void refresh(ActionEvent event) throws Exception {

        inboxUpdate();

        JFXSnackbar snackbar = new JFXSnackbar(anchorPane);
        snackbar.getStylesheets().add("Style.css");
        snackbar.show("Refreshed", 3000);

//        FXMLLoader loader = new FXMLLoader();
//        loader.setLocation(getClass().getResource("SecureFileTransfer.fxml"));
//        myScene = (Scene) ((Node) event.getSource()).getScene();
//        Stage stage = (Stage) (myScene).getWindow();
//        Parent nextView = loader.load();
//
//        ControllerSecureFileTransfer controller = loader.<ControllerSecureFileTransfer>getController();
//        //controller.passData(admin);
//
//        stage.setScene(new Scene(nextView));
//        stage.setTitle("NSPJ");
//        stage.show();


//        thisInbox = assign(ownBucketName);
//
//        System.out.println(thisInbox);
//
//        filename.setCellValueFactory(new PropertyValueFactory<ControllerSecureFileTransfer, String>("name"));
//        date.setCellValueFactory(new PropertyValueFactory<ControllerSecureFileTransfer, String>("detail"));
//        action.setCellValueFactory(new PropertyValueFactory<ControllerSecureFileTransfer, Button>("button"));
//
//        for (ControllerSecureFileTransfer r : thisInbox){
//            r.toString();
//        }
//        userInbox.setItems(thisInbox);
    }


    @FXML
    void transferFile(ActionEvent event) throws Exception {

//        credential = login.login();
        if (emailBox.getValue()==null) {

            popAlert("Please select an Email to send the file to.");

        }else {


            Scanner sc = new Scanner(emailBox.getValue());
            System.out.println(emailBox.getValue());
            sc.useDelimiter("@gmail.com");
            String sending = sc.next();

            privateBucketName = "inbox-" + sending;

            System.out.print(privateBucketName);


            File file;

            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Open Resource File");
            file = fileChooser.showOpenDialog(null);


            FileScanner scan = new FileScanner();
            scan.Scanner(file.getAbsolutePath());

            if (scan.scannerReport() == false) {

                popAlert("Your file contains virus. It will not be allowed to be uploaded");

            } else {

                String filename = file.getName();
//            User_InfoDB user = new User_InfoDB();
                System.out.println(emailBox.getValue() + " " + filename);

                encryptFileNew(file);

//            uploadFile(filename, encryptFile(getFileInBytes(file), user.getPublicKey(emailBox.getValue())));
            }

//        process.start();
//        process.setOnSucceeded(e -> {
//            process.reset();
//            Platform.runLater(() -> {
//                JFXSnackbar snackbar = new JFXSnackbar(anchorPane);
//                snackbar.getStylesheets().add("Style.css");
//                snackbar.show("Upload success", 3000);
//            });
//
//        });
//        process.setOnFailed(e -> {
//            process.reset();
//            Platform.runLater(() -> {
//                JFXSnackbar snackbar = new JFXSnackbar(anchorPane);
//                snackbar.getStylesheets().add("Style.css");
//                snackbar.show("Updating", 3000);
//
//            });
//        });
//
//        process.setOnCancelled(e -> {
//            process.reset();
//            Platform.runLater(() -> {
//                JFXSnackbar snackbar = new JFXSnackbar(anchorPane);
//                snackbar.getStylesheets().add("Style.css");
//                snackbar.show("Updating The Logs", 3000);
//
//            });
//        });

        }

    }

    public ObservableList assign(String bucketname) throws Exception {
        getStorage();
        Page<Blob> blobs = storage.list(bucketname);

        for (Blob blob : blobs.iterateAll()) {

            String namef = blob.getName();
            System.out.println(namef);
            String detailf = convertTime(blob.getCreateTime());
            System.out.println(detailf);
            thisInbox.add(new ControllerSecureFileTransfer(namef, detailf));
        }
        return thisInbox;

    }

    private String convertTime(long time) {
        Date date = new Date(time);
        Format format = new SimpleDateFormat(" dd/MM/yyyy HH:mm:ss");
        return format.format(date);
    }


    private void uploadFile(String filename, byte[] out) throws Exception {

        getStorage();
        Page<Bucket> buckets = storage.list();
        for (Bucket bucket : buckets.iterateAll()) {
            if (bucket.toString().contains(privateBucketName)) {
                System.out.println(bucket.toString());
                InputStream input = new ByteArrayInputStream(out);
                Blob blob = bucket.create(filename, input, "text/plain");
            }
        }
        JFXSnackbar snackbar = new JFXSnackbar(anchorPane);
        snackbar.getStylesheets().add("Style.css");
        snackbar.show("Upload success", 3000);
    }

    private void uploadFileNew(String filename, String AbsolutePath, byte[] out) throws Exception {
        getStorage();
        Page<Bucket> buckets = storage.list();
        for (Bucket bucket : buckets.iterateAll()) {
            if (bucket.toString().contains(privateBucketName)) {
                System.out.println(bucket.toString());
                File initialFile = new File(AbsolutePath);
                InputStream input = new ByteArrayInputStream(out);
                Blob blob = bucket.create(filename, input, "text/plain");
            }
        }
    }

    private void getStorage() throws Exception {
//        credential = login.login();
        if (credential.getExpiresInSeconds() < 250) {
//            credential = login.login();
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


//    public byte[] encryptFile(byte[] input)
//            throws IOException, GeneralSecurityException {
//        this.cipher.init(Cipher.ENCRYPT_MODE, key);
//
//        return this.cipher.doFinal(input);
////      writeToFile(output, this.cipher.doFinal(input));
//    }

    private void encryptFileNew(File f) throws Exception {
        KeyGenerator keyGen = KeyGenerator.getInstance("AES");
        keyGen.init(128);

        SecretKey secKey = keyGen.generateKey();

        Cipher aesCipher = Cipher.getInstance("AES");

        byte[] byteTextNew = Files.readAllBytes(new File(f.getAbsolutePath()).toPath());

        aesCipher.init(Cipher.ENCRYPT_MODE, secKey);
        //Encrypt file here
        byte[] byteCipherText = aesCipher.doFinal(byteTextNew);

        uploadFileNew(f.getName(), f.getAbsolutePath(), byteCipherText);

        //TODO Encrypt the key and get back the E key string which will be uploaded to the cloud
        //encoding the key to String
        String encodedSecretKey = Base64.getEncoder().encodeToString(secKey.getEncoded());
        System.out.println(encodedSecretKey);
//
//        byte[] salt = new byte[8];
//        srandom.nextBytes(salt);
//        //Convert salt to base64 for uploading
//        String encodedSalt = Base64.getEncoder().encodeToString(salt);

        /* Derive the key, given password and salt. */
//        SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");
//        KeySpec spec = new PBEKeySpec(password.toCharArray(), salt, 65536, 256);
//        SecretKey tmp = factory.generateSecret(spec);
//        SecretKey secret = new SecretKeySpec(tmp.getEncoded(), "AES");

        String encryptedSymmetricKey = getEncryptedSymmetricKey(encodedSecretKey);
        //At this point symmetric key has been encoded then encrypted then encoded again
        System.out.println(encryptedSymmetricKey);
//        TODO Upload this instead^

//        JSONObject main =new JSONObject();

        //encrypt the secretKey then encode?

        //Testing for decoded key
        byte[] decodedKey = Base64.getDecoder().decode(encodedSecretKey);
        // rebuild key using SecretKeySpec
        SecretKey originalKey = new SecretKeySpec(decodedKey, 0, decodedKey.length, "AES");
        System.err.println("TESTING" + secKey.equals(originalKey));
        //Testing for decoded key

        JSONObject fileObj = new JSONObject();

        JSONObject filemetadata = new JSONObject();
        filemetadata.put("Encrypted Symmetric Key", encryptedSymmetricKey);
//        filemetadata.put("Salt", encodedSalt);

        fileObj.put("metadata", filemetadata);
//        main.put("metadata",fileObj);

        try {
            // Writing to a file
            File file = new File("JsonFile.json");
            file.createNewFile();
            FileWriter fileWriter = new FileWriter(file);
            System.out.println("Writing JSON object to file");
            System.out.println("-----------------------");
            System.out.print(fileObj);

            fileWriter.write(fileObj.toJSONString());
            fileWriter.flush();
            fileWriter.close();

        } catch (IOException e) {
            e.printStackTrace();
        }

        String[] command = {"curl", "-X", "PATCH", "--data-binary", "@JsonFile.json",
                "-H", "Authorization: Bearer " + credential.getAccessToken(),
                "-H", "Content-Type: application/json",
                "https://www.googleapis.com/storage/v1/b/" + privateBucketName + "/o/" + convertName(f.getName())};

        ProcessBuilder process = new ProcessBuilder(command);
        Process p;
        try {
            p = process.start();
            BufferedReader reader = new BufferedReader(new InputStreamReader(p.getInputStream()));
            StringBuilder builder = new StringBuilder();
            String line = null;
            while ((line = reader.readLine()) != null) {
                builder.append(line);
                builder.append(System.getProperty("line.separator"));
            }
            String result = builder.toString();
            System.out.print(result);

        } catch (IOException e) {
            System.out.print("error");
            e.printStackTrace();
        }

        JFXSnackbar snackbar = new JFXSnackbar(anchorPane);
        snackbar.show("Transfer success", 3000);
        snackbar.getStylesheets().add("Style.css");
    }

//    private byte[] iv = {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0};
//    private IvParameterSpec ivspec = new IvParameterSpec(iv);

    private String getEncryptedSymmetricKey(String symmetricKey) throws Exception {

        //returns encrypted symmetric key
        byte[] EncryptedSymmetricKey = encryptSymmetricKey(symmetricKey);
        //convert E. symmetric key to base64
        return Base64.getEncoder().encodeToString(EncryptedSymmetricKey);
//        return null;
    }

    private byte[] encryptSymmetricKey(String symmetricKey) throws Exception {
        Cipher cipher = Cipher.getInstance("RSA");
        User_InfoDB user = new User_InfoDB();
        cipher.init(Cipher.ENCRYPT_MODE, user.getPublicKey(emailBox.getValue()));

        return cipher.doFinal(symmetricKey.getBytes());
    }

    //By the time it comes here must password must be converted to same Key using the same salt
    private String getDecryptedSymmetricKey(String EncryptedSymmetricKeyEncoded) throws Exception {
        //decode String from EncryptedSymmetricKeyString
        byte[] EncryptedSymmetricKey = Base64.getDecoder().decode(EncryptedSymmetricKeyEncoded);
        byte[] PrivateKeyString = decryptSymmetricKey(EncryptedSymmetricKey);
        //return Base64.getEncoder().encodeToString(PrivateKeyString);
        return new String(PrivateKeyString);
    }

    private byte[] decryptSymmetricKey(byte[] encrypted) throws Exception {
        Cipher cipher = Cipher.getInstance("RSA");
        User_InfoDB user = new User_InfoDB();
        cipher.init(Cipher.DECRYPT_MODE, user.getPrivateKey(ownEmail,password));

        return cipher.doFinal(encrypted);
    }

    private String convertName(String name) {
        name = name.replace(" ", "%20");
        return name;
    }

    public void decryptFile(byte[] input,File output, PrivateKey key)
            throws IOException, GeneralSecurityException {
        this.cipher.init(Cipher.DECRYPT_MODE, key);

        writeToFile(output, this.cipher.doFinal(input));
    }


    public byte[] getFileInBytes(File f) throws IOException {
        FileInputStream fis = new FileInputStream(f);
        byte[] fbytes = new byte[(int) f.length()];
        fis.read(fbytes);
        fis.close();
        return fbytes;
    }

    private void downloadFile(Storage storage, String bucketName, String objectName) throws Exception {

        Platform.runLater(() -> {

//            myScene = anchorPane.getScene();
        FileChooser fileChooser = new FileChooser();
        fileChooser.setInitialFileName(objectName);
        File filePath = fileChooser.showSaveDialog(null);

//                User_InfoDB user = new User_InfoDB();
//
//            byte [] hi = new byte[0];
//            try {
//                hi = getFileInBytes(filePath);
//
//
//                credential = login.login();
//            decryptFile(hi,filePath, user.getPrivateKey(login.getEmail(),password));
//
//            } catch (Exception e) {
//                e.printStackTrace();
//            }


            if (filePath != null) {
            String filePathString = filePath.getAbsolutePath();
            Path downloadTo = Paths.get(filePathString);

            BlobId blobId = BlobId.of(bucketName, objectName);
            Blob blob = storage.get(blobId);
            System.out.println(blob);
            if (blob == null) {
                System.out.println("No such object");
                return;
            }
            PrintStream writeTo = System.out;
            if (downloadTo != null) {
                try {
                    writeTo = new PrintStream(new FileOutputStream(downloadTo.toFile()));
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
            }
            if (blob.getSize() < 1_000_000) {
                // Blob is small read all its content in one request
                byte[] content = blob.getContent();

                try {
                    writeTo.write(content);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } else {
                // When Blob size is big or unknown use the blob's channel reader.
                try (ReadChannel reader = blob.reader()) {
                    WritableByteChannel channel = Channels.newChannel(writeTo);
                    ByteBuffer bytes = ByteBuffer.allocate(64 * 1024);
                    while (reader.read(bytes) > 0) {
                        bytes.flip();
                        channel.write(bytes);
                        bytes.clear();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (downloadTo == null) {
                writeTo.println();
            } else {
                writeTo.close();
            }

        }



        });
    }

    private void downloadFileNew(Storage storage, String bucketName, String objectName) throws Exception {


        FileChooser fileChooser = new FileChooser();
        fileChooser.setInitialFileName(objectName);
        File filePath = fileChooser.showSaveDialog(null);
        if (filePath != null) {
            String filePathString = filePath.getAbsolutePath();
            Path downloadTo = Paths.get(filePathString);

            BlobId blobId = BlobId.of(bucketName, objectName);
            Blob blob = storage.get(blobId);
            System.out.println(blob);
            if (blob == null) {
                System.out.println("No such object");
                return;
            }
            PrintStream writeTo = System.out;
            if (downloadTo != null) {
                writeTo = new PrintStream(new FileOutputStream(downloadTo.toFile()));
            }
            if (blob.getSize() < 1_000_000) {
                // Blob is small read all its content in one request
                byte[] content = blob.getContent();

                writeTo.write(content);
            } else {
                // When Blob size is big or unknown use the blob's channel reader.
                try (ReadChannel reader = blob.reader()) {
                    WritableByteChannel channel = Channels.newChannel(writeTo);
                    ByteBuffer bytes = ByteBuffer.allocate(64 * 1024);
                    while (reader.read(bytes) > 0) {
                        bytes.flip();
                        channel.write(bytes);
                        bytes.clear();
                    }
                }
            }
            if (downloadTo == null) {
                writeTo.println();
            } else {
                writeTo.close();
            }

            //Current decryption is here
            //getting back the same salt
//            byte[] salt = new byte[8];
//            String saltMetadata = blob.getMetadata().get("Salt");
//            System.err.println(saltMetadata);
//            salt = Base64.getDecoder().decode(saltMetadata);

            SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");
//            KeySpec spec = new PBEKeySpec(password.toCharArray(), salt, 65536, 256);
//            SecretKey tmp = factory.generateSecret(spec);
//            SecretKey passwordKey = new SecretKeySpec(tmp.getEncoded(), "AES");

            String encodedKey = blob.getMetadata().get("Encrypted Symmetric Key");
            String decryptedSymmetricKey = getDecryptedSymmetricKey(encodedKey);

            byte[] decodedKey = Base64.getDecoder().decode(decryptedSymmetricKey);
            // rebuild key using SecretKeySpec
            SecretKey originalKey = new SecretKeySpec(decodedKey, 0, decodedKey.length, "AES");
//        decryptFileNew(file1,originalKey);
//        byte[] cipherText = Files.readAllBytes(new File(file1.getAbsolutePath()).toPath());
            byte[] cipherText = Files.readAllBytes(new File(filePath.getAbsolutePath()).toPath());


            Cipher aesCipher = Cipher.getInstance("AES");
            aesCipher.init(Cipher.DECRYPT_MODE, originalKey);
            byte[] bytePlainText = aesCipher.doFinal(cipherText);
            Files.write(Paths.get(filePath.getAbsolutePath()), bytePlainText);
        }
    }

    private void deleteFile(String bucketName, String blobName) throws Exception {

        try {
            if (storage == null) {
                getStorage();
            }
        } catch (Exception e) {
            e.printStackTrace();
            getStorage();
        }
        BlobId blobId = BlobId.of(bucketName, blobName);
        boolean deleted = storage.delete(blobId);
        if (deleted) {
            // the blob was deleted
            JFXSnackbar snackbar = new JFXSnackbar(anchorPane);
            snackbar.getStylesheets().add("Style.css");
            snackbar.show("Download success", 3000);
            System.out.println("Deleted");
        } else {
            // the blob was not found
            System.out.println("Not deleted not found");
        }
    }

    private void inboxUpdate() throws Exception {

        thisInbox.clear();
//        userInbox.setItems(thisInbox);

        ownEmail = login.getEmail();

        Scanner sc = new Scanner(ownEmail);
        System.out.println(ownEmail);
        sc.useDelimiter("@gmail.com");
        String send = sc.next();

        ownBucketName = "inbox-" + send;

        System.out.println(ownBucketName);

        thisInbox = assign(ownBucketName);

        System.out.println(thisInbox);

        filename.setCellValueFactory(new PropertyValueFactory<ControllerSecureFileTransfer, String>("name"));
        date.setCellValueFactory(new PropertyValueFactory<ControllerSecureFileTransfer, String>("detail"));
        action.setCellValueFactory(new PropertyValueFactory<ControllerSecureFileTransfer, Button>("button"));

        for (ControllerSecureFileTransfer r : thisInbox) {
            r.toString();
        }
        userInbox.setItems(thisInbox);

    }


    private void popAlert(String message) {
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

//    Service process = new Service() {
//        @Override
//        protected Task createTask() {
//            return new Task() {
//                @Override
//                protected Void call() throws Exception {
//
//                    Scanner sc = new Scanner(emailBox.getValue());
//                    System.out.println(emailBox.getValue());
//                    sc.useDelimiter("@gmail.com");
//                    String sending = sc.next();
//
//                    privateBucketName = "inbox-" + sending;
//
//                    System.out.print(sending);
//
//
//                    File file;
//
//                    Platform.runLater(() -> {
//
//                        FileChooser fileChooser = new FileChooser();
//                        fileChooser.setTitle("Open Resource File");
//                        file = fileChooser.showOpenDialog(null);
//
//
//                    });
//
//
//                    FileScanner scan = new FileScanner();
//                    scan.Scanner(file.getAbsolutePath());
//
//                    if (scan.scannerReport() == false) {
//
//                        popAlert("Your file contains virus. It will not be allowed to be uploaded");
//
//                    } else {
//
//                        String filename = file.getName();
//                        User_InfoDB user = new User_InfoDB();
//                        System.out.println(emailBox.getValue() + " " + filename);
//
//                        uploadFile(filename, encryptFile(getFileInBytes(file), user.getPublicKey(emailBox.getValue())));
//                    }
//
//                    return null;
//                }
//            };
//        }
//    };


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