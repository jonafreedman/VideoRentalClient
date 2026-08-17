/**
 * Main application entry point that bootstraps the JavaFX user interface.
 */
package com.rental.client;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class ClientApplication extends Application {

	/**
     * Initializes and displays the primary stage with the Login FXML view layout.
     *
     * @param primaryStage top-level JavaFX stage container
     */
    @Override
    public void start(Stage primaryStage) {
        try {
            // Links Java code to visual design file
            Parent root = FXMLLoader.load(getClass().getResource("/com/rental/client/view/LoginView.fxml"));
            
            primaryStage.setTitle("Video Rental Store - Login");
            primaryStage.setScene(new Scene(root, 400, 350));
            primaryStage.setResizable(false);
            primaryStage.show();
        } catch (IOException e) {
            System.err.println("Error: Could not load LoginView.fxml. Make sure the file exists in the view folder!");
            e.printStackTrace();
        }
    }

    /**
     * Launches the JavaFX application execution runtime.
     *
     * @param args command-line arguments
     */
    public static void main(String[] args) {
        launch(args); // Launches JavaFX background thread
    }
}
