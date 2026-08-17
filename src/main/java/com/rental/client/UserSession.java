//** Keeps track of who is logged in across all screens without having to re-authenticate **//
package com.rental.client;

public class UserSession {

    private static UserSession instance;

    private String username;
    private Long userId;

    private UserSession(String username, Long userId) {
        this.username = username;
        this.userId = userId;
    }

    public static void setInstance(String username, Long userId) {
        instance = new UserSession(username, userId);
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

    public Long getUserId() {
        return userId;
    }
}
