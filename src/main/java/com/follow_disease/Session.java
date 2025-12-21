package com.follow_disease.service;

import com.follow_disease.User;

public final class Session {
    private static User currentUser;

    private Session() {}

    public static void setCurrentUser(User u) { currentUser = u; }
    public static User getCurrentUser() { return currentUser; }
    public static void clear() { currentUser = null; }
}
