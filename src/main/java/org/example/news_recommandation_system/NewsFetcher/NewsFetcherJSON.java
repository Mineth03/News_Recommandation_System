package org.example.news_recommandation_system.NewsFetcher;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class NewsFetcherJSON {

    private static final String NDJSON_FILE_PATH = "src/main/resources/DataSet/Dataset.json";
    private static final String DATABASE_URL = "mongodb://localhost:27017";
    private static final String DATABASE_NAME = "News_Recommendation_System";
    private static final String COLLECTION_NAME = "Articles";

    public static void main(String[] args) {
        try {
            // Step 1: Read articles from NDJSON
            List<JSONObject> articles = readNDJSON(NDJSON_FILE_PATH);

            // Step 2: Categorize and store articles
            categorizeAndStoreArticlesFromJSON(articles);

            System.out.println("Articles fetched and stored successfully!");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Method to read articles from an NDJSON file
    private static List<JSONObject> readNDJSON(String filePath) throws IOException {
        List<JSONObject> articles = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (!line.trim().isEmpty()) { // Ignore empty lines
                    try {
                        // Parse each line as a JSONObject and add to the list
                        articles.add(new JSONObject(line));
                    } catch (Exception e) {
                        System.err.println("Invalid JSON line: " + line);
                        e.printStackTrace();
                    }
                }
            }
        }
        return articles;
    }

    // Method to categorize and store articles in MongoDB
    private static void categorizeAndStoreArticlesFromJSON(List<JSONObject> records) {
        MongoClient mongoClient = MongoClients.create(DATABASE_URL);
        MongoDatabase database = mongoClient.getDatabase(DATABASE_NAME);
        MongoCollection<Document> collection = database.getCollection(COLLECTION_NAME);

        // Initialize the categorizer
        ArticleCategorizer categorizer = new ArticleCategorizer();

        for (JSONObject record : records) {
            String heading = record.optString("headline", "Unknown");
            String article = record.optString("short_description", "");
            String date = record.optString("date", "Unknown");

            // Categorize the article using the ArticleCategorizer class
            String category = categorizer.categorizeArticle(article);

            // Only store the article if it has a valid category
            if (!category.equals("Uncategorized")) {
                // Create a document to store in MongoDB
                Document document = new Document("heading", heading)
                        .append("article", article)
                        .append("category", category)
                        .append("date", date);

                collection.insertOne(document);
            }
        }

        mongoClient.close();
    }
}
