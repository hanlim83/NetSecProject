package Model;

import java.util.ArrayList;

public class GetIAM {
    private String line;
    private static ArrayList<String> tempPermissionList= new ArrayList<>();

    public GetIAM(){}

    public GetIAM(ArrayList<String> permissionList){
        tempPermissionList=permissionList;
    }

    public ArrayList<String> getTempPermissionList() {
        return tempPermissionList;
    }

    public String getLine() {
        return line;
    }
}
