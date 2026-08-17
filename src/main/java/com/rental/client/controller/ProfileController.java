/**
 * Controller class displaying account details and loan history records for the current user session.
 */
package com.rental.client.controller;

import com.rental.client.Movie;
import com.rental.client.RentalLog;
import com.rental.client.UserSession;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;

import java.time.LocalDate;

public class ProfileController {

    @FXML private Label profileUserLabel;
    @FXML private Button backToCatalogButton;

    @FXML private TableView<RentalLog> loanHistoryTableView;
    @FXML private TableColumn<RentalLog, String> loanTitleColumn;
    @FXML private TableColumn<RentalLog, String> rentDateColumn;
    @FXML private TableColumn<RentalLog, String> returnDateColumn;
    @FXML private TableColumn<RentalLog, String> loanStatusColumn;

    private final ObservableList<RentalLog> historyLogs = FXCollections.observableArrayList();

    /**
     * Displays the active session username and binds table view columns to RentalLog fields.
     */
    @FXML
    public void initialize() {
        // Set logged-in username
        String activeUser = (UserSession.getInstance() != null) ? UserSession.getInstance().getUsername() : "customer_user_01";
        profileUserLabel.setText("Active Account: " + activeUser);

        // Bind Table View Columns
        loanTitleColumn.setCellValueFactory(new PropertyValueFactory<>("movieTitle"));
        rentDateColumn.setCellValueFactory(new PropertyValueFactory<>("dateBorrowed"));
        returnDateColumn.setCellValueFactory(new PropertyValueFactory<>("dateReturned"));
        loanStatusColumn.setCellValueFactory(new PropertyValueFactory<>("status"));

        loanHistoryTableView.setItems(historyLogs);

        // Close window action
        backToCatalogButton.setOnAction(e -> closeWindow());
    }

    /**
     * Populates the user's rental history table by pairing active catalog rentals with mock historical logs.
     *
     * @param allMovies master movie observable list
     */
    public void loadUserRentalHistory(ObservableList<Movie> allMovies) {
        historyLogs.clear();

        // Populate history logs based on active rentals + seed historical returns
        for (Movie movie : allMovies) {
            if ("Rented".equalsIgnoreCase(movie.getStatus())) {
                historyLogs.add(new RentalLog(
                    movie.getTitle(),
                    LocalDate.now().minusDays(3).toString(),
                    "Active Loan",
                    "ACTIVE"
                ));
            }
        }

        // Add completed historical logs for testing UI
        historyLogs.add(new RentalLog("Inception", "2026-07-10", "2026-07-15", "RETURNED"));
        historyLogs.add(new RentalLog("The Matrix", "2026-06-01", "2026-06-05", "RETURNED"));
    }

    /**
     * Closes the profile stage window.
     */
    private void closeWindow() {
        Stage stage = (Stage) backToCatalogButton.getScene().getWindow();
        stage.close();
    }
}
