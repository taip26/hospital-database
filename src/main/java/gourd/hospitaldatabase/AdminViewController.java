package gourd.hospitaldatabase;

import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class AdminViewController {
    public void onBackButtonClick(ActionEvent actionEvent) {
        MainController.navigate_to_main(actionEvent);
    }
}
