package gourd.hospitaldatabase;

import javafx.beans.property.*;

public class InsuranceReportEntry {
    private final StringProperty insurance;
    private final IntegerProperty count;

    public InsuranceReportEntry(String insurance, int count) {
        this.insurance = new SimpleStringProperty(insurance);
        this.count = new SimpleIntegerProperty(count);
    }

    public StringProperty insuranceProperty() { return insurance; }
    public IntegerProperty countProperty() { return count; }
}
