package com.follow_disease.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.shape.Circle;

public class PatientController {

    // Sol paneldeki Label'lar (FXML'deki fx:id'ler ile birebir aynı olmalı)
    @FXML private Label nameLabel;
    @FXML private Label emailLabel;
    @FXML private Label ageLabel;
    @FXML private Label genderLabel;


    // Profil ikonu ve Güncelle butonu (Gerektiğinde renk vs. değiştirmek için)
    @FXML private Circle profileCircle;
    @FXML private Button updateButton;


      //Bu metot FXML dosyası yüklendiğinde otomatik olarak çalışır.

    @FXML
    public void initialize() {
        System.out.println("Hasta sayfası başarıyla yüklendi.");

        // Şimdilik JSON'dan çekmediğimiz için varsayılan örnek veriler koyalım
        // İleride UserService.getCurrentUser() ile buraları dolduracağız
        nameLabel.setText("Ahmet Yılmaz");
        emailLabel.setText("ahmet@mail.com");
        ageLabel.setText("34");
        genderLabel.setText("Erkek");
    }




     //Bilgileri Güncelle butonuna tıklandığında çalışacak olan metot

    @FXML
    public void handleUpdateAction(ActionEvent event) {
        System.out.println("Bilgileri Güncelle butonuna tıklandı!");
        // İleride buraya güncelleme formunun açılma kodlarını yazacağız
    }

    @FXML
    public void handleLogout(ActionEvent event) {
        // Burada giriş ekranına yönlendirme kodunu yazacağız
        System.out.println("Çıkış yapılıyor...");
    }
}