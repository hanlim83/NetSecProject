import Database.admin_DB;
import Model.AWSSMS;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Scanner;

public class AWSSMSTest {
    public static void main(String[] args) {
        AWSSMS generalHandler = new AWSSMS();
        admin_DB SQL = new admin_DB();
        Scanner sc = new Scanner(System.in);
        try {
            ArrayList<String> AdminphoneNumbers = SQL.getAllPhoneNo();
            AWSSMS specficHandler = new AWSSMS(AdminphoneNumbers);
            specficHandler.sendAlert();
            System.out.print("Enter a phone number: ");
            String phoneNumber = sc.next();
            sc.nextLine();
            System.out.print("Enter Message (On the same line): ");
            String Message = sc.nextLine();
            generalHandler.sendSMS(phoneNumber, Message);

        } catch (SQLException e) {
            System.err.println("SQL Not Working!");
            System.out.print("Enter a phone number: ");
            String phoneNumber = sc.next();
            sc.nextLine();
            System.out.print("Enter Message (On the same line): ");
            String Message = sc.nextLine();
            generalHandler.sendSMS(phoneNumber, Message);
        } finally {
            sc.close();
        }
    }

}
