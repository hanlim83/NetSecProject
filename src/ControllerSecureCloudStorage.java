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
import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
import javafx.event.EventHandler;
import javafx.geometry.Bounds;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.Parent;
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
import javafx.scene.input.ScrollEvent;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
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
    private JFXTextField filterField;

    @FXML
    private Label FilePathLabel;

    @FXML
    private JFXButton homeButton;

    @FXML
    private JFXButton backButton;

    @FXML
    private JFXSpinner JFXSpinner;

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
        Path path = FileSystems.getDefault().getPath("src/View/baseline_cloud_upload_black_18dp_small.png");
        File file = new File(path.toUri());
        Image imageForFile;
        try {
            imageForFile = new Image(file.toURI().toURL().toExternalForm());
            ImageView imageView = new ImageView(imageForFile);
//            imageView.setFitHeight(24.5);
//            imageView.setFitWidth(35);
            UploadButton.setGraphic(imageView);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        Path path1 = FileSystems.getDefault().getPath("src/View/baseline_home_white_18dp.png");
        File file1 = new File(path1.toUri());
        Image imageForFile1;
        try {
            imageForFile1 = new Image(file1.toURI().toURL().toExternalForm());
            ImageView imageView1 = new ImageView(imageForFile1);
//            imageView.setFitHeight(24.5);
//            imageView.setFitWidth(35);
            homeButton.setGraphic(imageView1);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
    }

    private ObservableList<ControllerSecureCloudStorage.TableBlob> blobsListFirst;

    public void loadTableProcess() {
        process.start();
        process.setOnSucceeded(e -> {
            JFXSpinner.setVisible(false);
            process.reset();
            blobs = blobsListFirst;
            TableMethod();
        });
        process.setOnCancelled(e -> {
            process.reset();
        });
        process.setOnFailed(e -> {
            process.reset();
        });
    }

    //restart testing to optimize this page
    Service process = new Service() {
        @Override
        protected Task createTask() {
            return new Task() {
                @Override
                protected Void call() throws Exception {
                    getObservableList();
                    return null;
                }
            };
        }
    };

    private OAuth2Login login1 = new OAuth2Login();

    private ArrayList<String> arrayFolder1 = new ArrayList<String>();

    private ObservableList<ControllerSecureCloudStorage.TableBlob> blobs1 = FXCollections.observableArrayList();

    private ObservableList<ControllerSecureCloudStorage.TableBlob> getObservableList() throws Exception {
        Credential credential1 = login1.login();
        credential = credential1;
        ObservableList<ControllerSecureCloudStorage.TableBlob> blobs;
        blobs1 = FXCollections.observableArrayList();
        Storage storage = StorageOptions.newBuilder().setCredentials(GoogleCredentials.create(new AccessToken(credential1.getAccessToken(), null))).build().getService();
        String email = null;
        try {
            email = login.getEmail();
        } catch (IOException e) {
            e.printStackTrace();
        }
        Scanner s = new Scanner(email).useDelimiter("@");
        String emailFront = s.next();
        emailFront = emailFront.replace(".", "");
        String privateBucketName = emailFront + "nspj";
        Page<Blob> blobList = storage.list(privateBucketName);
        for (Blob blob : blobList.iterateAll()) {
//            BlobList.add(new MyBlob(blob));
            //if it is folder only add in once check here

            if (blob.getName().contains("/")) {
                Scanner s1 = new Scanner(blob.getName()).useDelimiter("/");
                String folderName = s1.next();
//                String filename=s1.next();
                if (checkFolderName1(folderName) == false) {
                    arrayFolder1.add(folderName);
                    blobs1.add(new ControllerSecureCloudStorage.TableBlob(folderName, convertTime1(blob.getCreateTime()), folderName, "Folder"));
                }
            } else {
                blobs1.add(new ControllerSecureCloudStorage.TableBlob(blob.getName(), convertTime1(blob.getCreateTime()), "", "File"));
            }
        }
        blobsListFirst = blobs1;
        return blobs1;
    }

    private String convertTime1(long time) {
        Date date = new Date(time);
        Format format = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
        return format.format(date);
    }

    private boolean checkFolderName1(String folderName) {
        boolean check = false;
        for (String s : arrayFolder1) {
            if (folderName.equals(s)) {
                check = true;
                break;
            } else {
                check = false;
            }
        }
        return check;
    }

    @FXML
    void onClickHomeButton(ActionEvent event) throws IOException {
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getResource("UserHome.fxml"));
        myScene = (Scene) ((Node) event.getSource()).getScene();
        Stage stage = (Stage) (myScene).getWindow();
        Parent nextView = loader.load();

        ControllerUserHome controller = loader.<ControllerUserHome>getController();
        //controller.passData(admin);

        stage.setScene(new Scene(nextView));
        stage.setTitle("NSPJ");
        stage.show();
    }

    public void passData(ObservableList<TableBlob> blobs) {
        this.blobs = blobs;
        TableMethod();
    }

    @FXML
    private void onClickBackButton(ActionEvent event) throws Exception {
        updateObservableList();
        backButton.setVisible(false);
        FilePathLabel.setText("/");
    }

    @FXML
    private void onClickUploadButton(ActionEvent event) throws Exception {
        checkUserDownloadDelete("Upload");
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

        updateObservableList();

        JFXSnackbar snackbar = new JFXSnackbar(anchorPane);
        snackbar.show("Upload success", 3000);
        snackbar.getStylesheets().add("Style.css");
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
                Blob blob = bucket.create(filename, input, "text/plain");
            }
        }
    }

    private Service processUpload = new Service() {
        @Override
        protected Task createTask() {
            return new Task() {
                @Override
                protected Void call() throws Exception {

                    return null;
                }
            };
        }
    };

    //Download,saving and decrypt is here now
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

            JFXSnackbar snackbar = new JFXSnackbar(anchorPane);
            snackbar.show("Download success", 3000);
            snackbar.getStylesheets().add("Style.css");
        }
    }

    private String bucketNameDelete;
    private String deleteblobName;

    private void deleteFile(String bucketName, String blobName){
        bucketNameDelete = bucketName;
        deleteblobName = blobName;
        JFXSpinner.setVisible(true);
        processDelete.start();
        processDelete.setOnSucceeded(e -> {
            processDelete.reset();
            JFXSpinner.setVisible(false);
            try {
                updateObservableList();
            } catch (Exception e1) {
                e1.printStackTrace();
            }
        });
        processDelete.setOnCancelled(e -> {
            processDelete.reset();
            JFXSpinner.setVisible(false);
        });
        processDelete.setOnFailed(e -> {
            processDelete.reset();
            JFXSpinner.setVisible(false);
        });
    }

    private Service processDelete = new Service() {
        @Override
        protected Task createTask() {
            return new Task() {
                @Override
                protected Void call() throws Exception {
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
                    BlobId blobId = BlobId.of(bucketNameDelete, deleteblobName);
                    boolean deleted = storage.delete(blobId);
                    if (deleted) {
                        // the blob was deleted
                        Platform.runLater(() -> {
                            JFXSnackbar snackbar = new JFXSnackbar(anchorPane);
                            snackbar.show("Delete success", 3000);
                            snackbar.getStylesheets().add("Style.css");
                        });
                        try {
                            updateObservableList();
                        } catch (Exception e1) {
                            e1.printStackTrace();
                        }
                    } else {
                        // the blob was not found
                        System.out.println("Not deleted not found");
                    }
                    return null;
                }
            };
        }
    };

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

    private boolean checkPassword;
    private String tempPassword;

    private ArrayList<String> arrayFolder = new ArrayList<String>();

    private void updateObservableList() throws Exception {
        processUpdateObservableList.start();
        processUpdateObservableList.setOnSucceeded(e -> {
            processUpdateObservableList.reset();
        });
        processUpdateObservableList.setOnCancelled(e -> {
            processUpdateObservableList.reset();
        });
        processUpdateObservableList.setOnFailed(e -> {
            processUpdateObservableList.reset();
        });

//        try {
//            if (storage == null) {
//                getStorage();
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//            getStorage();
//        }
//        if (privateBucketName == null) {
//            calculateEmail();
//        }
//        Page<Blob> blobList = storage.list(privateBucketName);
//        blobs.clear();
//        arrayFolder.clear();
//        for (Blob blob : blobList.iterateAll()) {
////            BlobList.add(new MyBlob(blob));
//            //if it is folder only add in once check here
//
//            if (blob.getName().contains("/")) {
//                Scanner s = new Scanner(blob.getName()).useDelimiter("/");
//                String folderName = s.next();
////                String filename=s.next();
//                if (checkFolderName(folderName) == false) {
//                    arrayFolder.add(folderName);
//                    blobs.add(new ControllerSecureCloudStorage.TableBlob(folderName, convertTime(blob.getCreateTime()), folderName, "Folder"));
//                }
//            } else {
//                blobs.add(new ControllerSecureCloudStorage.TableBlob(blob.getName(), convertTime(blob.getCreateTime()), "", "File"));
//            }
//        }
//
//        JFXTreeTableView.unGroup(folderColumn);
//        JFXTreeTableView.unGroup(typeColumn);
//        JFXTreeTableView.getColumns().clear();
//        JFXTreeTableView.getColumns().setAll(typeColumn, fileColumn, dateColumn, settingsColumn);
//        JFXTreeTableView.group(typeColumn);
//        fileColumn.setPrefWidth(390);
//        dateColumn.setPrefWidth(350);
//        settingsColumn.setPrefWidth(150);
//        typeColumn.setPrefWidth(156);
    }

    //Service here
    private Service processUpdateObservableList = new Service() {
        @Override
        protected Task createTask() {
            return new Task() {
                @Override
                protected Void call() throws Exception {
                    JFXSpinner.setVisible(true);
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
                    arrayFolder.clear();
                    for (Blob blob : blobList.iterateAll()) {
//            BlobList.add(new MyBlob(blob));
                        //if it is folder only add in once check here

                        if (blob.getName().contains("/")) {
                            Scanner s = new Scanner(blob.getName()).useDelimiter("/");
                            String folderName = s.next();
//                String filename=s.next();
                            if (checkFolderName(folderName) == false) {
                                arrayFolder.add(folderName);
                                blobs.add(new ControllerSecureCloudStorage.TableBlob(folderName, convertTime(blob.getCreateTime()), folderName, "Folder"));
                            }
                        } else {
                            blobs.add(new ControllerSecureCloudStorage.TableBlob(blob.getName(), convertTime(blob.getCreateTime()), "", "File"));
                        }
                    }
                    Platform.runLater(() -> {
                        JFXTreeTableView.unGroup(folderColumn);
                        JFXTreeTableView.unGroup(typeColumn);
                        JFXTreeTableView.getColumns().clear();
                        JFXTreeTableView.getColumns().setAll(typeColumn, fileColumn, dateColumn, settingsColumn);
                        JFXTreeTableView.group(typeColumn);
                        fileColumn.setPrefWidth(390);
                        dateColumn.setPrefWidth(350);
                        settingsColumn.setPrefWidth(150);
                        typeColumn.setPrefWidth(156);
                        JFXSpinner.setVisible(false);
                    });
                    return null;
                }
            };
        }
    };

    private boolean checkFolderName(String folderName) {
        boolean check = false;
        for (String s : arrayFolder) {
            if (folderName.equals(s)) {
                check = true;
                break;
            } else {
                check = false;
            }
        }
        return check;
    }

    private JFXTreeTableColumn<TableBlob, String> fileColumn = new JFXTreeTableColumn<>("Name");
    private JFXTreeTableColumn<TableBlob, String> dateColumn = new JFXTreeTableColumn<>("Date");
    private JFXTreeTableColumn<TableBlob, String> settingsColumn = new JFXTreeTableColumn<>("Actions");
    private JFXTreeTableColumn<TableBlob, String> folderColumn = new JFXTreeTableColumn<>("Folder");
    private JFXTreeTableColumn<TableBlob, String> typeColumn = new JFXTreeTableColumn<>("Type");

    //for building table etc.
    private void TableMethod() {
        fileColumn.setPrefWidth(390);
        fileColumn.setCellValueFactory((TreeTableColumn.CellDataFeatures<TableBlob, String> param) -> {
            if (fileColumn.validateValue(param)) {
                return param.getValue().getValue().blobName;
            } else {
                return fileColumn.getComputedValue(param);
            }
        });

        dateColumn.setPrefWidth(350);
        dateColumn.setCellValueFactory((TreeTableColumn.CellDataFeatures<TableBlob, String> param) -> {
            if (dateColumn.validateValue(param)) {
                return param.getValue().getValue().date;
            } else {
                return dateColumn.getComputedValue(param);
            }
        });

        settingsColumn.setPrefWidth(150);
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
                                    try {
//                                        System.out.println(item);
                                        int selectdIndex = getTreeTableRow().getIndex();
                                        System.out.println(selectdIndex);
                                        TableBlob tableBlob = JFXTreeTableView.getSelectionModel().getModelItem(selectdIndex).getValue();
                                        if (tableBlob.getType2().equals("Folder")) {
                                            JFXTreeTableView.refresh();
                                            return;
                                        } else {
                                            btn.setOnAction(event -> {
                                                System.out.println(tableBlob.getBlobName());
                                                blobName = tableBlob.getBlobName();
                                                System.out.println(tableBlob.getDate());
                                                Bounds boundsInScene = btn.localToScene(btn.getBoundsInLocal());
                                                Bounds boundsInScene1 = anchorPane.localToScene(anchorPane.getBoundsInLocal());
                                                if (boundsInScene1.getMaxY() - boundsInScene.getMaxY() > 100) {
                                                    showVbox(boundsInScene.getMinX(), boundsInScene.getMaxY(), true);
                                                    //down is true
                                                    System.out.println("going down");
                                                } else {
                                                    showVbox(boundsInScene.getMinX(), boundsInScene.getMinY() - 100, false);
                                                    System.out.println("going up");
                                                    //up is false
                                                }
                                            });

                                            btn.setId("tableJFXButton");

                                            Path path = FileSystems.getDefault().getPath("src\\View\\more.png");
                                            File file = new File(path.toUri());
                                            Image imageForFile;
                                            try {
                                                imageForFile = new Image(file.toURI().toURL().toExternalForm());
                                                ImageView iv1 = new ImageView(imageForFile);
                                                iv1.setFitHeight(24.5);
                                                iv1.setFitWidth(35);
                                                btn.setGraphic(iv1);
                                            } catch (MalformedURLException e) {
                                                e.printStackTrace();
                                            }
//                                    btn.setBorder(new Border(new BorderStroke(Color.BLACK,
//                                            BorderStrokeStyle.SOLID, CornerRadii.EMPTY, BorderWidths.DEFAULT)));
//                                    btn.setStyle("-fx-border-width: 5;");
//                                    btn.setStyle("-fx-border-radius: 5;");
                                            btn.getStylesheets().add("Style.css");
                                            btn.setOnMouseEntered(event -> {
//                                        btn.setStyle("-fx-border-radius: 5;");
                                            });

                                            btn.setOnMouseExited(event -> {

                                            });

                                            setGraphic(btn);
                                            setText(null);
//                                    Image imageEllipsis = new Image(getClass().getResourceAsStream("View/horizontal_ellipsis.png"));
                                        }
                                    } catch (ClassCastException e) {
//                                        e.printStackTrace();
                                        JFXTreeTableView.refresh();
                                        return;
                                    } catch (NullPointerException e) {
//                                        e.printStackTrace();
                                        JFXTreeTableView.refresh();
                                        return;
                                    }
                                }
                            }
                        };
                        return cell;
                    }
                };

        settingsColumn.setCellFactory(cellFactory);

