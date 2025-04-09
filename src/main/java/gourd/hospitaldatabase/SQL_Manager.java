package gourd.hospitaldatabase;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class SQL_Manager {
    private static Connection conn;

    public static String connectToDatabase(String url) {
        try {
            if (conn == null || conn.isClosed()) {
                conn = DriverManager.getConnection(url);
                return "Connected to Database!";
            } else {
                return "Already connected to Database!";
            }
        } catch (SQLException e) {
            return "Failed to connect to database. Error message:\n" + e.getMessage();
        }
    }

    public static Connection getConnection() {
        return conn;
    }

    public static String closeConnection() {
        try {
            if (conn != null && !conn.isClosed()) {
                conn.close();
                conn = null;
                return "Database connection closed!";
            } else {
                return "Database connection already closed!";
            }
        } catch (SQLException e) {
            return "Error closing connection: " + e.getMessage();
        }
    }
}
