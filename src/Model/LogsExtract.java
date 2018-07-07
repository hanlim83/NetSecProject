package Model;

import com.google.cloud.logging.LogEntry;
import com.google.cloud.logging.Payload;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class LogsExtract {
    public LogEntry logentry;
    private String timestamp;
    private String action;
    private String bucketName;
    private String user;
    //    private String projectID;
    private String severity;
    private String finalEmail;
    private String nonFinalEmail;
    Pattern p = Pattern.compile("\\\\");
    Pattern p1 = Pattern.compile("@");
    Pattern p2 = Pattern.compile("gmail.com");
    ArrayList<String> stringList = new ArrayList();
    ArrayList<String> stringList2 = new ArrayList();
    ArrayList<String> stringList3 = new ArrayList();
    ArrayList<String> stringList4 = new ArrayList();
    ArrayList<String> stringList5 = new ArrayList();
    ArrayList<String> stringList6 = new ArrayList();
    Iterator listIterator;

    String string1st;

    int globalchecker = 0;
    ArrayList<Integer> globalChckerList = new ArrayList<>();

//    Object obj = new JSONObject();
//    Payload.JsonPayload jsonPayload;
//
//    Object o = new Object();

    public LogsExtract() {

    }

    public LogsExtract(LogEntry logentry) {
        this.logentry = logentry;
        // Get Timestamp
        Date date = new Date(logentry.getReceiveTimestamp());
        DateFormat formatter = new SimpleDateFormat("HH:mm:ss");
        String dateFormatted = formatter.format(date);
        this.timestamp = dateFormatted;
        // Get Severity
        this.severity = logentry.getSeverity().toString();
        // Get type of action -> GCS Bucket / CloudSQL / Project
        this.action = logentry.getResource().getType();
        // Get label -> Location, ProjectID, Bucketname
        this.bucketName = logentry.getResource().getLabels().toString();
        //Get payload
        // if type_url: "type.googleapis.com/google.cloud.audit.AuditLog" -> Creation/Deletion of bucket
        //
//        o = logentry.getPayload();
//        ((Payload) o).toString();
//        System.out.println(((Payload) o).toString());
//        String jsonString = logentry.getPayload().getData().toString();
//        JSONObject jsonResult = new JSONObject(jsonString);
//        JSONArray data = jsonResult.getJSONArray("data");

        //tdy since 1.11pm
        //use slash as delimiter!! (/)
        //get all the string and store in arraylist
        // check if @ exist
        //if @ exist take the string and substring the 3 numbers infront
        this.user = logentry.getPayload().getData().toString();

        Scanner sc1 = new Scanner(user);
        stringList.add(sc1.nextLine());
        while (sc1.hasNextLine()) {
            stringList.add(sc1.nextLine());
        }
        //checking if the entire whole string has @ sign anot
               for (int i=0;i<stringList.size();i++) {
                   Matcher m = p1.matcher(stringList.get(i));
                   if (m.find()){
                    //   string1st=stringList.get(i);
                       //if got @ sign, store in stringlist2
                       stringList2.add(stringList.get(i));
                       System.out.println("got @"+stringList.get(i));
                   }else{
                       //if no @ sign, store in stringList3
                       stringList3.add(stringList.get(i));
                       System.out.println("no @ " + stringList.get(i));
                       this.nonFinalEmail=stringList3.get(i);
                       System.out.println("HEEEEEEEREEEEEEEEEEEEEEEEEEEEEEEEEEE =================== " + nonFinalEmail);
                       //HEEEEEEEREEEEEEEEEEEEEEEEEEEEEEEEEEE =================== type_url: "type.googleapis.com/google.cloud.audit.AuditLog"
                       this.globalchecker=-1;
                       globalChckerList.add(globalchecker);
                   }
               }


        // check if got slash first!
        for(int o=0;o<stringList2.size();o++){
            Matcher m1=p.matcher(stringList2.get(o));
            System.out.println("======== Checking if got slash!");
            if (m1.find()){
                //if got slash (/)
                System.out.println("======== Got Slash");
                stringList5.add(stringList2.get(o));
                System.out.println("Added into stringlist5!");
            }
            else {
                //if don even have slash(/)
                System.out.println("========== No Slash");
                stringList6.add(stringList2.get(o));

            }
        }


        Scanner sc=null;
        for (int k=0;k<stringList5.size();k++) {
            sc = new Scanner(stringList5.get(k)).useDelimiter("\\\\");
            stringList4.add(sc.next());
            while (sc.hasNext()) {
                stringList4.add(sc.next());
            }

        }

        //seperating into different strings with \ as delimiter and storing in arraylist
//        stringList4.add(sc.next());
//        stringList4.add(sc.nextLine());

        listIterator = stringList4.listIterator();
        //check if @ exist
        //       for (int i=0;i<stringList.size();i++){
        //           Matcher m = p1.matcher(stringList.get(i));
        //           int checker =0;
        while (listIterator.hasNext()) {
//            for (int i = 0; i < stringList.size(); i++) {
//                Matcher m = p1.matcher(stringList.get(i));
            String emailString = listIterator.next().toString();
            System.out.println(emailString);
//            Matcher m = p1.matcher(emailString);
//            if (m.find()) {
//                System.out.println("Found @ Sign!");
//                    String emailString = stringList.get(i);

                Matcher m2 = p2.matcher(emailString);
                if (m2.find()){
                    this.finalEmail = emailString.substring(3, emailString.length());
                    this.globalchecker = 1;
                    globalChckerList.add(globalchecker);
                    System.out.println("HERE IS THE FINAL EMAIL LALALALAA : " + finalEmail);
                }
                else {
                    System.out.println("GOT @ but not EMAIL");

                }
//                int count = 0;
//                for (int i = 0; i < emailString.length(); i++) {
//                    if (emailString.charAt(i) == 'i') {
//                        count++;
//                    }
//                }
//
//                if (count > 15) {
//                    this.finalEmail = emailString.substring(3, emailString.length());
//                    System.out.println("HERE IS THE FINAL EMAIL LALALALAA : " + finalEmail);
//                } else {
//                    System.out.println("GOT @ but not EMAIL");
//                    // need to pull out entire string that is not email
//                }
                //                    stringList2.add(emailString);
//            } else {
//                System.out.println("Did not find @ sign");
//                globalchecker = -1;
//                System.out.println(globalchecker);
//                globalChckerList.add(globalchecker);
//
//                System.out.println("GOT STUFF BUT NO @ SIGN");
//               String nonemailString =  emailString;
//                }
//                else {
//                    System.out.println(" ===================================== NULL!!! GOT NOTHING ITS BLANK");
//                }
//            }
//                    String nonemailString =  listIterator.next().toString();
//                    System.out.println(nonemailString);
//                    this.nonFinalEmail = nonemailString;
//                    stringList3.add(nonemailString);
        }

//        }
//        }
        //Extracting the first 3 numbers infront of the string
//        for (int i = 0; i < stringList2.size(); i++) {
//            globalchecker = 1;
//            System.out.println(globalchecker);
//            globalChckerList.add(globalchecker);
//            this.finalEmail = stringList2.get(i).substring(3, stringList2.get(i).length());
//            System.out.println(finalEmail);
//        }
//        for (int k=0;k<stringList3.size();k++){
//            this.user=stringList3.get(k);
//        }
//
//        try {
//            Scanner sc = new Scanner(user).useDelimiter("@");
//            //Breaking into 2 parts of string, 1 infront of @, 1 after of @
//            String firstpart = sc.next();
//            String secondpart = sc.next();
//            System.out.println("FIRST PART : " +firstpart);
//            //Part of email in front of @
//            Scanner sc2 = new Scanner(firstpart).useDelimiter("\\[0-9][0-9][0-9]");
//            String frontpart = sc2.next();
//            System.out.println("EMAIL FRONT PART : " + frontpart);
//
//            //Part of email behind @ infront of first /
//            Scanner sc1 = new Scanner(secondpart).useDelimiter("\\\\");
//            String lastpart = sc1.next();
//            System.out.println("EMAIL PART : " + lastpart);
//        }catch(java.util.NoSuchElementException e){
//            e.printStackTrace();
//        }

//        Matcher m = p1.matcher(user);
//
//        if (m.find()){
//            System.out.println("Found @ sign!");
//            //find the first slash(/) infront of @ sign
//            user.split(String.valueOf(p1));
//            System.out.println((user.split(Pattern.quote(p))));
//            //find the first slash(/) after the @ sign
//        }


    }
//get RESOURCES
//    resourceType=entry.getResource().getType();
//                System.out.println(resourceType);

//getting type -> GCS Bucket / cloudSQL or project
    //     System.out.println(entry.getResource().getType());

    //getting labels -> Location, projectid, bucketname
    // System.out.println(entry.getResource().getLabels());

    //get severity
    //    System.out.println(entry.getSeverity());

    //get payload -> name of user\
    //if -> type_url: "type.googleapis.com/google.cloud.audit.AuditLog" means is deleted/created something
    // System.out.println(entry.getPayload().getData());


    public ArrayList<Integer> getGlobalChckerList() {
        return globalChckerList;
    }

    public String getNonFinalEmail() {
        return nonFinalEmail;
    }

    public int getGlobalchecker() {
        return globalchecker;
    }

    public LogEntry getLogentry() {
        return logentry;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public String getAction() {
        return action;
    }

    public String getBucketName() {
        return bucketName;
    }

    public String getUser() {
        return user;
    }

//    public String getProjectID() {
//        return projectID;
//    }


    public String getFinalEmail() {
        return finalEmail;
    }

    public ArrayList<String> getStringList3() {
        return stringList3;
    }

    public String getSeverity() {
        return severity;
    }
}
