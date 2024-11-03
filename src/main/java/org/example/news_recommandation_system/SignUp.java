package org.example.news_recommandation_system;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.stage.Stage;

import java.io.IOException;

public class SignUp {

    @FXML
    private Button backButton;

    @FXML
    private void handleBackButtonClick() throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("LogInPage.fxml"));
        Parent signUpRoot = loader.load();

        Stage stage = (Stage) backButton.getScene().getWindow();
        stage.setScene(new Scene(signUpRoot));
        stage.show();
    }

}
