package gourd.hospitaldatabase;

import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;

public class AdminViewController {
    public void onBackButtonClick(ActionEvent actionEvent) {
        MainController.navigate_to_main(actionEvent);
    }

    public void onOpenAddBillModal(ActionEvent actionEvent) {
        try {
            // Load the FXML file for the modal
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("add-bill-modal.fxml"));
            Parent modalRoot = fxmlLoader.load();

            // Create a new stage for the modal
            Stage modalStage = new Stage();
            modalStage.initModality(Modality.APPLICATION_MODAL); // Block interaction with other windows
            modalStage.setTitle("Create Medical Bill");
            modalStage.setScene(new Scene(modalRoot));
            modalStage.showAndWait(); // Wait for the modal to close before returning
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
