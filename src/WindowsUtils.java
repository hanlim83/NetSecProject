import java.io.*;
import java.sql.*;
import java.util.ArrayList;
import java.util.Scanner;

public class WindowsUtils implements Runnable{
//    private static final String[] EDITIONS = {
//            "Basic", "Home", "Professional", "Enterprise"
//    };

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

    public String getEdition() {
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

    private ArrayList<String> SupportedVersions = new ArrayList<String>();
    String currentOSVersion;
    public boolean checkWindowsApproved() throws SQLException {
        boolean windowsApproved = false;
        String currentOSVersion=getEdition();
        CheckSupportedVersion();
//        run();
        for(String s:SupportedVersions) {
            if (s.equals(currentOSVersion)) {
                System.out.println("Current Version: "+currentOSVersion+" ArrList: "+s);
                windowsApproved=true;
                break;
            } else {
                System.out.println(s);
                windowsApproved=false;
            }
        }
        return windowsApproved;
    }

    // TODO: fill this in
    // The instance connection name can be obtained from the instance overview page in Cloud Console
    // or by running "gcloud sql instances describe <instance> | grep connectionName".
    static String instanceConnectionName = "netsecpj:us-central1:device-supported-versions";

    // TODO: fill this in
    // The database from which to list tables.
    static String databaseName = "device_build_number";

    static String username = "root";

    // TODO: fill this in
    // This is the password that was set via the Cloud Console or empty if never set
    // (not recommended).
    static String password = "root";



    public void CheckSupportedVersion() throws SQLException {
        if (instanceConnectionName.equals("<device-supported-versions>")) {
            System.err.println("Please update the sample to specify the instance connection name.");
            System.exit(1);
        }

        if (password.equals("<insert_password>")) {
            System.err.println("Please update the sample to specify the mysql password.");
            System.exit(1);
        }
        System.out.printf("The edition of Windows you are using is: %s%n", getEdition());

        //[START doc-example]
        String jdbcUrl = String.format(
                "jdbc:mysql://google/%s?cloudSqlInstance=%s"
                        + "&socketFactory=com.google.cloud.sql.mysql.SocketFactory&useSSL=false",
                databaseName,
                instanceConnectionName);
        //same
        System.out.println(jdbcUrl);

        //check this
        //Connection connection = DriverManager.getConnection(jdbcUrl, username, password);
        Connection connection = DriverManager.getConnection(jdbcUrl);
        //[END doc-example]

        try (Statement statement = connection.createStatement()) {
//            ResultSet resultSet = statement.executeQuery("SELECT * FROM entries");
            ResultSet resultSet = statement.executeQuery("SELECT versionNumber FROM entries");
            while (resultSet.next()) {
                //System.out.println(resultSet.getString(1));
                SupportedVersions.add(resultSet.getString(1));
            }
        }
    }

    @Override
    public void run() {
        currentOSVersion=getEdition();
        try {
            CheckSupportedVersion();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}