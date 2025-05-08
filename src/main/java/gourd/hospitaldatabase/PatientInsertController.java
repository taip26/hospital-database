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

public class PatientInsertController {

    @FXML private TextField dobField;
    @FXML private TextField nameField;
    @FXML private TextField addressField;
    @FXML private TextField insuranceField;

    @FXML
    private void onInsertPatientClick() {
        String dob = dobField.getText();
        String name = nameField.getText();
        String address = addressField.getText();
        String insurance = insuranceField.getText();

        boolean success = SQL_Manager.insertPatient(dob, name, address, insurance);

        if (success) {
            Stage stage = (Stage) dobField.getScene().getWindow();
            stage.close();
        } else {
            showAlert("Insert Failed", "Could not insert patient information.");
        }
    }

    @FXML
    public void onCancelClick(ActionEvent event) {
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.close();
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
