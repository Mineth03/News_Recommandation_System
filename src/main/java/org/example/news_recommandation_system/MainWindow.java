package org.example.news_recommandation_system;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Optional;

public class MainWindow {

    @FXML
    private Button btnProfile;
    @FXML
    private Button btnSave;
    @FXML
    private Button btnSearch;
    @FXML
    private Button btnRecommended;
    @FXML
    private Button btnAbout;
    @FXML
    private Button btnLogout;


    @FXML
    private Pane paneAbout;
    @FXML
    private Pane paneSave;
    @FXML
    private Pane paneRecommend;
    @FXML
    private Pane paneProfile;
    @FXML
    private Pane paneSearch;

    @FXML
    public void buttonClicksConfig(ActionEvent actionEvent){
        if (actionEvent.getSource() == btnProfile){
            paneProfile.toFront();
        }
        if (actionEvent.getSource() == btnAbout){
            paneAbout.toFront();
        }
        if (actionEvent.getSource() == btnSave) {
            paneSave.toFront();
        }
        if (actionEvent.getSource() == btnRecommended) {
            paneRecommend.toFront();
        }
        if (actionEvent.getSource() == btnSearch) {
            paneSearch.toFront();
        }
    }

    @FXML
    private void handleLogoutButtonClick() throws IOException {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Log-Out");
        alert.setHeaderText("Are you sure you want to Log-Out?");
        Optional<ButtonType> output = alert.showAndWait();

        if (output.isPresent() && output.get() == ButtonType.OK) {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("LoginPage.fxml"));
            Parent signUpRoot = loader.load();

            Stage stage = (Stage) btnLogout.getScene().getWindow();
            stage.setScene(new Scene(signUpRoot));
            stage.show();
        }
    }

    @FXML
    public void exit(ActionEvent ignoredExit) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Exit");
        alert.setHeaderText("Are you sure you want to exit?");
        Optional<ButtonType> output = alert.showAndWait();

        if (output.isPresent() && output.get() == ButtonType.OK) {
            System.exit(0);

        }
    }

}
