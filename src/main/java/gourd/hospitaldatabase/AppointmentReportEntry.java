package gourd.hospitaldatabase;

import javafx.beans.property.*;

public class AppointmentReportEntry {
    private final StringProperty doctorName;
    private final IntegerProperty count;

    public AppointmentReportEntry(String doctorName, int count) {
        this.doctorName = new SimpleStringProperty(doctorName);
        this.count = new SimpleIntegerProperty(count);
    }

    public StringProperty doctorNameProperty() {
        return doctorName;
    }

    public IntegerProperty countProperty() {
        return count;
    }
}
