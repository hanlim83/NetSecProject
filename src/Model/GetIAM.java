package Model;

import java.util.ArrayList;

public class GetIAM {
    private String line;

    private String globalRole;
    private String globalUser;


    private static ArrayList<String> tempPermissionList = new ArrayList<>();

    static ArrayList<String> getiamlist = new ArrayList<>();
    static ArrayList<String> cloudsqladminLIST;
    static ArrayList<String> ownerList = new ArrayList<>();
    static ArrayList<String> editorList = new ArrayList<>();
    static ArrayList<String> viewerList = new ArrayList<>();
    static ArrayList<String> firebaseList = new ArrayList<>();
    static ArrayList<String> computeEngineList = new ArrayList<>();
    static ArrayList<String> loggingadminList = new ArrayList<>();
    static ArrayList<String> monitoringadminList = new ArrayList<>();
    static ArrayList<String> apikeysadminList = new ArrayList<>();
    static ArrayList<String> storageadminList = new ArrayList<>();

    private static int w;
    private static int g;
    private static int wg = 100;

    static int globalChecker;

    ArrayList<IAMExtract> extractingIAM = new ArrayList<>();
    IAMExtract iamExtract;

    public GetIAM() {
    }

    public GetIAM(ArrayList<String> permissionList) {
        tempPermissionList = permissionList;
        getiamlist = tempPermissionList;
        getiamlist.remove(0);

        System.out.println("HIHI " + getiamlist);
        for (int i = 0; i < getiamlist.size(); i++) {
            System.out.println("GetIAM Model trying to print permissions from getiamlist :" + tempPermissionList.get(i));
        }

        //START OF CloudSQL List
        if (globalChecker == 4) {
            for (int o = 0; o < getiamlist.size(); o++) {
                if (getiamlist.get(o).contains("role: roles/cloudsql.admin")) {
                    w = getiamlist.indexOf(getiamlist.get(o));
                    System.out.println("W is : " + w);
                }
            }
            //nearest "member" from w
            for (int i=0;i<getiamlist.size();i++){
                if (getiamlist.get(i).contains("members:")) {
                    g = getiamlist.indexOf(getiamlist.get(i));
                    System.out.println("first G is : " + g);
                    //g is members
                    //w is roles
                    int wg1 = w-g;
                    while(wg1<wg && wg1>0){
                        wg=wg1;
                    }
                    g = w - wg;
                    System.out.println("second G is :" + g);
                }
                }
            cloudsqladminLIST = new ArrayList<String>(getiamlist.subList(g,w));


//            for (int o = 0; o < getiamlist.size(); o++) {
//                String s = getiamlist.get(o);
//                cloudsqladminLIST.add(getiamlist.get(o));
//                System.out.println("Adding into cloudsql list : " + s);
//                if (getiamlist.get(o).contains("role: roles/cloudsql.admin")) {
//                    w = getiamlist.indexOf(getiamlist.get(o));
//                    System.out.println("INDEX OF ROLE LINE : " + w);
//                    System.out.println("FOUND THIS");
//                    break;
//                }
//            }
//            cloudsqladminLIST.remove(0);
            for (int p = 0; p < cloudsqladminLIST.size(); p++) {
                System.out.println("Inside of CLOUDSQLADMINLIST : " + cloudsqladminLIST.get(p));
            }
            iamExtract = new IAMExtract(cloudsqladminLIST);
            extractingIAM.add(iamExtract);
//            globalRole = cloudsqladminLIST.get(cloudsqladminLIST.size());
//            for (int n = 0; n < cloudsqladminLIST.size() - 1; n++) {
//                globalUser = cloudsqladminLIST.get(n);
//            }
            while (w != -1) {
                getiamlist.remove(w);
                w--;
            }
            for (int i = 0; i < getiamlist.size(); i++) {
                System.out.println("CHECKING GETIAMLIST AGAIN: " + getiamlist.get(i));
            }
            //END OF CLOUDSQL LIST
        } else if (globalChecker == 6) {
            //START OF COMPUTE ENGINE LIST
            for (int o = 0; o < getiamlist.size(); o++) {
                String s = getiamlist.get(o);
                computeEngineList.add(getiamlist.get(o));
                System.out.println("Adding into compute engine list : " + s);
                if (getiamlist.get(o).contains("role: roles/compute.serviceAgent")) {
                    w = getiamlist.indexOf(getiamlist.get(o));
                    System.out.println("INDEX OF ROLE LINE : " + w);
                    System.out.println("FOUND THIS");
                    break;
                }
            }
            computeEngineList.remove(0);
            for (int p = 0; p < computeEngineList.size(); p++) {
                System.out.println("Inside of COMPUTE ENGINE LIST : " + computeEngineList.get(p));
            }
            iamExtract = new IAMExtract(computeEngineList);
            extractingIAM.add(iamExtract);
//            globalRole = computeEngineList.get(computeEngineList.size());
//            for (int n = 0; n < computeEngineList.size() - 1; n++) {
//                globalUser = computeEngineList.get(n);
//            }
            while (w != -1) {
                getiamlist.remove(w);
                w--;
            }
            for (int i = 0; i < getiamlist.size(); i++) {
                System.out.println("CHECKING GETIAMLIST AGAIN: " + getiamlist.get(i));
            }
            //END OF COMPUTE ENGINE LIST
        } else if (globalChecker == 2) {
            //START OF EDITOR LIST
            for (int o = 0; o < getiamlist.size(); o++) {
                String s = getiamlist.get(o);
                editorList.add(getiamlist.get(o));
                System.out.println("Adding into editor list : " + s);
                if (getiamlist.get(o).contains("role: roles/editor")) {
                    w = getiamlist.indexOf(getiamlist.get(o));
                    System.out.println("INDEX OF ROLE LINE : " + w);
                    System.out.println("FOUND THIS");
                    break;
                }
            }
            editorList.remove(0);
            for (int p = 0; p < editorList.size(); p++) {
                System.out.println("Inside of EDITOR LIST : " + editorList.get(p));
            }
            iamExtract = new IAMExtract(editorList);
            extractingIAM.add(iamExtract);
//            globalRole = editorList.get(editorList.size());
//            for (int n = 0; n < editorList.size() - 1; n++) {
//                globalUser = editorList.get(n);
//            }
            while (w != -1) {
                getiamlist.remove(w);
                w--;
            }
            for (int i = 0; i < getiamlist.size(); i++) {
                System.out.println("CHECKING GETIAMLIST AGAIN: " + getiamlist.get(i));
            }
            //END OF EDITOR LIST
        } else if (globalChecker == 5) {
            //START OF FIREBASE LIST
            for (int o = 0; o < getiamlist.size(); o++) {
                String s = getiamlist.get(o);
                firebaseList.add(getiamlist.get(o));
                System.out.println("Adding into firebase list : " + s);
                if (getiamlist.get(o).contains("role: roles/firebaserules.system")) {
                    w = getiamlist.indexOf(getiamlist.get(o));
                    System.out.println("INDEX OF ROLE LINE : " + w);
                    System.out.println("FOUND THIS");
                    break;
                }
            }
            firebaseList.remove(0);
            for (int p = 0; p < firebaseList.size(); p++) {
                System.out.println("Inside of FIRE BASE LIST : " + firebaseList.get(p));
            }
            iamExtract = new IAMExtract(firebaseList);
            extractingIAM.add(iamExtract);
//            globalRole = firebaseList.get(firebaseList.size());
//            for (int n = 0; n < firebaseList.size() - 1; n++) {
//                globalUser = firebaseList.get(n);
//            }
            while (w != -1) {
                getiamlist.remove(w);
                w--;
            }
            for (int i = 0; i < getiamlist.size(); i++) {
                System.out.println("CHECKING GETIAMLIST AGAIN: " + getiamlist.get(i));
            }
            //END OF FIREBASE LIST
        } else if (globalChecker == 7) {
            //START OF LOGGING ADMIN LIST
            for (int o = 0; o < getiamlist.size(); o++) {
                String s = getiamlist.get(o);
                loggingadminList.add(getiamlist.get(o));
                System.out.println("Adding into logging admin list : " + s);
                if (getiamlist.get(o).contains("role: roles/logging.admin")) {
                    w = getiamlist.indexOf(getiamlist.get(o));
                    System.out.println("INDEX OF ROLE LINE : " + w);
                    System.out.println("FOUND THIS");
                    break;
                }
            }
            loggingadminList.remove(0);
            for (int p = 0; p < loggingadminList.size(); p++) {
                System.out.println("Inside of LOGGING ADMIN LIST : " + loggingadminList.get(p));
            }
            iamExtract = new IAMExtract(loggingadminList);
            extractingIAM.add(iamExtract);
//            globalRole = loggingadminList.get(loggingadminList.size());
//            for (int n = 0; n < loggingadminList.size() - 1; n++) {
//                globalUser = loggingadminList.get(n);
//            }
            while (w != -1) {
                getiamlist.remove(w);
                w--;
            }
            for (int i = 0; i < getiamlist.size(); i++) {
                System.out.println("CHECKING GETIAMLIST AGAIN: " + getiamlist.get(i));
            }
        }
        //END OF LOGGING ADMIN LIST
        else if (globalChecker == 9) {
            //START OF MONITORING ADMIN LIST
            for (int o = 0; o < getiamlist.size(); o++) {
                String s = getiamlist.get(o);
                monitoringadminList.add(getiamlist.get(o));
                System.out.println("Adding into monitoring admin list : " + s);
                if (getiamlist.get(o).contains("role: roles/monitoring.admin")) {
                    w = getiamlist.indexOf(getiamlist.get(o));
                    System.out.println("INDEX OF ROLE LINE : " + w);
                    System.out.println("FOUND THIS");
                    break;
                }
            }
            monitoringadminList.remove(0);
            for (int p = 0; p < monitoringadminList.size(); p++) {
                System.out.println("Inside of MONITORING ADMIN LIST : " + monitoringadminList.get(p));
            }
            iamExtract = new IAMExtract(monitoringadminList);
            extractingIAM.add(iamExtract);
//            globalRole = monitoringadminList.get(monitoringadminList.size());
//            for (int n = 0; n < monitoringadminList.size() - 1; n++) {
//                globalUser = monitoringadminList.get(n);
//            }
            while (w != -1) {
                getiamlist.remove(w);
                w--;
            }
            for (int i = 0; i < getiamlist.size(); i++) {
                System.out.println("CHECKING GETIAMLIST AGAIN: " + getiamlist.get(i));
            }
        }
        //END OF MONITORING ADMIN LIST
        else if (globalChecker == 1) {
            //START OF OWNER LIST
            System.out.println("CHOSE OWNER LIST");
            for (int o = 0; o < getiamlist.size(); o++) {
                String s = getiamlist.get(o);
                ownerList.add(getiamlist.get(o));
                System.out.println("Adding into owner list : " + s);
                if (getiamlist.get(o).contains("role: roles/owner")) {
                    w = getiamlist.indexOf(getiamlist.get(o));
                    System.out.println("INDEX OF ROLE LINE : " + w);
                    System.out.println("FOUND THIS");
                    break;
                }
            }
            ownerList.remove(0);
            for (int p = 0; p < ownerList.size(); p++) {
                System.out.println("Inside of OWNER LIST : " + ownerList.get(p));
            }
            iamExtract = new IAMExtract(ownerList);
            extractingIAM.add(iamExtract);
//            globalRole = ownerList.get(ownerList.size());
//            for (int n = 0; n < ownerList.size() - 1; n++) {
//                globalUser = ownerList.get(n);
//            }
            while (w != -1) {
                getiamlist.remove(w);
                w--;
            }
            for (int i = 0; i < getiamlist.size(); i++) {
                System.out.println("CHECKING GETIAMLIST AGAIN: " + getiamlist.get(i));
            }
            //END OF OWNER LIST
        } else if (globalChecker == 10) {
            //START OF API KEYS ADMIN
            for (int o = 0; o < getiamlist.size(); o++) {
                String s = getiamlist.get(o);
                apikeysadminList.add(getiamlist.get(o));
                System.out.println("Adding into api keys admin list : " + s);
                if (getiamlist.get(o).contains("role: roles/serviceusage.apiKeysAdmin")) {
                    w = getiamlist.indexOf(getiamlist.get(o));
                    System.out.println("INDEX OF ROLE LINE : " + w);
                    System.out.println("FOUND THIS");
                    break;
                }
            }
            apikeysadminList.remove(0);
            for (int p = 0; p < apikeysadminList.size(); p++) {
                System.out.println("Inside of API KEYS ADMIN LIST : " + apikeysadminList.get(p));
            }
            iamExtract = new IAMExtract(apikeysadminList);
            extractingIAM.add(iamExtract);
//            globalRole = apikeysadminList.get(apikeysadminList.size());
//            for (int n = 0; n < apikeysadminList.size() - 1; n++) {
//                globalUser = apikeysadminList.get(n);
//            }
            while (w != -1) {
                getiamlist.remove(w);
                w--;
            }
            for (int i = 0; i < getiamlist.size(); i++) {
                System.out.println("CHECKING GETIAMLIST AGAIN: " + getiamlist.get(i));
            }
            //END OF API KEYS ADMIN
        } else if (globalChecker == 8) {
            //START OF STORAGE ADMIN LIST
            for (int o = 0; o < getiamlist.size(); o++) {
                String s = getiamlist.get(o);
                storageadminList.add(getiamlist.get(o));
                System.out.println("Adding into storage admin list : " + s);
                if (getiamlist.get(o).contains("role: roles/storage.admin")) {
                    w = getiamlist.indexOf(getiamlist.get(o));
                    System.out.println("INDEX OF ROLE LINE : " + w);
                    System.out.println("FOUND THIS");
                    break;
                }
            }
            storageadminList.remove(0);
            for (int p = 0; p < storageadminList.size(); p++) {
                System.out.println("Inside of STORAGE ADMIN LIST : " + storageadminList.get(p));
            }
            iamExtract = new IAMExtract(storageadminList);
            extractingIAM.add(iamExtract);
//            globalRole = storageadminList.get(storageadminList.size());
//            for (int n = 0; n < storageadminList.size() - 1; n++) {
//                globalUser = storageadminList.get(n);
//            }
            while (w != -1) {
                getiamlist.remove(w);
                w--;
            }
            for (int i = 0; i < getiamlist.size(); i++) {
                System.out.println("CHECKING GETIAMLIST AGAIN: " + getiamlist.get(i));
            }
            //END OF STORAGE ADMIN LIST
        } else if (globalChecker == 3) {
            //START OF VIEWER LIST
            for (int o = 0; o < getiamlist.size(); o++) {
                String s = getiamlist.get(o);
                viewerList.add(getiamlist.get(o));
                System.out.println("Adding into viewer list : " + s);
                if (getiamlist.get(o).contains("role: roles/viewer")) {
                    w = getiamlist.indexOf(getiamlist.get(o));
                    System.out.println("INDEX OF ROLE LINE : " + w);
                    System.out.println("FOUND THIS");
                    break;
                }
            }
            viewerList.remove(0);
            for (int p = 0; p < viewerList.size(); p++) {
                System.out.println("Inside of VIEWER LIST : " + viewerList.get(p));
            }
            iamExtract = new IAMExtract(viewerList);
            extractingIAM.add(iamExtract);
//            globalRole = viewerList.get(viewerList.size());
//            for (int n = 0; n < viewerList.size() - 1; n++) {
//                globalUser = viewerList.get(n);
//            }
            while (w != -1) {
                getiamlist.remove(w);
                w--;
            }
            for (int i = 0; i < getiamlist.size(); i++) {
                System.out.println("CHECKING GETIAMLIST AGAIN: " + getiamlist.get(i));
            }
            //END OF VIEWER LIST
        }
    }

