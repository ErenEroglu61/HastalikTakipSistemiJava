package com.follow_disease;

public abstract class User {
    private int id;
    private String name;
    private String surname;
    private String email;
    private String password;
    private String role;

    public User(int id, String name, String surname, String email, String password, String role) {
        this.id = id;
        this.name = name;
        this.surname = surname;
        this.email = email;
        this.password = password;
        this.role = role;
    }

    // Ortak metot
    public String getFullName() {
        return name + " " + surname;
    }

    // Alt sınıfların override etmesi gereken soyut metotlar
    public abstract String getWelcomeMessage();
    public abstract String getRoleDescription();

    // Getter ve Setter'lar
    public int getId() {
        return id; }
    public String getEmail() {
        return email; }
    public String getRole() {
        return role; }
    public String getName() {
        return name; }
    public String getSurname() {
        return surname; }
    public String getPassword() {
        return password;
    }
    public void setId(int id) {
        this.id = id; }
    public void setEmail(String email){
        this.email = email; } ;

    //public void setRole(String role) {this.role = role;} bu satırı şimdilik boyle yaptım
    // bence rol değiştirilemese daha iyi olur kodun mantığı açısından doktorları sisteme biz giriyoruz çünkü
    // en azından şimdilik böyle yapalım dedik

    public void setName(String name) {
        this.name = name; }
    public void setSurname(String surname) {
        this.surname = surname;
    }
    public void setPassword(String password) {
        this.password = password;
    }
}
