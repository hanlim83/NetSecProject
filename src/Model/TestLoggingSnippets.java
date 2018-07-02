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
    public static void main (String [] args) throws Exception{
        LoggingSnippets loggingsnippets = new LoggingSnippets();
        // Create a service object
        // Credentials are inferred from the environment
//        LoggingOptions options = LoggingOptions.getDefaultInstance();
        LoggingOptions options = LoggingOptions.getDefaultInstance();
        try(Logging logging = options.getService()) {

//            // Create a log entry
//            LogEntry firstEntry = LogEntry.newBuilder(Payload.StringPayload.of("message"))
//                    .setLogName("test-log")
//                    .setResource(MonitoredResource.newBuilder("global")
//                            .addLabel("project_id", options.getProjectId())
//                            .build())
//                    .build();
//            logging.write(Collections.singleton(firstEntry));

            // List log entries
            Page<LogEntry> entries = logging.listLogEntries(
                    EntryListOption.filter("logName=projects/" + options.getProjectId() + "/logs/test-log"));
            for (LogEntry logEntry : entries.iterateAll()) {
                System.out.println(logEntry);
            }
        }
    }
}
