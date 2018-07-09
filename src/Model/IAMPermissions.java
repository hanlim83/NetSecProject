package Model;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class IAMPermissions implements Runnable{
 //   String line;

    public IAMPermissions(){

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
            if (line == null) { break; }
            System.out.println(line);
        }
        return line;
    }


    public static void main (String args[]){
        IAMPermissions permissions = new IAMPermissions();
        permissions.listPermissions();
    }

    @Override
    public void run() {

    }
}
