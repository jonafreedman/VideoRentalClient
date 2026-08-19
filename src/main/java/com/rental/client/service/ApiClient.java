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
     * Submits an HTTP PUT request to check in a borrowed DVD copy by its loan ID.
     *
     * @param loanId unique identifier of the active loan transaction record
     * @return true if the backend confirms successful return check-in (HTTP 200)
     * @throws Exception if HTTP communication fails or connection times out
     */
    public boolean returnMovie(Long loanId) throws Exception {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + "/loans/return/" + loanId))
                .PUT(HttpRequest.BodyPublishers.noBody())
                .header("Content-Type", "application/json")
                .build();

        HttpResponse<String> response = this.httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        return response.statusCode() == 200;
    }

    /**
     * Fetches historical rental transaction logs for a specific customer by user ID.
     *
     * @param userId unique primary key identifier of the targeted user account
     * @return list of historical rental log records belonging to the account
     * @throws Exception if HTTP communication fails or response status is non-200
     */
    public List<RentalLog> getUserRentalHistory(Long userId) throws Exception {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + "/loans/user/" + userId))
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
    
    /**
     * Submits a POST request to register a new customer account on the backend server.
     *
     * @param username requested account handle
     * @param password account authentication password
     * @return true if backend acknowledges successful account creation (HTTP 200/201)
     */
    public boolean registerUser(String username, String password) {
        try {
            String jsonPayload = String.format("{\"username\":\"%s\", \"password\":\"%s\"}", username, password);

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(BASE_URL + "/users/register"))
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(jsonPayload))
                    .build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            return response.statusCode() == 200 || response.statusCode() == 201;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}