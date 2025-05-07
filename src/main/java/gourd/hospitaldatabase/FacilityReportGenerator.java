package gourd.hospitaldatabase;

import java.sql.*;
import java.util.*;

public class FacilityReportGenerator {

    private static final String CONNECTION_URL = AppConstants.DATABASE_URL;

    public static Map<String, Integer> getPatientCountByInsurance() {
        Map<String, Integer> report = new LinkedHashMap<>();

        String query = "SELECT Insurance, COUNT(*) AS PatientCount " +
                "FROM patients GROUP BY Insurance ORDER BY PatientCount DESC";

        try (Connection conn = DriverManager.getConnection(CONNECTION_URL);
             PreparedStatement stmt = conn.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                report.put(rs.getString("Insurance"), rs.getInt("PatientCount"));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return report;
    }

    public static Map<String, Integer> getAppointmentsPerDoctor() {
        Map<String, Integer> report = new LinkedHashMap<>();

        String query = "SELECT s.Name, COUNT(*) AS AppointmentCount " +
                "FROM appointment a " +
                "JOIN staff s ON a.StaffID = s.StaffID " +
                "GROUP BY s.Name " +
                "ORDER BY AppointmentCount DESC";

        try (Connection conn = DriverManager.getConnection(CONNECTION_URL);
             PreparedStatement stmt = conn.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                report.put(rs.getString("Name"), rs.getInt("AppointmentCount"));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return report;
    }

    public static Map<String, Integer> getMonthlyAppointmentCounts() {
        Map<String, Integer> report = new LinkedHashMap<>();

        String query = "SELECT DATE_FORMAT(VisitDate, '%Y-%m') AS Month, COUNT(*) AS AppointmentCount " +
                "FROM appointment " +
                "GROUP BY Month " +
                "ORDER BY Month";

        try (Connection conn = DriverManager.getConnection(CONNECTION_URL);
             PreparedStatement stmt = conn.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                String month = rs.getString("Month");
                int count = rs.getInt("AppointmentCount");
                report.put(month, count);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return report;
    }


}
