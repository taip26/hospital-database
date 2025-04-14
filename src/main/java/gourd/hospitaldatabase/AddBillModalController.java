package gourd.hospitaldatabase;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.util.regex.Pattern;

public class AddBillModalController {
    @FXML
    private TextField dateField;

    @FXML
    private TextField timeField;

    @FXML
    private ComboBox<String> doctorDropdown;

    @FXML
    private ComboBox<String> patientDropdown;

    @FXML
    private TextField reasonField;

    @FXML
    private TextField moneySumField;

    @FXML
    private TextField insuranceDeductibleField;

    private static final Pattern DATE_PATTERN = Pattern.compile("\\d{2}/\\d{2}/\\d{4}");
    private static final Pattern TIME_PATTERN = Pattern.compile("(0[1-9]|1[0-2]):[0-5][0-9] (AM|PM)");

    @FXML
    private void initialize() {
        doctorDropdown.getItems().addAll("Dr. Smith", "Dr. Johnson", "Dr. Lee");
        patientDropdown.getItems().addAll("John Doe", "Jane Roe", "Alice Brown");
    }

    @FXML
    private void handleSubmit() {
        String date = dateField.getText();
        String time = timeField.getText();
        String doctor = doctorDropdown.getValue();
        String patient = patientDropdown.getValue();
        String reason = reasonField.getText();
        String moneySum = moneySumField.getText();
        String insuranceDeductible = insuranceDeductibleField.getText();

        // Validate inputs
        if (!DATE_PATTERN.matcher(date).matches()) {
            showAlert("Invalid Date", "Please enter a valid date in MM/DD/YYYY format.");
            return;
        }

        if (!TIME_PATTERN.matcher(time).matches()) {
            showAlert("Invalid Time", "Please enter a valid time in HH:MM AM/PM format.");
            return;
        }

        if (doctor == null || doctor.isEmpty()) {
            showAlert("Invalid Doctor", "Please select a doctor.");
            return;
        }

        if (patient == null || patient.isEmpty()) {
            showAlert("Invalid Patient", "Please select a patient.");
            return;
        }

        if (reason == null || reason.trim().isEmpty()) {
            showAlert("Invalid Reason", "Please enter a reason.");
            return;
        }

        try {
            Double.parseDouble(moneySum);
        } catch (NumberFormatException e) {
            showAlert("Invalid Money Sum", "Please enter a valid number for Money Sum.");
            return;
        }

        try {
            Double.parseDouble(insuranceDeductible);
        } catch (NumberFormatException e) {
            showAlert("Invalid Insurance Deductible", "Please enter a valid number for Insurance Deductible.");
            return;
        }

        // If all validations pass, process the data
        System.out.println("Date: " + date);
        System.out.println("Time: " + time);
        System.out.println("Doctor: " + doctor);
        System.out.println("Patient: " + patient);
        System.out.println("Reason: " + reason);
        System.out.println("Money Sum: " + moneySum);
        System.out.println("Insurance Deductible: " + insuranceDeductible);
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    public void handleCancel(ActionEvent actionEvent) {
        ((Stage) ((Node) actionEvent.getSource()).getScene().getWindow()).close();
    }
}