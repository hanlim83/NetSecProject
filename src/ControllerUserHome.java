import Database.User_InfoDB;
import Model.MyBlob;
import Model.OAuth2Login;
import com.google.api.client.auth.oauth2.Credential;
import com.google.api.gax.paging.Page;
import com.google.auth.oauth2.AccessToken;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.storage.*;
import com.jfoenix.animation.alert.JFXAlertAnimation;
import com.jfoenix.controls.*;
import com.jfoenix.transitions.hamburger.HamburgerBackArrowBasicTransition;
import javafx.application.Platform;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.*;
import java.net.*;
import java.security.PublicKey;
import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ControllerUserHome implements Initializable {
    @FXML
    private AnchorPane anchorPane;

    @FXML
    private JFXHamburger hamburger;

    @FXML
    private JFXDrawer drawer;

    @FXML
    private Label LastFileModifiedLabel;

    @FXML
    private Label TimeLabel;

    @FXML
    private Label GreetingsLabel;

    public static AnchorPane rootP;

    private Credential credential;
    private OAuth2Login login = new OAuth2Login();
    private static String name;

    private static String lastFile;
    private static String greetings;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        hamburgerBar();
        timerprocess.start();
        try {
            InfoUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static Timer timer;
    private static int counter = 0;

    public static void StopTimer() {
        if (counter != 0) {
            timer.purge();
            timer.cancel();
        }
    }


    private void updateTimer() {
        counter++;
        timer = new Timer();
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                String string = new SimpleDateFormat("HH:mm:ss").format(new Date());
                Platform.runLater(() -> {
                    TimeLabel.setText(string);
                });
            }
        }, 0, 100);
    }

//    private Service processTable = new Service() {
//        @Override
//        protected Task createTask() {
//            return new Task() {
//                @Override
//                protected Void call() throws Exception {
//                    getObservableList();
//                    return null;
//                }
//            };
//        }
//    };

//    public static ObservableList<ControllerSecureCloudStorage.TableBlob> blobs = FXCollections.observableArrayList();
//    public void getObservableList(){
//         blobs = FXCollections.observableArrayList();
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
//
////        ControllerSecureCloudStorage controllerSecureCloudStorage=new ControllerSecureCloudStorage();
////        controllerSecureCloudStorage.passData(blobs);
//    }

    private String convertTime(long time) {
        Date date = new Date(time);
        Format format = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
        return format.format(date);
    }

    private Service timerprocess = new Service() {
        @Override
        protected Task createTask() {
            return new Task() {
                @Override
                protected Void call() throws Exception {
                    updateTimer();
                    return null;
                }
            };
        }
    };

    private void InfoUpdate() throws Exception {
        getLatestFile();
        if (!BlobList.isEmpty()) {
            Collections.sort(BlobList);
            System.out.println("=======================LATEST FILE/TIME===============================");
            System.out.println(BlobList.get(0).toString());
            System.out.println(convertTime(BlobList.get(0).getTime()));
            lastFile = "Your last file was modified on " + convertTime(BlobList.get(0).getTime());
            LastFileModifiedLabel.setText(lastFile);
            greetings = getGreetings() + name;
            GreetingsLabel.setText(greetings);
        } else {
            LastFileModifiedLabel.setText("No file in your storage");
            greetings = getGreetings() + name;
            GreetingsLabel.setText(greetings);
        }
    }

    private ArrayList<MyBlob> BlobList = new ArrayList<MyBlob>();

    private void getLatestFile() throws Exception {
        credential = login.login();
        Storage storage = StorageOptions.newBuilder().setCredentials(GoogleCredentials.create(new AccessToken(credential.getAccessToken(), null))).build().getService();
        String email = login.getEmail();
        name = login.getName();
        Scanner s = new Scanner(email).useDelimiter("@");
        String emailFront = s.next();
        emailFront = emailFront.replace(".", "");
        String bucketname = emailFront + "nspj";
        try {
            Page<Blob> blobs = storage.list(bucketname);
            for (Blob blob : blobs.iterateAll()) {
                // do something with the blob
                BlobList.add(new MyBlob(blob));
                System.out.println("FROM METHOD" + blob);
                System.out.println(convertTime(blob.getCreateTime()));
                System.out.println("FROM METHOD" + blob.getName());
//            if (fileName.equals(blob.getName())) {
//                System.out.println("Choose Different NAME!");
//                return true;
//            }
            }
        } catch (com.google.cloud.storage.StorageException e) {
            Platform.runLater(() -> {
                Scene myScene = anchorPane.getScene();
                Stage stage = (Stage) (myScene).getWindow();

                String title = "Alert";
                String content = "Your bucket was not found. Please contact the administrator for more details";

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
            });
        }
    }

    private String getGreetings() {
        String greetings = null;
        int hours = Integer.parseInt(new SimpleDateFormat("HH").format(new Date()));
        if (hours >= 0 && hours < 12) {
            greetings = "Good morning ";
        } else if (hours >= 12 && hours < 16) {
            greetings = "Good afternoon ";
        } else if (hours >= 16 && hours < 21) {
            greetings = "Good evening ";
        } else if (hours >= 21 && hours <= 24) {
            greetings = "Good night ";
        }
        return greetings;
    }

