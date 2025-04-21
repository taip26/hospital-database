package gourd.hospitaldatabase;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.TextField;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;

public class AdminViewController {

    @FXML
    private TextField patientIdField;
    @FXML
    private TextField searchPatientIdField;

    public void onBackButtonClick(ActionEvent actionEvent) {
        MainController.navigate_to_main(actionEvent);
    }

    @FXML
    public void onOpenAddBillModal(ActionEvent actionEvent) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("add-bill-modal.fxml"));
            Parent modalRoot = fxmlLoader.load();

            Stage modalStage = new Stage();
            modalStage.initModality(Modality.APPLICATION_MODAL);
            modalStage.setTitle("Create Medical Bill");
            modalStage.setScene(new Scene(modalRoot));
            modalStage.showAndWait();
        } catch (IOException e) {
            e.printStackTrace();
        }
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

    @FXML
    public void onSearchPatientClick(ActionEvent event) {
        String patientId = searchPatientIdField.getText();

        if (patientId == null || patientId.isBlank()) {
            System.out.println("Please enter a Patient ID.");
            return;
        }

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("patient-update.fxml"));
            Parent root = loader.load();
            PatientUpdateController controller = loader.getController();
            controller.loadPatientById(patientId);

            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void onInsertPatientClick() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("patient-insert.fxml"));
            Parent root = loader.load();

            Stage stage = (Stage) patientIdField.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
