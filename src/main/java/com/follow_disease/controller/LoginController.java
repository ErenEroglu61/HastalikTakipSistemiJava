package com.follow_disease.controller;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

public class LoginController {

    @FXML
    private TextField emailField;

    @FXML
    private PasswordField passwordField;

    @FXML
    private Button loginButton;

    @FXML
    private Hyperlink registerLink;

    @FXML
    public void handleLogin() {
        String email = emailField.getText();
        String password = passwordField.getText();

        // İleride buraya şifre kontrol kodlarını ekleyeceğiz.
    }
    @FXML
    public void handleRegisterRedirect() {
        System.out.println("Kayıt ol sayfasına yönlendiriliyor...");
        // Kayıt sayfası yapılınca burası oraya yönlendirecek.
    }
}
