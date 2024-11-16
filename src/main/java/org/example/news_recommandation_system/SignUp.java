package org.example.news_recommandation_system;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import com.mongodb.client.*;
import com.mongodb.client.MongoClient;
import com.mongodb.ConnectionString;
import org.bson.Document;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class SignUp{

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
    private CheckBox checkBoxTechnology, checkBoxAI, checkBoxPolitics, checkBoxHealthcare, checkBoxEntertainment,
            checkBoxScience, checkBoxSports, checkBoxBusiness, checkBoxInvestigative, checkBoxLifestyle;


    @FXML
    private Pane paneRecords;

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
        if (checkBoxAI.isSelected()) preferences.add("AI");
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
        try (MongoClient mongoClient = MongoClients.create("mongodb://localhost:27017")) {
            MongoDatabase database = mongoClient.getDatabase("News_Recommendation_System");
            MongoCollection<Document> collection = database.getCollection("User");

            Document userDoc = new Document("name", newUser.getName())
                    .append("email", newUser.getEmail())
                    .append("age", newUser.getAge())
                    .append("gender", newUser.getGender())
                    .append("Preferences", newUser.getPreferences())
                    .append("username", newUser.getUsername())
                    .append("password", newUser.getPassword());

            collection.insertOne(userDoc);
            MainWindow.setCurrentUsername(username);

            FXMLLoader loader = new FXMLLoader(getClass().getResource("MainWindow.fxml"));
            Parent signUpRoot = loader.load();
            Stage stage = (Stage) btnSignup.getScene().getWindow();
            stage.setScene(new Scene(signUpRoot));
            stage.show();
        } catch (Exception e) {
            System.out.println("Error connecting to MongoDB: " + e.getMessage());
        }
    }


    private boolean validateInputs() {
        String errorMessage = "";

        if (txtFName.getText().isEmpty() || txtLName.getText().isEmpty() || txtEmail.getText().isEmpty() ||
                txtAge.getText().isEmpty() || txtUsername.getText().isEmpty() || txtPassword.getText().isEmpty() ||
                txtCPassword.getText().isEmpty()) {
            errorMessage += "✕All fields are mandatory.\n";
        }

        try {
            int age = Integer.parseInt(txtAge.getText());
            if (age < 14 || age > 100) {
                errorMessage += "✕Age should be between 14 and 100.\n";
            }
        } catch (NumberFormatException e) {
            errorMessage += "✕Age should be a valid number.\n";
        }

        if (!txtEmail.getText().matches("[^@]+@[^.]+\\..+")) {
            errorMessage += "✕Invalid email format.\n";
        }

        int selectedPreferences = 0;
        if (checkBoxTechnology.isSelected()) selectedPreferences++;
        if (checkBoxAI.isSelected()) selectedPreferences++;
        if (checkBoxPolitics.isSelected()) selectedPreferences++;
        if (checkBoxHealthcare.isSelected()) selectedPreferences++;
        if (checkBoxEntertainment.isSelected()) selectedPreferences++;
        if (checkBoxScience.isSelected()) selectedPreferences++;
        if (checkBoxSports.isSelected()) selectedPreferences++;
        if (checkBoxBusiness.isSelected()) selectedPreferences++;
        if (checkBoxInvestigative.isSelected()) selectedPreferences++;
        if (checkBoxLifestyle.isSelected()) selectedPreferences++;

        if (selectedPreferences < 2) {
            errorMessage += "✕Please select at least two preferences.\n";
        }

        // Validate password (letters and numbers, min length of 5)
        String password = txtPassword.getText();
        if (password.length() < 5 || !password.matches("^(?=.*[a-zA-Z])(?=.*\\d).+$")) {
            errorMessage += "✕Password must be at least 5 characters long\n and contain both letters and numbers.\n";
        }

        String confirmPassword = txtCPassword.getText();
        if (!password.equals(confirmPassword)) {
            Alert alert = new Alert(Alert.AlertType.ERROR, "✕Password and Confirm Password do not match.");
            alert.showAndWait();
            return false;
        }

        if (!errorMessage.isEmpty()) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Validation Error");
            alert.setHeaderText("Please correct the following errors:");
            alert.setContentText(errorMessage);
            alert.showAndWait();
            return false;
        }

        return true;
    }

    private boolean handleDuplicates(String username, String email) {
        try (MongoClient mongoClient = MongoClients.create("mongodb://localhost:27017")) {
            MongoDatabase database = mongoClient.getDatabase("News_Recommendation_System");
            MongoCollection<Document> collection = database.getCollection("User");

            Document existingUser = collection.find(
                    new Document("$or", List.of(
                            new Document("username", username),
                            new Document("email", email)
                    ))
            ).first();

            return existingUser != null;
        } catch (Exception e) {
            System.out.println("Error connecting to MongoDB: " + e.getMessage());
            return true;
        }
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
        FXMLLoader loader = new FXMLLoader(getClass().getResource("LogInPage.fxml"));
        Parent signUpRoot = loader.load();

        Stage stage = (Stage) btnBack.getScene().getWindow();
        stage.setScene(new Scene(signUpRoot));
        stage.show();
    }

    @FXML
    public void exit(ActionEvent event) {
        Exit.showExitConfirmation(event);
    }

}
