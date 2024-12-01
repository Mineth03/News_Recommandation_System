package org.example.news_recommendation_system.App;

import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;
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
import org.bson.Document;
import org.example.news_recommendation_system.DataBase.MongoDBConnection;
import org.example.news_recommendation_system.Model.Articles;
import org.example.news_recommendation_system.Model.LoginRecord;
import org.example.news_recommendation_system.Model.User;
import org.example.news_recommendation_system.NewsFetcher.ArticleCategorizer;
import org.example.news_recommendation_system.Service.ExitAndAlerts;

import java.io.IOException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

public class AdminWindow {

    private static String currentAdminId;
    private final ObservableList<Articles> articleList = FXCollections.observableArrayList();


    @FXML
    private Button btnProfile;
    @FXML
    private Button btnManageUserRecords;
    @FXML
    private Button btnAddNewR;
    @FXML
    private Button btnDeleteR;
    @FXML
    private Button btnEditProfile;
    @FXML
    private Button btnBack1;
    @FXML
    private Button btnViewUser;
    @FXML
    private Button btnChangePassword;
    @FXML
    private Button btnPasswordConfirm;
    @FXML
    private Button btnSetPassword;
    @FXML
    private Button btnBack2;
    @FXML
    private Button btnBack3;
    @FXML
    private Button btnAdd;
    @FXML
    private Button btnLogout;

    @FXML
    private Label labelName;
    @FXML
    private Label labelAge;
    @FXML
    private Label labelEmail;
    @FXML
    private Label labelAdminId;
    @FXML
    private Label labelGender;
    @FXML
    private Label labelNameUser;
    @FXML
    private Label labelAgeUser;
    @FXML
    private Label labelEmailUser;
    @FXML
    private Label LabelPreferences;
    @FXML
    private Label labelGenderUser;

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
    private TextField txtHeading;
    @FXML
    private TextField txtLink;
    @FXML
    private TextArea txtArticleBody;

    @FXML
    private DatePicker datePicker;
    @FXML
    private DatePicker datePickerDelete;

    @FXML
    private Pane paneAddNew;
    @FXML
    private Pane paneManageUserRecords;
    @FXML
    private Pane paneProfile;
    @FXML
    private Pane paneDeleteR;
    @FXML
    private Pane paneEditAdminInfo;
    @FXML
    private Pane paneViewUserRecords;
    @FXML
    private Pane paneChangePassword;
    @FXML
    private Pane paneCheckPrevPassword;
    @FXML
    private Pane paneNewPassword;

    @FXML
    private TableView<LoginRecord> tableLoginDetails;
    @FXML
    private TableColumn<LoginRecord, String> tableColumnDate;
    @FXML
    private TableColumn<LoginRecord, String> tableColumnTime;

    @FXML
    private TableView<LoginRecord> tableLoginDetailsUser;
    @FXML
    private TableColumn<LoginRecord, String> tableColumnDateUser;
    @FXML
    private TableColumn<LoginRecord, String> tableColumnTimeUser;

    @FXML
    private TableView<Articles> tableRemoveArticles;
    @FXML
    private TableColumn<Articles, String> tableColumnRemoveCategory;
    @FXML
    private TableColumn<Articles, String> tableColumnRemoveDate;
    @FXML
    private TableColumn<Articles, String> tableColumnRemoveHeading;

    @FXML
    private TableView<User> tableUserDetails;
    @FXML
    private TableColumn<User, String> tableColumnAge;
    @FXML
    private TableColumn<User, String> tableColumnUsername;
    @FXML
    private TableColumn<User, String> tableColumnEmail;
    @FXML
    private TableColumn<User, String> tableColumnGender;
    @FXML
    private TableColumn<User, String> tableColumnName;
    @FXML
    private TableColumn<User, String> tableColumnPreferences;

    @FXML
    private CheckBox checkBoxTechnology, checkBoxEducation, checkBoxPolitics, checkBoxHealthcare, checkBoxEntertainment,
            checkBoxScience, checkBoxSports, checkBoxBusiness, checkBoxInvestigative, checkBoxLifestyle;

    private final MongoDBConnection mongoDBConnection;

    public static void setCurrentAdminId(String adminId) {
        currentAdminId = adminId;
    }

