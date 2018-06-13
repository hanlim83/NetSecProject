import Model.OAuth2Login;
import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.util.store.DataStoreFactory;
import com.google.api.client.util.store.FileDataStoreFactory;
import com.google.api.gax.paging.Page;

import com.google.api.services.oauth2.Oauth2;
import com.google.auth.oauth2.AccessToken;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.storage.Blob;
import com.google.cloud.storage.Bucket;
import com.google.cloud.storage.Storage;
import com.google.cloud.storage.StorageOptions;
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
//
//    private static final String APPLICATION_NAME = "Test";
//
//    /** Directory to store user credentials. */
//    private static final java.io.File DATA_STORE_DIR =
//            new java.io.File(System.getProperty("user.home"), ".store/oauth2_sample");
//
//    /**
//     * Global instance of the {@link DataStoreFactory}. The best practice is to make it a single
//     * globally shared instance across your application.
//     */
//    private static FileDataStoreFactory dataStoreFactory;
//
//    /** Global instance of the HTTP transport. */
//    private static HttpTransport httpTransport;
//
//    /** Global instance of the JSON factory. */
//    private static final JsonFactory JSON_FACTORY = JacksonFactory.getDefaultInstance();

    private OAuth2Login login=new OAuth2Login();
    private Scene myScene;

    @Override
    public void initialize(URL location, ResourceBundle resources) {

    }

    @FXML
    void onClickLoginButton(ActionEvent event) throws IOException {
        MACaddrTest();
//        try {
//            new AccessToken(login.authorize().getAccessToken(),null).getExpirationTime();
//            System.out.println(new AccessToken(login.authorize().getAccessToken(),null).getExpirationTime());
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
        try {
            // authorization
            Credential credential=login.login();
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
                File initialFile = new File("C:\\Users\\hugoc\\Desktop\\NSPJ Logs\\Latest Login method logs.txt");
                InputStream targetStream = new FileInputStream(initialFile);
                InputStream content = new ByteArrayInputStream("Hello, World!".getBytes(UTF_8));
                Blob blob = bucket.create("TestFile", targetStream, "text/plain");
            }

//            System.out.println(login.authorize().getAccessToken());

//            Tokeninfo tokeninfo = oauth2.tokeninfo().setAccessToken(credential.getAccessToken()).execute();
//            System.out.println(tokeninfo.toPrettyString());
            //System.out.println(accessToken.getExpirationTime());
            //System.out.println(accessToken.getTokenValue().toString());
            //ChangeScene(event);

            //return;
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