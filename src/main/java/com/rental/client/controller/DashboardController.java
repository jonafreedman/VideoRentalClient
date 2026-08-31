/**
 * Controller class managing main dashboard catalog displays, filtering, and navigation.
 */
package com.rental.client.controller;

import java.util.List;

import com.rental.client.model.Movie;
import com.rental.client.service.ApiClient;
import com.rental.client.util.AppConstants;
import com.rental.client.util.UserSession;

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
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.stage.Modality;
import javafx.stage.Stage;

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
    @FXML
    private Button adminPanelButton;
    @FXML
    private Button logoutButton;

    private ObservableList<Movie> masterMovieList = FXCollections.observableArrayList();
    private FilteredList<Movie> filteredMovieList;
    private final ApiClient apiClient = new ApiClient();

    /**
     * Initializes component listeners, table column bindings, and loads remote backend catalog data.
     */
    @FXML
    public void initialize() {
        // 1. Personalize User Account Button
        if (UserSession.getInstance() != null) {
            String currentUser = UserSession.getInstance().getUsername();
            viewProfileButton.setText("👤 " + currentUser + "'s Account");
            
            // Check if logged-in user possesses administrative privileges
            if ("ADMIN".equalsIgnoreCase(UserSession.getInstance().getRole())) {
                adminPanelButton.setVisible(true);
                adminPanelButton.setOnAction(event -> handleOpenAdminView());
            }
        }

        // 2. Map Table Columns to Movie object getters
        titleColumn.setCellValueFactory(new PropertyValueFactory<>("title"));
        categoryColumn.setCellValueFactory(new PropertyValueFactory<>("category"));
        availabilityColumn.setCellValueFactory(new PropertyValueFactory<>("status"));

        // 3. Setup Filtered List Wrapper
        filteredMovieList = new FilteredList<>(masterMovieList, p -> true);
        moviesTableView.setItems(filteredMovieList);

        // Fetch initial movies from Spring Boot API
        loadMoviesFromBackend();

        // 4. Live Search listener 
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
        logoutButton.setOnAction(event -> handleLogout());
    }
    
    /**
     * Fetches fresh catalog data from the backend REST service and updates the UI list.
     */
    private void loadMoviesFromBackend() {
        try {
            // Fetch movies from Spring Boot REST API
            List<Movie> moviesFromApi = apiClient.getAllMovies();
            masterMovieList.setAll(moviesFromApi);
        } catch (Exception e) {
            System.err.println("Could not connect to Spring Boot server on http://localhost:8080/api. Keeping local data.");
            e.printStackTrace();
        }
    }
    
    /**
     * Applies multi-criteria predicate filtering based on category selection and keyword text search.
     */
    private void applyFilters() {
        String searchText = searchTextField.getText() == null ? "" : searchTextField.getText().toLowerCase().trim();
        String selectedCategory = categoryListView.getSelectionModel().getSelectedItem();

        filteredMovieList.setPredicate(movie -> {
            // 1. Category Filter
            boolean matchesCategory = true;
            if (selectedCategory != null && !selectedCategory.equals("All Movies")) {
                // Extract core category word 
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

    /**
     * Opens modal detail dialog for selected movie and triggers backend re-fetch if rented.
     */
    @FXML
    private void handleInspectAndRent() {
        Movie selectedMovie = moviesTableView.getSelectionModel().getSelectedItem();

        if (selectedMovie == null) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("No Selection");
            alert.setHeaderText(null);
            alert.setContentText("Please select a movie from the table first.");
            alert.showAndWait();
            return;
        }

        try {
            FXMLLoader loader = new FXMLLoader(
                getClass().getResource("/com/rental/client/view/MovieDetailView.fxml")
            );
            Parent root = loader.load();

            MovieDetailController controller = loader.getController();
            controller.setMovieData(selectedMovie);

            Stage modalStage = new Stage();
            modalStage.setTitle("Inspect & Rent - " + selectedMovie.getTitle());
            modalStage.initModality(Modality.APPLICATION_MODAL);
            modalStage.initOwner(viewDetailsButton.getScene().getWindow());
            modalStage.setScene(new Scene(root, AppConstants.DETAIL_WIDTH, AppConstants.DETAIL_HEIGHT));
            modalStage.setResizable(false);
            
            // 1. Show window and block execution until closed
            modalStage.showAndWait();

            // 2. Refresh live backend data if rental succeeded
            if (controller.isRentalConfirmed()) {
                loadMoviesFromBackend(); // Re-queries Spring Boot API for exact live counts
            }

        } catch (Exception e) {
            System.err.println("Failed to load MovieDetailView.fxml:");
            e.printStackTrace();
        }
    }

    /**
     * Opens user profile modal view populated with rental transaction history.
     */
    private void handleViewProfile() {
        try {
            FXMLLoader loader = new FXMLLoader(
                getClass().getResource("/com/rental/client/view/ProfileView.fxml")
            );
            Parent root = loader.load();

            ProfileController controller = loader.getController();
            controller.loadUserRentalHistory(); 

            Stage modalStage = new Stage();
            modalStage.setTitle("Customer Profile & Rental History Logs");
            modalStage.initModality(Modality.APPLICATION_MODAL);
            modalStage.initOwner(viewProfileButton.getScene().getWindow());
            modalStage.setScene(new Scene(root, AppConstants.PROFILE_WIDTH, AppConstants.PROFILE_HEIGHT));
            modalStage.setResizable(false);

            modalStage.showAndWait();

        } catch (Exception e) {
            System.err.println("Failed to open ProfileView.fxml:");
            e.printStackTrace();
        }
    }
    
    /**
     * Opens store management panel modal view for administrator accounts.
     */
    private void handleOpenAdminView() {
        try {
            FXMLLoader loader = new FXMLLoader(
                getClass().getResource("/com/rental/client/view/AdminView.fxml")
            );
            Parent root = loader.load();

            Stage modalStage = new Stage();
            modalStage.setTitle("Movie Rental Hub - Store Administration Dashboard");
            modalStage.initModality(Modality.APPLICATION_MODAL);
            modalStage.initOwner(adminPanelButton.getScene().getWindow());
            modalStage.setScene(new Scene(root, 1000, 650));
            modalStage.setResizable(true);

            modalStage.showAndWait();

            // Refresh catalog table on dashboard when closing admin view
            loadMoviesFromBackend();

        } catch (Exception e) {
            System.err.println("Failed to open AdminView.fxml:");
            e.printStackTrace();
        }
    }
    
    /**
     * Clears current user session and returns to the login screen.
     */
    private void handleLogout() {
        // Clear active user session
        UserSession.cleanUserSession();

        try {
            FXMLLoader loader = new FXMLLoader(
                getClass().getResource("/com/rental/client/view/LoginView.fxml")
            );
            Parent loginRoot = loader.load();

            Stage stage = (Stage) logoutButton.getScene().getWindow();
            stage.setScene(new Scene(loginRoot, AppConstants.LOGIN_WIDTH, AppConstants.LOGIN_HEIGHT));
            stage.setTitle("Movie Rental System - Login");
            stage.centerOnScreen();

        } catch (Exception e) {
            System.err.println("Failed to load LoginView.fxml:");
            e.printStackTrace();
        }
    }
}
