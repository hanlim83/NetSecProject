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
import com.jfoenix.controls.cells.editors.TextFieldEditorBuilder;
import com.jfoenix.controls.cells.editors.base.GenericEditableTreeTableCell;
import com.jfoenix.controls.datamodels.treetable.RecursiveTreeObject;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.ObservableList;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
import javafx.event.EventHandler;
import javafx.geometry.Bounds;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.control.TreeTableColumn.CellEditEvent;
import com.jfoenix.transitions.hamburger.HamburgerBackArrowBasicTransition;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Callback;
import org.json.simple.JSONObject;

import javax.crypto.*;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;
import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.ByteBuffer;
import java.nio.channels.Channels;
import java.nio.channels.WritableByteChannel;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.*;
import java.security.spec.KeySpec;
import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;


public class ControllerSecureCloudStorage implements Initializable {
    @FXML
    private AnchorPane anchorPane;

    @FXML
    private com.jfoenix.controls.JFXTreeTableView<TableBlob> JFXTreeTableView;

    @FXML
    private AnchorPane TableAnchorPane;

    @FXML
    private JFXHamburger hamburger;

    @FXML
    private JFXButton UploadButton;

    @FXML
    private JFXDrawer drawer;

    @FXML
    private HBox FileButtonsHbox;

    @FXML
    private JFXTextField filterField;

    private Scene myScene;

    public static AnchorPane rootP;

    private Credential credential;
    private OAuth2Login login = new OAuth2Login();

