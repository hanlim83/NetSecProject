package Database;

import java.beans.PropertyVetoException;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;
import java.sql.*;
import java.util.ArrayList;

public class User_InfoDB {
    //TODO check if migration was successful
    private static String instanceConnectionName = "netsecpj:us-central1:nspj-project";
    private static String databaseName = "user_info";
    private static String username = "root";
    private static String password = "root";

    public ArrayList<User> getUserList() throws SQLException {
        ArrayList<User> UserList = new ArrayList<User>();

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

            preparedStatement=connection.prepareStatement("SELECT * FROM entries");

            resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                User user= new User();
                user.setEmail(resultSet.getString("email"));
                user.setStatus(resultSet.getString("status"));
                user.setHashPassword(resultSet.getString("hashPassword"));
                user.setPublicKey(resultSet.getString("publicKey"));
                user.setPrivateKey(resultSet.getString("privateKey"));
                user.setPhoneNo(resultSet.getString("phoneNo"));
                user.setEntryID(resultSet.getString("entryID"));
                user.setActivationTime(resultSet.getString("activationTime"));
                UserList.add(user);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            if (resultSet != null) try { resultSet.close(); } catch (SQLException e) {e.printStackTrace();}
            if (preparedStatement != null) try { preparedStatement.close(); } catch (SQLException e) {e.printStackTrace();}
            if (connection != null) try { connection.close(); } catch (SQLException e) {e.printStackTrace();}
        }


//        PreparedStatement
//
//        try (Statement statement = connection.createStatement()) {
////            ResultSet resultSet = statement.executeQuery("SELECT * FROM entries");
//            ResultSet resultSet = preparedStatement.executeQuery();
//
//        }
//        connection.close();
        return UserList;
    }

    public String getAccStatus(String email){
        String state = "";
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;

        try {
            connection = DataSource.getInstance().getConnection();
//            statement = connection.createStatement();

            preparedStatement=connection.prepareStatement("SELECT status FROM entries WHERE email=?");
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

    public void setUserKeyInfo(String hashPassword, String publicKey, String encryptedPrivateKey,String phoneNo, String activationTime, String email) throws SQLException {
        //maybe change to boolean next time

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

            preparedStatement=connection.prepareStatement("UPDATE entries SET status='Active', hashPassword=?, publicKey=?, privateKey=?, phoneNo=?, activationTime=? WHERE email=?");
            preparedStatement.setString(1,hashPassword);
            preparedStatement.setString(2,publicKey);
            preparedStatement.setString(3,encryptedPrivateKey);
            preparedStatement.setString(4,phoneNo);
            preparedStatement.setString(5,activationTime);
            preparedStatement.setString(6,email);

            preparedStatement.executeUpdate();

//            resultSet = preparedStatement.executeQuery();
//            while (resultSet.next()) {
//                state=resultSet.getString(1);
//            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            if (resultSet != null) try { resultSet.close(); } catch (SQLException e) {e.printStackTrace();}
            if (preparedStatement != null) try { preparedStatement.close(); } catch (SQLException e) {e.printStackTrace();}
            if (connection != null) try { connection.close(); } catch (SQLException e) {e.printStackTrace();}
        }




        //Here no need to return any result so how?
//        try (Statement statement = connection.createStatement()) {
//            ResultSet resultSet = statement.executeQuery("SELECT * FROM entries");
//            ResultSet resultSet = statement.executeQuery("UPDATE entries SET status='Active', hashPassword='"+hashPassword+"', publicKey='"+publicKey+"', privateLKey='"+privateKey+"' WHERE email='"+email+"'");
//            while (resultSet.next()) {
//                //System.out.println(resultSet.getString(1));
//                state=resultSet.getString(1);
//            }
//            statement.executeUpdate("UPDATE entries SET status='Active', hashPassword='"+hashPassword+"', publicKey='"+publicKey+"', privateKey='"+encryptedPrivateKey+"', phoneNo='"+phoneNo+"' WHERE email='"+email+"'");

//        }
    }

    public void deleteUser(String email) throws SQLException {
        //maybe change to boolean next time

//        String state = "";

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

            preparedStatement=connection.prepareStatement("DELETE FROM entries WHERE email=?");
            preparedStatement.setString(1,email);

            preparedStatement.executeUpdate();

//            resultSet = preparedStatement.executeQuery();
//            while (resultSet.next()) {
//                state=resultSet.getString(1);
//            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            if (resultSet != null) try { resultSet.close(); } catch (SQLException e) {e.printStackTrace();}
            if (preparedStatement != null) try { preparedStatement.close(); } catch (SQLException e) {e.printStackTrace();}
            if (connection != null) try { connection.close(); } catch (SQLException e) {e.printStackTrace();}
        }



        //Here no need to return any result so how?
//        try (Statement statement = connection.createStatement()) {
//            ResultSet resultSet = statement.executeQuery("SELECT * FROM entries");
//            ResultSet resultSet = statement.executeQuery("UPDATE entries SET status='Active', hashPassword='"+hashPassword+"', publicKey='"+publicKey+"', privateLKey='"+privateKey+"' WHERE email='"+email+"'");
//            while (resultSet.next()) {
//                //System.out.println(resultSet.getString(1));
//                state=resultSet.getString(1);
//            }
//            statement.executeUpdate("DELETE FROM entries WHERE email='" + email + "'");
//            preparedStatement.executeUpdate();
//        }
    }

    public void createUser(String email) throws SQLException {
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

            preparedStatement=connection.prepareStatement("INSERT INTO entries (email,status) values (?,'Inactive')");
            preparedStatement.setString(1,email);

            preparedStatement.executeUpdate();

//            resultSet = preparedStatement.executeQuery();
//            while (resultSet.next()) {
//                state=resultSet.getString(1);
//            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            if (resultSet != null) try { resultSet.close(); } catch (SQLException e) {e.printStackTrace();}
            if (preparedStatement != null) try { preparedStatement.close(); } catch (SQLException e) {e.printStackTrace();}
            if (connection != null) try { connection.close(); } catch (SQLException e) {e.printStackTrace();}
        }

        //Here no need to return any result so how?
