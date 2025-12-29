package com.follow_disease.controller;

import com.follow_disease.*;
import com.follow_disease.service.ProfileService;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.effect.DropShadow;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.fxml.FXMLLoader;

import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class PatientController implements Notification {

    @FXML private Label nameLabel;
    @FXML private Label emailLabel;
    @FXML private Label ageLabel;
    @FXML private Label genderLabel;
    @FXML private Label phoneLabel;
    @FXML private Label tcLabel;
    @FXML private Label passwordLabel;

    @FXML private VBox vboxActiveDiseases;
    @FXML private VBox vboxDiseasesHistory;
    @FXML private VBox vboxActiveMedicines;

    @FXML private Button updateButton;

    @FXML private MenuButton notificationMenuButton;
    @FXML private Circle notificationBadge;

    @FXML
    public void initialize() {

        // Oturumdaki kullanıcı bilgisi alınır
        User user = Session.getCurrentUser();

        if (user != null) {

            // Kullanıcı bilgileri label'lara atanır
            nameLabel.setText(safe(user.getName()) + " " + safe(user.getSurname()));
            emailLabel.setText(safe(user.getEmail()));
            ageLabel.setText(safe(user.getAge()));
            genderLabel.setText(safe(user.getGender()));
            phoneLabel.setText(safe(user.getPhone()));
            tcLabel.setText(safe(user.getTc()));
            passwordLabel.setText(maskPassword(user.getPassword()));

            // TC numarasına göre hasta kaydı bulunur
            Patient patient = findPatientByTc(user.getTc());

            // Hasta bulunduysa hastalık ve ilaç bilgileri yüklenir
            if (patient != null) {
                loadMedicalData(patient);
            }
        }

        // Bildirim menüsü güncellenir
        updateNotificationUI();
    }

    @Override
    public List<String> getNotifications() {

        // Oturumdaki kullanıcı alınır
        User user = Session.getCurrentUser();

        if (user != null) {

            // Kullanıcıya ait hasta kaydı bulunur
            Patient patient = findPatientByTc(user.getTc());

            if (patient != null) {
                return patient.getNotifications();
            }
        }

        return new ArrayList<>();
    }

    @Override
    public void setNotifications(List<String> notifications) {
        // Bu metot şu an aktif kullanılmıyor
    }

    @Override
    public void updateNotificationUI() {

        // Bildirim menüsü temizlenir
        notificationMenuButton.getItems().clear();

        // Bildirim listesi alınır
        List<String> notifications = getNotifications();

        if (notifications != null && !notifications.isEmpty()) {

            // Bildirim varsa kırmızı gösterge aktif edilir
            notificationBadge.setVisible(true);

            // Bildirimler menüye eklenir
            for (String msg : notifications) {
                CustomMenuItem item = new CustomMenuItem(new Label(msg));
                notificationMenuButton.getItems().add(item);
            }

        } else {

            // Bildirim yoksa gösterge gizlenir
            notificationBadge.setVisible(false);
            notificationMenuButton.getItems().add(new MenuItem("Bildirim yok"));
        }
    }

    @FXML
    public void handleNotificationShowing() {

        // Kullanıcı bildirim menüsünü açtığında bildirimler temizlenir
        User user = Session.getCurrentUser();

        if (user != null) {
            clearNotifications(user.getTc(), false);
            notificationBadge.setVisible(false);
        }
    }

    @FXML
    public void handleUpdateAction(ActionEvent event) {

        // Oturumdaki kullanıcı alınır
        User user = Session.getCurrentUser();
        if (user == null) return;

        // Profil güncelleme dialog'u oluşturulur
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle("Profil Güncelle");
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        // Güncellenecek alanlar doldurulur
        TextField ageField = new TextField(safe(user.getAge()));
        TextField genderField = new TextField(safe(user.getGender()));
        TextField phoneField = new TextField(safe(user.getPhone()));
        TextField emailField = new TextField(safe(user.getEmail()));
        PasswordField passwordField = new PasswordField();
        passwordField.setText(safe(user.getPassword()));

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);

        grid.addRow(0, new Label("Yaş:"), ageField);
        grid.addRow(1, new Label("Cinsiyet:"), genderField);
        grid.addRow(2, new Label("Telefon:"), phoneField);
        grid.addRow(3, new Label("E-posta:"), emailField);
        grid.addRow(4, new Label("Şifre:"), passwordField);

        dialog.getDialogPane().setContent(grid);

        // Kullanıcı iptal ederse işlem yapılmaz
        if (dialog.showAndWait().orElse(ButtonType.CANCEL) != ButtonType.OK) return;

        // Bilgiler servis üzerinden güncellenir
        boolean success = ProfileService.updatePatient(
            user,
            ageField.getText(),
            genderField.getText(),
            phoneField.getText(),
            emailField.getText(),
            passwordField.getText()
        );

        // Güncelleme başarılıysa arayüz yenilenir
        if (success) {
            User updated = Session.getCurrentUser();
            ageLabel.setText(safe(updated.getAge()));
            genderLabel.setText(safe(updated.getGender()));
            phoneLabel.setText(safe(updated.getPhone()));
            emailLabel.setText(safe(updated.getEmail()));
            passwordLabel.setText(maskPassword(updated.getPassword()));
        }
    }

    @FXML
    public void handleLogout(ActionEvent event) {

        // Oturum temizlenir
        Session.clear();

        try {
            Parent root = FXMLLoader.load(
                getClass().getResource("/com/follow_disease/login.fxml")
            );

            // Mevcut sahne login ekranı ile değiştirilir
            Stage stage = (Stage) ((Node) event.getSource())
                .getScene().getWindow();

            stage.setScene(new Scene(root));
            stage.show();

        } catch (IOException e) {
            new Alert(Alert.AlertType.ERROR,
                "Giriş sayfası açılamadı").showAndWait();
        }
    }

    private void loadMedicalData(Patient patient) {

        // Önce eski veriler temizlenir
        vboxActiveDiseases.getChildren().clear();
        vboxDiseasesHistory.getChildren().clear();
        vboxActiveMedicines.getChildren().clear();

        // Aktif hastalık eklenir
        if (patient.getCurrent_disease() != null) {
            vboxActiveDiseases.getChildren().add(
                createSimpleCard(patient.getCurrent_disease(), "#D32F2F")
            );
        }

        // Hastalık geçmişi eklenir
        if (patient.getDisease_history() != null) {
            for (String disease : patient.getDisease_history()) {
                vboxDiseasesHistory.getChildren().add(
                    createSimpleCard(disease, "#64748B")
                );
            }
        }

        // Aktif ilaçlar eklenir
        if (patient.getCurrent_medicine() != null) {

            List<MedicineControl> medicines = loadMedicinesFromJson();

            for (String med : patient.getCurrent_medicine()) {
                vboxActiveMedicines.getChildren().add(
                    createMedicineCard(
                        med,
                        findMedicineByName(medicines, med)
                    )
                );
            }
        }
    }

    private String safe(String value) {
        // Null değerlerin önüne geçer
        return value == null ? "" : value.trim();
    }

    private String maskPassword(String password) {
        // Şifreyi yıldızlı gösterir
        if (password == null || password.isEmpty()) return "";
        return "*".repeat(password.length());
    }

    private Patient findPatientByTc(String tc) {

        // JSON dosyasından hasta listesi okunur
        try (FileReader reader = new FileReader("database/patients.json")) {

            Type type = new TypeToken<List<Patient>>(){}.getType();
            List<Patient> patients = new Gson().fromJson(reader, type);

            // TC eşleşmesi aranır
            for (Patient p : patients) {
                if (tc.equals(p.getTc())) return p;
            }

        } catch (Exception ignored) {
        }

        return null;
    }

    private VBox createSimpleCard(String text, String color) {

        // Basit kart yapısı oluşturur
        VBox card = new VBox();
        card.setStyle(
            "-fx-padding:15;" +
                "-fx-border-color:#E2E8F0;" +
                "-fx-border-radius:10;"
        );

        Label label = new Label(text.toUpperCase());
        label.setStyle("-fx-font-weight:bold;-fx-text-fill:" + color);

        card.getChildren().add(label);
        card.setEffect(new DropShadow(10, Color.rgb(0, 0, 0, 0.1)));

        return card;
    }

    private List<MedicineControl> loadMedicinesFromJson() {

        // İlaç bilgileri JSON'dan okunur
        try (FileReader reader = new FileReader("database/medicines.json")) {

            Type type = new TypeToken<List<MedicineControl>>(){}.getType();
            return new Gson().fromJson(reader, type);

        } catch (Exception e) {
            return new ArrayList<>();
        }
    }

    private MedicineControl findMedicineByName(
        List<MedicineControl> meds, String name) {

        // İlaç adı eşleşmesi aranır
        return meds.stream()
            .filter(m -> m.getMedicine_name()
                .equalsIgnoreCase(name))
            .findFirst()
            .orElse(null);
    }

    private VBox createMedicineCard(
        String name, MedicineControl info) {

        // İlaç kartı oluşturur
        VBox card = new VBox(6);
        card.setStyle(
            "-fx-padding:15;" +
                "-fx-border-color:#E2E8F0;" +
                "-fx-border-radius:10;"
        );

        card.getChildren().add(
            new Label(name.toUpperCase())
        );

        // Yan etkiler varsa eklenir
        if (info != null &&
            info.getAdditional_side_effects() != null) {

            for (String effect :
                info.getAdditional_side_effects()) {

                card.getChildren().add(
                    new Label("• " + effect)
                );
            }
        }

        card.setEffect(new DropShadow(10, Color.rgb(0, 0, 0, 0.1)));
        return card;
    }

    @FXML
    public void handleContactDoctor(ActionEvent event) {

        // Doktorla iletişim penceresi açılır
        try {
            Parent root = FXMLLoader.load(
                getClass().getResource(
                    "/com/follow_disease/PatientContactDoctor.fxml")
            );

            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.show();

        } catch (Exception ignored) {
        }
    }
}
