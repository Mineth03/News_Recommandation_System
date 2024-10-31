module org.example.news_recommandation_system {
    requires javafx.controls;
    requires javafx.fxml;


    opens org.example.news_recommandation_system to javafx.fxml;
    exports org.example.news_recommandation_system;
}