//        try (Statement statement = connection.createStatement()) {
//            ResultSet resultSet = statement.executeQuery("SELECT * FROM entries");
//            ResultSet resultSet = statement.executeQuery("UPDATE entries SET status='Active', hashPassword='"+hashPassword+"', publicKey='"+publicKey+"', privateLKey='"+privateKey+"' WHERE email='"+email+"'");
//            while (resultSet.next()) {
//                //System.out.println(resultSet.getString(1));
//                state=resultSet.getString(1);
//            }
//            statement.executeUpdate("INSERT INTO entries (email,status) values ('"+email+"','Inactive')");
//            preparedStatement.executeUpdate();
//        }
    }

    public String getPhoneNumber(String email) throws SQLException {
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

            preparedStatement=connection.prepareStatement("INSERT INTO entries (email,status) values (?,'Inactive')");
            preparedStatement.setString(1,email);

            preparedStatement.executeUpdate();

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

//        preparedStatement=connection.prepareStatement("SELECT phoneNo FROM entries WHERE email=?");
//        preparedStatement.setString(1,email);

//        try (Statement statement = connection.createStatement()) {
////            ResultSet resultSet = statement.executeQuery("SELECT * FROM entries");
////            ResultSet resultSet = statement.executeQuery("SELECT phoneNo FROM entries WHERE email='"+email+"'");
//            ResultSet resultSet = preparedStatement.executeQuery();
//            while (resultSet.next()) {
//                phoneNo=resultSet.getString(1);
//            }
//        }
        return phoneNo;
    }

    public void setPhoneNo(String phoneNo, String email) throws SQLException {
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

            preparedStatement=connection.prepareStatement("UPDATE entries SET phoneNo=? WHERE email=?");
            preparedStatement.setString(1,phoneNo);
            preparedStatement.setString(2,email);

            preparedStatement.executeUpdate();

//            resultSet = preparedStatement.executeQuery();
//            while (resultSet.next()) {
//                state=resultSet.getString(1);
//            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            if (resultSet != null) try { resultSet.close(); } catch (SQLException e) {e.printStackTrace();}
            if (preparedStatement != null) try { preparedStatement.close(); } catch (SQLException e) {e.printStackTrace();}
            if (connection != null) try { connection.close(); } catch (SQLException e) {e.printStackTrace();}
        }

