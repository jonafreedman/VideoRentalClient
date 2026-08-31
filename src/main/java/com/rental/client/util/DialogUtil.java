/** 
 * Utility class for displaying alerts 
 */
package com.rental.client.util;

import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;

public final class DialogUtil {

    private DialogUtil() {} // Prevent instantiation

    /**
     * Displays dialog boxes for system alerts and notifications.
     *
     * @param alertType alert type classification
     * @param title modal title text
     * @param content main message body
     */
    public static void showAlert(AlertType alertType, String title, String content) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}
