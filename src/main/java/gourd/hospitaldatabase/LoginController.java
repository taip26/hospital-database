//package gourd.hospitaldatabase;
//
//import javafx.event.ActionEvent;
//import javafx.fxml.FXML;
//import javafx.fxml.FXMLLoader;
//import javafx.scene.Node;
//import javafx.scene.Parent;
//import javafx.scene.Scene;
//import javafx.scene.control.Alert;
//import javafx.scene.control.ComboBox;
//import javafx.scene.control.PasswordField;
//import javafx.scene.control.TextField;
//import javafx.stage.Stage;
//
//import java.io.IOException;
//
//public class LoginController {
//    @FXML
//    private TextField usernameField;
//
//    @FXML
//    private PasswordField passwordField;
//
//    @FXML
//    private void initialize() {
//    }
//
//    @FXML
//    private void handleLogin(ActionEvent event) {
//        String username = usernameField.getText();
//        String password = passwordField.getText();
//
//        if (username.isEmpty() || password.isEmpty()) {
//            showAlert("Error", "Please enter both username and password");
//            return;
//        }
//
//        boolean authenticated = false;
//
//        if ("Staff".equals(userType)) {
//            StaffModel staff = SQL_Manager.authenticateStaff(username, password);
//            if (staff != null) {
//                SessionManager.getInstance().setCurrentUser(staff, "STAFF");
//                authenticated = true;
//            }
//        } else {
//            AdministratorModel admin = SQL_Manager.authenticateAdmin(username, password);
//            if (admin != null) {
//                SessionManager.getInstance().setCurrentUser(admin, "ADMIN");
//                authenticated = true;
//            }
//        }
//
//        if (authenticated) {
//            try {
//                // Determine which view to load based on user type
//                String fxmlPath = "Staff".equals(userType) ? "staff-view.fxml" : "admin-view.fxml";
//
//                FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
//                Parent root = loader.load();
//
//                Scene scene = new Scene(root);
//                Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
//                stage.setScene(scene);
//                stage.show();
//            } catch (IOException e) {
//                e.printStackTrace();
//                showAlert("Error", "Could not load application: " + e.getMessage());
//            }
//        } else {
//            showAlert("Authentication Failed", "Invalid username or password");
//        }
//    }
//
//    private void showAlert(String title, String message) {
//        Alert alert = new Alert(Alert.AlertType.ERROR);
//        alert.setTitle(title);
//        alert.setHeaderText(null);
//        alert.setContentText(message);
//        alert.showAndWait();
//    }
//}