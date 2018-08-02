package Database;

import java.sql.*;
import java.util.ArrayList;

public class admin_DB {
    //TODO check if migration is successful
    private static String instanceConnectionName = "netsecpj:us-central1:nspj-project";
    private static String databaseName = "admin_DB";
    private static String username = "root";
    private static String password = "root";

    public ArrayList<Admin> getAdminList() throws SQLException {
        ArrayList<Admin> AdminList = new ArrayList<Admin>();

        String jdbcUrl = String.format(
                "jdbc:mysql://google/%s?cloudSqlInstance=%s"
                        + "&socketFactory=com.google.cloud.sql.mysql.SocketFactory&useSSL=false",
                databaseName,
                instanceConnectionName);

        Connection connection = DriverManager.getConnection(jdbcUrl, username, password);

        PreparedStatement preparedStatement=connection.prepareStatement("SELECT * FROM entriesAdmin");

        try (Statement statement = connection.createStatement()) {
//            ResultSet resultSet = statement.executeQuery("SELECT * FROM entries");
            ResultSet resultSet=preparedStatement.executeQuery();
            while (resultSet.next()) {
                Admin admin=new Admin();
                admin.setEmail(resultSet.getString("email"));
                admin.setPhoneNo(resultSet.getString("phoneNumber"));
                admin.setEntryID(resultSet.getString("entryID"));
                AdminList.add(admin);
            }
        }
        return AdminList;
    }

    public void createAdmin(String email,String phoneNo) throws SQLException {
        String jdbcUrl = String.format(
                "jdbc:mysql://google/%s?cloudSqlInstance=%s"
                        + "&socketFactory=com.google.cloud.sql.mysql.SocketFactory&useSSL=false",
                databaseName,
                instanceConnectionName);

        Connection connection = DriverManager.getConnection(jdbcUrl, username, password);

        PreparedStatement preparedStatement=connection.prepareStatement("INSERT INTO entriesAdmin (email,phoneNumber) VALUES (?,?)");
        preparedStatement.setString(1,email);
        preparedStatement.setString(2,phoneNo);

        try (Statement statement = connection.createStatement()) {
//            statement.executeUpdate("INSERT INTO entries (email,phoneNumber) VALUES ('"+email+"','"+phoneNo+"')");
            preparedStatement.executeUpdate();
        }
    }

    public void deleteAdmin(String email,String phoneNo) throws SQLException {
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
//            statement = connection.createStatement();

            preparedStatement=connection.prepareStatement("DELETE FROM entriesAdmin WHERE email=? AND phoneNumber=?");
            preparedStatement.setString(1,email);
            preparedStatement.setString(2,phoneNo);

            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            if (resultSet != null) try { resultSet.close(); } catch (SQLException e) {e.printStackTrace();}
            if (preparedStatement != null) try { preparedStatement.close(); } catch (SQLException e) {e.printStackTrace();}
            if (connection != null) try { connection.close(); } catch (SQLException e) {e.printStackTrace();}
        }

//        try (Statement statement = connection.createStatement()) {
////            statement.executeUpdate("DELETE FROM entries WHERE email='"+email+"' AND phoneNumber='"+phoneNo+"'");
//        }
    }

    public String getAdminAccStatus(String email) throws SQLException {
        String state = "";

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
//            statement = connection.createStatement();

            preparedStatement=connection.prepareStatement("SELECT EXISTS(SELECT * FROM entriesAdmin WHERE email=?)");
            preparedStatement.setString(1,email);

            resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                //System.out.println(resultSet.getString(1));
                state=resultSet.getString(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            if (resultSet != null) try { resultSet.close(); } catch (SQLException e) {e.printStackTrace();}
            if (preparedStatement != null) try { preparedStatement.close(); } catch (SQLException e) {e.printStackTrace();}
            if (connection != null) try { connection.close(); } catch (SQLException e) {e.printStackTrace();}
        }

//        try (Statement statement = connection.createStatement()) {
////            ResultSet resultSet = statement.executeQuery("SELECT EXISTS(SELECT * FROM entries WHERE email = '"+email+"')");
//
//        }
//        if (state==)
        System.out.println(state);
        return state;
    }

    public ArrayList<String> getAllPhoneNo() throws SQLException {
        ArrayList<String> phoneNoList=new ArrayList<String>();

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
//            statement = connection.createStatement();

            preparedStatement=connection.prepareStatement("SELECT phoneNumber FROM entriesAdmin");


            resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                phoneNoList.add(resultSet.getString(1));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            if (resultSet != null) try { resultSet.close(); } catch (SQLException e) {e.printStackTrace();}
            if (preparedStatement != null) try { preparedStatement.close(); } catch (SQLException e) {e.printStackTrace();}
            if (connection != null) try { connection.close(); } catch (SQLException e) {e.printStackTrace();}
        }


//        try (Statement statement = connection.createStatement()) {
////            ResultSet resultSet = statement.executeQuery("SELECT phoneNumber FROM entries");
//
//        }
        return phoneNoList;
    }

    public String getPhoneNo(String email) throws SQLException {
        String phoneNo = null;

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
//            statement = connection.createStatement();

            preparedStatement=connection.prepareStatement("SELECT phoneNumber FROM entriesAdmin WHERE email=?");
            preparedStatement.setString(1,email);


            resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                phoneNo=resultSet.getString(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            if (resultSet != null) try { resultSet.close(); } catch (SQLException e) {e.printStackTrace();}
            if (preparedStatement != null) try { preparedStatement.close(); } catch (SQLException e) {e.printStackTrace();}
            if (connection != null) try { connection.close(); } catch (SQLException e) {e.printStackTrace();}
        }

//        try (Statement statement = connection.createStatement()) {
////            ResultSet resultSet = statement.executeQuery("SELECT phoneNumber FROM entries WHERE email='"+email+"'");
//        }
        return phoneNo;
    }

    public ArrayList<String> getAllEmail() throws SQLException {
        ArrayList<String> emailList=new ArrayList<String>();

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
//            statement = connection.createStatement();

            preparedStatement=connection.prepareStatement("SELECT email FROM entries");

            resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                emailList.add(resultSet.getString(1));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            if (resultSet != null) try { resultSet.close(); } catch (SQLException e) {e.printStackTrace();}
            if (preparedStatement != null) try { preparedStatement.close(); } catch (SQLException e) {e.printStackTrace();}
            if (connection != null) try { connection.close(); } catch (SQLException e) {e.printStackTrace();}
        }

//        try (Statement statement = connection.createStatement()) {
////            ResultSet resultSet = statement.executeQuery("SELECT phoneNumber FROM entries");
//
//        }
        return emailList;
    }
}
