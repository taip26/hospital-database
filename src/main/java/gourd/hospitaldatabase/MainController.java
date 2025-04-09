package gourd.hospitaldatabase;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.stage.Stage;

import java.io.IOException;

public class MainController {
    private final static String CONNECTION_URL = "jdbc:mysql://localhost:3307/hospital?user=root&password=password";

    @FXML
    private Label connectionMessage;

    @FXML
    protected void onConnectButtonClick() {
        String msg = SQL_Manager.connectToDatabase(CONNECTION_URL);
        connectionMessage.setText(msg);
    }

    @FXML
    protected void onCloseConnectionButtonClick() {
        String msg = SQL_Manager.closeConnection();
        connectionMessage.setText(msg);
    }

    @FXML
    public void onPatientButtonClick(ActionEvent actionEvent) {
        switchView(actionEvent, "patient-view.fxml");
    }

    @FXML
    public void onStaffButtonClick(ActionEvent actionEvent) {
        switchView(actionEvent, "staff-view.fxml");
    }

    @FXML
    public void onAdminButtonClick(ActionEvent actionEvent) {
        switchView(actionEvent, "admin-view.fxml");
    }

    private void switchView(ActionEvent actionEvent, String fxmlFile) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource(fxmlFile));
            Parent root = fxmlLoader.load();
            Stage stage = (Stage) ((Node) actionEvent.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}