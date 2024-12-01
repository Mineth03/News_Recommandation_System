package org.example.news_recommendation_system.App;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import org.bson.Document;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import org.example.news_recommendation_system.Model.User;
import org.example.news_recommendation_system.Service.ExitAndAlerts;
import org.example.news_recommendation_system.DataBase.MongoDBConnection;
import org.example.news_recommendation_system.Service.RecommendationEngine;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class SignUp {

    @FXML
    private ChoiceBox<String> ChoiceBoxGender;
    @FXML
    private Button btnBack;
    @FXML
    private Button btnSignup;
    @FXML
    private TextField txtFName;
    @FXML
    private TextField txtLName;
    @FXML
    private TextField txtEmail;
    @FXML
    private TextField txtAge;
    @FXML
    private TextField txtUsername;
    @FXML
    private PasswordField txtPassword;
    @FXML
    private CheckBox checkBoxTechnology, checkBoxEducation, checkBoxPolitics, checkBoxHealthcare, checkBoxEntertainment,
            checkBoxScience, checkBoxSports, checkBoxBusiness, checkBoxInvestigative, checkBoxLifestyle;

    private final MongoDBConnection mongoDBConnection;

    private final RecommendationEngine recommendationEngine;

    public SignUp() {
        mongoDBConnection = new MongoDBConnection();
        recommendationEngine = new RecommendationEngine(mongoDBConnection);
    }

    @FXML
    private void handleSignupButtonClick() {
        if (!validateInputs()) {
            return;
        }

        String firstName = capitalize(txtFName.getText());
        String lastName = capitalize(txtLName.getText());
        String name = firstName + " " + lastName;
        String email = txtEmail.getText();
        int age = Integer.parseInt(txtAge.getText());
        String gender = ChoiceBoxGender.getValue();
        String username = txtUsername.getText();
        String password = txtPassword.getText();

        if (handleDuplicates(username, email)) {
            Alert alert = new Alert(Alert.AlertType.ERROR, "Username or Email already exists. Please choose a different one.");
            alert.showAndWait();
            return;
        }

        List<String> preferences = new ArrayList<>();
        if (checkBoxTechnology.isSelected()) preferences.add("Technology");
        if (checkBoxEducation.isSelected()) preferences.add("Education");
        if (checkBoxPolitics.isSelected()) preferences.add("Politics");
        if (checkBoxHealthcare.isSelected()) preferences.add("Healthcare");
        if (checkBoxEntertainment.isSelected()) preferences.add("Entertainment");
        if (checkBoxScience.isSelected()) preferences.add("Science");
        if (checkBoxSports.isSelected()) preferences.add("Sports");
        if (checkBoxBusiness.isSelected()) preferences.add("Business");
        if (checkBoxInvestigative.isSelected()) preferences.add("Investigative");
        if (checkBoxLifestyle.isSelected()) preferences.add("Lifestyle");

        // Create a User object with the collected data
        User newUser = new User(name, email, age, gender, password, preferences, username);

        // Insert data into MongoDB
        try {
            Document userDoc = new Document("name", newUser.getName())
                    .append("email", newUser.getEmail())
                    .append("age", newUser.getAge())
                    .append("gender", newUser.getGender())
                    .append("Preferences", newUser.getPreferences())
                    .append("username", newUser.getUsername())
                    .append("password", newUser.getPassword());

            mongoDBConnection.insertDocument("User", userDoc);

            recommendationEngine.saveUserPreferences(username, checkBoxTechnology.isSelected(), checkBoxEducation.isSelected(),
                    checkBoxPolitics.isSelected(), checkBoxHealthcare.isSelected(),
                    checkBoxEntertainment.isSelected(), checkBoxScience.isSelected(),
                    checkBoxSports.isSelected(), checkBoxBusiness.isSelected(),
                    checkBoxInvestigative.isSelected(), checkBoxLifestyle.isSelected());


            // Set the current user in MainWindow
            MainWindow.setCurrentUsername(username);
            ArticleView.setCurrentUsername(username);
            saveLoginDetails(username);

            // Redirect to MainWindow
            Parent mainRoot = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("/org/example/news_recommendation_system/MainWindow.fxml")));
            Stage stage = (Stage) btnSignup.getScene().getWindow();
            Scene scene = stage.getScene();
            scene.setRoot(mainRoot);
            stage.sizeToScene();
            Application.makeSceneDraggable(stage, (Pane) mainRoot);

        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    private void saveLoginDetails(String username) {
        try {
            Document loginRecord = new Document("Username", username)
                    .append("Login_time", LocalDateTime.now().toString());
            mongoDBConnection.insertDocument("User_Login_Log", loginRecord);
        } catch (Exception e) {
            ExitAndAlerts.showAlert(Alert.AlertType.ERROR, "Database Error", "Could not save login details.");
        }
    }

    private boolean validateInputs() {
        // Your validation logic here
        return true;
    }

    private boolean handleDuplicates(String username, String email) {
        Document query = new Document("$or", List.of(
                new Document("username", username),
                new Document("email", email)
        ));

        Document existingUser = mongoDBConnection.findDocument("User", query);
        return existingUser != null;
    }

    private String capitalize(String name) {
        if (name == null || name.isEmpty()) return name;
        return name.substring(0, 1).toUpperCase() + name.substring(1).toLowerCase();
    }

    public void initialize() {
        ChoiceBoxGender.setValue("Gender");
        ChoiceBoxGender.getSelectionModel().selectedIndexProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal.intValue() > 0 && ChoiceBoxGender.getItems().getFirst().equals("Gender")) {
                ChoiceBoxGender.getItems().remove("Gender");
            }
        });
    }

    @FXML
    private void handleBackButtonClick() throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/org/example/news_recommendation_system/LogInPage.fxml"));
        Parent signUpRoot = loader.load();

        Stage stage = (Stage) btnBack.getScene().getWindow();
        stage.setScene(new Scene(signUpRoot));
        stage.show();
    }

    @FXML
    public void exit(ActionEvent event) {
        ExitAndAlerts.showExitConfirmation(event);
    }
}