//        folderColumn.setPrefWidth(150);
//        folderColumn.setCellValueFactory((TreeTableColumn.CellDataFeatures<TableBlob, String> param) -> {
//            if (folderColumn.validateValue(param)) {
//                return param.getValue().getValue().folderName;
//            } else {
//                return folderColumn.getComputedValue(param);
//            }
//        });

        typeColumn.setPrefWidth(156);
        typeColumn.setCellValueFactory((TreeTableColumn.CellDataFeatures<TableBlob, String> param) -> {
            if (typeColumn.validateValue(param)) {
                return param.getValue().getValue().type2;
            } else {
                return typeColumn.getComputedValue(param);
            }
        });

//        folderColumn.setCellFactory((TreeTableColumn<TableBlob, String> param) -> new GenericEditableTreeTableCell<>(
//                new TextFieldEditorBuilder()));
//        folderColumn.setOnEditCommit((CellEditEvent<TableBlob, String> t) -> t.getTreeTableView()
//                .getTreeItem(t.getTreeTablePosition()
//                        .getRow())
//                .getValue().folderName.set(t.getNewValue()));

        typeColumn.setCellFactory((TreeTableColumn<TableBlob, String> param) -> new GenericEditableTreeTableCell<>(
                new TextFieldEditorBuilder()));
        typeColumn.setOnEditCommit((CellEditEvent<TableBlob, String> t) -> t.getTreeTableView()
                .getTreeItem(t.getTreeTablePosition()
                        .getRow())
                .getValue().type2.set(t.getNewValue()));

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
        JFXTreeTableView.getColumns().setAll(typeColumn, fileColumn, dateColumn, settingsColumn);
        JFXTreeTableView.setEditable(false);
        JFXTreeTableView.setPrefHeight(451);
        TableAnchorPane.getChildren().add(JFXTreeTableView);

