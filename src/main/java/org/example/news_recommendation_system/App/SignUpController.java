package org.example.news_recommendation_system.App;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import org.example.news_recommendation_system.DataBase.MongoDBConnection;
import org.example.news_recommendation_system.Model.User;
import org.example.news_recommendation_system.Service.MainService;
import org.example.news_recommendation_system.Service.LogIn;
import org.example.news_recommendation_system.Service.RecommendationEngine;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class SignUpController {

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
    private PasswordField txtCPassword;
    @FXML
    private CheckBox checkBoxTechnology, checkBoxEducation, checkBoxPolitics, checkBoxHealthcare, checkBoxEntertainment,
            checkBoxScience, checkBoxSports, checkBoxBusiness, checkBoxInvestigative, checkBoxLifestyle;

    private final LogIn logIn;
    private final RecommendationEngine recommendationEngine;
    private final MongoDBConnection mongoDBConnection;

    public SignUpController() {
        logIn = new LogIn();
        mongoDBConnection = new MongoDBConnection();
        recommendationEngine = new RecommendationEngine(mongoDBConnection);
    }

    @FXML
    private void handleSignupButtonClick() {
        if (!validateInputs()) {
            return;
        }

        String firstName = logIn.capitalize(txtFName.getText());
        String lastName = logIn.capitalize(txtLName.getText());
        String name = firstName + " " + lastName;
        String email = txtEmail.getText();
        int age = Integer.parseInt(txtAge.getText());
        String gender = ChoiceBoxGender.getValue();
        String username = txtUsername.getText();
        String password = txtPassword.getText();

        if (logIn.isDuplicateUser(username, email)) {
            Alert alert = new Alert(Alert.AlertType.ERROR, "Username or Email already exists. Please choose a different one.");
            alert.showAndWait();
            return;
        }

        List<String> preferences = getSelectedPreferences();

        // Create a User object with the collected data
        User newUser = new User(name, email, age, gender, password, preferences, username);

        // Save user and preferences
        if (logIn.saveUser(newUser, preferences)) {
            logIn.saveLoginDetails(username);

            MainWindowController.setCurrentUsername(username);
            ArticleViewController.setCurrentUsername(username);
            saveUserPreferences(username);
            // Redirect to MainWindow
            navigateToMainPage(username);
        } else {
            MainService.showAlert(Alert.AlertType.ERROR, "Signup Error", "Could not complete signup.");
        }
    }

    private List<String> getSelectedPreferences() {
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
        return preferences;
    }

    private void saveUserPreferences(String username) {
        // Determine initial scores based on checkbox selections
        boolean checkBoxTechnologySelected = checkBoxTechnology.isSelected();
        boolean checkBoxEducationSelected = checkBoxEducation.isSelected();
        boolean checkBoxPoliticsSelected = checkBoxPolitics.isSelected();
        boolean checkBoxHealthcareSelected = checkBoxHealthcare.isSelected();
        boolean checkBoxEntertainmentSelected = checkBoxEntertainment.isSelected();
        boolean checkBoxScienceSelected = checkBoxScience.isSelected();
        boolean checkBoxSportsSelected = checkBoxSports.isSelected();
        boolean checkBoxBusinessSelected = checkBoxBusiness.isSelected();
        boolean checkBoxInvestigativeSelected = checkBoxInvestigative.isSelected();
        boolean checkBoxLifestyleSelected = checkBoxLifestyle.isSelected();

        // Use the RecommendationEngine to create and save the preferences document
        recommendationEngine.saveUserPreferences(
                username,
                checkBoxTechnologySelected,
                checkBoxEducationSelected,
                checkBoxPoliticsSelected,
                checkBoxHealthcareSelected,
                checkBoxEntertainmentSelected,
                checkBoxScienceSelected,
                checkBoxSportsSelected,
                checkBoxBusinessSelected,
                checkBoxInvestigativeSelected,
                checkBoxLifestyleSelected
        );
    }

    @FXML
    private void navigateToMainPage(String username) {
        try {
            MainWindowController.setCurrentUsername(username);
            ArticleViewController.setCurrentUsername(username);
            Parent mainRoot = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("/org/example/news_recommendation_system/MainWindow.fxml")));
            Stage stage = (Stage) btnSignup.getScene().getWindow();
            Scene scene = stage.getScene();
            scene.setRoot(mainRoot);
            stage.sizeToScene();
            Application.makeSceneDraggable(stage, (Pane) mainRoot);
        } catch (IOException e) {
            MainService.showAlert(Alert.AlertType.ERROR, "Navigation Error", "Unable to load MainWindow.");
        }
    }

    private boolean validateInputs() {
        StringBuilder errorMsg = new StringBuilder();

        // Validate first name and last name (no numbers allowed)
        if (!txtFName.getText().matches("[a-zA-Z]+")) {
            errorMsg.append("- First name must only contain letters.\n");
        }

        if (!txtLName.getText().matches("[a-zA-Z]+")) {
            errorMsg.append("- Last name must only contain letters.\n");
        }

        // Validate age (must be between 12 and 100)
        try {
            int age = Integer.parseInt(txtAge.getText());
            if (age < 12 || age > 100) {
                errorMsg.append("- Age must be between 12 and 100.\n");
            }
        } catch (NumberFormatException e) {
            errorMsg.append("- Age must be a valid number.\n");
        }

        // Validate email (basic pattern match)
        if (!txtEmail.getText().matches("^[\\w.%+-]+@[\\w.-]+\\.[a-zA-Z]{2,}$")) {
            errorMsg.append("- Email is not valid.\n");
        }

        // Validate that at least 2 checkboxes are selected
        int selectedCount = 0;
        if (checkBoxTechnology.isSelected()) selectedCount++;
        if (checkBoxEducation.isSelected()) selectedCount++;
        if (checkBoxPolitics.isSelected()) selectedCount++;
        if (checkBoxHealthcare.isSelected()) selectedCount++;
        if (checkBoxEntertainment.isSelected()) selectedCount++;
        if (checkBoxScience.isSelected()) selectedCount++;
        if (checkBoxSports.isSelected()) selectedCount++;
        if (checkBoxBusiness.isSelected()) selectedCount++;
        if (checkBoxInvestigative.isSelected()) selectedCount++;
        if (checkBoxLifestyle.isSelected()) selectedCount++;

        if (selectedCount < 2) {
            errorMsg.append("- Please select at least two preferences.\n");
        }

        // Validate password (minimum 6 characters, contains letters and numbers)
        String password = txtPassword.getText();
        if (password.length() < 6 || !password.matches(".*[a-zA-Z].*") || !password.matches(".*\\d.*")) {
            errorMsg.append("- Password must be at least 6 characters long and contain both letters and numbers.\n");
        }

        // Validate confirm password matches password
        String passwordC = txtCPassword.getText();
        if (!password.equals(passwordC)){
            errorMsg.append("- Passwords do not match.\n");
        }

        // If there are errors, show all in one alert
        if (!errorMsg.isEmpty()) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Validation Errors");
            alert.setHeaderText("Please correct the following errors:");
            alert.setContentText(errorMsg.toString());
            alert.showAndWait();
            return false;
        }

        return true;
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
    public void exit(ActionEvent event) {
        MainService.showExitConfirmation(event);
    }
}
