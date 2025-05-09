package gourd.hospitaldatabase;

public class SessionManager {
    private static SessionManager instance;
    private Object currentUser;
    private String userType; // "STAFF" or "ADMIN"

    private int selectedPatientId = -1;
    private int selectedInventoryId = -1;

    private SessionManager() {
        // Private constructor for singleton
    }

    public static SessionManager getInstance() {
        if (instance == null) {
            instance = new SessionManager();
        }
        return instance;
    }

    public void setCurrentUser(Object user, String type) {
        this.currentUser = user;
        this.userType = type;
    }

    public Object getCurrentUser() {
        return currentUser;
    }

    public String getUserType() {
        return userType;
    }

    public boolean isLoggedIn() {
        return currentUser != null;
    }

    public boolean isAdmin() {
        return isLoggedIn() && "ADMIN".equals(userType);
    }

    public boolean isStaff() {
        return isLoggedIn() && "STAFF".equals(userType);
    }

    public void logout() {
        currentUser = null;
        userType = null;
    }

    // Helper methods to get the typed user
    public StaffModel getStaffUser() {
        return isStaff() ? (StaffModel) currentUser : null;
    }

    public AdministratorModel getAdminUser() {
        return isAdmin() ? (AdministratorModel) currentUser : null;
    }

    public void setSelectedPatientId(int id) {
        this.selectedPatientId = id;
    }

    public int getSelectedPatientId() {
        return selectedPatientId;
    }

    public void setSelectedInventoryId(int id) { this.selectedInventoryId = id; }

    public int getSelectedInventoryId()   { return selectedInventoryId; }
}