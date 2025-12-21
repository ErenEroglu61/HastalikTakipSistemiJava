package com.follow_disease.service;

import com.follow_disease.User;
import com.follow_disease.Doctor;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import java.io.FileReader;
import java.io.FileWriter;
import java.lang.reflect.Type;
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
        try (FileReader r = new FileReader(USER_PATH.toFile())) {
            Type t = new TypeToken<List<User>>(){}.getType();
            List<User> list = GSON.fromJson(r, t);
            return list == null ? new ArrayList<>() : list;
        } catch (Exception e) {
            e.printStackTrace();
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

    public static List<Doctor> readDoctors() {
        try (FileReader r = new FileReader(DOCTORS_PATH.toFile())) {
            Type t = new TypeToken<List<Doctor>>(){}.getType();
            List<Doctor> list = GSON.fromJson(r, t);
            return list == null ? new ArrayList<>() : list;
        } catch (Exception e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    public static Doctor findDoctorByTc(String tcNo) {
        if (tcNo == null) return null;
        String tc = tcNo.trim();
        for (Doctor d : readDoctors()) {
            if (d.getTc() != null && d.getTc().trim().equals(tc)) return d;
        }
        return null;
    }

    public static User findUserByEmail(String email) {
        if (email == null) return null;
        for (User u : readUsers()) {
            if (u.getEmail() != null && u.getEmail().equalsIgnoreCase(email.trim())) return u;
        }
        return null;
    }

    public static boolean updateUser(User updated) {
        List<User> users = readUsers();
        for (int i = 0; i < users.size(); i++) {
            User u = users.get(i);
            // id varsa id ile, yoksa email ile yakala
            boolean matchById = (u.getId() == updated.getId() && updated.getId() != 0);
            boolean matchByEmail = (u.getEmail() != null && updated.getEmail() != null
                    && u.getEmail().equalsIgnoreCase(updated.getEmail()));

            if (matchById || matchByEmail) {
                users.set(i, updated);
                writeUsers(users);
                return true;
            }
        }
        return false;
    }
}
