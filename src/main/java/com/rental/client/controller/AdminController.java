/**
 * Controller class managing admin functionality: adding movies, adjusting stock, and overseeing store loans.
 */
package com.rental.client.controller;

import com.rental.client.model.Movie;
import com.rental.client.model.RentalLog;
import com.rental.client.service.ApiClient;
import com.rental.client.util.DialogUtil;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;

import java.util.List;

public class AdminController {

    // Tab 1: Inventory Control Controls
    @FXML private TextField newTitleField;
    @FXML private TextField newCategoryField;
    @FXML private TextField newStockField;
    @FXML private Button addMovieButton;

    @FXML private TableView<Movie> inventoryTableView;
    @FXML private TableColumn<Movie, String> invTitleColumn;
    @FXML private TableColumn<Movie, String> invCategoryColumn;
    @FXML private TableColumn<Movie, Integer> invAvailableColumn;

    @FXML private TextField updateStockField;
    @FXML private Button updateStockButton;

    // Tab 2: Store Loans Controls
    @FXML private TableView<RentalLog> adminTrackingTableView;
    @FXML private TableColumn<RentalLog, String> adminUserColumn;
    @FXML private TableColumn<RentalLog, String> adminMovieColumn;
    @FXML private TableColumn<RentalLog, String> adminDateColumn;
    @FXML private Button markReturnedButton;

    // Tab 3: User Management Controls
    @FXML private TableView<com.rental.client.model.User> usersTableView;
    @FXML private TableColumn<com.rental.client.model.User, Long> userIdColumn;
    @FXML private TableColumn<com.rental.client.model.User, String> usernameColumn;
    @FXML private TableColumn<com.rental.client.model.User, String> userRoleColumn;
    @FXML private Button toggleRoleButton;

    private final ObservableList<com.rental.client.model.User> userList = FXCollections.observableArrayList();
    private final ApiClient apiClient = new ApiClient();
    private final ObservableList<Movie> catalogList = FXCollections.observableArrayList();
    private final ObservableList<RentalLog> activeLoansList = FXCollections.observableArrayList();

    /**
     * Binds tables, listens for row selections, and loads initial admin data.
     */
    @FXML
    public void initialize() {
        // Setup Catalog Table
        invTitleColumn.setCellValueFactory(new PropertyValueFactory<>("title"));
        invCategoryColumn.setCellValueFactory(new PropertyValueFactory<>("category"));
        invAvailableColumn.setCellValueFactory(new PropertyValueFactory<>("availableCopies"));
        inventoryTableView.setItems(catalogList);

        // Setup Active Loans Table
        adminUserColumn.setCellValueFactory(new PropertyValueFactory<>("userName"));
        adminMovieColumn.setCellValueFactory(new PropertyValueFactory<>("movieTitle"));
        adminDateColumn.setCellValueFactory(new PropertyValueFactory<>("dateBorrowed"));
        adminTrackingTableView.setItems(activeLoansList);

        // Setup User Management Table 
        userIdColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        usernameColumn.setCellValueFactory(new PropertyValueFactory<>("username"));
        userRoleColumn.setCellValueFactory(new PropertyValueFactory<>("role"));
        usersTableView.setItems(userList);
        
        // Button Listeners
        addMovieButton.setOnAction(e -> handleAddMovie());
        updateStockButton.setOnAction(e -> handleUpdateStock());
        markReturnedButton.setOnAction(e -> handleAdminReturn());
        toggleRoleButton.setOnAction(e -> handleToggleUserRole());

        loadCatalogData();
        loadActiveLoansData();
        loadUserData();
    }

    /** Loads movies from server into inventory table. */
    private void loadCatalogData() {
        catalogList.clear();
        try {
            List<Movie> movies = apiClient.getAllMovies();
            catalogList.addAll(movies);
        } catch (Exception e) {
        	DialogUtil.showAlert(AlertType.ERROR, "Error", "Failed to fetch movie catalog.");
        }
    }

    /** Loads active store loans across all users. */
    private void loadActiveLoansData() {
        activeLoansList.clear();
        try {
            List<RentalLog> activeLoans = apiClient.getActiveStoreLoans();
            activeLoansList.addAll(activeLoans);
        } catch (Exception e) {
        	DialogUtil.showAlert(AlertType.ERROR, "Error", "Failed to fetch active store loans.");
        }
    }

