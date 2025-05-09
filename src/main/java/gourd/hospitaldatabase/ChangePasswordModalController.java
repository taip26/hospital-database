package gourd.hospitaldatabase;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.stage.Stage;

public class ChangePasswordModalController {

    @FXML private PasswordField currentPasswordField;
    @FXML private PasswordField newPasswordField;
    @FXML private PasswordField confirmNewPasswordField;
    @FXML private Label statusLabel;

    private SessionManager sessionManager = SessionManager.getInstance();

    @FXML
    public void initialize() {
        statusLabel.setText("");
    }

    @FXML
    private void handleChangePassword() {
        String currentPassword = currentPasswordField.getText();
        String newPassword = newPasswordField.getText();
        String confirmNewPassword = confirmNewPasswordField.getText();

        statusLabel.setText(""); // Clear previous status

        if (currentPassword.isEmpty() || newPassword.isEmpty() || confirmNewPassword.isEmpty()) {
            statusLabel.setText("All password fields are required.");
            return;
        }

        if (!newPassword.equals(confirmNewPassword)) {
            statusLabel.setText("New passwords do not match.");
            newPasswordField.clear();
            confirmNewPasswordField.clear();
            newPasswordField.requestFocus();
            return;
        }

        if (newPassword.length() < 6) { // Basic password policy
            statusLabel.setText("New password must be at least 6 characters.");
            newPasswordField.requestFocus();
            return;
        }

        if (newPassword.equals(currentPassword)) {
            statusLabel.setText("New password cannot be the same as the current password.");
            newPasswordField.clear();
            confirmNewPasswordField.clear();
            newPasswordField.requestFocus();
            return;
        }

        Object currentUserObject = sessionManager.getCurrentUser();
        String userType = sessionManager.getUserType();
        int userId = -1;

        if ("ADMIN".equals(userType) && currentUserObject instanceof AdministratorModel) {
            userId = ((AdministratorModel) currentUserObject).getAdminID();
        } else if ("STAFF".equals(userType) && currentUserObject instanceof StaffModel) {
            userId = ((StaffModel) currentUserObject).getStaffID();
        }

        if (userId == -1 || userType == null) {
            showAlert(Alert.AlertType.ERROR, "User Error", "Could not identify current user session.");
            return;
        }

        String storedHash = SQL_Manager.getUserPasswordHash(userId, userType);
        if (storedHash == null) {
             showAlert(Alert.AlertType.ERROR, "Verification Error", "Could not retrieve current password for verification.");
             return;
        }

        if (!Hash.verifyPassword(currentPassword, storedHash)) {
            statusLabel.setText("Incorrect current password.");
            currentPasswordField.clear();
            newPasswordField.clear();
            confirmNewPasswordField.clear();
            currentPasswordField.requestFocus();
            return;
        }

        boolean success = SQL_Manager.updateUserPassword(userId, userType, newPassword);

        if (success) {
            showAlert(Alert.AlertType.INFORMATION, "Success", "Password changed successfully.");
            closeWindow();
        } else {
            showAlert(Alert.AlertType.ERROR, "Update Failed", "Failed to change password. Please try again or contact support.");
            statusLabel.setText("Failed to update password in database.");
        }
    }

    @FXML
    private void handleCancel(ActionEvent event) {
        closeWindow();
    }

    private void closeWindow() {
        Stage stage = (Stage) statusLabel.getScene().getWindow();
        stage.close();
    }

    private void showAlert(Alert.AlertType alertType, String title, String message) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}