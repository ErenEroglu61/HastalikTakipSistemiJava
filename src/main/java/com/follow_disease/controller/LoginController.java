package com.follow_disease.controller;

import com.follow_disease.service.UserService;
import javafx.fxml.FXML;
import com.follow_disease.User;
import com.follow_disease.Session;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.event.ActionEvent;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import java.io.IOException;

public class LoginController {

    @FXML private TextField emailField;
    @FXML private PasswordField passwordField;

    @FXML
    public void handleLogin(ActionEvent event) {
        String email = emailField.getText();
        String password = passwordField.getText();

        if (UserService.login_method(email, password)) {
            User user = Session.getCurrentUser();
            String fxmlPath;

            if (user instanceof com.follow_disease.Doctor) {
                fxmlPath = "/com/follow_disease/doctorPage.fxml";
            } else {
                fxmlPath = "/com/follow_disease/patientPage.fxml";
            }
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
                Parent root = loader.load();

                //  Mevcut pencereyi (Stage) alıp yeni sayfayı içine koyuyoruz
                Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
                stage.setScene(new Scene(root));
                stage.show();

            } catch (IOException e) {
                System.err.println("HATA: FXML dosyası yüklenemedi. Yol: " + fxmlPath);
                e.printStackTrace();
            }
        } else {
            // Giriş başarısızsa uyarı ver
            new Alert(Alert.AlertType.ERROR, "E-posta veya şifre hatalı!").showAndWait();
        }
    }

    @FXML
    public void handleRegisterRedirect(javafx.event.ActionEvent event) { //event hangi buton olduğu anlaşılsın diye
        try {

            javafx.fxml.FXMLLoader loader = new javafx.fxml.FXMLLoader(getClass().getResource("/com/follow_disease/register.fxml"));
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
