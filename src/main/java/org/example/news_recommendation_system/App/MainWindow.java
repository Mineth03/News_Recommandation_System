package org.example.news_recommendation_system.App;

import com.mongodb.client.*;
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
import java.io.IOException;
import java.util.*;
import org.bson.Document;
import org.example.news_recommendation_system.DataBase.MongoDBConnection;
import org.example.news_recommendation_system.Model.Articles;
import org.example.news_recommendation_system.Model.LoginRecord;
import org.example.news_recommendation_system.Service.RecommendationEngine;
import org.example.news_recommendation_system.Service.MainService;

public class MainWindow {

    private static String currentUsername;

    private final ObservableList<Articles> articleList = FXCollections.observableArrayList();

    @FXML
    private Button btnProfile;
    @FXML
    private Button btnSave;
    @FXML
    private Button btnSearch;
    @FXML
    private Button btnRecommended;
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
    private Button btnBack3;
    @FXML
    private Button btnBack4;
    @FXML
    private Button btnConfirm;
    @FXML
    private Button btnPasswordConfirm;
    @FXML
    private Button btnSetPassword;
    @FXML
    private Button btnRefresh;
    @FXML
    private Button btnView;
    @FXML
    private Button btnViewS;

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
    private TableView<Articles> tableFilterNews;
    @FXML
    private TableColumn<Articles, String> tableColumnTitleF;
    @FXML
    private TableColumn<Articles, String> tableColumnCategoryF;
    @FXML
    private TableColumn<Articles, String> tableColumnDateF;

    @FXML
    private TableView<Articles> tableSave;
    @FXML
    private TableColumn<Articles, String> tableColumnTitleS;
    @FXML
    private TableColumn<Articles, String> tableColumnCategoryS;
    @FXML
    private TableColumn<Articles, String> tableColumnDateS;

    @FXML
    private CheckBox checkBoxTechnology, checkBoxEducation, checkBoxPolitics, checkBoxHealthcare, checkBoxEntertainment,
            checkBoxScience, checkBoxSports, checkBoxBusiness, checkBoxInvestigative, checkBoxLifestyle;
    @FXML
    private CheckBox checkBoxTechnology1, checkBoxEducation1, checkBoxPolitics1, checkBoxHealthcare1, checkBoxEntertainment1,
            checkBoxScience1, checkBoxSports1, checkBoxBusiness1, checkBoxInvestigative1, checkBoxLifestyle1;

    private final MongoDBConnection mongoDBConnection;

    private final RecommendationEngine recommendationEngine;
    private MainService mainService;

    public static void setCurrentUsername(String username) {
        currentUsername = username;
    }

    public MainWindow() {
        mongoDBConnection = new MongoDBConnection();
        recommendationEngine = new RecommendationEngine(mongoDBConnection);
    }

