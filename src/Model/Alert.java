package Model;

import java.util.Calendar;

public class Alert {
    private Calendar dateTime;
    private String alertContent;

    public Alert(String alertContent) {
        this.alertContent = alertContent;
        dateTime = Calendar.getInstance();
    }

    public Alert(Calendar dateTime, String alertContent) {
        this.dateTime = dateTime;
        this.alertContent = alertContent;
    }

    public Calendar getDateTime() {
        return dateTime;
    }

    public String getAlertContent() {
        return alertContent;
    }
}
