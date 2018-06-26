package Model;

import com.google.api.client.auth.oauth2.Credential;
import com.google.auth.oauth2.AccessToken;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.storage.Storage;
import com.google.cloud.storage.StorageOptions;

import java.util.Scanner;

import static Model.CloudSQLAuth.login;

public class TestStorageSnippets {
    public static void main(String[] args){
            String bucketName;
            Scanner sc = new Scanner(System.in);

            System.out.print("Enter ur bucket name: ");
            bucketName=sc.next();

//            Credential credential;
//            try {
//                credential = login.login();
//                Storage storage = StorageOptions.newBuilder().setCredentials(GoogleCredentials.create(new AccessToken(credential.getAccessToken(),null))).build().getService();
                StorageSnippets storage1 = new StorageSnippets();
              // storage1.getCredentials();
                storage1.listBuckets();
               storage1.createBucketWithStorageClassAndLocation(bucketName);
             //   storage1.deleteGcsBucket(bucketName);
//            } catch (Exception e) {
//                e.printStackTrace();
//            }

        }
}