    /** Registers a new movie to catalog. */
    private void handleAddMovie() {
        String title = newTitleField.getText().trim();
        String category = newCategoryField.getText().trim();
        String stockStr = newStockField.getText().trim();

        if (title.isEmpty() || category.isEmpty() || stockStr.isEmpty()) {
        	DialogUtil.showAlert(AlertType.WARNING, "Validation Error", "All fields are required.");
            return;
        }

        try {
            int stock = Integer.parseInt(stockStr);
            Movie newMovie = new Movie(title, category, stock);
            boolean success = apiClient.addMovie(newMovie);

            if (success) {
            	DialogUtil.showAlert(AlertType.INFORMATION, "Success", "Movie successfully added!");
                newTitleField.clear();
                newCategoryField.clear();
                newStockField.clear();
                loadCatalogData();
            } else {
            	DialogUtil.showAlert(AlertType.ERROR, "Error", "Server failed to add movie.");
            }
        } catch (NumberFormatException e) {
        	DialogUtil.showAlert(AlertType.WARNING, "Validation Error", "Initial copies must be a valid number.");
        }
    }

    /** Updates stock for selected table item while validating active loan safety limits. */
    private void handleUpdateStock() {
        Movie selected = inventoryTableView.getSelectionModel().getSelectedItem();
        if (selected == null) {
        	DialogUtil.showAlert(AlertType.WARNING, "Selection Required", "Please select a movie from the inventory table first.");
            return;
        }

        try {
            int newStock = Integer.parseInt(updateStockField.getText().trim());
            boolean success = apiClient.updateMovieStock(selected.getId(), newStock);

            if (success) {
            	DialogUtil.showAlert(AlertType.INFORMATION, "Success", "Stock updated successfully.");
                updateStockField.clear();
                loadCatalogData();
            } else {
            	DialogUtil.showAlert(AlertType.ERROR, "Stock Error", "Stock cannot be reduced below active out-on-loan copies.");
            }
        } catch (NumberFormatException e) {
        	DialogUtil.showAlert(AlertType.WARNING, "Validation Error", "Please enter a valid stock number.");
        }
    }

    /** Processes DVD return for a selected loan record in global tracking queue. */
    private void handleAdminReturn() {
        RentalLog selectedLoan = adminTrackingTableView.getSelectionModel().getSelectedItem();
        if (selectedLoan == null) {
        	DialogUtil.showAlert(AlertType.WARNING, "Selection Required", "Please select an active loan to return.");
            return;
        }

        boolean success = apiClient.returnMovie(selectedLoan.getId());
        if (success) {
        	DialogUtil.showAlert(AlertType.INFORMATION, "Success", "DVD checked back into store inventory.");
            loadActiveLoansData();
            loadCatalogData();
        } else {
        	DialogUtil.showAlert(AlertType.ERROR, "Error", "Could not process check-in.");
        }
    }

    /** Loads registered accounts from server into user table. */
    private void loadUserData() {
        userList.clear();
        try {
            List<com.rental.client.model.User> users = apiClient.getAllUsers();
            userList.addAll(users);
        } catch (Exception e) {
        	DialogUtil.showAlert(AlertType.ERROR, "Error", "Failed to fetch users catalog.");
        }
    }

    /** Change role between USER and ADMIN while enforcing self-modification restriction. */
    private void handleToggleUserRole() {
        com.rental.client.model.User selectedUser = usersTableView.getSelectionModel().getSelectedItem();
        
        if (selectedUser == null) {
        	DialogUtil.showAlert(AlertType.WARNING, "Selection Required", "Please select a user from the table first.");
            return;
        }

        // Check if target user is currently logged-in administrator
        String currentLoggedInUser = com.rental.client.util.UserSession.getInstance().getUsername();
        if (selectedUser.getUsername().equalsIgnoreCase(currentLoggedInUser)) {
        	DialogUtil.showAlert(AlertType.ERROR, "Action Denied", "Security Restriction: You cannot modify your own administrative role.");
            return;
        }

        // Change target user's role
        String targetRole = "ADMIN".equalsIgnoreCase(selectedUser.getRole()) ? "USER" : "ADMIN";
        boolean success = apiClient.updateUserRole(selectedUser.getId(), targetRole);

        if (success) {
        	DialogUtil.showAlert(AlertType.INFORMATION, "Success", "Role updated to " + targetRole + " for user: " + selectedUser.getUsername());
            loadUserData();
        } else {
        	DialogUtil.showAlert(AlertType.ERROR, "Error", "Failed to update role on backend server.");
        }
    }
}