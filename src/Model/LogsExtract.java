package Model;

import com.google.cloud.logging.LogEntry;

public class LogsExtract {
    public LogEntry logentry;
    private String timestamp;
    private String action;
    private String bucketName;
    private String user;
    private String projectID;
    private String severity;

    public LogsExtract(){

    }

    public LogsExtract(LogEntry logentry){
        this.logentry=logentry;
        this.timestamp=logentry.getTimestamp().toString();
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

    public String getProjectID() {
        return projectID;
    }

    public String getSeverity() {
        return severity;
    }
}
