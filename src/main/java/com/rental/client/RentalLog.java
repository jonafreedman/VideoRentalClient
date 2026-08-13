package com.rental.client;

public class RentalLog {
    private String movieTitle;
    private String dateBorrowed;
    private String dateReturned;
    private String status;

    public RentalLog(String movieTitle, String dateBorrowed, String dateReturned, String status) {
        this.movieTitle = movieTitle;
        this.dateBorrowed = dateBorrowed;
        this.dateReturned = dateReturned;
        this.status = status;
    }

    public String getMovieTitle() { return movieTitle; }
    public String getDateBorrowed() { return dateBorrowed; }
    public String getDateReturned() { return dateReturned; }
    public String getStatus() { return status; }

    public void setDateReturned(String dateReturned) { this.dateReturned = dateReturned; }
    public void setStatus(String status) { this.status = status; }
}
