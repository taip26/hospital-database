package gourd.hospitaldatabase;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class SQL_Manager {
    private static Connection conn;

    public static String connectToDatabase() {
        try {
            if (conn == null || conn.isClosed()) {
                conn = DriverManager.getConnection(AppConstants.DATABASE_URL);
                return AppConstants.CONNECTED_TO_DATABASE;
            } else {
                return AppConstants.ALREADY_CONNECTED;
            }
        } catch (SQLException e) {
            return AppConstants.FAILED_TO_CONNECT + e.getMessage();
        }
    }

    public static Connection getConnection() {
        try {
            if (conn == null || conn.isClosed()) {
                // Reconnect if the connection is closed
                connectToDatabase();
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to reconnect to the database: " + e.getMessage(), e);
        }
        return conn;
    }

    public static String closeConnection() {
        try {
            if (conn != null && !conn.isClosed()) {
                conn.close();
                conn = null;
                return AppConstants.CONNECTION_CLOSED;
            } else {
                return AppConstants.CONNECTION_ALREADY_CLOSED;
            }
        } catch (SQLException e) {
            return AppConstants.ERROR_CLOSING_CONNECTION + e.getMessage();
        }
    }

    public static String insertMedicalBill(int appointmentID, int patientID, int staffID, String billDate, String reason,
                                           double paymentAmount, double insuranceDeductible,
                                           String paymentStatus, int adminID) {
        String sql = "INSERT INTO medicalbills (AppointmentID, PatientID, StaffID, BillDate, Reason, PaymentAmount, " +
                "InsuranceDeductible, PaymentStatus, AdminID) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
        int rowsAffected;
        try (var conn = getConnection();
             var pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, appointmentID);
            pstmt.setInt(2, patientID);
            pstmt.setInt(3, staffID);
            pstmt.setDate(4, java.sql.Date.valueOf(billDate)); // Format: YYYY-MM-DD
            pstmt.setString(5, reason);
            pstmt.setBigDecimal(6, java.math.BigDecimal.valueOf(paymentAmount));
            pstmt.setBigDecimal(7, java.math.BigDecimal.valueOf(insuranceDeductible));
            pstmt.setString(8, paymentStatus);
            pstmt.setInt(9, adminID);

            rowsAffected = pstmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return "Inserted"+ rowsAffected +"row(s) successfully";
    }

    public static List<AppointmentModel> getAllAppointmentsList() {
        String sql = "SELECT * FROM appointment;";
        List<AppointmentModel> appointmentsList = new ArrayList<>();

        try (var conn = getConnection();
             var pstmt = conn.prepareStatement(sql);
             var rs = pstmt.executeQuery()) {

            while (rs.next()) {
                int appointmentID = rs.getInt("AppointmentID");
                int patientID = rs.getInt("PatientID");
                int staffID = rs.getInt("StaffID");
                String date = rs.getString("VisitDate");
                String time = rs.getString("VisitTime");

                AppointmentModel appointment = new AppointmentModel(appointmentID, patientID, staffID, date, time);
                appointmentsList.add(appointment);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return appointmentsList;
    }

    public static List<StaffModel> getDoctorsList() {
        String sql = "SELECT * FROM staff WHERE role IN ('Physician', 'Surgeon', 'Pediatrician', 'Dermatologist', 'Cardiologist', 'Neurologist', 'Radiologist');";
        List<StaffModel> doctorsList = new ArrayList<>();

        try (var conn = getConnection();
             var pstmt = conn.prepareStatement(sql);
             var rs = pstmt.executeQuery()) {

            while (rs.next()) {
                int staffID = rs.getInt("StaffID");
                String name = rs.getString("Name");
                String role = rs.getString("Role");
                doctorsList.add(new StaffModel(staffID, name, role));
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return doctorsList;
    }

    public static List<PatientModel> getAllPatientsList() {
        String sql = "SELECT * FROM patients;";
        List<PatientModel> patientsList = new ArrayList<>();

        try (var conn = getConnection();
             var pstmt = conn.prepareStatement(sql);
             var rs = pstmt.executeQuery()) {

            while (rs.next()) {
                int patientID = rs.getInt("PatientID");
                String name = rs.getString("Name");
                java.sql.Date dob = rs.getDate("dob");
                String address = rs.getString("Address");
                String insurance = rs.getString("Insurance");

                PatientModel patient = new PatientModel();
                patient.setPatientID(patientID);
                patient.setName(name);
                patient.setDob(dob);
                patient.setAddress(address);
                patient.setInsurance(insurance);

                patientsList.add(patient);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return patientsList;
    }
}
