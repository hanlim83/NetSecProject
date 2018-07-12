package Model;

import java.util.ArrayList;

public class IAMExtract {
    private static String globalRole;
    private static String globalUser;

    public IAMExtract(){

    }

    public IAMExtract(ArrayList<String> getiamlist){
        globalRole = getiamlist.get(getiamlist.size());
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
