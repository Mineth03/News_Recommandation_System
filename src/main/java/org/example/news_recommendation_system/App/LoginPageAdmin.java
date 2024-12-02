package org.example.news_recommendation_system.App;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import org.bson.Document;
import org.example.news_recommendation_system.Service.MainService;
import org.example.news_recommendation_system.Service.LogIn;
import org.example.news_recommendation_system.DataBase.MongoDBConnection;

import java.io.IOException;
import java.net.URL;
import java.util.Objects;
import java.util.ResourceBundle;

public class LoginPageAdmin implements Initializable {

    @FXML
    private Button btnBack;

    @FXML
    private Button btnLoginAdmin;

    @FXML
    private TextField txtAdminID;

    @FXML
    private TextField txtPasswordAdmin;

    @FXML
    private TextField txtAdminName;

    private MongoCollection<Document> adminDetailsCollection;
    private MongoCollection<Document> adminLoginDetailsCollection;
    private final LogIn logIn = new LogIn();

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        MongoDBConnection mongoDBConnection = new MongoDBConnection();
        MongoDatabase database = mongoDBConnection.getDatabase();
        adminDetailsCollection = database.getCollection("Admin");
        adminLoginDetailsCollection = database.getCollection("Admin_Login_Log");
    }

    @FXML
    private void handleBackButtonClick() throws IOException {
        Parent mainRoot = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("/org/example/news_recommendation_system/LogInPage.fxml")));
        Stage stage = (Stage) btnBack.getScene().getWindow();
        Scene scene = stage.getScene();
        scene.setRoot(mainRoot);
        stage.sizeToScene();
        Application.makeSceneDraggable(stage, (Pane) mainRoot);
    }

    @FXML
    private void handleLogInButtonClick() throws IOException {
        String name = txtAdminName.getText();
        String password = txtPasswordAdmin.getText();
        String id = txtAdminID.getText();

        if (logIn.usernamePasswordCheck(adminDetailsCollection, "adminID", id, "password", password)) {
            logIn.saveLoginDetails(adminLoginDetailsCollection, "adminId", id);
            MainService.showAlert(Alert.AlertType.INFORMATION, "Login", "Welcome " + name);
            AdminWindow.setCurrentAdminId(id);
            Parent mainRoot = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("/org/example/news_recommendation_system/AdminWindow.fxml")));
            Stage stage = (Stage) btnLoginAdmin.getScene().getWindow();
            Scene scene = stage.getScene();
            scene.setRoot(mainRoot);
            stage.sizeToScene();
            Application.makeSceneDraggable(stage, (Pane) mainRoot);
        } else {
            MainService.showAlert(Alert.AlertType.ERROR, "Login", "Incorrect name/ password or ID");
        }
    }

    @FXML
    public void exit(ActionEvent event) {
        MainService.showExitConfirmation(event);
    }
}
