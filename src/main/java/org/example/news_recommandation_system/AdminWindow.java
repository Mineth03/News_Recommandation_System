package org.example.news_recommandation_system;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
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

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;

public class AdminWindow {

    private static String currentAdminId;

    @FXML
    private Button btnProfile;
    @FXML
    private Button btnLogout;
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
    private Button btnRemoveUser;
    @FXML
    private Button btnViewUser;


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


    public static void setCurrentAdminId(String adminId) {
        currentAdminId = adminId;
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
    private void updateAdminDetails() {
        if (currentAdminId == null || currentAdminId.isEmpty()) {
            System.out.println("No admin found for the current session.");
            return;
        }

        MongoDatabase database = getDatabase();
        if (database == null) return;

        MongoCollection<Document> collection = database.getCollection("Admin");

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

        MongoDatabase database = getDatabase();
        if (database == null) return;

        MongoCollection<Document> loginLogCollection = database.getCollection("Admin_Login_Log");
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

        MongoDatabase database = getDatabase();
        if (database == null) return;

        MongoCollection<Document> collection = database.getCollection("Admin");

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

        } else {
            System.out.println("Admin not found in the database.");
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

        MongoDatabase database = getDatabase();
        if (database == null) return;

        MongoCollection<Document> collection = database.getCollection("Admin");

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

        MongoDatabase database = getDatabase();
        if (database == null) {
            System.out.println("Database connection failed.");
            return;
        }

        MongoCollection<Document> collection = database.getCollection("User");

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
            MongoDatabase database = getDatabase();
            if (database == null) return;

            MongoCollection<Document> collection = database.getCollection("User");

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
        MongoDatabase database = getDatabase();
        if (database == null) return;

        MongoCollection<Document> collection = database.getCollection("User");

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

        // Connect to MongoDB
        MongoDatabase database = getDatabase();
        if (database == null) {
            System.err.println("Failed to connect to the database.");
            return;
        }

        MongoCollection<Document> loginLogCollection = database.getCollection("User_Login_Log");

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


}
