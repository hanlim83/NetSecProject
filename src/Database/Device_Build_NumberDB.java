package Database;

import java.sql.*;
import java.util.ArrayList;

public class Device_Build_NumberDB {
    //Stable
    public ArrayList<OSVersion> CheckSupportedVersion(){
        ArrayList<OSVersion> OSVersionList = new ArrayList<OSVersion>();

        Connection connection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;

        try {
            connection = DataSource.getInstance().getConnection();

            preparedStatement=connection.prepareStatement("SELECT * FROM entriesDeviceSupportedVersion");

            resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                OSVersion osVersion = new OSVersion();
                osVersion.setVersionName(resultSet.getString("versionName"));
                osVersion.setVersionNumber(resultSet.getString("versionNumber"));
                osVersion.setEntryID(resultSet.getInt("entryID"));
                OSVersionList.add(osVersion);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            if (resultSet != null) try { resultSet.close(); } catch (SQLException e) {e.printStackTrace();}
            if (preparedStatement != null) try { preparedStatement.close(); } catch (SQLException e) {e.printStackTrace();}
            if (connection != null) try { connection.close(); } catch (SQLException e) {e.printStackTrace();}
        }

        return OSVersionList;
    }

    public void insertNewOSVersion(String versionName, String versionNumber){
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;

        try {
            connection = DataSource.getInstance().getConnection();

            preparedStatement=connection.prepareStatement("INSERT INTO entriesDeviceSupportedVersion (versionName, versionNumber) VALUES (?,?)");
            preparedStatement.setString(1,versionName);
            preparedStatement.setString(2,versionNumber);

            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            if (resultSet != null) try { resultSet.close(); } catch (SQLException e) {e.printStackTrace();}
            if (preparedStatement != null) try { preparedStatement.close(); } catch (SQLException e) {e.printStackTrace();}
            if (connection != null) try { connection.close(); } catch (SQLException e) {e.printStackTrace();}
        }
    }
    
    public void deleteOSVersion(String entryID){
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;

        try {
            connection = DataSource.getInstance().getConnection();

            preparedStatement=connection.prepareStatement("DELETE FROM entriesDeviceSupportedVersion WHERE entryID=?");
            preparedStatement.setString(1,entryID);
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            if (resultSet != null) try { resultSet.close(); } catch (SQLException e) {e.printStackTrace();}
            if (preparedStatement != null) try { preparedStatement.close(); } catch (SQLException e) {e.printStackTrace();}
            if (connection != null) try { connection.close(); } catch (SQLException e) {e.printStackTrace();}
        }
    }
}
