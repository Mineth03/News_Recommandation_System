package org.example.news_recommendation_system.NewsFetcher;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;
import org.example.news_recommendation_system.Service.ArticleCategorizer;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

public class NewsFetcherJSON {

    private static final String NDJSON_FILE_PATH = "src/main/resources/DataSet/Dataset.json";
    private static final String DATABASE_URL = "mongodb+srv://admin:12345678%40mineth@cluster0.s0ovw.mongodb.net/?retryWrites=true&w=majority&appName=Cluster0";
    private static final String DATABASE_NAME = "News_Recommendation_System";
    private static final String COLLECTION_NAME = "Articles";
    private static final int THREAD_POOL_SIZE = Runtime.getRuntime().availableProcessors() * 2; // Optimized thread pool size
    private static final int BATCH_SIZE = 50; // Batch size for MongoDB insertions

    public static void main(String[] args) {
        ExecutorService executorService = new ThreadPoolExecutor(
                THREAD_POOL_SIZE,
                THREAD_POOL_SIZE,
                0L,
                TimeUnit.MILLISECONDS,
                new LinkedBlockingQueue<>(100) // Bounded queue for backpressure
        );

        try {
            // Step 1: Read articles from NDJSON
            List<JSONObject> articles = readNDJSON(NDJSON_FILE_PATH);

            // Step 2: Categorize and store articles using multithreading
            processArticlesInParallel(articles, executorService);

            System.out.println("Articles fetched and stored successfully!");
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            executorService.shutdown();
            try {
                if (!executorService.awaitTermination(60, TimeUnit.SECONDS)) {
                    executorService.shutdownNow();
                }
            } catch (InterruptedException e) {
                executorService.shutdownNow();
            }
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
                        articles.add(new JSONObject(line)); // Parse JSON line
                    } catch (Exception e) {
                        System.err.println("Invalid JSON line: " + line);
                        e.printStackTrace();
                    }
                }
            }
        }
        return articles;
    }

    // Method to process articles in parallel
    private static void processArticlesInParallel(List<JSONObject> articles, ExecutorService executorService) {
        List<Future<Void>> futures = new ArrayList<>();

        // Divide articles into batches for efficient processing
        List<List<JSONObject>> batches = createBatches(articles, BATCH_SIZE);

        for (List<JSONObject> batch : batches) {
            Callable<Void> task = new ArticleBatchProcessorTask(batch);
            futures.add(executorService.submit(task));
        }

        // Wait for all tasks to complete
        for (Future<Void> future : futures) {
            try {
                future.get(); // Wait for the task to complete
            } catch (InterruptedException | ExecutionException e) {
                System.err.println("Error processing batch: " + e.getMessage());
                e.printStackTrace();
            }
        }
    }

    // Helper method to divide a list into batches
    private static List<List<JSONObject>> createBatches(List<JSONObject> articles, int batchSize) {
        List<List<JSONObject>> batches = new ArrayList<>();
        for (int i = 0; i < articles.size(); i += batchSize) {
            int end = Math.min(i + batchSize, articles.size());
            batches.add(articles.subList(i, end));
        }
        return batches;
    }

    // Task class for processing a batch of articles
    static class ArticleBatchProcessorTask implements Callable<Void> {
        private final List<JSONObject> batch;

        ArticleBatchProcessorTask(List<JSONObject> batch) {
            this.batch = batch;
        }

        @Override
        public Void call() {
            MongoClient mongoClient = MongoClientProvider.getClient();
            try {
                MongoDatabase database = mongoClient.getDatabase(DATABASE_NAME);
                MongoCollection<Document> collection = database.getCollection(COLLECTION_NAME);

                ArticleCategorizer categorizer = new ArticleCategorizer();

                List<Document> documents = new ArrayList<>();
                for (JSONObject article : batch) {
                    String heading = article.optString("headline", "Unknown");
                    String shortDescription = article.optString("short_description", "");
                    String date = article.optString("date", "Unknown");
                    String link = article.optString("link", "Unknown");

                    // Categorize the article
                    String category = categorizer.categorizeArticle(shortDescription);

                    if (!category.equals("Uncategorized")) {
                        // Create a document for MongoDB
                        Document document = new Document("heading", heading)
                                .append("article", shortDescription)
                                .append("category", category)
                                .append("date", date)
                                .append("url", link);
                        documents.add(document);
                    }
                }

                // Batch insertion to MongoDB
                if (!documents.isEmpty()) {
                    collection.insertMany(documents);
                }
            } catch (Exception e) {
                System.err.println("Error processing batch: " + e.getMessage());
                e.printStackTrace();
            }
            return null;
        }
    }

    // Singleton provider for MongoClient
    static class MongoClientProvider {
        private static final MongoClient mongoClient = MongoClients.create(DATABASE_URL);

        public static MongoClient getClient() {
            return mongoClient;
        }
    }
}
