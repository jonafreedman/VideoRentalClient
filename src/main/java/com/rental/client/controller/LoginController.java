/**
 * Controller class managing user authentication interactions and main window transition.
 */
package com.rental.client.controller;

import com.rental.client.AuthService;
import com.rental.client.UserSession;
import com.rental.client.service.ApiClient;

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

    @FXML private TextField usernameField;
    @FXML private PasswordField passwordField;
    @FXML private Button loginButton;
    @FXML private Button registerButton;

    private final AuthService authService = new AuthService();
    private final ApiClient apiClient = new ApiClient();

    @FXML
    public void initialize() {
        loginButton.setOnAction(event -> handleLogin());
        registerButton.setOnAction(event -> handleRegister());
    }

    /**
     * Validates input and logs into existing user account.
     */
    private void handleLogin() {
        String username = usernameField.getText().trim();
        String password = passwordField.getText().trim();

        if (username.isEmpty() || password.isEmpty()) {
            showAlert(AlertType.WARNING, "Validation Error", "Please fill out both username and password.");
            return;
        }

        executeLogin(username, password);
    }

    /**
     * Registers a new account and automatically logs in on success.
     */
    private void handleRegister() {
        String username = usernameField.getText().trim();
        String password = passwordField.getText().trim();

        if (username.isEmpty() || password.isEmpty()) {
            showAlert(AlertType.WARNING, "Validation Error", "Please enter a username and password to create an account.");
            return;
        }

        boolean success = apiClient.registerUser(username, password);

        if (success) {
            showAlert(AlertType.INFORMATION, "Account Created", "Welcome! Account created successfully. Logging you in...");
            executeLogin(username, password); // Auto-login after registration
        } else {
            showAlert(AlertType.ERROR, "Registration Failed", "Could not register account. Username may already exist.");
        }
    }

    /**
     * Authenticates with backend and loads the Dashboard view.
     */
    private void executeLogin(String username, String password) {
        Long userId = authService.authenticateAndGetUserId(username, password);

        if (userId != null) {
            UserSession.setInstance(username, userId);

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