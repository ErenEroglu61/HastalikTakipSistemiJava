package com.follow_disease.controller;

import com.follow_disease.service.UserService;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.ComboBox;

public class RegisterController {
    @FXML private TextField nameField;
    @FXML private TextField surnameField;
    @FXML private TextField ageField;
    @FXML private ComboBox<String> genderComboBox;
    @FXML private TextField phoneField;
    @FXML private TextField tcNoField;
    @FXML private TextField emailField;
    @FXML private PasswordField passwordField;

    @FXML
    public void handleRegister(javafx.event.ActionEvent event) {

        String name = nameField.getText();
        String surname = surnameField.getText();
        String age = ageField.getText();
        String gender = genderComboBox.getValue();
        String tcNo = tcNoField.getText();
        String phone = phoneField.getText();
        String email = emailField.getText();
        String password = passwordField.getText();


        boolean success = UserService.register_method(name, surname, age, gender, tcNo, phone, email, password);

        if (success) {
            redirectToLogin(event);
        }
    }

    @FXML
    public void handleLoginRedirect(javafx.event.ActionEvent event) {
        redirectToLogin(event);
    }

    private void redirectToLogin(javafx.event.ActionEvent event) {
        try {
            javafx.fxml.FXMLLoader loader = new javafx.fxml.FXMLLoader(getClass().getResource("/login.fxml"));
            javafx.scene.Parent root = loader.load();
            javafx.stage.Stage stage = (javafx.stage.Stage) ((javafx.scene.Node) event.getSource()).getScene().getWindow();
            stage.setScene(new javafx.scene.Scene(root));
            stage.show();
        } catch (java.io.IOException e) {
            e.printStackTrace();
        }
    }
}