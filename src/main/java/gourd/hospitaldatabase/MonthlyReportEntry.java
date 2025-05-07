package gourd.hospitaldatabase;

import javafx.beans.property.*;

public class MonthlyReportEntry {
    private final StringProperty month;
    private final IntegerProperty count;

    public MonthlyReportEntry(String month, int count) {
        this.month = new SimpleStringProperty(month);
        this.count = new SimpleIntegerProperty(count);
    }

    public StringProperty monthProperty() {
        return month;
    }

    public IntegerProperty countProperty() {
        return count;
    }
}
