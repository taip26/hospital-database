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
import javafx.scene.control.TextInputDialog;
import java.util.Optional;

public class AdminViewController {
    private AdministratorModel currentAdmin;

    @FXML public Label welcomeLabel;

    // Patient Management Controls
    @FXML private TextField patientSearchField;
    @FXML private ListView<PatientModel> patientListView;
    @FXML private Button updatePatientButton;
    @FXML private Button deletePatientButton;

    // Staff Management Controls - NEW
    @FXML private TextField staffSearchField;
    @FXML private ListView<StaffModel> staffListView;
    @FXML private Button updateStaffButton;
    @FXML private Button deleteStaffButton;
    @FXML private Button addNewStaffButton; 
    @FXML private Button resetStaffPasswordButton;

    private ObservableList<PatientModel> allPatientsList;
    private ObservableList<PatientModel> filteredPatientsList;

    private ObservableList<StaffModel> allStaffList; // NEW
    private ObservableList<StaffModel> filteredStaffList; // NEW


    @FXML
    public void initialize() {
        currentAdmin = (AdministratorModel) SessionManager.getInstance().getCurrentUser();
        if (currentAdmin != null) {
            welcomeLabel.setText("Welcome, " + currentAdmin.getName() + " (Admin)");
        } else {
            welcomeLabel.setText("Welcome, Admin (User not fully loaded)");
        }
        setupPatientManagementSection();
        setupStaffManagementSection(); // NEW
    }

