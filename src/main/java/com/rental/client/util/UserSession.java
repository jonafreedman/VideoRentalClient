/**
 * Class tracking the logged-in user across the applications screens.
 */
package com.rental.client.util;

public class UserSession {

    private static UserSession instance;

    private String username;
    private Long userId;
    private String role;

    private UserSession(String username, Long userId, String role) {
        this.username = username;
        this.userId = userId;
        this.role = role;
    }
    
    /**
     * Initializes or overwrites the current active user session instance.
     *
     * @param username user account handle
     * @param userId user primary key ID
     * @param role user authorization role 
     */
    public static void setInstance(String username, Long userId, String role) {
        instance = new UserSession(username, userId, role);
    }

    /**
     * @return active UserSession instance, or null if no user is signed in
     */
    public static UserSession getInstance() {
        return instance;
    }

    /**
     * Clears the current active user session upon logout.
     */
    public static void cleanUserSession() {
        instance = null;
    }

    /** @return authenticated username handle */
    public String getUsername() {
        return username;
    }

    /** @return authenticated user database ID */
    public Long getUserId() {
        return userId;
    }

    /** @return authenticated user account role */
    public String getRole() {
        return role;
    }
}