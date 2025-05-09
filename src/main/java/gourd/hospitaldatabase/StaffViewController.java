package gourd.hospitaldatabase;

import gourd.hospitaldatabase.pojos.Appointment;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalTime;

public class StaffViewController {
    private StaffModel currentStaff;

    @FXML
    private TableView<AppointmentModel> appointmentsTable;

    @FXML
    private TableColumn<AppointmentModel, Integer> appointmentIdColumn;

    @FXML
    private TableColumn<AppointmentModel, String> patientIdColumn;

    @FXML
    private TableColumn<AppointmentModel, String> staffIdColumn;

    @FXML
    private TableColumn<AppointmentModel, LocalDate> dateColumn;

    @FXML
    private TableColumn<AppointmentModel, LocalTime> timeColumn;

    @FXML
    private TableColumn<AppointmentModel, String> statusColumn;

    private ObservableList<AppointmentModel> appointmentsList = FXCollections.observableArrayList();

    @FXML
    private Label welcomeLabel;

    @FXML
    private TextField patientIdField;

    @FXML
    private TextField staffIdField;

    // Other controls for date, time, and status
    @FXML
    private DatePicker appointmentDatePicker;

    @FXML
    private TextField appointmentTimeField;  // expecting a string like "14:30:00"

    // Label to display status messages
    @FXML
    private Label statusLabel;

    public void initialize() {
        currentStaff = (StaffModel) SessionManager.getInstance().getCurrentUser();
        welcomeLabel.setText("Welcome, " + currentStaff.getName());

        appointmentIdColumn.setCellValueFactory(new PropertyValueFactory<>("appointmentID"));
        patientIdColumn.setCellValueFactory(new PropertyValueFactory<>("patientID"));
        staffIdColumn.setCellValueFactory(new PropertyValueFactory<>("staffID"));
        statusColumn.setCellValueFactory(new PropertyValueFactory<>("status"));
        dateColumn.setCellValueFactory(new PropertyValueFactory<>("visitDate"));
        timeColumn.setCellValueFactory(new PropertyValueFactory<>("visitTime"));

        // Set the items in the table
        appointmentsTable.setItems(appointmentsList);

        // Load appointments
        loadAppointments();
    }

    private void loadAppointments() {
        // Clear existing items
        appointmentsList.clear();

        // Get appointments from database
        ObservableList<AppointmentModel> appointments = SQL_Manager.getStaffAppointmentsById(currentStaff.getStaffID());

        // Add to the list
        appointmentsList.addAll(appointments);
    }

    @FXML
    public void refreshAppointments() {
        loadAppointments();
    }

    @FXML
    private void handleOpenCreateAppointment() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("create-appointment-view.fxml"));
            Parent root = loader.load();

            Stage stage = new Stage();
            stage.setTitle("Create Appointment");
            stage.setScene(new Scene(root));
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setResizable(false);

            // Set event handler for when the window closes
            stage.setOnHidden(event -> loadAppointments());

            stage.showAndWait();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    @FXML
    public void onLogoutClick(ActionEvent actionEvent) {
        SessionManager.getInstance().logout();
        MainController.navigateToLogin(actionEvent);
    }

    public void refreshAppointments(ActionEvent actionEvent) {
        loadAppointments();
    }

    @FXML
    private void handleOpenPatientSearch() {
    try {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("patient-search-view.fxml"));
        Parent root = loader.load();

        Stage stage = new Stage();
        stage.setTitle("Search Patients");
        stage.setScene(new Scene(root));
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.setResizable(false);
        stage.showAndWait();

    } catch (IOException e) {
        e.printStackTrace();
    }
}

    @FXML
    private void goToPatientSearchView(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("patient-search.fxml"));
            Parent root = loader.load();

            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("Patient Search");
            stage.show();

        } catch (IOException e) {
            e.printStackTrace();
            statusLabel.setText("Failed to load patient search view.");
        }
    }

    @FXML
    private void goToInventory(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("staff-inventory-view.fxml"));
            Parent root = loader.load();

            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("Inventory");
            stage.show();

        } catch (IOException e) {
            e.printStackTrace();
            statusLabel.setText("Failed to load patient search view.");
        }
    }
}