    @FXML
    public void buttonClicksConfig(ActionEvent actionEvent){
        if (actionEvent.getSource() == btnProfile){
            updateUserDetails();
            paneProfile.toFront();
            paneCheckPrevPassword.toFront();
        }
        if (actionEvent.getSource() == btnSave) {
            loadSavedArticles();
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
        if (actionEvent.getSource() == btnBack3) {
            txtPrevPassword.clear();
            paneProfile.toFront();
        }
        if (actionEvent.getSource() == btnBack4) {
            txtNewPassword.clear();
            txtNewPasswordConfirm.clear();
            txtPrevPassword.clear();
            paneCheckPrevPassword.toFront();
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
        if (actionEvent.getSource() == btnRefresh) {
            refreshArticles(currentUsername);
        }
        if (actionEvent.getSource() == btnView) {
            handleViewArticleFromTable(tableFilterNews);
        }
        if (actionEvent.getSource() == btnViewS) {
            refreshArticles(currentUsername);
            handleViewArticleFromTable(tableSave);
        }
    }

    @FXML
    public void initialize() {
        mainService = new MainService(mongoDBConnection.getDatabase());

        tableColumnDate.setCellValueFactory(new PropertyValueFactory<>("date"));
        tableColumnTime.setCellValueFactory(new PropertyValueFactory<>("time"));

        loadAndDisplayArticles(currentUsername);
        loadArticles();

        //saved articles table
        tableColumnTitleS.setCellValueFactory(new PropertyValueFactory<>("heading"));
        tableColumnCategoryS.setCellValueFactory(new PropertyValueFactory<>("category"));
        tableColumnDateS.setCellValueFactory(new PropertyValueFactory<>("date"));
    }

    @FXML
    private void updateUserDetails() {
        if (currentUsername == null || currentUsername.isEmpty()) {
            System.out.println("No username found for the current session.");
            return;
        }

        MongoCollection<Document> collection = mongoDBConnection.getCollection("User");

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
        ObservableList<LoginRecord> loginHistory = mainService.getLoginHistory(currentUsername);
        tableLoginDetails.setItems(loginHistory);
    }

    private void loadUserProfileForEdit() {
        if (currentUsername == null || currentUsername.isEmpty()) {
            System.out.println("No username found for the current session.");
            return;
        }

        MongoCollection<Document> collection = mongoDBConnection.getCollection("User");

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

        String fullName = MainService.capitalizeFirstLetter(firstName) + " " + MainService.capitalizeFirstLetter(lastName);

        // Validate inputs
        if (!MainService.validateProfileInputs(firstName, lastName, email, ageText)) {
            return;
        }

        int age = Integer.parseInt(ageText);
        // Collect preferences
        List<String> selectedPreferences = getSelectedPreferences();
        // Update user profile using MainService
        mainService.updateUserProfile(currentUsername, fullName, age, email, selectedPreferences);
        MainService.showAlert(Alert.AlertType.INFORMATION, null, "Profile updated successfully.");
        updateUserDetails();
        paneProfile.toFront();
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

    @FXML
    private boolean checkCurrentPassword() {
        String currentPassword = txtPrevPassword.getText().trim();

        if (currentPassword.isEmpty()) {
            MainService.showAlert(Alert.AlertType.ERROR, null, "Current password is required.");
            return false;
        }
        if (!mainService.isUserExist(currentUsername)) {
            MainService.showAlert(Alert.AlertType.ERROR, null, "User not found in the database.");
            return false;
        }
        if (mainService.verifyCurrentPassword(currentUsername, currentPassword)) {
            paneNewPassword.toFront();
            return true;
        } else {
            MainService.showAlert(Alert.AlertType.ERROR, null, "Incorrect current password.");
            return false;
        }
    }

    @FXML
    private void updatePassword() {
        String newPassword = txtNewPassword.getText().trim();
        String confirmPassword = txtNewPasswordConfirm.getText().trim();

        if (!mainService.validatePassword(newPassword)) {
            MainService.showAlert(Alert.AlertType.ERROR, null, "Password must be at least 5 characters long and contain both letters and numbers.");
            return;
        }
        if (!newPassword.equals(confirmPassword)) {
            MainService.showAlert(Alert.AlertType.ERROR, null, "Passwords do not match.");
            return;
        }
        if (mainService.updatePassword(currentUsername, newPassword)) {
            Alert alert = new Alert(Alert.AlertType.INFORMATION, "Password updated successfully.");
            alert.showAndWait();
            paneProfile.toFront();
            paneCheckPrevPassword.toFront();
        } else {
            MainService.showAlert(Alert.AlertType.ERROR, null, "Error updating password.");
        }
    }

    public void loadAndDisplayArticles(String username) {
        List<Articles> articles = recommendationEngine.fetchArticlesBasedOnPoints(username);
        displayArticles(articles); // Populate the GridPane
    }

    public void displayArticles(List<Articles> articles) {
        paneGrid.getChildren().clear(); // Clear existing articles
        int columns = 4; // Set the number of articles per row
        int row = 0, col = 1;
        try {
            for (Articles article : articles) {
                // Load the article pane
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/org/example/news_recommendation_system/ArticleBox.fxml"));
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

    private void refreshArticles(String username) {
        List<Articles> refreshedArticles = recommendationEngine.fetchArticlesBasedOnPoints(username);
        displayArticles(refreshedArticles); // Redisplay the updated articles
    }

    @FXML
    private void loadArticles() {
        articleList.clear();
        // Fetch articles using the MainService
        ObservableList<Articles> articles = mainService.loadArticles();
        // Set up columns in the TableView
        tableColumnTitleF.setCellValueFactory(new PropertyValueFactory<>("heading"));
        tableColumnCategoryF.setCellValueFactory(new PropertyValueFactory<>("category"));
        tableColumnDateF.setCellValueFactory(new PropertyValueFactory<>("date"));
        // Bind data to the TableView
        tableFilterNews.setItems(articles);
    }

    @FXML
    private void handleFilterAction(ActionEvent event) {
        // Collect selected categories
        List<String> selectedCategories = new ArrayList<>();
        if (checkBoxTechnology1.isSelected()) selectedCategories.add("Technology");
        if (checkBoxEducation1.isSelected()) selectedCategories.add("Education");
        if (checkBoxHealthcare1.isSelected()) selectedCategories.add("Healthcare");
        if (checkBoxPolitics1.isSelected()) selectedCategories.add("Politics");
        if (checkBoxScience1.isSelected()) selectedCategories.add("Science");
        if (checkBoxEntertainment1.isSelected()) selectedCategories.add("Entertainment");
        if (checkBoxBusiness1.isSelected()) selectedCategories.add("Business");
        if (checkBoxSports1.isSelected()) selectedCategories.add("Sports");
        if (checkBoxLifestyle1.isSelected()) selectedCategories.add("Lifestyle");
        if (checkBoxInvestigative1.isSelected()) selectedCategories.add("Investigative");

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
        tableFilterNews.setItems(articleList);
        // Show an alert if no articles match the selected filters
        if (articleList.isEmpty()) {
            MainService.showAlert(Alert.AlertType.INFORMATION, "Filter Article", "No articles found for the selected filters.");
        }
    }

    @FXML
    private void handleViewArticleFromTable(TableView<Articles> tableView) {
        // Check if an article is selected in the table
        Articles selectedArticle = tableView.getSelectionModel().getSelectedItem();
        if (selectedArticle == null) {
            MainService.showAlert(Alert.AlertType.INFORMATION, "View Article", "Please select an article to view.");
            return;
        }

        // Get the heading of the selected article
        String selectedHeading = selectedArticle.getHeading();

        // Fetch article details using the MainService
        Articles article = mainService.getArticleByHeading(selectedHeading);

        if (article == null) {
            MainService.showAlert(Alert.AlertType.INFORMATION, "View Article", "The selected article could not be found in the database.");
            return;
        }

        try {
            // Load the ArticleView FXML and set the article details
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/org/example/news_recommendation_system/ArticleView.fxml"));
            AnchorPane articleViewPane = loader.load();

            // Get the controller of ArticleView
            ArticleView controller = loader.getController();
            controller.setCurrentUsername(currentUsername); // Pass current username
            controller.setArticleDetails(article); // Pass the selected article details

            // Open the new scene in a separate stage
            Stage articleStage = new Stage();
            articleStage.setTitle("View Article");
            articleStage.setScene(new Scene(articleViewPane));
            articleStage.show();
        } catch (Exception e) {
            e.printStackTrace();
            MainService.showAlert(Alert.AlertType.ERROR, "Error", "An error occurred while trying to view the article.");
        }
    }

    @FXML
    private void loadSavedArticles() {
        List<Articles> savedArticles = mainService.loadSavedArticles(currentUsername);
        tableSave.getItems().setAll(savedArticles);
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
        MainService.showExitConfirmation(event);
    }
}
