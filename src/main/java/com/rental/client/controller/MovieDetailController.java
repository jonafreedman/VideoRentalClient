package com.rental.client.controller;

import com.rental.client.Movie;
import com.rental.client.UserSession;
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

    public void setMovieData(Movie movie) {
        this.selectedMovie = movie;
        movieTitleLabel.setText(movie.getTitle());
        movieGenreLabel.setText("Genre: " + movie.getCategory());

        // Update Stock/Availability Banner
        if ("Rented".equalsIgnoreCase(movie.getStatus())) {
            movieStockLabel.setText("● Currently Rented - Out of Stock");
            movieStockLabel.setStyle("-fx-text-fill: #dc2626; -fx-font-weight: bold;");
            rentButton.setDisable(true);
            rentButton.setText("Out of Stock");
            rentButton.setStyle("-fx-background-color: #94a3b8; -fx-text-fill: white; -fx-background-radius: 6px;");
        } else {
            movieStockLabel.setText("● In Stock - Available for Rent");
            movieStockLabel.setStyle("-fx-text-fill: #16a34a; -fx-font-weight: bold;");
            rentButton.setDisable(false);
            rentButton.setText("Rent DVD Now");
            rentButton.setStyle("-fx-background-color: #2563eb; -fx-text-fill: white; -fx-font-weight: bold; -fx-background-radius: 6px;");
        }

        // Generate tailored movie reviews dynamically
        loadMovieSpecificReviews(movie);
    }

    private void loadMovieSpecificReviews(Movie movie) {
        movieReviews.clear();
        String title = movie.getTitle();

        // Seed realistic dynamic reviews based on title and category
        movieReviews.add("⭐⭐⭐⭐⭐ - Alice M: " + title + " is absolutely incredible! Must watch.");
        movieReviews.add("⭐⭐⭐⭐ - Bob T: Great standard for " + movie.getCategory() + " movies. Solid watch!");
        movieReviews.add("⭐⭐⭐⭐⭐ - Charlie K: One of my personal favorites. Highly recommend renting it.");
    }

    private void handleRentMovie() {
        if (selectedMovie != null && "Available".equalsIgnoreCase(selectedMovie.getStatus())) {
            selectedMovie.setStatus("Rented");
            rentalConfirmed = true;

            Alert alert = new Alert(AlertType.INFORMATION);
            alert.setTitle("Rental Confirmed");
            alert.setHeaderText(null);
            alert.setContentText("You have successfully rented " + selectedMovie.getTitle() + "!");
            alert.showAndWait();

            // Refresh modal UI state
            setMovieData(selectedMovie);
        }
    }

    private void handlePostReview() {
        String rating = ratingComboBox.getValue();
        String critique = reviewTextArea.getText().trim();

        if (rating == null || critique.isEmpty()) {
            Alert alert = new Alert(AlertType.WARNING);
            alert.setTitle("Incomplete Review");
            alert.setHeaderText(null);
            alert.setContentText("Please select a star rating and type a critique before posting.");
            alert.showAndWait();
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

        Alert alert = new Alert(AlertType.INFORMATION);
        alert.setTitle("Review Posted");
        alert.setHeaderText(null);
        alert.setContentText("Your review for " + selectedMovie.getTitle() + " has been published!");
        alert.showAndWait();
    }

    public boolean isRentalConfirmed() {
        return rentalConfirmed;
    }
}