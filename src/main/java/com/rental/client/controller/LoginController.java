/**
 * Controller class managing user authentication interactions and main window transition.
 */
package com.rental.client.controller;

import com.rental.client.AuthService;
import com.rental.client.service.ApiClient;
import com.rental.client.util.AppConstants;
import com.rental.client.util.DialogUtil;
import com.rental.client.util.UserSession;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
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
        	DialogUtil.showAlert(AlertType.WARNING, "Validation Error", "Please fill out both username and password.");
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
        	DialogUtil.showAlert(AlertType.WARNING, "Validation Error", "Please enter a username and password to create an account.");
            return;
        }

        boolean success = apiClient.registerUser(username, password);

        if (success) {
        	DialogUtil.showAlert(AlertType.INFORMATION, "Account Created", "Welcome! Account created successfully. Logging you in...");
            executeLogin(username, password); // Auto-login after registration
        } else {
        	DialogUtil.showAlert(AlertType.ERROR, "Registration Failed", "Could not register account. Username may already exist.");
        }
    }

    /**
     * Authenticates with backend and loads the Dashboard view.
     */
    private void executeLogin(String username, String password) {
        com.rental.client.model.User authenticatedUser = authService.authenticateUser(username, password);

        if (authenticatedUser != null) {
            // Pass username, userID and role into UserSession
            UserSession.setInstance(
                authenticatedUser.getUsername(), 
                authenticatedUser.getId(), 
                authenticatedUser.getRole()
            );

            try {
                FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/com/rental/client/view/DashboardView.fxml")
                );
                Parent dashboardRoot = loader.load();
                
                Stage stage = (Stage) loginButton.getScene().getWindow();
                stage.setScene(new Scene(dashboardRoot, AppConstants.DASHBOARD_WIDTH, AppConstants.DASHBOARD_HEIGHT));     
                stage.setTitle("Movie Rental System - Dashboard");
                stage.centerOnScreen();
                
            } catch (Exception e) {
                System.err.println("Failed to load DashboardView.fxml:");
                e.printStackTrace();
            }
        } else {
        	DialogUtil.showAlert(AlertType.ERROR, "Login Failed", "Invalid username or password. Please try again.");
        }
    }
}