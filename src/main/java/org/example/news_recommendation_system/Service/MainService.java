package org.example.news_recommendation_system.Service;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import org.bson.Document;
import org.example.news_recommendation_system.Model.Articles;
import org.example.news_recommendation_system.Model.LoginRecord;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static com.mongodb.client.model.Filters.eq;

public class MainService {

    private final MongoDatabase mongoDBConnection;


    public MainService(MongoDatabase mongoDBConnection) {
        this.mongoDBConnection = mongoDBConnection;
    }

    public void updateUserProfile(String username, String fullName, int age, String email, List<String> preferences) {
        MongoCollection<Document> collection = mongoDBConnection.getCollection("User");

        Document query = new Document("username", username);
        Document updatedData = new Document("name", fullName)
                .append("age", age)
                .append("email", email)
                .append("Preferences", preferences);

        Document updateOperation = new Document("$set", updatedData);
        collection.updateOne(query, updateOperation);
    }

    public static boolean validateProfileInputs(String firstName, String lastName, String email, String ageText) {
        if (firstName.isEmpty() || lastName.isEmpty() || email.isEmpty() || ageText.isEmpty()) {
            showAlert(Alert.AlertType.ERROR, null, "All fields are required.");
            return false;
        }
        try {
            int age = Integer.parseInt(ageText);
            if (age < 14 || age > 100) {
                showAlert(Alert.AlertType.ERROR, null, "Age should be between 14 and 100.");
                return false;
            }
        } catch (NumberFormatException e) {
            showAlert(Alert.AlertType.ERROR, null, "Age should be a valid number.");
            return false;
        }
        if (!email.matches("[^@]+@[^.]+\\..+")) {
            showAlert(Alert.AlertType.ERROR, null, "Invalid email format.");
            return false;
        }
        return true;
    }

    public static String capitalizeFirstLetter(String name) {
        if (name.isEmpty()) return "";
        return Character.toUpperCase(name.charAt(0)) + name.substring(1).toLowerCase();
    }

    public boolean verifyCurrentPassword(String username, String currentPassword) {
        MongoCollection<Document> collection = mongoDBConnection.getCollection("User");

        Document query = new Document("username", username);
        Document userDocument = collection.find(query).first();

        if (userDocument != null) {
            String storedPassword = userDocument.getString("password");
            return storedPassword != null && storedPassword.equals(currentPassword);
        }
        return false;
    }

    public boolean isUserExist(String username) {
        MongoCollection<Document> collection = mongoDBConnection.getCollection("User");
        Document query = new Document("username", username);
        return collection.find(query).first() != null;
    }

    public boolean updatePassword(String username, String newPassword) {
        MongoCollection<Document> collection = mongoDBConnection.getCollection("User");

        Document query = new Document("username", username);
        Document updateOperation = new Document("$set", new Document("password", newPassword));

        var result = collection.updateOne(query, updateOperation);
        return result.getModifiedCount() > 0;
    }

    public boolean validatePassword(String password) {
        if (password.length() < 5) {
            return false;
        }
        return password.matches(".*[a-zA-Z].*") && password.matches(".*\\d.*");
    }

    public ObservableList<LoginRecord> getLoginHistory(String username) {
        ObservableList<LoginRecord> loginHistory = FXCollections.observableArrayList();

        // Initialize the collection inside the method
        MongoCollection<Document> loginLogCollection = mongoDBConnection.getCollection("User_Login_Log");

        Document query = new Document("Username", username);
        for (Document logEntry : loginLogCollection.find(query)) {
            String loginDateTime = logEntry.getString("Login_time");

            LocalDateTime dateTime = LocalDateTime.parse(loginDateTime);
            String date = dateTime.toLocalDate().toString();
            String time = dateTime.toLocalTime().format(DateTimeFormatter.ofPattern("HH:mm:ss"));

            loginHistory.add(new LoginRecord(date, time));
        }

        loginHistory.sort((record1, record2) -> {
            LocalDateTime dateTime1 = LocalDateTime.parse(record1.getDate() + "T" + record1.getTime());
            LocalDateTime dateTime2 = LocalDateTime.parse(record2.getDate() + "T" + record2.getTime());
            return dateTime2.compareTo(dateTime1);
        });

        return loginHistory;
    }

    public ObservableList<Articles> loadArticles() {
        MongoCollection<Document> collection = mongoDBConnection.getCollection("Articles");
        ObservableList<Articles> articleList = FXCollections.observableArrayList();

        // Fetch all articles from the database
        for (Document doc : collection.find()) {
            String heading = doc.getString("heading");
            String category = doc.getString("category");
            String date = doc.getString("date");

            Articles article = new Articles(heading, category, date);
            articleList.add(article);
        }
        return articleList;
    }

    public ObservableList<Articles> filterArticles(List<String> selectedCategories) {
        MongoCollection<Document> collection = mongoDBConnection.getCollection("Articles");
        ObservableList<Articles> filteredArticles = FXCollections.observableArrayList();

        Document query = new Document();

        // Add category filter to query if categories are selected
        if (!selectedCategories.isEmpty()) {
            query.append("category", new Document("$in", selectedCategories));
        }

        // Fetch articles matching the query
        for (Document doc : collection.find(query)) {
            String heading = doc.getString("heading");
            String category = doc.getString("category");
            String date = doc.getString("date");

            Articles article = new Articles(heading, category, date);
            filteredArticles.add(article);
        }

        return filteredArticles;
    }

    public List<Articles> loadSavedArticles(String currentUsername) {
        MongoCollection<Document> interactionCollection = mongoDBConnection.getCollection("User-Article-Interaction");
        MongoCollection<Document> articlesCollection = mongoDBConnection.getCollection("Articles");

        List<Articles> savedArticles = new ArrayList<>();
        Document userInteraction = interactionCollection.find(eq("username", currentUsername)).first();

        if (userInteraction != null && userInteraction.containsKey("saved")) {
            List<String> savedHeadings = (List<String>) userInteraction.get("saved");

            for (String heading : savedHeadings) {
                Document articleDoc = articlesCollection.find(eq("heading", heading)).first();
                if (articleDoc != null) {
                    Articles article = new Articles(
                            articleDoc.getString("heading"),
                            articleDoc.getString("date"),
                            articleDoc.getString("category"),
                            articleDoc.getString("body"),
                            articleDoc.getString("url")
                    );
                    savedArticles.add(article);
                }
            }
        }
        return savedArticles;
    }

    public Articles getArticleByHeading(String heading) {
        try {
            MongoCollection<Document> articlesCollection = mongoDBConnection.getCollection("Articles");
            // Fetch article details based on the heading
            Document articleDoc = articlesCollection.find(eq("heading", heading)).first();

            if (articleDoc != null) {
                // Convert the MongoDB document to an Articles object
                return new Articles(
                        articleDoc.getString("heading"),
                        articleDoc.getString("date"),
                        articleDoc.getString("category"),
                        articleDoc.getString("article"),
                        articleDoc.getString("url")
                );
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static void showAlert(Alert.AlertType alertType, String title, String message) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
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
