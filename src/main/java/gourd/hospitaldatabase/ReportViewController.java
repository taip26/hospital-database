package gourd.hospitaldatabase;

import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.collections.*;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

import java.util.List;
import java.util.Map;

public class ReportViewController {

    // Insurance report UI
    @FXML private TableView<InsuranceReportEntry> insuranceTable;
    @FXML private TableColumn<InsuranceReportEntry, String> insuranceColumn;
    @FXML private TableColumn<InsuranceReportEntry, Integer> insuranceCountColumn;
    @FXML private TableView<MonthlyReportEntry> monthlyTable;

    // Appointments report UI
    @FXML private TableView<AppointmentReportEntry> appointmentsTable;

    @FXML
    public void initialize() {
        // Link insurance columns to data properties
        insuranceColumn.setCellValueFactory(data -> data.getValue().insuranceProperty());
        insuranceCountColumn.setCellValueFactory(data -> data.getValue().countProperty().asObject());
    }

    @FXML
    public void onLoadInsuranceReportClick() {
        Map<String, Integer> data = FacilityReportGenerator.getPatientCountByInsurance();
        ObservableList<InsuranceReportEntry> entries = FXCollections.observableArrayList();

        for (Map.Entry<String, Integer> entry : data.entrySet()) {
            entries.add(new InsuranceReportEntry(entry.getKey(), entry.getValue()));
        }

        insuranceTable.setItems(entries);
    }

    @FXML
    public void onLoadAppointmentsReportClick() {
        Map<String, Integer> data = FacilityReportGenerator.getAppointmentsPerDoctor();
        ObservableList<AppointmentReportEntry> entries = FXCollections.observableArrayList();

        for (Map.Entry<String, Integer> entry : data.entrySet()) {
            entries.add(new AppointmentReportEntry(entry.getKey(), entry.getValue()));
        }

        appointmentsTable.getColumns().clear();

        TableColumn<AppointmentReportEntry, String> doctorCol = new TableColumn<>("Doctor");
        doctorCol.setCellValueFactory(cellData -> cellData.getValue().doctorNameProperty());

        TableColumn<AppointmentReportEntry, Integer> countCol = new TableColumn<>("Appointments");
        countCol.setCellValueFactory(cellData -> cellData.getValue().countProperty().asObject());

        appointmentsTable.getColumns().addAll(doctorCol, countCol);
        appointmentsTable.setItems(entries);
    }


    @FXML
    public void onLoadMonthlyReportClick() {
        Map<String, Integer> data = FacilityReportGenerator.getMonthlyAppointmentCounts();
        ObservableList<MonthlyReportEntry> entries = FXCollections.observableArrayList();

        for (Map.Entry<String, Integer> entry : data.entrySet()) {
            entries.add(new MonthlyReportEntry(entry.getKey(), entry.getValue()));
        }

        monthlyTable.getColumns().clear();

        TableColumn<MonthlyReportEntry, String> monthCol = new TableColumn<>("Month");
        monthCol.setCellValueFactory(cellData -> cellData.getValue().monthProperty());

        TableColumn<MonthlyReportEntry, Integer> countCol = new TableColumn<>("Appointments");
        countCol.setCellValueFactory(cellData -> cellData.getValue().countProperty().asObject());

        monthlyTable.getColumns().addAll(monthCol, countCol);
        monthlyTable.setItems(entries);
    }
}
