package com.rental.client.controller;

import com.rental.client.Movie;
import com.rental.client.MovieService;
import com.rental.client.UserSession;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;

public class DashboardController {

    @FXML
    private ListView<String> categoryListView;
    @FXML
    private TextField searchTextField;
    @FXML
    private Button searchButton;
    @FXML
    private TableView<Movie> moviesTableView;
    @FXML
    private TableColumn<Movie, String> titleColumn;
    @FXML
    private TableColumn<Movie, String> categoryColumn;
    @FXML
    private TableColumn<Movie, String> availabilityColumn;
    @FXML
    private Button viewDetailsButton;
    @FXML
    private Button viewProfileButton;

    private final MovieService movieService = new MovieService();
    private ObservableList<Movie> masterMovieList = FXCollections.observableArrayList();
    private FilteredList<Movie> filteredMovieList;

    @FXML
    public void initialize() {
        // 1. Personalize User Account Button
        if (UserSession.getInstance() != null) {
            String currentUser = UserSession.getInstance().getUsername();
            viewProfileButton.setText("👤 " + currentUser + "'s Account");
        }

        // 2. Map Table Columns to Movie object getters (getTitle(), getCategory(), getStatus())
        titleColumn.setCellValueFactory(new PropertyValueFactory<>("title"));
        categoryColumn.setCellValueFactory(new PropertyValueFactory<>("category"));
        availabilityColumn.setCellValueFactory(new PropertyValueFactory<>("status"));

        // 3. Fetch Movies & Setup Filtering Wrapper
        masterMovieList.addAll(movieService.fetchAllMovies());
        filteredMovieList = new FilteredList<>(masterMovieList, p -> true);
        moviesTableView.setItems(filteredMovieList);

        // 4. Live Search listener (filters automatically as you type)
        searchTextField.textProperty().addListener((observable, oldValue, newValue) -> {
            applyFilters();
        });

        // 5. Category Selection listener
        categoryListView.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            applyFilters();
        });

        // 6. Action Button handlers
        searchButton.setOnAction(event -> applyFilters());
        viewDetailsButton.setOnAction(event -> handleInspectAndRent());
        viewProfileButton.setOnAction(event -> handleViewProfile());
    }

    // Method for movie search
    private void applyFilters() {
        String searchText = searchTextField.getText() == null ? "" : searchTextField.getText().toLowerCase().trim();
        String selectedCategory = categoryListView.getSelectionModel().getSelectedItem();

        filteredMovieList.setPredicate(movie -> {
            // 1. Category Filter
            boolean matchesCategory = true;
            if (selectedCategory != null && !selectedCategory.equals("All Movies")) {
                // Extract core category word (e.g. "Action", "Sci-Fi", "Comedy", "Drama", "Horror")
                String cleanCategory = selectedCategory.replaceAll("[^a-zA-Z]", " ").trim().toLowerCase();
                String movieCategory = movie.getCategory().toLowerCase();

                // Match if any primary word from selected category appears in the movie's category
                matchesCategory = false;
                for (String word : cleanCategory.split("\\s+")) {
                    if (!word.isEmpty() && movieCategory.contains(word)) {
                        matchesCategory = true;
                        break;
                    }
                }
            }

            // 2. Search Keyword Filter
            boolean matchesSearch = true;
            if (!searchText.isEmpty()) {
                matchesSearch = movie.getTitle().toLowerCase().contains(searchText) ||
                                movie.getCategory().toLowerCase().contains(searchText);
            }

            return matchesCategory && matchesSearch;
        });
    }

    private void handleInspectAndRent() {
        Movie selectedMovie = moviesTableView.getSelectionModel().getSelectedItem();
        if (selectedMovie != null) {
            System.out.println("Selected Movie to Rent: " + selectedMovie.getTitle() + " (Status: " + selectedMovie.getStatus() + ")");
        } else {
            System.out.println("No movie selected in table.");
        }
    }

    private void handleViewProfile() {
        if (UserSession.getInstance() != null) {
            System.out.println("Opening account logs for: " + UserSession.getInstance().getUsername());
        }
    }
}
