package gourd.hospitaldatabase;

import gourd.hospitaldatabase.pojos.Inventory;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;
import javafx.scene.Node;
import javafx.event.ActionEvent;

import java.io.IOException;
import java.util.List;

public class StaffInventoryController {

    @FXML private TextField inventorySearchField;
    @FXML private TableView<Inventory> inventoryTable;
    @FXML private TableColumn<Inventory, String> nameColumn;
    @FXML private TableColumn<Inventory, String> statusColumn;
    @FXML private TableColumn<Inventory, String> locationColumn;
    @FXML private Label statusLabel;

    private final ObservableList<Inventory> data = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        nameColumn.setCellValueFactory(cell ->
                new ReadOnlyStringWrapper(cell.getValue().getName()));
        statusColumn.setCellValueFactory(cell ->
                new ReadOnlyStringWrapper(cell.getValue().getStatus()));
        locationColumn.setCellValueFactory(cell ->
                new ReadOnlyStringWrapper(cell.getValue().getLocation()));
        TableColumn<Inventory, Void> claimCol = new TableColumn<>("Action");
        claimCol.setPrefWidth(120);

        claimCol.setCellFactory(col -> new TableCell<>() {
            private final Button btn = new Button("Claim Item");

            {
                btn.setOnAction(e -> {
                    Inventory item = getTableView().getItems().get(getIndex());
                    boolean ok = SQL_Manager.updateInventoryStatus(item.getItemId(), "In Use");
                    if (ok) {
                        // update the model and refresh the table
                        item.setStatus("In Use");
                        getTableView().refresh();
                    } else {
                        // optionally show an alert
                        Alert alert = new Alert(Alert.AlertType.ERROR,
                                "Failed to claim item " + item.getItemId());
                        alert.showAndWait();
                    }
                });
            }

            @Override
            protected void updateItem(Void v, boolean empty) {
                super.updateItem(v, empty);

                if (empty) {
                    setGraphic(null);
                } else {
                    Inventory item = getTableView().getItems().get(getIndex());
                    // only show button when status is exactly "available"
                    if ("Available".equalsIgnoreCase(item.getStatus())) {
                        setGraphic(btn);
                    } else {
                        setGraphic(null);
                    }
                }
            }
        });

        // 2) add it to your table
        inventoryTable.getColumns().add(claimCol);

        inventoryTable.setItems(data);

        // 3) continue with loading data…
        loadAll();
    }

    private void loadAll() {
        List<Inventory> all = SQL_Manager.getAllInventory();
        data.setAll(all);
        statusLabel.setText(all.size() + " item(s) loaded.");
    }

    @FXML
    private void handleSearch() {
        String term = inventorySearchField.getText().trim();
        List<Inventory> results;

        if (term.isEmpty()) {
            results = SQL_Manager.getAllInventory();
        } else {
            results = SQL_Manager.searchInventoryByName(term);
        }

        data.setAll(results);  // updates the TableView

        if (results.isEmpty()) {
            statusLabel.setText("No items found matching \"" + term + "\".");
        } else {
            statusLabel.setText(results.size() + " item(s) found.");
        }
    }

    @FXML
    private void handleReturn(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("staff-view.fxml"));
            Parent root = loader.load();

            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("Staff View");
            stage.show();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}