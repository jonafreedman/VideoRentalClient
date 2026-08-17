/**
 * Client-side data transfer object representing a movie entity received from the REST service.
 */
package com.rental.client;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Movie {
    private Long id;
    private String title;
    private String category;
    private int totalCopies;
    private int availableCopies;

    /**
     * Default no-argument constructor for Jackson JSON deserialization.
     */
    public Movie() {}

    /**
     * Constructs a Movie client object.
     *
     * @param id primary key identifier
     * @param title movie title
     * @param category genre category
     * @param totalCopies overall stock allocation
     * @param availableCopies active on-shelf inventory
     */
    public Movie(Long id, String title, String category, int totalCopies, int availableCopies) {
        this.id = id;
        this.title = title;
        this.category = category;
        this.totalCopies = totalCopies;
        this.availableCopies = availableCopies;
    }

    // Getters and Setters

    /** @return database primary key */
    public Long getId() { return id; }
    /** @param id database primary key */
    public void setId(Long id) { this.id = id; }

    /** @return title string */
    public String getTitle() { return title; }
    /** @param title title string */
    public void setTitle(String title) { this.title = title; }

    /** @return genre string */
    public String getCategory() { return category; }
    /** @param category genre string */
    public void setCategory(String category) { this.category = category; }

    /**
     * Derived getter property dynamically evaluated for JavaFX TableView PropertyValueFactory("status")
     *
     * @return readable availability status string
     */
    public String getStatus() {
        if (this.availableCopies <= 0) {
            return "Rented";
        }
        return "Available";
    }

    /** @return total system inventory count */
    public int getTotalCopies() { return totalCopies; }
    /** @param totalCopies total system inventory count */
    public void setTotalCopies(int totalCopies) { this.totalCopies = totalCopies; }

    /** @return currently available copy count */
    public int getAvailableCopies() { return availableCopies; }
    /** @param availableCopies currently available copy count */
    public void setAvailableCopies(int availableCopies) { this.availableCopies = availableCopies; }
}