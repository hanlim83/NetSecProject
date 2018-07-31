import Database.admin_DB;
import Model.OutlookEmail;

import java.sql.SQLException;

public class OutlookTesting {

    public static void main(String[] args) throws SQLException {
        OutlookEmail EmailHandler = new OutlookEmail();
        EmailHandler.setReceiverAddress("hansenhappy83@gmail.com");
//        EmailHandler.sendEmail("Testing","Hello, \nThis is for testing purposes only!!!");
        admin_DB SQL = new admin_DB();
        EmailHandler.setAdminEmailAddresses(SQL.getAllEmail());
        EmailHandler.sendBatchEmail("Do not be alarmed 2", "This is just a test of Outlook message class");
    }

}
