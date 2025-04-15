package gourd.hospitaldatabase;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.IOException;

public class AdminViewController {

    @FXML
    private TextField patientIdField;

    public void onBackButtonClick(ActionEvent actionEvent) {
        MainController.navigate_to_main(actionEvent);
    }

    @FXML
    public void onDeletePatientClick(ActionEvent event) {
        try {
            int patientId = Integer.parseInt(patientIdField.getText());
            boolean success = SQL_Manager.deletePatientById(patientId);

            if (success) {
                System.out.println("Patient with ID " + patientId + " deleted.");
            } else {
                System.out.println("Delete failed. Patient ID not found.");
            }
        } catch (NumberFormatException e) {
            System.out.println("Invalid Patient ID format.");
        }
    }
}
