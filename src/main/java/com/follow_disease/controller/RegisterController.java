package com.follow_disease.controller;

import com.follow_disease.service.UserService;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;
import java.io.IOException;

public class RegisterController {

    @FXML private TextField nameField;
    @FXML private TextField surnameField;
    @FXML private TextField ageField;
    @FXML private ComboBox<String> genderComboBox;
    @FXML private TextField tcNoField;
    @FXML private TextField phoneField;
    @FXML private TextField emailField;
    @FXML private PasswordField passwordField;

    @FXML
    public void initialize() {
        // TC No alanından odak çıktığında (Focus Lost) otomatik kontrol yapalım
        tcNoField.focusedProperty().addListener((observable, oldValue, newValue) -> {
            // newValue false ise kullanıcı kutudan çıkmış demektir
            if (!newValue) {
                handleAutoFill();
            }
        });
    }

    private void handleAutoFill() {
        String tc = tcNoField.getText();

        // TC 11 haneli ise kontrolü başlat
        if (tc != null && tc.trim().length() == 11) {
            UserService.HospitalRecord record = UserService.getHospitalRecord(tc);

            if (record != null) {
                nameField.setText(record.name);
                surnameField.setText(record.surname);

                // Kutucukları kilitledik Kullanıcı değiştiremesin diye
                nameField.setEditable(false);
                surnameField.setEditable(false);

                System.out.println("Resmi kayıt bulundu: " + record.name + " " + record.surname);
            } else {
                // Kayıt bulunamadıysa alanları temizle ve uyar
                nameField.clear();
                surnameField.clear();
                nameField.setEditable(true);
                surnameField.setEditable(true);
                nameField.setStyle(""); // Stilleri sıfırla
                surnameField.setStyle("");
            }
        }
    }
    @FXML
    public void handleRegister(ActionEvent event) {

        String name = nameField.getText();
        String surname = surnameField.getText();
        String age = ageField.getText();
        String gender = genderComboBox.getValue();
        String tcNo = tcNoField.getText();
        String phone = phoneField.getText();
        String email = emailField.getText();
        String password = passwordField.getText();

        //  FXML'de olmayan alanlar için boş string ("") oluşturuyoruz
        String branch = "";
        String medical_title = "";


        boolean success = UserService.register_method(
                name,
                surname,
                age,
                gender,
                tcNo,
                phone,
                email,
                password,
                branch,
                medical_title
        );

        if (success) {
            redirectToLogin(event);
        }
    }
    @FXML
    public void handleLoginRedirect(ActionEvent event) {
        redirectToLogin(event);
    }
    private void redirectToLogin(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/login.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}