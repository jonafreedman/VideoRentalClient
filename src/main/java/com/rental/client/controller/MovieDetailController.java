/**
 * Controller class managing individual movie detail displays, rental execution, and user reviews.
 */
package com.rental.client.controller;

import com.rental.client.Movie;
import com.rental.client.UserSession;
import com.rental.client.service.ApiClient;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextArea;
import javafx.stage.Stage;

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
            "⭐⭐⭐⭐⭐ (5 Stars)",
            "⭐⭐⭐⭐ (4 Stars)",
            "⭐⭐⭐ (3 Stars)",
            "⭐⭐ (2 Stars)",
            "⭐ (1 Star)"
        ));

        reviewsListView.setItems(movieReviews);

        // Action Handlers
        rentButton.setOnAction(e -> handleRentMovie());
        submitReviewButton.setOnAction(e -> handlePostReview());
    }

    /**
     * Populates view UI elements and availability banners using details from the target movie object.
     *
     * @param movie selected movie model target
     */
    public void setMovieData(Movie movie) {
        this.selectedMovie = movie;
        movieTitleLabel.setText(movie.getTitle());
        movieGenreLabel.setText("Genre: " + movie.getCategory());

        // Update Stock/Availability Banner based on ACTUAL database inventory numbers
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

        // Generate tailored movie reviews dynamically
        loadMovieSpecificReviews(movie);
    }

    /**
     * Generates initial review entries tailored to the target movie title and genre.
     */
    private void loadMovieSpecificReviews(Movie movie) {
        movieReviews.clear();
        String title = movie.getTitle();

        // Seed realistic dynamic reviews based on title and category
        movieReviews.add("⭐⭐⭐⭐⭐ - Alice M: " + title + " is absolutely incredible! Must watch.");
        movieReviews.add("⭐⭐⭐⭐ - Bob T: Great standard for " + movie.getCategory() + " movies. Solid watch!");
        movieReviews.add("⭐⭐⭐⭐⭐ - Charlie K: One of my personal favorites. Highly recommend renting it.");
    }

    /**
     * Calls REST ApiClient to record a loan transaction and decrements local available copies on success.
     */
    @FXML
    private void handleRentMovie() {
        if (selectedMovie == null) {
            showAlert(AlertType.WARNING, "No Selection", "No movie selected for rental.");
            return;
        }

        // Pass numeric ID (e.g., 1L) to match @RequestParam Long userId in Spring Boot
        Long currentUserId = UserSession.getInstance().getUserId(); 

        try {
            boolean success = apiClient.rentMovie(selectedMovie.getId(), currentUserId);

            if (success) {
                if (selectedMovie.getAvailableCopies() > 0) {
                    selectedMovie.setAvailableCopies(selectedMovie.getAvailableCopies() - 1);
                }
                this.rentalConfirmed = true;

                showAlert(AlertType.INFORMATION, "Success", 
                    "You have successfully rented " + selectedMovie.getTitle() + "!");
                
                Stage stage = (Stage) rentButton.getScene().getWindow();
                stage.close();
            } else {
                showAlert(AlertType.ERROR, "Rental Failed", 
                    "Server rejected rental. Check if movie copies are in stock.");
            }
        } catch (Exception e) {
            e.printStackTrace();
            showAlert(AlertType.ERROR, "Connection Error", 
                "Could not reach Spring Boot server to process rental.");
        }
    }

    /**
     * Processes submission of a new star rating and user comment into the review list.
     */
    private void handlePostReview() {
        String rating = ratingComboBox.getValue();
        String critique = reviewTextArea.getText().trim();

        if (rating == null || critique.isEmpty()) {
            showAlert(AlertType.WARNING, "Incomplete Review", "Please select a star rating and type a critique before posting.");
            return;
        }

        String username = (UserSession.getInstance() != null) ? UserSession.getInstance().getUsername() : "User";
        String starIcon = rating.split(" ")[0]; // Extracts the star emojis
        String newEntry = starIcon + " - " + username + ": " + critique;

        // Insert new review at top of list
        movieReviews.add(0, newEntry);

        // Reset input controls
        reviewTextArea.clear();
        ratingComboBox.getSelectionModel().clearSelection();

        showAlert(AlertType.INFORMATION, "Review Posted", "Your review for " + selectedMovie.getTitle() + " has been published!");
    }

    /**
     * Displays modal dialog boxes for system alerts and notifications.
     */
    private void showAlert(AlertType alertType, String title, String content) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }

    /**
     * @return true if a rental operation completed successfully during this dialog session
     */
    public boolean isRentalConfirmed() {
        return rentalConfirmed;
    }
}