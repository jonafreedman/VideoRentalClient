/**
 * Service client responsible for dispatching HTTP requests to the Spring Boot REST API endpoints.[cite: 31]
 */
package com.rental.client.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.rental.client.Movie;
import com.rental.client.RentalLog;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.List;

public class ApiClient {

    private static final String BASE_URL = "http://localhost:8080/api";
    private final HttpClient httpClient;
    private final ObjectMapper objectMapper;

    /**
     * Initializes the HTTP client instance with a 5-second connection timeout configuration.
     */
    public ApiClient() {
        this.httpClient = HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(5))
                .build();
        this.objectMapper = new ObjectMapper();
    }

    /**
     * Fetches the complete movie catalog from the Spring Boot backend REST endpoint.
     *
     * @return list of Movie entities retrieved from the catalog database
     * @throws Exception if HTTP communication fails or the response status is non-200
     */
    public List<Movie> getAllMovies() throws Exception {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + "/movies"))
                .GET()
                .header("Accept", "application/json")
                .build();

        HttpResponse<String> response = this.httpClient.send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() == 200) {
            String responseBody = response.body();
            return objectMapper.readValue(responseBody, new TypeReference<List<Movie>>() {});
        } else {
            throw new RuntimeException("Failed to fetch movies: HTTP " + response.statusCode());
        }
    }

    /**
     * Tricky Bit: Parameter 'movieModifierId' must match the parameter name expected by the backend controller endpoint.
     *
     * Submits a POST request to record a movie rental for a specific user ID.
     *
     * @param movieId target movie ID to rent
     * @param userId unique ID of the requesting user account
     * @return true if backend acknowledges successful rental (HTTP 200/201); false otherwise
     */
    public boolean rentMovie(Long movieId, Long userId) {
        try {
            // Matches @RequestParam Long userId, @RequestParam Long movieModifierId
            String url = BASE_URL + "/loans/rent?userId=" + userId + "&movieModifierId=" + movieId;

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .POST(HttpRequest.BodyPublishers.noBody())
                    .header("Content-Type", "application/json")
                    .build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

            System.out.println("Rent Status Code: " + response.statusCode());
            System.out.println("Rent Response Body: " + response.body());

            return response.statusCode() == 200 || response.statusCode() == 201;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Submits a JSON payload request to check in a borrowed DVD copy.
     *
     * @param movieId target movie ID
     * @param username handle of the user returning the DVD copy
     * @return true if return transaction succeeded
     * @throws Exception if connection fails
     */
    public boolean returnMovie(Long movieId, String username) throws Exception {
        String jsonPayload = String.format("{\"movieId\":%d, \"username\":\"%s\"}", movieId, username);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + "/loans/return"))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(jsonPayload))
                .build();

        HttpResponse<String> response = this.httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        return response.statusCode() == 200;
    }

    /**
     * Requests user rental history records by username.
     *
     * @param username user account handle
     * @return list of historical rental log records
     * @throws Exception if request fails
     */
    public List<RentalLog> getUserRentalHistory(String username) throws Exception {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + "/users/" + username + "/loans"))
                .GET()
                .header("Accept", "application/json")
                .build();

        HttpResponse<String> response = this.httpClient.send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() == 200) {
            String responseBody = response.body();
            return objectMapper.readValue(responseBody, new TypeReference<List<RentalLog>>() {});
        } else {
            throw new RuntimeException("Failed to fetch rental history: HTTP " + response.statusCode());
        }
    }
}