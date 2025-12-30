package com.follow_disease;

import java.util.ArrayList;
import java.util.List;

public class Doctor extends User implements Notification{

    private String branch;
    private String  medical_title;//doktorun unvanı ve branşı değiştirilemeyecek sistemde tanımlı olduğu için
    private List<String> notifications = new ArrayList<>();

    public Doctor(){
    }
    public Doctor(int id,String tc, String name, String surname, String age, String gender, String phone, String email, String password, String branch, String medical_title, List<String> notifications) {
        super(id, tc, name, surname, age, gender, phone, email, password, "doktor");
        this.branch = branch;
        this.medical_title = medical_title;
        this.notifications = new ArrayList<>();
    }

    // Getters
    public String getBranch() {
        return branch; }
    public String getMedical_title() {
        return medical_title; }

    @Override
    public List<String> getNotifications() {
        if (notifications == null) notifications = new ArrayList<>();
        return notifications; }
    @Override
    public void setNotifications(List<String> notifications) {
        this.notifications = notifications; }
    @Override
    public void updateNotificationUI(){}

    @Override
    public String getWelcomeMessage() {
        return "Hoş geldiniz, " + medical_title + " " +  getName() + " " + getSurname() + " (" + getBranch() + ")";
    }

    @Override
    public String getRoleDescription() { //Hasta doktor bilgilerine erişmek istediğinde kullanabiliriz
        return branch + " Bölümü - " + medical_title;
    }

}