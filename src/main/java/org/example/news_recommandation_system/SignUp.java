package org.example.news_recommandation_system;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

import java.io.IOException;

public class SignUp {

    @FXML
    private Button btnBack;
    @FXML
    private Button btnBack2;
    @FXML
    private Button btnContinue;
    @FXML
    private Button btnSignup;
    @FXML
    private Button btnNext;

    @FXML
    private Pane panePassword;
    @FXML
    private Pane paneRecords;
    @FXML
    private Pane paneSuccess;

    @FXML
    private void handleBackButtonClick() throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("LogInPage.fxml"));
        Parent signUpRoot = loader.load();

        Stage stage = (Stage) btnBack.getScene().getWindow();
        stage.setScene(new Scene(signUpRoot));
        stage.show();
    }

    @FXML
    private void handleContinueButtonClick() throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("MainWindow.fxml"));
        Parent signUpRoot = loader.load();

        Stage stage = (Stage) btnContinue.getScene().getWindow();
        stage.setScene(new Scene(signUpRoot));
        stage.show();
    }

    @FXML
    public void buttonClicksConfig(ActionEvent actionEvent){
        if (actionEvent.getSource() == btnNext){
            panePassword.toFront();
        }
        if (actionEvent.getSource() == btnBack2){
            paneRecords.toFront();
        }
        if (actionEvent.getSource() == btnSignup) {
            paneSuccess.toFront();
        }
    }

}
