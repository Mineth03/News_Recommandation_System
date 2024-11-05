package org.example.news_recommandation_system;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.layout.Pane;

public class AdminWindow {

    @FXML
    private Button btnProfile;
    @FXML
    private Button btnViewUserRecords;
    @FXML
    private Button btnAddNewR;
    @FXML
    private Button btnLogout;

    @FXML
    private Pane paneAddNew;
    @FXML
    private Pane paneViewUserRecords;
    @FXML
    private Pane paneProfile;

    @FXML
    public void buttonClicksConfig(ActionEvent actionEvent){
        if (actionEvent.getSource() == btnProfile){
            paneProfile.toFront();
        }
        if (actionEvent.getSource() == btnAddNewR){
            paneAddNew.toFront();
        }
        if (actionEvent.getSource() == btnViewUserRecords) {
            paneViewUserRecords.toFront();
        }
    }

}
