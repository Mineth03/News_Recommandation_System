package org.example.news_recommendation_system.App;

import com.mongodb.client.MongoDatabase;
import com.mongodb.client.MongoCollection;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.stage.Stage;
import org.bson.Document;
import org.example.news_recommendation_system.DataBase.MongoDBConnection;
import org.example.news_recommendation_system.Model.Articles;
import org.example.news_recommendation_system.Service.RecommendationEngine;


import java.awt.*;
import java.net.URI;
import java.util.ArrayList;

import static com.mongodb.client.model.Filters.eq;

public class ArticleView {

    private static String currentUsername;
    private MongoDBConnection mongoDBConnection;

    @FXML
    private Label labelHeading;
    @FXML
    private Label labelDate;
    @FXML
    private Label labelCategory;
    @FXML
    private Label labelContent;

    @FXML
    private Button btnFullArticle;
    @FXML
    private Button btnSave;
    @FXML
    private Button btnLike;
    @FXML
    private Button btnDislike;
    @FXML
    private Button btnClose;

    private final RecommendationEngine recommendationEngine;

    private Articles article;

    public static void setCurrentUsername(String username) {
        currentUsername = username;
    }

    public ArticleView() {
        mongoDBConnection = new MongoDBConnection();
        recommendationEngine = new RecommendationEngine(mongoDBConnection);
    }

    public void setArticleDetails(Articles article) {
        this.article = article;
        labelHeading.setText(article.getHeading());
        labelDate.setText(article.getDate());
        labelCategory.setText(article.getCategory());
        labelContent.setText(article.getBody());
    }

    @FXML
    private void handleFullArticleClick(ActionEvent event) {
        try {
            if (article != null && article.getUrl() != null && !article.getUrl().isEmpty()) {
                Desktop.getDesktop().browse(new URI(article.getUrl()));
                updatePointsForViewing(article.getCategory());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void setArticle(Articles article) {
        this.article = article;
    }

    @FXML
    private void handleLikeClick(ActionEvent event) {
        recommendationEngine.handleLikeClick(currentUsername, article);
    }

    @FXML
    private void handleDislikeClick(ActionEvent event) {
        recommendationEngine.handleDislikeClick(currentUsername, article);
    }

    // You can also call updatePointsForViewing if needed
    private void updatePointsForViewing(String category) {
        recommendationEngine.updatePointsForViewing(category, currentUsername);
    }

    @FXML
    private void updateSaveList(ActionEvent event) {
        MongoDatabase database = mongoDBConnection.getDatabase();
        MongoCollection<Document> userInteractionCollection = database.getCollection("User-Article-Interaction");

        // Check if the user's interaction document exists, if not create a new one
        Document userInteraction = userInteractionCollection.find(eq("username", currentUsername)).first();
        if (userInteraction == null) {
            // If no document exists for the current user, create a new one with empty lists
            Document newUserInteraction = new Document("username", currentUsername)
                    .append("liked", new ArrayList<>())
                    .append("disliked", new ArrayList<>())
                    .append("saved", new ArrayList<>());
            userInteractionCollection.insertOne(newUserInteraction);
            // After inserting, retrieve the newly created document
            userInteraction = newUserInteraction;
        }

        if (userInteraction == null || article == null) return;

        String articleHeading = article.getHeading();

        // Ensure that "saved" list exists in the document
        if (!userInteraction.containsKey("saved")) {
            userInteraction.put("saved", new ArrayList<String>());
        }

        ArrayList<String> savedArray = (ArrayList<String>) userInteraction.get("saved");

        // Check if the article has already been saved
        if (savedArray.contains(articleHeading)) {
            showAlert("Article Save", "This article has already been saved!", Alert.AlertType.INFORMATION);
            return;
        }

        // Add the article to the saved list
        savedArray.add(articleHeading);

        // Update the "saved" list in the user's interaction document
        userInteractionCollection.updateOne(
                eq("username", currentUsername),
                new Document("$set", new Document("saved", savedArray))
        );

        // Show success alert
        showAlert("Article Save", "Article saved successfully!", Alert.AlertType.INFORMATION);
    }


    private void showAlert(String title, String content, Alert.AlertType alertType) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }

    @FXML
    public void exit(ActionEvent event) {
        Stage stage = (Stage) btnClose.getScene().getWindow();
        stage.close();
    }
}
