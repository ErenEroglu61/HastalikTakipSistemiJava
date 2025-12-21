package com.follow_disease;

import java.util.ArrayList;
import java.util.List;

public class Patient extends User {
    private String age;
    private String gender;
    private String phone;
    private int doctor_id;
    private String current_disease;
    private List<String> medicines;
    private List<String> disease_history;

    public Patient(int id, String name, String surname, String tc, String phone, String email, String password, String age, String gender, int doctor_id, String current_disease, List<String> medicines) {
        super(id, tc, name, surname, email, password, "hasta");
        this.age = age;
        this.gender = gender;
        this.phone = phone;
        this.doctor_id = doctor_id;
        this.current_disease = current_disease;
        this.medicines = medicines;

        if (disease_history == null) {
            this.disease_history = new ArrayList<>();
        } else {
            this.disease_history = disease_history;
        }
    }

    // Getters and Setters
    public String getAge() {
        return age; }
    public String getGender() {
        return gender;}
    public String getPhone() {
        return phone;}
    public int getDoctor_id() {
        return doctor_id; }
    public String getCurrent_disease() {
        return current_disease; }
    public List<String> getMedicines() {
        return medicines; }
    public List<String> getDisease_history() {
        return disease_history; }


    public void setAge(String age) {
        this.age = age; }
    public void setGender(String gender) {
        this.gender = gender;}
    public void setPhone(String phone) {
        this.phone = phone;}
    public void setDoctor_id(int doctor_id) {
        this.doctor_id = doctor_id;}
    public void setCurrent_disease(String current_disease) {
        this.current_disease = current_disease;}
    public void setMedicines(List<String> medicines) {
        this.medicines = medicines;}
    public void setDisease_history(List<String> disease_history) {
        this.disease_history = disease_history; }

    @Override
    public String getWelcomeMessage() {
        // Örn: "Hoş geldiniz, Sayın Ahmet Özçelik"
        return "Hoş geldiniz, Sayın " + getName() + " " + getSurname();
    }

    @Override
    public String getRoleDescription() { // doktor sayfasında açılan pop upta hasta bilgilerine erişmek için kullanabiliriz
        return "Hasta (Yaş: " + age + ") - Tanı: " + current_disease;
    }

    @Override
    public void setEmail(String email) {
        if (email != null) {
            String lowerEmail = email.toLowerCase();
            // Kullancı giriş yaptığında sadece bu mailler kabul edilsin
            if (lowerEmail.endsWith("@gmail.com") ||
                    lowerEmail.endsWith("@hotmail.com") ||
                    lowerEmail.endsWith("@outlook.com")) {

                super.setEmail(email);
            } else {
                // Şimdilik konsola yazdırıyorum sonra arayuzde uyarı ekranı ekleriz
                System.out.println("HATA: Hastalar sadece gmail, hotmail veya outlook kullanarak giriş yapabilir.");
            }
        }
    }

    public void completeTreatment() {
        if (this.current_disease != null && !this.current_disease.isEmpty()) {
            // Geçmiş listesine ekle (Liste yukarıda initialize edildiği için asla null gelmez uyarı almayız)
            this.disease_history.add(this.current_disease + " (Tedavi Edildi)");
            // Mevcut alanları sıfırla
            this.current_disease = null;
            this.doctor_id = 0; // kişinin guncelde bir doktoru yoksa
            if (this.medicines != null) {
                    this.medicines.clear();
                }
            }
     }
}