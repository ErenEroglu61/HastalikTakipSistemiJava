package com.follow_disease.controller;

import com.follow_disease.Patient;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.MenuButton;
import javafx.scene.control.CustomMenuItem;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Circle;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.effect.DropShadow;
import javafx.scene.paint.Color;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.scene.Node;
import java.io.IOException;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;

import com.follow_disease.User;
import com.follow_disease.Session;
import com.follow_disease.service.ProfileService;

public class PatientController {

  // Sol paneldeki Label'lar (FXML'deki fx:id'ler ile birebir aynı olmalı)
  @FXML private Label nameLabel;
  @FXML private Label emailLabel;
  @FXML private Label ageLabel;
  @FXML private Label genderLabel;
  @FXML private Label phoneLabel;
  @FXML private Label tcLabel;
  @FXML private Label passwordLabel;
  @FXML private VBox vboxActiveDiseases;
  @FXML private VBox vboxDiseasesHistory;
  @FXML private Circle profileCircle;
  @FXML private Button updateButton;

  // Bildirim sistemi
  @FXML private MenuButton notificationMenuButton;
  @FXML private Circle notificationBadge;

  @FXML
  public void initialize() {
    User u = Session.getCurrentUser();
    if (u != null) {
      nameLabel.setText(safe(u.getName()) + " " + safe(u.getSurname()));
      emailLabel.setText(safe(u.getEmail()));
      ageLabel.setText(safe(u.getAge()));
      genderLabel.setText(safe(u.getGender()));
      tcLabel.setText(safe(u.getTc()));
      phoneLabel.setText(safe(u.getPhone()));

      if (u.getPassword() != null && !u.getPassword().isEmpty()) {
            passwordLabel.setText("*".repeat(u.getPassword().length()));
        } else {
            passwordLabel.setText("");
        }

      Patient patient = findPatientByTc(u.getTc());
      if (patient != null) {
        loadMedicalData(patient);
      }
    }
    loadNotifications();
  }

  private String safe(String s) {
    return s == null ? "" : s.trim();
  }

  @FXML
  public void handleUpdateAction(ActionEvent event) {
        User u = Session.getCurrentUser();
        if (u == null) return;

        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle("Profil Güncelle");
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        TextField age = new TextField(safe(u.getAge()));
        TextField gender = new TextField(safe(u.getGender()));
        TextField phone = new TextField(safe(u.getPhone()));
        TextField email = new TextField(safe(u.getEmail()));

        // 1. DEĞİŞİKLİK: Pencere içinde de şifre gizli yazılmalı
        PasswordField passwordField = new PasswordField();
        passwordField.setText(safe(u.getPassword()));

        GridPane grid = new GridPane();
        grid.setHgap(10); grid.setVgap(10);
        int r = 0;
        grid.addRow(r++, new Label("Yaş:"), age);
        grid.addRow(r++, new Label("Cinsiyet:"), gender);
        grid.addRow(r++, new Label("Tel:"), phone);
        grid.addRow(r++, new Label("Mail:"), email);
        grid.addRow(r++, new Label("Şifre:"), passwordField); // passwordField kullanıldı

        dialog.getDialogPane().setContent(grid);

        ButtonType result = dialog.showAndWait().orElse(ButtonType.CANCEL);
        if (result != ButtonType.OK) return;

        // Sıralama: email -> passwordField.getText()
        boolean ok = ProfileService.updatePatient(u, age.getText(), gender.getText(), phone.getText(), email.getText(), passwordField.getText());

        if(ok) {
            User nu = Session.getCurrentUser();

            ageLabel.setText(safe(nu.getAge()));
            genderLabel.setText(safe(nu.getGender()));
            phoneLabel.setText(safe(nu.getPhone()));
            emailLabel.setText(safe(nu.getEmail()));

            // 2. DEĞİŞİKLİK: Yıldızlı gösterme kısmı sadece başarılıysa çalışmalı
            if (nu.getPassword() != null && !nu.getPassword().isEmpty()) {
                passwordLabel.setText("*".repeat(nu.getPassword().length()));
            } else {
                passwordLabel.setText("");
            }
        }
  }
  @FXML
  public void handleLogout(ActionEvent event) {
    Session.clear();

    try {
      FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/follow_disease/login.fxml"));
      Parent root = loader.load();
      Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
      stage.setScene(new Scene(root));
      stage.setTitle("Hastalık Takip Sistemi - Giriş");
      stage.show();
    } catch (IOException e) {
      System.err.println("Login sayfası yüklenemedi: " + e.getMessage());
      e.printStackTrace();
      new Alert(Alert.AlertType.ERROR, "Giriş sayfası açılamadı.", ButtonType.OK).showAndWait();
    }
  }

