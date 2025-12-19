package com.follow_disease.service;

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

    // database klasörü proje çalışma dizininde
    private static final Path DB_DIR = Paths.get(System.getProperty("user.dir"), "database");
    private static final String[] DB_FILENAMES = {"user.jason"};
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static final Type LIST_TYPE = new TypeToken<List<RegisteredUser>>(){}.getType();

    public static synchronized boolean register_method(String name, String surname, String age,
                                                       String gender, String email, String password) {
        System.out.println("Sisteme Kaydediliyor: " + name + " " + surname + " / " + email);

        if (isNullOrEmpty(name) || isNullOrEmpty(surname) || isNullOrEmpty(age) ||
                isNullOrEmpty(gender) || isNullOrEmpty(email) || isNullOrEmpty(password)) {
            showAlert("Eksik alan", "Lütfen tüm alanları doldurun.");
            return false;
        }

        if (!isValidEmail(email)) {
            showAlert("Geçersiz e-posta", "Lütfen geçerli bir e-posta adresi girin.");
            return false;
        }

        try {  //expection kullanımı
            Path dbFile = getDbFile();
            List<RegisteredUser> users = readUsers(dbFile);

            Optional<RegisteredUser> exists = users.stream()
                    .filter(u -> u.getEmail().equalsIgnoreCase(email))
                    .findAny();
            if (exists.isPresent()) {
                showAlert("Kayıt hatası", "Bu e-posta ile zaten kayıt yapılmış.");
                return false;
            }

            // "hastane.com" ise doktor / değilse hasta yapma kısmı[registerda]
            String role = email.toLowerCase().endsWith("@hastane.com") ? "doktor" : "hasta";

            // Eğer doktor rolü olacaksa e-posta domain'ini zorunlu kural ekle
            if ("doktor".equals(role) && !email.toLowerCase().endsWith("@hastane.com")) {
                showAlert("E-posta hatası", "Doktorlar yalnızca @hastane.com uzantılı e-posta kullanabilir.");
                return false;
            }

            // ID arttırma dosyada
            int nextId = 1;
            if (!users.isEmpty()) {
                nextId = users.stream().max(Comparator.comparingInt(RegisteredUser::getId)).get().getId() + 1;
            }

            // Branch ve medical_title default boş -> REGİSTER SAYFASINDA TEXTFİELD YOK BUNLARI GİRECEK EKLEMELİ MİYİZ?
            RegisteredUser newUser = new RegisteredUser(nextId, name, surname, age, gender, email, password, role);
            users.add(newUser);

            writeUsers(dbFile, users);
            showInfo("Kayıt başarılı", "Kayıt başarıyla oluşturuldu. Giriş yapabilirsiniz.");
            System.out.println("Yeni kullanıcı eklendi: ID=" + nextId + " role=" + role);
            return true;

        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Dosya hatası", "Kullanıcı veritabanına yazılamadı: " + e.getMessage());
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
