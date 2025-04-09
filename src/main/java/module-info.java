module gourd.hospitaldatabase {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;


    opens gourd.hospitaldatabase to javafx.fxml;
    exports gourd.hospitaldatabase;
}