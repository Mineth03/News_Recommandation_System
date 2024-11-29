package org.example.news_recommandation_system;

import com.mongodb.client.*;
import com.mongodb.client.model.Filters;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import com.mongodb.*;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

import org.bson.Document;

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
    private Button btnPasswordConfirm;
    @FXML
    private Button btnSetPassword;


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
    private Pane paneChangePassword;
    @FXML
    private Pane paneCheckPrevPassword;
    @FXML
    private Pane paneNewPassword;
    @FXML
    private ScrollPane paneScroll;
    @FXML
    private GridPane paneGrid;

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
    private PasswordField txtPrevPassword;
    @FXML
    private TextField txtNewPassword;
    @FXML
    private TextField txtNewPasswordConfirm;

    @FXML
    private TableView<LoginRecord> tableLoginDetails;
    @FXML
    private TableColumn<LoginRecord, String> tableColumnDate;
    @FXML
    private TableColumn<LoginRecord, String> tableColumnTime;

    @FXML
    private CheckBox checkBoxTechnology, checkBoxEducation, checkBoxPolitics, checkBoxHealthcare, checkBoxEntertainment,
            checkBoxScience, checkBoxSports, checkBoxBusiness, checkBoxInvestigative, checkBoxLifestyle;


    public static void setCurrentUsername(String username) {
        currentUsername = username;
    }


    @FXML
    public void buttonClicksConfig(ActionEvent actionEvent){
        if (actionEvent.getSource() == btnProfile){
            updateUserDetails();
            paneProfile.toFront();
            paneCheckPrevPassword.toFront();
        }
        if (actionEvent.getSource() == btnAbout){
            paneAbout.toFront();
            paneCheckPrevPassword.toFront();
        }
        if (actionEvent.getSource() == btnSave) {
            paneSave.toFront();
            paneCheckPrevPassword.toFront();
        }
        if (actionEvent.getSource() == btnRecommended) {
            paneRecommend.toFront();
            paneCheckPrevPassword.toFront();
        }
        if (actionEvent.getSource() == btnSearch) {
            paneSearch.toFront();
            paneCheckPrevPassword.toFront();
        }
        if (actionEvent.getSource() == btnHome) {
            paneHome.toFront();
            paneCheckPrevPassword.toFront();
        }
        if (actionEvent.getSource() == btnEditProfile) {
            loadUserProfileForEdit();
            paneEditUserInfo.toFront();
        }
        if (actionEvent.getSource() == btnBack1) {
            paneProfile.toFront();
        }
        if (actionEvent.getSource() == btnChangePassword) {
            paneChangePassword.toFront();
        }
        if (actionEvent.getSource() == btnPasswordConfirm) {
            if (!checkCurrentPassword()){
                return;
            }
            paneNewPassword.toFront();
        }
        if (actionEvent.getSource() == btnSetPassword) {
            updatePassword();
        }
    }

    @FXML
    public void initialize() {
        tableColumnDate.setCellValueFactory(new PropertyValueFactory<>("date"));
        tableColumnTime.setCellValueFactory(new PropertyValueFactory<>("time"));
        loadAndDisplayArticles(currentUsername);
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
        checkBoxEducation.setSelected(preferences.contains("Education"));
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
        if (checkBoxEducation.isSelected()) preferences.add("Education");
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
    private boolean checkCurrentPassword() {
        String currentPassword = txtPrevPassword.getText().trim();

        if (currentPassword.isEmpty()) {
            showError("Current password is required.");
            return false;
        }

        MongoDatabase database = getDatabase();
        if (database == null) return false;

        MongoCollection<Document> collection = database.getCollection("User");

        Document query = new Document("username", currentUsername);
        Document userDocument = collection.find(query).first();

        if (userDocument != null) {
            String storedPassword = userDocument.getString("password");
            if (storedPassword != null && storedPassword.equals(currentPassword)) {
                paneNewPassword.toFront();
                return true;
            } else {
                showError("Incorrect current password.");
                return false;
            }
        } else {
            showError("User not found in the database.");
            return false;
        }
    }

    @FXML
    private void updatePassword() {
        String newPassword = txtNewPassword.getText().trim();
        String confirmPassword = txtNewPasswordConfirm.getText().trim();

        if (!validatePassword(newPassword)) {
            return;
        }

        if (!newPassword.equals(confirmPassword)) {
            showError("Passwords do not match.");
            return;
        }

        MongoDatabase database = getDatabase();
        if (database == null) return;

        MongoCollection<Document> collection = database.getCollection("User");

        Document query = new Document("username", currentUsername);
        Document updateOperation = new Document("$set", new Document("password", newPassword));

        collection.updateOne(query, updateOperation);

        Alert alert = new Alert(Alert.AlertType.INFORMATION, "Password updated successfully.");
        alert.showAndWait();

        paneProfile.toFront();
        paneCheckPrevPassword.toFront();
    }

    private boolean validatePassword(String password) {
        if (password.length() < 5) {
            showError("Password must be at least 5 characters long.");
            return false;
        }
        if (!password.matches(".*[a-zA-Z].*") || !password.matches(".*\\d.*")) {
            showError("Password must contain both letters and numbers.");
            return false;
        }
        return true;
    }

    public List<Articles> fetchArticlesBasedOnPoints(String username) {
        List<Articles> articles = new ArrayList<>();
        try {
            // Connect to MongoDB
            MongoDatabase database = getDatabase();
            MongoCollection<Document> articlesCollection = database.getCollection("Articles");
            MongoCollection<Document> userPointsCollection = database.getCollection("User_Preferences");

            // Retrieve the user's points document
            Document userPointsDoc = userPointsCollection.find(Filters.eq("username", username)).first();
            if (userPointsDoc == null) {
                System.out.println("No points found for user: " + username);
                return articles; // Return an empty list if no points are found
            }

            // Calculate the total points across all categories
            Map<String, Integer> categoryPoints = new HashMap<>();
            int totalPoints = 0;

            for (String key : userPointsDoc.keySet()) {
                if (!key.equals("_id") && !key.equals("username")) {
                    int points = userPointsDoc.getInteger(key, 0);
                    categoryPoints.put(key, points);
                    totalPoints += points;
                }
            }

            // If total points are zero, return empty list
            if (totalPoints == 0) {
                System.out.println("No points assigned to any category for user: " + username);
                return articles;
            }

            // Calculate the proportional distribution of articles per category
            Map<String, Integer> categoryArticleQuota = new HashMap<>();
            int totalQuota = 20; // Number of articles to display
            for (Map.Entry<String, Integer> entry : categoryPoints.entrySet()) {
                int quota = Math.round((float) entry.getValue() / totalPoints * totalQuota);
                categoryArticleQuota.put(entry.getKey(), quota);
            }

            // Retrieve and shuffle articles for each category
            for (Map.Entry<String, Integer> entry : categoryArticleQuota.entrySet()) {
                String category = entry.getKey();
                int quota = entry.getValue();

                if (quota > 0) {
                    List<Document> categoryArticles = articlesCollection.find(Filters.eq("category", category))
                            .into(new ArrayList<>());

                    // Shuffle and limit the articles to the quota
                    Collections.shuffle(categoryArticles);
                    for (int i = 0; i < Math.min(quota, categoryArticles.size()); i++) {
                        Document doc = categoryArticles.get(i);
                        String heading = doc.getString("heading");
                        String date = doc.getString("date");
                        Articles article = new Articles(heading, date, category, categoryPoints.get(category));
                        articles.add(article);
                    }
                }
            }

            // Shuffle the final list to mix categories
            Collections.shuffle(articles);

            return articles;

        } catch (Exception e) {
            e.printStackTrace();
        }
        return articles;
    }


    public void loadAndDisplayArticles(String username) {
        List<Articles> articles = fetchArticlesBasedOnPoints(username); // Fetch articles based on user points
        displayArticles(articles); // Populate the GridPane
    }

    public void displayArticles(List<Articles> articles) {
        paneGrid.getChildren().clear(); // Clear existing articles

        int columns = 5; // Set the number of articles per row
        int row = 0, col = 1;

        try {
            for (Articles article : articles) {
                // Load the article pane
                FXMLLoader loader = new FXMLLoader(getClass().getResource("ArticleBox.fxml"));
                AnchorPane articlePane = loader.load();

                // Set the article data using the controller
                ArticleBox controller = loader.getController();
                controller.setArticleData(article.getHeading(), article.getDate(), article.getCategory());

                // Add the article pane to the GridPane at the correct column and row
                paneGrid.add(articlePane, col, row);

                // Move to the next column
                col++;

                // If 5 articles are added in a row, move to the next row
                if (col == columns) {
                    col = 1; // Reset column to 1
                    row++;   // Increment the row
                }

                // Optionally add margins for better spacing
                GridPane.setMargin(articlePane, new Insets(40));
            }
        } catch (IOException e) {
            e.printStackTrace();
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
    public void exit(ActionEvent event) {
        Exit.showExitConfirmation(event);
    }
}
