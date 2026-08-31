/**
 * Service class handling HTTP authentication requests to the Spring Boot REST backend.
 */
package com.rental.client;

import com.rental.client.model.User;
import com.rental.client.util.AppConstants;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;

public class AuthService {

    private final HttpClient httpClient;
    private final ObjectMapper objectMapper = new ObjectMapper();

    /**
     * Initializes the client with a 5-second connection timeout setting.
     */
    public AuthService() {
        this.httpClient = HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(AppConstants.HTTP_TIMEOUT_SECONDS))
                .build();
    }

    /**
     * Sends a POST request with credentials to the backend and returns the authenticated User entity.
     *
     * @param username user account handle
     * @param password user secret key
     * @return User object populated with id, username, and role on successful login; null on error
     */
    public User authenticateUser(String username, String password) {
        try {
            String jsonPayload = String.format("{\"username\":\"%s\", \"password\":\"%s\"}", username, password);

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(AppConstants.AUTH_LOGIN_URL))
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(jsonPayload))
                    .build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

            System.out.println("Backend Response Code: " + response.statusCode());
            System.out.println("Backend Response Body: " + response.body());

            if (response.statusCode() == AppConstants.HTTP_OK) {
                return objectMapper.readValue(response.body(), User.class);
            }
            return null;

        } catch (Exception e) {
            System.err.println("Error communicating with backend server:");
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Sends a POST request with credentials to the backend and extracts the authenticated User ID.
     *
     * @param username user account handle
     * @param password user secret key
     * @return database primary key ID on successful login; null on error or bad credentials
     */
    public Long authenticateAndGetUserId(String username, String password) {
        try {
            // Building raw JSON strings, escaping if special characters are present
            String jsonPayload = String.format("{\"username\":\"%s\", \"password\":\"%s\"}", username, password);

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(AppConstants.AUTH_LOGIN_URL))
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(jsonPayload))
                    .build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

            System.out.println("Backend Response Code: " + response.statusCode());
            System.out.println("Backend Response Body: " + response.body());

            if (response.statusCode() == AppConstants.HTTP_OK) {
                // Parse JSON response to extract user ID
                JsonNode root = objectMapper.readTree(response.body());
                if (root.has("id")) {
                    return root.get("id").asLong();
                }
            }
            return null;

        } catch (Exception e) {
            System.err.println("Error communicating with backend server:");
            e.printStackTrace();
            return null;
        }
    }
}