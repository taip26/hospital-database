package gourd.hospitaldatabase;

import javafx.fxml.FXML;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.time.LocalDate;
import java.util.List;

public class CreateAppointmentController {
    @FXML
    private TextField patientSearchField;
    @FXML
    private ListView<String> patientListView;
    @FXML
    private TextField patientIdField;
    @FXML
    private DatePicker appointmentDatePicker;
    @FXML
    private TextField appointmentTimeField;
    @FXML
    private Label statusLabel;

    private StaffModel currentStaff;
    private List<PatientModel> patients;

    @FXML
    public void initialize() {
        currentStaff = (StaffModel) SessionManager.getInstance().getCurrentUser();

        // Set today's date as default
        appointmentDatePicker.setValue(LocalDate.now());

        // Load all patients
        loadPatients();

        // Set up search functionality
        setupSearch();
    }

    private void loadPatients() {
        patients = SQL_Manager.getAllPatients();
        updatePatientList("");
    }

    private void setupSearch() {
        patientSearchField.textProperty().addListener((observable, oldValue, newValue) -> {
            updatePatientList(newValue);
        });

        patientListView.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) {
                // Extract patient ID from selection (format: "ID - Name")
                String idStr = newVal.split(" - ")[0];
                patientIdField.setText(idStr);
            }
        });
    }

    private void updatePatientList(String searchText) {
        patientListView.getItems().clear();

        patients.stream()
                .filter(patient ->
                        searchText.isEmpty() ||
                                patient.getName().toLowerCase().contains(searchText.toLowerCase()) ||
                                String.valueOf(patient.getPatientID()).contains(searchText))
                .forEach(patient -> {
                    patientListView.getItems().add(
                            patient.getPatientID() + " - " + patient.getName()
                    );
                });
    }

    @FXML
    public void handleCreateAppointment() {
        // Retrieve and trim values from the text fields
        String patientText = patientIdField.getText().trim();
        String timeText = appointmentTimeField.getText().trim();
        LocalDate date = appointmentDatePicker.getValue();

        if (patientText.isEmpty() || timeText.isEmpty() || date == null) {
            statusLabel.setText("Error: Please fill in all fields.");
            return;
        }

        try {
            int patientId = Integer.parseInt(patientText);
            int staffId = currentStaff.getStaffID();
            String dateValue = date.toString();
            String status = "Scheduled";

            boolean inserted = SQL_Manager.insertAppointment(patientId, staffId, dateValue, timeText, status);

            if (inserted) {
                statusLabel.setText("Appointment created successfully.");
                // Close window after successful creation
                Stage stage = (Stage) statusLabel.getScene().getWindow();
                stage.close();
            } else {
                statusLabel.setText("Failed to create appointment.");
            }
        } catch (NumberFormatException e) {
            statusLabel.setText("Error: Patient ID and Staff ID must be valid integers.");
        }
    }

    @FXML
    private void handleCancel() {
        Stage stage = (Stage) statusLabel.getScene().getWindow();
        stage.close();
    }
}