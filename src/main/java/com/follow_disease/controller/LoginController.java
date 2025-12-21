package com.follow_disease.controller;

import com.follow_disease.service.UserService;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import com.follow_disease.User;
import com.follow_disease.Session;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;

public class LoginController {

    @FXML private TextField emailField;
    @FXML private PasswordField passwordField;

    @FXML
    public void handleLogin() {
        String email = emailField.getText();
        String password = passwordField.getText();
        boolean isSuccess = UserService.login_method(email, password);

        if (isSuccess) {
            User u = Session.getCurrentUser(); // UserService login içinde setlendi
            if (u == null) return;

            try {
                String fxml = u.getRole().equalsIgnoreCase("doktor")
                        ? "/doctorPage.fxml"
                        : "/patientPage.fxml";

                FXMLLoader loader = new FXMLLoader(getClass().getResource(fxml));
                Parent root = loader.load();

                Stage stage = (Stage) emailField.getScene().getWindow();
                stage.setScene(new Scene(root));
                stage.show();
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            System.out.println("Hata: E-posta veya şifre yanlış!");
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
