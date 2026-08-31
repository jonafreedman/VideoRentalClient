/**
 * Controller class displaying account session details and customer rental history logs.
 */
package com.rental.client.controller;

import com.rental.client.model.RentalLog;
import com.rental.client.service.ApiClient;
import com.rental.client.util.DialogUtil;
import com.rental.client.util.UserSession;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableCell;
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
    @FXML private TableColumn<RentalLog, Void> actionColumn;

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

        setupReturnButtonColumn();
        loanHistoryTableView.setItems(historyLogs);

        backToCatalogButton.setOnAction(e -> closeWindow());
    }

    /**
     * Configures cell factory for action column to generate dynamic "Return DVD" buttons on active loans.
     */
    private void setupReturnButtonColumn() {
        actionColumn.setCellFactory(param -> new TableCell<>() {
            private final Button returnButton = new Button("Return DVD");

            {
                returnButton.setStyle("-fx-background-color: #16a34a; -fx-text-fill: white; -fx-font-weight: bold; -fx-cursor: hand; -fx-background-radius: 4px;");
                returnButton.setOnAction(event -> {
                    RentalLog log = getTableView().getItems().get(getIndex());
                    handleReturnDVD(log);
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    RentalLog log = getTableView().getItems().get(getIndex());
                    // Only render button if loan is still active/unreturned
                    if (log.getDateReturned() == null || log.getDateReturned().isBlank() || "RENTED".equalsIgnoreCase(log.getStatus())) {
                        setGraphic(returnButton);
                    } else {
                        setGraphic(null);
                    }
                }
            }
        });
    }

    /**
     * Dispatches check-in request to server for selected loan record.
     *
     * @param log target active loan
     */
    private void handleReturnDVD(RentalLog log) {
        boolean success = apiClient.returnMovie(log.getId());

        if (success) {
        	DialogUtil.showAlert(AlertType.INFORMATION, "DVD Checked In", "Successfully returned: " + log.getMovieTitle());
            loadUserRentalHistory(); // Reload table data
        } else {
        	DialogUtil.showAlert(AlertType.ERROR, "Return Failed", "Server rejected DVD check-in operation.");
        }
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