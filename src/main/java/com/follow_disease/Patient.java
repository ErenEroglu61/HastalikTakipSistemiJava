package com.follow_disease;

import java.util.ArrayList;
import java.util.List;

public class Patient extends User implements Notification  {

    private int doctor_id;
    private String current_disease;
    private String appointmentDate;
    private String additionalPatientNote;
    private List<String> current_medicine;
    private List<String> additional_medicines;
    private List<String> prescriptions;
    private List<String> medicines;
    private List<String> disease_history;
    private String bloodType;
    private String additional_disease_course;
    private String additionalDoctorNote;
    private List<String> selectedSymptoms;
    private List<VitalSign> vitalSignsHistory;
    private String prescriptionCode;
    private List<String> notifications = new ArrayList<>();

    public Patient() {}

    public Patient(int id, String name, String surname, String tc, String phone, String email, String password, String age,
                   String gender, int doctor_id, String current_disease, String appointmentDate, String additionalPatientNote, List<String> medicines, List<String> current_medicine,
                   List<String> additional_medicines, List<String> prescriptions, List<String> disease_history, String bloodType, String additional_disease_course,
                   String additionalDoctorNote, List<String> selectedSymptoms, List<VitalSign> vitalSignsHistory, String prescriptionCode, List<String>notifications) {

        super(id, tc, name, surname, age, gender, phone, email, password, "hasta");
        this.doctor_id = doctor_id;
        this.current_disease = current_disease;
        this.appointmentDate = appointmentDate;

        this.additionalPatientNote = additionalPatientNote;
        this.bloodType = (bloodType == null) ? "" : bloodType;
        this.additional_disease_course = (additional_disease_course == null) ? "" : additional_disease_course;
        this.additionalDoctorNote = (additionalDoctorNote == null) ? "" : additionalDoctorNote;
        this.prescriptionCode = (prescriptionCode == null) ? "" : prescriptionCode;

        this.current_medicine = (current_medicine == null) ? new ArrayList<>() : current_medicine;
        this.additional_medicines = (additional_medicines == null) ? new ArrayList<>() : additional_medicines;
        this.prescriptions = (prescriptions == null) ? new ArrayList<>() : prescriptions;
        this.medicines = (medicines == null) ? new ArrayList<>() : medicines;
        this.disease_history = (disease_history == null) ? new ArrayList<>() : disease_history;
        this.selectedSymptoms = (selectedSymptoms == null) ? new ArrayList<>() : selectedSymptoms;
        this.vitalSignsHistory = (vitalSignsHistory == null) ? new ArrayList<>() : vitalSignsHistory;
        this.notifications = (notifications == null) ? new ArrayList<>() : notifications;
    }

    //Getters ve  Setters

    public int getDoctor_id() { return doctor_id; }
    public void setDoctor_id(int doctor_id) { this.doctor_id = doctor_id; }

    public String getCurrent_disease() { return current_disease; }
    public void setCurrent_disease(String current_disease) { this.current_disease = current_disease; }

    public String getAppointmentDate() { return appointmentDate; }
    public void setAppointmentDate(String appointmentDate) { this.appointmentDate = appointmentDate; }

    public String getAdditionalPatientNote() { return additionalPatientNote; }
    public void setAdditionalPatientNote(String additionalPatientNote) { this.additionalPatientNote = additionalPatientNote; }

    public List<String> getCurrent_medicine() { return current_medicine; }
    public void setCurrent_medicine(List<String> current_medicine) { this.current_medicine = current_medicine;}

    public List<String> getAdditional_medicines() { return additional_medicines; }
    public void setAdditional_medicines(List<String> additional_medicines) {this.additional_medicines = additional_medicines;}

    public List<String> getPrescriptions() { return prescriptions; }
    public void setPrescriptions(List<String> prescriptions) {this.prescriptions = prescriptions;}

    public List<String> getMedicines() { return medicines; }
    public void setMedicines(List<String> medicines) { this.medicines = medicines; }

    public List<String> getDisease_history() { return disease_history; }
    public void setDisease_history(List<String> disease_history) { this.disease_history = disease_history; }

    public String getBloodType() { return bloodType; }
    public void setBloodType(String bloodType) { this.bloodType = bloodType; }

    public String getAdditional_disease_course() { return additional_disease_course; }
    public void setAdditional_disease_course(String additional_disease_course) { this.additional_disease_course = additional_disease_course; }

    public String getAdditionalDoctorNote() { return additionalDoctorNote; }
    public void setAdditionalDoctorNote(String additionalNote) { this.additionalDoctorNote = additionalNote; }

    public List<String> getSelectedSymptoms() { return selectedSymptoms; }
    public void setSelectedSymptoms(List<String> selectedSymptoms) { this.selectedSymptoms = selectedSymptoms; }

    public List<VitalSign> getVitalSignsHistory() { return vitalSignsHistory; }
    public void setVitalSignsHistory(List<VitalSign> vitalSignsHistory) { this.vitalSignsHistory = vitalSignsHistory; }

    public String getPrescriptionCode() { return prescriptionCode; }
    public void setPrescriptionCode(String prescriptionCode) { this.prescriptionCode = prescriptionCode; }

    @Override
    public List<String> getNotifications() { return notifications;}
    @Override
    public void setNotifications(List<String> notifications) {this.notifications = notifications;}

    @Override
    public void updateNotificationUI() {}
    @Override
    public String getWelcomeMessage() {
        return "Hoş geldiniz, Sayın " + getName() + " " + getSurname();
    }

    @Override
    public String getRoleDescription() {
        return "Hasta - Tanı: " + current_disease;
    }

}