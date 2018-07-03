package Model;

import com.google.cloud.MonitoredResource;
import com.google.api.gax.paging.Page;
import com.google.cloud.logging.LogEntry;
import com.google.cloud.logging.Logging;
import com.google.cloud.logging.Logging.EntryListOption;
import com.google.cloud.logging.LoggingOptions;
import com.google.cloud.logging.Payload.StringPayload;

import java.util.Collections;

public class TestLoggingSnippets {
    public static void main(String[] args) throws Exception {
        LoggingSnippets loggingsnippets = new LoggingSnippets();

        // Create a service object
        // Credentials are inferred from the environment
        LoggingOptions options = LoggingOptions.getDefaultInstance();
        try (Logging logging = options.getService()) {
            System.out.println(options.getProjectId());


            // List log entries
            //filters -> delete , create
            Page<LogEntry> entries = logging.listLogEntries(EntryListOption.filter("create"));
            for (LogEntry logEntry : entries.iterateAll()) {
                // System.out.println(logEntry);

                // get RESOURCES
               // System.out.println(logEntry.getResource());

                //getting type -> GCS Bucket / cloudSQL or project
                System.out.println(logEntry.getResource().getType());

                //getting labels -> Location, projectid, bucketname
                // System.out.println(logEntry.getResource().getLabels());

                //get severity
                //    System.out.println(logEntry.getSeverity());

                //get payload -> name of user\
                //if -> type_url: "type.googleapis.com/google.cloud.audit.AuditLog" means is delete something
               // System.out.println(logEntry.getPayload().getData());

                //get value
                // System.out.println(logEntry.toString());
                //  System.out.println(logEntry.get);

                // Create a log entry
//            LogEntry firstEntry = LogEntry.newBuilder(Payload.StringPayload.of("message"))
//                    .setLogName("test-log")
//                    .setResource(MonitoredResource.newBuilder("global")
//                            .addLabel("project_id", options.getProjectId())
//                            .build())
//                    .build();
//            logging.write(Collections.singleton(firstEntry));
            }
        }
    }
}
