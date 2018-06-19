package Model;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.services.oauth2.Oauth2;
import com.google.auth.ServiceAccountSigner;
import com.google.auth.oauth2.AccessToken;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.auth.oauth2.ServiceAccountCredentials;
import com.google.cloud.BatchResult;
import com.google.api.gax.paging.Page;
import com.google.cloud.ReadChannel;
import com.google.cloud.WriteChannel;
import com.google.cloud.storage.Acl;
import com.google.cloud.storage.Acl.Role;
import com.google.cloud.storage.Acl.User;
import com.google.cloud.storage.Blob;
import com.google.cloud.storage.BlobId;
import com.google.cloud.storage.BlobInfo;
import com.google.cloud.storage.Bucket;
import com.google.cloud.storage.BucketInfo;
import com.google.cloud.storage.CopyWriter;
import com.google.cloud.storage.Storage;
import com.google.cloud.storage.Storage.BlobGetOption;
import com.google.cloud.storage.Storage.BlobListOption;
import com.google.cloud.storage.Storage.BlobSourceOption;
import com.google.cloud.storage.Storage.BlobTargetOption;
import com.google.cloud.storage.Storage.BlobWriteOption;
import com.google.cloud.storage.Storage.BucketField;
import com.google.cloud.storage.Storage.BucketGetOption;
import com.google.cloud.storage.Storage.BucketListOption;
import com.google.cloud.storage.Storage.BucketSourceOption;
import com.google.cloud.storage.Storage.ComposeRequest;
import com.google.cloud.storage.Storage.CopyRequest;
import com.google.cloud.storage.Storage.SignUrlOption;
import com.google.cloud.storage.StorageBatch;
import com.google.cloud.storage.StorageBatchResult;
import com.google.cloud.storage.StorageClass;
import com.google.cloud.storage.StorageException;
import com.google.cloud.storage.StorageOptions;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.Scanner;

import static Model.OAuth2Login.credential;
import static com.google.api.client.util.Charsets.UTF_8;

public class StorageSnippets {



    private OAuth2Login login=new OAuth2Login();
//     private StorageSnippets storageObject = new StorageSnippets();

//    public StorageSnippets(){
//
//    }
    private final Storage storages;

    public StorageSnippets(Storage storages) {
        this.storages = storages;
    }

    private Credential getCredentials(){
        Credential credential = null;
        try {
            credential = login.login();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return credential;
    }

    public Bucket createBucket(String bucketName){
        Storage storage = StorageOptions.newBuilder().setCredentials(GoogleCredentials.create(new AccessToken(credential.getAccessToken(),null))).build().getService();
        StorageSnippets storageObject = new StorageSnippets(storage);
        storageObject.getCredentials();
        Bucket buckets = storage.create(BucketInfo.of(bucketName));
//
        return buckets;
    }

    public void listBuckets() {
        try {
            Credential credential=login.login();
            Storage storage = StorageOptions.newBuilder().setCredentials(GoogleCredentials.create(new AccessToken(credential.getAccessToken(),null))).build().getService();
            Page<Bucket> buckets = storage.list();
            for (Bucket bucket : buckets.iterateAll()) {
                System.out.println(bucket.toString());
            }

        } catch (Exception e) {
            e.printStackTrace();
        }


        System.out.println("Credentials here : " + login.toString());

//        // [START createBucket]
//        Bucket bucket = storage.create(BucketInfo.of(bucketName));
//        // [END createBucket]
      //  return bucket;
    }



    public static void main(String[] args){
        String bucketName;
        Scanner sc = new Scanner(System.in);

        System.out.print("Enter ur bucket name: ");
        bucketName=sc.next();


        Storage storage = StorageOptions.newBuilder().setCredentials(GoogleCredentials.create(new AccessToken(credential.getAccessToken(),null))).build().getService();
        StorageSnippets storage1 = new StorageSnippets(storage);
        storage1.getCredentials();
        storage1.listBuckets();
        storage1.createBucket(bucketName);
    }
}