//        FlowPane main = new FlowPane();
//        main.setPadding(new Insets(10));
//        anchorPane.getChildren().add(JFXTreeTableView);

//        JFXButton groupButton = new JFXButton("Group");
//        UploadButton.setOnAction((action) ->{JFXTreeTableView.unGroup(typeColumn);
//            JFXTreeTableView.getColumns().clear();
//            JFXTreeTableView.getColumns().setAll(folderColumn, fileColumn, dateColumn, settingsColumn);
//            new Thread(() -> JFXTreeTableView.group(folderColumn)).start();
//        });

        JFXTreeTableView.group(typeColumn);

//        JFXTreeTableView.getColumns().clear();
//        JFXTreeTableView.getColumns().setAll(typeColumn, fileColumn, dateColumn, settingsColumn);
//        main.getChildren().add(groupButton);

        filterField.textProperty().addListener((o, oldVal, newVal) -> {
            JFXTreeTableView.setPredicate(userProp -> {
                final TableBlob blob = userProp.getValue();
                return blob.blobName.get().toLowerCase().contains(newVal.toLowerCase())
                        || blob.date.get().toLowerCase().contains(newVal.toLowerCase())
                        || blob.blobName.get().contains(newVal)
                        || blob.date.get().contains(newVal);
            });
        });

        JFXTreeTableView.addEventFilter(ScrollEvent.ANY, new EventHandler<ScrollEvent>() {
            @Override
            public void handle(ScrollEvent scrollEvent) {
                System.out.println("Scrolled.");
                vBox.setVisible(false);
            }
        });

        JFXTreeTableView.addEventHandler(MouseEvent.MOUSE_PRESSED, (e) -> {
            if (e.isPrimaryButtonDown()) {
                if (JFXTreeTableView.getSelectionModel().getSelectedItem() != null) {
//            TableBlob tableBlob = JFXTreeTableView.getSelectionModel().getSelectedItem().getValue();
//            System.out.println(tableBlob.getBlobName());
//            System.out.println(tableBlob.getDate());
//                    System.out.println(JFXTreeTableView.getSelectionModel().getSelectedItem().getValue().type);
                    if (JFXTreeTableView.getSelectionModel().getSelectedItem().getValue().getType2().equals("Folder")) {
//                        System.out.println(JFXTreeTableView.getSelectionModel().getSelectedItem().getValue().getFolderName());
//                        String folderName = JFXTreeTableView.getSelectionModel().getSelectedItem().getValue().getFolderName();
//                        //clear observable list and repopulate it with only that particular folder info
////                blobs.clear();
//
//                        try {
//                            if (storage == null) {
//                                getStorage();
//                            }
//                        } catch (Exception e1) {
//                            e1.printStackTrace();
//                            try {
//                                getStorage();
//                            } catch (Exception e2) {
//                                e2.printStackTrace();
//                            }
//                        }
//                        if (privateBucketName == null) {
//                            calculateEmail();
//                        }
//                        Page<Blob> blobList = storage.list(privateBucketName);
//                        blobs.clear();
//                        for (Blob blob : blobList.iterateAll()) {
//                            if (blob.getName().startsWith(folderName)) {
////                                if (blob.getName().contains("/")){
////                                    Scanner s = new Scanner(blob.getName()).useDelimiter("/");
////                                    String folderName=s.next();
//////                String filename=s.next();
////                                    if (checkFolderName(folderName) == false) {
////                                        arrayFolder.add(folderName);
////                                        blobs.add(new ControllerSecureCloudStorage.TableBlob(folderName, convertTime(blob.getCreateTime()),folderName,"General","Folder"));
////                                    }
////                                }
//                                Scanner s = new Scanner(blob.getName()).useDelimiter("/");
//                                String folderName1 = s.next();
////                                String fileName = null;
//                                if (s.hasNext()) {
//                                    String fileName = s.next();
//                                    blobs.add(new ControllerSecureCloudStorage.TableBlob(fileName, convertTime(blob.getCreateTime()), folderName, "File"));
////                                    }
//                                }
//                            }
//                        }
//
//                        JFXTreeTableView.unGroup(folderColumn);
//                        JFXTreeTableView.unGroup(typeColumn);
//                        JFXTreeTableView.getColumns().clear();
//                        JFXTreeTableView.getColumns().setAll(fileColumn, dateColumn, settingsColumn);
//                        JFXTreeTableView.getColumns();
//                        fileColumn.setPrefWidth(544);
//                        backButton.setVisible(true);
//                        FilePathLabel.setText("/" + folderName);
                        try {
                            updateObservableListFolder();
                        } catch (Exception e1) {
                            e1.printStackTrace();
                        }
                    }
                }
            }
        });
    }

    private void updateObservableListFolder() {
        processUpdateObservableListFolder.start();
        processUpdateObservableListFolder.setOnSucceeded(e -> {
            processUpdateObservableListFolder.reset();
        });
        processUpdateObservableListFolder.setOnCancelled(e -> {
            processUpdateObservableListFolder.reset();
        });
        processUpdateObservableListFolder.setOnFailed(e -> {
            processUpdateObservableListFolder.reset();
        });
    }

    //Service here
    private Service processUpdateObservableListFolder = new Service() {
        @Override
        protected Task createTask() {
            return new Task() {
                @Override
                protected Void call() throws Exception {
                    JFXSpinner.setVisible(true);
                    String folderName = JFXTreeTableView.getSelectionModel().getSelectedItem().getValue().getFolderName();
                    //clear observable list and repopulate it with only that particular folder info
//                blobs.clear();

                    try {
                        if (storage == null) {
                            getStorage();
                        }
                    } catch (Exception e1) {
                        e1.printStackTrace();
                        try {
                            getStorage();
                        } catch (Exception e2) {
                            e2.printStackTrace();
                        }
                    }
                    if (privateBucketName == null) {
                        calculateEmail();
                    }
                    Page<Blob> blobList = storage.list(privateBucketName);
                    blobs.clear();
                    for (Blob blob : blobList.iterateAll()) {
                        if (blob.getName().startsWith(folderName)) {
//                                if (blob.getName().contains("/")){
//                                    Scanner s = new Scanner(blob.getName()).useDelimiter("/");
//                                    String folderName=s.next();
////                String filename=s.next();
//                                    if (checkFolderName(folderName) == false) {
//                                        arrayFolder.add(folderName);
//                                        blobs.add(new ControllerSecureCloudStorage.TableBlob(folderName, convertTime(blob.getCreateTime()),folderName,"General","Folder"));
//                                    }
//                                }
                            Scanner s = new Scanner(blob.getName()).useDelimiter("/");
                            String folderName1 = s.next();
//                                String fileName = null;
                            if (s.hasNext()) {
                                String fileName = s.next();
                                blobs.add(new ControllerSecureCloudStorage.TableBlob(fileName, convertTime(blob.getCreateTime()), folderName, "File"));
                            }
                        }
                    }
                    Platform.runLater(() -> {
                        JFXTreeTableView.unGroup(folderColumn);
                        JFXTreeTableView.unGroup(typeColumn);
                        JFXTreeTableView.getColumns().clear();
                        JFXTreeTableView.getColumns().setAll(fileColumn, dateColumn, settingsColumn);
                        JFXTreeTableView.getColumns();
                        fileColumn.setPrefWidth(544);
                        backButton.setVisible(true);
                        FilePathLabel.setText("/" + folderName);
                        JFXSpinner.setVisible(false);
                    });
                    return null;
                }
            };
        }
    };

    private VBox vBox = new VBox();
    private JFXButton jfxDownloadButton = new JFXButton();
    private JFXButton jfxDeleteButton = new JFXButton();
    private int vBoxCounter = 0;

    private double minX;
    private double maxY;
    private boolean direction;

    private String blobName;

    private void showVbox(double minX, double maxY, boolean direction) {
        System.out.println("showing Vbox");
        double minWidth = 100;
        double minHeight = 100;
        this.minX = minX;
        this.maxY = maxY;
        this.direction = direction;
        if (vBoxCounter == 0) {
            if (direction == false) {
                vBox.setLayoutX(minX);
                vBox.setLayoutY(maxY);
                vBox.setMinSize(minWidth, minHeight);
                Background unfocusBackground = new Background(new BackgroundFill(Color.web("#F4F4F4"), CornerRadii.EMPTY, Insets.EMPTY));
                vBox.setBackground(unfocusBackground);
            } else {
                vBox.setLayoutX(minX);
                vBox.setLayoutY(maxY);
                vBox.setMinSize(minWidth, minHeight);
                Background unfocusBackground = new Background(new BackgroundFill(Color.web("#F4F4F4"), CornerRadii.EMPTY, Insets.EMPTY));
                vBox.setBackground(unfocusBackground);
            }

            jfxDownloadButton.setText("Download");
            jfxDownloadButton.setFont(Font.font("System", 15));
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
            jfxDeleteButton.setFont(Font.font("System", 15));
            jfxDeleteButton.setMinSize(minWidth, vBox.getMinHeight() / 2);

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
//                    calculateEmail();
                    try {
                        getStorage();
                        deleteFile(privateBucketName, blobName);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
//                    updateTable();
//                    try {
//                        updateObservableList();
//                    } catch (Exception e) {
//                        e.printStackTrace();
//                    }
                });
                cancel.setOnAction(___ -> {
                    vBox.setVisible(false);
                    myScene.removeEventFilter(MouseEvent.MOUSE_PRESSED, closeVbox);
                    alert.hideWithAnimation();
                });
                alert.show();
            });

            vBox.getChildren().addAll(jfxDownloadButton, jfxDeleteButton);
            anchorPane.getChildren().add(vBox);
            vBoxCounter++;
            vBox.setVisible(true);
        } else {
            if (direction == false) {
                vBox.setLayoutX(minX);
                vBox.setLayoutY(maxY);
                vBox.setMinSize(minWidth, minHeight);
                Background unfocusBackground = new Background(new BackgroundFill(Color.web("#F4F4F4"), CornerRadii.EMPTY, Insets.EMPTY));
                vBox.setBackground(unfocusBackground);
                vBox.setVisible(true);
            } else {
                vBox.setLayoutX(minX);
                vBox.setLayoutY(maxY);
                vBox.setMinSize(minWidth, minHeight);
                Background unfocusBackground = new Background(new BackgroundFill(Color.web("#F4F4F4"), CornerRadii.EMPTY, Insets.EMPTY));
                vBox.setBackground(unfocusBackground);
                vBox.setVisible(true);
            }
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
            layout.setActions(jfxOKButton, jfxcancelButton);
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
                //if matches continue to encrypt also need to store the password somewhere
                password = jfxPasswordField.getText();
                alert.hideWithAnimation();
                calculateEmail();
                FileChooser fileChooser = new FileChooser();
                fileChooser.setTitle("Choose File to Upload");
                //FEATURE: stage now loads as 1 page instead of 2
                Stage stage1 = (Stage) anchorPane.getScene().getWindow();
                File file = fileChooser.showOpenDialog(stage1);
                if (file != null) {
                    try {
                        if (checkNameTaken(file.getName()) == true) {
                            System.out.println("Change NAME!!!! Add showing alert");
                            changeNameAlert("Please change your file name");
                        } else if (CheckFileSafe(file.getAbsolutePath()) == false) {
                            myScene = anchorPane.getScene();
                            Stage stage2 = (Stage) (myScene).getWindow();

                            String title1 = "";
                            String content = "Your file contains virus. It will not be allowed to be uploaded";

                            JFXButton close = new JFXButton("Close");
                            close.setButtonType(JFXButton.ButtonType.RAISED);
                            close.setStyle("-fx-background-color: #00bfff;");

                            JFXButton report = new JFXButton("Go to report");
                            report.setButtonType(JFXButton.ButtonType.RAISED);
                            report.setStyle("-fx-background-color: #00bfff;");

                            JFXDialogLayout layout = new JFXDialogLayout();
                            layout.setHeading(new Label(title));
                            layout.setBody(new Label(content));
                            layout.setActions(close, report);
                            JFXAlert<Void> alert1 = new JFXAlert<>(stage2);
                            alert1.setOverlayClose(true);
                            alert1.setAnimation(JFXAlertAnimation.CENTER_ANIMATION);
                            alert1.setContent(layout);
                            alert1.initModality(Modality.NONE);
                            close.setOnAction(__ -> alert1.hideWithAnimation());
                            report.setOnAction(__ -> {
                                alert1.hideWithAnimation();
                                FXMLLoader loader = new FXMLLoader();
                                loader.setLocation(getClass().getResource("ScanReport.fxml"));
                                Stage stage3 = (Stage) (myScene).getWindow();
                                Parent nextView = null;
                                try {
                                    nextView = loader.load();
                                } catch (IOException e1) {
                                    e1.printStackTrace();
                                }

                                ControllerScanReport controller = loader.<ControllerScanReport>getController();

                                stage3.setScene(new Scene(nextView));
                                stage3.setTitle("NSPJ");
                                stage3.show();
                            });
                            alert1.show();
                        } else {
                            encryptFileNew(file);
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
                myScene = anchorPane.getScene();
                Stage stage1 = (Stage) (myScene).getWindow();

                String title1 = "Alert";
                String content1 = "Wrong Password";

                JFXButton close1 = new JFXButton("Close");

                close1.setButtonType(JFXButton.ButtonType.RAISED);

                close1.setStyle("-fx-background-color: #00bfff;");

                JFXDialogLayout layout = new JFXDialogLayout();
                layout.setHeading(new Label(title1));
                layout.setBody(new Label(content1));
                layout.setActions(close1);
                JFXAlert<Void> alert1 = new JFXAlert<>(stage1);
                alert1.setOverlayClose(true);
                alert1.setAnimation(JFXAlertAnimation.CENTER_ANIMATION);
                alert1.setContent(layout);
                alert1.initModality(Modality.NONE);
                close1.setOnAction(__ -> alert1.hideWithAnimation());
                alert1.show();
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
                    FileChooser fileChooser = new FileChooser();
                    fileChooser.setTitle("Choose File to Upload");
                    //FEATURE: stage now loads as 1 page instead of 2
                    Stage stage1 = (Stage) anchorPane.getScene().getWindow();
                    File file = fileChooser.showOpenDialog(stage1);
                    if (file != null) {
                        try {
                            if (checkNameTaken(file.getName()) == true) {
                                System.out.println("Change NAME!!!! Add showing alert");
                                changeNameAlert("Please change your file name");
                            } else if (CheckFileSafe(file.getAbsolutePath()) == false) {
                                myScene = anchorPane.getScene();
                                Stage stage2 = (Stage) (myScene).getWindow();

                                String title1 = "";
                                String content = "Your file contains virus. It will not be allowed to be uploaded";

                                JFXButton close = new JFXButton("Close");
                                close.setButtonType(JFXButton.ButtonType.RAISED);
                                close.setStyle("-fx-background-color: #00bfff;");

                                JFXButton report = new JFXButton("Go to report");
                                report.setButtonType(JFXButton.ButtonType.RAISED);
                                report.setStyle("-fx-background-color: #00bfff;");

                                JFXDialogLayout layout = new JFXDialogLayout();
                                layout.setHeading(new Label(title));
                                layout.setBody(new Label(content));
                                layout.setActions(close, report);
                                JFXAlert<Void> alert1 = new JFXAlert<>(stage2);
                                alert1.setOverlayClose(true);
                                alert1.setAnimation(JFXAlertAnimation.CENTER_ANIMATION);
                                alert1.setContent(layout);
                                alert1.initModality(Modality.NONE);
                                close.setOnAction(__ -> alert1.hideWithAnimation());
                                report.setOnAction(__ -> {
                                    alert1.hideWithAnimation();
                                    FXMLLoader loader = new FXMLLoader();
                                    loader.setLocation(getClass().getResource("ScanReport.fxml"));
                                    Stage stage3 = (Stage) (myScene).getWindow();
                                    Parent nextView = null;
                                    try {
                                        nextView = loader.load();
                                    } catch (IOException e1) {
                                        e1.printStackTrace();
                                    }

                                    ControllerScanReport controller = loader.<ControllerScanReport>getController();

                                    stage3.setScene(new Scene(nextView));
                                    stage3.setTitle("NSPJ");
                                    stage3.show();
                                });
                                alert1.show();
                            } else {
                                encryptFileNew(file);
                                updateTable();
                            }
                        } catch (Exception e1) {
                            e1.printStackTrace();
                        }
                    }
                }
            }
        });
        checkPasswordProcess.setOnCancelled(e -> {
            checkPasswordProcess.reset();
            jfxOKButton.setDisable(false);
            jfxcancelButton.setDisable(false);
        });
        checkPasswordProcess.setOnFailed(e -> {
            checkPasswordProcess.reset();
            jfxOKButton.setDisable(false);
            jfxcancelButton.setDisable(false);
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
                    return null;
                }
            };
        }
    };

    private boolean CheckFileSafe(String filepath) {
        FileScanner fileScanner = new FileScanner();
        fileScanner.Scanner(filepath);
        return fileScanner.scannerReport();
    }

    private void changeNameAlert(String message) {
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

    private EventHandler<MouseEvent> closeVbox = new EventHandler<MouseEvent>() {
        @Override
        public void handle(MouseEvent mouseEvent) {
            System.out.println("mouse click detected! " + mouseEvent.getSource());
            System.out.println(minX + " " + maxY);
            if (direction == true) {
                if (mouseEvent.getX() >= minX && mouseEvent.getX() <= minX + 100 && mouseEvent.getY() >= maxY && mouseEvent.getY() <= maxY + 100) {
                    System.out.println("Inside the vbox");
                } else {
                    vBox.setVisible(false);
                    myScene.removeEventFilter(MouseEvent.MOUSE_PRESSED, closeVbox);
                    System.out.println("Close VBox");
                }
            } else {
                if (mouseEvent.getX() >= minX && mouseEvent.getX() <= minX + 100 && mouseEvent.getY() >= maxY && mouseEvent.getY() <= maxY + 100) {
                    System.out.println("Inside the vbox");
                } else {
                    vBox.setVisible(false);
                    myScene.removeEventFilter(MouseEvent.MOUSE_PRESSED, closeVbox);
                    System.out.println("Close VBox");
                }
            }
        }
    };

    private void updateTable() {
        System.out.println("Updating Table");
        updateTableProcess.start();
        updateTableProcess.setOnSucceeded(e -> {
            updateTableProcess.reset();
            System.out.println("Update table success");
            //do other stuff
        });
        updateTableProcess.setOnCancelled(e -> {
            updateTableProcess.reset();
            System.out.println("Update table cancelled");
            //do other stuff
        });
        updateTableProcess.setOnFailed(e -> {
            updateTableProcess.reset();
            System.out.println("Update table failed");
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
                    return null;
                }
            };
        }
    };

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
        final StringProperty folderName;
        final StringProperty type2;

        //take in blobname can be file/folder, date, type, General/File, type
