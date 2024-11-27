package org.example.news_recommandation_system;

import javafx.fxml.FXML;
import javafx.scene.control.Label;

public class ArticleBox {

    @FXML
    private Label labelCategory;

    @FXML
    private Label labelDate;

    @FXML
    private Label labelHeading;

    public void setArticleData(String heading, String date, String category) {
        labelHeading.setText(heading);
        labelDate.setText(date);
        labelCategory.setText(category);
    }

}
