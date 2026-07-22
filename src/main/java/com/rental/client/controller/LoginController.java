package com.rental.client.controller;

import com.rental.client.AuthService;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

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
            // Optional: Switch to registration screen later
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
        
        // Make the HTTP request
        boolean success = authService.login(username, password);

        if (success) {
            showAlert(AlertType.INFORMATION, "Success", "Welcome back, " + username + "!");
            // Next: Open your main application Dashboard here
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