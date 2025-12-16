package com.follow_disease.service;

public class UserService {


    public static boolean register_method(String name, String surname, String age, String gender, String email, String password) {
        System.out.println("Sisteme Kaydediliyor: ");
        return true;
    }

    // YENİ: Giriş metodu
    public static boolean login_method(String email, String password) {
        System.out.println("UserService: Giriş kontrolü yapılıyor... " + email);

        return false;
    }
}