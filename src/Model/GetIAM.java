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
    int diff;

    static int globalChecker;

    static ArrayList<IAMExtract> extractingIAM = new ArrayList<>();
    IAMExtract iamExtract;
    static int difference;

    public GetIAM() {
    }

    public void setwg() {
        this.w = 0;
        this.g = 0;
    }

    public void setEmptyList(){

    }

    public GetIAM(ArrayList<String> permissionList) {
        tempPermissionList = permissionList;
        System.out.println("PASSED IN PERMISSION LIST!! " + tempPermissionList);
        splitDiffArrayList();
    }

    public void splitDiffArrayList(){
        //START OF CloudSQL List
        if (globalChecker == 4) {
            ArrayList<Integer> indexList = new ArrayList<>();
            getiamlist = tempPermissionList;
            getiamlist.remove(0);
            System.out.println("HIHI " + getiamlist);
            for (int i = 0; i < getiamlist.size(); i++) {
                System.out.println("GetIAM Model trying to print permissions from getiamlist :" + getiamlist.get(i));
            }
            for (int o = 0; o < getiamlist.size(); o++) {
                if (getiamlist.get(o).contains("role: roles/cloudsql.admin")) {
                    w = getiamlist.indexOf(getiamlist.get(o)) + 1;
                    System.out.println("W is : " + w);
                }
            }
            for (int z = 0; z < getiamlist.size(); z++) {
                if (getiamlist.get(z).contains("members:")) {
                    g = z;
                    indexList.add(g);
                    System.out.println("G is : " + g);
                }
            }
            diff = indexList.get(0);
            int smallDifference = w - diff;
            System.out.println("SMALL DIFF = " + smallDifference);
            for (int m = 1; m < indexList.size(); m++) {
                difference = w - indexList.get(m);
                System.out.println("DIFFERENCE  = " + difference);
                if (difference < smallDifference && difference > 0) {
                    smallDifference = difference;
                    diff = w-smallDifference;
                    System.out.println("DIFF is : " + diff);
                } else if (difference > 0) {
                    System.out.println("DIFF is : " + indexList.get(0));
                }
            }
            System.out.println("MEMBER IS : " + diff);
            System.out.println("ROLE IS : " + w);
            cloudsqladminLIST = new ArrayList<String>(getiamlist.subList(diff, w));
//            getiamlist.subList(diff, w).clear();
            cloudsqladminLIST.remove(0);
            for (int p = 0; p < cloudsqladminLIST.size(); p++) {
                System.out.println("Inside of CLOUDSQLADMINLIST : " + cloudsqladminLIST.get(p));
            }
            for (int i = 0; i < getiamlist.size(); i++) {
                System.out.println("CHECKING GETIAMLIST AGAIN: " + getiamlist.get(i));
            }
            iamExtract = new IAMExtract(cloudsqladminLIST);
            extractingIAM.add(iamExtract);
            //END OF CLOUDSQL LIST
        } else if (globalChecker == 6) {
            //START OF COMPUTE ENGINE LIST
            ArrayList<Integer> indexList = new ArrayList<>();
            getiamlist = tempPermissionList;
            getiamlist.remove(0);
            System.out.println("HIHI " + getiamlist);
            for (int i = 0; i < getiamlist.size(); i++) {
                System.out.println("GetIAM Model trying to print permissions from getiamlist :" + getiamlist.get(i));
            }
            for (int o = 0; o < getiamlist.size(); o++) {
                if (getiamlist.get(o).contains("role: roles/compute.serviceAgent")) {
                    w = getiamlist.indexOf(getiamlist.get(o)) + 1;
                    System.out.println("W is : " + w);
                }
            }
            //getiamlist.get(i).contains("members:")
            //nearest "member" from w
            for (int z = 0; z < getiamlist.size(); z++) {
                if (getiamlist.get(z).contains("members:")) {
                    g = z;
                    indexList.add(g);
                    System.out.println("G is : " + g);
                }
            }
            diff = indexList.get(0);
            int smallDifference = w - diff;
            System.out.println("SMALL DIFF = " + smallDifference);
            for (int m = 1; m < indexList.size(); m++) {
                difference = w - indexList.get(m);
                System.out.println("DIFFERENCE  = " + difference);
                if (difference < smallDifference && difference > 0) {
                    smallDifference = difference;
                    diff = w-smallDifference;
                    System.out.println("DIFF is : " + diff);
                } else if (difference > 0) {
                    System.out.println("DIFF is : " + indexList.get(0));
                }
            }
            System.out.println("MEMBER IS : " + diff);
            System.out.println("ROLE IS : " + w);
            computeEngineList = new ArrayList<String>(getiamlist.subList(diff, w));
//            getiamlist.subList(diff, w).clear();
            computeEngineList.remove(0);
            for (int p = 0; p < computeEngineList.size(); p++) {
                System.out.println("Inside of COMPUTE ENGINE LIST : " + computeEngineList.get(p));
            }
            for (int i = 0; i < getiamlist.size(); i++) {
                System.out.println("CHECKING GETIAMLIST AGAIN: " + getiamlist.get(i));
            }
            iamExtract = new IAMExtract(computeEngineList);
            extractingIAM.add(iamExtract);
            //END OF COMPUTE ENGINE LIST
        } else if (globalChecker == 2) {
            //START OF EDITOR LIST
            ArrayList<Integer> indexList = new ArrayList<>();
            getiamlist = tempPermissionList;
            getiamlist.remove(0);
            System.out.println("HIHI " + getiamlist);
            for (int i = 0; i < getiamlist.size(); i++) {
                System.out.println("GetIAM Model trying to print permissions from getiamlist :" + getiamlist.get(i));
            }
            for (int o = 0; o < getiamlist.size(); o++) {
                if (getiamlist.get(o).contains("role: roles/editor")) {
                    w = getiamlist.indexOf(getiamlist.get(o)) + 1;
                    System.out.println("W is : " + w);
                }
            }
            //getiamlist.get(i).contains("members:")
            //nearest "member" from w
            for (int z = 0; z < getiamlist.size(); z++) {
                if (getiamlist.get(z).contains("members:")) {
                    g = z;
                    indexList.add(g);
                    System.out.println("G is : " + g);
                }
            }
            diff = indexList.get(0);
            int smallDifference = w - diff;
            System.out.println("SMALL DIFF = " + smallDifference);
            for (int m = 1; m < indexList.size(); m++) {
                difference = w - indexList.get(m);
                System.out.println("DIFFERENCE  = " + difference);
                if (difference < smallDifference && difference > 0) {
                    smallDifference = difference;
                    diff = w-smallDifference;
                    System.out.println("DIFF is : " + diff);
                } else if (difference > 0) {
                    System.out.println("DIFF is : " + indexList.get(0));
                }
            }
            System.out.println("MEMBER IS : " + diff);
            System.out.println("ROLE IS : " + w);
            editorList = new ArrayList<String>(getiamlist.subList(diff, w));
//            getiamlist.subList(diff, w).clear();
            editorList.remove(0);
            for (int p = 0; p < editorList.size(); p++) {
                System.out.println("Inside of EDITOR LIST : " + editorList.get(p));
            }
            for (int i = 0; i < getiamlist.size(); i++) {
                System.out.println("CHECKING GETIAMLIST AGAIN: " + getiamlist.get(i));
            }
            iamExtract = new IAMExtract(editorList);
            extractingIAM.add(iamExtract);
            //END OF EDITOR LIST


        } else if (globalChecker == 5) {
            ArrayList<Integer> indexList = new ArrayList<>();
            getiamlist = tempPermissionList;
            getiamlist.remove(0);
            System.out.println("HIHI " + getiamlist);
            for (int i = 0; i < getiamlist.size(); i++) {
                System.out.println("GetIAM Model trying to print permissions from getiamlist :" + getiamlist.get(i));
            }
            //START OF FIREBASE LIST
            for (int o = 0; o < getiamlist.size(); o++) {
                if (getiamlist.get(o).contains("role: roles/firebaserules.system")) {
                    w = getiamlist.indexOf(getiamlist.get(o)) + 1;
                    System.out.println("W is : " + w);
                }
            }
            //getiamlist.get(i).contains("members:")
            //nearest "member" from w
            for (int z = 0; z < getiamlist.size(); z++) {
                if (getiamlist.get(z).contains("members:")) {
                    g = z;
                    indexList.add(g);
                    System.out.println("G is : " + g);
                }
            }
            diff = indexList.get(0);
            int smallDifference = w - diff;
            System.out.println("SMALL DIFF = " + smallDifference);
            for (int m = 1; m < indexList.size(); m++) {
                difference = w - indexList.get(m);
                System.out.println("DIFFERENCE  = " + difference);
                if (difference < smallDifference && difference > 0) {
                    smallDifference = difference;
                    diff = w-smallDifference;
                    System.out.println("DIFF is : " + diff);
                } else if (difference > 0) {
                    System.out.println("DIFF is : " + indexList.get(0));
                }
            }
            System.out.println("MEMBER IS : " + diff);
            System.out.println("ROLE IS : " + w);
            firebaseList = new ArrayList<String>(getiamlist.subList(diff, w));
//            getiamlist.subList(diff, w).clear();
            firebaseList.remove(0);
            for (int p = 0; p < firebaseList.size(); p++) {
                System.out.println("Inside of FIREBASE RULES LIST : " + firebaseList.get(p));
            }
            for (int i = 0; i < getiamlist.size(); i++) {
                System.out.println("CHECKING GETIAMLIST AGAIN: " + getiamlist.get(i));
            }
            iamExtract = new IAMExtract(firebaseList);
            extractingIAM.add(iamExtract);
            //END OF FIREBASE LIST


        } else if (globalChecker == 7) {
            ArrayList<Integer> indexList = new ArrayList<>();
            getiamlist = tempPermissionList;
            getiamlist.remove(0);
            System.out.println("HIHI " + getiamlist);
            for (int i = 0; i < getiamlist.size(); i++) {
                System.out.println("GetIAM Model trying to print permissions from getiamlist :" + getiamlist.get(i));
            }
            //START OF LOGGING ADMIN LIST

            for (int o = 0; o < getiamlist.size(); o++) {
                if (getiamlist.get(o).contains("role: roles/logging.admin")) {
                    w = getiamlist.indexOf(getiamlist.get(o)) + 1;
                    System.out.println("W is : " + w);
                }
            }
            //getiamlist.get(i).contains("members:")
            //nearest "member" from w
            for (int z = 0; z < getiamlist.size(); z++) {
                if (getiamlist.get(z).contains("members:")) {
                    g = z;
                    indexList.add(g);
                    System.out.println("G is : " + g);
                }
            }
            diff = indexList.get(0);
            int smallDifference = w - diff;
            System.out.println("SMALL DIFF = " + smallDifference);
            for (int m = 1; m < indexList.size(); m++) {
                difference = w - indexList.get(m);
                System.out.println("DIFFERENCE  = " + difference);
                if (difference < smallDifference && difference > 0) {
                    smallDifference = difference;
                    diff = w-smallDifference;
                    System.out.println("DIFF is : " + diff);
                } else if (difference > 0) {
                    System.out.println("DIFF is : " + indexList.get(0));
                }
            }
            System.out.println("MEMBER IS : " + diff);
            System.out.println("ROLE IS : " + w);
            loggingadminList = new ArrayList<String>(getiamlist.subList(diff, w));
//            getiamlist.subList(diff, w).clear();
            loggingadminList.remove(0);
            for (int p = 0; p < loggingadminList.size(); p++) {
                System.out.println("Inside of LOGGING ADMIN LIST : " + loggingadminList.get(p));
            }
            for (int i = 0; i < getiamlist.size(); i++) {
                System.out.println("CHECKING GETIAMLIST AGAIN: " + getiamlist.get(i));
            }
            iamExtract = new IAMExtract(loggingadminList);
            extractingIAM.add(iamExtract);
        }
        //END OF LOGGING ADMIN LIST


        else if (globalChecker == 9) {
            ArrayList<Integer> indexList = new ArrayList<>();
            getiamlist = tempPermissionList;
            getiamlist.remove(0);
            System.out.println("HIHI " + getiamlist);
            for (int i = 0; i < getiamlist.size(); i++) {
                System.out.println("GetIAM Model trying to print permissions from getiamlist :" + getiamlist.get(i));
            }
            //START OF MONITORING ADMIN LIST

            for (int o = 0; o < getiamlist.size(); o++) {
                if (getiamlist.get(o).contains("role: roles/monitoring.admin")) {
                    w = getiamlist.indexOf(getiamlist.get(o)) + 1;
                    System.out.println("W is : " + w);
                }
            }
            //getiamlist.get(i).contains("members:")
            //nearest "member" from w
            for (int z = 0; z < getiamlist.size(); z++) {
                if (getiamlist.get(z).contains("members:")) {
                    g = z;
                    indexList.add(g);
                    System.out.println("G is : " + g);
                }
            }
            diff = indexList.get(0);
            int smallDifference = w - diff;
            System.out.println("SMALL DIFF = " + smallDifference);
            for (int m = 1; m < indexList.size(); m++) {
                difference = w - indexList.get(m);
                System.out.println("DIFFERENCE  = " + difference);
                if (difference < smallDifference && difference > 0) {
                    smallDifference = difference;
                    diff = w-smallDifference;
                    System.out.println("DIFF is : " + diff);
                } else if (difference > 0) {
                    System.out.println("DIFF is : " + indexList.get(0));
                }
            }
            System.out.println("MEMBER IS : " + diff);
            System.out.println("ROLE IS : " + w);
            monitoringadminList = new ArrayList<String>(getiamlist.subList(diff, w));
//            getiamlist.subList(diff, w).clear();
            monitoringadminList.remove(0);
            for (int p = 0; p < monitoringadminList.size(); p++) {
                System.out.println("Inside of MONITORING ADMIN LIST : " + monitoringadminList.get(p));
            }
            for (int i = 0; i < getiamlist.size(); i++) {
                System.out.println("CHECKING GETIAMLIST AGAIN: " + getiamlist.get(i));
            }
            iamExtract = new IAMExtract(monitoringadminList);
            extractingIAM.add(iamExtract);
        }
        //END OF MONITORING ADMIN LIST


        else if (globalChecker == 1) {
            ArrayList<Integer> indexList = new ArrayList<>();
            getiamlist = tempPermissionList;
            getiamlist.remove(0);
            System.out.println("HIHI " + getiamlist);
            for (int i = 0; i < getiamlist.size(); i++) {
                System.out.println("GetIAM Model trying to print permissions from getiamlist :" + getiamlist.get(i));
            }
            //START OF OWNER LIST

            for (int o = 0; o < getiamlist.size(); o++) {
                if (getiamlist.get(o).contains("role: roles/owner")) {
                    w = getiamlist.indexOf(getiamlist.get(o)) + 1;
                    System.out.println("W is : " + w);
                }
            }
            //getiamlist.get(i).contains("members:")
            //nearest "member" from w
            for (int z = 0; z < getiamlist.size(); z++) {
                if (getiamlist.get(z).contains("members:")) {
                    g = z;
                    indexList.add(g);
                    System.out.println("G is : " + g);
                }
            }
            diff = indexList.get(0);
            int smallDifference = w - diff;
            System.out.println("SMALL DIFF = " + smallDifference);
            for (int m = 1; m < indexList.size(); m++) {
                difference = w - indexList.get(m);
                System.out.println("DIFFERENCE  = " + difference);
                if (difference < smallDifference && difference > 0) {
                    smallDifference = difference;
                    diff = w-smallDifference;
                    System.out.println("DIFF is : " + diff);
                } else if (difference > 0) {
                    System.out.println("DIFF is : " + indexList.get(0));
                }
            }
            System.out.println("MEMBER IS : " + diff);
            System.out.println("ROLE IS : " + w);
            ownerList = new ArrayList<String>(getiamlist.subList(diff, w));
//            getiamlist.subList(diff, w).clear();
            ownerList.remove(0);
            for (int p = 0; p < ownerList.size(); p++) {
                System.out.println("Inside of OWNER LIST : " + ownerList.get(p));
            }
            for (int i = 0; i < getiamlist.size(); i++) {
                System.out.println("CHECKING GETIAMLIST AGAIN: " + getiamlist.get(i));
            }
            iamExtract = new IAMExtract(ownerList);
            extractingIAM.add(iamExtract);
            //END OF OWNER LIST
        } else if (globalChecker == 10) {
            ArrayList<Integer> indexList = new ArrayList<>();
            getiamlist = tempPermissionList;
            getiamlist.remove(0);
            System.out.println("HIHI " + getiamlist);
            for (int i = 0; i < getiamlist.size(); i++) {
                System.out.println("GetIAM Model trying to print permissions from getiamlist :" + getiamlist.get(i));
            }
            //START OF API KEYS ADMIN

            for (int o = 0; o < getiamlist.size(); o++) {
                if (getiamlist.get(o).contains("role: roles/serviceusage.apiKeysAdmin")) {
                    w = getiamlist.indexOf(getiamlist.get(o)) + 1;
                    System.out.println("W is : " + w);
                }
            }
            //getiamlist.get(i).contains("members:")
            //nearest "member" from w
            for (int z = 0; z < getiamlist.size(); z++) {
                if (getiamlist.get(z).contains("members:")) {
                    g = z;
                    indexList.add(g);
                    System.out.println("G is : " + g);
                }
            }
            diff = indexList.get(0);
            int smallDifference = w - diff;
            System.out.println("SMALL DIFF = " + smallDifference);
            for (int m = 1; m < indexList.size(); m++) {
                difference = w - indexList.get(m);
                System.out.println("DIFFERENCE  = " + difference);
                if (difference < smallDifference && difference > 0) {
                    smallDifference = difference;
                    diff = w-smallDifference;
                    System.out.println("DIFF is : " + diff);
                } else if (difference > 0) {
                    System.out.println("DIFF is : " + indexList.get(0));
                }
            }
            System.out.println("MEMBER IS : " + diff);
            System.out.println("ROLE IS : " + w);
            apikeysadminList = new ArrayList<String>(getiamlist.subList(diff, w));
//            getiamlist.subList(diff, w).clear();
            apikeysadminList.remove(0);
            for (int p = 0; p < apikeysadminList.size(); p++) {
                System.out.println("Inside of API KEYS ADMIN LIST : " + apikeysadminList.get(p));
            }
            for (int i = 0; i < getiamlist.size(); i++) {
                System.out.println("CHECKING GETIAMLIST AGAIN: " + getiamlist.get(i));
            }
            iamExtract = new IAMExtract(apikeysadminList);
            extractingIAM.add(iamExtract);
            //END OF API KEYS ADMIN


        } else if (globalChecker == 8) {
            ArrayList<Integer> indexList = new ArrayList<>();
            getiamlist = tempPermissionList;
            getiamlist.remove(0);
            System.out.println("HIHI " + getiamlist);
            for (int i = 0; i < getiamlist.size(); i++) {
                System.out.println("GetIAM Model trying to print permissions from getiamlist :" + getiamlist.get(i));
            }
            //START OF STORAGE ADMIN LIST
            for (int o = 0; o < getiamlist.size(); o++) {
                if (getiamlist.get(o).contains("role: roles/storage.admin")) {
                    w = getiamlist.indexOf(getiamlist.get(o)) + 1;
                    System.out.println("W is : " + w);
                }
            }
            //getiamlist.get(i).contains("members:")
            //nearest "member" from w
            for (int z = 0; z < getiamlist.size(); z++) {
                if (getiamlist.get(z).contains("members:")) {
                    g = z;
                    indexList.add(g);
                    System.out.println("G is : " + g);
                }
            }
            diff = indexList.get(0);
            int smallDifference = w - diff;
            System.out.println("SMALL DIFF = " + smallDifference);
            for (int m = 1; m < indexList.size(); m++) {
                difference = w - indexList.get(m);
                System.out.println("DIFFERENCE  = " + difference);
                if (difference < smallDifference && difference > 0) {
                    smallDifference = difference;
                    diff = w-smallDifference;
                    System.out.println("DIFF is : " + diff);
                } else if (difference > 0) {
                    System.out.println("DIFF is : " + indexList.get(0));
                }
            }
            System.out.println("MEMBER IS : " + diff);
            System.out.println("ROLE IS : " + w);
            storageadminList = new ArrayList<String>(getiamlist.subList(diff, w));
//            getiamlist.subList(diff, w).clear();
            storageadminList.remove(0);
            for (int p = 0; p < storageadminList.size(); p++) {
                System.out.println("Inside of STORAGE ADMIN LIST : " + storageadminList.get(p));
            }
            for (int i = 0; i < getiamlist.size(); i++) {
                System.out.println("CHECKING GETIAMLIST AGAIN: " + getiamlist.get(i));
            }
            iamExtract = new IAMExtract(storageadminList);
            extractingIAM.add(iamExtract);
            //END OF STORAGE ADMIN LIST


        } else if (globalChecker == 3) {
            ArrayList<Integer> indexList = new ArrayList<>();
            getiamlist = tempPermissionList;
            getiamlist.remove(0);
            System.out.println("HIHI " + getiamlist);
            for (int i = 0; i < getiamlist.size(); i++) {
                System.out.println("GetIAM Model trying to print permissions from getiamlist :" + getiamlist.get(i));
            }
            //START OF VIEWER LIST
            for (int o = 0; o < getiamlist.size(); o++) {
                if (getiamlist.get(o).contains("role: roles/viewer")) {
                    w = getiamlist.indexOf(getiamlist.get(o)) + 1;
                    System.out.println("W is : " + w);
                }
            }
            //getiamlist.get(i).contains("members:")
            //nearest "member" from w
            for (int z = 0; z < getiamlist.size(); z++) {
                if (getiamlist.get(z).contains("members:")) {
                    g = z;
                    indexList.add(g);
                    System.out.println("G is : " + g);
                }
            }
            diff = indexList.get(0);
            int smallDifference = w - diff;
            System.out.println("SMALL DIFF = " + smallDifference);
            for (int m = 1; m < indexList.size(); m++) {
                difference = w - indexList.get(m);
                System.out.println("DIFFERENCE  = " + difference);
                if (difference < smallDifference && difference > 0) {
                    smallDifference = difference;
                    diff = w-smallDifference;
                    System.out.println("DIFF is : " + diff);
                } else if (difference > 0) {
                    System.out.println("DIFF is : " + indexList.get(0));
                }
            }
            System.out.println("MEMBER IS : " + diff);
            System.out.println("ROLE IS : " + w);
            viewerList = new ArrayList<String>(getiamlist.subList(diff, w));
            viewerList.remove(0);
            for (int p = 0; p < viewerList.size(); p++) {
                System.out.println("Inside of VIEWER LIST : " + viewerList.get(p));
            }
            for (int i = 0; i < getiamlist.size(); i++) {
                System.out.println("CHECKING GETIAMLIST AGAIN: " + getiamlist.get(i));
            }
            iamExtract = new IAMExtract(viewerList);
            extractingIAM.add(iamExtract);
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
