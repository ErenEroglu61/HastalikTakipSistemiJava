package com.follow_disease;

public class Doctor extends User {
    private String branch;
    private String  medical_title; //doktorun unvanı ve branşı değiştirilemeyecek sistemde tanımlı olduğu için

    public Doctor(){
    }
    public Doctor(int id,String tc, String name, String surname, String age, String gender, String phone, String email, String password, String branch, String medical_title) {
        // Üst sınıf olan User'ın constructor'ına ortak bilgileri gönderiyoruz
        super(id, tc, name, surname, age, gender, phone, email, password, "doktor");
        this.branch = branch;
        this.medical_title = medical_title;
    }

    // Getters
    public String getBranch() {
        return branch; }
    public String getMedical_title() {
        return medical_title; }

    @Override
    public String getWelcomeMessage() { // giriş ekranı açıldığında bu mesajı kullanabiliriz
        // Örn: "Hoş geldiniz, Prof. Dr. Erdem Yılmaz (Dahiliye)"
        return "Hoş geldiniz, " + medical_title + " " +  getName() + " " + getSurname() + " (" + getBranch() + ")";
    }

    @Override
    public String getRoleDescription() { //Hasta doktor bilgilerine erişmek istediğinde kullanabiliriz
        return branch + " Bölümü - " + medical_title;
    }


}