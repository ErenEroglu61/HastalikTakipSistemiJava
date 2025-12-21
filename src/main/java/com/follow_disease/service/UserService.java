package com.follow_disease.service;

import com.follow_disease.User;
import com.follow_disease.Session;
import com.google.gson.*;
import com.google.gson.reflect.TypeToken;
import javafx.application.Platform;
import javafx.scene.control.Alert;

import java.io.IOException;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

public class UserService {

    private static final Path DB_DIR = Paths.get(System.getProperty("user.dir"), "database");
    private static final Path HOSPITAL_RECORDS = DB_DIR.resolve("hospital_records.json");
    private static final Path DOCTORS_FILE = DB_DIR.resolve("doctors.json"); // Doktor listesi yolu
    private static final String[] DB_FILENAMES = {"user.json"};
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();

    private static User currentUser;

    public static User getCurrentUser() {
        return currentUser;
    }

    //  DTO Sınıfı
    public static class HospitalRecord {
        public String name;
        public String surname;

        public HospitalRecord(String name, String surname) {
            this.name = name;
            this.surname = surname;
        }
    }

    // Otomatik Doldurma için kullanılan metot
    public static HospitalRecord getHospitalRecord(String tc) {
        try {
            if (!Files.exists(HOSPITAL_RECORDS)) return null;

            String content = new String(Files.readAllBytes(HOSPITAL_RECORDS), StandardCharsets.UTF_8);
            JsonArray records = JsonParser.parseString(content).getAsJsonArray();

            for (JsonElement el : records) {
                JsonObject obj = el.getAsJsonObject();
                if (obj.get("tc").getAsString().equals(tc.trim())) {
                    return new HospitalRecord(
                            obj.get("name").getAsString(),
                            obj.get("surname").getAsString()
                    );
                }
            }
        } catch (Exception e) {
            System.err.println("Arşiv okuma hatası: " + e.getMessage());
        }
        return null; // Kayıt bulunamazsa null döner
    }

    private static boolean isNullOrEmpty(String s) {
        return s == null || s.trim().isEmpty();
    }

    // Hata veren repository yerine bu metodu kullanacağız
    private static boolean isDoctor(String tc) {
        try {
            if (!Files.exists(DOCTORS_FILE)) return false;
            String content = new String(Files.readAllBytes(DOCTORS_FILE), StandardCharsets.UTF_8);
            JsonArray arr = JsonParser.parseString(content).getAsJsonArray();
            for (JsonElement el : arr) {
                JsonObject obj = el.getAsJsonObject();
                if (obj.has("tc") && obj.get("tc").getAsString().equals(tc.trim())) {
                    return true;
                }
            }
        } catch (Exception e) {
            System.err.println("Doctor kontrol hatası: " + e.getMessage());
        }
        return false;
    }

