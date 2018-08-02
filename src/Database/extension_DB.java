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
//        String jdbcUrl = String.format(
//                "jdbc:mysql://google/%s?cloudSqlInstance=%s"
//                        + "&socketFactory=com.google.cloud.sql.mysql.SocketFactory&useSSL=false",
//                databaseName,
//                instanceConnectionName);
//
//        Connection connection = DriverManager.getConnection(jdbcUrl, username, password);

        Connection connection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;

        try {
            connection = DataSource.getInstance().getConnection();

            preparedStatement=connection.prepareStatement("SELECT * FROM entriesExtension");

            resultSet=preparedStatement.executeQuery();
            while (resultSet.next()) {
                ExtensionList.add(resultSet.getString(1));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            if (resultSet != null) try { resultSet.close(); } catch (SQLException e) {e.printStackTrace();}
            if (preparedStatement != null) try { preparedStatement.close(); } catch (SQLException e) {e.printStackTrace();}
            if (connection != null) try { connection.close(); } catch (SQLException e) {e.printStackTrace();}
        }

//        try (Statement statement = connection.createStatement()) {
////            ResultSet resultSet = statement.executeQuery("SELECT * FROM entries");
//        }
        return ExtensionList;
    }

    public void createExtension(String extension) throws SQLException {
//        String jdbcUrl = String.format(
//                "jdbc:mysql://google/%s?cloudSqlInstance=%s"
//                        + "&socketFactory=com.google.cloud.sql.mysql.SocketFactory&useSSL=false",
//                databaseName,
//                instanceConnectionName);
//
//        Connection connection = DriverManager.getConnection(jdbcUrl, username, password);

        Connection connection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;

        try {
            preparedStatement=connection.prepareStatement("INSERT INTO entriesExtension (extensions) VALUES (?)");
            preparedStatement.setString(1,extension);

            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            if (resultSet != null) try { resultSet.close(); } catch (SQLException e) {e.printStackTrace();}
            if (preparedStatement != null) try { preparedStatement.close(); } catch (SQLException e) {e.printStackTrace();}
            if (connection != null) try { connection.close(); } catch (SQLException e) {e.printStackTrace();}
        }

//        try (Statement statement = connection.createStatement()) {
////            statement.executeUpdate("INSERT INTO entries (extensions) VALUES ('"+extension+"')");
//        }
    }

    public void deleteExtension(String extension) throws SQLException {
//        String jdbcUrl = String.format(
//                "jdbc:mysql://google/%s?cloudSqlInstance=%s"
//                        + "&socketFactory=com.google.cloud.sql.mysql.SocketFactory&useSSL=false",
//                databaseName,
//                instanceConnectionName);
//
//        Connection connection = DriverManager.getConnection(jdbcUrl, username, password);

        Connection connection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;

        try {
            preparedStatement=connection.prepareStatement("DELETE FROM entriesExtension WHERE extensions=?");
            preparedStatement.setString(1,extension);

            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            if (resultSet != null) try { resultSet.close(); } catch (SQLException e) {e.printStackTrace();}
            if (preparedStatement != null) try { preparedStatement.close(); } catch (SQLException e) {e.printStackTrace();}
            if (connection != null) try { connection.close(); } catch (SQLException e) {e.printStackTrace();}
        }

//        try (Statement statement = connection.createStatement()) {
////            statement.executeUpdate("DELETE FROM entries WHERE extensions='"+extension+"'");
//            preparedStatement.executeUpdate();
//        }
    }
}
