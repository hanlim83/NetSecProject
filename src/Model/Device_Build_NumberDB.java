package Model;

import java.sql.*;
import java.util.ArrayList;

public class Device_Build_NumberDB {
    public ArrayList<OSVersion> CheckSupportedVersion() throws SQLException {
        ArrayList<OSVersion> OSVersionList = new ArrayList<OSVersion>();

        String instanceConnectionName = "netsecpj:us-central1:nspj-project";
        String databaseName = "device_build_number";
        String username = "root";
        String password = "root";

//            if (instanceConnectionName.equals("<device-supported-versions>")) {
//                System.err.println("Please update the sample to specify the instance connection name.");
//                System.exit(1);
//            }
//
//            if (password.equals("<insert_password>")) {
//                System.err.println("Please update the sample to specify the mysql password.");
//                System.exit(1);
//            }

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
            ResultSet resultSet = statement.executeQuery("SELECT * FROM entries");
            while (resultSet.next()) {
                OSVersion osVersion = new OSVersion();
                osVersion.setVersionName(resultSet.getString("versionName"));
                osVersion.setVersionNumber(resultSet.getString("versionNumber"));
                osVersion.setEntryID(resultSet.getInt("entryID"));
                OSVersionList.add(osVersion);
//                    SupportedVersions.add(resultSet.getString(1));
            }
        }
        return OSVersionList;
    }

    public void insertNewOSVersion(String versionName, String versionNumber) throws SQLException {
        String instanceConnectionName = "netsecpj:us-central1:nspj-project";
        String databaseName = "device_build_number";
        String username = "root";
        String password = "root";

//            if (instanceConnectionName.equals("<device-supported-versions>")) {
//                System.err.println("Please update the sample to specify the instance connection name.");
//                System.exit(1);
//            }
//
//            if (password.equals("<insert_password>")) {
//                System.err.println("Please update the sample to specify the mysql password.");
//                System.exit(1);
//            }

        //[START doc-example]
        String jdbcUrl = String.format(
                "jdbc:mysql://google/%s?cloudSqlInstance=%s"
                        + "&socketFactory=com.google.cloud.sql.mysql.SocketFactory&useSSL=false",
                databaseName,
                instanceConnectionName);

        Connection connection = DriverManager.getConnection(jdbcUrl, username, password);
        //[END doc-example]

        try (Statement statement = connection.createStatement()) {
            statement.executeUpdate("INSERT INTO entries (versionName, versionNumber) VALUES ('" + versionName + "','" + versionNumber + "')");
//            statement.executeUpdate("INSERT INTO entries (versionName, versionNumber) VALUES ('JAVA','JAVATEST')");
        }
    }

    //TODO FINISH THIS
    //Tested and Working
    public void deleteOSVersion(String entryID) throws SQLException {
        //Test out whether needs to be int or String also can
        String instanceConnectionName = "netsecpj:us-central1:nspj-project";
        String databaseName = "device_build_number";
        String username = "root";
        String password = "root";

        //[START doc-example]
        String jdbcUrl = String.format(
                "jdbc:mysql://google/%s?cloudSqlInstance=%s"
                        + "&socketFactory=com.google.cloud.sql.mysql.SocketFactory&useSSL=false",
                databaseName,
                instanceConnectionName);

        Connection connection = DriverManager.getConnection(jdbcUrl, username, password);
        //[END doc-example]

        try (Statement statement = connection.createStatement()) {
            statement.executeUpdate("DELETE FROM entries WHERE entryID=" + entryID);
        }
    }
}