    private static void showAlert(String title, String message) {
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

    // --- KAYIT VE GİRİŞ METOTLARI ---

    private static boolean isTcInHospitalRecords(String tc) {
        try {
            if (!Files.exists(HOSPITAL_RECORDS)) return false;
            String content = new String(Files.readAllBytes(HOSPITAL_RECORDS), StandardCharsets.UTF_8);
            JsonArray records = JsonParser.parseString(content).getAsJsonArray();

            for (JsonElement el : records) {
                JsonObject obj = el.getAsJsonObject();
                if (obj.get("tc").getAsString().trim().equals(tc.trim())) return true;
            }
        } catch (Exception e) { e.printStackTrace(); }
        return false;
    }

    public static synchronized boolean register_method(String name, String surname, String age,
                                                       String gender, String tc, String phone,
                                                       String email, String password,
                                                       String branch, String medical_title) {

        if (isNullOrEmpty(name) || isNullOrEmpty(surname) || isNullOrEmpty(tc) || isNullOrEmpty(email) || isNullOrEmpty(password)) {
            showAlert("Eksik alan", "Lütfen zorunlu alanları doldurun.");
            return false;
        }

        if (!isTcInHospitalRecords(tc)) {
            showAlert("Erişim Engellendi", "Hastanede bu TC numarasına ait kayıt bulunamadı.");
            return false;
        }

        try {
            Path dbFile = getDbFile();
            List<RegisteredUser> users = readUsers(dbFile);

            if (users.stream().anyMatch(u -> u.getEmail().equalsIgnoreCase(email) || u.getTc().equals(tc))) {
                showAlert("Kayıt hatası", "Bu e-posta veya TC zaten kayıtlı.");
                return false;
            }

            // HATA VEREN KISIM BURADA DÜZELTİLDİ:
            String role = isDoctor(tc) ? "doktor" : "hasta";

            int nextId = users.stream().max(Comparator.comparingInt(RegisteredUser::getId)).map(u -> u.getId() + 1).orElse(1);

            RegisteredUser newUser = new RegisteredUser(nextId, tc, name, surname, age, gender, phone, email, password, role, branch, medical_title);
            users.add(newUser);

            writeUsers(dbFile, users);
            showInfo("Başarılı", "Kayıt başarıyla oluşturuldu.");
            return true;
        } catch (Exception e) {
            showAlert("Hata", "Kayıt hatası: " + e.getMessage());
            return false;
        }
    }

    public static boolean login_method(String email, String password) {
        try {
            Path dbFile = getDbFile();
            List<RegisteredUser> users = readUsers(dbFile);

            Optional<RegisteredUser> found = users.stream()
                    .filter(u -> u.getEmail().equalsIgnoreCase(email) && u.getPassword().equals(password))
                    .findFirst();

            if (found.isPresent()) {
                RegisteredUser ru = found.get();

                User u;
                if ("doktor".equalsIgnoreCase(ru.getRole())) {
                    u = new com.follow_disease.Doctor(); // Doctor nesnesi üret
                } else {
                    u = new com.follow_disease.Patient(); // Patient nesnesi üret
                }

                // Ortak alanları set et
                u.setId(ru.getId());
                u.setName(ru.getName());
                u.setSurname(ru.getSurname());
                u.setTc(ru.getTc());
                u.setEmail(ru.getEmail());
                u.setPassword(ru.getPassword());


                currentUser = u;
                Session.setCurrentUser(u);
                return true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    // --- DTO VE NESNE YAPISI ---

    private static class RegisteredUser {
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
        private String branch;
        private String medical_title;

        public RegisteredUser(int id, String tc, String name, String surname, String age, String gender, String phone,
                              String email, String password, String role, String branch, String medical_title) {
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
            this.branch = branch;
            this.medical_title = medical_title;
        }

        public int getId() { return id; }
        public String getTc() { return tc; }
        public String getName() { return name; }
        public String getSurname() { return surname; }
        public String getAge() { return age; }
        public String getGender() { return gender; }
        public String getPhone() { return phone; }
        public String getEmail() { return email; }
        public String getPassword() { return password; }
        public String getRole() { return role; }
        public String getBranch() { return branch; }
        public String getMedical_title() { return medical_title; }
    }

    private static Path getDbFile() throws IOException {
        if (!Files.exists(DB_DIR)) Files.createDirectories(DB_DIR);
        Path path = DB_DIR.resolve("user.json");
        if (!Files.exists(path)) Files.write(path, "[]".getBytes(StandardCharsets.UTF_8));
        return path;
    }

    private static List<RegisteredUser> readUsers(Path dbFile) {
        try {
            String content = new String(Files.readAllBytes(dbFile), StandardCharsets.UTF_8);
            if (content.isEmpty() || content.equals("[]")) return new ArrayList<>();
            JsonArray arr = JsonParser.parseString(content).getAsJsonArray();
            List<RegisteredUser> users = new ArrayList<>();
            for (JsonElement el : arr) {
                JsonObject o = el.getAsJsonObject();
                users.add(new RegisteredUser(
                        o.get("id").getAsInt(),
                        o.get("tc").getAsString(),
                        o.get("name").getAsString(),
                        o.get("surname").getAsString(),
                        o.get("age").getAsString(),
                        o.has("gender") ? o.get("gender").getAsString() : "",
                        o.has("phone") ? o.get("phone").getAsString() : "",
                        o.get("email").getAsString(),
                        o.get("password").getAsString(),
                        o.get("role").getAsString(),
                        o.has("branch") ? o.get("branch").getAsString() : "",
                        o.has("medical_title") ? o.get("medical_title").getAsString() : ""
                ));
            }
            return users;
        } catch (Exception e) { return new ArrayList<>(); }
    }

    private static void writeUsers(Path dbFile, List<RegisteredUser> users) throws IOException {
        JsonArray arr = new JsonArray();
        for (RegisteredUser u : users) {
            JsonObject o = new JsonObject();
            o.addProperty("id", u.getId());
            o.addProperty("tc", u.getTc());
            o.addProperty("name", u.getName());
            o.addProperty("surname", u.getSurname());
            o.addProperty("age", u.getAge());
            o.addProperty("gender", u.getGender());
            o.addProperty("phone", u.getPhone());
            o.addProperty("email", u.getEmail());
            o.addProperty("password", u.getPassword());
            o.addProperty("role", u.getRole());
            o.addProperty("branch", u.getBranch());
            o.addProperty("medical_title", u.getMedical_title());
            arr.add(o);
        }
        Files.write(dbFile, GSON.toJson(arr).getBytes(StandardCharsets.UTF_8));
    }
}