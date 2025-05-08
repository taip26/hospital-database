package gourd.hospitaldatabase;

import java.sql.Date;

public class PatientModel {
    private int PatientID;
    private String Name;
    private Date dob;
    private String Address;
    private String Insurance;

    public int getPatientID() {
        return PatientID;
    }

    public void setPatientID(int patientID) {
        this.PatientID = patientID;
    }

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        this.Name = name;
    }

    public Date getDob() {
        return dob;
    }

    public void setDob(Date dob) {
        this.dob = dob;
    }

    public String getAddress() {
        return Address;
    }

    public void setAddress(String address) {
        this.Address = address;
    }

    public String getInsurance() {
        return Insurance;
    }

    public void setInsurance(String insurance) {
        this.Insurance = insurance;
    }

    @Override
    public String toString() {
        return PatientID + " - " + Name;
    }
}