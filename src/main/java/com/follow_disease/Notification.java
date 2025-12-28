package com.follow_disease;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public interface Notification {

    List<String> getNotifications();
    void setNotifications(List<String> notifications);
    void updateNotificationUI();


    default void sendNotification(String targetTc, String message, boolean isTargetDoctor) {
        String path = isTargetDoctor ? "database/doctors.json" : "database/patients.json";

        processFile(targetTc, path, isTargetDoctor, (targetUser) -> {

            List<String> list = targetUser.getNotifications();
            if (list == null) list = new ArrayList<>();

            list.clear();
            list.add(message);
            targetUser.setNotifications(list);
        });
    }

    default void clearNotifications(String tc, boolean isDoctor) {
        String path = isDoctor ? "database/doctors.json" : "database/patients.json";
        processFile(tc, path, isDoctor, (targetUser) -> {
            if (targetUser.getNotifications() != null) {
                targetUser.getNotifications().clear();
            }
        });
    }

    // JSON İşleme Motoru
    private void processFile(String tc, String path, boolean isDoctor, java.util.function.Consumer<Notification> action) {
        Gson gson = new Gson();
        try {
            // ÖNEMLİ: Burada 'Notification' tipinde bir liste gibi işlem yapıyoruz
            // Çünkü hem Patient hem Doctor bu interface'i implement ediyor.
            Type type = isDoctor ? new TypeToken<List<Doctor>>(){}.getType() : new TypeToken<List<Patient>>(){}.getType();

            List<Notification> users;
            try (FileReader reader = new FileReader(path)) {
                users = gson.fromJson(reader, type);
            }

            if (users != null) {
                for (Notification n : users) {
                    // Burada n.getTc() diyebilmek için User'dan geldiğini bilmeliyiz
                    // n bir Notification'dır, ama aynı zamanda bir User'dır (Casting yapabiliriz)
                    User u = (User) n;
                    if (u.getTc().equals(tc)) {
                        action.accept(n);
                        break;
                    }
                }
                try (FileWriter writer = new FileWriter(path)) {
                    gson.toJson(users, writer);
                }
            }
        } catch (IOException e) { e.printStackTrace(); }
    }
}