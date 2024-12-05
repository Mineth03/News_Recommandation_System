package org.example.news_recommendation_system.Service;

import com.mongodb.client.MongoCollection;
import org.bson.Document;
import org.example.news_recommendation_system.DataBase.MongoDBConnection;
import org.example.news_recommendation_system.Model.LoginRecord;
import org.example.news_recommendation_system.Model.User;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.List;

public class AdminService {

    private final MongoDBConnection mongoDBConnection;

    public AdminService(MongoDBConnection mongoDBConnection) {
        this.mongoDBConnection = mongoDBConnection;
    }

    public boolean updateAdminProfile(String adminID, String fullName, int age, String email) {
        try {
            MongoCollection<Document> collection = mongoDBConnection.getCollection("Admin");
            // Prepare the query and update operation
            Document query = new Document("adminID", adminID);
            Document updatedData = new Document("name", fullName)
                    .append("age", age)
                    .append("email", email);
            Document updateOperation = new Document("$set", updatedData);
            // Perform the update
            collection.updateOne(query, updateOperation);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public ObservableList<User> fetchAllUserDetails() {
        ObservableList<User> userDetails = FXCollections.observableArrayList();
        MongoCollection<Document> collection = mongoDBConnection.getCollection("User");

        for (Document document : collection.find()) {
            String name = document.getString("name");
            String email = document.getString("email");
            int age = document.getInteger("age", 0);
            String gender = document.getString("gender");
            List<String> preferences = (List<String>) document.get("Preferences");
            String username = document.getString("username");

            User user = new User(name, email, age, gender, "N/A", preferences, username); // Password is not used here
            userDetails.add(user);
        }

        return userDetails;
    }

    public Document fetchUserDetails(String username) {
        MongoCollection<Document> collection = mongoDBConnection.getCollection("User");
        return collection.find(new Document("username", username)).first();
    }

    public ObservableList<LoginRecord> fetchLoginHistory(String username) {
        MongoCollection<Document> loginLogCollection = mongoDBConnection.getCollection("User_Login_Log");
        ObservableList<LoginRecord> loginHistory = FXCollections.observableArrayList();

        for (Document logEntry : loginLogCollection.find(new Document("Username", username))) {
            String loginDateTime = logEntry.getString("Login_time");
            if (loginDateTime != null) {
                LocalDateTime dateTime = LocalDateTime.parse(loginDateTime);
                String date = dateTime.toLocalDate().toString();
                String time = dateTime.toLocalTime().format(DateTimeFormatter.ofPattern("HH:mm:ss"));

                loginHistory.add(new LoginRecord(date, time));
            }
        }

        // Sort by most recent login
        loginHistory.sort((record1, record2) -> {
            LocalDateTime dateTime1 = LocalDateTime.parse(record1.getDate() + "T" + record1.getTime());
            LocalDateTime dateTime2 = LocalDateTime.parse(record2.getDate() + "T" + record2.getTime());
            return dateTime2.compareTo(dateTime1);
        });

        return loginHistory;
    }

    public boolean removeUserByUsername(String username) {
        MongoCollection<Document> collection = mongoDBConnection.getCollection("User");
        Document query = new Document("username", username);

        long deletedCount = collection.deleteOne(query).getDeletedCount();
        return deletedCount > 0; // Return true if a user was deleted
    }

    public boolean addArticle(String heading, String body, String link, Date date, String category) {
        try {
            MongoCollection<Document> collection = mongoDBConnection.getCollection("Articles");

            // Prepare the article document
            Document doc = new Document("heading", heading)
                    .append("article", body)
                    .append("category", category)
                    .append("date", new SimpleDateFormat("MM/dd/yyyy").format(date))
                    .append("url", link);

            // Insert the article document into the database
            collection.insertOne(doc);
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}
