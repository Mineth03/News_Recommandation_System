package org.example.news_recommendation_system.App;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import org.bson.Document;
import org.example.news_recommendation_system.Service.ExitAndAlerts;
import org.example.news_recommendation_system.DataBase.MongoDBConnection;

import java.io.IOException;
import java.net.URL;
import java.time.LocalDateTime;
import java.util.Objects;
import java.util.ResourceBundle;

public class LogInPage implements Initializable {

    @FXML
    private Button signUpButton;
    @FXML
    private Button btnLogin;
    @FXML
    private Button btnLoginAdmin;

    @FXML
    private TextField txtUsername;
    @FXML
    private PasswordField txtPass;

    private MongoCollection<Document> userDetailsCollection;
    private MongoCollection<Document> userLoginDetailsCollection;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        MongoDBConnection mongoDBConnection = new MongoDBConnection();
        MongoDatabase database = mongoDBConnection.getDatabase();
        userDetailsCollection = database.getCollection("User");
        userLoginDetailsCollection = database.getCollection("User_Login_Log");
    }

    @FXML
    private void handleSignUpButtonClick() throws IOException {
        Parent mainRoot = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("/org/example/news_recommendation_system/SignUp.fxml")));
        Stage stage = (Stage) signUpButton.getScene().getWindow();
        Scene scene = stage.getScene();
        scene.setRoot(mainRoot);
        stage.sizeToScene();
        Application.makeSceneDraggable(stage, (Pane) mainRoot);
    }

    @FXML
    private void handleLoginButtonClick() throws IOException {
        String username = txtUsername.getText();
        String password = txtPass.getText();

        if (usernamePasswordCheck(username, password)) {
            saveLoginDetails(username);
            ExitAndAlerts.showAlert(Alert.AlertType.INFORMATION, "Login", "Welcome " + username);
            MainWindow.setCurrentUsername(username);
            ArticleView.setCurrentUsername(username);
            Parent mainRoot = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("/org/example/news_recommendation_system/MainWindow.fxml")));
            Stage stage = (Stage) btnLogin.getScene().getWindow();
            Scene scene = stage.getScene();
            scene.setRoot(mainRoot);
            stage.sizeToScene();
            Application.makeSceneDraggable(stage, (Pane) mainRoot);
        } else {
            ExitAndAlerts.showAlert(Alert.AlertType.ERROR, "Login", "Incorrect username or password");
        }
    }

    private boolean usernamePasswordCheck(String username, String password) {
        try {
            Document user = userDetailsCollection.find(new Document("username", username)
                    .append("password", password)).first();
            return user != null;
        } catch (Exception e) {
            ExitAndAlerts.showAlert(Alert.AlertType.ERROR, "Login Error", "An error occurred while checking credentials.");
        }
        return false;
    }

    private void saveLoginDetails(String username) {
        try {
            Document loginRecord = new Document("Username", username)
                    .append("Login_time", LocalDateTime.now().toString());
            userLoginDetailsCollection.insertOne(loginRecord);
        } catch (Exception e) {
            ExitAndAlerts.showAlert(Alert.AlertType.ERROR, "Database Error", "Could not save login details.");
        }
    }

    @FXML
    private void handleLogAdminButtonClick() throws IOException {
        Parent mainRoot = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("/org/example/news_recommendation_system/LoginPageAdmin.fxml")));
        Stage stage = (Stage) btnLoginAdmin.getScene().getWindow();
        Scene scene = stage.getScene();
        scene.setRoot(mainRoot);
        stage.sizeToScene();
        Application.makeSceneDraggable(stage, (Pane) mainRoot);
    }

    @FXML
    public void exit(ActionEvent event) {
        ExitAndAlerts.showExitConfirmation(event);
    }

}