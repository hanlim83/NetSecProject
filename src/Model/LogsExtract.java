package Model;

import com.google.cloud.logging.LogEntry;
import com.google.cloud.logging.Payload;
import com.jfoenix.controls.JFXSnackbar;
import javafx.application.Platform;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.net.UnknownHostException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class LogsExtract {
    String severitycolour;
    static int colorchecker=0;

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
        severitycolour = logentry.getSeverity().toString();
        this.severity = severitycolour;

        // Get type of action -> GCS Bucket / CloudSQL / Project
        this.action = logentry.getResource().getType();
        // Get label -> Location, ProjectID, Bucketname
        this.bucketName = logentry.getResource().getLabels().toString();

        this.user = logentry.getPayload().getData().toString();

        Scanner sc1 = new Scanner(user);
        stringList.add(sc1.nextLine());
        while (sc1.hasNextLine()) {
            stringList.add(sc1.nextLine());
        }
        //checking if the entire whole string has @ sign anot
        for (int i = 0; i < stringList.size(); i++) {
            Matcher m = p1.matcher(stringList.get(i));
            if (m.find()) {
                //   string1st=stringList.get(i);
                //if got @ sign, store in stringlist2
                stringList2.add(stringList.get(i));
                System.out.println("got @" + stringList.get(i));
            } else {
                //if no @ sign, store in stringList3
                stringList3.add(stringList.get(i));
                System.out.println("no @ " + stringList.get(i));
                this.nonFinalEmail = stringList3.get(i);
                System.out.println("HEEEEEEEREEEEEEEEEEEEEEEEEEEEEEEEEEE =================== " + nonFinalEmail);
                //HEEEEEEEREEEEEEEEEEEEEEEEEEEEEEEEEEE =================== type_url: "type.googleapis.com/google.cloud.audit.AuditLog"
                this.globalchecker = -1;
                globalChckerList.add(globalchecker);
            }
        }


        // check if got slash first!
        for (int o = 0; o < stringList2.size(); o++) {
            Matcher m1 = p.matcher(stringList2.get(o));
            System.out.println("======== Checking if got slash!");
            if (m1.find()) {
                //if got slash (/)
                System.out.println("======== Got Slash");
                stringList5.add(stringList2.get(o));
                System.out.println("Added into stringlist5!");
            } else {
                //if don even have slash(/)
                System.out.println("========== No Slash");
                stringList6.add(stringList2.get(o));

            }
        }


        Scanner sc = null;
        for (int k = 0; k < stringList5.size(); k++) {
            sc = new Scanner(stringList5.get(k)).useDelimiter("\\\\");
            stringList4.add(sc.next());
            while (sc.hasNext()) {
                stringList4.add(sc.next());
            }

        }

        listIterator = stringList4.listIterator();
        while (listIterator.hasNext()) {
            String emailString = listIterator.next().toString();
            System.out.println(emailString);

            Matcher m2 = p2.matcher(emailString);
            if (m2.find()) {
                this.finalEmail = emailString.substring(3, emailString.length());
                this.globalchecker = 1;
                globalChckerList.add(globalchecker);
                System.out.println("HERE IS THE FINAL EMAIL LALALALAA : " + finalEmail);
            } else {
                System.out.println("GOT @ but not EMAIL");

            }
        }

    }

    public int getColorchecker() {
        return colorchecker;
    }

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