package gourd.hospitaldatabase;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class PatientUpdateController {

    private PatientModel currentPatient;
//    @FXML private TextField patientIdField;
    @FXML private TextField dobField;
    @FXML private TextField nameField;
    @FXML private TextField addressField;
    @FXML private TextField insuranceField;

    public void loadPatient(PatientModel patient) {
        dobField.setText(patient.getDob().toString());
        nameField.setText(patient.getName());
        addressField.setText(patient.getAddress());
        insuranceField.setText(patient.getInsurance());
        currentPatient = patient;
    }

    @FXML
    private void onSaveChangesClick() {
        String dob = dobField.getText();
        String name = nameField.getText();
        String address = addressField.getText();
        String insurance = insuranceField.getText();

        boolean success = SQL_Manager.updatePatient(currentPatient.getPatientID(), dob, name, address, insurance);

        if (success) {
            Stage stage = (Stage) dobField.getScene().getWindow();
            stage.close();
        } else {
            showAlert("Update Failed", "Could not update patient information.");
        }
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    public void onCancelClick(ActionEvent actionEvent) {
        Stage stage = (Stage) dobField.getScene().getWindow();
        stage.close();
    }
}
