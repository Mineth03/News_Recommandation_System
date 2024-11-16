package org.example.news_recommandation_system;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import org.bson.Document;

import java.io.IOException;
import java.net.URL;
import java.time.LocalDateTime;
import java.util.Objects;
import java.util.ResourceBundle;

public class LoginPageAdmin implements Initializable {

    @FXML
    private Button btnBack;

    @FXML
    private Button btnLoginAdmin;


    @FXML
    private TextField txtAdminID;

    @FXML
    private TextField txtPasswordAdmin;

    @FXML
    private TextField txtAdminName;

    private MongoClient mongoClient;
    private MongoDatabase database;
    private MongoCollection<Document> adminDetailsCollection;
    private MongoCollection<Document> adminLoginDetailsCollection;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        try {
            mongoClient = MongoClients.create("mongodb://localhost:27017");
            database = mongoClient.getDatabase("News_Recommendation_System");
            adminDetailsCollection = database.getCollection("Admin");
            adminLoginDetailsCollection = database.getCollection("Admin_Login_Log");
        } catch (Exception e) {
            AlertHelper.showAlert(Alert.AlertType.ERROR, "Database Connection Error", "Could not connect to MongoDB.");
        }
    }

    @FXML
    private void handleBackButtonClick() throws IOException {
        Parent mainRoot = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("LogInPage.fxml")));
        Stage stage = (Stage) btnBack.getScene().getWindow();
        Scene scene = stage.getScene();
        scene.setRoot(mainRoot);
        stage.sizeToScene();
        Application.makeSceneDraggable(stage, (Pane) mainRoot);
    }

    @FXML
    private void handleLogInButtonClick() throws IOException {
        String name = txtAdminName.getText();
        String password = txtPasswordAdmin.getText();
        String id = txtAdminID.getText();

        if (checkCredentials(name, password, id)) {
            saveLoginDetails(id);
            AlertHelper.showAlert(Alert.AlertType.INFORMATION, "Login", "Welcome " + name);
            AdminWindow.setCurrentAdminId(id);
            Parent mainRoot = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("AdminWindow.fxml")));
            Stage stage = (Stage) btnLoginAdmin.getScene().getWindow();
            Scene scene = stage.getScene();
            scene.setRoot(mainRoot);
            stage.sizeToScene();
            Application.makeSceneDraggable(stage, (Pane) mainRoot);
        } else {
            AlertHelper.showAlert(Alert.AlertType.ERROR, "Login", "Incorrect name/ password or ID");
        }
    }

    private boolean checkCredentials(String name, String password, String id) {
        try {
            Document user = adminDetailsCollection.find(new Document("name", name)
                    .append("password", password).append("adminID", id)).first();
            return user != null;
        } catch (Exception e) {
            AlertHelper.showAlert(Alert.AlertType.ERROR, "Login Error", "An error occurred while checking credentials.");
        }
        return false;
    }

    private void saveLoginDetails(String id) {
        try {
            Document loginRecord = new Document("adminId", id)
                    .append("Login_time", LocalDateTime.now().toString());
            adminLoginDetailsCollection.insertOne(loginRecord);
        } catch (Exception e) {
            AlertHelper.showAlert(Alert.AlertType.ERROR, "Database Error", "Could not save login details.");
        }
    }

    @FXML
    public void exit(ActionEvent event) {
        Exit.showExitConfirmation(event);
    }

}
