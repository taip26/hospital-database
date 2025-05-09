package gourd.hospitaldatabase;

public class AppConstants {
    public static final String DATABASE_URL = "jdbc:mysql://localhost:3306/hospital?user=root&password=password-123";


    // Window Dimensions Constants
    public static final int WINDOW_WIDTH = 800;
    public static final int WINDOW_HEIGHT = 600;

    // String Constants
    public static final String APP_TITLE = "Hospital Database";
    public static final String CONNECTED_TO_DATABASE = "Connected to Database!";
    public static final String ALREADY_CONNECTED = "Already connected to Database!";
    public static final String FAILED_TO_CONNECT = "Failed to connect to database. Error message:\n";
    public static final String CONNECTION_CLOSED = "Database connection closed!";
    public static final String CONNECTION_ALREADY_CLOSED = "Database connection already closed!";
    public static final String ERROR_CLOSING_CONNECTION = "Error closing connection: ";
}