    private String privateBucketName;
    private ObservableList<TableBlob> blobs;
    private Storage storage;
    private static String password;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        hamburgerBar();
//        filterField.getStylesheets().add(this.getClass().getResource("styles.css").toExternalForm());
//        TextFlow textFlow = new TextFlow();
//        ImageView imageView = new ImageView("baseline_search_black_18dp.png");
//        // Remove :) from text
//        Text text = null;
//        text.setText("Search");
//        textFlow.getChildren().addAll(imageView,text);
//        filterField.setPromptText(textFlow);
        try {
            credential = login.login();
            storage = StorageOptions.newBuilder().setCredentials(GoogleCredentials.create(new AccessToken(credential.getAccessToken(), null))).build().getService();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void passData(ObservableList<TableBlob> blobs) {
        this.blobs = blobs;
        TableMethod();
    }

    @FXML
    void onClickUploadButton(ActionEvent event) throws Exception {
//        checkUserPassword();
//        uploadProcess.start();
        checkUserDownloadDelete("Upload");

//        getStorage();

//        calculateEmail();
////        UploadFileTest();
//        FileChooser fileChooser = new FileChooser();
//        fileChooser.setTitle("Choose File to Upload");
//        //FEATURE: stage now loads as 1 page instead of 2
//        Stage stage = (Stage) anchorPane.getScene().getWindow();
//        File file = fileChooser.showOpenDialog(stage);
//        if (checkNameTaken(file.getName()) == true) {
//            System.out.println("Change NAME!!!! Add showing alert");
//        } else {
//            encryptFileNew(file);
//            //may need to move update Table somewhere else instead
//            updateTable();
//        }

////        downloadFile(storage,"hugochiaxyznspj","42149.py",saveFile());
////        deleteFile("hugochiaxyznspj","42149.py");
//        decryptFileNew(file1);

//        downloadFile(storage,"hugochiaxyznspj","Encrypted test.txt",saveFile());
//        decryptFile(file1);
    }

    private boolean checkNameTaken(String fileName) throws Exception {
        if (storage == null) {
            getStorage();
        }
        if (privateBucketName == null) {
            calculateEmail();
        }
        Page<Blob> blobs = storage.list(privateBucketName);
        for (Blob blob : blobs.iterateAll()) {
            // do something with the blob
            System.out.println("FROM METHOD" + blob);
            System.out.println("FROM METHOD" + blob.getName());
            if (fileName.equals(blob.getName())) {
                System.out.println("Choose Different NAME!");
                return true;
            }
        }
        return false;
    }

    //pass in password here to encrypt the symmetric key
    private void encryptFileNew(File f) throws Exception {
        KeyGenerator keyGen = KeyGenerator.getInstance("AES");
        keyGen.init(128);

        SecretKey secKey = keyGen.generateKey();

        Cipher aesCipher = Cipher.getInstance("AES");

//        FileInputStream in = new FileInputStream(f);
//        byte[] byteText = new byte[(int) f.length()];
        byte[] byteTextNew = Files.readAllBytes(new File(f.getAbsolutePath()).toPath());

//        byte[] byteText = "Your Plain Text Here".getBytes();

        aesCipher.init(Cipher.ENCRYPT_MODE, secKey);
        //Encrypt file here
        byte[] byteCipherText = aesCipher.doFinal(byteTextNew);

        uploadFile(f.getName(), f.getAbsolutePath(), byteCipherText);

        //TODO Encrypt the key and get back the E key string which will be uploaded to the cloud
        //encoding the key to String
        String encodedSecretKey = Base64.getEncoder().encodeToString(secKey.getEncoded());
        System.out.println(encodedSecretKey);

        byte[] salt = new byte[8];
        srandom.nextBytes(salt);
        //Convert salt to base64 for uploading
        String encodedSalt = Base64.getEncoder().encodeToString(salt);

        /* Derive the key, given password and salt. */
        SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");
        KeySpec spec = new PBEKeySpec(password.toCharArray(), salt, 65536, 256);
        SecretKey tmp = factory.generateSecret(spec);
        SecretKey secret = new SecretKeySpec(tmp.getEncoded(), "AES");

        String encryptedSymmetricKey = getEncryptedSymmetricKey(secret, encodedSecretKey);
        //At this point symmetric key has been encoded then encrypted then encoded again
        System.out.println(encryptedSymmetricKey);
        //TODO Upload this instead^

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
        filemetadata.put("Salt", encodedSalt);

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

//        URL url = new URL(
////                "" +
////                "curl -X PATCH --data-binary @JsonFile.json " +
////                "        -H \"Authorization: Bearer "+"1/Gj3dwe658cFMW9X0IFyL5p8Pf6CnjAwZ0wT46IYDYeQ"+" \\\n" +
////                "        -H \"Content-Type: application/json\" \\\n" +
//                "https://www.googleapis.com/storage/v1/b/hugochiaxyznspj/o/Encrypted test");
//
////        curl -X PATCH --data-binary @JsonFile.json \\" +
////        "    -H \"Authorization: Bearer "+"1/Gj3dwe658cFMW9X0IFyL5p8Pf6CnjAwZ0wT46IYDYeQ"+"\\\"" +
////                "    -H \"Content-Type: application/json\"" +
////                "    \"https://www.googleapis.com/storage/v1/b/hugochiaxyznspj/o/Encrypted test\"
//
//        try (BufferedReader reader = new BufferedReader(new InputStreamReader(url.openStream(), "UTF-8"))) {
//            for (String line; (line = reader.readLine()) != null;) {
//                System.out.println(line);
//            }
//        } catch (UnsupportedEncodingException e) {
//            e.printStackTrace();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }

        //        curl -X PATCH --data-binary @[JSON_FILE_NAME].json \
//        -H "Authorization: Bearer [OAUTH2_TOKEN]" \
//        -H "Content-Type: application/json" \
//        "https://www.googleapis.com/storage/v1/b/[BUCKET_NAME]/o/[OBJECT_NAME]"


//        String accessToken = credential.getAccessToken();
//        System.out.println(accessToken);

//      "-H", "Authorization: Bearer ya29.GlwCBi4vxdEBtIa07c3ky9XkGT8kXEZEStM_kL-Hk0Btqd8iuwfZCuzvotbmEyem_ppxTgJuAtWdxOna3e2fDvzp37A1wbNFl9eX7OYYyvVuqBd4XHXRCqJ2EIq_4g",

        String[] command = {"curl", "-X", "PATCH", "--data-binary", "@JsonFile.json",
                "-H", "Authorization: Bearer " + credential.getAccessToken(),
                "-H", "Content-Type: application/json",
                "https://www.googleapis.com/storage/v1/b/" + privateBucketName + "/o/" + convertName(f.getName())};
        //                "https://www.googleapis.com/storage/v1/b/" + privateBucketName + "/o/Encrypted%20test"};

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
    }

    private String convertName(String name) {
        name = name.replace(" ", "%20");
        return name;
    }

    //pass in the
//    public String getEncryptedPrivateKeyString(String password, String privateKey) throws Exception {
//        //String EncryptedPrivateKeyString=null;
//        //getPrivateKeyString();
//
//        //encryption codes
//        byte[] EncryptedPrivateKeyString=encryptPrivateKey(password,privateKey);
//        //convert to base64
//        return Base64.getEncoder().encodeToString(EncryptedPrivateKeyString);
//    }
//
//    private byte[] encryptPrivateKey(SecretKeySpec symmetricKey, String privateKey) throws Exception {
//        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
//        cipher.init(Cipher.ENCRYPT_MODE, symmetricKey, ivspec);
//
//        return cipher.doFinal(privateKey.getBytes());
//    }

    private byte[] iv = {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0};
    private IvParameterSpec ivspec = new IvParameterSpec(iv);

    private String getEncryptedSymmetricKey(SecretKey secret, String symmetricKey) throws Exception {

        //returns encrypted symmetric key
        byte[] EncryptedSymmetricKey = encryptSymmetricKey(secret, symmetricKey);
        //convert E. symmetric key to base64
        return Base64.getEncoder().encodeToString(EncryptedSymmetricKey);
//        return null;
    }

    private SecureRandom srandom = new SecureRandom();

    private byte[] encryptSymmetricKey(SecretKey secret, String symmetricKey) throws Exception {
        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
        cipher.init(Cipher.ENCRYPT_MODE, secret, ivspec);

        return cipher.doFinal(symmetricKey.getBytes());
    }


    //By the time it comes here must password must be converted to same Key using the same salt
    private String getDecryptedSymmetricKey(SecretKey passwordKey, String EncryptedSymmetricKeyEncoded) throws NoSuchAlgorithmException, NoSuchPaddingException, IllegalBlockSizeException, InvalidKeyException, InvalidAlgorithmParameterException, BadPaddingException {
        //decode String from EncryptedSymmetricKeyString
        byte[] EncryptedSymmetricKey = Base64.getDecoder().decode(EncryptedSymmetricKeyEncoded);
        byte[] PrivateKeyString = decryptSymmetricKey(passwordKey, EncryptedSymmetricKey);
        //return Base64.getEncoder().encodeToString(PrivateKeyString);
        return new String(PrivateKeyString);
    }

    private byte[] decryptSymmetricKey(SecretKey passwordKey, byte[] encrypted) throws NoSuchPaddingException, NoSuchAlgorithmException, BadPaddingException, IllegalBlockSizeException, InvalidAlgorithmParameterException, InvalidKeyException {
        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
        cipher.init(Cipher.DECRYPT_MODE, passwordKey, ivspec);

        return cipher.doFinal(encrypted);
    }

    private void uploadFile(String filename, String AbsolutePath, byte[] out) throws Exception {
        getStorage();
        Page<Bucket> buckets = storage.list();
        for (Bucket bucket : buckets.iterateAll()) {
            if (bucket.toString().contains(privateBucketName)) {
                System.out.println(bucket.toString());
                File initialFile = new File(AbsolutePath);
                InputStream input = new ByteArrayInputStream(out);
//                InputStream targetStream = new FileInputStream(initialFile);
//            InputStream content = new ByteArrayInputStream("Hello, World!".getBytes(UTF_8));
                Blob blob = bucket.create(filename, input, "text/plain");
            }
        }
    }

    //Download,saving and decrypt is here now
//    private void downloadFile(Storage storage, String bucketName, String objectName, Path downloadTo) throws Exception {
    private void downloadFile(Storage storage, String bucketName, String objectName) throws Exception {
        vBox.setVisible(false);
        myScene.removeEventFilter(MouseEvent.MOUSE_PRESSED, closeVbox);
        FileChooser fileChooser = new FileChooser();
        Stage stage = (Stage) anchorPane.getScene().getWindow();
        fileChooser.setInitialFileName(objectName);
        File filePath = fileChooser.showSaveDialog(stage);
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
            byte[] salt = new byte[8];
            String saltMetadata = blob.getMetadata().get("Salt");
            System.err.println(saltMetadata);
            salt = Base64.getDecoder().decode(saltMetadata);

            SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");
            KeySpec spec = new PBEKeySpec(password.toCharArray(), salt, 65536, 256);
            SecretKey tmp = factory.generateSecret(spec);
            SecretKey passwordKey = new SecretKeySpec(tmp.getEncoded(), "AES");

            String encodedKey = blob.getMetadata().get("Encrypted Symmetric Key");
            String decryptedSymmetricKey = getDecryptedSymmetricKey(passwordKey, encodedKey);

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
        vBox.setVisible(false);
        myScene.removeEventFilter(MouseEvent.MOUSE_PRESSED, closeVbox);
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
            System.out.println("Deleted");
            //show toast deleted
        } else {
            // the blob was not found
            System.out.println("Not deleted not found");
        }
    }

