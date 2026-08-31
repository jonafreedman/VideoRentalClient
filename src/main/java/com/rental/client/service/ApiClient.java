/**
 * Service client responsible for dispatching HTTP requests to the Spring Boot REST API endpoints.
 */
package com.rental.client.service;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.Collections;
import java.util.List;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.rental.client.model.Movie;
import com.rental.client.model.RentalLog;
import com.rental.client.model.Review;
import com.rental.client.util.AppConstants;

public class ApiClient {

    private final HttpClient httpClient;
    private final ObjectMapper objectMapper;

    /**
     * Initializes the HTTP client instance with a 5-second connection timeout configuration.
     */
    public ApiClient() {
        this.httpClient = HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(AppConstants.HTTP_TIMEOUT_SECONDS))
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
                .uri(URI.create(AppConstants.BASE_URL + "/movies"))
                .GET()
                .header("Accept", "application/json")
                .build();

        HttpResponse<String> response = this.httpClient.send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() == AppConstants.HTTP_OK) {
            String responseBody = response.body();
            return objectMapper.readValue(responseBody, new TypeReference<List<Movie>>() {});
        } else {
            throw new RuntimeException("Failed to fetch movies: HTTP " + response.statusCode());
        }
    }

    /**
     * Submits a POST request to record a movie rental for a specific user ID.
     *
     * @param movieId target movie ID to rent
     * @param userId unique ID of the requesting user account
     * @return true if backend acknowledges successful rental (HTTP 200/201); false otherwise
     */
    public boolean rentMovie(Long movieId, Long userId) {
        try {
            String url = AppConstants.BASE_URL + "/loans/rent?userId=" + userId + "&movieId=" + movieId;

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .POST(HttpRequest.BodyPublishers.noBody())
                    .header("Content-Type", "application/json")
                    .build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

            System.out.println("Rent Status Code: " + response.statusCode());
            System.out.println("Rent Response Body: " + response.body());

            return response.statusCode() == AppConstants.HTTP_OK || response.statusCode() == AppConstants.HTTP_CREATED;
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
     */
    public boolean returnMovie(Long loanId) {
        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(AppConstants.BASE_URL + "/loans/return/" + loanId))
                    .PUT(HttpRequest.BodyPublishers.noBody())
                    .header("Content-Type", "application/json")
                    .build();

            HttpResponse<String> response = this.httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            return response.statusCode() == AppConstants.HTTP_OK;
        } catch (Exception e) {
            System.err.println("Failed to return movie for loan ID " + loanId);
            e.printStackTrace();
            return false;
        }
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
                .uri(URI.create(AppConstants.BASE_URL + "/loans/user/" + userId))
                .GET()
                .header("Accept", "application/json")
                .build();

        HttpResponse<String> response = this.httpClient.send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() == AppConstants.HTTP_OK) {
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
                    .uri(URI.create(AppConstants.BASE_URL + "/users/register"))
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(jsonPayload))
                    .build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            return response.statusCode() == AppConstants.HTTP_OK || response.statusCode() == AppConstants.HTTP_CREATED;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
 
    /**
     * Fetches all community reviews for a specific movie from the backend API.
     * 
     * @param movieId The database ID of the target movie
     * @return List of Review entities, or an empty list if none exist/error occurs
     */
    public List<Review> getReviewsByMovieId(Long movieId) {
        try {
            String url = AppConstants.BASE_URL + "/reviews/movie/" + movieId;
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .GET()
                    .build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() == AppConstants.HTTP_OK) {
                return objectMapper.readValue(response.body(), new TypeReference<List<Review>>() {});
            }
        } catch (Exception e) {
            System.err.println("Failed to fetch reviews for movie ID " + movieId);
            e.printStackTrace();
        }
        return Collections.emptyList();
    }

    
    /**
     * Sends a new review payload to the backend API to persist in the database.
     * 
     * @param movieId The database ID of the movie being reviewed
     * @param userId The database ID of the user submitting the review
     * @param rating Integer value (1-5) representing star rating
     * @param reviewText Written review text
     * @return true if successfully saved (200/201), false otherwise
     */
    public boolean addReview(Long movieId, Long userId, int rating, String reviewText) {
        try {
            String url = AppConstants.BASE_URL + "/reviews";
            String jsonPayload = String.format(
                "{\"movie\":{\"id\":%d}, \"user\":{\"id\":%d}, \"rating\":%d, \"reviewText\":\"%s\"}",
                movieId, userId, rating, reviewText
            );

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(jsonPayload))
                    .build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            return response.statusCode() == AppConstants.HTTP_OK || response.statusCode() == AppConstants.HTTP_CREATED;
        } catch (Exception e) {
            System.err.println("Failed to submit review");
            e.printStackTrace();
            return false;
        }
    }
    
    /**
     * Checks if a user has ever rented a specific movie by inspecting historical loans.
     * 
     * @param userId primary key of the user
     * @param movieId primary key of the movie
     * @param movieTitle title string of the movie to check against
     * @return true if a rental record exists for this user and movie title
     */
    public boolean hasUserRentedMovie(Long userId, Long movieId, String movieTitle) {
        try {
            List<RentalLog> history = getUserRentalHistory(userId);
            return history.stream().anyMatch(log -> log.getMovieTitle().equalsIgnoreCase(movieTitle));
        } catch (Exception e) {
            System.err.println("Could not verify user rental history.");
            return false;
        }
    }
    
    /**
     * Submits HTTP POST request to register a new movie.
     *
     * @param movie movie payload to save
     * @return true if backend confirms creation (HTTP 200)
     */
    public boolean addMovie(Movie movie) {
        try {
            String jsonBody = objectMapper.writeValueAsString(movie);
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(AppConstants.BASE_URL + "/movies"))
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(jsonBody))
                    .build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            return response.statusCode() == AppConstants.HTTP_OK;
        } catch (Exception e) {
            System.err.println("Failed to add new movie to server.");
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Submits HTTP PUT request to update available copies for a specific movie title.
     *
     * @param movieId target movie primary key
     * @param newStock target physical copy count
     * @return true if update succeeds (HTTP 200)
     */
    public boolean updateMovieStock(Long movieId, int newStock) {
        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(AppConstants.BASE_URL + "/movies/" + movieId + "/stock?newCount=" + newStock))
                    .PUT(HttpRequest.BodyPublishers.noBody())
                    .build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            return response.statusCode() == AppConstants.HTTP_OK;
        } catch (Exception e) {
            System.err.println("Failed to update stock for movie ID " + movieId);
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Retrieves all active unreturned store loans across all users.
     *
     * @return list of active RentalLog items
     */
    public List<RentalLog> getActiveStoreLoans() {
        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(AppConstants.BASE_URL + "/loans/active"))
                    .GET()
                    .build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() == AppConstants.HTTP_OK) {
                return objectMapper.readValue(response.body(), new TypeReference<List<RentalLog>>() {});
            }
        } catch (Exception e) {
            System.err.println("Failed to retrieve active store loans.");
            e.printStackTrace();
        }
        return Collections.emptyList();
    }
    
    /**
     * Fetches all registered system users from the backend REST endpoint.
     *
     * @return list of User entities retrieved from the database, or an empty list if an error occurs
     */
    public List<com.rental.client.model.User> getAllUsers() {
        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(AppConstants.BASE_URL + "/users"))
                    .GET()
                    .header("Accept", "application/json")
                    .build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() == AppConstants.HTTP_OK) {
                return objectMapper.readValue(response.body(), new TypeReference<List<com.rental.client.model.User>>() {});
            }
        } catch (Exception e) {
            System.err.println("Failed to fetch registered users.");
            e.printStackTrace();
        }
        return Collections.emptyList();
    }

    /**
     * Submits an HTTP PUT request to update the role of a specific user account.
     *
     * @param userId unique primary key identifier of the target user account
     * @param newRole target role string to assign (e.g., "USER" or "ADMIN")
     * @return true if backend acknowledges successful role update (HTTP 200); false otherwise
     */
    public boolean updateUserRole(Long userId, String newRole) {
        try {
            String url = AppConstants.BASE_URL + "/users/" + userId + "/role?newRole=" + newRole;
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .PUT(HttpRequest.BodyPublishers.noBody())
                    .build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            return response.statusCode() == AppConstants.HTTP_OK;
        } catch (Exception e) {
            System.err.println("Failed to update user role.");
            e.printStackTrace();
            return false;
        }
    }
}