package gourd.hospitaldatabase;

import gourd.hospitaldatabase.pojos.Appointment;
import gourd.hospitaldatabase.pojos.Inventory;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;


public class SQL_Manager {
    private final static String CONNECTION_URL = "jdbc:mysql://localhost:3306/hospital?user=root&password=password";
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
            pstmt.setDate(4, java.sql.Date.valueOf(billDate));
            pstmt.setString(5, reason);
            pstmt.setBigDecimal(6, java.math.BigDecimal.valueOf(paymentAmount));
            pstmt.setBigDecimal(7, java.math.BigDecimal.valueOf(insuranceDeductible));
            pstmt.setString(8, paymentStatus);
            pstmt.setInt(9, adminID);

            rowsAffected = pstmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return "Inserted " + rowsAffected + " row(s) successfully";
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
                String status = rs.getString("Status");
                String date = rs.getString("VisitDate");
                String time = rs.getString("VisitTime");

                AppointmentModel appointment = new AppointmentModel(appointmentID, patientID, staffID, status, date, time);
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


    public static boolean insertPatient(String dob, String name, String address, String insurance) {
        String query = "INSERT INTO patients (dob, Name, Address, Insurance) VALUES (?, ?, ?, ?)";

        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setString(1, dob);
            stmt.setString(2, name);
            stmt.setString(3, address);
            stmt.setString(4, insurance);

            int rows = stmt.executeUpdate();
            return rows > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public static boolean insertAppointment(Appointment appointment) {
        // SQL query with placeholders for parameters.
        String query = "INSERT INTO appointment (PatientID, StaffID, VisitDate, VisitTime, Status) VALUES (?, ?, ?, ?, ?)";

        try (Connection conn = SQL_Manager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            // Set the parameters from the Appointment object.
            stmt.setInt(1, appointment.getPatientId());
            stmt.setInt(2, appointment.getStaffId());
            // Assuming appointment.getDate() returns a String like "YYYY-MM-DD"
            stmt.setString(3, appointment.getDate());
            // Assuming appointment.getTime() returns a String like "HH:mm:ss"
            stmt.setString(4, appointment.getTime());
            stmt.setString(5, appointment.getStatus());

            int rowsAffected = stmt.executeUpdate();
            System.out.println("Rows affected: " + rowsAffected);
            return rowsAffected > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public static boolean insertAppointment(int patientID, int staffID, String visitDate, String visitTime, String status) {
        // SQL query with placeholders for parameters.
        String query = "INSERT INTO appointment (PatientID, StaffID, VisitDate, VisitTime, Status) VALUES (?, ?, ?, ?, ?)";

        try (Connection conn = SQL_Manager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            // Set the parameters from the Appointment object.
            stmt.setInt(1, patientID);
            stmt.setInt(2, staffID);
            // Assuming appointment.getDate() returns a String like "YYYY-MM-DD"
            stmt.setString(3, visitDate);
            // Assuming appointment.getTime() returns a String like "HH:mm:ss"
            stmt.setString(4, visitTime);
            stmt.setString(5, status);

            int rowsAffected = stmt.executeUpdate();
            System.out.println("Rows affected: " + rowsAffected);
            return rowsAffected > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public static boolean deleteAppointment(int appointmentId) {
        String query = "DELETE FROM appointment WHERE appointmentid = ?";
        try (Connection conn = SQL_Manager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setInt(1, appointmentId);
            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0; // Return true if a row was deleted
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public static List<String> getAllAdminsString() {
        String sql = "SELECT * FROM administrator;";
        List<String> adminsList = new ArrayList<>();

        try (var conn = getConnection();
             var pstmt = conn.prepareStatement(sql);
             var rs = pstmt.executeQuery()) {

            while (rs.next()) {
                int administratorID = rs.getInt("AdministratorID");
                String name = rs.getString("Name");
                String role = rs.getString("Role");

                String admin = administratorID + " " + name + " " + role;

                adminsList.add(admin);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return adminsList;
    }

    public static List<String> getAllMedicalBillsString() {
        String sql = "SELECT * FROM medicalbills;";
        List<String> billsList = new ArrayList<>();

        try (var conn = getConnection();
             var pstmt = conn.prepareStatement(sql);
             var rs = pstmt.executeQuery()) {

            while (rs.next()) {
                int billID = rs.getInt("BillID");
                int patientID = rs.getInt("PatientID");
                int staffID = rs.getInt("StaffID");
                String reason = rs.getString("Reason");

                String bill = billID + " " + patientID + " " + staffID + " " + reason;

                billsList.add(bill);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return billsList;
    }

    public static String updateMedicalBill(int billId, String billDate, String reason, double paymentAmount,
                                 double insuranceDeductible, String paymentStatus, int adminID) {
        String sql = "UPDATE medicalbills SET BillDate = ?, Reason = ?, PaymentAmount = ?, " +
                "InsuranceDeductible = ?, PaymentStatus = ?, AdminID = ? WHERE BillID = ?";
        try (var conn = getConnection();
             var pstmt = conn.prepareStatement(sql)) {

            pstmt.setDate(1, java.sql.Date.valueOf(billDate));
            pstmt.setString(2, reason);
            pstmt.setBigDecimal(3, java.math.BigDecimal.valueOf(paymentAmount));
            pstmt.setBigDecimal(4, java.math.BigDecimal.valueOf(insuranceDeductible));
            pstmt.setString(5, paymentStatus);
            pstmt.setInt(6, adminID);
            pstmt.setInt(7, billId);

            int rowsAffected = pstmt.executeUpdate();
            return "Updated " + rowsAffected + " row(s) successfully";
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public static String deleteMedicalBill(int billID) {
        String sql = "DELETE FROM medicalbills WHERE BillID = ?";
        try (var conn = getConnection();
             var pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, billID);
            int rowsAffected = pstmt.executeUpdate();
            return "Deleted " + rowsAffected + " row(s) successfully";
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public static List<AdministratorModel> getAdministratorsList() {
        String sql = "SELECT * FROM administrator;";
        List<AdministratorModel> administratorsList = new ArrayList<>();

        try (var conn = getConnection();
             var pstmt = conn.prepareStatement(sql);
             var rs = pstmt.executeQuery()) {

            while (rs.next()) {
                int administratorID = rs.getInt("AdministratorID");
                String name = rs.getString("Name");
                String role = rs.getString("Role");

                AdministratorModel administrator = new AdministratorModel(administratorID, name, role);
                administratorsList.add(administrator);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return administratorsList;
    }

    public static List<StaffModel> getStaffList() {
        String sql = "SELECT * FROM staff;";
        List<StaffModel> staffList = new ArrayList<>();

        try (var conn = getConnection();
             var pstmt = conn.prepareStatement(sql);
             var rs = pstmt.executeQuery()) {

            while (rs.next()) {
                int staffID = rs.getInt("StaffID");
                String name = rs.getString("Name");
                String role = rs.getString("Role");

                StaffModel staff = new StaffModel(staffID, name, role);
                staffList.add(staff);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return staffList;
    }

    public static Object authenticateUser(String username, String password) {
        String hashedPassword = Hash.sha256(password);

        if (hashedPassword == null) {
            return null;
        }

        // Check administrator table first
        String adminSql = "SELECT * FROM administrator WHERE username = ? AND PasswordHash = ?";
        try (var conn = getConnection();
             var adminStmt = conn.prepareStatement(adminSql)) {

            adminStmt.setString(1, username);
            adminStmt.setString(2, hashedPassword);

            try (var rs = adminStmt.executeQuery()) {
                if (rs.next()) {
                    int adminID = rs.getInt("AdministratorID");
                    String name = rs.getString("Name");
                    String role = rs.getString("Role");
                    return new AdministratorModel(adminID, name, role);
                }
            }

            // If no match in admin table, check staff table
            String staffSql = "SELECT * FROM staff WHERE username = ? AND PasswordHash = ?";
            try (var staffStmt = conn.prepareStatement(staffSql)) {
                staffStmt.setString(1, username);
                staffStmt.setString(2, hashedPassword);

                try (var rs = staffStmt.executeQuery()) {
                    if (rs.next()) {
                        int staffID = rs.getInt("StaffID");
                        String name = rs.getString("Name");
                        String role = rs.getString("Role");
                        return new StaffModel(staffID, name, role);
                    }
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Authentication error: " + e.getMessage(), e);
        }

        // If no match in either table, return null
        return null;
    }

    public static String getUserPasswordHash(int userId, String userType) {
        String tableName = userType.equalsIgnoreCase("ADMIN") ? "administrator" : "staff";
        String idColumnName = userType.equalsIgnoreCase("ADMIN") ? "AdministratorID" : "StaffID";
        String sql = "SELECT PasswordHash FROM " + tableName + " WHERE " + idColumnName + " = ?";

        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, userId);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getString("PasswordHash");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }


    public static boolean updateUserPassword(int userId, String userType, String newPassword) {
        String newHashedPassword = Hash.sha256(newPassword);
        if (newHashedPassword == null) {
            System.err.println("Password hashing failed during password update.");
            return false;
        }

        String tableName = userType.equalsIgnoreCase("ADMIN") ? "administrator" : "staff";
        String idColumnName = userType.equalsIgnoreCase("ADMIN") ? "AdministratorID" : "StaffID";
        String sql = "UPDATE " + tableName + " SET PasswordHash = ? WHERE " + idColumnName + " = ?";

        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, newHashedPassword);
            pstmt.setInt(2, userId);
            int rowsAffected = pstmt.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            e.printStackTrace(); // Handle appropriately
            return false;
        }
    }



    public static List<PatientModel> getAllPatients() {
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

    public static ObservableList<AppointmentModel> getStaffAppointmentsById(int staffID) {
        String sql = "SELECT * FROM appointment WHERE StaffID = ?";
        ObservableList<AppointmentModel> appointmentsList = FXCollections.observableArrayList();

        try (var conn = getConnection();
             var pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, staffID);
            try (var rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    int appointmentID = rs.getInt("AppointmentID");
                    int patientID = rs.getInt("PatientID");
                    String status = rs.getString("Status");
                    LocalDate date = rs.getDate("VisitDate").toLocalDate();
                    LocalTime time = rs.getTime("VisitTime").toLocalTime();

                    AppointmentModel appointment = new AppointmentModel(appointmentID, patientID, staffID, status, date.toString(), time.toString());
                    appointmentsList.add(appointment);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return appointmentsList;
    }

    public static PatientModel getPatientById(int patientId) {
        String sql = "SELECT * FROM patients WHERE PatientID = ?";
        
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
    
            stmt.setInt(1, patientId);
            ResultSet rs = stmt.executeQuery();
    
            if (rs.next()) {
                PatientModel patient = new PatientModel();
                patient.setPatientID(rs.getInt("PatientID"));
                patient.setName(rs.getString("Name"));
                patient.setDob(rs.getDate("dob"));
                patient.setAddress(rs.getString("Address"));
                patient.setInsurance(rs.getString("Insurance"));
                return patient;
            }
    
        } catch (SQLException e) {
            e.printStackTrace();
        }
    
        return null; 
    }
    
    public static ObservableList<MedicalBillModel> searchMedicalBills(String searchQuery) {
        String sql = "SELECT * FROM medicalbills WHERE " +
                "BillID LIKE ? OR " +
                "PatientID LIKE ? OR " +
                "StaffID LIKE ? OR " +
                "Reason LIKE ? OR " +
                "PaymentStatus LIKE ?";

        ObservableList<MedicalBillModel> billsList = FXCollections.observableArrayList();
        String searchParam = "%" + searchQuery + "%";

        try (var conn = getConnection();
             var pstmt = conn.prepareStatement(sql)) {

            // Set the search parameter for all searchable columns
            pstmt.setString(1, searchParam);
            pstmt.setString(2, searchParam);
            pstmt.setString(3, searchParam);
            pstmt.setString(4, searchParam);
            pstmt.setString(5, searchParam);

            try (var rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    MedicalBillModel bill = new MedicalBillModel();
                    bill.setBillID(rs.getInt("BillID"));
                    bill.setAppointmentID(rs.getInt("AppointmentID"));
                    bill.setPatientID(rs.getInt("PatientID"));
                    bill.setStaffID(rs.getInt("StaffID"));
                    bill.setBillDate(rs.getDate("BillDate").toLocalDate());
                    bill.setReason(rs.getString("Reason"));
                    bill.setPaymentAmount(rs.getDouble("PaymentAmount"));
                    bill.setInsuranceDeductible(rs.getDouble("InsuranceDeductible"));
                    bill.setPaymentStatus(rs.getString("PaymentStatus"));
                    bill.setAdminID(rs.getInt("AdminID"));

                    billsList.add(bill);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error searching medical bills: " + e.getMessage(), e);
        }
        return billsList;
    }

    public static MedicalBillModel getMedicalBillById(int billId) {
        String sql = "SELECT * FROM medicalbills WHERE BillID = ?";

        try (var conn = getConnection();
             var pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, billId);

            try (var rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    MedicalBillModel bill = new MedicalBillModel();
                    bill.setBillID(rs.getInt("BillID"));
                    bill.setAppointmentID(rs.getInt("AppointmentID"));
                    bill.setPatientID(rs.getInt("PatientID"));
                    bill.setStaffID(rs.getInt("StaffID"));
                    bill.setBillDate(rs.getDate("BillDate").toLocalDate());
                    bill.setReason(rs.getString("Reason"));
                    bill.setPaymentAmount(rs.getDouble("PaymentAmount"));
                    bill.setInsuranceDeductible(rs.getDouble("InsuranceDeductible"));
                    bill.setPaymentStatus(rs.getString("PaymentStatus"));
                    bill.setAdminID(rs.getInt("AdminID"));

                    return bill;
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error getting medical bill: " + e.getMessage(), e);
        }
        return null;
    }

    public static String getPatientNameById(int patientId) {
        String sql = "SELECT Name FROM patients WHERE PatientID = ?";
        try (var conn = getConnection();
             var pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, patientId);
            try (var rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getString("Name");
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error getting patient name: " + e.getMessage(), e);
        }
        return null;
    }

    public static String getStaffNameById(int staffId) {
        String sql = "SELECT Name FROM staff WHERE StaffID = ?";
        try (var conn = getConnection();
             var pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, staffId);
            try (var rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getString("Name");
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error getting staff name: " + e.getMessage(), e);
        }
        return null;
    }

    public static List<AppointmentModel> getAllAppointmentsWithNames() {
        String sql = "SELECT a.*, p.Name AS PatientName, s.Name AS StaffName " +
                     "FROM appointment a " +
                     "LEFT JOIN patients p ON a.PatientID = p.PatientID " +
                     "LEFT JOIN staff s ON a.StaffID = s.StaffID";

        List<AppointmentModel> appointmentsList = new ArrayList<>();

        try (var conn = getConnection();
             var pstmt = conn.prepareStatement(sql);
             var rs = pstmt.executeQuery()) {

            while (rs.next()) {
                AppointmentModel appointment = new AppointmentModel();
                appointment.setAppointmentID(rs.getInt("AppointmentID"));
                appointment.setPatientID(rs.getInt("PatientID"));
                appointment.setStaffID(rs.getInt("StaffID"));
                appointment.setStatus(rs.getString("Status"));
                appointment.setVisitDate(rs.getDate("VisitDate").toLocalDate().toString());
                appointment.setVisitTime(rs.getTime("VisitTime").toLocalTime().toString());

                // Set the pre-fetched names
                appointment.setPatientName(rs.getString("PatientName"));
                appointment.setStaffName(rs.getString("StaffName"));

                appointmentsList.add(appointment);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error getting appointments with names: " + e.getMessage(), e);
        }
        return appointmentsList;
    }

    public static List<MedicalBillModel> getAllMedicalBills() {
        String sql = "SELECT * FROM medicalbills";
        List<MedicalBillModel> billsList = new ArrayList<>();

        try (var conn = getConnection();
             var stmt = conn.createStatement();
             var rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                MedicalBillModel bill = new MedicalBillModel();
                bill.setBillID(rs.getInt("BillID"));
                bill.setAppointmentID(rs.getInt("AppointmentID"));
                bill.setPatientID(rs.getInt("PatientID"));
                bill.setStaffID(rs.getInt("StaffID"));
                bill.setBillDate(rs.getDate("BillDate").toLocalDate());
                bill.setReason(rs.getString("Reason"));
                bill.setPaymentAmount(rs.getDouble("PaymentAmount"));
                bill.setInsuranceDeductible(rs.getDouble("InsuranceDeductible"));
                bill.setPaymentStatus(rs.getString("PaymentStatus"));
                bill.setAdminID(rs.getInt("AdminID"));

                billsList.add(bill);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error getting medical bills: " + e.getMessage(), e);
        }
        return billsList;
    }

    public static Inventory getInventoryById(int id) {
        Inventory item = null;
        String sql = "SELECT ItemID, Status, Name, Category, Location "
                + "FROM inventory "
                + "WHERE ItemID = ?";

        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    item = new Inventory(
                            rs.getInt("ItemID"),
                            rs.getString("Status"),
                            rs.getString("Name"),
                            rs.getString("Category"),
                            rs.getString("Location")
                    );
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            // you might want to rethrow or handle more gracefully
        }

        return item;
    }

    public static List<Inventory> getAllInventory() {
        List<Inventory> items = new ArrayList<>();
        String sql = "SELECT ItemID, Status, Name, Category, Location FROM inventory";

        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                Inventory item = new Inventory(
                        rs.getInt("ItemID"),
                        rs.getString("Status"),
                        rs.getString("Name"),
                        rs.getString("Category"),
                        rs.getString("Location")
                );
                items.add(item);
            }

        } catch (SQLException e) {
            e.printStackTrace();
            // Optionally rethrow as a runtime exception or handle more gracefully
        }

        return items;
    }

    public static List<Inventory> searchInventoryByName(String nameTerm) {
        List<Inventory> items = new ArrayList<>();
        String sql = """
            SELECT ItemID, Status, Name, Category, Location
            FROM inventory
            WHERE Name LIKE ?
            """;

        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            // wrap with wildcards for partial match
            ps.setString(1, "%" + nameTerm + "%");

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    items.add(new Inventory(
                            rs.getInt("ItemID"),
                            rs.getString("Status"),
                            rs.getString("Name"),
                            rs.getString("Category"),
                            rs.getString("Location")
                    ));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return items;
    }

    public static boolean updateInventoryStatus(int itemId, String newStatus) {
        String sql = "UPDATE inventory SET Status = ? WHERE ItemID = ?";

        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, newStatus);
            ps.setInt(2, itemId);

            // executeUpdate returns the number of rows affected
            return ps.executeUpdate() == 1;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public static int getNextAvailablePatientId() {

        String query = "SELECT MAX(PatientID) AS MaxID FROM patients";

        try (Connection conn = getConnection();

             PreparedStatement stmt = conn.prepareStatement(query);

             ResultSet rs = stmt.executeQuery()) {



            if (rs.next()) {

                return rs.getInt("MaxID") + 1;

            }

        } catch (SQLException e) {

            e.printStackTrace();

        }

        return 1; // default to 1 if no records exist

    }



    public static int getNextAvailableStaffId() {

        String query = "SELECT MAX(StaffID) AS MaxID FROM staff";

        try (Connection conn = getConnection();

             PreparedStatement stmt = conn.prepareStatement(query);

             ResultSet rs = stmt.executeQuery()) {

            if (rs.next()) {

                return rs.getInt("MaxID") + 1;

            }

        } catch (SQLException e) {

            e.printStackTrace(); // Or handle more gracefully

        }

        return 1; // Default to 1 if no records exist or an error occurs

    }
    public static void insertStaff(String name, String role, String username, String password) {

        String hashedPassword = Hash.sha256(password);

        if (hashedPassword == null) {

            System.err.println("Password hashing failed.");

        }
        int id = getNextAvailableStaffId();
        String query = "INSERT INTO staff (Name, Role, username, PasswordHash, staffID) VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, name);
            stmt.setString(2, role);
            stmt.setString(3, username);
            stmt.setString(4, hashedPassword);
            stmt.setInt(5, id);
            int rowsAffected = stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
            if (e.getMessage().toLowerCase().contains("duplicate entry") && e.getMessage().toLowerCase().contains("username")) {
                System.err.println("Error: Username '" + username + "' already exists.");
            }
        }

    }



    public static List<StaffModel> getAllStaff() {

        String sql = "SELECT * FROM staff ORDER BY Name;";

        List<StaffModel> staffList = new ArrayList<>();

        try (Connection conn = getConnection();

             PreparedStatement pstmt = conn.prepareStatement(sql);

             ResultSet rs = pstmt.executeQuery()) {



            while (rs.next()) {

                int staffID = rs.getInt("StaffID");

                String name = rs.getString("Name");

                String role = rs.getString("Role");

                staffList.add(new StaffModel(staffID, name, role));

            }

        } catch (SQLException e) {

            e.printStackTrace();

        }

        return staffList;

    }



    public static StaffModel getStaffById(int staffId) {

        String sql = "SELECT * FROM staff WHERE StaffID = ?;";

        try (Connection conn = getConnection();

             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, staffId);

            try (ResultSet rs = pstmt.executeQuery()) {

                if (rs.next()) {

                    return new StaffModel(

                            rs.getInt("StaffID"),

                            rs.getString("Name"),

                            rs.getString("Role")

                    );

                }

            }

        } catch (SQLException e) {

            e.printStackTrace();

        }

        return null;

    }





    public static boolean updateStaff(int staffId, String name, String role, String username) {

        String query = "UPDATE staff SET Name = ?, Role = ?, username = ? WHERE StaffID = ?";

        try (Connection conn = getConnection();

             PreparedStatement stmt = conn.prepareStatement(query)) {



            stmt.setString(1, name);

            stmt.setString(2, role);

            stmt.setString(3, username);

            stmt.setInt(4, staffId);



            int rowsAffected = stmt.executeUpdate();

            return rowsAffected > 0;



        } catch (SQLException e) {

            e.printStackTrace();

            if (e.getMessage().toLowerCase().contains("duplicate entry") && e.getMessage().toLowerCase().contains("username")) {

                System.err.println("Error: Username '" + username + "' already exists for another staff member.");

            }

            return false;

        }

    }



    public static boolean updateStaffPassword(int staffId, String newPassword) {

        String hashedPassword = Hash.sha256(newPassword);

        if (hashedPassword == null) {

            System.err.println("Password hashing failed for staff password update.");

            return false;

        }

        String query = "UPDATE staff SET PasswordHash = ? WHERE StaffID = ?";

        try (Connection conn = getConnection();

             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setString(1, hashedPassword);

            stmt.setInt(2, staffId);

            int rowsAffected = stmt.executeUpdate();

            return rowsAffected > 0;

        } catch (SQLException e) {

            e.printStackTrace();

            return false;

        }

    }



    public static boolean deleteStaff(int staffId) {

        String query = "DELETE FROM staff WHERE StaffID = ?";

        try (Connection conn = getConnection();

             PreparedStatement stmt = conn.prepareStatement(query)) {



            stmt.setInt(1, staffId);

            int rowsAffected = stmt.executeUpdate();

            return rowsAffected > 0;



        } catch (SQLException e) {

            e.printStackTrace();

            return false;

        }

    }

    public static boolean updateAppointment(int appointmentID, int patientId, int staffId, String dateValue, String timeText, String status) {
        String sql = "UPDATE appointment SET PatientID = ?, StaffID = ?, VisitDate = ?, VisitTime = ?, Status = ? WHERE AppointmentID = ?";

        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, patientId);
            pstmt.setInt(2, staffId);
            pstmt.setString(3, dateValue);
            pstmt.setString(4, timeText);
            pstmt.setString(5, status);
            pstmt.setInt(6, appointmentID);

            int rowsAffected = pstmt.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public static ObservableList<AppointmentModel> getStaffAppointmentsWithNames(int staffId) {
        ObservableList<AppointmentModel> appointments = FXCollections.observableArrayList();
        String sql = "SELECT a.*, p.Name AS PatientName, s.Name AS StaffName " +
                "FROM appointment a " +
                "LEFT JOIN patients p ON a.PatientID = p.PatientID " +
                "LEFT JOIN staff s ON a.StaffID = s.StaffID " +
                "WHERE a.StaffID = ?";

        try (var conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, staffId);

            try (ResultSet rs = stmt.executeQuery()) {

                while (rs.next()) {
                    AppointmentModel appointment = new AppointmentModel();
                    appointment.setAppointmentID(rs.getInt("AppointmentID"));
                    appointment.setPatientID(rs.getInt("PatientID"));
                    appointment.setStaffID(rs.getInt("StaffID"));
                    appointment.setStatus(rs.getString("Status"));
                    appointment.setVisitDate(rs.getDate("VisitDate").toLocalDate().toString());
                    appointment.setVisitTime(rs.getTime("VisitTime").toLocalTime().toString());

                    // Set the pre-fetched names
                    appointment.setPatientName(rs.getString("PatientName"));
                    appointment.setStaffName(rs.getString("StaffName"));

                    appointments.add(appointment);
                }
            }

        } catch (SQLException e) {
            throw new RuntimeException("Error getting appointments with names: " + e.getMessage(), e);
        }
        return appointments;
    }

    public static int getNextAdminID() {
        String sql = "SELECT MAX(AdministratorID) AS MaxID FROM administrator";
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {

            if (rs.next()) {
                return rs.getInt("MaxID") + 1;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 1; // Default to 1 if no records exist
    }

    public static void insertAdmin(String name, String role, String username, String password) {
        String sql = "INSERT INTO administrator (Name, Role, username, PasswordHash, AdministratorID) VALUES (?, ?, ?, ?, ?)";
        String hashedPassword = Hash.sha256(password);
        if (hashedPassword == null) {
            System.err.println("Password hashing failed.");
            return;
        }
        int id = getNextAdminID();
        try (var conn = getConnection();
             var pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, name);
            pstmt.setString(2, role);
            pstmt.setString(3, username);
            pstmt.setString(4, hashedPassword);
            pstmt.setInt(5, id);

            int rowsAffected = pstmt.executeUpdate();
            System.out.println("Inserted " + rowsAffected + " row(s) successfully");
        } catch (SQLException e) {
            throw new RuntimeException("Error inserting admin: " + e.getMessage(), e);
        }
    }
}
