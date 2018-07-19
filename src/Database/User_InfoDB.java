package Database;

import java.sql.*;
import java.util.ArrayList;

public class User_InfoDB {
    private static String instanceConnectionName = "netsecpj:us-central1:nspj-project";
    private static String databaseName = "user_info";
    private static String username = "root";
    private static String password = "root";

    public ArrayList<User> getUserList() throws SQLException {
        ArrayList<User> UserList = new ArrayList<User>();

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
                User user= new User();
                user.setEmail(resultSet.getString("email"));
                user.setStatus(resultSet.getString("status"));
                user.setHashPassword(resultSet.getString("hashPassword"));
                user.setPublicKey(resultSet.getString("publicKey"));
                user.setPrivateKey(resultSet.getString("privateKey"));
                user.setPhoneNo(resultSet.getString("phoneNo"));
                user.setEntryID(resultSet.getString("entryID"));
                UserList.add(user);
            }
        }
        return UserList;
    }

    public String getAccStatus(String email) throws SQLException {
//        String instanceConnectionName = "netsecpj:us-central1:nspj-project";
//        String databaseName = "user_info";
//        String username = "root";
//        String password = "root";

        String state = "";
        //[START doc-example]
        String jdbcUrl = String.format(
                "jdbc:mysql://google/%s?cloudSqlInstance=%s"
                        + "&socketFactory=com.google.cloud.sql.mysql.SocketFactory&useSSL=false",
                databaseName,
                instanceConnectionName);

        Connection connection = DriverManager.getConnection(jdbcUrl, username, password);
        //[END doc-example]

        PreparedStatement preparedStatement=connection.prepareStatement("SELECT status FROM entries WHERE email=?");
        preparedStatement.setString(1,email);

        try (Statement statement = connection.createStatement()) {
//            ResultSet resultSet = statement.executeQuery("SELECT * FROM entries");
//            ResultSet resultSet = statement.executeQuery("SELECT status FROM entries WHERE email='"+email+"'");
            ResultSet resultSet=preparedStatement.executeQuery();
            while (resultSet.next()) {
                //System.out.println(resultSet.getString(1));
                state=resultSet.getString(1);
            }
        }
        System.out.println(state);
        return state;
    }

    public void setUserKeyInfo(String hashPassword, String publicKey, String encryptedPrivateKey,String phoneNo,String email) throws SQLException {
        //maybe change to boolean next time

        String instanceConnectionName = "netsecpj:us-central1:nspj-project";
        String databaseName = "user_info";
        String username = "root";
        String password = "root";
        String state = "";
//        if (instanceConnectionName.equals("user_info")) {
//            System.err.println("Please update the sample to specify the instance connection name.");
//            System.exit(1);
//        }
//
//        if (password.equals("<insert_password>")) {
//            System.err.println("Please update the sample to specify the mysql password.");
//            System.exit(1);
//        }

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

        //Here no need to return any result so how?
        try (Statement statement = connection.createStatement()) {
//            ResultSet resultSet = statement.executeQuery("SELECT * FROM entries");
//            ResultSet resultSet = statement.executeQuery("UPDATE entries SET status='Active', hashPassword='"+hashPassword+"', publicKey='"+publicKey+"', privateLKey='"+privateKey+"' WHERE email='"+email+"'");
//            while (resultSet.next()) {
//                //System.out.println(resultSet.getString(1));
//                state=resultSet.getString(1);
//            }
            statement.executeUpdate("UPDATE entries SET status='Active', hashPassword='"+hashPassword+"', publicKey='"+publicKey+"', privateKey='"+encryptedPrivateKey+"', phoneNo='"+phoneNo+"' WHERE email='"+email+"'");
        }
    }

    public void deleteUser(String email) throws SQLException {
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

//        if (instanceConnectionName.equals("user_info")) {
//            System.err.println("Please update the sample to specify the instance connection name.");
//            System.exit(1);
//        }
//
//        if (password.equals("<insert_password>")) {
//            System.err.println("Please update the sample to specify the mysql password.");
//            System.exit(1);
//        }

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
            statement.executeUpdate("DELETE FROM entries WHERE email='" + email + "'");
        }
    }

    public void createUser(String email) throws SQLException {
        String instanceConnectionName = "netsecpj:us-central1:nspj-project";
        String databaseName = "user_info";
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

        //Here no need to return any result so how?
        try (Statement statement = connection.createStatement()) {
//            ResultSet resultSet = statement.executeQuery("SELECT * FROM entries");
//            ResultSet resultSet = statement.executeQuery("UPDATE entries SET status='Active', hashPassword='"+hashPassword+"', publicKey='"+publicKey+"', privateLKey='"+privateKey+"' WHERE email='"+email+"'");
//            while (resultSet.next()) {
//                //System.out.println(resultSet.getString(1));
//                state=resultSet.getString(1);
//            }
            statement.executeUpdate("INSERT INTO entries (email,status) values ('"+email+"','Inactive')");
        }
    }

    public String getPhoneNumber(String email) throws SQLException {
        String phoneNo = null;
        String instanceConnectionName = "netsecpj:us-central1:nspj-project";
        String databaseName = "user_info";
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
            ResultSet resultSet = statement.executeQuery("SELECT phoneNo FROM entries WHERE email='"+email+"'");
            while (resultSet.next()) {
                phoneNo=resultSet.getString(1);
            }
        }
        return phoneNo;
    }

    public void setPhoneNo(String phoneNo, String email) throws SQLException {
        String instanceConnectionName = "netsecpj:us-central1:nspj-project";
        String databaseName = "user_info";
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
            statement.executeUpdate("UPDATE entries SET phoneNo='"+phoneNo+"' WHERE email='"+email+"'");
        }
    }
}
