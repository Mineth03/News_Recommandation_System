package org.example.news_recommendation_system.Service;

import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.event.ActionEvent;

import java.util.Optional;

public class ExitAndAlerts {

    public static void showAlert(Alert.AlertType alertType, String title, String message) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    public static boolean confirmationAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        Optional<ButtonType> output = alert.showAndWait();

        return output.isEmpty() || output.get() == ButtonType.OK;
    }

    public static void showExitConfirmation(ActionEvent event) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Exit");
        alert.setHeaderText("Are you sure you want to exit?");
        Optional<ButtonType> output = alert.showAndWait();

        // If the user confirms the exit
        if (output.isPresent() && output.get() == ButtonType.OK) {
            System.exit(0);
        }
    }


}
