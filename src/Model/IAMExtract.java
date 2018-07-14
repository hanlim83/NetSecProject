package Model;

import java.lang.reflect.Array;
import java.util.ArrayList;

public class IAMExtract {
    private static String globalRole;
    private static String globalUser;
    static ArrayList<String> bringiamlist = new ArrayList<>();

    public IAMExtract(){

    }

    public IAMExtract(ArrayList<String> getiamlist){
        this.bringiamlist=getiamlist;
        System.out.println("IAM EXTRACT ROLE IS : " + bringiamlist.size());
        int index = bringiamlist.size()-1;
        globalRole = bringiamlist.get(index);
//        globalRole="YOYOYO";
        System.out.println("IAMEXTRACT GLOBALROLE IS : " + globalRole);
        for (int n = 0; n < getiamlist.size() - 1; n++) {
            globalUser = getiamlist.get(n);
            System.out.println("IAMEXTRACT GLOBALUSER IS : " + globalUser);
        }
    }

    public String getGlobalRole() {
        return globalRole;
    }

    public String getGlobalUser() {
        return globalUser;
    }
}
