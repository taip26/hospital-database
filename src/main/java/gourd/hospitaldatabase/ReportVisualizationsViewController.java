package gourd.hospitaldatabase;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.layout.AnchorPane;

import java.awt.*;
import java.util.List;

public class ReportVisualizationsViewController {
    @FXML
    private AnchorPane chartContainer;

    public void initialize() {
        // Initialize the chart container
        chartContainer.getChildren().clear();
    }

    @FXML
    public void onViewMedicalBillStatusReportClick() {
        // Fetch data
        List<MedicalBillModel> bills = SQL_Manager.getAllMedicalBills();
        if (bills == null || bills.isEmpty()) {
            System.err.println("No medical bills found!");
            return;
        }
        System.out.println("Medical Bills: " + bills);

        // Generate the report
        AnchorPane reportPane = ReportUtils.createMedicalBillStatusReport(bills);
        if (reportPane == null) {
            System.err.println("Failed to generate report pane!");
            return;
        }

        // Update the UI
        chartContainer.getChildren().clear();
        chartContainer.getChildren().add(reportPane);

        // Ensure the chart fills the container
        AnchorPane.setTopAnchor(reportPane, 0.0);
        AnchorPane.setBottomAnchor(reportPane, 0.0);
        AnchorPane.setLeftAnchor(reportPane, 0.0);
        AnchorPane.setRightAnchor(reportPane, 0.0);
    }

    @FXML
    public void onViewStaffPerformanceReportClick() {
        // Fetch data
        List<AppointmentModel> appointments = SQL_Manager.getAllAppointmentsList();
        List<StaffModel> staffList = SQL_Manager.getStaffList();
        if (appointments == null || staffList == null || appointments.isEmpty() || staffList.isEmpty()) {
            System.err.println("No appointments or staff found!");
            return;
        }

        // Generate the report
        AnchorPane reportPane = ReportUtils.createStaffPerformanceReport(appointments, staffList);
        if (reportPane == null) {
            System.err.println("Failed to generate report pane!");
            return;
        }

        // Update the UI
        chartContainer.getChildren().clear();
        chartContainer.getChildren().add(reportPane);

        // Ensure the chart fills the container
        AnchorPane.setTopAnchor(reportPane, 0.0);
        AnchorPane.setBottomAnchor(reportPane, 0.0);
        AnchorPane.setLeftAnchor(reportPane, 0.0);
        AnchorPane.setRightAnchor(reportPane, 0.0);
    }

    @FXML
    public void onViewFinancialReportClick() {
        // Fetch data
        List<MedicalBillModel> bills = SQL_Manager.getAllMedicalBills();
        if (bills == null || bills.isEmpty()) {
            System.err.println("No financial data found!");
            return;
        }

        // Generate the report
        AnchorPane reportPane = ReportUtils.createFinancialReport(bills);
        if (reportPane == null) {
            System.err.println("Failed to generate financial report pane!");
            return;
        }

        // Update the UI
        chartContainer.getChildren().clear();
        chartContainer.getChildren().add(reportPane);

        // Ensure the chart fills the container
        AnchorPane.setTopAnchor(reportPane, 0.0);
        AnchorPane.setBottomAnchor(reportPane, 0.0);
        AnchorPane.setLeftAnchor(reportPane, 0.0);
        AnchorPane.setRightAnchor(reportPane, 0.0);
    }

    @FXML
    public void onViewPatientInsuranceReportClick() {
        // Fetch data
        List<PatientModel> patients = SQL_Manager.getAllPatients();
        if (patients == null || patients.isEmpty()) {
            System.err.println("No patient data found!");
            return;
        }

        // Generate the report
        AnchorPane reportPane = ReportUtils.createPatientInsuranceReport(patients);
        if (reportPane == null) {
            System.err.println("Failed to generate patient insurance report pane!");
            return;
        }

        // Update the UI
        chartContainer.getChildren().clear();
        chartContainer.getChildren().add(reportPane);

        // Ensure the chart fills the container
        AnchorPane.setTopAnchor(reportPane, 0.0);
        AnchorPane.setBottomAnchor(reportPane, 0.0);
        AnchorPane.setLeftAnchor(reportPane, 0.0);
        AnchorPane.setRightAnchor(reportPane, 0.0);
    }
}
