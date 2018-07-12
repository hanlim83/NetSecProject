package Model;

import com.google.api.client.auth.oauth2.Credential;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;

public class IAMPermissions {

    private OAuth2Login login = new OAuth2Login();
    ArrayList<String> permissionList = new ArrayList<>();
    GetIAM getiam;

    public IAMPermissions() {
//        Credential credential;
//        try {
//            credential = login.login();
//        } catch (Exception e) {
//            e.printStackTrace();
//        }

    }


    public String listPermissions() {
        ProcessBuilder builder = new ProcessBuilder(
                "cmd.exe","/c", "gcloud projects get-iam-policy netsecpj");
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


    public static void main (String args[]){
        IAMPermissions permissions = new IAMPermissions();
        permissions.listPermissions();
    }

}
