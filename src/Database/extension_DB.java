package Database;

import java.sql.*;
import java.util.ArrayList;

public class extension_DB {
    private static String instanceConnectionName = "netsecpj:us-central1:nspj-project";
    private static String databaseName = "extension_DB";
    private static String username = "root";
    private static String password = "root";

    public ArrayList<String> getExtensionList() throws SQLException {
        ArrayList<String> ExtensionList = new ArrayList<String>();
        //[START doc-example]
        String jdbcUrl = String.format(
                "jdbc:mysql://google/%s?cloudSqlInstance=%s"
                        + "&socketFactory=com.google.cloud.sql.mysql.SocketFactory&useSSL=false",
                databaseName,
                instanceConnectionName);

        Connection connection = DriverManager.getConnection(jdbcUrl, username, password);
        //[END doc-example]

        try (Statement statement = connection.createStatement()) {
            ResultSet resultSet = statement.executeQuery("SELECT * FROM entries");
            while (resultSet.next()) {
                ExtensionList.add(resultSet.getString(1));
            }
        }
        return ExtensionList;
    }
}
