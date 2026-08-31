/**
 * Client-side data transfer object representing a user entity received from the REST service.
 */
package com.rental.client.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class User {
    private Long id;
    private String username;
    private String role;

    /**
     * Default no-argument constructor for Jackson JSON deserialization.
     */
    public User() {}

    /**
     * Constructs a User client object.
     *
     * @param id primary key identifier
     * @param username user account handle
     * @param role account security role
     */
    public User(Long id, String username, String role) {
        this.id = id;
        this.username = username;
        this.role = role;
    }

    // Getters and Setters

    /** @return database primary key */
    public Long getId() { return id; }
    /** @param id database primary key */
    public void setId(Long id) { this.id = id; }

    /** @return account handle string */
    public String getUsername() { return username; }
    /** @param username account handle string */
    public void setUsername(String username) { this.username = username; }

    /** @return role designation string */
    public String getRole() { return role; }
    /** @param role role designation string */
    public void setRole(String role) { this.role = role; }
}