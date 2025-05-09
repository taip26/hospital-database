package gourd.hospitaldatabase;

public class StaffModel {
    private int StaffID;
    private String Name;
    private String Role;

    public StaffModel(int staffID, String name, String role) {
        this.StaffID = staffID;
        this.Name = name;
        this.Role = role;
    }

    // Getter and Setter for StaffID
    public int getStaffID() {
        return StaffID;
    }

    public void setStaffID(int staffID) {
        StaffID = staffID;
    }

    // Getter and Setter for Name
    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

    // Getter and Setter for Role
    public String getRole() {
        return Role;
    }

    public void setRole(String role) {
        Role = role;
    }

    /**
     * Meaningful string representation for display in UI controls.
     */
    @Override
    public String toString() {
        return StaffID + " - " + Name + " (" + Role + ")";
    }
}