package gourd.hospitaldatabase;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class SQL_Manager {
    private final static String CONNECTION_URL = "jdbc:mysql://localhost:3306/hospitaldb?user=root&password=password";
    private static Connection conn;

    public static String connectToDatabase(String url) {
        try {
            if (conn == null || conn.isClosed()) {
                conn = DriverManager.getConnection(url);
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
            return DriverManager.getConnection(CONNECTION_URL);
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
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

    public static boolean deletePatientById(int patientId) {
        String query = "DELETE FROM patients WHERE PatientID = ?";

        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setInt(1, patientId);
            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public static boolean updatePatient(int patientId, String dob, String name, String address, String insurance) {
        String query = "UPDATE patients SET dob = ?, Name = ?, Address = ?, Insurance = ? WHERE PatientID = ?";

        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setString(1, dob);
            stmt.setString(2, name);
            stmt.setString(3, address);
            stmt.setString(4, insurance);
            stmt.setInt(5, patientId);

            int rows = stmt.executeUpdate();
            return rows > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public static boolean insertPatient(int patientId, String dob, String name, String address, String insurance) {
        String query = "INSERT INTO patients (PatientID, dob, Name, Address, Insurance) VALUES (?, ?, ?, ?, ?)";
    
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
    
            stmt.setInt(1, patientId);
            stmt.setString(2, dob);
            stmt.setString(3, name);
            stmt.setString(4, address);
            stmt.setString(5, insurance);
    
            int rows = stmt.executeUpdate();
            return rows > 0;
    
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    

}
