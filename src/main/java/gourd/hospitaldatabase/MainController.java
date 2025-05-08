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
    private final static String CONNECTION_URL = "jdbc:mysql://localhost:3306/hospital?user=root&password=password-123";

    @FXML
    private Label connectionMessage;

    @FXML
    protected void onConnectButtonClick() {
        String msg = SQL_Manager.connectToDatabase();
        connectionMessage.setText(msg);
    }

    @FXML
    protected void onCloseConnectionButtonClick() {
        String msg = SQL_Manager.closeConnection();
        connectionMessage.setText(msg);
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
            stage.setScene(new Scene(root, AppConstants.WINDOW_WIDTH, AppConstants.WINDOW_HEIGHT));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void navigateToLogin(ActionEvent actionEvent) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(MainController.class.getResource("login-view.fxml"));
            Parent root = fxmlLoader.load();
            Stage stage = (Stage) ((Node) actionEvent.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root, AppConstants.WINDOW_WIDTH, AppConstants.WINDOW_HEIGHT));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}