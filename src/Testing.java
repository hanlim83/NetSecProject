import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.extensions.java6.auth.oauth2.AuthorizationCodeInstalledApp;
import com.google.api.client.extensions.jetty.auth.oauth2.LocalServerReceiver;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.util.store.DataStoreFactory;
import com.google.api.client.util.store.FileDataStoreFactory;
import com.google.api.gax.paging.Page;
import com.google.api.services.oauth2.Oauth2;
import com.google.api.services.oauth2.model.Tokeninfo;
import com.google.api.services.oauth2.model.Userinfoplus;
import com.google.auth.oauth2.AccessToken;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.storage.Bucket;
import com.google.cloud.storage.Storage;
import com.google.cloud.storage.StorageOptions;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.*;

public class Testing {
    static Credential credential=null;
    //GoogleCredential googleCredential=credential;

    /**
     * Be sure to specify the name of your application. If the application name is {@code null} or
     * blank, the application will log a warning. Suggested format is "MyCompany-ProductName/1.0".
     */
    private static final String APPLICATION_NAME = "Test";

    /** Directory to store user credentials. */
    private static final java.io.File DATA_STORE_DIR =
            new java.io.File(System.getProperty("user.home"), ".store/oauth2_sample");

    /**
     * Global instance of the {@link DataStoreFactory}. The best practice is to make it a single
     * globally shared instance across your application.
     */
    private static FileDataStoreFactory dataStoreFactory;

    /** Global instance of the HTTP transport. */
    private static HttpTransport httpTransport;

    /** Global instance of the JSON factory. */
    private static final JsonFactory JSON_FACTORY = JacksonFactory.getDefaultInstance();

    /** OAuth 2.0 scopes. */
    private static final List<String> SCOPES = Arrays.asList(
            "https://www.googleapis.com/auth/userinfo.profile",
            "https://www.googleapis.com/auth/userinfo.email",
            "https://www.googleapis.com/auth/devstorage.full_control",
            "https://www.googleapis.com/auth/cloud-platform");

    private static Oauth2 oauth2;
    private static GoogleClientSecrets clientSecrets;

    /** Authorizes the installed application to access user's protected data. */
    protected static Credential authorize() throws Exception {
        // load client secrets
        clientSecrets = GoogleClientSecrets.load(JSON_FACTORY,
                //new InputStreamReader(Testing.class.getResourceAsStream("client_secrets.json")));
                new InputStreamReader(new FileInputStream("C:\\Users\\hugoc\\Documents\\GitHub\\NetSecProject\\src\\client_secrets.json")));
        if (clientSecrets.getDetails().getClientId().startsWith("Enter")
                || clientSecrets.getDetails().getClientSecret().startsWith("Enter ")) {
            System.out.println("Enter Client ID and Secret from https://code.google.com/apis/console/ "
                    + "into oauth2-cmdline-sample/src/main/resources/client_secrets.json");
            System.exit(1);
        }
        // set up authorization code flow
        GoogleAuthorizationCodeFlow flow = new GoogleAuthorizationCodeFlow.Builder(
                httpTransport, JSON_FACTORY, clientSecrets, SCOPES).setDataStoreFactory(
                dataStoreFactory).build();
        // authorize
        return new AuthorizationCodeInstalledApp(flow, new LocalServerReceiver()).authorize("user");
    }

    public static void main(String[] args) {
        try {
            httpTransport = GoogleNetHttpTransport.newTrustedTransport();
            dataStoreFactory = new FileDataStoreFactory(DATA_STORE_DIR);
            // authorization
            credential = authorize();
            // set up global Oauth2 instance
            oauth2 = new Oauth2.Builder(httpTransport, JSON_FACTORY, credential).setApplicationName(
                    APPLICATION_NAME).build();
//            Storage storage = StorageOptions.newBuilder().setCredentials(new GoogleCredentials(new AccessToken(authorize().getAccessToken(),null))).build().getService();
//
//            Page<Bucket> buckets = storage.list();
//            for (Bucket bucket : buckets.iterateAll()) {
//                System.out.println(bucket.toString());
//            }
//            // run commands
            tokenInfo(credential.getAccessToken());
            userInfo();
            // success!

            //Test
            //authListBuckets(httpTransport,JSON_FACTORY,clientSecrets.getDetails().getClientId(),clientSecrets.getDetails().getClientSecret());
            //authListBuckets(credential.getAccessToken());
//            GoogleCredential googleCredential = new GoogleCredential.Builder()
//                    .setJsonFactory(JSON_FACTORY)
//                    .setTransport(httpTransport)
//                    .setClientSecrets(clientSecrets.getDetails().getClientId(), clientSecrets.getDetails().getClientSecret()).build()
//                    .setFromTokenResponse(JSON_FACTORY.fromString(
//                            credential.toString(), GoogleTokenResponse.class));
//            Storage storage = null;
//            try {
//                storage = StorageOptions.newBuilder().setCredentials()
//                        //.setCredentials(new GoogleAuthorizationCodeFlow(httpTransport,jsonFactory,clientId,clientSecret,anotherList))
////                        .setCredentials(googleCredential)
////                        .build()
////                        .getService();
//            } catch (Exception e) {
//                e.printStackTrace();
//            }

            //Test
            return;
        } catch (IOException e) {
            System.err.println(e.getMessage());
        } catch (Throwable t) {
            t.printStackTrace();
        }
        System.exit(1);
    }

    private static void tokenInfo(String accessToken) throws IOException {
        header("Validating a token");
        Tokeninfo tokeninfo = oauth2.tokeninfo().setAccessToken(accessToken).execute();
        System.out.println(tokeninfo.toPrettyString());
        if (!tokeninfo.getAudience().equals(clientSecrets.getDetails().getClientId())) {
            System.err.println("ERROR: audience does not match our client ID!");
        }
    }

    private static void userInfo() throws IOException {
        header("Obtaining User Profile Information");
        Userinfoplus userinfo = oauth2.userinfo().get().execute();
        System.out.println(userinfo.toPrettyString());
    }

    static void header(String name) {
        System.out.println();
        System.out.println("================== " + name + " ==================");
        System.out.println();
    }
}