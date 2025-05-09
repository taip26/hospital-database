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

    public static AnchorPane createAppointmentTrendsReport(List<AppointmentModel> appointments) {
        // Group appointments by date and count them
        Map<String, Long> appointmentCounts = appointments.stream()
                .collect(Collectors.groupingBy(AppointmentModel::getVisitDate, Collectors.counting()));

        // Prepare data for the chart
        List<String> dates = appointmentCounts.keySet().stream().sorted().toList();
        List<Long> counts = dates.stream().map(appointmentCounts::get).toList();

        // Create the chart
        CategoryChart chart = new CategoryChartBuilder()
                .title("Appointment Trends")
                .xAxisTitle("Date")
                .yAxisTitle("Number of Appointments")
                .build();
        chart.addSeries("Appointments", dates, counts);

        // Embed the chart in JavaFX
        SwingNode swingNode = new SwingNode();
        swingNode.setContent(new XChartPanel<>(chart));

        AnchorPane chartPane = new AnchorPane();
        chartPane.getChildren().add(swingNode);
        return chartPane;
    }

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
}