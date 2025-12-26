package com.follow_disease.service;

import com.follow_disease.User;
import com.follow_disease.Session;
import com.google.gson.*;
import javafx.application.Platform;
import javafx.scene.control.Alert;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.util.regex.Pattern;

public final class ProfileService {

  private ProfileService() {}

  // --- Yardımcı metotlar ---
  private static void showError(String title, String message) {
    Platform.runLater(() -> {
      Alert a = new Alert(Alert.AlertType.ERROR);
      a.setTitle(title);
      a.setHeaderText(null);
      a.setContentText(message);
      a.showAndWait();
    });
  }

  private static void showInfo(String title, String message) {
    Platform.runLater(() -> {
      Alert a = new Alert(Alert.AlertType.INFORMATION);
      a.setTitle(title);
      a.setHeaderText(null);
      a.setContentText(message);
      a.showAndWait();
    });
  }

  private static boolean isValidEmail(String email) {
    if (email == null) return false;
    Pattern emailPattern = Pattern.compile("^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$");
    return emailPattern.matcher(email.trim()).matches();
  }

  private static String normalizePhone(String phone) {
    if (phone == null) return null;
    String normalized = phone.replaceAll("\\s+", "");
    normalized = normalized.replaceAll("[^\\d]", "");
    return normalized;
  }

  private static boolean isEmailUnique(String email, String myTc) {
    if (email == null || email.trim().isEmpty()) return true;
    Path dbDir = Paths.get(System.getProperty("user.dir"), "database");
    Path usersFile = dbDir.resolve("user.json");
    try {
      if (!Files.exists(usersFile)) return true;
      String content = new String(Files.readAllBytes(usersFile), StandardCharsets.UTF_8).trim();
      if (content.isEmpty() || content.equals("[]")) return true;
      JsonArray arr = JsonParser.parseString(content).getAsJsonArray();
      for (JsonElement el : arr) {
        JsonObject obj = el.getAsJsonObject();
        if (!obj.has("tc")) continue;
        String tc = obj.get("tc").getAsString();
        if (tc.equals(myTc)) continue;
        if (obj.has("email") && obj.get("email").getAsString().equalsIgnoreCase(email.trim())) {
          return false;
        }
      }
      return true;
    } catch (Exception e) {
      System.err.println("isEmailUnique kontrol hatası: " + e.getMessage());
      return true;
    }
  }

  // --- Doktor: password, age, gender, phone ---
  public static boolean updateDoctor(User u, String age, String gender, String phone, String password) {
    if (u == null) {
      showError("Hata", "Oturumdaki kullanıcı bulunamadı.");
      return false;
    }

    // Age kontrolü
    if (age != null && !age.isBlank()) {
      if (!age.trim().matches("\\d+")) {
        showError("Hatalı yaş", "Yaş sadece rakamlardan oluşmalıdır.");
        return false;
      }
    }

    // Phone normalizasyon ve kontrol
    String normalizedPhone = null;
    if (phone != null && !phone.isBlank()) {
      normalizedPhone = normalizePhone(phone);
      if (!normalizedPhone.matches("\\d{11}")) {
        showError("Hatalı telefon", "Telefon 11 haneli olmalıdır (ör. 0533 865 22 16).");
        return false;
      }
    }

    // Alanları setle
    if (age != null && !age.isBlank()) u.setAge(age.trim());
    if (gender != null && !gender.isBlank()) u.setGender(gender.trim());
    if (normalizedPhone != null && !normalizedPhone.isBlank()) u.setPhone(normalizedPhone);
    if (password != null && !password.isBlank()) u.setPassword(password.trim());

    boolean ok = JsonDb.updateUser(u);
    if (ok) {
      Session.setCurrentUser(u);
      showInfo("Başarılı", "Profil güncellendi.");
    } else {
      showError("Güncelleme Hatası", "Profil güncellenemedi. Lütfen tekrar deneyin.");
    }
    return ok;
  }

  // --- Hasta: age, gender, password, phone, email ---
  public static boolean updatePatient(User u, String age, String gender, String phone, String email, String password) {
    if (u == null) {
      showError("Hata", "Oturumdaki kullanıcı bulunamadı.");
      return false;
    }

    // Age kontrolü
    if (age != null && !age.isBlank()) {
      if (!age.trim().matches("\\d+")) {
        showError("Hatalı yaş", "Yaş sadece rakamlardan oluşmalıdır.");
        return false;
      }
    }

    // Phone normalizasyon ve kontrol
    String normalizedPhone = null;
    if (phone != null && !phone.isBlank()) {
      normalizedPhone = normalizePhone(phone);
      if (!normalizedPhone.matches("\\d{11}")) {
        showError("Hatalı telefon", "Telefon 11 haneli olmalıdır (ör. 0533 865 22 16).");
        return false;
      }
    }

    // Email doğrulama + benzersizlik
    if (email != null && !email.isBlank()) {
      if (!isValidEmail(email)) {
        showError("Hatalı e-posta", "Lütfen geçerli bir e-posta adresi girin (ör. someone@gmail.com).");
        return false;
      }
      if (!isEmailUnique(email, u.getTc())) {
        showError("E-posta Kullanımda", "Bu e-posta başka bir kullanıcı tarafından kullanılıyor.");
        return false;
      }
    }

    // Alanları setle
    if (age != null && !age.isBlank()) u.setAge(age.trim());
    if (gender != null && !gender.isBlank()) u.setGender(gender.trim());
    if (normalizedPhone != null && !normalizedPhone.isBlank()) u.setPhone(normalizedPhone);
    if (email != null && !email.isBlank()) u.setEmail(email.trim());
    if (password != null && !password.isBlank()) u.setPassword(password.trim());

    boolean ok = JsonDb.updateUser(u);
    if (ok) {
      Session.setCurrentUser(u);
      showInfo("Başarılı", "Profil güncellendi.");
    } else {
      showError("Güncelleme Hatası", "Profil güncellenemedi. Lütfen tekrar deneyin.");
    }
    return ok;
  }
}