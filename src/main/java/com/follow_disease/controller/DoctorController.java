package com.follow_disease.controller;

import com.follow_disease.Doctor;
import com.follow_disease.Patient;
import com.follow_disease.User;
import com.follow_disease.service.JsonDb;
import com.follow_disease.service.ProfileService;
import com.follow_disease.Session;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.effect.DropShadow;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.List;

public class DoctorController {

    @FXML private VBox patientListContainer;

    // Üst kısım
    @FXML private Label doctornameLabel;     // FXML’de senin id: doctornameLabel
    @FXML private Label doctorRoleLabel;

    // Sol kartlar
    @FXML private Label emailLabel;
    @FXML private Label branchLabel;
    @FXML private Label titleLabel;

    // EKLEYECEĞİN kartlar
    @FXML private Label ageLabel;
    @FXML private Label genderLabel;
    @FXML private Label phoneLabel;
    @FXML private Label tcLabel;

    // Bildirim
    @FXML private MenuButton notificationMenuButton;
    @FXML private Circle notificationBadge;

    private int currentDoctorId = -1;

    @FXML
    public void initialize() {
        if (notificationMenuButton != null) {
            Label bellIcon = new Label("\uD83D\uDD14");
            bellIcon.setStyle("-fx-font-size: 18; -fx-text-fill: #1976D2;");
            notificationMenuButton.setGraphic(bellIcon);
            notificationMenuButton.setText("");
        }

        loadNotifications();
        loadDoctorUIFromSession();
        loadPatientsForDoctor();
    }

    private void loadDoctorUIFromSession() {
        User u = Session.getCurrentUser();
        if (u == null) {
            System.err.println("Session currentUser null! Login sonrası Session.setCurrentUser() çağrılmalı.");
            return;
        }

        // Sol panel: user.json’dan gelenler
        doctornameLabel.setText(safe(u.getName()) + " " + safe(u.getSurname()));
        doctorRoleLabel.setText("Doktor Profili");
        emailLabel.setText(safe(u.getEmail()));

        ageLabel.setText(safe(u.getAge()));
        genderLabel.setText(safe(u.getGender()));
        phoneLabel.setText(safe(u.getPhone()));
        tcLabel.setText(safe(u.getTc())); // user.json alan adı tcNo

        // doctors.json’dan branch/title
        Doctor d = JsonDb.findDoctorByTc(u.getTc());
        if (d != null) {
            currentDoctorId = d.getId();
            branchLabel.setText(safe(d.getBranch()));
            titleLabel.setText(safe(d.getMedical_title()));
        } else {
            // Doktor TC doctors.json’da yoksa yine UI boş kalmasın
            branchLabel.setText("-");
            titleLabel.setText("-");
            currentDoctorId = -1;
        }
    }

    private void loadPatientsForDoctor() {
        patientListContainer.getChildren().clear();
        if (currentDoctorId == -1) return;

        Gson gson = new Gson();
        try (FileReader reader = new FileReader("database/patients.json")) {
            Type patientListType = new TypeToken<List<Patient>>(){}.getType();
            List<Patient> allPatients = gson.fromJson(reader, patientListType);

            if (allPatients != null) {
                for (Patient p : allPatients) {
                    if (p.getDoctor_id() == currentDoctorId) {
                        addPatientCard(p);
                    }
                }
            }
        } catch (IOException e) {
            System.err.println("Patient dosyası okuma hatası: " + e.getMessage());
        }
    }

    // PROFİLİMİ GÜNCELLE butonu
    @FXML
    private void handleUpdateProfile(ActionEvent event) {
        User u = Session.getCurrentUser();
        if (u == null) return;

        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle("Profil Güncelle (Doktor)");
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        TextField age = new TextField(safe(u.getAge()));
        TextField gender = new TextField(safe(u.getGender()));
        TextField phone = new TextField(safe(u.getPhone()));
        PasswordField pass = new PasswordField();
        pass.setPromptText("Yeni şifre (boş bırak -> değişmesin)");

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);

        int r = 0;
        grid.addRow(r++, new Label("Yaş:"), age);
        grid.addRow(r++, new Label("Cinsiyet:"), gender);
        grid.addRow(r++, new Label("Tel No:"), phone);
        grid.addRow(r++, new Label("Şifre:"), pass);

        dialog.getDialogPane().setContent(grid);

        ButtonType res = dialog.showAndWait().orElse(ButtonType.CANCEL);
        if (res != ButtonType.OK) return;

        boolean ok = ProfileService.updateDoctor(u, age.getText(), gender.getText(), phone.getText(), pass.getText());
        if (!ok) {
            new Alert(Alert.AlertType.ERROR, "Güncelleme başarısız!", ButtonType.OK).showAndWait();
            return;
        }

