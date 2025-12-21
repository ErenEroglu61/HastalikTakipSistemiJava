package com.follow_disease.service;

import com.follow_disease.User;

public final class ProfileService {

    private ProfileService() {}

    // Doktor: password, age, gender, phone
    public static boolean updateDoctor(User u, String age, String gender, String phone, String password) {
        if (age != null && !age.isBlank()) u.setAge(age.trim());
        if (gender != null && !gender.isBlank()) u.setGender(gender.trim());
        if (phone != null && !phone.isBlank()) u.setPhone(phone.trim());
        if (password != null && !password.isBlank()) u.setPassword(password.trim());

        boolean ok = JsonDb.updateUser(u);
        if (ok) Session.setCurrentUser(u);
        return ok;
    }

    // Hasta: age, gender, password, phone, email
    public static boolean updatePatient(User u, String age, String gender, String phone, String email, String password) {
        if (age != null && !age.isBlank()) u.setAge(age.trim());
        if (gender != null && !gender.isBlank()) u.setGender(gender.trim());
        if (phone != null && !phone.isBlank()) u.setPhone(phone.trim());
        if (email != null && !email.isBlank()) u.setEmail(email.trim());
        if (password != null && !password.isBlank()) u.setPassword(password.trim());

        boolean ok = JsonDb.updateUser(u);
        if (ok) Session.setCurrentUser(u);
        return ok;
    }
}
