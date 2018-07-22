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
import java.util.Map;
import java.util.Set;

/**
 * This class contains Bucket-level IAM snippets for the {@link Storage} interface.
 */
public class BucketIamSnippets {
    private String email;
    private String bucketName;


    public String grantWriteAccess(String EMAIL, String BUCKETNAME) {
        this.email=EMAIL;
        this.bucketName=BUCKETNAME;
        ProcessBuilder builder = new ProcessBuilder(
                "cmd.exe", "/c", "gsutil acl ch -u " +email+":WRITE gs://"+bucketName);
        builder.redirectErrorStream(true);
        Process p = null;
        try {
            p = builder.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
        BufferedReader r = new BufferedReader(new InputStreamReader(p.getInputStream()));
        String line = null;
        while (true) {
            try {
                line = r.readLine();
            } catch (IOException e) {
                e.printStackTrace();
            }
            if (line == null) {
                break;
            }
        }
        return line;
    }

    public String grantReadAccess(String EMAIL, String BUCKETNAME) {
        this.email=EMAIL;
        this.bucketName=BUCKETNAME;
        ProcessBuilder builder = new ProcessBuilder(
                "cmd.exe", "/c", "gsutil acl ch -u " +email+":READ gs://"+bucketName);
        builder.redirectErrorStream(true);
        Process p = null;
        try {
            p = builder.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
        BufferedReader r = new BufferedReader(new InputStreamReader(p.getInputStream()));
        String line = null;
        while (true) {
            try {
                line = r.readLine();
            } catch (IOException e) {
                e.printStackTrace();
            }
            if (line == null) {
                break;
            }
        }
        return line;
    }

    public static void main (String[] args){
        BucketIamSnippets try1 = new BucketIamSnippets();
        try1.grantReadAccess("nspjdemo@gmail.com","testingonly123123");
    }

}