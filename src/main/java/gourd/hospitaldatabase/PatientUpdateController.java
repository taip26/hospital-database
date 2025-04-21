package gourd.hospitaldatabase;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class PatientUpdateController {

    @FXML private TextField patientIdField;
    @FXML private TextField dobField;
    @FXML private TextField nameField;
    @FXML private TextField addressField;
    @FXML private TextField insuranceField;

    public void loadPatientById(String patientId) {
        try (Connection conn = SQL_Manager.getConnection()) {
            String query = "SELECT * FROM patients WHERE PatientID = ?";
            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setInt(1, Integer.parseInt(patientId));
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                patientIdField.setText(String.valueOf(rs.getInt("PatientID")));
                dobField.setText(rs.getString("dob"));
                nameField.setText(rs.getString("Name"));
                addressField.setText(rs.getString("Address"));
                insuranceField.setText(rs.getString("Insurance"));
            } else {
                System.out.println("Patient ID not found.");
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void onSaveChangesClick() {
        int patientId = Integer.parseInt(patientIdField.getText());
        String dob = dobField.getText();
        String name = nameField.getText();
        String address = addressField.getText();
        String insurance = insuranceField.getText();

        boolean success = SQL_Manager.updatePatient(patientId, dob, name, address, insurance);

        if (success) {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("admin-view.fxml"));
                Parent root = loader.load();

                Stage stage = (Stage) patientIdField.getScene().getWindow();
                stage.setScene(new Scene(root));
                stage.show();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

}
