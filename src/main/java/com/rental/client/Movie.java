package com.rental.client;

public class Movie {

    private Long id;
    private String title;
    private String category;
    private String status; // e.g., "Available" or "Rented"

    // Default constructor (required for JSON deserialization)
    public Movie() {}

    public Movie(Long id, String title, String category, String status) {
        this.id = id;
        this.title = title;
        this.category = category;
        this.status = status;
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}