    private String convertTime(long time) {
        Date date = new Date(time);
        Format format = new SimpleDateFormat(" dd/MM/yyyy HH:mm:ss");
        return format.format(date);
    }

    private void calculateEmail() {
        String email = null;
        try {
            email = login.getEmail();
        } catch (IOException e) {
            e.printStackTrace();
        }
        Scanner s = new Scanner(email).useDelimiter("@");
        String emailFront = s.next();
        emailFront = emailFront.replace(".", "");
        privateBucketName = emailFront + "nspj";
    }

    //    public void decryptFileNew(File f, SecretKey secretKey) throws InvalidKeyException, BadPaddingException, IllegalBlockSizeException, IOException {
//        byte[] cipherText = Files.readAllBytes(new File(f.getAbsolutePath()).toPath());
//
//        aesCipher.init(Cipher.DECRYPT_MODE, secretKey);
//        byte[] bytePlainText = aesCipher.doFinal(cipherText);
//        Files.write(Paths.get(f.getAbsolutePath()), bytePlainText);
//    }
//
//    public void writeToFile(File f, Cipher c) throws Exception {
//        FileInputStream in = new FileInputStream(f);
//        byte[] input = new byte[(int) f.length()];
//        in.read(input);
//
//        FileOutputStream out = new FileOutputStream(f);
//        byte[] output = c.doFinal(input);
//        uploadFile("Encrypted test", f.getAbsolutePath(), output);
//
////        out.write(output);
//
//        out.flush();
//        out.close();
//        in.close();
//    }

    //original testing
//    public void decryptFile(File f)
//            throws Exception {
//        System.out.println("Decrypting file: " + f.getName());
//        Cipher cipher = Cipher.getInstance( symmetricKey.getAlgorithm() + "/CBC/PKCS5Padding" );
//        cipher.init(Cipher.DECRYPT_MODE, this.symmetricKey, new IvParameterSpec( IV ));
//        this.writeToFile(f,cipher);
//    }

//    public void decryptFile(File f, SecretKey secretKey)
//            throws Exception {
//        System.out.println("Decrypting file: " + f.getName());
//        Cipher cipher = Cipher.getInstance( secretKey.getAlgorithm() + "/CBC/PKCS5Padding" );
//        cipher.init(Cipher.DECRYPT_MODE, this.symmetricKey, new IvParameterSpec( IV ));
//        this.writeToFile(f,cipher);
//    }


//    public void decryptFile(File f,Cipher c)
//            throws InvalidKeyException, IOException, IllegalBlockSizeException, BadPaddingException, NoSuchPaddingException, NoSuchAlgorithmException {
//        System.out.println("Decrypting file: " + f.getName());
//
//        cipher.init(Cipher.DECRYPT_MODE, symmetricKey);
//        this.writeToFile(f,cipher);
//        this.writeToFile(f,c);
//    }

//    public Key generateSymmetricKey() throws Exception {
//        KeyGenerator generator = KeyGenerator.getInstance( "AES" );
//        SecretKey key = generator.generateKey();
////        key=symmetricKey;
//        return key;
//    }
//
//    private byte [] IV;
//    public void generateIV() {
//        SecureRandom random = new SecureRandom();
//        byte [] iv = new byte [16];
//        random.nextBytes( iv );
//        this.IV=iv;
////        return iv;
//    }
//
//    private Key symmetricKey;
//    private SecretKeySpec secretKey;

//    public void encryptFile(File f)
//            throws Exception {
//        System.out.println("Encrypting file: " + f.getName());
//        Key symmetricKey=generateSymmetricKey();
//        this.symmetricKey=symmetricKey;
//        generateIV();
////        this.secretKey=symmetricKey;
//        Cipher cipher = Cipher.getInstance( symmetricKey.getAlgorithm() + "/CBC/PKCS5Padding" );
//        cipher.init(Cipher.ENCRYPT_MODE, symmetricKey, new IvParameterSpec( IV ));
//        this.writeToFile(f,cipher);
////        cipher.do
////        decryptFile(f);
//    }

