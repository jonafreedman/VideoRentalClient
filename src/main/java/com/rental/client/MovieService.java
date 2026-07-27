package com.rental.client;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.List;

public class MovieService {

    private final HttpClient httpClient = HttpClient.newHttpClient();
    private final String BASE_URL = "http://localhost:8080/api/movies";

    public List<Movie> fetchAllMovies() {
        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(BASE_URL))
                    .GET()
                    .header("Accept", "application/json")
                    .build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() == 200) {
                return parseMoviesJson(response.body());
            }
        } catch (Exception e) {
            System.err.println("Error fetching movies from backend: " + e.getMessage());
        }

        // Return sample fallback data if backend is offline or empty
        return getFallbackMovies();
    }

    // Basic JSON parser helper (converts JSON string to Movie list)
    private List<Movie> parseMoviesJson(String json) {
        List<Movie> movies = new ArrayList<>();
        // Simple manual parsing if Jackson/Gson isn't in pom.xml yet
        if (json == null || json.trim().isEmpty() || json.equals("[]")) {
            return getFallbackMovies();
        }

        // Clean brackets
        String clean = json.replace("[", "").replace("]", "").trim();
        if (clean.isEmpty()) return getFallbackMovies();

        String[] objects = clean.split("\\},\\{");
        for (String obj : objects) {
            obj = obj.replace("{", "").replace("}", "").replace("\"", "");
            String[] fields = obj.split(",");
            
            Long id = 0L;
            String title = "Unknown";
            String category = "General";
            String status = "Available";

            for (String field : fields) {
                String[] keyVal = field.split(":");
                if (keyVal.length == 2) {
                    String key = keyVal[0].trim();
                    String val = keyVal[1].trim();

                    if (key.equalsIgnoreCase("id")) id = Long.parseLong(val);
                    if (key.equalsIgnoreCase("title")) title = val;
                    if (key.equalsIgnoreCase("category")) category = val;
                    if (key.equalsIgnoreCase("status") || key.equalsIgnoreCase("availability")) status = val;
                }
            }
            movies.add(new Movie(id, title, category, status));
        }
        return movies;
    }

    private List<Movie> getFallbackMovies() {
        List<Movie> sampleList = new ArrayList<>();
        sampleList.add(new Movie(1L, "Inception", "Sci-Fi & Fantasy", "Available"));
        sampleList.add(new Movie(2L, "The Dark Knight", "Action & Adventure", "Available"));
        sampleList.add(new Movie(3L, "Interstellar", "Sci-Fi & Fantasy", "Rented"));
        sampleList.add(new Movie(4L, "Superbad", "Comedy", "Available"));
        sampleList.add(new Movie(5L, "The Conjuring", "Horror", "Available"));
        sampleList.add(new Movie(6L, "The Godfather", "Drama", "Rented"));
        return sampleList;
    }
}