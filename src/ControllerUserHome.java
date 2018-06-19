import Model.OAuth2Login;
import com.google.api.client.auth.oauth2.Credential;
import com.google.api.gax.paging.Page;
import com.google.auth.oauth2.AccessToken;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.storage.Blob;
import com.google.cloud.storage.Bucket;
import com.google.cloud.storage.Storage;
import com.google.cloud.storage.StorageOptions;
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXDrawer;
import com.jfoenix.controls.JFXHamburger;
import com.jfoenix.transitions.hamburger.HamburgerBackArrowBasicTransition;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;

import java.io.*;
import java.net.*;
import java.util.ResourceBundle;
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

    private Scene myScene;

    public static AnchorPane rootP;

    private Credential credential;
    private OAuth2Login login=new OAuth2Login();

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        hamburgerBar();
    }

    @FXML
    void onClickRandomButton(ActionEvent event) throws Exception {
        System.out.println(getIp());
        MACaddrTest();
    }

    @FXML
    void onClickCloudStorageTestButton(ActionEvent event) {
        try {
            // authorization
            credential=login.login();
            // set up global Oauth2 instance

            // authorization + Get Buckets
            Storage storage = StorageOptions.newBuilder().setCredentials(GoogleCredentials.create(new AccessToken(credential.getAccessToken(),null))).build().getService();
            //Testing for storage
            Page<Bucket> buckets = storage.list();
            for (Bucket bucket : buckets.iterateAll()) {
                System.out.println(bucket.toString());
            }

            for (Bucket bucket : buckets.iterateAll()) {
                Page<Blob> blobs = bucket.list();
                for (Blob blob : blobs.iterateAll()) {
                    // do something with the blob
                    System.out.println(blob);
                    System.out.println(blob.getName());
                }
            }
            String filename= "TestFILENEW1";
            if (checkNameTaken(filename)==true){
                System.out.println("Change NAME!!!!");
            } else{
                uploadFile(filename);
            }
        } catch (Throwable t) {
            t.printStackTrace();
        }
    }

        public boolean checkNameTaken(String fileName){
        Storage storage = StorageOptions.newBuilder().setCredentials(GoogleCredentials.create(new AccessToken(credential.getAccessToken(),null))).build().getService();
        Page<Blob> blobs = storage.list("hr_dept");
        for (Blob blob : blobs.iterateAll()) {
            // do something with the blob
            System.out.println("FROM METHOD"+blob);
            System.out.println("FROM METHOD"+blob.getName());
            if(fileName.equals(blob.getName())){
                System.out.println("Choose Different NAME!");
                return true;
            }
        }
        return false;
    }

    public void uploadFile(String filename) throws FileNotFoundException {
        Storage storage = StorageOptions.newBuilder().setCredentials(GoogleCredentials.create(new AccessToken(credential.getAccessToken(),null))).build().getService();
        Page<Bucket> buckets = storage.list();
        for (Bucket bucket : buckets.iterateAll()) {
            System.out.println(bucket.toString());
            File initialFile = new File("C:\\Users\\hugoc\\Desktop\\NSPJ Logs\\Latest Login method logs.txt");
            InputStream targetStream = new FileInputStream(initialFile);
            InputStream content = new ByteArrayInputStream("Hello, World!".getBytes(UTF_8));
            Blob blob = bucket.create(filename, targetStream, "text/plain");
        }
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
            System.out.println("MACAddr"+sb.toString());

        } catch (UnknownHostException e) {

            e.printStackTrace();

        } catch (SocketException e){

            e.printStackTrace();

        }
        System.out.println("TESTTTTTTTTTTTTTTTTTTTTT"+getMacAddress());
    }

    public static String getMacAddress() throws UnknownHostException,
            SocketException
    {
        InetAddress ipAddress = InetAddress.getLocalHost();
        NetworkInterface networkInterface = NetworkInterface
                .getByInetAddress(ipAddress);
        byte[] macAddressBytes = networkInterface.getHardwareAddress();
        StringBuilder macAddressBuilder = new StringBuilder();

        for (int macAddressByteIndex = 0; macAddressByteIndex < macAddressBytes.length; macAddressByteIndex++)
        {
            String macAddressHexByte = String.format("%02X",
                    macAddressBytes[macAddressByteIndex]);
            macAddressBuilder.append(macAddressHexByte);

            if (macAddressByteIndex != macAddressBytes.length - 1)
            {
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