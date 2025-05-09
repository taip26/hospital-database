package gourd.hospitaldatabase;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.KeyEvent;
import javafx.stage.Stage;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

public class AddBillModalController {
    @FXML
    private ComboBox<String> adminDropdown;

    @FXML
    private TextField appointmentSearchField;

    @FXML
    private TableView<AppointmentModel> appointmentTableView;

    @FXML
    private TableColumn<AppointmentModel, Integer> idColumn;

    @FXML
    private TableColumn<AppointmentModel, String> dateColumn;

    @FXML
    private TableColumn<AppointmentModel, String> timeColumn;

    @FXML
    private TableColumn<AppointmentModel, String> patientColumn;

    @FXML
    private TableColumn<AppointmentModel, String> staffColumn;

    @FXML
    private TextField dateField;

    @FXML
    private TextField reasonField;

    @FXML
    private TextField moneySumField;

    @FXML
    private TextField insuranceDeductibleField;

    private ObservableList<AppointmentModel> allAppointments;
    private AppointmentModel selectedAppointment;

    private static final Pattern DATE_PATTERN = Pattern.compile("\\d{2}/\\d{2}/\\d{4}");

    @FXML
    private void initialize() {
        // Hide ID column
        idColumn.setVisible(false);

        // Set up table columns
        idColumn.setCellValueFactory(new PropertyValueFactory<>("appointmentID"));
        dateColumn.setCellValueFactory(new PropertyValueFactory<>("visitDate"));
        timeColumn.setCellValueFactory(new PropertyValueFactory<>("visitTime"));

        // Load all appointments with names already joined
        allAppointments = FXCollections.observableArrayList(SQL_Manager.getAllAppointmentsWithNames());

        // Set up name columns - these will use the pre-populated values
        patientColumn.setCellValueFactory(new PropertyValueFactory<>("patientName"));
        staffColumn.setCellValueFactory(new PropertyValueFactory<>("staffName"));

        appointmentTableView.setItems(allAppointments);

        // Add selection listener for appointment table
        appointmentTableView.getSelectionModel().selectedItemProperty().addListener(
                (obs, oldSelection, newSelection) -> {
                    if (newSelection != null) {
                        selectedAppointment = newSelection;
                    }
                });

        // Load admin dropdown
        List<String> admins = SQL_Manager.getAllAdminsString();
        adminDropdown.getItems().addAll(admins);

        // Set default date to today
        dateField.setText(LocalDate.now().format(DateTimeFormatter.ofPattern("MM/dd/yyyy")));
    }

    @FXML
    private void searchAppointment(KeyEvent event) {
        String searchText = appointmentSearchField.getText().toLowerCase();

        if (searchText == null || searchText.isEmpty()) {
            appointmentTableView.setItems(allAppointments);
            return;
        }

        // Filter appointments based on search text
        ObservableList<AppointmentModel> filteredList = FXCollections.observableArrayList();
        for (AppointmentModel appointment : allAppointments) {
            if (String.valueOf(appointment.getAppointmentID()).contains(searchText) ||
                    String.valueOf(appointment.getPatientID()).contains(searchText) ||
                    String.valueOf(appointment.getStaffID()).contains(searchText) ||
                    appointment.getVisitDate().toLowerCase().contains(searchText) ||
                    appointment.getVisitTime().toLowerCase().contains(searchText) ||
                    appointment.getStatus().toLowerCase().contains(searchText)) {
                filteredList.add(appointment);
            }
        }

        appointmentTableView.setItems(filteredList);
    }

    @FXML
    private void handleSubmit() {
        String selectedAdmin = adminDropdown.getValue();
        if (selectedAdmin == null || selectedAdmin.isEmpty()) {
            showAlert("Invalid Admin", "Please select an admin.");
            return;
        }

        // Extract admin ID from the selected admin string
        int adminID = Integer.parseInt(selectedAdmin.split(" ")[0]);

        if (selectedAppointment == null) {
            showAlert("Invalid Appointment", "Please select an appointment from the table.");
            return;
        }

        int doctorID = selectedAppointment.getStaffID();
        int patientID = selectedAppointment.getPatientID();

        String date = dateField.getText();
        String reason = reasonField.getText();
        String moneySum = moneySumField.getText();
        String insuranceDeductible = insuranceDeductibleField.getText();

        // Validate inputs
        if (!DATE_PATTERN.matcher(date).matches()) {
            showAlert("Invalid Date", "Please enter a valid date in MM/DD/YYYY format.");
            return;
        }

        if (reason == null || reason.trim().isEmpty()) {
            showAlert("Invalid Reason", "Please enter a reason.");
            return;
        }

        double moneySumValue;
        double insuranceDeductibleValue;
        try {
            moneySumValue = Double.parseDouble(moneySum);
        } catch (NumberFormatException e) {
            showAlert("Invalid Money Sum", "Please enter a valid number for Money Sum.");
            return;
        }

        try {
            insuranceDeductibleValue = Double.parseDouble(insuranceDeductible);
        } catch (NumberFormatException e) {
            showAlert("Invalid Insurance Deductible", "Please enter a valid number for Insurance Deductible.");
            return;
        }

        String formattedDate;
        try {
            LocalDate parsedDate = LocalDate.parse(date, DateTimeFormatter.ofPattern("MM/dd/yyyy"));
            formattedDate = parsedDate.toString(); // Converts to YYYY-MM-DD
        } catch (Exception e) {
            showAlert("Invalid Date", "Failed to parse date. Ensure it is in MM/DD/YYYY format.");
            return;
        }

        // Debug values
        System.out.println("Debugging Values:");
        System.out.println("Appointment ID: " + selectedAppointment.getAppointmentID());
        System.out.println("Patient ID: " + patientID);
        System.out.println("Doctor ID: " + doctorID);
        System.out.println("Date: " + formattedDate);
        System.out.println("Reason: " + reason);
        System.out.println("Money Sum: " + moneySumValue);
        System.out.println("Insurance Deductible: " + insuranceDeductibleValue);
        System.out.println("Payment Status: Pending");
        System.out.println("Admin ID: " + adminID);

        // Insert data into the database
        try {
            String result = SQL_Manager.insertMedicalBill(
                    selectedAppointment.getAppointmentID(),
                    patientID,
                    doctorID,
                    formattedDate,
                    reason,
                    moneySumValue,
                    insuranceDeductibleValue,
                    "Pending",
                    adminID
            );
            showAlert("Success", result);
            handleCancel(null); // Close the modal after successful submission
        } catch (Exception e) {
            showAlert("Error", "Failed to insert medical bill: " + e.getMessage());
        }
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    public void handleCancel(ActionEvent actionEvent) {
        Stage stage;
        if (actionEvent != null) {
            stage = (Stage) ((Node) actionEvent.getSource()).getScene().getWindow();
        } else {
            stage = (Stage) dateField.getScene().getWindow();
        }
        stage.close();
    }
}