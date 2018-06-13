import Model.OAuth2Login;
import com.google.api.client.auth.oauth2.Credential;
import com.google.api.gax.paging.Page;

import com.google.api.client.http.InputStreamContent;
import com.google.api.services.storage.model.ObjectAccessControl;
import com.google.api.services.storage.model.Objects;
import com.google.api.services.storage.model.StorageObject;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.GeneralSecurityException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import com.google.auth.oauth2.AccessToken;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.storage.*;
import com.jfoenix.controls.JFXButton;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.*;
import java.net.*;
import java.util.ResourceBundle;

import static com.google.api.client.util.Charsets.UTF_8;

public class ControllerLoginPage implements Initializable {
    @FXML
    private JFXButton LoginButton;

    private OAuth2Login login=new OAuth2Login();
    private Scene myScene;
    private Credential credential;

    @Override
    public void initialize(URL location, ResourceBundle resources) {

    }

    @FXML
    void onClickLoginButton(ActionEvent event) throws Exception {
        MACaddrTest();
        System.out.println(getIp());
        WindowsVersionNo();
        try {
            // authorization
            credential=login.login();
            //credential.getRefreshToken();
//            if (credential.getExpiresInSeconds()<900) {
//                //System.out.println(credential.getExpirationTimeMilliseconds());
//                System.out.println(credential.getExpiresInSeconds());
//                credential.getRefreshToken();
//                System.out.println(credential.getExpiresInSeconds());
//                System.out.println("Getting new Token");
////                oauth2 = new Oauth2.Builder(httpTransport, JSON_FACTORY, credential).setApplicationName(
////                        APPLICATION_NAME).build();
////                tokenInfo(credential.getAccessToken());
////                System.out.println("Token exists");
////                System.out.println("Token expiry time:"+credential.getExpiresInSeconds());
//            }
//            //else{
//            oauth2 = new Oauth2.Builder(httpTransport, JSON_FACTORY, credential).setApplicationName(
//                    APPLICATION_NAME).build();
//            tokenInfo(credential.getAccessToken());
//            System.out.println("Token exists");
//            System.out.println("Token expiry time:"+credential.getExpiresInSeconds());
//            //}

//            if (credential.getRefreshToken()!=null) {
//                credential.getRefreshToken();
//            }
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
        //System.exit(1);
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getResource("BaseLayout.fxml"));
        System.out.println(getClass().getResource("BaseLayout.fxml"));
        myScene = (Scene) ((Node) event.getSource()).getScene();
        Stage stage = (Stage) (myScene).getWindow();
        Parent nextView = loader.load();


        ControllerBaseLayout controller = loader.<ControllerBaseLayout>getController();
        //controller.passData(admin);

        stage.setScene(new Scene(nextView));
        stage.setTitle("NSPJ");
        stage.show();
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

    public void WindowsVersionNo(){
        System.out.println("os.name: " + System.getProperty("os.name"));
        System.out.println("os.version: " + System.getProperty("os.version"));
        WindowsUtils utils= new WindowsUtils();
        utils.getEdition();
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

}