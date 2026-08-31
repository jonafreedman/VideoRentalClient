/**
 * Controller class managing individual movie detail displays, rental execution, and live user reviews.
 */
package com.rental.client.controller;

import com.rental.client.model.Movie;
import com.rental.client.model.Review;
import com.rental.client.service.ApiClient;
import com.rental.client.util.DialogUtil;
import com.rental.client.util.UserSession;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextArea;
import javafx.stage.Stage;

import java.util.List;


public class MovieDetailController {

    @FXML private Label movieTitleLabel;
    @FXML private Label movieGenreLabel;
    @FXML private Label movieStockLabel;
    @FXML private Button rentButton;

    @FXML private ListView<String> reviewsListView;
    @FXML private ComboBox<String> ratingComboBox;
    @FXML private TextArea reviewTextArea;
    @FXML private Button submitReviewButton;

    private Movie selectedMovie;
    private boolean rentalConfirmed = false;
    private final ObservableList<String> movieReviews = FXCollections.observableArrayList();
    private final ApiClient apiClient = new ApiClient();

    /**
     * Binds dropdown selection lists and assigns action button listeners.
     */
    @FXML
    public void initialize() {
        // Populate Rating Options
        ratingComboBox.setItems(FXCollections.observableArrayList(
            "5 Stars",
            "4 Stars",
            "3 Stars",
            "2 Stars",
            "1 Star"
        ));

        reviewsListView.setItems(movieReviews);

        // Action Handlers
        rentButton.setOnAction(e -> handleRentMovie());
        submitReviewButton.setOnAction(e -> handlePostReview());
    }

    /**
     * Populates view UI elements and availability with details from the target movie object.
     *
     * @param movie selected movie model target
     */
    public void setMovieData(Movie movie) {
        this.selectedMovie = movie;
        movieTitleLabel.setText(movie.getTitle());
        movieGenreLabel.setText("Genre: " + movie.getCategory());

        // Update Stock/Availability Banner based on database inventory numbers
        if (movie.getAvailableCopies() <= 0) {
            movieStockLabel.setText("● Currently Rented - Out of Stock");
            movieStockLabel.setStyle("-fx-text-fill: #dc2626; -fx-font-weight: bold;");
            rentButton.setDisable(true);
            rentButton.setText("Out of Stock");
            rentButton.setStyle("-fx-background-color: #94a3b8; -fx-text-fill: white; -fx-background-radius: 6px;");
        } else {
            movieStockLabel.setText("● In Stock (" + movie.getAvailableCopies() + " copies) - Available for Rent");
            movieStockLabel.setStyle("-fx-text-fill: #16a34a; -fx-font-weight: bold;");
            rentButton.setDisable(false);
            rentButton.setText("Rent DVD Now");
            rentButton.setStyle("-fx-background-color: #2563eb; -fx-text-fill: white; -fx-font-weight: bold; -fx-background-radius: 6px;");
        }

        // Fetch live reviews from the database
        loadLiveReviews();
    }

    /**
     * Fetches live review records from the backend REST service and populates the UI list view.
     */
    private void loadLiveReviews() {
        movieReviews.clear();
        List<Review> reviews = apiClient.getReviewsByMovieId(selectedMovie.getId());

        if (reviews.isEmpty()) {
            movieReviews.add("No reviews yet. Be the first to leave one!");
        } else {
            for (Review r : reviews) {
                String author = (r.getUser() != null && r.getUser().getUsername() != null) 
                        ? r.getUser().getUsername() : "Anonymous";
                String commentText = (r.getReviewText() != null && !r.getReviewText().isBlank()) 
                        ? r.getReviewText() : "No written review provided.";

                String entry = String.format("[%d/5 ★] %s: %s", r.getRating(), author, commentText);
                movieReviews.add(entry);
            }
        }
    }

    /**
     * Calls REST ApiClient to record a loan transaction and decrements local available copies on success.
     */
    @FXML
    private void handleRentMovie() {
        if (selectedMovie == null) {
        	DialogUtil.showAlert(AlertType.WARNING, "No Selection", "No movie selected for rental.");
            return;
        }

        Long currentUserId = UserSession.getInstance().getUserId(); 

        try {
            boolean success = apiClient.rentMovie(selectedMovie.getId(), currentUserId);

            if (success) {
                if (selectedMovie.getAvailableCopies() > 0) {
                    selectedMovie.setAvailableCopies(selectedMovie.getAvailableCopies() - 1);
                }
                this.rentalConfirmed = true;

                DialogUtil.showAlert(AlertType.INFORMATION, "Success", 
                    "You have successfully rented " + selectedMovie.getTitle() + "!");
                
                Stage stage = (Stage) rentButton.getScene().getWindow();
                stage.close();
            } else {
            	DialogUtil.showAlert(AlertType.ERROR, "Rental Failed", 
                    "Server rejected rental. Check if movie copies are in stock.");
            }
        } catch (Exception e) {
            e.printStackTrace();
            DialogUtil.showAlert(AlertType.ERROR, "Connection Error", 
                "Could not reach Spring Boot server to process rental.");
        }
    }

    /**
     * Processes submission of a new star rating and user comment to the backend API.
     */
    private void handlePostReview() {
    	Long userId = UserSession.getInstance().getUserId();
    	
    	// Check if user has an active or past rental history for this movie
        boolean hasRented = apiClient.hasUserRentedMovie(userId, selectedMovie.getId(), selectedMovie.getTitle());
        if (!hasRented) {
        	DialogUtil.showAlert(AlertType.WARNING, "Review Restricted", 
                "You can only review movies you have rented in the past.");
            return;
        }
        String ratingSelection = ratingComboBox.getValue();
        String critique = reviewTextArea.getText().trim();

        if (ratingSelection == null || critique.isEmpty()) {
        	DialogUtil.showAlert(AlertType.WARNING, "Incomplete Review", "Please select a star rating and type a critique before posting.");
            return;
        }

        // Parse numerical rating from string 
        int rating = Integer.parseInt(ratingSelection.split(" ")[0]);
        boolean success = apiClient.addReview(selectedMovie.getId(), userId, rating, critique);

        if (success) {
            reviewTextArea.clear();
            ratingComboBox.getSelectionModel().clearSelection();
            loadLiveReviews(); // Refresh review list dynamically from backend
            DialogUtil.showAlert(AlertType.INFORMATION, "Review Posted", "Your review for " + selectedMovie.getTitle() + " has been published!");
        } else {
        	DialogUtil.showAlert(AlertType.ERROR, "Submission Error", "Could not save your review to the database.");
        }
    }

    /**
     * @return true if a rental operation completed successfully during this dialog session
     */
    public boolean isRentalConfirmed() {
        return rentalConfirmed;
    }
}