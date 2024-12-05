package org.example.news_recommendation_system.App;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.stage.Stage;
import org.example.news_recommendation_system.DataBase.MongoDBConnection;
import org.example.news_recommendation_system.Model.Articles;
import org.example.news_recommendation_system.Service.RecommendationEngine;
import java.awt.*;
import java.net.URI;

import static com.mongodb.client.model.Filters.eq;

public class ArticleViewController {

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
    private Button btnClose;

    private final RecommendationEngine recommendationEngine;

    private Articles article;

    public static void setCurrentUsername(String username) {
        currentUsername = username;
    }

    public ArticleViewController() {
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
                recommendationEngine.updatePointsForViewing(article.getCategory(), currentUsername);
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

    @FXML
    private void updateSaveList(ActionEvent event) {
        if (article != null) {
            recommendationEngine.updateSaveList(currentUsername, article);
        }
    }

    @FXML
    public void exit(ActionEvent event) {
        Stage stage = (Stage) btnClose.getScene().getWindow();
        stage.close();
    }
}
