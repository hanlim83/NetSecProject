package Model;

import com.google.cloud.logging.LogEntry;
import com.google.cloud.logging.Payload;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
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
    String p = "/";
    Pattern p1 = Pattern.compile("@");

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

        //use slash as delimiter!! (/)
        //get all the string
        // check if @ exist
        //if @ exist take the string and substring the 3 numbers infront


        this.user=logentry.getPayload().getData().toString();
        try {
            Scanner sc = new Scanner(user).useDelimiter("@");
            //Breaking into 2 parts of string, 1 infront of @, 1 after of @
            String firstpart = sc.next();
            String secondpart = sc.next();
            System.out.println("FIRST PART : " +firstpart);
            //Part of email in front of @
            Scanner sc2 = new Scanner(firstpart).useDelimiter("\\[0-9][0-9][0-9]");
            String frontpart = sc2.next();
            System.out.println("EMAIL FRONT PART : " + frontpart);

            //Part of email behind @ infront of first /
            Scanner sc1 = new Scanner(secondpart).useDelimiter("\\\\");
            String lastpart = sc1.next();
            System.out.println("EMAIL PART : " + lastpart);
        }catch(java.util.NoSuchElementException e){
            e.printStackTrace();
        }

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

    public String getSeverity() {
        return severity;
    }
}
