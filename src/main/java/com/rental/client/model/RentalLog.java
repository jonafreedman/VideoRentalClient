/**
 * Data model class representing a user rental transaction log line in the UI history view.
 */
package com.rental.client.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.Map;

@JsonIgnoreProperties(ignoreUnknown = true)
public class RentalLog {
    private Long id;
    private String userName;
    private String movieTitle;
    private String dateBorrowed;
    private String dateReturned;
    private String status;

    /**
     * Default no-argument constructor required by Jackson for JSON deserialization.
     */
    public RentalLog() {}

    /**
     * Constructs a RentalLog UI view record.
     *
     * @param movieTitle name of the movie rented
     * @param dateBorrowed checkout timestamp string
     * @param dateReturned check-in timestamp string or active state string
     * @param status transaction state indicator
     */
    public RentalLog(String movieTitle, String dateBorrowed, String dateReturned, String status) {
        this.movieTitle = movieTitle;
        this.dateBorrowed = dateBorrowed;
        this.dateReturned = dateReturned;
        this.status = status;
    }

    /**
     * Constructs a RentalLog record 
     *
     * @param id loan transaction primary key
     * @param movieTitle name of the movie rented
     * @param dateBorrowed checkout timestamp string
     * @param dateReturned check-in timestamp string or active state string
     * @param status transaction state indicator
     */
    public RentalLog(Long id, String movieTitle, String dateBorrowed, String dateReturned, String status) {
        this.id = id;
        this.movieTitle = movieTitle;
        this.dateBorrowed = dateBorrowed;
        this.dateReturned = dateReturned;
        this.status = status;
    }

    // --- Jackson Unpacking Helpers for Spring Boot JSON Payload ---

    /** Unpacks the username from the nested "user" JSON object */
    @JsonProperty("user")
    private void unpackUser(Map<String, Object> user) {
        if (user != null && user.containsKey("username")) {
            this.userName = (String) user.get("username");
        }
    }
    
    /** Unpacks the movie title from the nested "movie" JSON object */
    @JsonProperty("movie")
    private void unpackMovie(Map<String, Object> movie) {
        if (movie != null && movie.containsKey("title")) {
            this.movieTitle = (String) movie.get("title");
        }
    }

    /** Maps backend "rentDate" field to "dateBorrowed" */
    @JsonProperty("rentDate")
    public void setRentDate(String rentDate) {
    	if (rentDate != null && !rentDate.isEmpty()) {
            // Strip fractional seconds and format space separator
            String cleanString = rentDate.contains(".") ? rentDate.split("\\.")[0] : rentDate;
            this.dateBorrowed = cleanString.replace("T", " ");
        } else {
            this.dateBorrowed = "—";
        }
    }

    /** Maps backend "returnDate" field to "dateReturned" and calculates active loan status */
    @JsonProperty("returnDate")
    public void setReturnDate(String returnDate) {
    	if (returnDate != null) {
            this.dateReturned = returnDate.split("\\.")[0].replace("T", " ");
            this.status = "RETURNED";
        } else {
            this.dateReturned = "-";
            this.status = "RENTED";
        }
    }
    
    // Getters and Setters
    
    /** @return loan database primary key */
    public Long getId() { return id; }
    /** @param id loan database primary key */
    public void setId(Long id) { this.id = id; }

    /** @return user name string */
    public String getUserName() { return userName; }
    /** @param user name of the user which rented */
    public void setUserName(String userName) { this.userName = userName; }
    
    /** @return movie title string */
    public String getMovieTitle() { return movieTitle; }
    /** @param movieTitle name of the movie rented */
    public void setMovieTitle(String movieTitle) { this.movieTitle = movieTitle; }

    /** @return date movie was checked out */
    public String getDateBorrowed() { return dateBorrowed; }
    /** @param dateBorrowed checkout timestamp string */
    public void setDateBorrowed(String dateBorrowed) { this.dateBorrowed = dateBorrowed; }

    /** @return date movie was returned or current active state */
    public String getDateReturned() { return dateReturned; }
    /** @param dateReturned return date string */
    public void setDateReturned(String dateReturned) { this.dateReturned = dateReturned; }

    /** @return current status string */
    public String getStatus() { return status; }
    /** @param status status tag string */
    public void setStatus(String status) { this.status = status; }
}