package org.example.news_recommendation_system.Service;

import com.mongodb.client.MongoCollection;
import org.bson.Document;
import javafx.scene.control.Alert;
import com.mongodb.client.MongoCollection;
import org.bson.Document;
import org.example.news_recommendation_system.DataBase.MongoDBConnection;
import org.example.news_recommendation_system.Model.User;
import java.time.LocalDateTime;
import java.util.List;

public class LogIn {

    private final MongoDBConnection mongoDBConnection;

    public LogIn() {
        mongoDBConnection = new MongoDBConnection();
    }

    public boolean usernamePasswordCheck(MongoCollection<Document> collection, String identifierField, String identifierValue, String passwordField, String passwordValue) {
        try {
            Document user = collection.find(new Document(identifierField, identifierValue)
                    .append(passwordField, passwordValue)).first();
            return user != null;
        } catch (Exception e) {
            MainService.showAlert(Alert.AlertType.ERROR, "Login Error", "An error occurred while checking credentials.");
        }
        return false;
    }

    public void saveLoginDetails(MongoCollection<Document> collection, String identifierField, String identifierValue) {
        try {
            Document loginRecord = new Document(identifierField, identifierValue)
                    .append("Login_time", LocalDateTime.now().toString());
            collection.insertOne(loginRecord);
        } catch (Exception e) {
            MainService.showAlert(Alert.AlertType.ERROR, "Database Error", "Could not save login details.");
        }
    }

    public String capitalize(String name) {
        if (name == null || name.isEmpty()) return name;
        return name.substring(0, 1).toUpperCase() + name.substring(1).toLowerCase();
    }

    public boolean isDuplicateUser(String username, String email) {
        Document query = new Document("$or", List.of(
                new Document("username", username),
                new Document("email", email)
        ));
        return mongoDBConnection.findDocument("User", query) != null;
    }

    public boolean saveUser(User user, List<String> preferences) {
        try {
            Document userDoc = new Document("name", user.getName())
                    .append("email", user.getEmail())
                    .append("age", user.getAge())
                    .append("gender", user.getGender())
                    .append("Preferences", preferences)
                    .append("username", user.getUsername())
                    .append("password", user.getPassword());

            mongoDBConnection.insertDocument("User", userDoc);

            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public void saveLoginDetails(String username) {
        try {
            Document loginRecord = new Document("Username", username)
                    .append("Login_time", LocalDateTime.now().toString());
            mongoDBConnection.insertDocument("User_Login_Log", loginRecord);
        } catch (Exception e) {
            MainService.showAlert(Alert.AlertType.ERROR, "Database Error", "Could not save login details.");
        }
    }
}
