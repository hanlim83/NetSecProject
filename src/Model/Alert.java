package Model;

import com.jfoenix.controls.datamodels.treetable.RecursiveTreeObject;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

import java.text.SimpleDateFormat;
import java.util.Calendar;

public class Alert extends RecursiveTreeObject<Alert> {
    private StringProperty dateTime;
    private Calendar calendar;
    private StringProperty type;
    private String pcapPath;

    public Alert(String type, String pcapPath) {
        this.calendar = Calendar.getInstance();
        this.type = new SimpleStringProperty(type);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        this.dateTime = new SimpleStringProperty(sdf.format(calendar.getTime()));
        this.pcapPath = pcapPath;
    }

    public String getDateTime() {
        return dateTime.get();
    }

    public StringProperty dateTimeProperty() {
        return dateTime;
    }

    public Calendar getCalendar() {
        return calendar;
    }

    public String getType() {
        return type.get();
    }

    public StringProperty typeProperty() {
        return type;
    }

    public String getPcapPath() {
        return pcapPath;
    }

    @Override
    public String toString() {
        return "Alert Generated: " + dateTime.getValue() + " | Alert Type: " + type.getValue();
    }
}
