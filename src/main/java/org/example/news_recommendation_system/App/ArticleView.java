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
