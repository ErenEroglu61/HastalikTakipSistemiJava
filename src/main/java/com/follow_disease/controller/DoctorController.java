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
import javafx.scene.layout.*;
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

    // Şimdilik test maili; login hazır olduğunda LoginController'dan gelecek
    private String loggedInEmail = "erdem@hastane.com";
    private int currentDoctorId;

    @FXML
    public void initialize() {
        loadDataAndInitializeUI();
    }

    private void loadDataAndInitializeUI() {
        try (FileReader reader = new FileReader("database/users.json")) {
            Gson gson = new Gson();
            Type listType = new TypeToken<List<Object>>(){}.getType(); // Ham liste olarak oku
            // Not: Gerçek projede özel bir Deserializer yazmak daha iyidir
            // ama şimdilik manuel eşleme ile ilerliyoruz.
            List<User> allUsers = parseUsersFromJson(reader);

            patientListContainer.getChildren().clear();

            // 1. Önce giriş yapan doktoru bul
            for (User user : allUsers) {
                if (user instanceof Doctor && user.getEmail().equals(loggedInEmail)) {
                    Doctor currentDoc = (Doctor) user;
                    this.currentDoctorId = currentDoc.getId();

                    // Polimorfik Metotları Kullanma
                    doctorNameLabel.setText(currentDoc.getWelcomeMessage());
                    doctorRoleLabel.setText(currentDoc.getRoleDescription());
                    break;
                }
            }

            // 2. Bu doktora bağlı hastaları listele
            for (User user : allUsers) {
                if (user instanceof Patient) {
                    Patient p = (Patient) user;
                    if (p.getDoctor_id() == currentDoctorId) {
                        addPatientCard(p);
                    }
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
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