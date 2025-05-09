package gourd.hospitaldatabase;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

public class AdminViewController {
    private AdministratorModel currentAdmin;

    @FXML
    public Label welcomeLabel;

    @FXML
    private TextField patientSearchField;
    @FXML
    private ListView<PatientModel> patientListView;
    @FXML
    private Button updatePatientButton;
    @FXML
    private Button deletePatientButton;

    private ObservableList<PatientModel> allPatientsList;
    private ObservableList<PatientModel> filteredPatientsList;

    @FXML
    public void initialize() {
        currentAdmin = (AdministratorModel) SessionManager.getInstance().getCurrentUser();
        if (currentAdmin != null) {
            welcomeLabel.setText("Welcome, " + currentAdmin.getName() + " (Admin)");
        } else {
            welcomeLabel.setText("Welcome, Admin (User not fully loaded)");
        }
        setupPatientManagementSection();
    }

    private void setupPatientManagementSection() {
        allPatientsList = FXCollections.observableArrayList();
        filteredPatientsList = FXCollections.observableArrayList();

        patientListView.setItems(filteredPatientsList);

        patientListView.setCellFactory(lv -> new ListCell<PatientModel>() {
            @Override
            protected void updateItem(PatientModel patient, boolean empty) {
                super.updateItem(patient, empty);
                if (empty || patient == null) {
                    setText(null);
                } else {
                    setText(patient.toString());
                }
            }
        });

        patientSearchField.textProperty().addListener((observable, oldValue, newValue) -> {
            filterPatientList(newValue);
        });

        patientListView.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newSelection) -> {
            boolean disabled = (newSelection == null);
            updatePatientButton.setDisable(disabled);
            deletePatientButton.setDisable(disabled);
        });

        updatePatientButton.setDisable(true);
        deletePatientButton.setDisable(true);

        loadAllPatients();
    }

    private void loadAllPatients() {
        List<PatientModel> patientsFromDB = SQL_Manager.getAllPatients();
        allPatientsList.clear();
        allPatientsList.addAll(patientsFromDB);
        filterPatientList(patientSearchField.getText());
    }

    private void filterPatientList(String filter) {
        filteredPatientsList.clear();
        if (filter == null || filter.isEmpty()) {
            filteredPatientsList.addAll(allPatientsList);
        } else {
            String lowerCaseFilter = filter.toLowerCase();
            for (PatientModel patient : allPatientsList) {
                if (patient.getName().toLowerCase().contains(lowerCaseFilter) ||
                        String.valueOf(patient.getPatientID()).contains(lowerCaseFilter)) {
                    filteredPatientsList.add(patient);
                }
            }
        }
    }

    @FXML
    public void onLogoutClick(ActionEvent actionEvent) {
        SessionManager.getInstance().logout();
        navigateToLoginScreen(actionEvent);
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
            showAlert(Alert.AlertType.ERROR, "Load Error", "Could not open the 'Add Bill' window.");
        }
    }

    @FXML
    public void onOpenUpdateBillModal(ActionEvent actionEvent) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("update-bill-modal.fxml"));
            Parent modalRoot = fxmlLoader.load();

            Stage modalStage = new Stage();
            modalStage.initModality(Modality.APPLICATION_MODAL);
            modalStage.setTitle("Update/Delete Medical Bill");
            modalStage.setScene(new Scene(modalRoot));
            modalStage.showAndWait();
        } catch (IOException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Load Error", "Could not open the 'Update Bill' window.");
        }
    }

    @FXML
    public void onDeleteSelectedPatientClick(ActionEvent event) {
        PatientModel selectedPatient = patientListView.getSelectionModel().getSelectedItem();
        if (selectedPatient == null) {
            showAlert(Alert.AlertType.WARNING, "No Selection", "Please select a patient from the list to delete.");
            return;
        }

        Alert confirmationDialog = new Alert(Alert.AlertType.CONFIRMATION,
                "Are you sure you want to delete patient: " + selectedPatient.getName() + " (ID: " + selectedPatient.getPatientID() + ")?",
                ButtonType.YES, ButtonType.NO);
        confirmationDialog.setTitle("Confirm Deletion");
        confirmationDialog.setHeaderText(null);
        Optional<ButtonType> result = confirmationDialog.showAndWait();

        if (result.isPresent() && result.get() == ButtonType.YES) {
            boolean success = SQL_Manager.deletePatientById(selectedPatient.getPatientID());
            if (success) {
                showAlert(Alert.AlertType.INFORMATION, "Success", "Patient " + selectedPatient.getName() + " deleted successfully.");
                loadAllPatients();
            } else {
                showAlert(Alert.AlertType.ERROR, "Deletion Failed", "Could not delete patient " + selectedPatient.getName() + ".");
            }
        }
    }

    @FXML
    public void onUpdateSelectedPatientClick(ActionEvent event) {
        PatientModel selectedPatient = patientListView.getSelectionModel().getSelectedItem();
        if (selectedPatient == null) {
            showAlert(Alert.AlertType.WARNING, "No Selection", "Please select a patient from the list to update.");
            return;
        }

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("patient-update.fxml"));
            Parent root = loader.load();
            PatientUpdateController controller = loader.getController();
            controller.loadPatient(selectedPatient);

            Stage stage = new Stage();
            stage.setTitle("Update Patient: " + selectedPatient.getName());
            stage.setScene(new Scene(root));
            stage.initModality(Modality.APPLICATION_MODAL); // Makes it a modal pop-up.
            stage.setResizable(false);
            stage.showAndWait(); // Wait for the window to close before returning.
            loadAllPatients();
        } catch (IOException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Load Error", "Could not load the patient update screen.");
        }
    }

    @FXML
    private void onInsertPatientClick() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("patient-insert.fxml"));
            Parent root = loader.load();

            Stage stage = new Stage();
            stage.setTitle("Insert Patient");
            stage.setScene(new Scene(root));
            stage.initModality(Modality.APPLICATION_MODAL); // Makes it a modal pop-up.
            stage.setResizable(false);
            stage.showAndWait(); // Wait for the window to close before returning.
            loadAllPatients();
        } catch (Exception e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Load Error", "Could not load the 'Insert Patient' screen.");
        }
    }

    @FXML
    private void onViewReportsClick() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("report-view.fxml"));
            Stage stage = new Stage();
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setTitle("Report Viewer");
            stage.setScene(new Scene(loader.load()));
            stage.showAndWait();
        } catch (IOException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Load Error", "Could not load the 'Report View' screen.");
        }
    }

    private void navigateToLoginScreen(ActionEvent actionEvent) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("login-view.fxml"));
            Parent root = fxmlLoader.load();
            Stage stage = (Stage) ((Node) actionEvent.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root, AppConstants.WINDOW_WIDTH, AppConstants.WINDOW_HEIGHT));
            stage.setTitle(AppConstants.APP_TITLE);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("Failed to navigate to login screen: " + e.getMessage());
        }
    }

    private void showAlert(Alert.AlertType alertType, String title, String message) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    public void onViewReportVisualizationsClick(ActionEvent actionEvent) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("report-visualizations-view.fxml"));
            Stage stage = new Stage();
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setTitle("Report Viewer");
            stage.setScene(new Scene(loader.load(), AppConstants.WINDOW_WIDTH + 100, AppConstants.WINDOW_HEIGHT + 100));
            stage.showAndWait();
        } catch (IOException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Load Error", "Could not load the 'Report View' screen.");
        }
    }
}