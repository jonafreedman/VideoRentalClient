package com.rental.client;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;

public class AuthService {

    private static final String BASE_URL = "http://localhost:8080/api/users/login";
    private final HttpClient httpClient;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public AuthService() {
        this.httpClient = HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(5))
                .build();
    }

    /**
     * Sends a POST request with login credentials to the Spring Boot backend.
     * Returns the user's database ID if login succeeds, or null if it fails.
     */
    public Long authenticateAndGetUserId(String username, String password) {
        try {
            String jsonPayload = String.format("{\"username\":\"%s\", \"password\":\"%s\"}", username, password);

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(BASE_URL))
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(jsonPayload))
                    .build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

            System.out.println("Backend Response Code: " + response.statusCode());
            System.out.println("Backend Response Body: " + response.body());

            if (response.statusCode() == 200) {
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
