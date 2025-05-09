package gourd.hospitaldatabase;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import javafx.scene.Node;
import javafx.event.ActionEvent;

import java.io.IOException;

public class PatientDetailsController {

    @FXML private TextField idField;
    @FXML private TextField nameField;
    @FXML private TextField dobField;
    @FXML private TextField addressField;
    @FXML private TextField insuranceField;

    private PatientModel patient;

    @FXML
    public void initialize() {
        int patientId = SessionManager.getInstance().getSelectedPatientId();

        if (patientId <= 0) {
            System.err.println("Invalid patient ID from session.");
            return;
        }

        patient = SQL_Manager.getPatientById(patientId);
        if (patient == null) {
            System.err.println("Patient not found with ID: " + patientId);
            return;
        }

        idField.setText(String.valueOf(patient.getPatientID()));
        nameField.setText(patient.getName());
        dobField.setText(patient.getDob().toString());
        addressField.setText(patient.getAddress());
        insuranceField.setText(patient.getInsurance());
    }

    @FXML
    private void handleReturn(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("staff-view.fxml"));
            Parent root = loader.load();

            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("Staff View");
            stage.show();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
