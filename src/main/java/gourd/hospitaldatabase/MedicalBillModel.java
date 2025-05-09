package gourd.hospitaldatabase;

import java.time.LocalDate;

public class MedicalBillModel {
    private int billID;
    private int appointmentID;
    private int patientID;
    private int staffID;
    private LocalDate billDate;
    private String reason;
    private double paymentAmount;
    private double insuranceDeductible;
    private String paymentStatus;
    private int adminID;

    // Getters and setters
    public int getBillID() { return billID; }
    public void setBillID(int billID) { this.billID = billID; }

    public int getAppointmentID() { return appointmentID; }
    public void setAppointmentID(int appointmentID) { this.appointmentID = appointmentID; }

    public int getPatientID() { return patientID; }
    public void setPatientID(int patientID) { this.patientID = patientID; }

    public int getStaffID() { return staffID; }
    public void setStaffID(int staffID) { this.staffID = staffID; }

    public LocalDate getBillDate() { return billDate; }
    public void setBillDate(LocalDate billDate) { this.billDate = billDate; }

    public String getReason() { return reason; }
    public void setReason(String reason) { this.reason = reason; }

    public double getPaymentAmount() { return paymentAmount; }
    public void setPaymentAmount(double paymentAmount) { this.paymentAmount = paymentAmount; }

    public double getInsuranceDeductible() { return insuranceDeductible; }
    public void setInsuranceDeductible(double insuranceDeductible) { this.insuranceDeductible = insuranceDeductible; }

    public String getPaymentStatus() { return paymentStatus; }
    public void setPaymentStatus(String paymentStatus) { this.paymentStatus = paymentStatus; }

    public int getAdminID() { return adminID; }
    public void setAdminID(int adminID) { this.adminID = adminID; }

    @Override
    public String toString() {
        return "Bill #" + billID + " - Patient: " + patientID + " - $" + paymentAmount;
    }
}