/**
 * Data model class representing a user rental transaction log line in the UI history view.
 */
package com.rental.client;

public class RentalLog {
    private String movieTitle;
    private String dateBorrowed;
    private String dateReturned;
    private String status;

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
    
    // Getters and Setters
    
    /** @return movie title string */
    public String getMovieTitle() { return movieTitle; }
    /** @return date movie was checked out */
    public String getDateBorrowed() { return dateBorrowed; }
    /** @return date movie was returned or current active state */
    public String getDateReturned() { return dateReturned; }
    /** @return current status string */
    public String getStatus() { return status; }

    /** @param dateReturned return date string */
    public void setDateReturned(String dateReturned) { this.dateReturned = dateReturned; }
    /** @param status status tag string */
    public void setStatus(String status) { this.status = status; }
}
