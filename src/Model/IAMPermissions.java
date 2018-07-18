package Model;

import com.google.api.client.auth.oauth2.Credential;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;

public class IAMPermissions {

    ArrayList<String> permissionList = new ArrayList<>();
    GetIAM getiam;
    private static String userEmail;
    private static String userRole;

    private static String userRevokeEmail;
    private static String userRevokeRole;

    public IAMPermissions() {

    }

    public String listPermissions() {
        ProcessBuilder builder = new ProcessBuilder(
                "cmd.exe", "/c", "gcloud projects get-iam-policy netsecpj");
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
            permissionList.add(line);
        }
        getiam = new GetIAM(permissionList);
        return line;
    }


    public void addPermissions(String email, String role) {
        userEmail=email;
        userRole=role;
        System.out.println("EMAIL IS: " + userEmail);
        System.out.println("ROLE IS: " + userRole);
        ProcessBuilder builder = new ProcessBuilder(
                "cmd.exe", "/c", "gcloud projects add-iam-policy-binding netsecpj --member user:"+userEmail+" --role "+userRole);
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
    }

    public void revokePermissions(String email, String role) {
        userRevokeEmail=email;
        userRevokeRole=role;
        System.out.println("EMAIL IS: " + userEmail);
        System.out.println("ROLE IS: " + userRole);
        ProcessBuilder builder = new ProcessBuilder(
                "cmd.exe", "/c", "gcloud projects remove-iam-policy-binding netsecpj --member user:"+userRevokeEmail+" --role "+userRevokeRole);
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
    }

    public static void main (String args[]){
        IAMPermissions permissions = new IAMPermissions();
        permissions.addPermissions("Winstonlimjy2000@gmail.com","roles/editor");
    }

}