    public void UploadFileTest() {
//        try {
//            // authorization
//            //commented out credential to test new optimization techniques
////            credential = login.login();
//            // set up global Oauth2 instance
//            FileChooser fileChooser = new FileChooser();
//            fileChooser.setTitle("Open Resource File");
//            //FEATURE: stage now loads as 1 page instead of 2
//            Stage stage = (Stage) anchorPane.getScene().getWindow();
//            File file = fileChooser.showOpenDialog(stage);
//            if (file != null) {
//                String pathsInfo = "";
//                pathsInfo += "getPath(): " + file.getPath() + "\n";
//                pathsInfo += "getAbsolutePath(): " + file.getAbsolutePath() + "\n";
//
//                pathsInfo += (new File(file.getPath())).isAbsolute();
//
//                try {
//                    pathsInfo += "getCanonicalPath(): " +
//                            file.getCanonicalPath() + "\n";
//                } catch (IOException ex) {
//
//                }
//                System.out.println(pathsInfo);
//                // authorization + Get Buckets
////                Storage storage = StorageOptions.newBuilder().setCredentials(GoogleCredentials.create(new AccessToken(credential.getAccessToken(), null))).build().getService();
//                getStorage();
//                //Testing for storage
//                Page<Bucket> buckets = storage.list();
//                for (Bucket bucket : buckets.iterateAll()) {
//                    System.out.println(bucket.toString());
//                }
//
//                for (Bucket bucket : buckets.iterateAll()) {
//                    Page<Blob> blobs = bucket.list();
//                    for (Blob blob : blobs.iterateAll()) {
//                        // do something with the blob
//                        System.out.println(blob);
//                        System.out.println(blob.getName());
//                    }
//                }
//                //String filename= "TestFILENEW1";
//                //Actual Codes
//                if (checkNameTaken(file.getName()) == true) {
//                    System.out.println("Change NAME!!!!");
//                } else {
//                    uploadFile(file.getName(), file.getAbsolutePath());
//                }
//            } else {
//                System.out.println("No file selected");
//            }
//        } catch (Throwable t) {
//            t.printStackTrace();
//        }
    }

    private boolean checkPassword;
    private String tempPassword;

    //TODO DELETE THIS AFTER sufficient testing
//    private void checkUserPassword() {
//        myScene = anchorPane.getScene();
//        Stage stage = (Stage) (myScene).getWindow();
//
//        String title = "Enter your password to enter the restricted area";
//
//        JFXPasswordField jfxPasswordField = new JFXPasswordField();
//        jfxPasswordField.setPromptText("Enter password");
//
//        JFXButton jfxOKButton = new JFXButton("Ok");
//
//        jfxOKButton.setButtonType(JFXButton.ButtonType.RAISED);
//
//        jfxOKButton.setStyle("-fx-background-color: #00bfff;");
//
//        JFXAlert<Void> alert = new JFXAlert<>(stage);
//        if (password == null) {
//            JFXDialogLayout layout = new JFXDialogLayout();
//            layout.setHeading(new Label(title));
//            layout.setBody(jfxPasswordField);
//            layout.setActions(jfxOKButton);
//            alert.setOverlayClose(true);
//            alert.setAnimation(JFXAlertAnimation.CENTER_ANIMATION);
//            alert.setContent(layout);
//            alert.initModality(Modality.NONE);
//            jfxOKButton.setOnAction(__ -> {
//                //check
//                tempPassword = jfxPasswordField.getText();
//                jfxOKButton.setDisable(true);
//                jfxPasswordField.setDisable(true);
//                uploadProcess.start();
//            });
//            alert.show();
//        } else {
////            password = jfxPasswordField.getText();
//            alert.hideWithAnimation();
//            calculateEmail();
//            //        UploadFileTest();
//            FileChooser fileChooser = new FileChooser();
//            fileChooser.setTitle("Choose File to Upload");
//            Stage stage1 = (Stage) anchorPane.getScene().getWindow();
//            File file = fileChooser.showOpenDialog(stage1);
//            if (file != null) {
//                try {
//                    if (checkNameTaken(file.getName()) == true) {
//                        System.out.println("Change NAME!!!! Add showing alert");
//                    } else {
//                        encryptFileNew(file);
//                        //may need to move update Table somewhere else instead
//                        updateTable();
//                    }
//                } catch (Exception e1) {
//                    e1.printStackTrace();
//                }
//            }
//        }
//        uploadProcess.setOnSucceeded(e -> {
//            uploadProcess.reset();
//            //if dosen't match redo process
//            if (checkPassword == false) {
//                jfxPasswordField.setDisable(false);
//                jfxOKButton.setDisable(false);
//                System.out.println("Wrong password");
//            } else {
//                //if matches continue to encrypt also need to store the password somewhere
//                password = jfxPasswordField.getText();
//                alert.hideWithAnimation();
//                calculateEmail();
//                //        UploadFileTest();
//                FileChooser fileChooser = new FileChooser();
//                fileChooser.setTitle("Choose File to Upload");
//                //FEATURE: stage now loads as 1 page instead of 2
//                Stage stage1 = (Stage) anchorPane.getScene().getWindow();
//                File file = fileChooser.showOpenDialog(stage1);
//                if (file != null) {
//                    try {
//                        if (checkNameTaken(file.getName()) == true) {
//                            System.out.println("Change NAME!!!! Add showing alert");
//                        } else {
//                            encryptFileNew(file);
//                            //may need to move update Table somewhere else instead
//                            updateTable();
//                        }
//                    } catch (Exception e1) {
//                        e1.printStackTrace();
//                    }
//                }
//            }
//        });
//        uploadProcess.setOnCancelled(e -> {
//            uploadProcess.reset();
//        });
//        uploadProcess.setOnFailed(e -> {
//            uploadProcess.reset();
//            jfxOKButton.setDisable(false);
//        });
//    }

