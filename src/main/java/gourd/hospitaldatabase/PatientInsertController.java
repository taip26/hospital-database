package gourd.hospitaldatabase;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import javafx.scene.Node;

public class PatientInsertController {

    @FXML private TextField dobField;
    @FXML private TextField nameField;
    @FXML private TextField addressField;
    @FXML private TextField insuranceField;

    @FXML
    private void onInsertPatientClick() {
        int patientId = SQL_Manager.getNextAvailablePatientId();
        String dob = dobField.getText();
        String name = nameField.getText();
        String address = addressField.getText();
        String insurance = insuranceField.getText();

        boolean success = SQL_Manager.insertPatient(patientId, dob, name, address, insurance);

        if (success) {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("admin-view.fxml"));
                Parent root = loader.load();

                Stage stage = (Stage) dobField.getScene().getWindow();
                stage.setScene(new Scene(root));
                stage.show();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @FXML
    private void onBackButtonClick(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("admin-view.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }    
}
