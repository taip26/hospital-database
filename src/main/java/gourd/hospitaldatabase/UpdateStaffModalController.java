package gourd.hospitaldatabase;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.ComboBox; 
import javafx.scene.control.PasswordField; 
import javafx.stage.Stage;

public class UpdateStaffModalController {

    @FXML private TextField staffIdField;
    @FXML private TextField nameField;
    @FXML private TextField roleField; // Or ComboBox<String> roleComboBox;
    @FXML private TextField usernameField;
    @FXML private PasswordField newPasswordField; 
    @FXML private Label statusLabel;

    private StaffModel currentStaff;
    private AdminViewController adminViewController;

    public void setAdminViewController(AdminViewController adminViewController) {
        this.adminViewController = adminViewController;
    }

    public void loadStaffData(StaffModel staff) {
        this.currentStaff = staff;
        if (staff != null) {
            staffIdField.setText(String.valueOf(staff.getStaffID()));
            nameField.setText(staff.getName());
            roleField.setText(staff.getRole()); // Or roleComboBox.setValue(staff.getRole())
        }
         statusLabel.setText("");
    }

    @FXML
    private void handleUpdateStaff() {
        if (currentStaff == null) {
            showAlert(Alert.AlertType.ERROR, "Error", "No staff member selected for update.");
            return;
        }

        String name = nameField.getText();
        String role = roleField.getText(); 
        String username = usernameField.getText();

        if (name.isEmpty() || role.isEmpty() || username.isEmpty()) {
            statusLabel.setText("Name, Role, and Username are required.");
            return;
        }
        if (username.length() < 4) {
            statusLabel.setText("Username must be at least 4 characters.");
            return;
        }

        boolean detailsUpdated = SQL_Manager.updateStaff(currentStaff.getStaffID(), name, role, username);

        if (detailsUpdated) { 
            showAlert(Alert.AlertType.INFORMATION, "Success", "Staff member '" + name + "' updated successfully.");
            if (adminViewController != null) {
                adminViewController.refreshStaffList();
            }
            closeWindow();
        } else {
            showAlert(Alert.AlertType.ERROR, "Error", "Failed to update staff member. Username might already exist or no changes made.");
            statusLabel.setText("Failed to update staff. Check console for details.");
        }
    }

    @FXML
    private void handleCancel(ActionEvent event) {
        closeWindow();
    }

    private void closeWindow() {
        Stage stage = (Stage) staffIdField.getScene().getWindow();
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