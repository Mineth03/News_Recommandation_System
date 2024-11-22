package org.example.news_recommandation_system;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;

public class ArticleView {

    @FXML
    private Button btnClose;

    @FXML
    private Label labelContent;

    @FXML
    private Label labelDate;

    @FXML
    private Label labelHeading;

    @FXML
    void exit(ActionEvent event) {
        Exit.showExitConfirmation(event);
    }

}
