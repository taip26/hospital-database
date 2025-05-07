package gourd.hospitaldatabase;

public class AdministratorModel {
    private int adminID;
    private String Name;
    private String Role;


    public AdministratorModel(int adminID, String name, String role) {
        this.adminID = adminID;
        this.Name = name;
        this.Role = role;
    }

    public int getAdminID() {
        return adminID;
    }

    public void setAdminID(int adminID) {
        this.adminID = adminID;
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
}
