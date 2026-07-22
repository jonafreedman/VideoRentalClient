module com.rental.client {
    // Links the JavaFX libraries Maven downloaded
    requires javafx.controls;
    requires javafx.fxml;
    requires java.net.http; // Enables HTTP client requests
    
    // Gives JavaFX permission to open the screens
    opens com.rental.client to javafx.graphics;
    opens com.rental.client.controller to javafx.fxml;
}