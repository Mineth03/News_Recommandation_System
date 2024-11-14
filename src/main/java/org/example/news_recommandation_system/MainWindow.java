package org.example.news_recommandation_system;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import com.mongodb.*;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Optional;
import org.bson.Document;
import java.util.List;

public class MainWindow {

    private static String currentUsername;


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
    private Button btnHome;
    @FXML
    private Button btnLogout;
    @FXML
    private Button btnEditProfile;
    @FXML
    private Button btnChangePassword;
    @FXML
    private Button btnBack1;
    @FXML
    private Button btnConfirm;


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
    private Pane paneHome;
    @FXML
    private Pane paneEditUserInfo;

    @FXML
    private Label labelName;
    @FXML
    private Label labelAge;
    @FXML
    private Label labelEmail;
    @FXML
    private Label LabelPreferences;
    @FXML
    private Label labelGender;

    @FXML
    private TextField txtFirstName;
    @FXML
    private TextField txtLastName;
    @FXML
    private TextField txtEmail;
    @FXML
    private TextField txtAge;


    @FXML
    private TableView<LoginRecord> tableLoginDetails;
    @FXML
    private TableColumn<LoginRecord, String> tableColumnDate;
    @FXML
    private TableColumn<LoginRecord, String> tableColumnTime;

    @FXML
    private CheckBox checkBoxTechnology, checkBoxAI, checkBoxPolitics, checkBoxHealthcare, checkBoxEntertainment,
            checkBoxScience, checkBoxSports, checkBoxBusiness, checkBoxInvestigative, checkBoxLifestyle;


    public static void setCurrentUsername(String username) {
        currentUsername = username;
    }


