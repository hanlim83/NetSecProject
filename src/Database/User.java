package Database;

import java.util.Scanner;

public class User {
    private String email;
    private String status;
    private String hashPassword;
    private String publicKey;
    private String privateKey;
    private String phoneNo;
    private String entryID;
    private String ActivationTime;

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getHashPassword() {
        return hashPassword;
    }

    public void setHashPassword(String hashPassword) {
        this.hashPassword = hashPassword;
    }

    public String getPublicKey() {
        return publicKey;
    }

    public void setPublicKey(String publicKey) {
        this.publicKey = publicKey;
    }

    public String getPrivateKey() {
        return privateKey;
    }

    public void setPrivateKey(String privateKey) {
        this.privateKey = privateKey;
    }

    public String getPhoneNo() {
        return phoneNo;
    }

    public void setPhoneNo(String phoneNo) {
        this.phoneNo = phoneNo;
    }

    public String getEntryID() {
        return entryID;
    }

    public void setEntryID(String entryID) {
        this.entryID = entryID;
    }

    public String getActivationTime() {
        return ActivationTime;
    }

    public void setActivationTime(String activationTime) {
        ActivationTime = convertTime(activationTime);
    }

    private String convertTime(String time) {
        String dateDisplay;
        String timeDisplay;
        Scanner s = new Scanner(time).useDelimiter("T");
        String dateGeneral = s.next();
        timeDisplay = s.next();
        Scanner s1 = new Scanner(dateGeneral).useDelimiter("-");
        String year = s1.next();
        String month = s1.next();
        String date = s1.next();
        dateDisplay = date + "/" + month + "/" + year;
        System.out.println(dateDisplay + " " + timeDisplay.substring(0, 8));
        return dateDisplay + " " + timeDisplay.substring(0, 8);
    }
}
