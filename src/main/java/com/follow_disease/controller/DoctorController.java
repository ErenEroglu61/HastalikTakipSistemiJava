package com.follow_disease.controller;

import com.follow_disease.*;
import com.follow_disease.service.JsonDb;
import com.follow_disease.service.ProfileService;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
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
import javafx.scene.Node;

import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.List;
import java.util.Objects;

public class DoctorController implements Notification {

  @FXML private VBox patientListContainer;
  @FXML private TableView<Patient> patientTable;

  @FXML private Label doctornameLabel;
  @FXML private Label doctorRoleLabel;

  @FXML private Label emailLabel;
  @FXML private Label branchLabel;
  @FXML private Label titleLabel;
  @FXML private Label ageLabel;
  @FXML private Label genderLabel;
  @FXML private Label phoneLabel;
  @FXML private Label tcLabel;
  @FXML private Label passwordLabel;

  // Bildirim
  @FXML private MenuButton notificationMenuButton;
  @FXML private Circle notificationBadge;

  private int currentDoctorId = -1;

  @FXML
    public void handleNotificationShowing() {
        User u = Session.getCurrentUser();
        if (u != null) {
            // clearNotifications(TC, isDoctor) -> isDoctor burada TRUE olmalı
            clearNotifications(u.getTc(), true);

            // Kırmızı noktayı anında kapat
            notificationBadge.setVisible(false);
        }
    }

    @FXML
  public void initialize() {

        updateNotificationUI();
        loadDoctorUIFromSession();
        loadPatientsForDoctor();
  }

            @Override
            public List<String> getNotifications() {
                User u = Session.getCurrentUser();
                if (u != null) {
                    // JsonDb sınıfındaki metodunu kullanarak en güncel doktor verisini çekiyoruz
                    Doctor d = JsonDb.findDoctorByTc(u.getTc());
                    return d != null ? d.getNotifications() : new java.util.ArrayList<>();
                }
                return new java.util.ArrayList<>();
            }

            @Override
            public void setNotifications(List<String> notifications) {}

            @Override
            public void updateNotificationUI() {
                notificationMenuButton.getItems().clear();
                List<String> notifications = getNotifications();

                if (notifications != null && !notifications.isEmpty()) {
                    notificationBadge.setVisible(true);
                    for (String msg : notifications) {
                        Label label = new Label(msg);
                        label.setStyle(
                                "-fx-font-family: 'Segoe UI Semibold'; " +
                                        "-fx-font-size: 13px; " +
                                        "-fx-text-fill: #1e293b; " +
                                        "-fx-padding: 8 15 8 15;"
                        );
                        CustomMenuItem item = new CustomMenuItem(label);
                        notificationMenuButton.getItems().add(item);
                    }
                } else {
                    notificationBadge.setVisible(false);
                    notificationMenuButton.getItems().add(new MenuItem("Bildirim yok"));
                }
            }

            private void loadDoctorUIFromSession() {
                User u = Session.getCurrentUser();
                if (u == null) {
                    System.err.println("Session currentUser null! Login sonrası Session.setCurrentUser() çağrılmalı.");
                    return;
                }

                // Sol panel: user.json'dan gelenler
                doctornameLabel.setText(safe(u.getName()) + " " + safe(u.getSurname()));
                doctorRoleLabel.setText("Doktor Profili");
                emailLabel.setText(safe(u.getEmail()));

                ageLabel.setText(safe(u.getAge()));
                genderLabel.setText(safe(u.getGender()));
                phoneLabel.setText(safe(u.getPhone()));
                tcLabel.setText(safe(u.getTc()));

                if (u.getPassword() != null && !u.getPassword().isEmpty()) {
                    // Sayfa ilk açıldığında şifre uzunluğu kadar yıldız koyar
                    passwordLabel.setText("*".repeat(u.getPassword().length()));
                } else {
                    passwordLabel.setText("");
                }

                // doctors.json'dan branch/title
                Doctor d = JsonDb.findDoctorByTc(u.getTc());
                if (d != null) {
                    currentDoctorId = d.getId();
                    branchLabel.setText(safe(d.getBranch()));
                    titleLabel.setText(safe(d.getMedical_title()));
                } else {
                    // Doktor TC doctors.json'da yoksa yine UI boş kalmasın
                    branchLabel.setText("-");
                    titleLabel.setText("-");
                    currentDoctorId = -1;
                }
            }

