package Model;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Iterator;

public class IAMExtract {
    private String globalRole;
    private String globalUser;
    ArrayList<String> bringiamlist = new ArrayList<>();
   public static ArrayList<IAMExtract> iamobjlist = new ArrayList<>();
   public static ArrayList<String> stringEmailList = new ArrayList<>();
    int index1 = 0;


    public IAMExtract() {

    }

    public IAMExtract(ArrayList<String> getiamlist) {
        for (int k = 0; k < getiamlist.size(); k++) {
            bringiamlist.add(getiamlist.get(k));
        }

        System.out.println("IAM EXTRACT ROLE SIZE IS : " + bringiamlist.size());
        int index = bringiamlist.size() - 1;
        globalRole = bringiamlist.get(index).substring(8,getiamlist.get(index).length());
        System.out.println("IAMEXTRACT GLOBALROLE IS : " + globalRole);

        takeIAMList();
//        takeEmailList();

        for (int n = 0; n < getiamlist.size() - 1; n++) {
            globalUser = getiamlist.get(n);
            stringEmailList.add(globalUser);
            System.out.println("IAMEXTRACT GLOBALUSER IS : " + globalUser);
        }
    }

    public IAMExtract(String role, String user){
        this.globalUser=user;
        this.globalRole=role;
    }

    public ArrayList<IAMExtract> takeIAMList(){
        for(int i=0;i<bringiamlist.size()-1;i++){
            iamobjlist.add(new IAMExtract(globalRole,bringiamlist.get(i)));
        }
        return iamobjlist;
    }


//    public ArrayList<String> getStringEmailList() {
//        return stringEmailList;
//    }

    public String getGlobalRole() {
        return globalRole;
    }

    public String getGlobalUser() {
        return globalUser;
    }
}
