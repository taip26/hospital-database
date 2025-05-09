package gourd.hospitaldatabase;

    import javafx.fxml.FXML;
    import javafx.scene.control.*;
    import javafx.stage.Stage;

    import java.time.LocalDate;
    import java.time.format.DateTimeFormatter;
    import java.util.List;

public class EditAppointmentViewController {

    @FXML
    private ComboBox<String> statusComboBox;

    private List<PatientModel> patients;

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

    private AppointmentModel appointment;

    public void initialize() {
        // Set today's date as default
        // Load all patients
        loadPatients();

        // Set up search functionality
        setupSearch();

        statusComboBox.getItems().addAll("Scheduled", "Completed", "Cancelled");
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

    public void setAppointment(AppointmentModel appointment) {
        this.appointment = appointment;

        // Populate fields with appointment data
        patientIdField.setText(String.valueOf(appointment.getPatientID()));
        appointmentDatePicker.setValue(LocalDate.parse(appointment.getVisitDate(), DateTimeFormatter.ofPattern("yyyy-MM-dd")));
        appointmentTimeField.setText(appointment.getVisitTime());
        statusComboBox.setValue(appointment.getStatus()); // Set the current status
    }

    @FXML
    private void handleEditAppointment() {
        try {
            // Validate input
            String patientId = patientIdField.getText().trim();
            LocalDate date = appointmentDatePicker.getValue();
            String time = appointmentTimeField.getText().trim();
            String status = statusComboBox.getValue();

            if (patientId.isEmpty() || date == null || time.isEmpty() || status == null) {
                statusLabel.setText("Error: Please fill in all fields.");
                return;
            }

            // Update the appointment object
            appointment.setPatientID(Integer.parseInt(patientId));
            appointment.setVisitDate(String.valueOf(date));
            appointment.setVisitTime(String.valueOf(java.time.LocalTime.parse(time, DateTimeFormatter.ofPattern("HH:mm"))));
            appointment.setStatus(status);
            // Update the appointment in the database
            boolean updated = SQL_Manager.updateAppointment(appointment.getAppointmentID(), appointment.getPatientID(), appointment.getStaffID(), appointment.getVisitDate(), appointment.getVisitTime(), appointment.getStatus());

            if (updated) {
                statusLabel.setText("Appointment updated successfully.");
                closeWindow();
            } else {
                statusLabel.setText("Failed to update appointment.");
            }
        } catch (Exception e) {
            statusLabel.setText("Error: Invalid input.");
        }
    }

    @FXML
    private void handleCancel() {
        closeWindow();
    }

    private void closeWindow() {
        Stage stage = (Stage) statusLabel.getScene().getWindow();
        stage.close();
    }
}