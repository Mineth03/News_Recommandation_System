package org.example.news_recommandation_system;

import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.stage.Modality;
import javafx.stage.Stage;
import org.bson.Document;

public class ArticleBox {

    @FXML
    private Label labelCategory;
    @FXML
    private Label labelDate;
    @FXML
    private Label labelHeading;

    @FXML
    private Button btnArticle;


    public void setArticleData(String heading, String date, String category) {
        labelHeading.setText(heading);
        labelDate.setText(date);
        labelCategory.setText(category);
    }

    @FXML
    private void handleArticleButtonClick() {
        try {
            // Connect to MongoDB
            var mongoClient = MongoClients.create("mongodb://localhost:27017");
            MongoDatabase database = mongoClient.getDatabase("News_Recommendation_System");
            MongoCollection<Document> collection = database.getCollection("Articles");

            // Query the database by heading
            String heading = labelHeading.getText();
            Document query = new Document("heading", heading);
            Document articleDoc = collection.find(query).first();

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
                FXMLLoader loader = new FXMLLoader(getClass().getResource("ArticleView.fxml"));
                Parent root = loader.load();

                ArticleView controller = loader.getController();
                controller.setArticleDetails(article);

                // Open the new scene in a modal window
                Stage stage = new Stage();
                stage.initModality(Modality.APPLICATION_MODAL);
                stage.setTitle("Article Details");
                stage.setScene(new Scene(root));
                stage.show();
            } else {
                System.out.println("Article not found in the database!");
            }

            mongoClient.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
