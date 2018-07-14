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
    ArrayList<GetIAM> iamobjectList = new ArrayList<>();

    public IAMPermissions() {

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
//            System.out.println("ADDED INTO PERMISSION LIST!!!!!!!!!!!!!!!!!!!");
        }
        getiam = new GetIAM(permissionList);
//        iamobjectList.add(getiam);
        return line;
    }

//    public ArrayList<GetIAM> getIAMArrayList(){
//        return iamobjectList;
//    }

    public static void main (String args[]){
        IAMPermissions permissions = new IAMPermissions();
        permissions.listPermissions();
    }

}
