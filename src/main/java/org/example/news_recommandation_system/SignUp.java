package org.example.news_recommandation_system;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;

import javafx.scene.control.*;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.ResourceBundle;

public class SignUp {

    @FXML
    private Button btnBack;
    @FXML
    private Button btnContinue;
    @FXML
    private Button btnSignup;

    @FXML
    private TextField txtFName;
    @FXML
    private TextField txtLName;
    @FXML
    private TextField txtEmail;
    @FXML
    private TextField txtAge;
    @FXML
    private TextField txtUsername;
    @FXML
    private TextField txtPassword;
    @FXML
    private TextField txtCPassword;


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

    public void confirm(ActionEvent actionEvent){
        paneSuccess.toFront();
    }

}
