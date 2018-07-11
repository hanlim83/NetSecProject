package Database;

import java.sql.*;

public class admin_DB {
    public String getAdminAccStatus(String email) throws SQLException {
        String instanceConnectionName = "netsecpj:us-central1:nspj-project";
        String databaseName = "admin_DB";
        String username = "root";
        String password = "root";

        String state = "";

        //[START doc-example]
        String jdbcUrl = String.format(
                "jdbc:mysql://google/%s?cloudSqlInstance=%s"
                        + "&socketFactory=com.google.cloud.sql.mysql.SocketFactory&useSSL=false",
                databaseName,
                instanceConnectionName);

        Connection connection = DriverManager.getConnection(jdbcUrl, username, password);
        //same
        System.out.println(jdbcUrl);
        //[END doc-example]

        try (Statement statement = connection.createStatement()) {
//            ResultSet resultSet = statement.executeQuery("SELECT * FROM entries");
            ResultSet resultSet = statement.executeQuery("SELECT EXISTS(SELECT * FROM entries WHERE email = '"+email+"')");
            while (resultSet.next()) {
                //System.out.println(resultSet.getString(1));
                state=resultSet.getString(1);
            }
        }
//        if (state==)
        System.out.println(state);
        return state;
    }

    public String getPhoneNo(String email) throws SQLException {
        String phoneNo = null;
        String instanceConnectionName = "netsecpj:us-central1:nspj-project";
        String databaseName = "admin_DB";
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
//            ResultSet resultSet = statement.executeQuery("SELECT * FROM entries");
            ResultSet resultSet = statement.executeQuery("SELECT phoneNumber FROM entries WHERE email='"+email+"'");
            while (resultSet.next()) {
                phoneNo=resultSet.getString(1);
            }
        }
        return phoneNo;
    }
}
