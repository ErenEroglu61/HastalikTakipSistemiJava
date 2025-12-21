package com.follow_disease.controller;

import com.follow_disease.Doctor;
import com.follow_disease.Patient;
import com.follow_disease.User;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.MenuButton;
import javafx.scene.control.CustomMenuItem;
import javafx.scene.layout.*;
import javafx.scene.shape.Circle;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.List;

public class DoctorController {

  @FXML private VBox patientListContainer;
  @FXML private Label doctorNameLabel;
  @FXML private Label doctorRoleLabel;
  @FXML private Label emailLabel;
  @FXML private Label branchLabel;
  @FXML private Label titleLabel;
  @FXML private MenuButton notificationMenuButton;
  @FXML private Circle notificationBadge;

  // Şimdilik test maili; login hazır olduğunda LoginController'dan gelecek
  private String loggedInEmail = "erdem@hastane.com";
  private int currentDoctorId;

  @FXML
  public void initialize() {
        // 1. Bildirim ve Zil Ayarları
        if (notificationMenuButton != null) {
            Label bellIcon = new Label("\uD83D\uDD14");
            bellIcon.setStyle("-fx-font-size: 18; -fx-text-fill: #1976D2;");
            notificationMenuButton.setGraphic(bellIcon);
            notificationMenuButton.setText("");
        }

        loadNotifications();

        // 2. Verileri Yükle
        loadDataAndInitializeUI();
    }

    private void loadDataAndInitializeUI() {
        Gson gson = new Gson();
        patientListContainer.getChildren().clear(); // Listeyi her seferinde temizle
        String doctorTc = "";

        // user.json içinden giriş yapan e-postaya ait TC No'yu bulalım
        try (FileReader reader = new FileReader("database/user.json")) {
            Type listType = new TypeToken<List<com.google.gson.JsonObject>>(){}.getType();
            List<com.google.gson.JsonObject> users = gson.fromJson(reader, listType);

            if (users != null) {
                for (com.google.gson.JsonObject userObj : users) {
                    if (userObj.has("email") && userObj.get("email").getAsString().equals(loggedInEmail)) {
                        doctorTc = userObj.get("tc").getAsString();
                        break;
                    }
                }
            }
        } catch (IOException e) {
            System.err.println("User dosyası okuma hatası: " + e.getMessage());
        }

        //  TC bulunduysa doctors.json'dan mesleki bilgileri alıyoruz
        if (doctorTc != null && !doctorTc.isEmpty()) {
            try (FileReader reader = new FileReader("database/doctors.json")) {
                Type docListType = new TypeToken<List<Doctor>>(){}.getType();
                List<Doctor> allDoctors = gson.fromJson(reader, docListType);

                if (allDoctors != null) {
                    for (Doctor doc : allDoctors) {
                        if (doc.getTc() != null && doc.getTc().equals(doctorTc)) {
                            this.currentDoctorId = doc.getId(); // Hastaları bulmak için lazım

                            // Ekranı doldur
                            doctorNameLabel.setText(doc.getWelcomeMessage());
                            doctorRoleLabel.setText(doc.getRoleDescription());
                            emailLabel.setText(loggedInEmail);
                            branchLabel.setText(doc.getBranch());
                            titleLabel.setText(doc.getMedical_title());
                            break;
                        }
                    }
                }
            } catch (IOException e) {
                System.err.println("Doctor dosyası okuma hatası.");
            }
        }

        //  Hastaları patients.json içinden yükle

        try (FileReader reader = new FileReader("database/patients.json")) {
            Type patientListType = new TypeToken<List<Patient>>(){}.getType();
            List<Patient> allPatients = gson.fromJson(reader, patientListType);

            if (allPatients != null) {
                for (Patient p : allPatients) {
                    // Sadece bu doktorun ID'sine sahip hastaları ekle
                    if (p.getDoctor_id() == this.currentDoctorId) {
                        addPatientCard(p);
                    }
                }
            }
        } catch (IOException e) {
            System.err.println("Patient dosyası okuma hatası.");
        }
    }
  private void addPatientCard(Patient patient) {
    HBox card = new HBox(20);
    card.setStyle("-fx-background-color: white; -fx-padding: 20; -fx-background-radius: 12; " +
      "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.08), 8, 0, 0, 4); -fx-alignment: center-left;");

    VBox infoBox = new VBox(5);
    Label nameLabel = new Label(patient.getName() + " " + patient.getSurname());
    nameLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 15px;");
    Label detailLabel = new Label(patient.getRoleDescription()); // Yaş ve Tanı bilgisi
    detailLabel.setStyle("-fx-text-fill: #7f8c8d;");
    infoBox.getChildren().addAll(nameLabel, detailLabel);

    Region spacer = new Region();
    HBox.setHgrow(spacer, Priority.ALWAYS);

    Button btnDetails = new Button("Detayları Gör");
    btnDetails.setStyle("-fx-background-color: #d32f2f; -fx-text-fill: white; -fx-font-weight: bold; -fx-cursor: hand; -fx-padding: 8 18; -fx-background-radius: 5;");

    btnDetails.setOnAction(e -> openPatientPopup(patient));

    card.getChildren().addAll(infoBox, spacer, btnDetails);
    patientListContainer.getChildren().add(card);
  }

