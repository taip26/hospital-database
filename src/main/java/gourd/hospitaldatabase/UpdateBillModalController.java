package gourd.hospitaldatabase;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.regex.Pattern;

public class UpdateBillModalController {
    @FXML
    private ComboBox<String> adminDropdown;

    @FXML
    private ComboBox<String> billDropdown;

    @FXML
    public ComboBox<String> statusDropdown;

    @FXML
    private TextField dateField;

    @FXML
    private TextField reasonField;

    @FXML
    private TextField moneySumField;

    @FXML
    private TextField insuranceDeductibleField;

    private static final Pattern DATE_PATTERN = Pattern.compile("\\d{2}/\\d{2}/\\d{4}");

    @FXML
    private void initialize() {
        statusDropdown.getItems().addAll("Pending", "Paid", "Void");
        List<String> bills = SQL_Manager.getAllMedicalBillsString();
        billDropdown.getItems().addAll(bills);
        List<String> admins = SQL_Manager.getAllAdminsString();
        adminDropdown.getItems().addAll(admins);
    }

    @FXML
    private void handleUpdate() {
        String selectedAdmin = adminDropdown.getValue();
        if (selectedAdmin == null || selectedAdmin.isEmpty()) {
            showAlert("Invalid Admin", "Please select an admin.");
            return;
        }
        // Extract admin ID from the selected admin string
        int adminID = Integer.parseInt(selectedAdmin.split(" ")[0]);
        String selectedBill = billDropdown.getValue();
        if (selectedBill == null || selectedBill.isEmpty()) {
            showAlert("Invalid Selection", "Please select a bill to update.");
            return;
        }
        int billId = Integer.parseInt(selectedBill.split(" ")[0]);
        String billDate = dateField.getText();
        String reason = reasonField.getText();
        String paymentAmount = moneySumField.getText();
        String insuranceDeductible = insuranceDeductibleField.getText();
        String paymentStatus = statusDropdown.getValue();

        if (!DATE_PATTERN.matcher(billDate).matches()) {
            showAlert("Invalid Date", "Please enter a valid date in MM/DD/YYYY format.");
            return;
        }

        if (reason == null || reason.trim().isEmpty()) {
            showAlert("Invalid Reason", "Please enter a reason.");
            return;
        }

        double paymentAmountValue;
        double insuranceDeductibleValue;
        try {
            paymentAmountValue = Double.parseDouble(paymentAmount);
        } catch (NumberFormatException e) {
            showAlert("Invalid Payment Amount", "Please enter a valid number for Payment Amount.");
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
            LocalDate parsedDate = LocalDate.parse(billDate, DateTimeFormatter.ofPattern("MM/dd/yyyy"));
            formattedDate = parsedDate.toString(); // Converts to YYYY-MM-DD
        } catch (Exception e) {
            showAlert("Invalid Date", "Failed to parse date. Ensure it is in MM/DD/YYYY format.");
            return;
        }

        // Update the medical bill in the database
        try {
            String result = SQL_Manager.updateMedicalBill(
                    billId,
                    formattedDate,
                    reason,
                    paymentAmountValue,
                    insuranceDeductibleValue,
                    paymentStatus,
                    adminID // Replace with actual admin ID
            );
            showAlert("Success", result);
            handleCancel(null); // Close the modal after successful update
        } catch (Exception e) {
            showAlert("Error", "Failed to update medical bill: " + e.getMessage());
        }
    }

    @FXML
    private void handleDelete() {
        String selectedBill = billDropdown.getValue();
        if (selectedBill == null || selectedBill.isEmpty()) {
            showAlert("Invalid Selection", "Please select a bill to delete.");
            return;
        }
        int billId = Integer.parseInt(selectedBill.split(" ")[0]);

        // Delete the medical bill from the database
        try {
            String result = SQL_Manager.deleteMedicalBill(billId);
            showAlert("Success", result);
            handleCancel(null); // Close the modal after successful deletion
        } catch (Exception e) {
            showAlert("Error", "Failed to delete medical bill: " + e.getMessage());
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
