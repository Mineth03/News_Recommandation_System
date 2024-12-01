package org.example.news_recommendation_system.NewsFetcher;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;
import org.example.news_recommendation_system.Service.ArticleCategorizer;

import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class NewsFetcher {

    private static final String API_KEY = "407f8c58a5094a058981924dc9ad28f4";
    private static final String NEWS_API_URL = "https://newsapi.org/v2/everything?q=technology&language=en&pageSize=100&page=";

    public static void main(String[] args) {
        try {
            // Step 1: Fetch articles
            JsonArray articles = fetchArticles(10);

            // Step 2: Categorize and store articles
            categorizeAndStoreArticles(articles);

            System.out.println("Articles fetched and stored successfully!");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Method to fetch articles from News API
    private static JsonArray fetchArticles(int totalPages) throws Exception {
        JsonArray allArticles = new JsonArray();

        for (int page = 1; page <= totalPages; page++) {
            URL url = new URL(NEWS_API_URL + page + "&apiKey=" + API_KEY);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");

            if (connection.getResponseCode() == 200) {
                JsonObject responseJson = JsonParser.parseReader(new InputStreamReader(connection.getInputStream())).getAsJsonObject();
                JsonArray articles = responseJson.getAsJsonArray("articles");
                allArticles.addAll(articles);
            } else {
                System.err.println("Error fetching articles: " + connection.getResponseMessage());
                break;
            }

            connection.disconnect();
        }

        return allArticles;
    }

    /// Method to categorize and store articles
    private static void categorizeAndStoreArticles(JsonArray articles) {
        MongoClient mongoClient = MongoClients.create("mongodb://localhost:27017");
        MongoDatabase database = mongoClient.getDatabase("News_Recommendation_System");
        MongoCollection<Document> collection = database.getCollection("Articles");

        ArticleCategorizer categorizer = new ArticleCategorizer();

        for (var article : articles) {
            JsonObject articleJson = article.getAsJsonObject();
            String title = articleJson.get("title").getAsString();
            String description = articleJson.get("description").isJsonNull() ? "" : articleJson.get("description").getAsString();
            String content = articleJson.get("content").isJsonNull() ? "" : articleJson.get("content").getAsString();
            String author = articleJson.get("author").isJsonNull() ? "Unknown" : articleJson.get("author").getAsString();
            String publishedDate = articleJson.get("publishedAt").getAsString();

            // Categorize the article
            String category = categorizer.categorizeArticle(title + " " + description + " " + content);

            // Only store articles that are not categorized as "Uncategorized"
            if (!category.equalsIgnoreCase("Uncategorized")) {
                // Create a document to store in MongoDB
                Document document = new Document("title", title)
                        .append("category", category)
                        .append("author", author)
                        .append("publishedDate", publishedDate)
                        .append("description", description)
                        .append("content", content);

                collection.insertOne(document);
            }
        }

        mongoClient.close();
    }

}