  private void openPatientPopup(Patient patient) {
    try {
      FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/patientDetailsPopup.fxml"));
      Parent root = loader.load();

      // Pop-up ekranına hastayı gönderen mantık buraya gelecek

      Stage stage = new Stage();
      stage.initModality(Modality.APPLICATION_MODAL);
      stage.setTitle("Hasta Detay: " + patient.getName());
      stage.setScene(new Scene(root));
      stage.show();
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  // Bildirim Sistemi Metodları
  private void loadNotifications() {
    notificationMenuButton.getItems().clear();

    // Geçici bildirimler
    CustomMenuItem item1 = createNotificationItem("Yeni hasta kaydı: Ahmet Demir", "5 dakika önce");
    CustomMenuItem item2 = createNotificationItem("Randevu hatırlatması: Ayşe Yılmaz", "15 dakika önce");
    CustomMenuItem item3 = createNotificationItem("Test sonucu hazır: Mehmet Kaya", "1 saat önce");

    notificationMenuButton.getItems().addAll(item1, item2, item3);

    // Bildirim sayısına göre gösterip göstermeyeceği
    int notificationCount = notificationMenuButton.getItems().size();
    notificationBadge.setVisible(notificationCount > 0);
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

    // Bildirime tıklandığında yapılacak işlem
    content.setOnMouseClicked(e -> {
      System.out.println("Bildirime tıklandı: " + title);
      // Çalıştığını kontrol için
    });

    return item;
  }

  // Bildirim sayısını güncelleme zorunlu değil
  public void updateNotificationCount(int count) {
    notificationBadge.setVisible(count > 0);
  }

  // Zorunlu değil yeni bildirim oluşturmak için
  public void addNotification(String title, String time) {
    CustomMenuItem newItem = createNotificationItem(title, time);
    notificationMenuButton.getItems().add(0, newItem); // En üste ekle
    notificationBadge.setVisible(true);
  }

  @FXML
  private void handleLogout() {
    System.out.println("Oturum kapatıldı.");
    // Login ekranına yönlendirme kodu...
  }

  // JSON'dan nesne tipine göre parse eden yardımcı metot (Basitleştirilmiş)
  private List<User> parseUsersFromJson(FileReader reader) {
    // GSON kütüphanesi ile verileri uygun sınıflara (Doctor/Patient) eşleme mantığı
    // Burayı projenin geri kalanındaki JSON okuma yapına göre doldurabilirsin.
    return new java.util.ArrayList<>();
  }
}