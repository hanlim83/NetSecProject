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
        String jdbcUrl = String.format(
                "jdbc:mysql://google/%s?cloudSqlInstance=%s"
                        + "&socketFactory=com.google.cloud.sql.mysql.SocketFactory&useSSL=false",
                databaseName,
                instanceConnectionName);

        Connection connection = DriverManager.getConnection(jdbcUrl, username, password);

        PreparedStatement preparedStatement=connection.prepareStatement("SELECT * FROM entries");

        try (Statement statement = connection.createStatement()) {
//            ResultSet resultSet = statement.executeQuery("SELECT * FROM entries");
            ResultSet resultSet=preparedStatement.executeQuery();
            while (resultSet.next()) {
                ExtensionList.add(resultSet.getString(1));
            }
        }
        return ExtensionList;
    }

    public void createExtension(String extension) throws SQLException {
        String jdbcUrl = String.format(
                "jdbc:mysql://google/%s?cloudSqlInstance=%s"
                        + "&socketFactory=com.google.cloud.sql.mysql.SocketFactory&useSSL=false",
                databaseName,
                instanceConnectionName);

        Connection connection = DriverManager.getConnection(jdbcUrl, username, password);

        PreparedStatement preparedStatement=connection.prepareStatement("INSERT INTO entries (extensions) VALUES (?)");
        preparedStatement.setString(1,extension);

        try (Statement statement = connection.createStatement()) {
//            statement.executeUpdate("INSERT INTO entries (extensions) VALUES ('"+extension+"')");
            preparedStatement.executeUpdate();
        }
    }

    public void deleteExtension(String extension) throws SQLException {
        String jdbcUrl = String.format(
                "jdbc:mysql://google/%s?cloudSqlInstance=%s"
                        + "&socketFactory=com.google.cloud.sql.mysql.SocketFactory&useSSL=false",
                databaseName,
                instanceConnectionName);

        Connection connection = DriverManager.getConnection(jdbcUrl, username, password);

        PreparedStatement preparedStatement=connection.prepareStatement("DELETE FROM entries WHERE extensions=?");
        preparedStatement.setString(1,extension);

        try (Statement statement = connection.createStatement()) {
//            statement.executeUpdate("DELETE FROM entries WHERE extensions='"+extension+"'");
            preparedStatement.executeUpdate();
        }
    }
}
