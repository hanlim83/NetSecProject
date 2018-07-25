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
import javafx.scene.input.MouseEvent;
import javafx.scene.input.ScrollEvent;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Callback;
import org.json.simple.JSONObject;

import javax.crypto.*;
import javax.crypto.spec.IvParameterSpec;
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
import java.util.function.Function;
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
        checkUserPassword();
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

//        FileChooser fileChooser1 = new FileChooser();
//        fileChooser1.setTitle("Open Resource File");
//        //FEATURE: stage now loads as 1 page instead of 2
//        Stage stage1 = (Stage) anchorPane.getScene().getWindow();
//        File file1 = fileChooser.showOpenDialog(stage1);
//
//        decryptFileNew(file1);

//        downloadFile(storage,"hugochiaxyznspj","Encrypted test.txt",saveFile());
//        FileChooser fileChooser1 = new FileChooser();
//        fileChooser1.setTitle("Open Resource File");
//        //FEATURE: stage now loads as 1 page instead of 2
//        Stage stage1 = (Stage) anchorPane.getScene().getWindow();
//        File file1 = fileChooser.showOpenDialog(stage1);
//        decryptFile(file1);
    }

    private boolean checkNameTaken(String fileName) throws Exception {
//        Storage storage = StorageOptions.newBuilder().setCredentials(GoogleCredentials.create(new AccessToken(credential.getAccessToken(), null))).build().getService();
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

//    public static byte [] generateIV() {
//        SecureRandom random = new SecureRandom();
//        byte [] iv = new byte [16];
//        random.nextBytes( iv );
//        return iv;
//    }

    //    private SecretKey secKey;
//    private Cipher aesCipher;

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
        byte[] byteCipherText = aesCipher.doFinal(byteTextNew);
        uploadFile(f.getName(), f.getAbsolutePath(), byteCipherText);
//        uploadFile("Encrypted test", f.getAbsolutePath(), byteCipherText);

        //encoding the key to String
        String encodedSecretKey = Base64.getEncoder().encodeToString(secKey.getEncoded());
//        JSONObject main =new JSONObject();

        //Testing for decoded key
        byte[] decodedKey = Base64.getDecoder().decode(encodedSecretKey);
        // rebuild key using SecretKeySpec
        SecretKey originalKey = new SecretKeySpec(decodedKey, 0, decodedKey.length, "AES");
        System.err.println("TESTING" + secKey.equals(originalKey));
        //Testing for decoded key

        JSONObject fileObj = new JSONObject();

        JSONObject filemetadata = new JSONObject();
        filemetadata.put("Encrypted Symmetric Key", encodedSecretKey);

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

        //for oauth2 token field
//        credential.getAccessToken();

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


        String accessToken = credential.getAccessToken();
        System.out.println(accessToken);

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

    private void uploadFile(String filename, String AbsolutePath, byte[] out) throws Exception {
//        Storage storage = StorageOptions.newBuilder().setCredentials(GoogleCredentials.create(new AccessToken(credential.getAccessToken(), null))).build().getService();
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
        FileChooser fileChooser = new FileChooser();
        Stage stage = (Stage) anchorPane.getScene().getWindow();
        File filePath = fileChooser.showSaveDialog(stage);
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
//        FileChooser fileChooser1 = new FileChooser();
//        fileChooser1.setTitle("Open Resource File");
//        //FEATURE: stage now loads as 1 page instead of 2
//        Stage stage1 = (Stage) anchorPane.getScene().getWindow();
//        File file1 = fileChooser1.showOpenDialog(stage1);

        String encodedKey = blob.getMetadata().get("Encrypted Symmetric Key");
        byte[] decodedKey = Base64.getDecoder().decode(encodedKey);
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

    private String convertTime(long time) {
        Date date = new Date(time);
        Format format = new SimpleDateFormat(" dd/MM/yyyy HH:mm:ss");
        return format.format(date);
    }

//    private Path saveFile() {
//        FileChooser fileChooser = new FileChooser();
//        Stage stage = (Stage) anchorPane.getScene().getWindow();
//        File filePath = fileChooser.showSaveDialog(stage);
//        String filePathString = filePath.getAbsolutePath();
//        Path path = Paths.get(filePathString);
////        fileChooser.setTitle("Save Image");
//////        System.out.println(pic.getId());
////        File file = fileChooser.showSaveDialog(null);
////        return Paths.get(file.getName());
//        return path;
//    }

    public void deleteFile(String bucketName, String blobName) {
        Storage storage = StorageOptions.newBuilder().setCredentials(GoogleCredentials.create(new AccessToken(credential.getAccessToken(), null))).build().getService();
        BlobId blobId = BlobId.of(bucketName, blobName);
        boolean deleted = storage.delete(blobId);
        if (deleted)

        {
            // the blob was deleted
            System.out.println("Deleted");
        } else

        {
            // the blob was not found
            System.out.println("Not deleted not found");
        }
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

    private boolean checkPassword;
    private String tempPassword;
    private void checkUserPassword() {

        myScene = anchorPane.getScene();
        Stage stage = (Stage) (myScene).getWindow();

        String title = "Enter your password to enter the restricted area";
//        String content = "The connection timeout. Please try again";
        JFXPasswordField jfxPasswordField = new JFXPasswordField();
        jfxPasswordField.setPromptText("Enter password");

        JFXButton jfxOKButton = new JFXButton("Ok");

        jfxOKButton.setButtonType(JFXButton.ButtonType.RAISED);

        jfxOKButton.setStyle("-fx-background-color: #00bfff;");

        JFXDialogLayout layout = new JFXDialogLayout();
        layout.setHeading(new Label(title));
        layout.setBody(jfxPasswordField);
        layout.setActions(jfxOKButton);
        JFXAlert<Void> alert = new JFXAlert<>(stage);
        alert.setOverlayClose(true);
        alert.setAnimation(JFXAlertAnimation.CENTER_ANIMATION);
        alert.setContent(layout);
        alert.initModality(Modality.NONE);
        jfxOKButton.setOnAction(__ ->{
            //check
            tempPassword=jfxPasswordField.getText();
            uploadProcess.start();});
        alert.show();
        uploadProcess.setOnSucceeded(e -> {
            uploadProcess.reset();
            //if dosen't match redo process
            if (checkPassword==false){
                System.out.println("Wrong password");
            }else{
                //if matches continue to encrypt also need to store the password somewhere
                alert.hideWithAnimation();
                calculateEmail();
                //        UploadFileTest();
                FileChooser fileChooser = new FileChooser();
                fileChooser.setTitle("Choose File to Upload");
                //FEATURE: stage now loads as 1 page instead of 2
                Stage stage1 = (Stage) anchorPane.getScene().getWindow();
                File file = fileChooser.showOpenDialog(stage1);
                try {
                    if (checkNameTaken(file.getName()) == true) {
                        System.out.println("Change NAME!!!! Add showing alert");
                    } else {
                        encryptFileNew(file);
                        //may need to move update Table somewhere else instead
                        updateTable();
                    }
                }catch(Exception e1){
                    e1.printStackTrace();
                }
            }
        });
    }

    private Service uploadProcess = new Service() {
        @Override
        protected Task createTask() {
            return new Task() {
                @Override
                protected Void call() throws Exception {
                    //check password against the hash in DB first then set password temporarily as a global variable
                    User_InfoDB user_infoDB=new User_InfoDB();
                    checkPassword=user_infoDB.checkPassword(tempPassword,login.getEmail());
//                    Platform.runLater(() -> {
//
//                    });
                    return null;
                }
            };
        }
    };

    private void updateObservableList() throws Exception {
//        Storage storage = StorageOptions.newBuilder().setCredentials(GoogleCredentials.create(new AccessToken(credential.getAccessToken(), null))).build().getService();
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
////        users.add(new TreeTableDemo.User(COMPUTER_DEPARTMENT, "23", "CD 1"));
////        users.add(new TreeTableDemo.User(SALES_DEPARTMENT, "22", "Employee 1"));
////        users.add(new TreeTableDemo.User(SALES_DEPARTMENT, "24", "Employee 2"));
////        users.add(new TreeTableDemo.User(SALES_DEPARTMENT, "25", "Employee 4"));
////        users.add(new TreeTableDemo.User(SALES_DEPARTMENT, "27", "Employee 5"));
////        users.add(new TreeTableDemo.User(IT_DEPARTMENT, "42", "ID 2"));
////        users.add(new TreeTableDemo.User(HR_DEPARTMENT, "21", "HR 1"));
////        users.add(new TreeTableDemo.User(HR_DEPARTMENT, "28", "HR 2"));
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
//        privateBucketName = emailFront + "nspj";
////        String bucketname="hugochiaxyznspj";
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

        JFXTreeTableColumn<TableBlob, String> settingsColumn = new JFXTreeTableColumn<>("Others");
        settingsColumn.setPrefWidth(175);
        Callback<TreeTableColumn<TableBlob, String>, TreeTableCell<TableBlob, String>> cellFactory
                =
                new Callback<TreeTableColumn<TableBlob, String>, TreeTableCell<TableBlob, String>>() {
                    @Override
                    public TreeTableCell call(final TreeTableColumn<TableBlob, String> param) {
                        final TreeTableCell<TableBlob, String> cell = new TreeTableCell<TableBlob, String>() {

                            JFXButton btn = new JFXButton("Others");

                            @Override
                            public void updateItem(String item, boolean empty) {
                                super.updateItem(item, empty);
                                if (empty) {
                                    setGraphic(null);
                                    setText(null);
                                } else {
//                                    btn.setButtonType(JFXButton.ButtonType.RAISED);
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

                                    setGraphic(btn);
                                    setText(null);
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
//                        || blob.blobName.get().toUpperCase().contains(newVal)
//                        || blob.date.get().toUpperCase().contains(newVal)
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
//                Storage storage = StorageOptions.newBuilder().setCredentials(GoogleCredentials.create(new AccessToken(credential.getAccessToken(), null))).build().getService();
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
//                myScene = anchorPane.getScene();
//                Stage stage = (Stage) (myScene).getWindow();
//
//                String title = "";
//                String content = "Are you sure you want to delete this file?";
//
//                JFXButton close = new JFXButton("Ok");
//
//                close.setButtonType(JFXButton.ButtonType.RAISED);
//
//                close.setStyle("-fx-background-color: #00bfff;");
//
//                JFXDialogLayout layout = new JFXDialogLayout();
//                layout.setHeading(new Label(title));
//                layout.setBody(new Label(content));
//                layout.setActions(close);
//                JFXAlert<Void> alert = new JFXAlert<>(stage);
//                alert.setOverlayClose(true);
//                alert.setAnimation(JFXAlertAnimation.CENTER_ANIMATION);
//                alert.setContent(layout);
//                alert.initModality(Modality.NONE);
//                close.setOnAction(___ -> {alert.hideWithAnimation();
//                    calculateEmail();
//                    deleteFile(privateBucketName,blobName);
//                    updateObservableList();
//                    TableMethod();});
//                alert.show();
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

                JFXButton close = new JFXButton("Ok");

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
                close.setOnAction(___ -> {
                    alert.hideWithAnimation();
                    calculateEmail();
                    deleteFile(privateBucketName, blobName);
//                    updateObservableList();
//                    TableMethod();
                    updateTable();
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

    private EventHandler<MouseEvent> closeVbox = new EventHandler<MouseEvent>() {
        @Override
        public void handle(MouseEvent mouseEvent) {
            System.out.println("mouse click detected! " + mouseEvent.getSource());
            System.out.println(minX + " " + maxY);
            if (mouseEvent.getX() >= minX && mouseEvent.getX() <= minX + 100 && mouseEvent.getY() >= maxY && mouseEvent.getY() <= maxY + 200) {
                System.out.println("Inside the vbox");
            } else {
                vBox.setVisible(false);
                myScene.removeEventFilter(MouseEvent.MOUSE_PRESSED, closeVbox);
            }
        }
    };

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

//    /**
//     * A custom cell that shows a checkbox, label and button in the
//     * TreeCell.
//     */
//    class CustomCell extends TreeTableCell<String> {
//        @Override
//        protected void updateItem(String item, boolean empty) {
//            super.updateItem(item, empty);
//
//            // If the cell is empty we don't show anything.
//            if (isEmpty()) {
//                setGraphic(null);
//                setText(null);
//            } else {
//                // We only show the custom cell if it is a leaf, meaning it has
//                // no children.
//                if (this.getTreeTableItem().isLeaf()) {
//
//                    // A custom HBox that will contain your check box, label and
//                    // button.
//                    HBox cellBox = new HBox(10);
//
////                    CheckBox checkBox = new CheckBox();
////                    Label label = new Label(item);
//                    JFXButton button = new JFXButton("Press!");
//                    // Here we bind the pref height of the label to the height of the checkbox. This way the label and the checkbox will have the same size.
////                    label.prefHeightProperty().bind(checkBox.heightProperty());
//
//                    cellBox.getChildren().addAll(button);
//
//                    // We set the cellBox as the graphic of the cell.
//                    setGraphic(cellBox);
//                    setText(null);
//                } else {
//                    // If this is the root we just display the text.
//                    setGraphic(null);
//                    setText(item);
//                }
//            }
//        }
//    }

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
