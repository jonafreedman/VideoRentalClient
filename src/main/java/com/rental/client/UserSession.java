/**
 * Class tracking the logged-in user across the applications screens.
 */
package com.rental.client;

public class UserSession {

    private static UserSession instance;

    private String username;
    private Long userId;

    private UserSession(String username, Long userId) {
        this.username = username;
        this.userId = userId;
    }
    
    /**
     * Initializes or overwrites the current active user session instance.
     *
     * @param username user account handle
     * @param userId user primary key ID
     */
    public static void setInstance(String username, Long userId) {
        instance = new UserSession(username, userId);
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
}