  private Patient findPatientByTc(String tc) {
    if (tc == null || tc.isEmpty()) return null;

    com.google.gson.Gson gson = new com.google.gson.Gson();

    try (java.io.FileReader reader = new java.io.FileReader("database/patients.json")) {
      java.lang.reflect.Type patientListType = new com.google.gson.reflect.TypeToken<java.util.List<Patient>>(){}.getType();
      java.util.List<Patient> allPatients = gson.fromJson(reader, patientListType);

      if (allPatients != null) {
        for (Patient p : allPatients) {
          if (tc.equals(p.getTc())) {
            return p;
          }
        }
      }
    } catch (java.io.IOException e) {
      System.err.println("patients.json okunurken hata oluştu: " + e.getMessage());
    }

    return null;
  }

  private void loadMedicalData(Patient patient) {
    vboxActiveDiseases.getChildren().clear();
    vboxDiseasesHistory.getChildren().clear();

    if (patient.getCurrent_disease() != null && !patient.getCurrent_disease().isEmpty()) {
      vboxActiveDiseases.getChildren().add(createSimpleCard(patient.getCurrent_disease(), "#D32F2F"));
    }

    if (patient.getDisease_history() != null) {
      for (String disease : patient.getDisease_history()) {
        vboxDiseasesHistory.getChildren().add(createSimpleCard(disease, "#64748B"));
      }
    }

    if (patient.getAdditional_medicines() != null) {
      for (String medicine : patient.getAdditional_medicines()) {
        // vboxActiveMedicines.getChildren().add(createSimpleCard(medicine, "#1976D2"));
      }
    }
  }

  private VBox createSimpleCard(String text, String color) {
    VBox card = new VBox();
    card.setSpacing(5);
    card.setStyle("-fx-background-color: white; " +
      "-fx-border-color: #E2E8F0; " +
      "-fx-border-width: 1; " +
      "-fx-background-radius: 10; " +
      "-fx-border-radius: 10; " +
      "-fx-padding: 15;");

    Label title = new Label(text.toUpperCase());
    title.setStyle("-fx-font-weight: bold; -fx-font-size: 13; -fx-text-fill: " + color + ";");

    card.getChildren().add(title);
    DropShadow shadow = new DropShadow(10, 0, 2, Color.rgb(0, 0, 0, 0.1));
    card.setEffect(shadow);

    return card;
  }

  public void handleContactDoctor(ActionEvent event) {
    try {
      java.net.URL fxmlLocation = getClass().getResource("/com/follow_disease/PatientContactDoctor.fxml");

      Parent root = FXMLLoader.load(fxmlLocation);

      Stage stage = new Stage();
      stage.setScene(new Scene(root));
      stage.setTitle("Doktor Bilgilendirme Formu");
      stage.initModality(Modality.APPLICATION_MODAL);
      stage.show();

    } catch (Exception e) {
      System.err.println("Yükleme sırasında teknik bir hata oluştu: " + e.getMessage());
      e.printStackTrace();
    }
  }

  private void loadNotifications() {
    notificationMenuButton.getItems().clear();

    CustomMenuItem item1 = createNotificationItem("İlaç hatırlatması: Lisinopril", "2 saat önce");
    CustomMenuItem item2 = createNotificationItem("Doktor randevusu yarın saat 14:00", "5 saat önce");
    CustomMenuItem item3 = createNotificationItem("Yeni test sonuçlarınız hazır", "1 gün önce");

    notificationMenuButton.getItems().addAll(item1, item2, item3);

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

    content.setOnMouseClicked(e -> {
      System.out.println("Bildirime tıklandı: " + title);
    });

    return item;
  }

  public void updateNotificationCount(int count) {
    notificationBadge.setVisible(count > 0);
  }

  public void addNotification(String title, String time) {
    CustomMenuItem newItem = createNotificationItem(title, time);
    notificationMenuButton.getItems().add(0, newItem);
    notificationBadge.setVisible(true);
  }

  public void clearNotifications() {
    notificationMenuButton.getItems().clear();
    notificationBadge.setVisible(false);
  }
}