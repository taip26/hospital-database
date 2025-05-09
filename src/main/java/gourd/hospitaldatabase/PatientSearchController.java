package gourd.hospitaldatabase;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import javafx.scene.Node;
import javafx.event.ActionEvent;

import java.io.IOException;
import java.util.List;

public class PatientSearchController {

    @FXML
    private TextField patientSearchField;

    @FXML
    private ListView<String> patientListView;

    @FXML
    private Label statusLabel;

    private List<PatientModel> patients;

    @FXML
public void initialize() {
    loadPatients();

    patientSearchField.textProperty().addListener((observable, oldValue, newValue) -> {
        updatePatientList(newValue);
    });
}


    private void loadPatients() {
        patients = SQL_Manager.getAllPatients();

        patientListView.getItems().clear();
        for (PatientModel patient : patients) {
            patientListView.getItems().add(patient.getPatientID() + " - " + patient.getName());
        }
    }

    @FXML
    private void handleSearch() {
        String selectedItem = patientListView.getSelectionModel().getSelectedItem();

        if (selectedItem == null) {
            statusLabel.setText("Please select a patient from the list.");
            return;
        }

        try {
            // Extract ID from "ID - Name"
            int patientId = Integer.parseInt(selectedItem.split(" - ")[0]);
            SessionManager.getInstance().setSelectedPatientId(patientId);

            FXMLLoader loader = new FXMLLoader(getClass().getResource("patient-details.fxml"));
            Parent root = loader.load();

            Stage stage = new Stage();
            stage.setTitle("Patient Details");
            stage.setScene(new Scene(root));
            stage.show();

            // Optionally close this window
            ((Stage) statusLabel.getScene().getWindow()).close();

        } catch (IOException | NumberFormatException e) {
            e.printStackTrace();
            statusLabel.setText("Failed to open patient details.");
        }
    }

    @FXML
    private void handleBack(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("staff-view.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            statusLabel.setText("Failed to return to staff view.");
        }
    }

    private void updatePatientList(String searchText) {
        patientListView.getItems().clear();
    
        for (PatientModel patient : patients) {
            if (searchText == null || searchText.isEmpty()
                || patient.getName().toLowerCase().contains(searchText.toLowerCase())
                || String.valueOf(patient.getPatientID()).contains(searchText)) {
    
                patientListView.getItems().add(patient.getPatientID() + " - " + patient.getName());
            }
        }
    }
    
}
