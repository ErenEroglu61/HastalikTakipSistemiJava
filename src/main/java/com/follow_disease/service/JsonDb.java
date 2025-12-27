package com.follow_disease.service;

import com.follow_disease.User;
import com.follow_disease.Doctor;
import com.follow_disease.Patient;
import com.google.gson.*;
import com.google.gson.reflect.TypeToken;

import java.io.FileReader;
import java.io.FileWriter;
import java.lang.reflect.Type;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public final class JsonDb {

    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();

    private static final Path USER_PATH = Paths.get("database", "user.json");
    private static final Path DOCTORS_PATH = Paths.get("database", "doctors.json");

    private JsonDb() {}

    public static List<User> readUsers() {
        List<User> userList = new ArrayList<>();
        try {
            String content = new String(Files.readAllBytes(USER_PATH));
            JsonArray array = JsonParser.parseString(content).getAsJsonArray();

            for (JsonElement element : array) {
                JsonObject obj = element.getAsJsonObject();
                String tc = obj.has("tc") ? obj.get("tc").getAsString() : null;

                // Eğer bu TC doktorlar listesinde varsa Doctor olarak, yoksa Patient olarak canlandır
                if (tc != null && isTcInDoctors(tc)) {
                    userList.add(GSON.fromJson(obj, Doctor.class));
                } else {
                    userList.add(GSON.fromJson(obj, Patient.class));
                }
            }
        } catch (Exception e) {
            System.err.println("readUsers hatası: " + e.getMessage());
        }
        return userList;
    }

    private static boolean isTcInDoctors(String tc) {
        for (Doctor d : readDoctors()) {
            if (d.getTc() != null && d.getTc().equals(tc)) return true;
        }
        return false;
    }

    public static List<Doctor> readDoctors() {
        try (FileReader r = new FileReader(DOCTORS_PATH.toFile())) {
            Type t = new TypeToken<List<Doctor>>(){}.getType();
            List<Doctor> list = GSON.fromJson(r, t);
            return list == null ? new ArrayList<>() : list;
        } catch (Exception e) {
            return new ArrayList<>();
        }
    }

    public static void writeUsers(List<User> users) {
        try (FileWriter w = new FileWriter(USER_PATH.toFile())) {
            GSON.toJson(users, w);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static boolean updateDoctorProfile(String tc, String age, String gender, String phone, String email, String password) {
        return updateProfileInternal(tc, age, gender, phone, email, password);
    }

    public static boolean updatePatientProfile(String tc, String age, String gender, String phone, String email, String password) {
        return updateProfileInternal(tc, age, gender, phone, email, password);
    }

    private static boolean updateProfileInternal(String tc, String age, String gender, String phone, String email, String password) {
        try {
            String content = new String(java.nio.file.Files.readAllBytes(USER_PATH));
            JsonArray array = JsonParser.parseString(content).getAsJsonArray();
            boolean found = false;

            for (JsonElement el : array) {
                JsonObject obj = el.getAsJsonObject();
                if (obj.has("tc") && obj.get("tc").getAsString().equals(tc)) {
                    obj.addProperty("age", age);
                    obj.addProperty("gender", gender);
                    obj.addProperty("phone", phone);
                    obj.addProperty("email", email); // Email eklendi
                    if (password != null && !password.isEmpty()) {
                        obj.addProperty("password", password);
                    }
                    found = true;
                    break;
                }
            }
            if (found) {
                java.nio.file.Files.write(USER_PATH, GSON.toJson(array).getBytes());
                return true;
            }
        } catch (Exception e) { e.printStackTrace(); }
        return false;
    }
    public static User findUserByEmail(String email) {
        if (email == null) return null;
        for (User u : readUsers()) {
            if (u.getEmail() != null && u.getEmail().equalsIgnoreCase(email.trim())) return u;
        }
        return null;
    }

    public static Doctor findDoctorByTc(String tcNo) {
        if (tcNo == null) return null;
        for (Doctor d : readDoctors()) {
            if (d.getTc() != null && d.getTc().equals(tcNo.trim())) return d;
        }
        return null;
    }
}