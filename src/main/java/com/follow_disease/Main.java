package com.follow_disease;

import javafx.application.Application;

public class Main {
    public static void main(String[] args) {
        // App.main(args) yerine doğrudan Application.launch kullanıyoruz.
        // Bu yöntem modül kontrolünü (javafx.controls hatasını) tamamen atlatır.
        Application.launch(App.class, args);
    }
}