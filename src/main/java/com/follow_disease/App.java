package com.follow_disease;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import java.io.IOException;

public class App extends Application {

    private static Scene scene;

    @Override
    public void start(Stage stage) throws IOException {
        // Hangi ekranı görmek istiyorsanız başındaki açıklama kısmını kaldırın şimdilik sayfları bağlamadığımız için boyle olsun
        //showLogin(stage);         // Giriş ekranını açar
        showPatientPage(stage); // Hasta panelini açar
       // showDoctorPage(stage);

    }

    // GİRİŞ EKRANINI AÇAN METOT
    private void showLogin(Stage stage) throws IOException {
        Parent root = FXMLLoader.load(getClass().getResource("/login.fxml"));
        scene = new Scene(root, 800, 600);
        stage.setTitle("Hastalık Takip Sistemi - Giriş");
        stage.setScene(scene);
        stage.show();
    }

    // HASTA PANELİNİ AÇAN METOT
    private void showPatientPage(Stage stage) throws IOException {
        Parent root = FXMLLoader.load(getClass().getResource("/patientPage.fxml"));
        scene = new Scene(root, 1000, 700); // Panel daha geniş olduğu için 1000x700
        stage.setTitle("Hasta Kontrol Paneli");
        stage.setScene(scene);
        stage.show();
    }

    // DOKTOR PANELİ
    private void showDoctorPage(Stage stage) throws IOException {

        Parent root = FXMLLoader.load(getClass().getResource("/doctorPage.fxml"));


        scene = new Scene(root, 1100, 700);
        stage.setTitle("Doktor Kontrol Paneli");
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}