    public int takeinGlobalChecker(int globalCheck) {
        globalChecker = globalCheck;
        System.out.println("GET IAM CHECKER: " + globalChecker);
        return globalChecker;
    }

    public ArrayList<IAMExtract> getExtractingIAM() {
        return extractingIAM;
    }

    public ArrayList<String> getCloudsqladminLIST() {
        return cloudsqladminLIST;
    }

    public ArrayList<String> getOwnerList() {
        return ownerList;
    }

    public ArrayList<String> getEditorList() {
        return editorList;
    }

    public ArrayList<String> getViewerList() {
        return viewerList;
    }

    public ArrayList<String> getFirebaseList() {
        return firebaseList;
    }

    public ArrayList<String> getComputeEngineList() {
        return computeEngineList;
    }

    public ArrayList<String> getLoggingadminList() {
        return loggingadminList;
    }

    public ArrayList<String> getMonitoringadminList() {
        return monitoringadminList;
    }

    public ArrayList<String> getApikeysadminList() {
        return apikeysadminList;
    }

    public ArrayList<String> getStorageadminList() {
        return storageadminList;
    }

    public ArrayList<String> getTempPermissionList() {
        return tempPermissionList;
    }

    public String getGlobalRole() {
        return globalRole;
    }

    public String getGlobalUser() {
        return globalUser;
    }

    public static int getGlobalChecker() {
        return globalChecker;
    }

    public String getLine() {
        return line;
    }
}
