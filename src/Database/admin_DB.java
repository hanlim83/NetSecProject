package Database;

import java.sql.*;
import java.util.ArrayList;

public class admin_DB {
    public ArrayList<Admin> getAdminList(){
        ArrayList<Admin> AdminList = new ArrayList<Admin>();

        Connection connection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;

        try {
            connection = DataSource.getInstance().getConnection();

            preparedStatement=connection.prepareStatement("SELECT * FROM entriesAdmin");

            resultSet=preparedStatement.executeQuery();
            while (resultSet.next()) {
                Admin admin=new Admin();
                admin.setEmail(resultSet.getString("email"));
                admin.setPhoneNo(resultSet.getString("phoneNumber"));
                admin.setEntryID(resultSet.getString("entryID"));
                AdminList.add(admin);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            if (resultSet != null) try { resultSet.close(); } catch (SQLException e) {e.printStackTrace();}
            if (preparedStatement != null) try { preparedStatement.close(); } catch (SQLException e) {e.printStackTrace();}
            if (connection != null) try { connection.close(); } catch (SQLException e) {e.printStackTrace();}
        }

        return AdminList;
    }

    public void createAdmin(String email,String phoneNo){
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;

        try {
            connection = DataSource.getInstance().getConnection();

            preparedStatement=connection.prepareStatement("INSERT INTO entriesAdmin (email,phoneNumber) VALUES (?,?)");
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
    }

    public void deleteAdmin(String email,String phoneNo){
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;

        try {
            connection = DataSource.getInstance().getConnection();

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
    }

    public String getAdminAccStatus(String email) throws SQLException {
        String state = "";

        Connection connection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;

        try {
            connection = DataSource.getInstance().getConnection();

            preparedStatement=connection.prepareStatement("SELECT EXISTS(SELECT * FROM entriesAdmin WHERE email=?)");
            preparedStatement.setString(1,email);

            resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                state=resultSet.getString(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            if (resultSet != null) try { resultSet.close(); } catch (SQLException e) {e.printStackTrace();}
            if (preparedStatement != null) try { preparedStatement.close(); } catch (SQLException e) {e.printStackTrace();}
            if (connection != null) try { connection.close(); } catch (SQLException e) {e.printStackTrace();}
        }
        System.out.println(state);
        return state;
    }

    public ArrayList<String> getAllPhoneNo(){
        ArrayList<String> phoneNoList=new ArrayList<String>();

        Connection connection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;

        try {
            connection = DataSource.getInstance().getConnection();

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

        return phoneNoList;
    }

    public String getPhoneNo(String email){
        String phoneNo = null;

        Connection connection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;

        try {
            connection = DataSource.getInstance().getConnection();

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

        return phoneNo;
    }

    public ArrayList<String> getAllEmail(){
        ArrayList<String> emailList=new ArrayList<String>();

        Connection connection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;

        try {
            connection = DataSource.getInstance().getConnection();

            preparedStatement=connection.prepareStatement("SELECT email FROM entriesAdmin");

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

        return emailList;
    }

    public void setLastLoginTime(String date, String email){
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;

        try {
            connection = DataSource.getInstance().getConnection();

            preparedStatement=connection.prepareStatement("UPDATE entriesAdmin SET lastLogin=? WHERE email=?");
            preparedStatement.setString(1,date);
            preparedStatement.setString(2,email);

            preparedStatement.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            if (resultSet != null) try { resultSet.close(); } catch (SQLException e) {e.printStackTrace();}
            if (preparedStatement != null) try { preparedStatement.close(); } catch (SQLException e) {e.printStackTrace();}
            if (connection != null) try { connection.close(); } catch (SQLException e) {e.printStackTrace();}
        }
    }

    public String getLastLoginTime(String email){
        String date = null;

        Connection connection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;

        try {
            connection = DataSource.getInstance().getConnection();

            preparedStatement=connection.prepareStatement("SELECT lastLogin FROM entriesAdmin WHERE email=?");
            preparedStatement.setString(1,email);

            resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                date=resultSet.getString(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            if (resultSet != null) try { resultSet.close(); } catch (SQLException e) {e.printStackTrace();}
            if (preparedStatement != null) try { preparedStatement.close(); } catch (SQLException e) {e.printStackTrace();}
            if (connection != null) try { connection.close(); } catch (SQLException e) {e.printStackTrace();}
        }
        return date;
    }
}
