package com.follow_disease.controller;

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

  // Profil ikonu ve Güncelle butonu (Gerektiğinde renk vs. değiştirmek için)
  @FXML private Circle profileCircle;
  @FXML private Button updateButton;

  // Bildirim sistemi
  @FXML private MenuButton notificationMenuButton;
  @FXML private Circle notificationBadge;

  // Bu metot FXML dosyası yüklendiğinde otomatik olarak çalışır.
  @FXML
  public void initialize() {
    System.out.println("Hasta sayfası başarıyla yüklendi.");

    User u = Session.getCurrentUser();
    if (u != null) {
      nameLabel.setText(safe(u.getName()) + " " + safe(u.getSurname()));
      emailLabel.setText(safe(u.getEmail()));
      ageLabel.setText(safe(u.getAge()));
      genderLabel.setText(safe(u.getGender()));
    }

    loadNotifications();
  }

  private String safe(String s) {
    return s == null ? "" : s.trim();
  }

  // Bilgileri Güncelle butonuna tıklandığında çalışacak olan metot

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
    PasswordField pass = new PasswordField();
    pass.setPromptText("Yeni şifre (boş bırak -> değişmesin)");

    GridPane grid = new GridPane();
    grid.setHgap(10); grid.setVgap(10);
    int r = 0;
    grid.addRow(r++, new Label("Yaş:"), age);
    grid.addRow(r++, new Label("Cinsiyet:"), gender);
    grid.addRow(r++, new Label("Tel:"), phone);
    grid.addRow(r++, new Label("Mail:"), email);
    grid.addRow(r++, new Label("Şifre:"), pass);

    dialog.getDialogPane().setContent(grid);

    ButtonType result = dialog.showAndWait().orElse(ButtonType.CANCEL);
    if (result != ButtonType.OK) return;

    boolean ok = ProfileService.updatePatient(u, age.getText(), gender.getText(), phone.getText(), email.getText(), pass.getText());
    if (!ok) {
      new Alert(Alert.AlertType.ERROR, "Güncelleme başarısız!", ButtonType.OK).showAndWait();
      return;
    }

    // UI refresh
    User nu = Session.getCurrentUser();
    nameLabel.setText(safe(nu.getName()) + " " + safe(nu.getSurname()));
    emailLabel.setText(safe(nu.getEmail()));
    ageLabel.setText(safe(nu.getAge()));
    genderLabel.setText(safe(nu.getGender()));

    new Alert(Alert.AlertType.INFORMATION, "Profil güncellendi ✅", ButtonType.OK).showAndWait();
  }

  @FXML
  public void handleLogout(ActionEvent event) {
    // Burada giriş ekranına yönlendirme kodunu yazacağız
    System.out.println("Çıkış yapılıyor...");
    Session.clear();
  }

  // Bildirim Sistemi
  private void loadNotifications() {
    notificationMenuButton.getItems().clear();

    // Örnek bildirimler - gerçek veritabanı verilerinizle değiştirilecek
    CustomMenuItem item1 = createNotificationItem("İlaç hatırlatması: Lisinopril", "2 saat önce");
    CustomMenuItem item2 = createNotificationItem("Doktor randevusu yarın saat 14:00", "5 saat önce");
    CustomMenuItem item3 = createNotificationItem("Yeni test sonuçlarınız hazır", "1 gün önce");

    notificationMenuButton.getItems().addAll(item1, item2, item3);

    // Bildirim sayısına göre gösterebip gösterilmeyeceği seçecek
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

    // Bildirime tıklandığında ne yapılacak
    content.setOnMouseClicked(e -> {
      System.out.println("Bildirime tıklandı: " + title);
    });

    return item;
  }

  // Bildirim sayısını güncelleme metodu zorunlu değil
  public void updateNotificationCount(int count) {
    notificationBadge.setVisible(count > 0);
  }

  // Dinamik olarak yeni bildirim ekleme metodu zorunlu değil
  public void addNotification(String title, String time) {
    CustomMenuItem newItem = createNotificationItem(title, time);
    notificationMenuButton.getItems().add(0, newItem); // En üste ekle
    notificationBadge.setVisible(true);
  }

  // Tüm bildirimleri temizleme metodu zorunlu değil
  public void clearNotifications() {
    notificationMenuButton.getItems().clear();
    notificationBadge.setVisible(false);
  }
}