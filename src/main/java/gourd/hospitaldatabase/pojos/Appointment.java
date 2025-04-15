package gourd.hospitaldatabase.pojos;



public class Appointment {
    private int patientId;
    private int staffId;
    private String date;
    private String time;
    private String status;

    // Constructor with parameters
    public Appointment(int patientId, String date, String time, String status, int staffId) {
        this.patientId = patientId;
        this.date = date;
        this.time = time;
        this.status = status;
        this.staffId = staffId;
    }

    // Default constructor
    public Appointment() {}

    // Getters and setters
    public int getPatientId() {
        return patientId;
    }

    public void setPatientId(int patientId) {
        this.patientId = patientId;
    }

    public int getStaffId() {
        return staffId;
    }

    public void setStaffId(int staffId) {
        this.staffId = staffId;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    // Optional: Override toString() for debugging purposes
    @Override
    public String toString() {
        return "Appointment{" +
                "patientId=" + patientId +
                ", staffId=" + staffId +
                ", date=" + date +
                ", time=" + time +
                ", status='" + status + '\'' +
                '}';
    }
}