//        try (Statement statement = connection.createStatement()) {
////            statement.executeUpdate("UPDATE entries SET phoneNo='"+phoneNo+"' WHERE email='"+email+"'");
//            preparedStatement.executeUpdate();
//        }
    }

    public boolean checkPassword(String passwordToHash,String email) throws SQLException {
        String hashPassword=get_SHA_512_SecurePassword(passwordToHash,email);
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

            preparedStatement=connection.prepareStatement("SELECT CASE WHEN hashPassword=? THEN 1 ELSE 0 END FROM entries WHERE email=?");
            preparedStatement.setString(1,hashPassword);
            preparedStatement.setString(2,email);

//            preparedStatement.executeUpdate();

            resultSet = preparedStatement.executeQuery();
//            resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                if(resultSet.getString(1).equals("1")){
                    return true;
                }else{
                    return false;
                }
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
////            ResultSet resultSet = statement.executeQuery("SELECT CASE WHEN hashPassword='"+hashPassword+"' THEN 1 ELSE 0 END FROM entries WHERE email='"+email+"'");
//
//        }
        return false;
    }

    public PublicKey getMyPublicKey(String email) throws InvalidKeySpecException, NoSuchAlgorithmException {
        String publicKeyString = null;
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;

        try {
            connection = DataSource.getInstance().getConnection();
//            statement = connection.createStatement();

            preparedStatement=connection.prepareStatement("SELECT publicKey FROM entries WHERE email=?");
            preparedStatement.setString(1,email);

//            preparedStatement.executeUpdate();

            resultSet = preparedStatement.executeQuery();
//            resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                publicKeyString=resultSet.getString(1);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            if (resultSet != null) try { resultSet.close(); } catch (SQLException e) {e.printStackTrace();}
            if (preparedStatement != null) try { preparedStatement.close(); } catch (SQLException e) {e.printStackTrace();}
            if (connection != null) try { connection.close(); } catch (SQLException e) {e.printStackTrace();}
        }
        //convert to public key then return
        RSAKeyGenerator rsaKeyGenerator = new RSAKeyGenerator();
        return rsaKeyGenerator.getPublicKey(publicKeyString);
    }

    public PublicKey getPublicKey(String email) throws InvalidKeySpecException, NoSuchAlgorithmException {
        String publicKeyString = null;
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;

        try {
            connection = DataSource.getInstance().getConnection();
//            statement = connection.createStatement();

            preparedStatement=connection.prepareStatement("SELECT publicKey FROM entries WHERE email=?");
            preparedStatement.setString(1,email);

//            preparedStatement.executeUpdate();

            resultSet = preparedStatement.executeQuery();
//            resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                publicKeyString=resultSet.getString(1);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            if (resultSet != null) try { resultSet.close(); } catch (SQLException e) {e.printStackTrace();}
            if (preparedStatement != null) try { preparedStatement.close(); } catch (SQLException e) {e.printStackTrace();}
            if (connection != null) try { connection.close(); } catch (SQLException e) {e.printStackTrace();}
        }
        //convert to public key then return
        RSAKeyGenerator rsaKeyGenerator = new RSAKeyGenerator();
        return rsaKeyGenerator.getPublicKey(publicKeyString);
    }

    public PrivateKey getPrivateKey(String email, String password) throws Exception {
        String encryptedPrivateKeyString = null;
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;

        try {
            connection = DataSource.getInstance().getConnection();
//            statement = connection.createStatement();

            preparedStatement=connection.prepareStatement("SELECT privateKey WHERE email=?");
            preparedStatement.setString(1,email);

            preparedStatement.executeUpdate();

            resultSet = preparedStatement.executeQuery();
//            resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                encryptedPrivateKeyString=resultSet.getString(1);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            if (resultSet != null) try { resultSet.close(); } catch (SQLException e) {e.printStackTrace();}
            if (preparedStatement != null) try { preparedStatement.close(); } catch (SQLException e) {e.printStackTrace();}
            if (connection != null) try { connection.close(); } catch (SQLException e) {e.printStackTrace();}
        }

        RSAKeyGenerator rsaKeyGenerator = new RSAKeyGenerator();
        //encrypted privateKey get from cloud
        String decryptedPrivateKeyString = rsaKeyGenerator.getPrivateKeyString(password, encryptedPrivateKeyString);
        return rsaKeyGenerator.getPrivateKey(decryptedPrivateKeyString);
    }

    private String get_SHA_512_SecurePassword(String passwordToHash, String salt) {
        String generatedPassword = null;
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-512");
            md.update(salt.getBytes(StandardCharsets.UTF_8));
            byte[] bytes = md.digest(passwordToHash.getBytes(StandardCharsets.UTF_8));
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < bytes.length; i++) {
                sb.append(Integer.toString((bytes[i] & 0xff) + 0x100, 16).substring(1));
            }
            generatedPassword = sb.toString();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return generatedPassword;
    }
}
