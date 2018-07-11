import Model.Device_Build_NumberDB;
import Model.MyBlob;
import Model.OAuth2Login;
import Model.OSVersion;
import com.google.api.client.auth.oauth2.Credential;
import com.google.api.gax.paging.Page;
import com.google.auth.oauth2.AccessToken;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.storage.*;
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXDrawer;
import com.jfoenix.controls.JFXHamburger;
import com.jfoenix.controls.JFXSnackbar;
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
import javafx.stage.FileChooser;

import java.io.*;
import java.net.*;
import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

import static java.nio.charset.StandardCharsets.UTF_8;

public class ControllerUserHome implements Initializable {
    @FXML
    private AnchorPane anchorPane;

    @FXML
    private JFXHamburger hamburger;

    @FXML
    private JFXDrawer drawer;

    @FXML
    private JFXButton randomButton;

    @FXML
    private JFXButton CloudStorageTestButton;

    @FXML
    private JFXButton RSAButton;

    @FXML
    private Label LastFileModifiedLabel;

    @FXML
    private Label TimeLabel;

    @FXML
    private Label GreetingsLabel;

    private Scene myScene;

    public static AnchorPane rootP;

    private Credential credential;
    private OAuth2Login login = new OAuth2Login();
    WindowsUtils utils = new WindowsUtils();

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        hamburgerBar();
        updateTimer();
        try {
            credential = login.login();
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            getLatestFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
        Collections.sort(BlobList);
        System.out.println("=======================LATEST FILE/TIME===============================");
        System.out.println(BlobList.get(0).toString());
        System.out.println(convertTime(BlobList.get(0).getTime()));
        LastFileModifiedLabel.setText("Your last file was modified on " + convertTime(BlobList.get(0).getTime()));
        try {
            GreetingsLabel.setText("Good afternoon "+login.getName());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static Timer timer;
    private static int counter=0;

    public static void StopTimer(){
        if (counter!=0){
            timer.purge();
            timer.cancel();
        }
    }


    private void updateTimer(){
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


    ArrayList<MyBlob> BlobList = new ArrayList<MyBlob>();

    private void getLatestFile() throws IOException {
        Storage storage = StorageOptions.newBuilder().setCredentials(GoogleCredentials.create(new AccessToken(credential.getAccessToken(), null))).build().getService();
        String email = login.getEmail();
        Scanner s = new Scanner(email).useDelimiter("@");
        String emailFront = s.next();
        emailFront = emailFront.replace(".", "");
        String bucketname = emailFront + "nspj";
//        String bucketname="hugochiaxyznspj";
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
    }

    public String convertTime(long time) {
        Date date = new Date(time);
        Format format = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
        return format.format(date);
    }

    Service process = new Service() {
        @Override
        protected Task createTask() {
            return new Task() {
                @Override
                protected Void call() throws Exception {
                    //To migrate codes here for efficency
                    return null;
                }
            };
        }
    };


    //TODO Test new implementation of generating symmetric key per file, encrypting using Master Key(Password), and uploading/downloading of file followed by decrypting file
    //To be removed soon
    @FXML
    void onClickRandomButton(ActionEvent event) throws Exception {
        Device_Build_NumberDB device_build_numberDB = new Device_Build_NumberDB();
//        device_build_numberDB.deleteOSVersion("8");
//        ArrayList<OSVersion> osList=device_build_numberDB.CheckSupportedVersion();
//        System.out.println(osList.size());
////        Scanner s = new Scanner(osName).useDelimiter("                ");
////        String firstLine=s.next();
////        String osBuildNoStr=s.next();
////        //System.out.println("OS version is " + osName);
////        Scanner sc = new Scanner(osBuildNoStr).useDelimiter(" ");
////        String osBuildNo=sc.next();
////        System.out.println(osBuildNo);
//
//        System.out.println(output.toString());
//        //Desktop.getDesktop().open(new File("C://"));
////        Runtime.getRuntime().exec("explorer.exe /select," + "C://");
//        //output.toString() will contain the result of "netsh advfirewall show all profiles state"
//        System.out.println(DomainFirewall);
//        System.out.println(PrivateFirewall);
//        System.out.println(PublicFirewall);

//        WindowsUtils utils=new WindowsUtils();
//        ResultSet resultset=utils.handleSQLCommands("device_build_number");
//        while (resultset.next()) {
//            System.out.println(resultset.getString(1));
////            SupportedVersions.add(resultSet.getString(1));
//        }

//        //TODO TESTING DELETE B4 PUSH
////        System.out.println(utils.getAccStatus("<EMAIL SANITIZED>"));
//        utils.setUserKeyInfo("Testing1","Testing2","Testing3","<EMAIL SANITIZED>");
        device_build_numberDB.insertNewOSVersion("From JAVA","Hello");
    }

    @FXML
    void onClickRSAButton(ActionEvent event) throws Exception {
        RSAKeyGenerator rsaKeyGenerator = new RSAKeyGenerator();
        rsaKeyGenerator.buildKeyPair();
        System.out.println("=====================Public Key==========================");
        System.out.println(rsaKeyGenerator.getPublicKeyString());
        System.out.println("================================Private Key================================");
        String privateKey = rsaKeyGenerator.getPrivateKeyString();
        System.out.println(privateKey);

        String encryptedPrivateKey = rsaKeyGenerator.getEncryptedPrivateKeyString("pass1233", privateKey);
        System.out.println("==========================Encrypted Private Key==================================");
        System.out.println(encryptedPrivateKey);

        System.out.println("==========================Decrypted Private Key==================================");
        String privateKey2 = rsaKeyGenerator.getPrivateKeyString("pass1233", encryptedPrivateKey);
        System.out.println(privateKey2);
        System.out.println(privateKey.equals(privateKey2));
    }

    @FXML
    void onClickCloudStorageTestButton(ActionEvent event) throws Exception {
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

    public void MACaddrTest() throws SocketException, UnknownHostException {
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

    public static String getMacAddress() throws UnknownHostException,
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