    private void updateObservableList() throws Exception {
        try {
            if (storage == null) {
                getStorage();
            }
        } catch (Exception e) {
            e.printStackTrace();
            getStorage();
        }
        if (privateBucketName == null) {
            calculateEmail();
        }
        Page<Blob> blobList = storage.list(privateBucketName);
        blobs.clear();
        for (Blob blob : blobList.iterateAll()) {
//            BlobList.add(new MyBlob(blob));
            blobs.add(new ControllerSecureCloudStorage.TableBlob(blob.getName(), convertTime(blob.getCreateTime())));
        }
    }

    private void TableMethod() {
//        blobs = FXCollections.observableArrayList();
//        Page<Blob> blobList = storage.list(privateBucketName);
//        for (Blob blob : blobList.iterateAll()) {
////            BlobList.add(new MyBlob(blob));
//            blobs.add(new TableBlob(blob.getName(), convertTime(blob.getCreateTime())));
//        }

        JFXTreeTableColumn<TableBlob, String> fileColumn = new JFXTreeTableColumn<>("File Name");
        fileColumn.setPrefWidth(525);
        fileColumn.setCellValueFactory((TreeTableColumn.CellDataFeatures<TableBlob, String> param) -> {
            if (fileColumn.validateValue(param)) {
                return param.getValue().getValue().blobName;
            } else {
                return fileColumn.getComputedValue(param);
            }
        });

        JFXTreeTableColumn<TableBlob, String> dateColumn = new JFXTreeTableColumn<>("Date");
        dateColumn.setPrefWidth(350);
        dateColumn.setCellValueFactory((TreeTableColumn.CellDataFeatures<TableBlob, String> param) -> {
            if (dateColumn.validateValue(param)) {
                return param.getValue().getValue().date;
            } else {
                return dateColumn.getComputedValue(param);
            }
        });

        JFXTreeTableColumn<TableBlob, String> settingsColumn = new JFXTreeTableColumn<>("Actions");
        settingsColumn.setPrefWidth(175);
        Callback<TreeTableColumn<TableBlob, String>, TreeTableCell<TableBlob, String>> cellFactory
                =
                new Callback<TreeTableColumn<TableBlob, String>, TreeTableCell<TableBlob, String>>() {
                    @Override
                    public TreeTableCell call(final TreeTableColumn<TableBlob, String> param) {
                        final TreeTableCell<TableBlob, String> cell = new TreeTableCell<TableBlob, String>() {
                            JFXButton btn = new JFXButton();

                            @Override
                            public void updateItem(String item, boolean empty) {
                                super.updateItem(item, empty);
                                if (empty) {
                                    setGraphic(null);
                                    setText(null);
                                } else {
                                    btn.setOnAction(event -> {
                                        int selectdIndex = getTreeTableRow().getIndex();
                                        System.out.println(selectdIndex);
                                        TableBlob tableBlob = JFXTreeTableView.getSelectionModel().getModelItem(selectdIndex).getValue();
                                        System.out.println(tableBlob.getBlobName());
                                        blobName = tableBlob.getBlobName();
                                        System.out.println(tableBlob.getDate());
                                        Bounds boundsInScene = btn.localToScene(btn.getBoundsInLocal());
                                        showVbox(boundsInScene.getMinX(), boundsInScene.getMaxY());
                                    });

//                                    Image imageEllipsis = new Image("https://www.google.com.sg/url?sa=i&source=images&cd=&cad=rja&uact=8&ved=2ahUKEwiLuLWN8cbcAhWEdH0KHQ5xAXIQjRx6BAgBEAU&url=https%3A%2F%2Fwww.charbase.com%2F22ef-unicode-midline-horizontal-ellipsis&psig=AOvVaw3K7nmLaegBi6CsmUh6izNv&ust=1533042128558435");
//                                    btn.setGraphic(new ImageView(imageEllipsis));

                                    Path path = FileSystems.getDefault().getPath("src\\View\\more.png");
                                    File file = new File(path.toUri());
                                    Image imageForFile;
                                    try {
                                        imageForFile = new Image(file.toURI().toURL().toExternalForm());
                                        ImageView iv1 = new ImageView(imageForFile);
                                        iv1.setFitHeight(28);
                                        iv1.setFitWidth(40);
                                        btn.setGraphic(iv1);
                                    } catch (MalformedURLException e) {
                                        e.printStackTrace();
                                    }
                                    btn.setBorder(new Border(new BorderStroke(Color.rgb(41,221,244),
                                            BorderStrokeStyle.SOLID, CornerRadii.EMPTY, BorderWidths.DEFAULT)));

                                    setGraphic(btn);
                                    setText(null);
//                                    Image imageEllipsis = new Image(getClass().getResourceAsStream("View/horizontal_ellipsis.png"));

                                }
                            }
                        };
                        return cell;
                    }
                };

        settingsColumn.setCellFactory(cellFactory);

        dateColumn.setCellFactory((TreeTableColumn<TableBlob, String> param) -> new GenericEditableTreeTableCell<>(
                new TextFieldEditorBuilder()));
        dateColumn.setOnEditCommit((CellEditEvent<TableBlob, String> t) -> t.getTreeTableView()
                .getTreeItem(t.getTreeTablePosition()
                        .getRow())
                .getValue().date.set(t.getNewValue()));

        fileColumn.setCellFactory((TreeTableColumn<TableBlob, String> param) -> new GenericEditableTreeTableCell<>(
                new TextFieldEditorBuilder()));
        fileColumn.setOnEditCommit((CellEditEvent<TableBlob, String> t) -> t.getTreeTableView()
                .getTreeItem(t.getTreeTablePosition()
                        .getRow())
                .getValue().blobName.set(t.getNewValue()));

        final TreeItem<TableBlob> root = new RecursiveTreeItem<>(blobs, RecursiveTreeObject::getChildren);

        JFXTreeTableView = new JFXTreeTableView<>(root);
        JFXTreeTableView.setShowRoot(false);
        JFXTreeTableView.setEditable(true);
        JFXTreeTableView.getColumns().setAll(fileColumn, dateColumn, settingsColumn);
        JFXTreeTableView.setEditable(false);
        TableAnchorPane.getChildren().add(JFXTreeTableView);

//        FlowPane main = new FlowPane();
//        main.setPadding(new Insets(10));
//        anchorPane.getChildren().add(JFXTreeTableView);

        filterField.textProperty().addListener((o, oldVal, newVal) -> {
            JFXTreeTableView.setPredicate(userProp -> {
                final TableBlob blob = userProp.getValue();
                return blob.blobName.get().toLowerCase().contains(newVal.toLowerCase())
                        || blob.date.get().toLowerCase().contains(newVal.toLowerCase())
                        || blob.blobName.get().contains(newVal)
                        || blob.date.get().contains(newVal);
            });
        });

        JFXTreeTableView.addEventHandler(MouseEvent.MOUSE_PRESSED, (e) -> {
            if (e.isPrimaryButtonDown()) {
                onEdit();
            }
        });
    }

