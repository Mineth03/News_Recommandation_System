package org.example.news_recommandation_system.NewsFetcher;

import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;

import org.json.JSONObject;

public class ArticleCategorizeModel {

    private static final String API_URL = "http://localhost:5000/classify";
    private static final String[] CATEGORIES = {
            "Technology", "Education", "Politics", "Healthcare",
            "Entertainment", "Science", "Sports", "Business",
            "Investigative", "Lifestyle"
    };

    public String categorizeArticle(String article) {
        try {
            // Prepare JSON payload
            JSONObject payload = new JSONObject();
            payload.put("article", article);
            payload.put("categories", CATEGORIES);

            // Make HTTP POST request to Python API
            URL url = new URL(API_URL);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Content-Type", "application/json");
            connection.setDoOutput(true);

            // Write the JSON payload
            try (OutputStream os = connection.getOutputStream()) {
                byte[] input = payload.toString().getBytes(StandardCharsets.UTF_8);
                os.write(input, 0, input.length);
            }

            // Read the response
            if (connection.getResponseCode() == 200) {
                try (Scanner scanner = new Scanner(connection.getInputStream())) {
                    String response = scanner.useDelimiter("\\A").next();
                    JSONObject jsonResponse = new JSONObject(response);
                    return jsonResponse.getString("category");
                }
            } else {
                System.err.println("API Error: " + connection.getResponseMessage());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "Uncategorized";
    }
}
