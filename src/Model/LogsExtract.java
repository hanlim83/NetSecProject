package Model;

import com.google.cloud.logging.LogEntry;
import com.google.cloud.logging.Payload;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class LogsExtract {
    public LogEntry logentry;
    private String timestamp;
    private String action;
    private String bucketName;
    private String user;
    private String projectID;
    private String severity;

    private String shorteningUser;

    Object obj = new JSONObject();
    Payload.JsonPayload jsonPayload;

    Object o = new Object();

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

        System.out.println(logentry.getPayload().getData().toString());
;


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

    public String getProjectID() {
        return projectID;
    }

    public String getSeverity() {
        return severity;
    }
}
