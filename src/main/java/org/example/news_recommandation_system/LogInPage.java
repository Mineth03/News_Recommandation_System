package org.example.news_recommandation_system;

import com.mongodb.client.*;
import com.mongodb.ConnectionString;
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




    private MongoClient mongoClient;
    private MongoDatabase database;
    private MongoCollection<Document> userDetailsCollection;
    private MongoCollection<Document> userLoginDetailsCollection;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        try {
            mongoClient = MongoClients.create("mongodb://localhost:27017");
            database = mongoClient.getDatabase("News_Recommendation_System");
            userDetailsCollection = database.getCollection("User");
            userLoginDetailsCollection = database.getCollection("User_Login_Log");
        } catch (Exception e) {
            AlertHelper.showAlert(Alert.AlertType.ERROR, "Database Connection Error", "Could not connect to MongoDB.");
        }
    }


    @FXML
    private void handleSignUpButtonClick() throws IOException {
        Parent mainRoot = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("SignUp.fxml")));
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

        if (checkCredentials(username, password)) {
            saveLoginDetails(username);
            AlertHelper.showAlert(Alert.AlertType.INFORMATION, "Login", "Welcome " + username);
            Parent mainRoot = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("MainWindow.fxml")));
            Stage stage = (Stage) btnLogin.getScene().getWindow();
            Scene scene = stage.getScene();
            scene.setRoot(mainRoot);
            stage.sizeToScene();
            Application.makeSceneDraggable(stage, (Pane) mainRoot);
        } else {
            AlertHelper.showAlert(Alert.AlertType.ERROR, "Login", "Incorrect username or password");
        }
    }

    private boolean checkCredentials(String username, String password) {
        try {
            Document user = userDetailsCollection.find(new Document("username", username)
                    .append("password", password)).first();
            return user != null;
        } catch (Exception e) {
            AlertHelper.showAlert(Alert.AlertType.ERROR, "Login Error", "An error occurred while checking credentials.");
        }
        return false;
    }

    private void saveLoginDetails(String username) {
        try {
            Document loginRecord = new Document("Username", username)
                    .append("Login_time", LocalDateTime.now().toString());
            userLoginDetailsCollection.insertOne(loginRecord);
        } catch (Exception e) {
            AlertHelper.showAlert(Alert.AlertType.ERROR, "Database Error", "Could not save login details.");
        }
    }

    @FXML
    private void handleLogAdminButtonClick() throws IOException {
        Parent mainRoot = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("LogInPageAdmin.fxml")));
        Stage stage = (Stage) btnLoginAdmin.getScene().getWindow();
        Scene scene = stage.getScene();
        scene.setRoot(mainRoot);
        stage.sizeToScene();
        Application.makeSceneDraggable(stage, (Pane) mainRoot);
    }

}