    public AdminWindow() {
        mongoDBConnection = new MongoDBConnection();
    }

    @FXML
    public void initialize() {
        tableColumnDate.setCellValueFactory(new PropertyValueFactory<>("date"));
        tableColumnTime.setCellValueFactory(new PropertyValueFactory<>("time"));
        tableColumnDateUser.setCellValueFactory(new PropertyValueFactory<>("date"));
        tableColumnTimeUser.setCellValueFactory(new PropertyValueFactory<>("time"));
    }


    @FXML
    public void buttonClicksConfig(ActionEvent actionEvent){
        if (actionEvent.getSource() == btnDeleteR){
            loadArticles();
            paneDeleteR.toFront();
        }
        if (actionEvent.getSource() == btnAddNewR){
            paneAddNew.toFront();
        }
        if (actionEvent.getSource() == btnProfile) {
            updateAdminDetails();
            paneProfile.toFront();
        }
        if (actionEvent.getSource() == btnEditProfile) {
            loadUserProfileForEdit();
            paneEditAdminInfo.toFront();
        }
        if (actionEvent.getSource() == btnBack1) {
            paneProfile.toFront();
        }
        if (actionEvent.getSource() == btnManageUserRecords) {
            loadAllUserDetails();
            paneManageUserRecords.toFront();
        }
        if (actionEvent.getSource() == btnViewUser) {
            updateUserDetails();
            paneViewUserRecords.toFront();
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
    private void updateAdminDetails() {
        if (currentAdminId == null || currentAdminId.isEmpty()) {
            System.out.println("No admin found for the current session.");
            return;
        }

        MongoCollection<Document> collection = mongoDBConnection.getCollection("Admin");

        Document query = new Document("adminID", currentAdminId);
        Document userDocument = collection.find(query).first();

        if (userDocument != null) {
            labelName.setText(userDocument.getString("name"));
            labelAge.setText(String.valueOf(userDocument.getInteger("age")));
            labelGender.setText(userDocument.getString("gender"));
            labelEmail.setText(userDocument.getString("email"));
            labelAdminId.setText(userDocument.getString("adminID"));
        } else {
            System.out.println("User not found in the database.");
        }

        displayLoginHistory();
    }

    private void displayLoginHistory() {
        ObservableList<LoginRecord> loginHistory = FXCollections.observableArrayList();

        MongoCollection<Document> loginLogCollection = mongoDBConnection.getCollection("Admin_Login_Log");
        Document query = new Document("adminId", currentAdminId);

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

        // Update table items
        tableLoginDetails.setItems(loginHistory);
    }

    private void loadUserProfileForEdit() {
        if (currentAdminId == null || currentAdminId.isEmpty()) {
            System.out.println("No username found for the current session.");
            return;
        }

        MongoCollection<Document> collection = mongoDBConnection.getCollection("Admin");

        Document query = new Document("adminID", currentAdminId);
        Document userDocument = collection.find(query).first();

        if (userDocument != null) {
            String fullName = userDocument.getString("name");
            if (fullName != null) {
                String[] nameParts = fullName.split(" ");
                txtFirstName.setText(nameParts.length > 0 ? nameParts[0] : "");
                txtLastName.setText(nameParts.length > 1 ? nameParts[1] : "");
            }

            txtEmail.setText(userDocument.getString("email"));
            txtAge.setText(String.valueOf(userDocument.getInteger("age", 0)));

        }
    }

    @FXML
    private void saveAdminProfile() {
        String firstName = txtFirstName.getText().trim();
        String lastName = txtLastName.getText().trim();
        String email = txtEmail.getText().trim();
        String ageText = txtAge.getText().trim();

        String fullName = capitalizeFirstLetter(firstName) + " " + capitalizeFirstLetter(lastName);

        if (!validateProfileInputs(firstName, lastName, email, ageText)) {
            return;
        }

        int age = Integer.parseInt(ageText);

        MongoCollection<Document> collection = mongoDBConnection.getCollection("Admin");

        Document query = new Document("adminID", currentAdminId);
        Document updatedData = new Document("name", fullName)
                .append("age", age)
                .append("email", email);

        Document updateOperation = new Document("$set", updatedData);
        collection.updateOne(query, updateOperation);

        Alert alert = new Alert(Alert.AlertType.INFORMATION, "Profile updated successfully.");
        alert.showAndWait();

        updateAdminDetails();
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

    private void showError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR, message);
        alert.showAndWait();
    }

    @FXML
    private void loadAllUserDetails() {
        ObservableList<User> userDetails = FXCollections.observableArrayList();

        MongoCollection<Document> collection = mongoDBConnection.getCollection("User");

        for (Document document : collection.find()) {
            String name = document.getString("name");
            String email = document.getString("email");
            int age = document.getInteger("age", 0);
            String gender = document.getString("gender");
            List<String> preferences = (List<String>) document.get("Preferences");
            String username = document.getString("username");

            User user = new User(name, email, age, gender, "N/A", preferences, username); // Password is not used here
            userDetails.add(user);
        }

        // Bind data to TableView
        tableColumnName.setCellValueFactory(new PropertyValueFactory<>("name"));
        tableColumnUsername.setCellValueFactory(new PropertyValueFactory<>("username")); // Assuming username is email
        tableColumnAge.setCellValueFactory(new PropertyValueFactory<>("age"));
        tableColumnGender.setCellValueFactory(new PropertyValueFactory<>("gender"));
        tableColumnEmail.setCellValueFactory(new PropertyValueFactory<>("email"));
        tableColumnPreferences.setCellValueFactory(new PropertyValueFactory<>("preferences"));

        tableUserDetails.setItems(userDetails);
    }

    @FXML
    private void removeSelectedUser() {
        // Get the selected user from the TableView
        User selectedUser = tableUserDetails.getSelectionModel().getSelectedItem();

        if (selectedUser == null) {
            showError("No user selected. Please select a user to remove.");
            return;
        }

        String username = selectedUser.getUsername();

        // Confirm deletion
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Remove User");
        alert.setHeaderText("Are you sure you want to remove this user?");
        alert.setContentText("User: " + selectedUser.getName());
        Optional<ButtonType> output = alert.showAndWait();

        if (output.isPresent() && output.get() == ButtonType.OK) {
            // Remove the user from the database
            MongoCollection<Document> collection = mongoDBConnection.getCollection("User");

            Document query = new Document("username", username);
            collection.deleteOne(query);

            // Remove the user from the TableView
            tableUserDetails.getItems().remove(selectedUser);

            Alert successAlert = new Alert(Alert.AlertType.INFORMATION, "User removed successfully.");
            successAlert.showAndWait();
        }
    }

    @FXML
    private void updateUserDetails() {
        // Get the selected user from the table
        User selectedUser = tableUserDetails.getSelectionModel().getSelectedItem();

        if (selectedUser == null) {
            showError("No user selected. Please select a user to view.");
            return;
        }

        String username = selectedUser.getUsername();

        MongoCollection<Document> collection = mongoDBConnection.getCollection("User");

        // Query to find the user document
        Document query = new Document("username", username);
        Document userDocument = collection.find(query).first();

        if (userDocument != null) {
            labelNameUser.setText(userDocument.getString("name"));
            labelAgeUser.setText(String.valueOf(userDocument.getInteger("age")));
            labelGenderUser.setText(userDocument.getString("gender"));
            labelEmailUser.setText(userDocument.getString("email"));

            // Handle preferences
            List<String> preferences = (List<String>) userDocument.get("Preferences");
            if (preferences != null && !preferences.isEmpty()) {
                LabelPreferences.setText(String.join(", ", preferences));
            } else {
                LabelPreferences.setText("No preferences available.");
            }
        } else {
            showError("User not found in the database.");
        }

        // Fetch and display user login history
        displayLoginHistoryUser();
    }



    @FXML
    private void displayLoginHistoryUser() {
        // Get the selected user from the user details table
        User selectedUser = tableUserDetails.getSelectionModel().getSelectedItem();

        if (selectedUser == null) {
            showError("No user selected. Please select a user to view login history.");
            return;
        }

        // Get the username from the selected user
        String username = selectedUser.getUsername();

        if (username == null || username.isEmpty()) {
            showError("Invalid username. Please try again.");
            return;
        }

        MongoCollection<Document> loginLogCollection = mongoDBConnection.getCollection("User_Login_Log");

        // Query to find login records for the user
        Document query = new Document("Username", username);

        ObservableList<LoginRecord> loginHistory = FXCollections.observableArrayList();

        for (Document logEntry : loginLogCollection.find(query)) {
            String loginDateTime = logEntry.getString("Login_time");
            if (loginDateTime != null) {
                LocalDateTime dateTime = LocalDateTime.parse(loginDateTime);
                String date = dateTime.toLocalDate().toString();
                String time = dateTime.toLocalTime().format(DateTimeFormatter.ofPattern("HH:mm:ss"));

                loginHistory.add(new LoginRecord(date, time));
            }
        }

        loginHistory.sort((record1, record2) -> {
            LocalDateTime dateTime1 = LocalDateTime.parse(record1.getDate() + "T" + record1.getTime());
            LocalDateTime dateTime2 = LocalDateTime.parse(record2.getDate() + "T" + record2.getTime());
            return dateTime2.compareTo(dateTime1);
        });

        // Update the TableView with the fetched login history
        tableLoginDetailsUser.setItems(loginHistory);
    }

    @FXML
    private boolean checkCurrentPassword() {
        String currentPassword = txtPrevPassword.getText().trim();

        if (currentPassword.isEmpty()) {
            showError("Current password is required.");
            return false;
        }

        MongoCollection<Document> collection = mongoDBConnection.getCollection("Admin");

        Document query = new Document("adminID", currentAdminId);
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

        MongoCollection<Document> collection = mongoDBConnection.getCollection("Admin");

        Document query = new Document("adminID", currentAdminId);
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

    @FXML
    public void addArticle(ActionEvent event) {
        String heading = txtHeading.getText();
        String body = txtArticleBody.getText();
        String link = txtLink.getText();
        LocalDate dateInput = datePicker.getValue();

        // Validation checks
        if (heading.isEmpty() || body.isEmpty() || link.isEmpty() || dateInput == null) {
            ExitAndAlerts.showAlert(Alert.AlertType.ERROR, "Validation Error", "All fields are required!");
            return;
        }

        try {
            new URL(link).toURI(); // Check if the link is a valid URL
        } catch (Exception e) {
            ExitAndAlerts.showAlert(Alert.AlertType.ERROR, "Validation Error", "Please provide a valid URL!");
            return;
        }

        try {
            // Convert LocalDate to Date
            Date date = java.sql.Date.valueOf(dateInput);

            // Instantiate ArticleCategorizer and categorize article
            ArticleCategorizer articleCategorizer = new ArticleCategorizer();
            String category = articleCategorizer.categorizeArticle(body);

            // Optional: Create the article object or insert directly into the database
            MongoDatabase database = MongoClients.create().getDatabase("News_Recommendation_System");
            MongoCollection<Document> collection = database.getCollection("Articles");

            Document doc = new Document("heading", heading)
                    .append("article", body)
                    .append("category", category)
                    .append("date", new SimpleDateFormat("MM/dd/yyyy").format(date))
                    .append("url", link);

            collection.insertOne(doc);

            ExitAndAlerts.showAlert(Alert.AlertType.INFORMATION, "Success", "Article added successfully!");
            txtHeading.clear();
            txtArticleBody.clear();
            txtLink.clear();
            datePicker.setValue(null);
        } catch (Exception ex) {
            ExitAndAlerts.showAlert(Alert.AlertType.ERROR, "Error", "An error occurred: " + ex.getMessage());
        }
    }

    @FXML
    private void loadArticles() {
        MongoCollection<Document> collection = mongoDBConnection.getCollection("Articles");

        // Clear existing data in the TableView
        articleList.clear();

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

        // Fetch all articles from the database
        for (Document doc : collection.find()) {
            String heading = doc.getString("heading");
            String category = doc.getString("category");
            String date = doc.getString("date");

            Articles article = new Articles(heading, category, date);
            articleList.add(article);
        }

        // Set up columns in the TableView
        tableColumnRemoveHeading.setCellValueFactory(new PropertyValueFactory<>("heading"));
        tableColumnRemoveCategory.setCellValueFactory(new PropertyValueFactory<>("category"));
        tableColumnRemoveDate.setCellValueFactory(new PropertyValueFactory<>("date"));

        // Bind data to the TableView
        tableRemoveArticles.setItems(articleList);
    }

    @FXML
    private void handleFilterAction(ActionEvent event) {
        // Initialize the query document
        Document query = new Document();

        // Collect selected categories
        List<String> selectedCategories = new ArrayList<>();
        if (checkBoxTechnology.isSelected()) selectedCategories.add("Technology");
        if (checkBoxEducation.isSelected()) selectedCategories.add("Education");
        if (checkBoxHealthcare.isSelected()) selectedCategories.add("Healthcare");
        if (checkBoxPolitics.isSelected()) selectedCategories.add("Politics");
        if (checkBoxScience.isSelected()) selectedCategories.add("Science");
        if (checkBoxEntertainment.isSelected()) selectedCategories.add("Entertainment");
        if (checkBoxBusiness.isSelected()) selectedCategories.add("Business");
        if (checkBoxSports.isSelected()) selectedCategories.add("Sports");
        if (checkBoxLifestyle.isSelected()) selectedCategories.add("Lifestyle");
        if (checkBoxInvestigative.isSelected()) selectedCategories.add("Investigative");
        // Add similar conditions for other checkboxes

        // Add category filter to query if categories are selected
        if (!selectedCategories.isEmpty()) {
            query.append("category", new Document("$in", selectedCategories));
        }

        // Get the selected date from the DatePicker
        LocalDate selectedDate = datePickerDelete.getValue();
        if (selectedDate != null) {
            // Format LocalDate to MM-DD-YYYY for MongoDB query
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM-dd-yyyy");
            String formattedDate = selectedDate.format(formatter);
            query.append("date", formattedDate); // Add date filter to query
        }

        // Ensure at least one filter is selected
        if (query.isEmpty()) {
            ExitAndAlerts.showAlert(Alert.AlertType.INFORMATION, "Delete Article", "Please select at least one category or a date.");
            return;
        }

        MongoCollection<Document> collection = mongoDBConnection.getCollection("Articles");

        // Clear existing data in the TableView
        articleList.clear();

        // Fetch articles matching the query
        for (Document doc : collection.find(query)) {
            String heading = doc.getString("heading");
            String category = doc.getString("category");
            String date = doc.getString("date");

            Articles article = new Articles(heading, category, date);
            articleList.add(article);
        }

        // Update the TableView
        tableRemoveArticles.setItems(articleList);

        // Show an alert if no articles match the selected filters
        if (articleList.isEmpty()) {
            ExitAndAlerts.showAlert(Alert.AlertType.INFORMATION, "Delete Article", "No articles found for the selected filters.");

        }
    }


    @FXML
    private void deleteArticle(ActionEvent event) {
        // Get the selected article from the TableView
        Articles selectedArticle = tableRemoveArticles.getSelectionModel().getSelectedItem();

        if (selectedArticle == null) {
            // Show an alert if no article is selected
            ExitAndAlerts.showAlert(Alert.AlertType.INFORMATION, "Delete Article", "Please select an article to delete.");
            return;
        }

        // Get the heading of the selected article to find it in the database
        String heading = selectedArticle.getHeading();

        MongoCollection<Document> collection = mongoDBConnection.getCollection("Articles");

        // Remove the article from the database by matching the heading (you can use other fields to refine the query)
        collection.deleteOne(Filters.eq("heading", heading));

        // Remove the article from the TableView
        articleList.remove(selectedArticle);

        // Show a confirmation message
        ExitAndAlerts.showAlert(Alert.AlertType.INFORMATION, "Delete Article", "Article deleted successfully.");

    }


    @FXML
    private void handleLogoutButtonClick() throws IOException {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Log-Out");
        alert.setHeaderText("Are you sure you want to Log-Out?");
        Optional<ButtonType> output = alert.showAndWait();

        if (output.isPresent() && output.get() == ButtonType.OK) {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/org/example/news_recommendation_system/LogInPage.fxml"));
            Parent signUpRoot = loader.load();

            Stage stage = (Stage) btnLogout.getScene().getWindow();
            stage.setScene(new Scene(signUpRoot));
            stage.show();
        }
    }

    @FXML
    public void exit(ActionEvent event) {
        ExitAndAlerts.showExitConfirmation(event);
    }
}