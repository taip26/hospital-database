module gourd.hospitaldatabase {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.swing;
    requires org.knowm.xchart;
    requires java.sql;


    opens gourd.hospitaldatabase to javafx.fxml;
    exports gourd.hospitaldatabase;
}