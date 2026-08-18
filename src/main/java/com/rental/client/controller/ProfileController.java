/**
 * Controller class displaying account session details and customer rental history logs.
 */
package com.rental.client.controller;

import com.rental.client.RentalLog;
import com.rental.client.UserSession;
import com.rental.client.service.ApiClient;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;

import java.util.List;

public class ProfileController {

    @FXML private Label profileUserLabel;
    @FXML private Button backToCatalogButton;

    @FXML private TableView<RentalLog> loanHistoryTableView;
    @FXML private TableColumn<RentalLog, String> loanTitleColumn;
    @FXML private TableColumn<RentalLog, String> rentDateColumn;
    @FXML private TableColumn<RentalLog, String> returnDateColumn;
    @FXML private TableColumn<RentalLog, String> loanStatusColumn;

    private final ObservableList<RentalLog> historyLogs = FXCollections.observableArrayList();
    private final ApiClient apiClient = new ApiClient();

    /**
     * Binds table columns to RentalLog fields and displays active account username.
     */
    @FXML
    public void initialize() {
        String activeUser = (UserSession.getInstance() != null) ? UserSession.getInstance().getUsername() : "Guest";
        profileUserLabel.setText("Active Account: " + activeUser);

        loanTitleColumn.setCellValueFactory(new PropertyValueFactory<>("movieTitle"));
        rentDateColumn.setCellValueFactory(new PropertyValueFactory<>("dateBorrowed"));
        returnDateColumn.setCellValueFactory(new PropertyValueFactory<>("dateReturned"));
        loanStatusColumn.setCellValueFactory(new PropertyValueFactory<>("status"));

        loanHistoryTableView.setItems(historyLogs);

        backToCatalogButton.setOnAction(e -> closeWindow());
    }

    /**
     * Fetches real user rental logs from the Spring Boot API endpoint using the active session user ID.
     */
    public void loadUserRentalHistory() {
        historyLogs.clear();

        if (UserSession.getInstance() == null) {
            return;
        }

        Long currentUserId = UserSession.getInstance().getUserId();

        try {
            List<RentalLog> userLogs = apiClient.getUserRentalHistory(currentUserId);
            historyLogs.addAll(userLogs);
        } catch (Exception e) {
            System.err.println("Could not load user rental logs from server.");
            e.printStackTrace();
        }
    }

    /**
     * Closes current stage window to return to catalog dashboard.
     */
    private void closeWindow() {
        Stage stage = (Stage) backToCatalogButton.getScene().getWindow();
        stage.close();
    }
}