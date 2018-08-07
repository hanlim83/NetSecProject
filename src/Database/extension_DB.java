package Database;

import java.sql.*;
import java.util.ArrayList;

public class extension_DB {
    public ArrayList<String> getExtensionList(){
        ArrayList<String> ExtensionList = new ArrayList<String>();

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

        return ExtensionList;
    }

    public void createExtension(String extension){
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;

        try {
            connection = DataSource.getInstance().getConnection();

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
    }

    public void deleteExtension(String extension){
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;

        try {
            connection = DataSource.getInstance().getConnection();

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
    }
}
