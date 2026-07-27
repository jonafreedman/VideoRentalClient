module com.rental.client {
    // Links the JavaFX libraries Maven downloaded
    requires javafx.controls;
    requires javafx.fxml;
    requires java.net.http; // Enables HTTP client requests
    
    // Gives JavaFX permission to open screens and read model properties (PropertyValueFactory)
    opens com.rental.client to javafx.graphics, javafx.fxml, javafx.base;
    opens com.rental.client.controller to javafx.fxml;
}