    // --- Patient Management Methods (from previous refactor) ---
    private void setupPatientManagementSection() {
        allPatientsList = FXCollections.observableArrayList();
        filteredPatientsList = FXCollections.observableArrayList();
        patientListView.setItems(filteredPatientsList);

        patientListView.setCellFactory(lv -> new ListCell<PatientModel>() {
            @Override
            protected void updateItem(PatientModel patient, boolean empty) {
                super.updateItem(patient, empty);
                setText(empty || patient == null ? null : patient.toString());
            }
        });

        patientSearchField.textProperty().addListener((observable, oldValue, newValue) -> filterPatientList(newValue));
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
        if (patientsFromDB != null) {
            allPatientsList.addAll(patientsFromDB);
        }
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
    public void onUpdateSelectedPatientClick(ActionEvent event) {
        PatientModel selectedPatient = patientListView.getSelectionModel().getSelectedItem();
        if (selectedPatient == null) {
            showAlert(Alert.AlertType.WARNING, "No Selection", "Please select a patient to update.");
            return;
        }
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("patient-update.fxml"));
            Parent root = loader.load();
            PatientUpdateController controller = loader.getController();
            controller.loadPatient(selectedPatient);
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("Update Patient: " + selectedPatient.getName());
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Load Error", "Could not load patient update screen.");
        }
    }

    @FXML
    public void onDeleteSelectedPatientClick(ActionEvent event) {
        PatientModel selectedPatient = patientListView.getSelectionModel().getSelectedItem();
        if (selectedPatient == null) {
            showAlert(Alert.AlertType.WARNING, "No Selection", "Please select a patient to delete.");
            return;
        }
        Alert confirmationDialog = new Alert(Alert.AlertType.CONFIRMATION,
                "Delete patient: " + selectedPatient.getName() + "?", ButtonType.YES, ButtonType.NO);
        confirmationDialog.setTitle("Confirm Deletion");
        confirmationDialog.setHeaderText(null);
        Optional<ButtonType> result = confirmationDialog.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.YES) {
            boolean success = SQL_Manager.deletePatientById(selectedPatient.getPatientID());
            if (success) {
                showAlert(Alert.AlertType.INFORMATION, "Success", "Patient deleted.");
                loadAllPatients();
            } else {
                showAlert(Alert.AlertType.ERROR, "Deletion Failed", "Could not delete patient.");
            }
        }
    }

    @FXML
    private void onInsertPatientClick() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("patient-insert.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) welcomeLabel.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("Insert New Patient");
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Load Error", "Could not load 'Insert Patient' screen.");
        }
    }


    // --- Staff Management Methods - NEW ---
    private void setupStaffManagementSection() {
        allStaffList = FXCollections.observableArrayList();
        filteredStaffList = FXCollections.observableArrayList();
        staffListView.setItems(filteredStaffList);

        staffListView.setCellFactory(lv -> new ListCell<StaffModel>() {
            @Override
            protected void updateItem(StaffModel staff, boolean empty) {
                super.updateItem(staff, empty);
                setText(empty || staff == null ? null : staff.toString());
            }
        });

        staffSearchField.textProperty().addListener((observable, oldValue, newValue) -> filterStaffList(newValue));
        staffListView.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newSelection) -> {
            boolean disabled = (newSelection == null);
            updateStaffButton.setDisable(disabled);
            deleteStaffButton.setDisable(disabled);
            resetStaffPasswordButton.setDisable(disabled);
        });
        updateStaffButton.setDisable(true);
        deleteStaffButton.setDisable(true);
        resetStaffPasswordButton.setDisable(true);
        loadAllStaff();
    }

    private void loadAllStaff() {
        List<StaffModel> staffFromDB = SQL_Manager.getAllStaff();
        allStaffList.clear();
        if (staffFromDB != null) {
            allStaffList.addAll(staffFromDB);
        }
        filterStaffList(staffSearchField.getText());
    }

    // Public method to be called from modal controllers after add/update
    public void refreshStaffList() {
        loadAllStaff();
    }


    private void filterStaffList(String filter) {
        filteredStaffList.clear();
        if (filter == null || filter.isEmpty()) {
            filteredStaffList.addAll(allStaffList);
        } else {
            String lowerCaseFilter = filter.toLowerCase();
            for (StaffModel staff : allStaffList) {
                if (staff.getName().toLowerCase().contains(lowerCaseFilter) ||
                    String.valueOf(staff.getStaffID()).contains(lowerCaseFilter) ||
                    staff.getRole().toLowerCase().contains(lowerCaseFilter)) {
                    filteredStaffList.add(staff);
                }
            }
        }
    }

    @FXML
    private void onAddNewStaffClick(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("add-staff-modal.fxml"));
            Parent root = loader.load();

            AddStaffModalController controller = loader.getController();
            controller.setAdminViewController(this); // Pass reference for refresh

            Stage modalStage = new Stage();
            modalStage.initModality(Modality.APPLICATION_MODAL);
            modalStage.initOwner(((Node) event.getSource()).getScene().getWindow());
            modalStage.setTitle("Add New Staff Member");
            modalStage.setScene(new Scene(root));
            modalStage.showAndWait();
            // Refresh is handled by the modal controller calling refreshStaffList()
        } catch (IOException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Load Error", "Could not open 'Add Staff' window.");
        }
    }

    @FXML
    private void onUpdateSelectedStaffClick(ActionEvent event) {
        StaffModel selectedStaff = staffListView.getSelectionModel().getSelectedItem();
        if (selectedStaff == null) {
            showAlert(Alert.AlertType.WARNING, "No Selection", "Please select a staff member to update.");
            return;
        }

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("update-staff-modal.fxml"));
            Parent root = loader.load();

            UpdateStaffModalController controller = loader.getController();
            controller.setAdminViewController(this); // Pass reference for refresh
            controller.loadStaffData(selectedStaff); // Pass selected staff data to modal

            Stage modalStage = new Stage();
            modalStage.initModality(Modality.APPLICATION_MODAL);
            modalStage.initOwner(((Node) event.getSource()).getScene().getWindow());
            modalStage.setTitle("Update Staff: " + selectedStaff.getName());
            modalStage.setScene(new Scene(root));
            modalStage.showAndWait();
            // Refresh handled by modal
        } catch (IOException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Load Error", "Could not load 'Update Staff' window.");
        }
    }

    @FXML
    private void onDeleteSelectedStaffClick(ActionEvent event) {
        StaffModel selectedStaff = staffListView.getSelectionModel().getSelectedItem();
        if (selectedStaff == null) {
            showAlert(Alert.AlertType.WARNING, "No Selection", "Please select a staff member to delete.");
            return;
        }

        Alert confirmationDialog = new Alert(Alert.AlertType.CONFIRMATION,
                "Delete staff member: " + selectedStaff.getName() + "?", ButtonType.YES, ButtonType.NO);
        confirmationDialog.setTitle("Confirm Deletion");
        confirmationDialog.setHeaderText(null);
        Optional<ButtonType> result = confirmationDialog.showAndWait();

        if (result.isPresent() && result.get() == ButtonType.YES) {
            boolean success = SQL_Manager.deleteStaff(selectedStaff.getStaffID());
            if (success) {
                showAlert(Alert.AlertType.INFORMATION, "Success", "Staff member deleted.");
                loadAllStaff(); // Refresh list
            } else {
                showAlert(Alert.AlertType.ERROR, "Deletion Failed", "Could not delete staff member. They might be linked to other records.");
            }
        }
    }

    // --- Other Admin Methods (Bills, Reports, Logout) ---
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
            showAlert(Alert.AlertType.ERROR, "Load Error", "Could not open 'Add Bill' window.");
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
            showAlert(Alert.AlertType.ERROR, "Load Error", "Could not open 'Update Bill' window.");
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
            showAlert(Alert.AlertType.ERROR, "Load Error", "Could not load 'Report View' screen.");
        }
    }

    @FXML
    public void onLogoutClick(ActionEvent actionEvent) {
        SessionManager.getInstance().logout();
        navigateToLoginScreen(actionEvent);
    }

    @FXML
    private void onChangeMyPasswordClick(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("change-password-modal.fxml"));
            Parent root = loader.load();

            Stage modalStage = new Stage();
            modalStage.initModality(Modality.APPLICATION_MODAL);
            // Get the current stage to set the owner of the modal
            if (event.getSource() instanceof Node) {
                modalStage.initOwner(((Node) event.getSource()).getScene().getWindow());
            } else if (welcomeLabel != null && welcomeLabel.getScene() != null) { // Fallback
                modalStage.initOwner(welcomeLabel.getScene().getWindow());
            }
            modalStage.setTitle("Change My Password");
            modalStage.setScene(new Scene(root));
            modalStage.showAndWait();
        } catch (IOException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Load Error", "Could not open the 'Change Password' window.");
        }
    }

   
    @FXML
    private void onResetSelectedStaffPasswordClick(ActionEvent event) {
            StaffModel selectedStaff = staffListView.getSelectionModel().getSelectedItem();
            if (selectedStaff == null) {
            showAlert(Alert.AlertType.WARNING, "No Selection", "Please select a staff member to reset their password.");
            return;
        }

        TextInputDialog dialog = new TextInputDialog(); 
        dialog.setTitle("Reset Staff Password");
        dialog.setHeaderText("Reset password for: " + selectedStaff.getName() + " (ID: " + selectedStaff.getStaffID() + ")");
        dialog.setContentText("Enter new password:");

        Optional<String> result = dialog.showAndWait();
        result.ifPresent(newPassword -> {
            if (newPassword.trim().isEmpty() || newPassword.trim().length() < 6) { 
                showAlert(Alert.AlertType.ERROR, "Invalid Password", "New password must be at least 6 characters.");
                return;
            }
            boolean success = SQL_Manager.updateStaffPassword(selectedStaff.getStaffID(), newPassword.trim());
            if (success) {
                showAlert(Alert.AlertType.INFORMATION, "Success", "Password for " + selectedStaff.getName() + " has been reset.");
            } else {
                showAlert(Alert.AlertType.ERROR, "Error", "Failed to reset password for " + selectedStaff.getName() + ".");
            }
        });
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