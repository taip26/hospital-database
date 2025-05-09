package gourd.hospitaldatabase;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.control.ComboBox; 
import javafx.stage.Stage;

public class AddStaffModalController {

    @FXML private TextField nameField;
    @FXML private TextField roleField; 
    @FXML private TextField usernameField;
    @FXML private PasswordField passwordField;
    @FXML private Label statusLabel;

    private AdminViewController adminViewController;

    public void setAdminViewController(AdminViewController adminViewController) {
        this.adminViewController = adminViewController;
    }

    @FXML
    public void initialize() {
        statusLabel.setText("");
    }

    @FXML
    private void handleAddStaff() {
        String name = nameField.getText();
        String role = roleField.getText(); 
        String username = usernameField.getText();
        String password = passwordField.getText();

        if (name.isEmpty() || role.isEmpty() || username.isEmpty() || password.isEmpty()) {
            statusLabel.setText("All fields are required.");
            return;
        }

        if (username.length() < 4) {
            statusLabel.setText("Username must be at least 4 characters.");
            return;
        }
        if (password.length() < 6) {
            statusLabel.setText("Password must be at least 6 characters.");
            return;
        }

        boolean success = SQL_Manager.insertStaff(name, role, username, password);

        if (success) {
            showAlert(Alert.AlertType.INFORMATION, "Success", "Staff member '" + name + "' added successfully.");
            if (adminViewController != null) {
                adminViewController.refreshStaffList(); 
            }
            closeWindow();
        } else {
            showAlert(Alert.AlertType.ERROR, "Error", "Failed to add staff member. Username might already exist or database error occurred.");
            statusLabel.setText("Failed to add staff. Check console for details.");
        }
    }

    @FXML
    private void handleCancel(ActionEvent event) {
        closeWindow();
    }

    private void closeWindow() {
        Stage stage = (Stage) nameField.getScene().getWindow();
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