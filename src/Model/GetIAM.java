package Model;

import java.util.ArrayList;

public class GetIAM {
    private String line;
    private static ArrayList<String> tempPermissionList= new ArrayList<>();

    public GetIAM(){}

    public GetIAM(ArrayList<String> permissionList){
        this.tempPermissionList=permissionList;
        for (int i = 0; i < tempPermissionList.size(); i++) {
            System.out.println("TRYING NOW " + tempPermissionList.get(i));
        }

    }

    public ArrayList<String> getTempPermissionList() {
        return tempPermissionList;
    }

    public String getLine() {
        return line;
    }
}
