package gourd.hospitaldatabase;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import javafx.scene.Node;

import java.io.IOException;

public class PatientInsertController {

    @FXML private TextField dobField;
    @FXML private TextField nameField;
    @FXML private TextField addressField;
    @FXML private TextField insuranceField;

    @FXML
    private void onInsertPatientClick(ActionEvent event) {
        String dob = dobField.getText();
        String name = nameField.getText();
        String address = addressField.getText();
        String insurance = insuranceField.getText();

        boolean success = SQL_Manager.insertPatient(dob, name, address, insurance);

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("admin-view.fxml"));
            Parent root = loader.load();
            Scene scene = new Scene(root, AppConstants.WINDOW_WIDTH, AppConstants.WINDOW_HEIGHT);
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            showAlert("Error", "Could not insert patient information. " + e.getMessage());
        }
    }

    @FXML
    public void onCancelClick(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("admin-view.fxml"));
            Parent root = loader.load();
            Scene scene = new Scene(root, AppConstants.WINDOW_WIDTH, AppConstants.WINDOW_HEIGHT);
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            showAlert("Error", "Could not load application: " + e.getMessage());
        }
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