//    Service process = new Service() {
//        @Override
//        protected Task createTask() {
//            return new Task() {
//                @Override
//                protected Void call() throws Exception {
//                    try {
//                        credential = login.login();
//                    } catch (Exception e) {
//                        e.printStackTrace();
//                    }
//                    getLatestFile();
//                    Collections.sort(BlobList);
//                    System.out.println("=======================LATEST FILE/TIME===============================");
//                    System.out.println(BlobList.get(0).toString());
//                    System.out.println(convertTime(BlobList.get(0).getTime()));
//                    Platform.runLater(() -> {
//                        LastFileModifiedLabel.setText("Your last file was modified on " + convertTime(BlobList.get(0).getTime()));
//                        try {
//                            GreetingsLabel.setText("Good afternoon "+login.getName());
//                        } catch (IOException e) {
//                            e.printStackTrace();
//                        }
//                    });
//                    return null;
//                }
//            };
//        }
//    };

    //TODO Test new implementation of generating symmetric key per file, encrypting using Master Key(Password), and uploading/downloading of file followed by decrypting file
    @FXML
    void onClickRSAButton(ActionEvent event) throws Exception {
        User_InfoDB user_infoDB = new User_InfoDB();
        System.out.println(user_infoDB.checkPassword("WrongPassword", "Email here"));
//        Database.RSAKeyGenerator rsaKeyGenerator = new Database.RSAKeyGenerator();
//        rsaKeyGenerator.buildKeyPair();
//        System.out.println("=====================Public Key==========================");
//        System.out.println(rsaKeyGenerator.getPublicKeyString());
//        System.out.println("================================Private Key================================");
//        String privateKey = rsaKeyGenerator.getPrivateKeyString();
//        System.out.println(privateKey);
//
//        String encryptedPrivateKey = rsaKeyGenerator.getEncryptedPrivateKeyString("pass1233", privateKey);
//        System.out.println("==========================Encrypted Private Key==================================");
//        System.out.println(encryptedPrivateKey);
//
//        System.out.println("==========================Decrypted Private Key==================================");
//        String privateKey2 = rsaKeyGenerator.getPrivateKeyString("pass1233", encryptedPrivateKey);
//        System.out.println(privateKey2);
//        System.out.println(privateKey.equals(privateKey2));
        System.out.println(getIp());
        MACaddrTest();
    }

    public static String getIp() throws Exception {
        URL whatismyip = new URL("http://checkip.amazonaws.com");
        BufferedReader in = null;
        try {
            in = new BufferedReader(new InputStreamReader(
                    whatismyip.openStream()));
            String ip = in.readLine();
            return ip;
        } finally {
            if (in != null) {
                try {
                    in.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private void MACaddrTest() throws SocketException, UnknownHostException {
//        System.out.println("Ip: " + GetNetworkAddress.GetAddress("ip"));
//        System.out.println("Mac: " + GetNetworkAddress.GetAddress("mac"));
        InetAddress ip;
        try {

            ip = InetAddress.getLocalHost();
            System.out.println("Current IP address : " + ip.getHostAddress());

            NetworkInterface network = NetworkInterface.getByInetAddress(ip);

            byte[] mac = network.getHardwareAddress();

            System.out.print("Current MAC address : ");

            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < mac.length; i++) {
                sb.append(String.format("%02X%s", mac[i], (i < mac.length - 1) ? "-" : ""));
            }
            System.out.println("MACAddr" + sb.toString());

        } catch (UnknownHostException e) {

            e.printStackTrace();

        } catch (SocketException e) {

            e.printStackTrace();

        }
        System.out.println("TESTTTTTTTTTTTTTTTTTTTTT" + getMacAddress());
    }

    private static String getMacAddress() throws UnknownHostException,
            SocketException {
        InetAddress ipAddress = InetAddress.getLocalHost();
        NetworkInterface networkInterface = NetworkInterface
                .getByInetAddress(ipAddress);
        byte[] macAddressBytes = networkInterface.getHardwareAddress();
        StringBuilder macAddressBuilder = new StringBuilder();

        for (int macAddressByteIndex = 0; macAddressByteIndex < macAddressBytes.length; macAddressByteIndex++) {
            String macAddressHexByte = String.format("%02X",
                    macAddressBytes[macAddressByteIndex]);
            macAddressBuilder.append(macAddressHexByte);

            if (macAddressByteIndex != macAddressBytes.length - 1) {
                macAddressBuilder.append(":");
            }
        }

        return macAddressBuilder.toString();
    }


    public void hamburgerBar() {
        rootP = anchorPane;

        try {
            VBox box = FXMLLoader.load(getClass().getResource("UserSideTab.fxml"));
            drawer.setSidePane(box);
            drawer.setVisible(false);
            drawer.setDefaultDrawerSize(219);
        } catch (IOException ex) {
            Logger.getLogger(ControllerBaseLayoutNew.class.getName()).log(Level.SEVERE, null, ex);
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
