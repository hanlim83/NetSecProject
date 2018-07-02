import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Scanner;

/**
 * A sample app that connects to a Cloud SQL instance and lists all available tables in a database.
 */
public class ListTables {
    // TODO: fill this in
    // The instance connection name can be obtained from the instance overview page in Cloud Console
    // or by running "gcloud sql instances describe <instance> | grep connectionName".
    static String instanceConnectionName = "netsecpj:us-central1:nspj-project";

    // TODO: fill this in
    // The database from which to list tables.
    static String databaseName = "device_build_number";

    static String username = "root";

    // TODO: fill this in
    // This is the password that was set via the Cloud Console or empty if never set
    // (not recommended).
    static String password = "root";

    public static void main(String[] args) throws IOException, SQLException {
        if (instanceConnectionName.equals("<device-supported-versions>")) {
            System.err.println("Please update the sample to specify the instance connection name.");
            System.exit(1);
        }

        if (password.equals("<insert_password>")) {
            System.err.println("Please update the sample to specify the mysql password.");
            System.exit(1);
        }
        ListDevice();
        System.out.printf("The edition of Windows you are using is: %s%n", getEdition());
    }

    public static void ListDevice() throws SQLException {

        //[START doc-example]
        String jdbcUrl = String.format(
                "jdbc:mysql://google/%s?cloudSqlInstance=%s"
                        + "&socketFactory=com.google.cloud.sql.mysql.SocketFactory&useSSL=false",
                databaseName,
                instanceConnectionName);

        Connection connection = DriverManager.getConnection(jdbcUrl, username, password);
        //[END doc-example]

        try (Statement statement = connection.createStatement()) {
//            ResultSet resultSet = statement.executeQuery("SELECT * FROM entries");
            ResultSet resultSet = statement.executeQuery("SELECT versionNumber FROM entries");
            while (resultSet.next()) {
                System.out.println(resultSet.getString(1));
            }
//            ResultSet resultSet2 = statement.executeQuery("SELECT * FROM entries");
//            while (resultSet2.next()) {
//                System.out.println(resultSet.getString(1));
//            }
        }
    }


    public static String findSysInfo(String term) {
        try {
            Runtime rt = Runtime.getRuntime();
            Process pr = rt.exec("CMD /C SYSTEMINFO | FINDSTR /B /C:\"" + term + "\"");
            BufferedReader in = new BufferedReader(new InputStreamReader(pr.getInputStream()));
            return in.readLine();
        } catch (IOException e) {
            System.err.println(e.getMessage());
        }
        return "";
    }

//    public static String findSysInfo2(String term) {
//        try {
//            Runtime rt = Runtime.getRuntime();
//            Process pr = rt.exec("systeminfo | findstr /B /C:\"OS Name\" /C:\"OS Version\"");
//            BufferedReader in = new BufferedReader(new InputStreamReader(pr.getInputStream()));
//            return in.readLine();
//        } catch (IOException e) {
//            System.err.println(e.getMessage());
//        }
//        return "";
//    }

    public static String getEdition() {
        String osName = findSysInfo("OS Version:");
//        if (!osName.isEmpty()) {
//            for (String edition : EDITIONS) {
//                if (osName.contains(edition)) {
//                    return edition;
//                }
//            }
//        }
        Scanner s = new Scanner(osName).useDelimiter("                ");
        String firstLine=s.next();
        String osBuildNoStr=s.next();
        //System.out.println("OS version is " + osName);
        Scanner sc = new Scanner(osBuildNoStr).useDelimiter(" ");
        String osBuildNo=sc.next();
        System.out.println(osBuildNo);
        return osBuildNo;
    }
}