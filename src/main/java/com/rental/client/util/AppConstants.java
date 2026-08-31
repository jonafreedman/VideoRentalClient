package com.rental.client.util;

import java.net.HttpURLConnection;

/**
 * Centralized constant definitions for backend API communication, 
 * HTTP status validation, and JavaFX interface dimensions.
 */
public final class AppConstants {

    // Private constructor prevents instantiation of utility class
    private AppConstants() {}

    // -----------------------------------------------------------------
    // API & HTTP Constants
    // -----------------------------------------------------------------
    public static final String BASE_URL = "http://localhost:8080/api";
    public static final String AUTH_LOGIN_URL = BASE_URL + "/users/login";
    public static final int HTTP_TIMEOUT_SECONDS = 5;
    
    public static final int HTTP_OK = HttpURLConnection.HTTP_OK;          // 200
    public static final int HTTP_CREATED = HttpURLConnection.HTTP_CREATED;  // 201

    // -----------------------------------------------------------------
    // JavaFX Window Dimensions
    // -----------------------------------------------------------------
    public static final double LOGIN_WIDTH = 400.0;
    public static final double LOGIN_HEIGHT = 350.0;
    
    public static final double DASHBOARD_WIDTH = 950.0;
    public static final double DASHBOARD_HEIGHT = 650.0;
    
    public static final double DETAIL_WIDTH = 750.0;
    public static final double DETAIL_HEIGHT = 620.0;
    
    public static final double PROFILE_WIDTH = 750.0;
    public static final double PROFILE_HEIGHT = 500.0;
}
