package com.follow_disease.service;

import com.follow_disease.User;
import com.follow_disease.Session;
import com.google.gson.*;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;

public final class ProfileService {

    private ProfileService() {}

    private static void showError(String title, String message) {
        javafx.application.Platform.runLater(() -> {
            Alert a = new Alert(Alert.AlertType.ERROR, message, ButtonType.OK);
            a.setTitle(title);
            a.setHeaderText(null);
            a.showAndWait();
        });
    }

    private static void showInfo(String title, String message) {
        javafx.application.Platform.runLater(() -> {
            Alert a = new Alert(Alert.AlertType.INFORMATION, message, ButtonType.OK);
            a.setTitle(title);
            a.setHeaderText(null);
            a.showAndWait();
        });
    }

    private static boolean isValidGender(String gender) {
        if (gender == null) return false;
        String g = gender.trim();
        return g.equalsIgnoreCase("Kadın") || g.equalsIgnoreCase("Erkek");
    }

    private static boolean isPhoneValid(String rawPhone) {
        if (rawPhone == null || rawPhone.isBlank()) return false;
        if (!rawPhone.matches("[0-9\\s\\-\\(\\)]+")) return false;
        String clean = rawPhone.replaceAll("[^\\d]", "");
        return clean.length() == 11;
    }

    private static boolean isValidEmailFormat(String email) {
        if (email == null || email.isBlank()) return false;
        email = email.trim().toLowerCase();
        if (!email.contains("@") || !email.contains(".")) return false;
        int atIndex = email.indexOf("@");
        int lastDotIndex = email.lastIndexOf(".");
        if (atIndex <= 0 || lastDotIndex <= atIndex + 1 || lastDotIndex == email.length() - 1) return false;
        if (email.length() - lastDotIndex < 3) return false;
        return email.chars().filter(ch -> ch == '@').count() == 1;
    }

    private static String normalizePhone(String phone) {
        return phone == null ? "" : phone.replaceAll("[^\\d]", "");
    }

    private static boolean isEmailUnique(String email, String myTc) {
        if (email == null || email.trim().isEmpty()) return true;
        Path usersFile = Paths.get(System.getProperty("user.dir"), "database", "user.json");
        try {
            if (!Files.exists(usersFile)) return true;
            String content = new String(Files.readAllBytes(usersFile), StandardCharsets.UTF_8).trim();
            JsonArray arr = JsonParser.parseString(content).getAsJsonArray();
            for (JsonElement el : arr) {
                JsonObject obj = el.getAsJsonObject();
                String tc = obj.has("tc") ? obj.get("tc").getAsString() : "";
                if (tc.equals(myTc)) continue;
                if (obj.has("email") && obj.get("email").getAsString().equalsIgnoreCase(email.trim())) return false;
            }
        } catch (Exception e) { return true; }
        return true;
    }

    public static boolean updateDoctor(User u, String age, String gender, String phone, String email, String password) {
        if (u == null) return false;
        if (age != null && !age.isBlank() && !age.trim().matches("\\d+")) { showError("Hata", "Yaş rakam olmalı."); return false; }
        if (!isValidGender(gender)) { showError("Hata", "Cinsiyet 'Kadın' veya 'Erkek' olmalı."); return false; }
        if (!isPhoneValid(phone)) { showError("Hata", "Telefon 11 haneli rakam olmalı."); return false; }
        if (email != null && !email.isBlank()) {
            if (!isValidEmailFormat(email)) { showError("Hata", "Geçersiz e-posta formatı."); return false; }
            if (!isEmailUnique(email, u.getTc())) { showError("Hata", "E-posta kullanımda."); return false; }
        }

        u.setAge(age.trim());
        u.setGender(gender.trim());
        u.setPhone(normalizePhone(phone));
        u.setEmail(email.trim());
        if (password != null && !password.isBlank()) u.setPassword(password.trim());

        boolean ok = JsonDb.updateDoctorProfile(u.getTc(), u.getAge(), u.getGender(), u.getPhone(), u.getEmail(), u.getPassword());
        if (ok) { Session.setCurrentUser(u); showInfo("Başarılı", "Profil güncellendi."); }
        return ok;
    }

    public static boolean updatePatient(User u, String age, String gender, String phone, String email, String password) {
        if (u == null) return false;
        if (age != null && !age.isBlank() && !age.trim().matches("\\d+")) { showError("Hata", "Yaş sayı olmalı."); return false; }
        if (!isValidGender(gender)) { showError("Hata", "Cinsiyet hatalı."); return false; }
        if (!isPhoneValid(phone)) { showError("Hata", "Telefon hatalı."); return false; }
        if (email != null && !email.isBlank()) {
            if (!isValidEmailFormat(email)) { showError("Hata", "E-posta formatı hatalı."); return false; }
            if (!isEmailUnique(email, u.getTc())) { showError("Hata", "E-posta kullanımda."); return false; }
        }

        u.setAge(age.trim());
        u.setGender(gender.trim());
        u.setPhone(normalizePhone(phone));
        u.setEmail(email.trim());
        if (password != null && !password.isBlank()) u.setPassword(password.trim());

        boolean ok = JsonDb.updatePatientProfile(u.getTc(), u.getAge(), u.getGender(), u.getPhone(), u.getEmail(), u.getPassword());
        if (ok) { Session.setCurrentUser(u); showInfo("Başarılı", "Profil güncellendi."); }
        return ok;
    }
}