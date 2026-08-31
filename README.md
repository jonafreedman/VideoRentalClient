# Video Rental Store - JavaFX Desktop Client

A JavaFX desktop client interface built to interact seamlessly with the Spring Boot RESTful API backend. Provides dual user modes (Customer Dashboard & Store Management Administration Panel) for seamless movie browsing, rental checkouts, history tracking, and inventory control.

---

## Features & User Interface

* **User Authentication:** User login and auto-login account registration UI.
* **Interactive Main Dashboard:**
  * Real-time search by title/category and multi-criteria genre filter.
  * Personal account overview button displaying logged-in session details.
* **Movie Inspection & Rental Dialog:**
  * Live stock availability status banner.
  * Instant DVD checkout with automated backend inventory updating.
  * Community ratings display (1–5 Stars) and user critique submission.
* **Customer Profile & Loan History:**
  * Customer loan history log showing checkout/return timestamps.
  * Interactive "Return DVD" action buttons for outstanding active rentals.
* **Store Management Dashboard (Admin Only):**
  * **Inventory Control:** Register new movie titles into backend catalog and update available copy stock.
  * **Global Active Store Loans:** Oversee outstanding customer rentals and process store check-ins.
  * **User Management:** View registered users and toggle account roles (`USER` / `ADMIN`), with self-demotion security protection.

---

## Project Structure

```text
com.rental.client/
 ClientApplication.java        # Main JavaFX Execution Entry Point
 AuthService.java              # User Authentication HTTP service
 controller/                   # JavaFX Controllers
    AdminController.java      # Admin Management Panel controller
    DashboardController.java  # Main Catalog Dashboard controller
    LoginController.java      # Sign-in & Registration controller
    MovieDetailController.java# Movie modal details & rental controller
    ProfileController.java    # Account profile & user loan history controller
 model/                        # Client Data Transfer Objects (DTOs)
    Movie.java
    RentalLog.java
    Review.java
    User.java
 service/
    ApiClient.java            # Service for dispatching REST HTTP requests
 util/
    AppConstants.java         # Centralized API URLs & layout configurations
    DialogUtil.java           # JavaFX Alert modal helper
    UserSession.java          # Singleton tracking authenticated session state
 view/ (Resources)             # FXML Visual Layout Designs
     AdminView.fxml
     DashboardView.fxml
     LoginView.fxml
     MovieDetailView.fxml
     ProfileView.fxml
```

---

## Configuration

Backend endpoints and UI window dimension settings are centralized in `com.rental.client.util.AppConstants`:

```java
public static final String BASE_URL = "http://localhost:8080/api";
public static final String AUTH_LOGIN_URL = BASE_URL + "/users/login";
public static final int HTTP_TIMEOUT_SECONDS = 5;
```

---

## Running the Client

### Prerequisites
* JDK 17+ with JavaFX SDK configured (or Maven dependencies included).
* Ensure the **Spring Boot REST Server** is running on `http://localhost:8080`.

### Execution Steps
1. Open the client project in Eclipse or your preferred IDE.
2. Ensure JavaFX build path/modules are configured properly.
3. Run `ClientApplication.java` as a **Java Application**.
