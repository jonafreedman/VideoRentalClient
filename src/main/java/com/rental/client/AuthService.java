package com.rental.client;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;

public class AuthService {

    
    private static final String BASE_URL = "http://localhost:8080/api/users/login";
    private final HttpClient httpClient;

    public AuthService() {
        this.httpClient = HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(5))
                .build();
    }

    /**
     * Sends a POST request with login credentials to the Spring Boot backend.
     * Returns true if login is successful (HTTP 200), false otherwise.
     */
    public boolean login(String username, String password) {
        try {
            // Simple JSON payload string
            String jsonPayload = String.format("{\"username\":\"%s\", \"password\":\"%s\"}", username, password);

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(BASE_URL))
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(jsonPayload))
                    .build();

            // Send the request and wait for the response
            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

            System.out.println("Backend Response Code: " + response.statusCode());
            System.out.println("Backend Response Body: " + response.body());

            // If the server returns HTTP 200 OK, the login is valid
            return response.statusCode() == 200;

        } catch (Exception e) {
            System.err.println("Error communicating with backend server:");
            e.printStackTrace();
            return false;
        }
    }
}
