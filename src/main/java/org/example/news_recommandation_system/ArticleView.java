package org.example.news_recommandation_system;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.stage.Stage;

import java.awt.*;
import java.net.URI;

public class ArticleView {

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
    private Button btnClose;

    private Articles article; // Store the article object

    public void setArticleDetails(Articles article) {
        this.article = article;
        labelHeading.setText(article.getHeading());
        labelDate.setText(article.getDate());
        labelCategory.setText(article.getCategory());
        labelContent.setText(article.getBody());
    }

    @FXML
    private void handleFullArticleClick(ActionEvent event) {
        if (article != null && article.getUrl() != null && !article.getUrl().isEmpty()) {
            try {
                Desktop.getDesktop().browse(new URI(article.getUrl())); // Open the URL in the default browser
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @FXML
    public void exit(ActionEvent event) {
        Stage stage = (Stage) btnClose.getScene().getWindow();
        stage.close();
    }
}