            private void loadPatientsForDoctor() {
                patientListContainer.getChildren().clear();
                if (currentDoctorId == -1) return;

                Gson gson = new GsonBuilder().setPrettyPrinting().create();
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

            @FXML
            private void handleUpdateProfile(ActionEvent event) {
                User u = Session.getCurrentUser();
                if (u == null) return;

                Dialog<ButtonType> dialog = new Dialog<>();
                dialog.setTitle("Profil Güncelle (Doktor)");
                dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

                // Giriş alanlarını oluşturma
                TextField age = new TextField(safe(u.getAge()));
                TextField gender = new TextField(safe(u.getGender()));
                TextField phone = new TextField(safe(u.getPhone()));
                TextField email = new TextField(safe(u.getEmail()));

                // ŞİFRE ALANI: PasswordField kullanıyoruz (yazarken görünmez)
                PasswordField passwordField = new PasswordField();
                passwordField.setText(safe(u.getPassword()));

                GridPane grid = new GridPane();
                grid.setHgap(10);
                grid.setVgap(10);

                int r = 0;
                grid.addRow(r++, new Label("Yaş:"), age);
                grid.addRow(r++, new Label("Cinsiyet:"), gender);
                grid.addRow(r++, new Label("Tel No:"), phone);
                grid.addRow(r++, new Label("E-mail:"), email);
                grid.addRow(r++, new Label("Şifre:"), passwordField);

                dialog.getDialogPane().setContent(grid);

                ButtonType res = dialog.showAndWait().orElse(ButtonType.CANCEL);
                if (res != ButtonType.OK) return;

                // SIRALAMA DİKKAT: u, age, gender, phone, email, password
                boolean ok = ProfileService.updateDoctor(u,
                        age.getText(),
                        gender.getText(),
                        phone.getText(),
                        email.getText(),
                        passwordField.getText());

                if (ok) {
                    // Nesneyi güncelle
                    u.setAge(age.getText());
                    u.setGender(gender.getText());
                    u.setPhone(phone.getText());
                    u.setEmail(email.getText());
                    u.setPassword(passwordField.getText());

                    // Arayüzdeki (UI) etiketleri güncelle
                    ageLabel.setText(u.getAge());
                    genderLabel.setText(u.getGender());
                    phoneLabel.setText(u.getPhone());
                    emailLabel.setText(u.getEmail());

                    // Şifreyi ana ekranda yıldızlı gösterme
                    if (u.getPassword() != null && !u.getPassword().isEmpty()) {
                        passwordLabel.setText("*".repeat(u.getPassword().length()));
                    } else {
                        passwordLabel.setText("");
                    }
                }
            }

            private void addPatientCard(Patient patient) {
                HBox card = new HBox(20);
                card.setStyle("-fx-background-color: white; " +
                        "-fx-background-radius: 15; " +
                        "-fx-border-color: #D1D5DB; " +
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

            @FXML
            private void openPatientPopup(Patient selectedPatient) {
                String path = "/com/follow_disease/DoctorPagePatientDetail.fxml";

                java.net.URL resource = getClass().getResource(path);

                if (resource == null) {
                    resource = getClass().getClassLoader().getResource("com/follow_disease/DoctorPagePatientDetail.fxml");
                }
                try {
                    FXMLLoader loader = new FXMLLoader(resource);
                    Parent root = loader.load();

                    DoctorPagePatientDetailController controller = loader.getController();
                    if (controller != null) {
                        controller.initData(selectedPatient);
                    }

                    Stage stage = new Stage();
                    stage.setScene(new Scene(root));
                    stage.setTitle("Hasta Detayı");
                    stage.initModality(Modality.APPLICATION_MODAL);
                    stage.show();

                } catch (IOException e) {
                    System.err.println("HATA: FXML bulundu ama yüklenemedi!");
                    System.err.println("Sebep: FXML içindeki 'fx:controller' yolu yanlış olabilir veya dosya bozuk.");
                    e.printStackTrace();
                }
            }

            @FXML
            private void handleOpenPatientDetails() {
                Patient selectedPatient = patientTable.getSelectionModel().getSelectedItem();

                if (selectedPatient != null) {
                    try {
                        FXMLLoader loader = new FXMLLoader(getClass().getResource("/DoctorPagePatientDetail.fxml"));
                        Parent root = loader.load();

                        DoctorPagePatientDetailController detailController = loader.getController();
                        detailController.initData(selectedPatient);

                        Stage stage = new Stage();
                        stage.setTitle("Hasta Tıbbi Kaydı: " + selectedPatient.getName() + " " + selectedPatient.getSurname());
                        stage.setScene(new Scene(root));

                        stage.initModality(Modality.APPLICATION_MODAL);
                        stage.showAndWait();

                        patientTable.refresh();

                    } catch (IOException e) {
                        e.printStackTrace();
                        System.err.println("HATA:DoctorPagePatientDetail.fxml dosyası yüklenemedi!");
                    }
                }
            }

            @FXML
            private void handleLogout(ActionEvent event) {
                Session.clear();

                try {
                    FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/follow_disease/login.fxml"));
                    Parent root = loader.load();

                    Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
                    stage.setScene(new Scene(root));
                    stage.setTitle("Hastalık Takip Sistemi - Giriş");
                    stage.show();
                } catch (IOException e) {
                    e.printStackTrace();
                    new Alert(Alert.AlertType.ERROR, "Giriş sayfası açılamadı: " + e.getMessage(), ButtonType.OK).showAndWait();
                }
            }

            private String safe(String s) {
                return s == null ? "" : s.trim();
            }
        }