//        TableBlob(String blobName, String date, String folderName, String type, String type2) {
        TableBlob(String blobName, String date, String folderName, String type2) {
//            if(blobName.contains("/")){
//                Scanner s = new Scanner(blobName).useDelimiter("/");
//                String folderName = s.next();
//                this.folderName = new SimpleStringProperty(folderName);
////                String filename = s.next();
//                if (type.equals("General")) {
//                    this.blobName = new SimpleStringProperty(folderName);
//                } else{
//                    this.blobName = new SimpleStringProperty(folderName);
//                }
////                this.blobName = new SimpleStringProperty(folderName);
//                this.type =  new SimpleStringProperty("Folder");
//            }
//            else{
//                this.blobName = new SimpleStringProperty(blobName);
//                this.type = new SimpleStringProperty("File");
//                this.folderName = new SimpleStringProperty("");
//            }
            if (type2.equals("Folder")) {
                this.type2 = new SimpleStringProperty("Folder");
                this.folderName = new SimpleStringProperty(folderName);
            } else {
                this.type2 = new SimpleStringProperty("File");
                this.folderName = new SimpleStringProperty("");
            }
//            this.folderName = new SimpleStringProperty(blobName);
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

        public String getFolderName() {
            return folderName.get();
        }

        public StringProperty folderNameProperty() {
            return folderName;
        }

        public void setFolderName(String folderName) {
            this.folderName.set(folderName);
        }

        public String getType2() {
            return type2.get();
        }

        public StringProperty type2Property() {
            return type2;
        }

        public void setType2(String type2) {
            this.type2.set(type2);
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
