package Model;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class User_InfoDB {

    public void DELETEUSER(String email) throws SQLException {
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
}