package com.rental.client.controller;

import com.rental.client.UserSession;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;

public class DashboardController {

    @FXML
    private ListView<String> categoryListView;
    @FXML
    private TextField searchTextField;
    @FXML
    private Button searchButton;
    @FXML
    private TableView<?> moviesTableView;
    @FXML
    private TableColumn<?, ?> titleColumn;
    @FXML
    private TableColumn<?, ?> categoryColumn;
    @FXML
    private TableColumn<?, ?> availabilityColumn;
    @FXML
    private Button viewDetailsButton;
    @FXML
    private Button viewProfileButton;

    @FXML
    public void initialize() {
        // 1. Personalize the profile button with the logged-in user's name
        if (UserSession.getInstance() != null) {
            String currentUser = UserSession.getInstance().getUsername();
            viewProfileButton.setText("👤 " + currentUser + "'s Account");
        }

        // 2. Set up initial event listeners
        searchButton.setOnAction(event -> handleSearch());
        viewDetailsButton.setOnAction(event -> handleInspectAndRent());
        viewProfileButton.setOnAction(event -> handleViewProfile());
    }

    private void handleSearch() {
        String query = searchTextField.getText().trim();
        System.out.println("Searching catalog for: " + query);
        // Will connect this to Spring Boot movie search API 
    }

    private void handleInspectAndRent() {
        System.out.println("Inspect & Rent clicked!");
        // Handles opening movie details and processing rentals
    }

    private void handleViewProfile() {
        System.out.println("Account profile clicked for user: " + UserSession.getInstance().getUsername());
        // Will show current active rentals & rental history
    }
}
