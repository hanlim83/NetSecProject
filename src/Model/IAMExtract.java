package Model;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Iterator;

public class IAMExtract {
    private static String globalRole;
    private static String globalUser;
    ArrayList<String> bringiamlist = new ArrayList<>();
    int index1 = 0;

    public IAMExtract() {

    }

    public IAMExtract(ArrayList<String> getiamlist) {
        for (int k = 0; k < getiamlist.size(); k++) {
            bringiamlist.add(getiamlist.get(k));
        }

        System.out.println("IAM EXTRACT ROLE IS : " + bringiamlist.size());
        int index = bringiamlist.size() - 1;
        globalRole = bringiamlist.get(index);
//        globalRole="YOYOYO";
        System.out.println("IAMEXTRACT GLOBALROLE IS : " + globalRole);
        Iterator it = bringiamlist.iterator();
        for (int n = 0; n < getiamlist.size() - 1; n++) {
            globalUser = getiamlist.get(n);
            System.out.println("IAMEXTRACT GLOBALUSER IS : " + globalUser);
        }
//        while(it.hasNext()){
//            globalUser = getiamlist.get(index1);
//            index1++;
//            System.out.println("IAMEXTRACT GLOBALUSER IS : " + globalUser);
//        }
    }

    public String getGlobalRole() {
        return globalRole;
    }

    public String getGlobalUser() {
        return globalUser;
    }
}
