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
import java.util.ArrayList;
import java.util.Scanner;

import static Model.CloudSQLAuth.login;
import static Model.OAuth2Login.credential;
import static com.google.api.client.util.Charsets.UTF_8;

public class StorageSnippets {

    private OAuth2Login login = new OAuth2Login();

    Storage storage;
    Bucket bucket;
    String BUCKETS;

    public StorageSnippets() {
        Credential credential;
        try {
            credential = login.login();
            this.storage = StorageOptions.newBuilder().setCredentials(GoogleCredentials.create(new AccessToken(credential.getAccessToken(), null))).build().getService();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public StorageSnippets(Storage storage) {
        this.storage = storage;
    }


//    // [TARGET delete(BucketSourceOption...)]
//    public boolean delete() {
//        // [START delete]
//        boolean deleted = bucket.delete(BucketSourceOption.metagenerationMatch());
//        if (deleted) {
//            System.out.println("The bucket was deleted!!");
//        } else {
//            // the bucket was not found
//            System.out.println("The bucket was not found!!");
//        }
//        // [END delete]
//        return deleted;
//    }

    public Bucket createBucketWithStorageClassAndLocation(String bucketName) {
        // [START createBucketWithStorageClassAndLocation]
        bucket = storage.create(BucketInfo.newBuilder(bucketName)
                // See here for possible values: http://g.co/cloud/storage/docs/storage-classes
                .setStorageClass(StorageClass.MULTI_REGIONAL)
                // Possible values: http://g.co/cloud/storage/docs/bucket-locations#location-mr
                .setLocation("US")
                .build());
        // [END createBucketWithStorageClassAndLocation]
        return bucket;
    }

    public ArrayList<String> listBuckets() {
        ArrayList<String> bucketList = new ArrayList();
        try {
            Page<Bucket> buckets = storage.list();
            for (Bucket bucket : buckets.iterateAll()) {
                BUCKETS = bucket.toString();
                bucketList.add(BUCKETS);
                System.out.println(bucket.toString());
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return bucketList;
    }


}
