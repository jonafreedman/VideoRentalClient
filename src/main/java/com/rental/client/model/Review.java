/**
 * Client-side data transfer object representing a movie review entity received from the REST service.
 */
package com.rental.client.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Review {
    private Long id;
    private int rating;
    private String reviewText;
    private Movie movie;
    private User user;

    /**
     * Default no-argument constructor for Jackson JSON deserialization.
     */
    public Review() {}

    /**
     * Constructs a Review client object.
     *
     * @param id primary key identifier
     * @param rating numerical star evaluation score
     * @param comment user feedback commentary text
     * @param movie associated movie entity reference
     * @param user author user entity reference
     */
    public Review(Long id, int rating, String comment, Movie movie, User user) {
        this.id = id;
        this.rating = rating;
        this.reviewText = comment;
        this.movie = movie;
        this.user = user;
    }

    // Getters and Setters

    /** @return database primary key */
    public Long getId() { return id; }
    /** @param id database primary key */
    public void setId(Long id) { this.id = id; }

    /** @return numerical star rating */
    public int getRating() { return rating; }
    /** @param rating numerical star rating */
    public void setRating(int rating) { this.rating = rating; }

    /** @return review text content */
    public String getReviewText() { return reviewText; }
    /** @param comment review text content */
    public void setReviewText(String comment) { this.reviewText = comment; }

    /** @return associated movie entity */
    public Movie getMovie() { return movie; }
    /** @param movie associated movie entity */
    public void setMovie(Movie movie) { this.movie = movie; }

    /** @return review author user entity */
    public User getUser() { return user; }
    /** @param user review author user entity */
    public void setUser(User user) { this.user = user; }
}