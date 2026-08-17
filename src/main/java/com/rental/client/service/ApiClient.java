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

    public ApiClient() {
        this.httpClient = HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(5))
                .build();
        this.objectMapper = new ObjectMapper();
    }

    // 1. Fetch Movie Catalog (GET /api/movies)
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

    // 2. Rent Movie (POST /api/loans/rent)
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

    // 3. Return Movie (POST /api/loans/return)
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

    // 4. Fetch User Rental History Logs (GET /api/users/{username}/loans)
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