package org.example.news_recommandation_system;

import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.event.ActionEvent;
import java.util.Optional;

public class Exit {
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
