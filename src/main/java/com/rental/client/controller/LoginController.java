package com.rental.client.controller;

import com.rental.client.AuthService;
import com.rental.client.UserSession;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class LoginController {

    @FXML
    private TextField usernameField;
    @FXML
    private PasswordField passwordField;
    @FXML
    private Button loginButton;
    @FXML
    private Button registerButton;

    private final AuthService authService = new AuthService();

    @FXML
    public void initialize() {
        loginButton.setOnAction(event -> handleLogin());

        registerButton.setOnAction(event -> {
            System.out.println("Create Account Clicked!");
        });
    }

    private void handleLogin() {
        String username = usernameField.getText().trim();
        String password = passwordField.getText().trim();

        if (username.isEmpty() || password.isEmpty()) {
            showAlert(AlertType.WARNING, "Validation Error", "Please fill out both username and password.");
            return;
        }

        System.out.println("Attempting connection to backend for user: " + username);
        
        // Authenticate with Spring Boot and fetch user ID
        Long userId = authService.authenticateAndGetUserId(username, password);

        if (userId != null) {
            // 1. Save both username and numeric ID to session memory
            UserSession.setInstance(username, userId);

            // 2. Load Dashboard view
            try {
                FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/com/rental/client/view/DashboardView.fxml")
                );
                Parent dashboardRoot = loader.load();
                
                Stage stage = (Stage) loginButton.getScene().getWindow();
                stage.setScene(new Scene(dashboardRoot, 950, 650));
                stage.setTitle("Movie Rental System - Dashboard");
                stage.centerOnScreen();
                
            } catch (Exception e) {
                System.err.println("Failed to load DashboardView.fxml:");
                e.printStackTrace();
            }
        } else {
            showAlert(AlertType.ERROR, "Login Failed", "Invalid username or password. Please try again.");
        }
    }

    private void showAlert(AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}