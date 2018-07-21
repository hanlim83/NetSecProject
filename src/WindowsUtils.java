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

    //TODO Migrate this to the otherDB
    public void CheckSupportedVersion() throws SQLException {
        // TODO: fill this in
        // The instance connection name can be obtained from the instance overview page in Cloud Console
        // or by running "gcloud sql instances describe <instance> | grep connectionName".
        String instanceConnectionName = "netsecpj:us-central1:nspj-project";

        // TODO: fill this in
        // The database from which to list tables.
        String databaseName = "device_build_number";

        String username = "root";

        // TODO: fill this in
        // This is the password that was set via the Cloud Console or empty if never set
        // (not recommended).
        String password = "root";

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

        Connection connection = DriverManager.getConnection(jdbcUrl, username, password);
        //same
        System.out.println(jdbcUrl);

        //check this
        //Connection connection = DriverManager.getConnection(jdbcUrl, username, password);
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

    public void setUserKeyInfo(String hashPassword, String publicKey, String encryptedPrivateKey,String email) throws SQLException {
        //maybe change to boolean next time
        // TODO: fill this in
        // The instance connection name can be obtained from the instance overview page in Cloud Console
        // or by running "gcloud sql instances describe <instance> | grep connectionName".
        String instanceConnectionName = "netsecpj:us-central1:nspj-project";

        // TODO: fill this in
        // The database from which to list tables.
        String databaseName = "user_info";

        String username = "root";

        // TODO: fill this in
        // This is the password that was set via the Cloud Console or empty if never set
        // (not recommended).
        String password = "root";

        String state = "";
        if (instanceConnectionName.equals("user_info")) {
            System.err.println("Please update the sample to specify the instance connection name.");
            System.exit(1);
        }

        if (password.equals("<insert_password>")) {
            System.err.println("Please update the sample to specify the mysql password.");
            System.exit(1);
        }

        //[START doc-example]
        String jdbcUrl = String.format(
                "jdbc:mysql://google/%s?cloudSqlInstance=%s"
                        + "&socketFactory=com.google.cloud.sql.mysql.SocketFactory&useSSL=false",
                databaseName,
                instanceConnectionName);

        Connection connection = DriverManager.getConnection(jdbcUrl, username, password);
        //same
        System.out.println(jdbcUrl);

        //check this
        //Connection connection = DriverManager.getConnection(jdbcUrl, username, password);
        //[END doc-example]

        //Here no need to return any result so how?
        try (Statement statement = connection.createStatement()) {
//            ResultSet resultSet = statement.executeQuery("SELECT * FROM entries");
//            ResultSet resultSet = statement.executeQuery("UPDATE entries SET status='Active', hashPassword='"+hashPassword+"', publicKey='"+publicKey+"', privateLKey='"+privateKey+"' WHERE email='"+email+"'");
//            while (resultSet.next()) {
//                //System.out.println(resultSet.getString(1));
//                state=resultSet.getString(1);
//            }
            statement.executeUpdate("UPDATE entries SET status='Active', hashPassword='"+hashPassword+"', publicKey='"+publicKey+"', privateKey='"+encryptedPrivateKey+"' WHERE email='"+email+"'");
        }

    }

    public ResultSet handleSQLCommands(String dbName) throws SQLException {
        ResultSet resultSet;
        ResultSet resultSet2;
        // TODO: fill this in
        // The instance connection name can be obtained from the instance overview page in Cloud Console
        // or by running "gcloud sql instances describe <instance> | grep connectionName".
        String instanceConnectionName = "netsecpj:us-central1:nspj-project";

        // TODO: fill this in
        // The database from which to list tables.
        String databaseName = dbName;

        String username = "root";

        // TODO: fill this in
        // This is the password that was set via the Cloud Console or empty if never set
        // (not recommended).
        String password = "root";

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

        Connection connection = DriverManager.getConnection(jdbcUrl, username, password);
        //same
        System.out.println(jdbcUrl);

        try (Statement statement = connection.createStatement()) {
//            ResultSet resultSet = statement.executeQuery("SELECT * FROM entries");
            resultSet = statement.executeQuery("SELECT versionNumber FROM entries");
            //Check if can return whole result set
            while (resultSet.next()) {
                //System.out.println(resultSet.getString(1));
//                SupportedVersions.add(resultSet.getString(1));
//                resultSet2=resultSet.getString(1);
            }
            //Check if can return whole result set
        }
        return resultSet;
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