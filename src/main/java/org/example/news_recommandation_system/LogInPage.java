package org.example.news_recommandation_system;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;
import java.io.IOException;

public class LogInPage {

    @FXML
    private Button signUpButton;
    @FXML
    private Button btnLogin;

    @FXML
    private void handleSignUpButtonClick() throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("SignUp.fxml"));
        Parent signUpRoot = loader.load();

        Stage stage = (Stage) signUpButton.getScene().getWindow();
        stage.setScene(new Scene(signUpRoot));
        stage.show();
    }

    @FXML
    private void handleLoginButtonClick() throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("MainWindow.fxml"));
        Parent signUpRoot = loader.load();

        Stage stage = (Stage) btnLogin.getScene().getWindow();
        stage.setScene(new Scene(signUpRoot));
        stage.show();
    }


}