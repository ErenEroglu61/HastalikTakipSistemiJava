package com.follow_disease;

public abstract class User {

    private int id;
    private String tc;
    private String name;
    private String surname;
    private String age;
    private String gender;
    private String phone;
    private String email;
    private String password;
    private String role;

    public User() {}

    public User(int id, String tc,String name, String surname,String age, String gender, String phone, String email, String password, String role) {
        this.id = id;
        this.tc = tc;
        this.name = name;
        this.surname = surname;
        this.age = age;
        this.gender = gender;
        this.phone = phone;
        this.email = email;
        this.password = password;
        this.role = role;
    }

    public String getFullName() {
        return name + " " + surname;
    }

    public abstract String getWelcomeMessage();
    public abstract String getRoleDescription();

    // Getter ve Setter'lar
    public int getId() {
        return id; }
    public String getTc() {
        return tc;}
    public String getRole() {
        return role; }
    public String getName() {
        return name; }
    public String getSurname() {
        return surname; }
    public String getAge() {
        return age;}
    public String getGender() {
        return gender;}
    public String getPhone() {
        return phone;}
    public String getEmail() {
        return email; }
    public String getPassword() {
        return password;}

    public void setId(int id) {
        this.id = id; }
    public void setTc(String tc) {
        this.tc = tc;};
    public void setName(String name) {
        this.name = name; }
    public void setSurname(String surname) {
        this.surname = surname;}
    public void setAge(String age) {
        this.age = age;}
    public void setGender(String gender) {
        this.gender = gender;}
    public void setPhone(String phone) {
        this.phone = phone;}
    public void setEmail(String email){
        this.email = email; }
    public void setPassword(String password) {
        this.password = password;}
}
