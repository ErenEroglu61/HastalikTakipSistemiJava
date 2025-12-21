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
    @FXML private TextField tcField;
    @FXML private TextField phoneField;
    @FXML private TextField emailField;
    @FXML private PasswordField passwordField;

    @FXML
    public void initialize() {
        // TC No alanından odak çıktığında (Focus Lost) kontrol tetiklenir
        tcField.focusedProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue) { // Odak kaybolduğunda
                handleAutoFill();
            }
        });
    }

    @FXML
    private void handleAutoFill() {
        String tc = tcField.getText();

        if (tc != null && tc.trim().length() == 11) {
            UserService.HospitalRecord record = UserService.getHospitalRecord(tc);

            if (record != null) {
                nameField.setText(record.name);
                surnameField.setText(record.surname);
                nameField.setEditable(false);
                surnameField.setEditable(false);
            } else {
                nameField.clear();
                surnameField.clear();
                nameField.setEditable(true);
                surnameField.setEditable(true);

                Alert alert = new Alert(Alert.AlertType.WARNING);
                alert.setTitle("Kayıt Sorgulama");
                alert.setHeaderText("TC Kimlik No Geçersiz");
                alert.setContentText("Hastanemizin kayıtlarında bu TC bulunamadı. Lütfen kontrol edin.");
                alert.showAndWait();
            }
        }
    }

    @FXML
    public void handleRegister(ActionEvent event) {
        String name = nameField.getText();
        String surname = surnameField.getText();
        String age = ageField.getText();
        String gender = genderComboBox.getValue();
        String tc = tcField.getText();
        String phone = phoneField.getText();
        String email = emailField.getText();
        String password = passwordField.getText();

        boolean success = UserService.register_method(
                name, surname, age, gender, tc, phone, email, password, "", ""
        );

        if (success) {
            goToLogin(event);
        }
    }

    @FXML
    public void handleLoginRedirect(ActionEvent event) {
        goToLogin(event);
    }

    private void goToLogin(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/login.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            System.err.println("Giriş sayfası yüklenemedi: " + e.getMessage());
            e.printStackTrace();
        }
    }
}