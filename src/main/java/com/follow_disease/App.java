package com.follow_disease;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import java.io.IOException;
import java.util.Objects;

public class App extends Application {

    private static Scene scene;

    @Override
    public void start(Stage stage) throws Exception {

        Parent root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("/com/follow_disease/login.fxml")));
        stage.setTitle("Hastalık Takip Sistemi");
        stage.setScene(new Scene(root));
        stage.show();
    }

    private void showLogin(Stage stage) throws IOException {
        Parent root = FXMLLoader.load(getClass().getResource("/com/follow_disease/login.fxml"));
        scene = new Scene(root, 800, 600);
        stage.setTitle("Hastalık Takip Sistemi - Giriş");
        stage.setScene(scene);
        stage.show();
    }

    private void showPatientPage(Stage stage) throws IOException {
        Parent root = FXMLLoader.load(getClass().getResource("/com/follow_disease/patientPage.fxml"));
        scene = new Scene(root, 1000, 700); // Panel daha geniş olduğu için 1000x700
        stage.setTitle("Hasta Kontrol Paneli");
        stage.setScene(scene);
        stage.show();
    }

    private void showDoctorPage(Stage stage) throws IOException {

        Parent root = FXMLLoader.load(getClass().getResource("/com/follow_disease/doctorPage.fxml"));


        scene = new Scene(root, 1100, 700);
        stage.setTitle("Doktor Kontrol Paneli");
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}