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
import java.util.Properties;
import java.util.Scanner;


import static com.google.api.client.util.Charsets.UTF_8;

public class StorageSnippets {

    private OAuth2Login login = new OAuth2Login();
    Storage storage;
    Bucket bucket;
    String BUCKETS;
    ArrayList<CloudBuckets> cloudbucketsList = new ArrayList<CloudBuckets>();
    int checker=0;

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

//    public void getCredentials(){
//        try {
//            Credential credentials = login.login();
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }

//    public Bucket createBucket(String bucketName){
//        Storage storage = StorageOptions.newBuilder().setCredentials(GoogleCredentials.create(new AccessToken(credential.getAccessToken(),null))).build().getService();
//        StorageSnippets storageObject = new StorageSnippets(storage);
//        storageObject.getCredentials();
//        Bucket buckets = storage.create(BucketInfo.of(bucketName));
//
//        return buckets;
//
//    }


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

        CloudBuckets cloudB = new CloudBuckets(bucket);
        cloudbucketsList.add(cloudB);
        // [END createBucketWithStorageClassAndLocation]
        return bucket;
    }

   /* public ArrayList<String> listBuckets() {
        ArrayList<String> bucketList = new ArrayList();
        try {
            Page<Bucket> buckets = storage.list();
            for (Bucket bucket : buckets.iterateAll()) {
                cloudbucketsList.add(new CloudBuckets(bucket));
                *//*BUCKETS = bucket.toString();
                bucketList.add(BUCKETS);*//*
                System.out.println(bucket.toString());
            }
         // Calling object conversion class to pass in arraylist of buckets
//          ObjectConversion objc = new ObjectConversion(bucketList);
        } catch (Exception e) {
            e.printStackTrace();
        }

//        // [START createBucket]
//        Bucket bucket = storage.create(BucketInfo.of(bucketName));
//        // [END createBucket]
      //  return bucket;
        return bucketList;
    }*/

    public void listBuckets() {
        try {
            Page<Bucket> buckets = storage.list();
            for (Bucket bucket : buckets.iterateAll()) {
                CloudBuckets cloudB = new CloudBuckets(bucket);
                cloudbucketsList.add(cloudB);
                System.out.println(bucket.toString());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

//    public boolean deleteBuckets() {
//       // boolean deleted = bucket.delete(BucketSourceOption.metagenerationMatch());
//        storage.buckets().delete(Properties.bucketId).execute()
//        if (deleted) {
//            // the bucket was deleted
//        } else
//
//        {
//            // the bucket was not found
//        }
//    }



    public int deleteGcsBucket(String gcsBucketName) {
//        StorageOptions.Builder optionsBuilder = StorageOptions.newBuilder();
//        StorageOptions storageOptions = optionsBuilder.setProjectId(projectId).build();
//        Storage storage = storageOptions.getService();
        System.out.println("Processing to delete bucket...");
        Iterable<Blob> blobs = storage.list(gcsBucketName, Storage.BlobListOption.prefix("")).iterateAll();
        for (Blob blob : blobs) {
            blob.delete(Blob.BlobSourceOption.generationMatch());
        }

        System.out.println("Deleting...");
        storage.delete(gcsBucketName);
        checker=1;
        return checker;
    }

    public int getChecker() {
        return checker;
    }

    public ArrayList<CloudBuckets> getCloudbucketsList() {
        return cloudbucketsList;
    }
}
