package com.follow_disease.controller;

import com.follow_disease.service.UserService;
import javafx.fxml.FXML;
import javafx.scene.control.*;

public class LoginController {

    @FXML private TextField emailField;
    @FXML private PasswordField passwordField;

    @FXML
    public void handleLogin() {
        String email = emailField.getText();
        String password = passwordField.getText();
        boolean isSuccess = UserService.login_method(email, password);

        if (isSuccess) {
            System.out.println("Giriş Başarılı! Ana sayfaya yönlendiriliyorsunuz...");
            // İleride buraya ana sayfa açma kodu gelecek
        } else {
            System.out.println("Hata: E-posta veya şifre yanlış!");
            // İleride buraya kırmızı bir uyarı yazısı ekleriz
        }
    }


    @FXML
    public void handleRegisterRedirect(javafx.event.ActionEvent event) { //event hangi buton olduğu anlaşılsın diye
        try {

            javafx.fxml.FXMLLoader loader = new javafx.fxml.FXMLLoader(getClass().getResource("/register.fxml"));
            javafx.scene.Parent root = loader.load();

            javafx.stage.Stage stage = (javafx.stage.Stage) ((javafx.scene.Node) event.getSource()).getScene().getWindow();
            stage.setScene(new javafx.scene.Scene(root));
            stage.show();
        } catch (Exception e) {
            System.out.println("Login'den Register'a geçerken hata: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