        // UI refresh
        User nu = Session.getCurrentUser();
        ageLabel.setText(safe(nu.getAge()));
        genderLabel.setText(safe(nu.getGender()));
        phoneLabel.setText(safe(nu.getPhone()));

        new Alert(Alert.AlertType.INFORMATION, "Profil güncellendi ✅", ButtonType.OK).showAndWait();
    }

    private void addPatientCard(Patient patient) {
        HBox card = new HBox(20);
        card.setStyle("-fx-background-color: white; " +
                "-fx-background-radius: 15; " +
                "-fx-border-color: #1976D2; " +
                "-fx-border-width: 1.5; " +
                "-fx-border-radius: 15; " +
                "-fx-padding: 15; " +
                "-fx-alignment: center-left;");

        DropShadow blueShadow = new DropShadow();
        blueShadow.setColor(Color.web("#1976D233"));
        blueShadow.setRadius(15);
        blueShadow.setOffsetY(5);
        card.setEffect(blueShadow);

        VBox infoBox = new VBox(5);
        Label name = new Label(patient.getName() + " " + patient.getSurname());
        name.setStyle("-fx-font-weight: bold; -fx-font-size: 16px; -fx-text-fill: #334155;");

        String dateInfo = patient.getAppointmentDate() != null ? patient.getAppointmentDate() : "Tarih Belirtilmedi";
        String diseaseInfo = patient.getCurrent_disease() != null ? patient.getCurrent_disease() : "Tanı Konulmadı";

        Label details = new Label("📅 " + dateInfo + "  •  Durum: " + diseaseInfo);
        details.setStyle("-fx-text-fill: #64748B; -fx-font-size: 13px;");

        infoBox.getChildren().addAll(name, details);

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        Button btnDetails = new Button("Detayları Gör");
        btnDetails.setStyle("-fx-background-color: #1976D2; -fx-text-fill: white; -fx-font-weight: bold; " +
                "-fx-background-radius: 10; -fx-cursor: hand; -fx-padding: 8 18;");
        btnDetails.setOnAction(e -> openPatientPopup(patient));

        card.getChildren().addAll(infoBox, spacer, btnDetails);
        patientListContainer.getChildren().add(card);
    }

    private void openPatientPopup(Patient patient) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/patientDetailsPopup.fxml"));
            Parent root = loader.load();

            Stage stage = new Stage();
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setTitle("Hasta Detay: " + patient.getName());
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void loadNotifications() {
        notificationMenuButton.getItems().clear();

        CustomMenuItem item1 = createNotificationItem("Yeni hasta kaydı: Ahmet Demir", "5 dakika önce");
        CustomMenuItem item2 = createNotificationItem("Randevu hatırlatması: Ayşe Yılmaz", "15 dakika önce");
        CustomMenuItem item3 = createNotificationItem("Test sonucu hazır: Mehmet Kaya", "1 saat önce");

        notificationMenuButton.getItems().addAll(item1, item2, item3);
        notificationBadge.setVisible(notificationMenuButton.getItems().size() > 0);
    }

    private CustomMenuItem createNotificationItem(String title, String time) {
        VBox content = new VBox(5);
        content.setStyle("-fx-padding: 10; -fx-min-width: 280; -fx-background-color: white;");
        content.setOnMouseEntered(e -> content.setStyle("-fx-padding: 10; -fx-min-width: 280; -fx-background-color: #F5F5F5; -fx-cursor: hand;"));
        content.setOnMouseExited(e -> content.setStyle("-fx-padding: 10; -fx-min-width: 280; -fx-background-color: white;"));

        Label titleLabel = new Label(title);
        titleLabel.setStyle("-fx-font-weight: bold; -fx-text-fill: #334155; -fx-font-size: 13;");
        titleLabel.setWrapText(true);
        titleLabel.setMaxWidth(260);

        Label timeLabel = new Label(time);
        timeLabel.setStyle("-fx-font-size: 11; -fx-text-fill: #64748B;");

        content.getChildren().addAll(titleLabel, timeLabel);

        CustomMenuItem item = new CustomMenuItem(content);
        item.setHideOnClick(false);

        content.setOnMouseClicked(e -> System.out.println("Bildirime tıklandı: " + title));
        return item;
    }

    @FXML
    private void handleLogout() {
        System.out.println("Oturum kapatıldı.");
        Session.clear();
        // Login ekranına yönlendirme kodun sende nasıl ise aynı şekilde devam
    }

    private String safe(String s) {
        return s == null ? "" : s.trim();
    }
}
