package gourd.hospitaldatabase;

public class AppointmentModel {
    private int AppointmentID;
    private int PatientID;
    private int StaffID;
    private String Status;
    private String VisitDate;
    private String VisitTime;
    private String patientName;
    private String staffName;

    public AppointmentModel() {
        this.AppointmentID = -1;
        this.PatientID = -1;
        this.StaffID = -1;
        this.VisitDate = null;
        this.VisitTime = null;
        this.Status = null;
        this.patientName = null;
        this.staffName = null;
    }

    public AppointmentModel(int appointmentID, int patientID, int staffID, String status, String date, String time) {
        this.AppointmentID = appointmentID;
        this.PatientID = patientID;
        this.StaffID = staffID;
        this.VisitDate = date;
        this.VisitTime = time;
        this.Status = status;
    }

    // Getters and setters
    public String getPatientName() { return patientName; }
    public void setPatientName(String patientName) { this.patientName = patientName; }

    public String getStaffName() { return staffName; }
    public void setStaffName(String staffName) { this.staffName = staffName; }

    // Getter and Setter for Status
    public String getStatus() {
        return Status;
    }
    public void setStatus(String status) {
        Status = status;
    }

    public int getAppointmentID() {
        return AppointmentID;
    }

    public void setAppointmentID(int appointmentID) {
        AppointmentID = appointmentID;
    }

    // Getter and Setter for PatientID
    public int getPatientID() {
        return PatientID;
    }

    public void setPatientID(int patientID) {
        PatientID = patientID;
    }

    // Getter and Setter for StaffID
    public int getStaffID() {
        return StaffID;
    }

    public void setStaffID(int staffID) {
        StaffID = staffID;
    }

    // Getter and Setter for Date
    public String getVisitDate() {
        return VisitDate;
    }

    public void setVisitDate(String visitDate) {
        VisitDate = visitDate;
    }

    // Getter and Setter for Time
    public String getVisitTime() {
        return VisitTime;
    }

    public void setVisitTime(String visitTime) {
        VisitTime = visitTime;
    }
}
