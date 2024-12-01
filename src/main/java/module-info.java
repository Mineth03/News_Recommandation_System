module org.example.news_recommandation_system {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;
    requires org.mongodb.driver.sync.client;
    requires org.mongodb.bson;
    requires org.mongodb.driver.core;
    requires com.google.gson;
    requires commons.csv;
    requires org.json;
    requires java.desktop;


    opens org.example.news_recommendation_system to javafx.fxml;
    exports org.example.news_recommendation_system.Model;
    opens org.example.news_recommendation_system.Model to javafx.fxml;
    exports org.example.news_recommendation_system.App;
    opens org.example.news_recommendation_system.App to javafx.fxml;
}