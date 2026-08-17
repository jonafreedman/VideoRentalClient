package com.rental.client;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Movie {
    private Long id;
    private String title;
    private String category;
    private int totalCopies;
    private int availableCopies;

    public Movie() {}

    public Movie(Long id, String title, String category, int totalCopies, int availableCopies) {
        this.id = id;
        this.title = title;
        this.category = category;
        this.totalCopies = totalCopies;
        this.availableCopies = availableCopies;
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }

    // Derived property used by TableView PropertyValueFactory("status")
    public String getStatus() {
        if (this.availableCopies <= 0) {
            return "Rented";
        }
        return "Available";
    }

    public int getTotalCopies() { return totalCopies; }
    public void setTotalCopies(int totalCopies) { this.totalCopies = totalCopies; }

    public int getAvailableCopies() { return availableCopies; }
    public void setAvailableCopies(int availableCopies) { this.availableCopies = availableCopies; }
}