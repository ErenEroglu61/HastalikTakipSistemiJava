package com.follow_disease.service;

import com.follow_disease.User;
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
    private static final String[] DB_FILENAMES = {"user.json"};
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();


    public static class HospitalRecord {  //Hastane arşivindeki resmi kayıtları temsil eden DTO
        public String name;
        public String surname;

        public HospitalRecord(String name, String surname) {
            this.name = name;
            this.surname = surname;
        }
    }

    public static HospitalRecord getHospitalRecord(String tcNo) {//Verilen TC numarasına göre hastane arşivinden
     //isim ve soyisim bilgilerini getirirmek için
        try {
            if (!Files.exists(HOSPITAL_RECORDS)) return null;

            String content = new String(Files.readAllBytes(HOSPITAL_RECORDS), StandardCharsets.UTF_8);
            JsonArray records = JsonParser.parseString(content).getAsJsonArray();

            for (JsonElement el : records) {
                JsonObject obj = el.getAsJsonObject();
                if (obj.get("tc").getAsString().equals(tcNo.trim())) {
                    return new HospitalRecord(
                            obj.get("name").getAsString(),
                            obj.get("surname").getAsString()
                    );
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null; // Kayıt bulunamazsa null döner
    }
    private static boolean isTcInHospitalRecords(String tcNo) {
        try {
            if (!Files.exists(HOSPITAL_RECORDS)) return false;

            String content = new String(Files.readAllBytes(HOSPITAL_RECORDS), StandardCharsets.UTF_8);
            JsonArray records = JsonParser.parseString(content).getAsJsonArray();

            for (JsonElement el : records) {
                JsonObject obj = el.getAsJsonObject();
                String registeredTc = obj.get("tc").getAsString().trim();// boşlukları silsin diye
                if (registeredTc.equals(tcNo.trim())) return true;
            }
        } catch (Exception e) { e.printStackTrace(); }
        return false;
    }

    public static synchronized boolean register_method(String name, String surname, String age,
                                                       String gender, String tcNo, String phone,
                                                       String email, String password,
                                                       String branch, String medical_title) {

        //  Boş alan kontrolü
        if (isNullOrEmpty(name) || isNullOrEmpty(surname) || isNullOrEmpty(tcNo) || isNullOrEmpty(email) || isNullOrEmpty(password)) {
            showAlert("Eksik alan", "Lütfen zorunlu alanları (Ad, Soyad, TC, E-posta, Şifre) doldurun.");
            return false;
        }

        // E-posta format kontrolü
        if (!isValidEmail(email)) {
            showAlert("Geçersiz e-posta", "Lütfen geçerli bir e-posta adresi girin.");
            return false;
        }

        //  TC Kontrolü (hospital_records.json)
        if (!isTcInHospitalRecords(tcNo)) {
            showAlert("Erişim Engellendi", "Hastanemizde bu TC kimlik numarasına ait bir kayıt bulunamadı.");
            return false;
        }

        try {
            Path dbFile = getDbFile();
            List<RegisteredUser> users = readUsers(dbFile);

            // 4. ADIM: Mükerrer kayıt kontrolü (Email veya TC ile)
            boolean exists = users.stream()
                    .anyMatch(u -> u.getEmail().equalsIgnoreCase(email) || (u.getTcNo() != null && u.getTcNo().equals(tcNo)));

            if (exists) {
                showAlert("Kayıt hatası", "Bu e-posta veya TC numarası ile zaten kayıt yapılmış.");
                return false;
            }

            // Rol belirleme
            String role = email.toLowerCase().endsWith("@hastane.com") ? "doktor" : "hasta";

            //  Doktor kuralı kontrolü
            if ("doktor".equals(role) && !email.toLowerCase().endsWith("@hastane.com")) {
                showAlert("E-posta hatası", "Doktorlar yalnızca @hastane.com uzantılı e-posta kullanabilir.");
                return false;
            }

            // ID hesaplama
            int nextId = 1;
            if (!users.isEmpty()) {
                nextId = users.stream().max(Comparator.comparingInt(RegisteredUser::getId)).get().getId() + 1;
            }

            //Nesneyi oluştur ve listeye ekle
            RegisteredUser newUser = new RegisteredUser(nextId, name, surname, age, gender, email, password, role, branch, medical_title);
            newUser.tcNo = tcNo; // TC numarasını manuel set ediyoruz
            newUser.phone = phone; // Telefonu set ediyoruz

            users.add(newUser);

            //  Dosyaya yaz
            writeUsers(dbFile, users);
            showInfo("Kayıt başarılı", "Kayıt başarıyla oluşturuldu. Giriş yapabilirsiniz.");
            return true;

        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Hata", "Kayıt sırasında bir sorun oluştu: " + e.getMessage());
            return false;
        }
    }

    public static boolean login_method(String email, String password) {
        System.out.println("UserService: Giriş kontrolü yapılıyor... " + email);

        if (isNullOrEmpty(email) || isNullOrEmpty(password)) {
            showAlert("Eksik alan", "Lütfen e-posta ve şifre alanlarını doldurun.");
            return false;
        }

        if (!isValidEmail(email)) {
            showAlert("Geçersiz e-posta", "Lütfen geçerli bir e-posta adresi girin.");
            return false;
        }

        try {
            Path dbFile = getDbFile();
            List<RegisteredUser> users = readUsers(dbFile);

            Optional<RegisteredUser> found = users.stream()
                    .filter(u -> u.getEmail().equalsIgnoreCase(email))
                    .findFirst();

            if (!found.isPresent()) {
                showAlert("Giriş hatası", "Böyle bir kullanıcı bulunamadı.");
                return false;
            }

            RegisteredUser user = found.get();

            if (!user.getPassword().equals(password)) {
                showAlert("Giriş hatası", "E-posta veya şifre hatalı.");
                return false;
            }

            if ("doktor".equalsIgnoreCase(user.getRole()) && !email.toLowerCase().endsWith("@hastane.com")) {
                showAlert("Giriş hatası", "Doktorlar yalnızca @hastane.com uzantılı e-posta ile giriş yapabilir.");
                return false;
            }

            showInfo("Giriş başarılı", "Hoş geldiniz, " + user.getName() + " " + user.getSurname());
            return true;

        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Hata", "Giriş sırasında hata oluştu: " + e.getMessage());
            return false;
        }
    }


    // DTO sınıfı   -> DTO ne demek? Data transfer object -> json dosyasına veri aktarımını yapan sınıf olarak oluşturdum (transfer işlemlerini yapıyor sadece dr ya da patient'a)
    private static class RegisteredUser {
        private int id;
        private String name;
        private String surname;
        private String age;
        private String gender;
        private String tcNo;
        private String phone;
        private String email;
        private String password;
        private String role;
        private String branch;
        private String medical_title;

        public RegisteredUser(int id, String name, String surname, String age, String gender,
                              String email, String password, String role) {
            this.id = id;
            this.name = name;
            this.surname = surname;
            this.age = age;
            this.gender = gender;
            this.tcNo = tcNo;
            this.phone = phone;
            this.email = email;
            this.password = password;
            this.role = role;
            this.branch = "";
            this.medical_title = "";
        }

        public RegisteredUser(int id, String name, String surname, String age, String gender,
                              String email, String password, String role, String branch, String medical_title) {
            this.id = id;
            this.name = name;
            this.surname = surname;
            this.age = age;
            this.gender = gender;
            this.email = email;
            this.password = password;
            this.role = role;
            this.branch = branch == null ? "" : branch;
            this.medical_title = medical_title == null ? "" : medical_title;  //bu alanları gireceksek diye oluşturduğum 2. constructor
        }

        public int getId() { return id; }
        public String getName() { return name; }
        public String getSurname() { return surname; }
        public String getAge() { return age; }
        public String getGender() { return gender; }
        public String getTcNo() { return tcNo; }
        public String getPhone() { return phone; }
        public String getEmail() { return email; }
        public String getPassword() { return password; }
        public String getRole() { return role; }
        public String getBranch() { return branch; }
        public String getMedical_title() { return medical_title; }

        public void setBranch(String branch) { this.branch = branch; }
        public void setMedical_title(String medical_title) { this.medical_title = medical_title; }
    }

    private static Path getDbFile() throws IOException {
        if (!Files.exists(DB_DIR)) {
            Files.createDirectories(DB_DIR);
        }

        for (String fname : DB_FILENAMES) {
            Path p = DB_DIR.resolve(fname);
            if (Files.exists(p)) return p;
        }

        Path defaultPath = DB_DIR.resolve(DB_FILENAMES[0]);
        if (!Files.exists(defaultPath)) {
            Files.createFile(defaultPath);
            Files.write(defaultPath, "[]".getBytes(StandardCharsets.UTF_8), StandardOpenOption.TRUNCATE_EXISTING);
        }
        return defaultPath;
    }

    private static List<RegisteredUser> readUsers(Path dbFile) {
        try {
            if (!Files.exists(dbFile)) {
                return new ArrayList<>();
            }
            byte[] bytes = Files.readAllBytes(dbFile);
            String content = new String(bytes, StandardCharsets.UTF_8).trim();
            if (content.isEmpty()) return new ArrayList<>();

            // JSON içeriğini manuel parse edip role'a göre alanları oku
            JsonElement rootElem = JsonParser.parseString(content);
            if (!rootElem.isJsonArray()) return new ArrayList<>();
            JsonArray arr = rootElem.getAsJsonArray();

            List<RegisteredUser> users = new ArrayList<>();
            for (JsonElement el : arr) {
                if (!el.isJsonObject()) continue;
                JsonObject obj = el.getAsJsonObject();

                int id = obj.has("id") && !obj.get("id").isJsonNull() ? obj.get("id").getAsInt() : 0;
                String name = obj.has("name") && !obj.get("name").isJsonNull() ? obj.get("name").getAsString() : "";
                String surname = obj.has("surname") && !obj.get("surname").isJsonNull() ? obj.get("surname").getAsString() : "";
                String age = obj.has("age") && !obj.get("age").isJsonNull() ? obj.get("age").getAsString() : "";
                String tcNo = obj.has("tcNo") && !obj.get("tcNo").isJsonNull() ? obj.get("tcNo").getAsString() : "";
                String phone = obj.has("phone") && !obj.get("phone").isJsonNull() ? obj.get("phone").getAsString() : "";
                String email = obj.has("email") && !obj.get("email").isJsonNull() ? obj.get("email").getAsString() : "";
                String password = obj.has("password") && !obj.get("password").isJsonNull() ? obj.get("password").getAsString() : "";
                String role = obj.has("role") && !obj.get("role").isJsonNull() ? obj.get("role").getAsString() : "";

                // role'a bağlı alanlar: doktor için gender, branch, medical_title; hasta için bu alanlar olmayabilir
                String gender = obj.has("gender") && !obj.get("gender").isJsonNull() ? obj.get("gender").getAsString() : "";
                String branch = obj.has("branch") && !obj.get("branch").isJsonNull() ? obj.get("branch").getAsString() : "";
                String medical_title = obj.has("medical_title") && !obj.get("medical_title").isJsonNull() ? obj.get("medical_title").getAsString() : "";

                // Eğer role hasta ise, bazı dosyalarda gender/branch yok olabilir; yine de nesne oluştur (alanlar boş)
                RegisteredUser ru = new RegisteredUser(id, name, surname, age, gender, email, password, role, branch, medical_title);
                users.add(ru);
            }

            return users == null ? new ArrayList<>() : users;
        } catch (IOException | JsonSyntaxException e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    private static void writeUsers(Path dbFile, List<RegisteredUser> users) throws IOException {
        // role'a göre alanları kısıtlayarak JsonArray oluştur
        JsonArray arr = new JsonArray();
        for (RegisteredUser u : users) {
            JsonObject obj = new JsonObject();
            obj.addProperty("id", u.getId());
            obj.addProperty("name", u.getName());
            obj.addProperty("surname", u.getSurname());
            obj.addProperty("age", u.getAge());
            obj.addProperty("tcNo", u.getTcNo());
            obj.addProperty("phone", u.getPhone());
            obj.addProperty("email", u.getEmail());
            obj.addProperty("password", u.getPassword());
            obj.addProperty("role", u.getRole());

            // Doktor ise ekstra alanları ekle
            if ("doktor".equalsIgnoreCase(u.getRole())) {
                // gender eklenmesi gerekiyorsa ekle
                if (!isNullOrEmpty(u.getGender())) { obj.addProperty("gender", u.getGender());}
                // branch ve medical_title alanlarını doktor için ekle
                if (!isNullOrEmpty(u.getBranch())) { obj.addProperty("branch", u.getBranch());}
                if (!isNullOrEmpty(u.getMedical_title())) { obj.addProperty("medical_title", u.getMedical_title());}
            }
            else {
                // role hasta ise -> gender/branch/medical_title yok
            }

            arr.add(obj);
        }

        String json = GSON.toJson(arr);
        Files.write(dbFile, json.getBytes(StandardCharsets.UTF_8),
                StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
        System.out.println("Users saved to: " + dbFile.toAbsolutePath());
    }

    private static boolean isNullOrEmpty(String s) {
        return s == null || s.trim().isEmpty();
    }

    private static boolean isValidEmail(String email) {
        return email.matches("^[A-Za-z0-9+_.-]+@(.+)$");
    }

    private static void showAlert(String title, String message) {
        try {
            Platform.runLater(() -> {
                Alert a = new Alert(Alert.AlertType.ERROR);
                a.setTitle(title);
                a.setHeaderText(null);
                a.setContentText(message);
                a.showAndWait();
            });
        } catch (IllegalStateException ex) {
            System.err.println("ALERT (GUI yok): " + title + " - " + message);
        }
    }

    private static void showInfo(String title, String message) {
        try {
            Platform.runLater(() -> {
                Alert a = new Alert(Alert.AlertType.INFORMATION);
                a.setTitle(title);
                a.setHeaderText(null);
                a.setContentText(message);
                a.showAndWait();
            });
        } catch (IllegalStateException ex) {
            System.out.println("INFO: " + title + " - " + message);
        }
    }
}
