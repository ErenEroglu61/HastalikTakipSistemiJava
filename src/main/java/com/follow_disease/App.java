package com.follow_disease;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.util.Objects;

public class App extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception {
        // Tasarım dosyasını (login.fxml) buradan yüklüyoruz
        Parent root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("/login.fxml")));

        primaryStage.setTitle("Hastalık Takip Sistemi");
        primaryStage.setScene(new Scene(root, 800, 600));
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}