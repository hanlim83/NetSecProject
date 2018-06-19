package Model;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.util.store.DataStoreFactory;
import com.google.api.client.util.store.FileDataStoreFactory;
import com.google.api.services.oauth2.Oauth2;
import com.google.api.services.sqladmin.SQLAdmin;
import com.google.api.services.sqladmin.model.DatabaseInstance;
import com.google.api.services.sqladmin.model.InstancesListResponse;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

public class CloudSQLAuth {
    static OAuth2Login login= new OAuth2Login();
    static Credential credential;

    static {
        try {
            credential = login.login();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
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
            "https://www.googleapis.com/auth/cloud-platform",
            "https://www.googleapis.com/auth/sqlservice.admin");

    private static Oauth2 oauth2;
    private static GoogleClientSecrets clientSecrets;

    public static void authorize() throws IOException {
        // Set up global SQLAdmin instance.
        SQLAdmin client = new SQLAdmin.Builder(httpTransport, JSON_FACTORY, credential)
                .setServicePath("sql/v1beta4/")
                .setApplicationName(APPLICATION_NAME).build();
        InstancesListResponse resp = client.instances().list("netsecpj").execute();
        List<DatabaseInstance> list = resp.getItems();
        for (DatabaseInstance d : list) {
            System.out.println(d.getName());
        }
    }

    public static void main (String[] args){
        try {
            authorize();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
