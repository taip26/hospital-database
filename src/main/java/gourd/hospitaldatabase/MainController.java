package gourd.hospitaldatabase;

import javafx.fxml.FXML;
import javafx.scene.control.Label;

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
}