module com.rental.client {
    // Links the JavaFX libraries Maven downloaded
    requires transitive javafx.controls;
    requires javafx.fxml;
    requires transitive javafx.graphics;        // Required for Application/Stage access
    requires java.net.http;                     // Enables HTTP client requests
    requires com.fasterxml.jackson.databind;    // Required for Jackson JSON
    
    // Gives JavaFX and Jackson permission to read model properties
    opens com.rental.client to javafx.graphics, javafx.fxml, javafx.base, com.fasterxml.jackson.databind;
    opens com.rental.client.controller to javafx.fxml;
    opens com.rental.client.service to javafx.fxml;

    // Exports packages so controllers and services are visible
    exports com.rental.client;
    exports com.rental.client.controller;
    exports com.rental.client.service;
    
    exports com.rental.client.util;
    opens com.rental.client.util to javafx.fxml;
    
    exports com.rental.client.model;
    opens com.rental.client.model to com.fasterxml.jackson.databind, javafx.base;
}