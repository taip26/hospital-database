package gourd.hospitaldatabase;

import javafx.embed.swing.SwingNode;
import javafx.scene.layout.AnchorPane;
import org.knowm.xchart.CategoryChart;
import org.knowm.xchart.CategoryChartBuilder;
import org.knowm.xchart.XChartPanel;
import org.knowm.xchart.PieChart;
import org.knowm.xchart.PieChartBuilder;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class ReportUtils {

    public static AnchorPane createStaffPerformanceReport(List<AppointmentModel> appointments, List<StaffModel> staffList) {
        // Count appointments by staff ID
        Map<Integer, Long> staffAppointmentCounts = appointments.stream()
                .collect(Collectors.groupingBy(AppointmentModel::getStaffID, Collectors.counting()));

        // Map staff names to their appointment counts
        List<String> staffNames = staffList.stream()
                .filter(staff -> staffAppointmentCounts.containsKey(staff.getStaffID()))
                .map(StaffModel::getName)
                .toList();
        List<Long> counts = staffList.stream()
                .filter(staff -> staffAppointmentCounts.containsKey(staff.getStaffID()))
                .map(staff -> staffAppointmentCounts.get(staff.getStaffID()))
                .toList();

        // Create the chart
        CategoryChart chart = new CategoryChartBuilder()
                .title("Staff Performance")
                .xAxisTitle("Staff")
                .yAxisTitle("Number of Appointments")
                .build();
        chart.addSeries("Appointments", staffNames, counts);

        // Rotate X-axis labels for better readability
        chart.getStyler().setXAxisLabelRotation(45);

        // Embed the chart in JavaFX
        SwingNode swingNode = new SwingNode();
        swingNode.setContent(new XChartPanel<>(chart));

        AnchorPane chartPane = new AnchorPane();
        chartPane.getChildren().add(swingNode);
        return chartPane;
    }

    public static AnchorPane createFinancialReport(List<MedicalBillModel> bills) {
        // Aggregate financial data
        double totalPayments = bills.stream().mapToDouble(MedicalBillModel::getPaymentAmount).sum();
        double totalInsuranceDeductibles = bills.stream().mapToDouble(MedicalBillModel::getInsuranceDeductible).sum();

        // Create the chart
        CategoryChart chart = new CategoryChartBuilder()
                .title("Financial Report")
                .xAxisTitle("Category")
                .yAxisTitle("Amount")
                .build();
        chart.addSeries("Financial Data", List.of("Payments", "Insurance Deductibles"), List.of(totalPayments, totalInsuranceDeductibles));

        // Embed the chart in JavaFX
        SwingNode swingNode = new SwingNode();
        swingNode.setContent(new XChartPanel<>(chart));

        AnchorPane chartPane = new AnchorPane();
        chartPane.getChildren().add(swingNode);
        return chartPane;
    }

    public static AnchorPane createMedicalBillStatusReport(List<MedicalBillModel> bills) {
        // Count bills by payment status
        Map<String, Long> statusCounts = bills.stream()
                .collect(Collectors.groupingBy(MedicalBillModel::getPaymentStatus, Collectors.counting()));

        // Create the chart
        PieChart chart = new PieChartBuilder()
                .title("Medical Bill Status")
                .build();
        statusCounts.forEach(chart::addSeries);

        // Embed the chart in JavaFX
        SwingNode swingNode = new SwingNode();
        swingNode.setContent(new XChartPanel<>(chart));

        AnchorPane chartPane = new AnchorPane();
        chartPane.getChildren().add(swingNode);
        return chartPane;
    }

    public static AnchorPane createPatientInsuranceReport(List<PatientModel> patients) {
        // Aggregate data by insurance provider
        Map<String, Long> insuranceCounts = patients.stream()
                .collect(Collectors.groupingBy(PatientModel::getInsurance, Collectors.counting()));

        // Create the pie chart
        PieChart chart = new PieChartBuilder()
                .title("Patient Insurance Distribution")
                .build();
        insuranceCounts.forEach(chart::addSeries);

        // Embed the chart in JavaFX
        SwingNode swingNode = new SwingNode();
        swingNode.setContent(new XChartPanel<>(chart));

        AnchorPane chartPane = new AnchorPane();
        chartPane.getChildren().add(swingNode);
        return chartPane;
    }
}