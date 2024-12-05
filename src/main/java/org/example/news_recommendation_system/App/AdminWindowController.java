package org.example.news_recommendation_system.App;

import com.mongodb.client.MongoCollection;
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
import org.example.news_recommendation_system.Service.AdminService;
import org.example.news_recommendation_system.Service.ArticleCategorizer;
import org.example.news_recommendation_system.Service.MainService;

import java.io.IOException;
import java.net.URL;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class AdminWindowController {

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
    private Button btnBack3;
    @FXML
    private Button btnBack4;
    @FXML
    private Button btnLogout;
    @FXML
    private Button btnHome;
    @FXML
    private Button btnAddH;
    @FXML
    private Button btnRemoveH;

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
    private Pane paneHome;

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
    private MainService mainService;
    private AdminService adminService;
    public static void setCurrentAdminId(String adminId) {
        currentAdminId = adminId;
    }

    public AdminWindowController() {
        mongoDBConnection = new MongoDBConnection();
    }

    @FXML
    public void initialize() {
        mainService = new MainService(mongoDBConnection.getDatabase());
        adminService = new AdminService(mongoDBConnection);

        loadAllUserDetails();

        tableColumnDate.setCellValueFactory(new PropertyValueFactory<>("date"));
        tableColumnTime.setCellValueFactory(new PropertyValueFactory<>("time"));

        tableColumnDateUser.setCellValueFactory(new PropertyValueFactory<>("date"));
        tableColumnTimeUser.setCellValueFactory(new PropertyValueFactory<>("time"));

        tableColumnName.setCellValueFactory(new PropertyValueFactory<>("name"));
        tableColumnUsername.setCellValueFactory(new PropertyValueFactory<>("username"));
        tableColumnAge.setCellValueFactory(new PropertyValueFactory<>("age"));
        tableColumnGender.setCellValueFactory(new PropertyValueFactory<>("gender"));
        tableColumnEmail.setCellValueFactory(new PropertyValueFactory<>("email"));
        tableColumnPreferences.setCellValueFactory(new PropertyValueFactory<>("preferences"));
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
        if (actionEvent.getSource() == btnHome){
            paneHome.toFront();
        }
        if (actionEvent.getSource() == btnAddH){
            paneAddNew.toFront();
        }
        if (actionEvent.getSource() == btnRemoveH) {
            loadArticles();
            paneDeleteR.toFront();
        }
        if (actionEvent.getSource() == btnEditProfile) {
            loadUserProfileForEdit();
            paneEditAdminInfo.toFront();
        }
        if (actionEvent.getSource() == btnBack1) {
            paneProfile.toFront();
        }
        if (actionEvent.getSource() == btnBack3) {
            paneProfile.toFront();
        }
        if (actionEvent.getSource() == btnBack4) {
            paneCheckPrevPassword.toFront();
            txtNewPassword.clear();
            txtNewPasswordConfirm.clear();
            txtPrevPassword.clear();
        }
        if (actionEvent.getSource() == btnManageUserRecords) {
            paneManageUserRecords.toFront();
        }
        if (actionEvent.getSource() == btnViewUser) {
            updateUserDetails();
        }
        if (actionEvent.getSource() == btnChangePassword) {
            txtNewPassword.clear();
            txtNewPasswordConfirm.clear();
            txtPrevPassword.clear();
            paneChangePassword.toFront();
            paneCheckPrevPassword.toFront();
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
        // Generate full name with capitalized first letters
        String fullName = MainService.capitalizeFirstLetter(firstName) + " " + MainService.capitalizeFirstLetter(lastName);
        // Validate inputs
        if (MainService.validateProfileInputs(firstName, lastName, email, ageText)) {
            return;
        }
        int age = Integer.parseInt(ageText);
        // Update admin profile using AdminService
        boolean isUpdated = adminService.updateAdminProfile(currentAdminId, fullName, age, email);
        if (isUpdated) {
            MainService.showAlert(Alert.AlertType.INFORMATION, null, "Profile updated successfully.");
            updateAdminDetails();
            paneProfile.toFront();
        } else {
            MainService.showAlert(Alert.AlertType.ERROR, "Error", "Failed to update the profile. Please try again.");
        }
    }


    @FXML
    private void loadAllUserDetails() {
        ObservableList<User> userDetails = adminService.fetchAllUserDetails();
        tableUserDetails.setItems(userDetails);
    }

    @FXML
    private void removeSelectedUser() {
        User selectedUser = tableUserDetails.getSelectionModel().getSelectedItem();
        if (selectedUser == null) {
            MainService.showAlert(Alert.AlertType.ERROR, null, "No user selected. Please select a user to remove.");
            return;
        }
        String username = selectedUser.getUsername();

        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Remove User");
        alert.setHeaderText("Are you sure you want to remove this user?");
        alert.setContentText("User: " + selectedUser.getName());
        Optional<ButtonType> output = alert.showAndWait();

        if (output.isPresent() && output.get() == ButtonType.OK) {
            // Call service method to remove the user
            boolean isDeleted = adminService.removeUserByUsername(username);
            if (isDeleted) {
                tableUserDetails.getItems().remove(selectedUser);
                MainService.showAlert(Alert.AlertType.INFORMATION, null, "User removed successfully.");
            } else {
                MainService.showAlert(Alert.AlertType.ERROR, null, "Failed to remove the user. Please try again.");
            }
        }
    }

    @FXML
    private void updateUserDetails() {
        // Get the selected user from the table
        User selectedUser = tableUserDetails.getSelectionModel().getSelectedItem();
        if (selectedUser == null) {
            MainService.showAlert(Alert.AlertType.ERROR, null, "No user selected. Please select a user to view.");
            return;
        }
        String username = selectedUser.getUsername();
        // Fetch user details from the service
        Document userDetails = adminService.fetchUserDetails(username);
        if (userDetails != null) {
            labelNameUser.setText(userDetails.getString("name"));
            labelAgeUser.setText(String.valueOf(userDetails.getInteger("age")));
            labelGenderUser.setText(userDetails.getString("gender"));
            labelEmailUser.setText(userDetails.getString("email"));

            // Handle preferences
            List<String> preferences = (List<String>) userDetails.get("Preferences");
            if (preferences != null && !preferences.isEmpty()) {
                LabelPreferences.setText(String.join(", ", preferences));
            } else {
                LabelPreferences.setText("No preferences available.");
            }
        } else {
            MainService.showAlert(Alert.AlertType.ERROR, null, "User not found in the database.");
        }
        // Fetch and display user login history
        displayLoginHistoryUser(username);
        paneViewUserRecords.toFront();
    }

    @FXML
    private void displayLoginHistoryUser(String username) {
        if (username == null || username.isEmpty()) {
            MainService.showAlert(Alert.AlertType.ERROR, null, "Invalid username. Please try again.");
            return;
        }
        // Fetch login history from the service
        ObservableList<LoginRecord> loginHistory = adminService.fetchLoginHistory(username);
        if (loginHistory.isEmpty()) {
            MainService.showAlert(Alert.AlertType.ERROR, null, "No login history found for this user.");
        }
        // Update the TableView with the fetched login history
        tableLoginDetailsUser.setItems(loginHistory);
    }

    @FXML
    private boolean checkCurrentPassword() {
        String currentPassword = txtPrevPassword.getText().trim();

        if (currentPassword.isEmpty()) {
            MainService.showAlert(Alert.AlertType.ERROR, null, "Current password is required.");
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
                MainService.showAlert(Alert.AlertType.ERROR, null, "Incorrect current password.");
                return false;
            }
        } else {
            MainService.showAlert(Alert.AlertType.ERROR, null, "User not found in the database.");
            return false;
        }
    }

    @FXML
    private void updatePassword() {
        String newPassword = txtNewPassword.getText().trim();
        String confirmPassword = txtNewPasswordConfirm.getText().trim();

        if (mainService.validatePassword(newPassword)) {
            return;
        }

        if (!newPassword.equals(confirmPassword)) {
            MainService.showAlert(Alert.AlertType.ERROR, null, "Passwords do not match.");
            return;
        }

        MongoCollection<Document> collection = mongoDBConnection.getCollection("Admin");

        Document query = new Document("adminID", currentAdminId);
        Document updateOperation = new Document("$set", new Document("password", newPassword));

        collection.updateOne(query, updateOperation);

        MainService.showAlert(Alert.AlertType.INFORMATION, null, "Password updated successfully.");

        paneProfile.toFront();
        paneCheckPrevPassword.toFront();
    }

    @FXML
    public void addArticle(ActionEvent event) {
        String heading = txtHeading.getText();
        String body = txtArticleBody.getText();
        String link = txtLink.getText();
        LocalDate dateInput = datePicker.getValue();

        // Validation checks
        if (heading.isEmpty() || body.isEmpty() || link.isEmpty() || dateInput == null) {
            MainService.showAlert(Alert.AlertType.ERROR, "Validation Error", "All fields are required!");
            return;
        }
        try {
            new URL(link).toURI(); // Validate URL
        } catch (Exception e) {
            MainService.showAlert(Alert.AlertType.ERROR, "Validation Error", "Please provide a valid URL!");
            return;
        }
        try {
            Date date = java.sql.Date.valueOf(dateInput);
            ArticleCategorizer articleCategorizer = new ArticleCategorizer();
            String category = articleCategorizer.categorizeArticle(body);

            boolean isAdded = adminService.addArticle(heading, body, link, date, category);

            if (isAdded) {
                MainService.showAlert(Alert.AlertType.INFORMATION, "Success", "Article added successfully!");
                txtHeading.clear();
                txtArticleBody.clear();
                txtLink.clear();
                datePicker.setValue(null);
            } else {
                MainService.showAlert(Alert.AlertType.ERROR, "Error", "Failed to add the article. Please try again.");
            }
        } catch (Exception ex) {
            MainService.showAlert(Alert.AlertType.ERROR, "Error", "An error occurred: " + ex.getMessage());
        }
    }

    @FXML
    private void loadArticles() {
        articleList.clear();
        // Fetch articles using the MainService
        ObservableList<Articles> articles = mainService.loadArticles();
        // Set up columns in the TableView
        tableColumnRemoveHeading.setCellValueFactory(new PropertyValueFactory<>("heading"));
        tableColumnRemoveCategory.setCellValueFactory(new PropertyValueFactory<>("category"));
        tableColumnRemoveDate.setCellValueFactory(new PropertyValueFactory<>("date"));
        // Bind data to the TableView
        tableRemoveArticles.setItems(articles);
    }

    @FXML
    private void handleFilterAction(ActionEvent event) {
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

        // Ensure at least one category is selected
        if (selectedCategories.isEmpty()) {
            MainService.showAlert(Alert.AlertType.INFORMATION, "Filter Article", "Please select at least one category.");
            return;
        }
        // Fetch filtered articles using the MainService
        ObservableList<Articles> filteredArticles = mainService.filterArticles(selectedCategories);
        // Clear existing data in the TableView
        articleList.clear();
        // Add filtered articles to the list
        articleList.addAll(filteredArticles);
        // Update the TableView
        tableRemoveArticles.setItems(articleList);
        if (articleList.isEmpty()) {
            MainService.showAlert(Alert.AlertType.INFORMATION, "Filter Article", "No articles found for the selected filters.");
        }
    }

    @FXML
    private void deleteArticle(ActionEvent event) {
        // Get the selected article from the TableView
        Articles selectedArticle = tableRemoveArticles.getSelectionModel().getSelectedItem();

        if (selectedArticle == null) {
            MainService.showAlert(Alert.AlertType.INFORMATION, "Delete Article", "Please select an article to delete.");
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
        MainService.showAlert(Alert.AlertType.INFORMATION, "Delete Article", "Article deleted successfully.");

    }

    @FXML
    private void handleLogoutButtonClick() throws IOException {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Log-Out");
        alert.setHeaderText("Are you sure you want to Log-Out?");
        Optional<ButtonType> output = alert.showAndWait();

        if (output.isPresent() && output.get() == ButtonType.OK) {
            Parent mainRoot = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("/org/example/news_recommendation_system/LogInPage.fxml")));
            Stage stage = (Stage) btnLogout.getScene().getWindow();
            Scene scene = stage.getScene();
            scene.setRoot(mainRoot);
            stage.sizeToScene();
            Application.makeSceneDraggable(stage, (Pane) mainRoot);
        }
    }

    @FXML
    public void exit(ActionEvent event) {
        MainService.showExitConfirmation(event);
    }
}