    private VBox vBox = new VBox();
    private JFXButton jfxDownloadButton = new JFXButton();
    private JFXButton jfxDeleteButton = new JFXButton();
    private int vBoxCounter = 0;

    private double minX;
    private double maxY;

    private String blobName;

    private void showVbox(double minX, double maxY) {
        double minWidth = 100;
        double minHeight = 100;
        this.minX = minX;
        this.maxY = maxY;
        if (vBoxCounter == 0) {
            vBox.setLayoutX(minX);
            vBox.setLayoutY(maxY);
            vBox.setMinSize(minWidth, minHeight);
            Background unfocusBackground = new Background(new BackgroundFill(Color.web("#F4F4F4"), CornerRadii.EMPTY, Insets.EMPTY));
            vBox.setBackground(unfocusBackground);
            jfxDownloadButton.setText("Download");
            jfxDownloadButton.setMinSize(vBox.getMinWidth(), vBox.getMinHeight() / 2);
            jfxDownloadButton.setOnAction(__ -> {
                //Download File
                if (password == null) {
                    try {
                        checkUserDownloadDelete("Download");
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else {
                    try {
                        getStorage();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    System.out.println("Download File");
                    calculateEmail();
                    try {
                        downloadFile(storage, privateBucketName, blobName);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
            jfxDeleteButton.setText("Delete");
            jfxDeleteButton.setMinSize(minWidth, vBox.getMinHeight() / 2);

            //Update this to show confirmation pop-up
            jfxDeleteButton.setOnAction(__ -> {
                System.out.println("ONCLICK DELETE File");
                myScene = anchorPane.getScene();
                Stage stage = (Stage) (myScene).getWindow();

                String title = "";
                String content = "Are you sure you want to delete this file?";

                JFXButton delete = new JFXButton("Ok, Delete");
                delete.setButtonType(JFXButton.ButtonType.RAISED);
                delete.setStyle("-fx-background-color: #00bfff;");

                JFXButton cancel = new JFXButton("Cancel");
                cancel.setButtonType(JFXButton.ButtonType.RAISED);
                cancel.setStyle("-fx-background-color: #00bfff;");

                JFXDialogLayout layout = new JFXDialogLayout();
                layout.setHeading(new Label(title));
                layout.setBody(new Label(content));
                layout.setActions(delete, cancel);
                JFXAlert<Void> alert = new JFXAlert<>(stage);
                alert.setOverlayClose(false);
                alert.setAnimation(JFXAlertAnimation.CENTER_ANIMATION);
                alert.setContent(layout);
                alert.initModality(Modality.NONE);
                delete.setOnAction(___ -> {
                    alert.hideWithAnimation();
                    calculateEmail();
                    try {
                        deleteFile(privateBucketName, blobName);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    updateTable();
                });
                cancel.setOnAction(___ -> {
                    vBox.setVisible(false);
                    myScene.removeEventFilter(MouseEvent.MOUSE_PRESSED, closeVbox);
                    alert.hideWithAnimation();
                });
                alert.show();
            });

            vBox.getChildren().addAll(jfxDownloadButton, jfxDeleteButton);
//            vBox.getChildren().add(jfxDeleteButton);
            anchorPane.getChildren().add(vBox);
            vBoxCounter++;
//            vBox.setFocusTraversable(true);
//            vBox.focusedProperty().addListener((obs, wasFocused, isNowFocused) -> {
//                if (isNowFocused) {
//                    vBox.setVisible(false);
//                }
//            });
            vBox.setVisible(true);
        } else {
            vBox.setLayoutX(minX);
            vBox.setLayoutY(maxY);
//            vBox.setMinSize(100, 200);
            Background unfocusBackground = new Background(new BackgroundFill(Color.web("#F4F4F4"), CornerRadii.EMPTY, Insets.EMPTY));
            vBox.setBackground(unfocusBackground);
            vBox.setVisible(true);
        }
        myScene = anchorPane.getScene();
        myScene.addEventFilter(MouseEvent.MOUSE_PRESSED, closeVbox);
    }

    private boolean checkUserDownloadDelete(String type) throws Exception {
        myScene = anchorPane.getScene();
        Stage stage = (Stage) (myScene).getWindow();

        String title = "Enter your password to enter the restricted area";

        JFXPasswordField jfxPasswordField = new JFXPasswordField();
        jfxPasswordField.setPromptText("Enter password");

        JFXButton jfxOKButton = new JFXButton("Ok");
        jfxOKButton.setButtonType(JFXButton.ButtonType.RAISED);
        jfxOKButton.setStyle("-fx-background-color: #00bfff;");

        JFXButton jfxcancelButton = new JFXButton("Cancel");
        jfxcancelButton.setButtonType(JFXButton.ButtonType.RAISED);
        jfxcancelButton.setStyle("-fx-background-color: #00bfff;");

        JFXAlert<Void> alert = new JFXAlert<>(stage);
        if (password == null) {
            JFXDialogLayout layout = new JFXDialogLayout();
            layout.setHeading(new Label(title));
            layout.setBody(jfxPasswordField);
            layout.setActions(jfxOKButton,jfxcancelButton);
            alert.setOverlayClose(false);
            alert.setAnimation(JFXAlertAnimation.CENTER_ANIMATION);
            alert.setContent(layout);
            alert.initModality(Modality.NONE);
            jfxOKButton.setOnAction(__ -> {
                //check
                tempPassword = jfxPasswordField.getText();
                jfxOKButton.setDisable(true);
                jfxcancelButton.setDisable(true);
                jfxPasswordField.setDisable(true);
                checkPasswordProcess.start();
            });
            jfxcancelButton.setOnAction(___ -> {
                vBox.setVisible(false);
                myScene.removeEventFilter(MouseEvent.MOUSE_PRESSED, closeVbox);
                alert.hideWithAnimation();
            });
            alert.show();
        } else {
//            password = jfxPasswordField.getText();
//            alert.hideWithAnimation();
            calculateEmail();
            //        UploadFileTest();
            if (type.equals("Download")) {
                try {
                    getStorage();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                System.out.println("Download File");
                calculateEmail();
                downloadFile(storage, privateBucketName, blobName);
            } else if (type.equals("Upload")) {
                //TODO upload codes
                //if matches continue to encrypt also need to store the password somewhere
                password = jfxPasswordField.getText();
                alert.hideWithAnimation();
                calculateEmail();
                //        UploadFileTest();
                FileChooser fileChooser = new FileChooser();
                fileChooser.setTitle("Choose File to Upload");
                //FEATURE: stage now loads as 1 page instead of 2
                Stage stage1 = (Stage) anchorPane.getScene().getWindow();
                File file = fileChooser.showOpenDialog(stage1);
                if (file != null) {
                    try {
                        if (checkNameTaken(file.getName()) == true) {
                            System.out.println("Change NAME!!!! Add showing alert");
                        } else {
                            encryptFileNew(file);
                            //may need to move update Table somewhere else instead
                            updateTable();
                        }
                    } catch (Exception e1) {
                        e1.printStackTrace();
                    }
                }
            }
        }
        checkPasswordProcess.setOnSucceeded(e -> {
            checkPasswordProcess.reset();
            //if dosen't match redo process
            if (checkPassword == false) {
                jfxPasswordField.setDisable(false);
                jfxcancelButton.setDisable(false);
                jfxOKButton.setDisable(false);
                System.out.println("Wrong password");
            } else {
                //if matches continue to encrypt also need to store the password somewhere
                password = jfxPasswordField.getText();
                alert.hideWithAnimation();
                if (type.equals("Download")) {
                    try {
                        getStorage();
                    } catch (Exception e1) {
                        e1.printStackTrace();
                    }
                    System.out.println("Download File");
                    calculateEmail();
                    try {
                        downloadFile(storage, privateBucketName, blobName);
                    } catch (Exception e1) {
                        e1.printStackTrace();
                    }
                } else if (type.equals("Upload")) {
                    //TODO upload codes
                    //if matches continue to encrypt also need to store the password somewhere
                    password = jfxPasswordField.getText();
                    alert.hideWithAnimation();
                    calculateEmail();
                    //        UploadFileTest();
                    FileChooser fileChooser = new FileChooser();
                    fileChooser.setTitle("Choose File to Upload");
                    //FEATURE: stage now loads as 1 page instead of 2
                    Stage stage1 = (Stage) anchorPane.getScene().getWindow();
                    File file = fileChooser.showOpenDialog(stage1);
                    if (file != null) {
                        try {
                            if (checkNameTaken(file.getName()) == true) {
                                System.out.println("Change NAME!!!! Add showing alert");
                            } else {
                                encryptFileNew(file);
                                //may need to move update Table somewhere else instead
                                updateTable();
                            }
                        } catch (Exception e1) {
                            e1.printStackTrace();
                        }
                    }
                }
//                //        UploadFileTest();
//                FileChooser fileChooser = new FileChooser();
//                fileChooser.setTitle("Choose File to Upload");
//                //FEATURE: stage now loads as 1 page instead of 2
//                Stage stage1 = (Stage) anchorPane.getScene().getWindow();
//                File file = fileChooser.showOpenDialog(stage1);
//                if (file != null) {
//                    try {
//                        if (checkNameTaken(file.getName()) == true) {
//                            System.out.println("Change NAME!!!! Add showing alert");
//                        } else {
//                            encryptFileNew(file);
//                            //may need to move update Table somewhere else instead
//                            updateTable();
//                        }
//                    } catch (Exception e1) {
//                        e1.printStackTrace();
//                    }
//                }
            }
        });
        checkPasswordProcess.setOnCancelled(e -> {
            checkPasswordProcess.reset();
            jfxOKButton.setDisable(false);
            jfxcancelButton.setDisable(true);
        });
        checkPasswordProcess.setOnFailed(e -> {
            checkPasswordProcess.reset();
            jfxOKButton.setDisable(false);
            jfxcancelButton.setDisable(true);
        });
        return false;
    }

    private Service checkPasswordProcess = new Service() {
        @Override
        protected Task createTask() {
            return new Task() {
                @Override
                protected Void call() throws Exception {
                    //check password against the hash in DB first then set password temporarily as a global variable
                    User_InfoDB user_infoDB = new User_InfoDB();
                    checkPassword = user_infoDB.checkPassword(tempPassword, login.getEmail());
//                    Platform.runLater(() -> {
//
//                    });
                    return null;
                }
            };
        }
    };

    private EventHandler<MouseEvent> closeVbox = new EventHandler<MouseEvent>() {
        @Override
        public void handle(MouseEvent mouseEvent) {
            System.out.println("mouse click detected! " + mouseEvent.getSource());
            System.out.println(minX + " " + maxY);
            if (mouseEvent.getX() >= minX && mouseEvent.getX() <= minX + 100 && mouseEvent.getY() >= maxY && mouseEvent.getY() <= maxY + 200) {
                System.out.println("Inside the vbox");
            } else {
                vBox.setVisible(false);
//                myScene.removeEventFilter(MouseEvent.MOUSE_PRESSED, closeVbox);
//                addListeners();
            }
        }
    };

    private void addListeners(){
        filterField.textProperty().addListener((o, oldVal, newVal) -> {
            JFXTreeTableView.setPredicate(userProp -> {
                final TableBlob blob = userProp.getValue();
                return blob.blobName.get().toLowerCase().contains(newVal.toLowerCase())
                        || blob.date.get().toLowerCase().contains(newVal.toLowerCase())
                        || blob.blobName.get().contains(newVal)
                        || blob.date.get().contains(newVal);
            });
        });

        JFXTreeTableView.addEventHandler(MouseEvent.MOUSE_PRESSED, (e) -> {
            if (e.isPrimaryButtonDown()) {
                onEdit();
            }
        });
    }

    private void updateTable() {
        System.out.println("Updating Table");
        updateTableProcess.start();
        updateTableProcess.setOnSucceeded(e -> {
            updateTableProcess.reset();
            //do other stuff
        });
        updateTableProcess.setOnCancelled(e -> {
            updateTableProcess.reset();
            //do other stuff
        });
        updateTableProcess.setOnFailed(e -> {
            updateTableProcess.reset();
            //do other stuff
        });
    }

    private Service updateTableProcess = new Service() {
        @Override
        protected Task createTask() {
            return new Task() {
                @Override
                protected Void call() throws Exception {
                    updateObservableList();
                    TableMethod();
                    return null;
                }
            };
        }
    };

    private void onEdit() {
        FileButtonsHbox.setVisible(true);
        if (JFXTreeTableView.getSelectionModel().getSelectedItem() != null) {
            TableBlob tableBlob = JFXTreeTableView.getSelectionModel().getSelectedItem().getValue();
            System.out.println(tableBlob.getBlobName());
            System.out.println(tableBlob.getDate());
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

    public static final class TableBlob extends RecursiveTreeObject<TableBlob> {
        final StringProperty blobName;
        final StringProperty date;

        TableBlob(String blobName, String date) {
            this.blobName = new SimpleStringProperty(blobName);
            this.date = new SimpleStringProperty(date);
        }

        public String getBlobName() {
            return blobName.get();
        }

        public StringProperty blobNameProperty() {
            return blobName;
        }

        public String getDate() {
            return date.get();
        }

        public StringProperty dateProperty() {
            return date;
        }
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
