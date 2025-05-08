package gourd.hospitaldatabase;

public class AppointmentModel {
    private int AppointmentID;
    private int PatientID;
    private int StaffID;
    private String Status;
    private String VisitDate;
    private String VisitTime;

    public AppointmentModel(int appointmentID, int patientID, int staffID, String status, String date, String time) {
        this.AppointmentID = appointmentID;
        this.PatientID = patientID;
        this.StaffID = staffID;
        this.VisitDate = date;
        this.VisitTime = time;
        this.Status = status;
    }

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
