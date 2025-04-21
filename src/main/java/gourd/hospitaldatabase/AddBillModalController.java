package gourd.hospitaldatabase;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

public class AddBillModalController {
    @FXML
    private ComboBox<String> adminDropdown;

    @FXML
    private ComboBox<String> appointmentDropdown;

    @FXML
    private TextField dateField;

    @FXML
    private TextField reasonField;

    @FXML
    private TextField moneySumField;

    @FXML
    private TextField insuranceDeductibleField;

    Map<String, AppointmentModel> appointmentMap = new HashMap<>();

    private static final Pattern DATE_PATTERN = Pattern.compile("\\d{2}/\\d{2}/\\d{4}");
    private static final Pattern TIME_PATTERN = Pattern.compile("(0[1-9]|1[0-2]):[0-5][0-9] (AM|PM)");

    @FXML
    private void initialize() {
        List<AppointmentModel> appointments = SQL_Manager.getAllAppointmentsList();
        for (AppointmentModel appointment : appointments) {
            appointmentDropdown.getItems().add(
                   appointment.getAppointmentID() + " " + appointment.getVisitDate() + " " + appointment.getVisitTime() + " " +
                            appointment.getPatientID() + " " + appointment.getStaffID()
            );
            appointmentMap.put(
                    appointment.getAppointmentID() + " " + appointment.getVisitDate() + " " + appointment.getVisitTime() + " " +
                            appointment.getPatientID() + " " + appointment.getStaffID(),
                    appointment
            );
        }
        List<String> admins = SQL_Manager.getAllAdminsString();
        adminDropdown.getItems().addAll(admins);
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
        String appointmentSelected = appointmentDropdown.getValue();
        if (appointmentSelected == null || appointmentSelected.isEmpty()) {
            showAlert("Invalid Appointment", "Please select an appointment.");
            return;
        }
        // Extract appointment details
        AppointmentModel appointment = appointmentMap.get(appointmentSelected);
        int doctorID = appointment.getStaffID();
        int patientID = appointment.getPatientID();
//        String[] appointmentSplit = appointment.split(" ");
//        if (appointmentSplit.length < 4) {
//            showAlert("Invalid Appointment Format", "The selected appointment format is incorrect.");
//            return;
//        }
//        String doctorIDString = appointmentSplit[0];
//        String patientIDString = appointmentSplit[1];
//        String visitDate = appointmentSplit[2];
//        String visitTime = appointmentSplit[3];
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

//        int patientID;
//        int doctorID;
//
//        try {
//            patientID = Integer.parseInt(patientIDString);
//            doctorID = Integer.parseInt(doctorIDString);
//        } catch (NumberFormatException e) {
//            showAlert("Invalid ID", "Please select a valid appointment.");
//            return;
//        }

        String formattedDate;
        try {
            LocalDate parsedDate = LocalDate.parse(date, DateTimeFormatter.ofPattern("MM/dd/yyyy"));
            formattedDate = parsedDate.toString(); // Converts to YYYY-MM-DD
        } catch (Exception e) {
            showAlert("Invalid Date", "Failed to parse date. Ensure it is in MM/DD/YYYY format.");
            return;
        }


        System.out.println("Debugging Values:");
        System.out.println("Appointment ID: " + appointment.getAppointmentID());
        System.out.println("Patient ID: " + patientID);
        System.out.println("Doctor ID: " + doctorID);
        System.out.println("Date: " + formattedDate);
        System.out.println("Reason: " + reason);
        System.out.println("Money Sum: " + moneySumValue);
        System.out.println("Insurance Deductible: " + insuranceDeductibleValue);
        System.out.println("Payment Status: Pending");
        System.out.println("Admin ID: 1");


        // Insert data into the database
        try {
            String result = SQL_Manager.insertMedicalBill(
                    appointment.getAppointmentID(),
                    patientID, // Replace with actual patientID
                    doctorID, // Replace with actual staffID
                    formattedDate,
                    reason,
                    moneySumValue,
                    insuranceDeductibleValue,
                    "Pending", // Example payment status
                    adminID  // Replace with actual adminID
            );
            showAlert("Success", result);
            handleCancel(null); // Close the modal after successful submission
        } catch (Exception e) {
            showAlert("Error", "Failed to insert medical bill: " + e.getMessage());
        }

    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
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
            stage = (Stage) dateField.getScene().getWindow(); // Fallback to the current window
        }
        stage.close();
    }
}
