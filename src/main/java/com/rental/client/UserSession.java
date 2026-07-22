//** Keeps track of who is logged in across all screens without having to re-authenticate **//
package com.rental.client;

public class UserSession {

    private static UserSession instance;
    private String username;
    
    // private builder to force usage of startSession method
    private UserSession(String username) {
        this.username = username;
    }
    
    // forces the user to have only one instance- no multiple sessions feom the same clients
    public static void startSession(String username) {
        instance = new UserSession(username);
    }

    public static UserSession getInstance() {
        return instance;
    }

    public static void cleanUserSession() {
        instance = null;
    }

    public String getUsername() {
        return username;
    }
}