    @FXML
    public void buttonClicksConfig(ActionEvent actionEvent){
        if (actionEvent.getSource() == btnProfile){
            updateUserDetails();
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
        if (actionEvent.getSource() == btnHome) {
            paneHome.toFront();
        }
        if (actionEvent.getSource() == btnEditProfile) {
            loadUserProfileForEdit();
            paneEditUserInfo.toFront();
        }
        if (actionEvent.getSource() == btnBack1) {
            paneProfile.toFront();
        }
    }

    @FXML
    public void initialize() {
        tableColumnDate.setCellValueFactory(new PropertyValueFactory<>("date"));
        tableColumnTime.setCellValueFactory(new PropertyValueFactory<>("time"));
    }

    private MongoDatabase getDatabase() {
        try {
            MongoClient mongoClient = MongoClients.create("mongodb://localhost:27017");
            return mongoClient.getDatabase("News_Recommendation_System");
        } catch (Exception e) {
            System.err.println("Failed to connect to MongoDB: " + e.getMessage());
            return null;
        }
    }

    @FXML
    private void updateUserDetails() {
        if (currentUsername == null || currentUsername.isEmpty()) {
            System.out.println("No username found for the current session.");
            return;
        }

        MongoDatabase database = getDatabase();
        if (database == null) return;

        MongoCollection<Document> collection = database.getCollection("User");

        Document query = new Document("username", currentUsername);
        Document userDocument = collection.find(query).first();

        if (userDocument != null) {
            labelName.setText(userDocument.getString("name"));
            labelAge.setText(String.valueOf(userDocument.getInteger("age")));
            labelGender.setText(userDocument.getString("gender"));
            labelEmail.setText(userDocument.getString("email"));

            List<String> preferences = (List<String>) userDocument.get("Preferences");
            if (preferences != null) {
                LabelPreferences.setText(String.join(", ", preferences));
            }
        } else {
            System.out.println("User not found in the database.");
        }

        displayLoginHistory();
    }

    private void displayLoginHistory() {
        ObservableList<LoginRecord> loginHistory = FXCollections.observableArrayList();

        MongoDatabase database = getDatabase();
        if (database == null) return;

        MongoCollection<Document> loginLogCollection = database.getCollection("User_Login_Log");

        Document query = new Document("Username", currentUsername);
        for (Document logEntry : loginLogCollection.find(query)) {
            String loginDateTime = logEntry.getString("Login_time");

            LocalDateTime dateTime = LocalDateTime.parse(loginDateTime);
            String date = dateTime.toLocalDate().toString();
            String time = dateTime.toLocalTime().format(DateTimeFormatter.ofPattern("HH:mm:ss"));

            loginHistory.add(new LoginRecord(date, time));
        }

        loginHistory.sort((record1, record2) -> {
            LocalDateTime dateTime1 = LocalDateTime.parse(record1.getDate() + "T" + record1.getTime());
            LocalDateTime dateTime2 = LocalDateTime.parse(record2.getDate() + "T" + record2.getTime());
            return dateTime2.compareTo(dateTime1);
        });

        tableLoginDetails.setItems(loginHistory);
    }

    private void loadUserProfileForEdit() {
        if (currentUsername == null || currentUsername.isEmpty()) {
            System.out.println("No username found for the current session.");
            return;
        }

        MongoDatabase database = getDatabase();
        if (database == null) return;

        MongoCollection<Document> collection = database.getCollection("User");

        Document query = new Document("username", currentUsername);
        Document userDocument = collection.find(query).first();

        if (userDocument != null) {
            // Split the full name into first and last names if possible
            String fullName = userDocument.getString("name");
            if (fullName != null) {
                String[] nameParts = fullName.split(" ");
                txtFirstName.setText(nameParts.length > 0 ? nameParts[0] : "");
                txtLastName.setText(nameParts.length > 1 ? nameParts[1] : "");
            }

            txtEmail.setText(userDocument.getString("email"));
            txtAge.setText(String.valueOf(userDocument.getInteger("age", 0)));

            List<String> preferences = (List<String>) userDocument.get("Preferences");
            if (preferences != null) {
                setPreferencesCheckboxes(preferences);
            }
        } else {
            System.out.println("User not found in the database.");
        }
    }

    // Helper method to set the checkboxes based on user preferences
    private void setPreferencesCheckboxes(List<String> preferences) {
        checkBoxTechnology.setSelected(preferences.contains("Technology"));
        checkBoxAI.setSelected(preferences.contains("AI"));
        checkBoxPolitics.setSelected(preferences.contains("Politics"));
        checkBoxBusiness.setSelected(preferences.contains("Business"));
        checkBoxEntertainment.setSelected(preferences.contains("Entertainment"));
        checkBoxLifestyle.setSelected(preferences.contains("Lifestyle"));
        checkBoxScience.setSelected(preferences.contains("Science"));
        checkBoxSports.setSelected(preferences.contains("Sports"));
        checkBoxInvestigative.setSelected(preferences.contains("Investigative"));
        checkBoxHealthcare.setSelected(preferences.contains("Healthcare"));
    }

    @FXML
    private void saveUserProfile() {
        String firstName = txtFirstName.getText().trim();
        String lastName = txtLastName.getText().trim();
        String email = txtEmail.getText().trim();
        String ageText = txtAge.getText().trim();

        // Concatenate names and capitalize
        String fullName = capitalizeFirstLetter(firstName) + " " + capitalizeFirstLetter(lastName);

        // Validate inputs
        if (!validateProfileInputs(firstName, lastName, email, ageText)) {
            return;
        }

        int age = Integer.parseInt(ageText);

        // Collect preferences
        List<String> selectedPreferences = getSelectedPreferences();

        // Connect to MongoDB and update the user information
        MongoDatabase database = getDatabase();
        if (database == null) return;

        MongoCollection<Document> collection = database.getCollection("User");

        Document query = new Document("username", currentUsername);
        Document updatedData = new Document("name", fullName)
                .append("age", age)
                .append("email", email)
                .append("Preferences", selectedPreferences);

        Document updateOperation = new Document("$set", updatedData);
        collection.updateOne(query, updateOperation);

        Alert alert = new Alert(Alert.AlertType.INFORMATION, "Profile updated successfully.");
        alert.showAndWait();

        updateUserDetails();
        paneProfile.toFront();
    }

    private boolean validateProfileInputs(String firstName, String lastName, String email, String ageText) {
        if (firstName.isEmpty() || lastName.isEmpty() || email.isEmpty() || ageText.isEmpty()) {
            showError("All fields are required.");
            return false;
        }
        try {
            int age = Integer.parseInt(ageText);
            if (age < 14 || age > 100) {
                showError("Age should be between 14 and 100.");
                return false;
            }
        } catch (NumberFormatException e) {
            showError("Age should be a valid number.");
            return false;
        }
        if (!txtEmail.getText().matches("[^@]+@[^.]+\\..+")) {
            showError("Invalid email format.");
        }
        return true;
    }

    private String capitalizeFirstLetter(String name) {
        if (name.isEmpty()) return "";
        return Character.toUpperCase(name.charAt(0)) + name.substring(1).toLowerCase();
    }

    private List<String> getSelectedPreferences() {
        List<String> preferences = new ArrayList<>();
        if (checkBoxTechnology.isSelected()) preferences.add("Technology");
        if (checkBoxAI.isSelected()) preferences.add("AI");
        if (checkBoxPolitics.isSelected()) preferences.add("Politics");
        if (checkBoxBusiness.isSelected()) preferences.add("Business");
        if (checkBoxEntertainment.isSelected()) preferences.add("Entertainment");
        if (checkBoxLifestyle.isSelected()) preferences.add("Lifestyle");
        if (checkBoxScience.isSelected()) preferences.add("Science");
        if (checkBoxSports.isSelected()) preferences.add("Sports");
        if (checkBoxInvestigative.isSelected()) preferences.add("Investigative");
        if (checkBoxHealthcare.isSelected()) preferences.add("Healthcare");
        return preferences;
    }

    private void showError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR, message);
        alert.showAndWait();
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
    public void exit(ActionEvent event) {
        Exit.showExitConfirmation(event);
    }

}
