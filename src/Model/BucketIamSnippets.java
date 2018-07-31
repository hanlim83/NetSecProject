package Model;
import com.google.cloud.Identity;
import com.google.cloud.Policy;
import com.google.cloud.Role;
import com.google.cloud.storage.Bucket;
import com.google.cloud.storage.Storage;
import com.google.cloud.storage.StorageOptions;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Map;
import java.util.Set;

/**
 * This class contains Bucket-level IAM snippets for the {@link Storage} interface.
 */
public class BucketIamSnippets {
   public static ArrayList<String> bucketRoleList = new ArrayList<>();
   public static ArrayList<String> bucketMember = new ArrayList<>();
    /**
     * Example of listing the Bucket-Level IAM Roles and Members
     */
    public Policy listBucketIamMembers(String bucketName) {
        bucketRoleList.clear();
        bucketMember.clear();
        // [START view_bucket_iam_members]
        // Initialize a Cloud Storage client
        Storage storage = StorageOptions.getDefaultInstance().getService();

        // Get IAM Policy for a bucket
        Policy policy = storage.getIamPolicy(bucketName);

        // Print Roles and its identities
        Map<Role, Set<Identity>> policyBindings = policy.getBindings();
        for(Map.Entry<Role, Set<Identity>> entry : policyBindings.entrySet()) {
            System.out.printf("Role: %s Identities: %s\n", entry.getKey(), entry.getValue());
//            System.out.println(entry.getKey());
//            System.out.println(entry.getValue());
            bucketRoleList.add(String.valueOf(entry.getKey()));
            bucketMember.add(String.valueOf(entry.getValue()));
        }
        // [END view_bucket_iam_members]
        return policy;
    }

    /**
     * Example of adding a member to the Bucket-level IAM
     */
    public Policy addBucketIamMember(String bucketName, String role2, String identity2) {
        // [START add_bucket_iam_member]
        // Initialize a Cloud Storage client
        Role role = Role.of(role2);
        Identity identity = Identity.valueOf(identity2);

        Storage storage = StorageOptions.getDefaultInstance().getService();

        // Get IAM Policy for a bucket
        Policy policy = storage.getIamPolicy(bucketName);

        // Add identity to Bucket-level IAM role
        Policy updatedPolicy = storage.setIamPolicy(bucketName,
                policy.toBuilder().addIdentity(role, identity).build());

        if (updatedPolicy.getBindings().get(role).contains(identity)) {
            System.out.printf("Added %s with role %s to %s\n", identity, role, bucketName);
        }
        // [END add_bucket_iam_member]
        return updatedPolicy;
    }

    /**
     * Example of removing a member from the Bucket-level IAM
     */
    public Policy removeBucketIamMember(String bucketName, String role1, String identity1) {
        // [START remove_bucket_iam_member]
        // Initialize a Cloud Storage client
        Role role;
        role= Role.of(role1);

        Identity identity;
        identity = Identity.valueOf(identity1);

        Storage storage = StorageOptions.getDefaultInstance().getService();

        // Get IAM Policy for a bucket
        Policy policy = storage.getIamPolicy(bucketName);

        // Remove an identity from a Bucket-level IAM role
        Policy updatedPolicy = storage.setIamPolicy(bucketName,
                policy.toBuilder().removeIdentity(role, identity).build());

        if (updatedPolicy.getBindings().get(role) == null ||
                !updatedPolicy.getBindings().get(role).contains(identity)) {
            System.out.printf("Removed %s with role %s from %s\n", identity, role, bucketName);
        }
        // [END remove_bucket_iam_member]
        return updatedPolicy;
    }

    public static void main (String[] args){
        BucketIamSnippets try1 = new BucketIamSnippets();
        try1.listBucketIamMembers("testingonly123123");

//        try1.removeBucketIamMember("testingonly123123","roles/storage.legacyBucketReader","projectViewer:netsecpj");
//        try1.addBucketIamMember("testingonly123123","roles/storage.objectViewer","user:nspjdemo@gmail.com");
//        try1.addBucketIamMember("testingonly123123","roles/storage.legacyBucketReader","projectViewer:netsecpj");

        //        try1.grantReadAccess("nspjdemo@gmail.com","permissionbucket1");

    }

    public ArrayList<String> getBucketRoleList() {
        return bucketRoleList;
    }

    public ArrayList<String> getBucketMember() {
        return bucketMember;
    }


//    private String email;
//    private String bucketName;
//
//
//    public String grantWriteAccess(String EMAIL, String BUCKETNAME) {
//        this.email=EMAIL;
//        this.bucketName=BUCKETNAME;
//        ProcessBuilder builder = new ProcessBuilder(
//                "cmd.exe", "/c", "gsutil acl ch -u " +email+":WRITE gs://"+bucketName);
//        builder.redirectErrorStream(true);
//        Process p = null;
//        try {
//            p = builder.start();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//        BufferedReader r = new BufferedReader(new InputStreamReader(p.getInputStream()));
//        String line = null;
//        while (true) {
//            try {
//                line = r.readLine();
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//            if (line == null) {
//                break;
//            }
//        }
//        return line;
//    }
//
//    public String grantReadAccess(String EMAIL, String BUCKETNAME) {
//        this.email=EMAIL;
//        this.bucketName=BUCKETNAME;
//        ProcessBuilder builder = new ProcessBuilder(
//                "cmd.exe", "/c", "gsutil acl ch -u " +email+":READ gs://"+bucketName);
//        builder.redirectErrorStream(true);
//        Process p = null;
//        try {
//            p = builder.start();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//        BufferedReader r = new BufferedReader(new InputStreamReader(p.getInputStream()));
//        String line = null;
//        while (true) {
//            try {
//                line = r.readLine();
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//            if (line == null) {
//                break;
//            }
//        }
//        return line;
//    }
//


}