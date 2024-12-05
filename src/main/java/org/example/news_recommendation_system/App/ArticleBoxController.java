package org.example.news_recommendation_system.App;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import org.bson.Document;
import org.example.news_recommendation_system.DataBase.MongoDBConnection;
import org.example.news_recommendation_system.Model.Articles;

public class ArticleBoxController {

    @FXML
    private Label labelCategory;
    @FXML
    private Label labelDate;
    @FXML
    private Label labelHeading;

    private final MongoDBConnection mongoDBConnection;

    public ArticleBoxController() {
        mongoDBConnection = new MongoDBConnection();
    }

    public void setArticleData(String heading, String date, String category) {
        labelHeading.setText(heading);
        labelDate.setText(date);
        labelCategory.setText(category);
    }

    @FXML
    private void handleArticleButtonClick() {
        try {
            String heading = labelHeading.getText();
            Document query = new Document("heading", heading);
            Document articleDoc = mongoDBConnection.findDocument("Articles", query);

            if (articleDoc != null) {
                // Create an Articles object with retrieved data
                Articles article = new Articles(
                        articleDoc.getString("heading"),
                        articleDoc.getString("date"),
                        articleDoc.getString("category"),
                        articleDoc.getString("article")
                );
                article.setUrl(articleDoc.getString("url"));

                // Load the ArticleView.fxml and pass the data
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/org/example/news_recommendation_system/ArticleView.fxml"));
                Parent root = loader.load();

                ArticleViewController controller = loader.getController();
                controller.setArticleDetails(article);

                // Open the new scene in a modal window
                Stage stage = new Stage();
                stage.initModality(Modality.APPLICATION_MODAL);
                stage.setScene(new Scene(root));
                stage.initStyle(StageStyle.UNDECORATED);
                stage.show();
            } else {
                System.out.println("Article not found in the database!");
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
