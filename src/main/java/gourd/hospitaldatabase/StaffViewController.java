package gourd.hospitaldatabase;

import gourd.hospitaldatabase.pojos.Appointment;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;

public class StaffViewController {
    private StaffModel currentStaff;

    @FXML
    private Label welcomeLabel;

    @FXML
    private TextField patientIdField;

    @FXML
    private TextField staffIdField;

    // Other controls for date, time, and status
    @FXML
    private DatePicker appointmentDatePicker;

    @FXML
    private TextField appointmentTimeField;  // expecting a string like "14:30:00"

    // Label to display status messages
    @FXML
    private Label statusLabel;

    public void initialize() {
        currentStaff = (StaffModel) SessionManager.getInstance().getCurrentUser();
        welcomeLabel.setText("Welcome, " + currentStaff.getName());
    }

    public void onBackButtonClick(ActionEvent actionEvent) {
        MainController.navigate_to_main(actionEvent);
    }

    @FXML
    private void handleOpenCreateAppointment() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("create-appointment-view.fxml"));
            Parent root = loader.load();

            Stage stage = new Stage();
            stage.setTitle("Create Appointment");
            stage.setScene(new Scene(root));
            stage.initModality(Modality.APPLICATION_MODAL); // Makes it a modal pop-up.
            stage.setResizable(false);
            stage.showAndWait(); // Wait for the window to close before returning.

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // This method gets triggered when the "Create Appointment" button is clicked
    @FXML
    private void handleCreateAppointment(ActionEvent event) {
        // Retrieve and trim values from the text fields
        String patientText = patientIdField.getText().trim();
        String staffText = staffIdField.getText().trim();
        String timeText = appointmentTimeField.getText().trim();
        // Get the date from the DatePicker (may return null if not set)
        String dateValue = appointmentDatePicker.getValue().toString();

        // Validate that all fields have data
        if (patientText.isEmpty() || staffText.isEmpty() || timeText.isEmpty() || dateValue.isEmpty()) {
            statusLabel.setText("Error: Please fill in all fields.");
            return;
        }

        try {
            // Convert patient and staff ID strings to integers
            int patientId = Integer.parseInt(patientText);
            int staffId = Integer.parseInt(staffText);

            // Set the status as "Scheduled" if all validations pass
            String status = "Scheduled";

            // Create the Appointment object. Ensure the constructor parameters match your Appointment class.
            Appointment appointment = new Appointment(patientId, dateValue, timeText, status, staffId);
            System.out.println(appointment.toString());
            // Attempt to insert the appointment into the database
            boolean inserted = SQL_Manager.insertAppointment(appointment);
            if (inserted) {
                statusLabel.setText("Appointment created successfully. Status: " + status);
            } else {
                statusLabel.setText("Failed to create appointment.");
            }
        } catch (NumberFormatException e) {
            statusLabel.setText("Error: Patient ID and Staff ID must be valid integers.");
        }
    }

    @FXML
    public void onOpenReportsClick() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("report-view.fxml"));
            Parent root = loader.load();
            Stage stage = new Stage();
            stage.setTitle("Report Viewer");
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
