package org.example.news_recommendation_system.Service;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;
import org.example.news_recommendation_system.Model.Articles;
import org.example.news_recommendation_system.DataBase.MongoDBConnection;

import java.util.*;

import static com.mongodb.client.model.Filters.eq;

public class RecommendationEngine {
    private final MongoDBConnection mongoDBConnection;

    public RecommendationEngine(MongoDBConnection mongoDBConnection) {
        this.mongoDBConnection = mongoDBConnection;
    }

    public void saveUserPreferences(String username, boolean checkBoxTechnology, boolean checkBoxEducation,
                                    boolean checkBoxPolitics, boolean checkBoxHealthcare, boolean checkBoxEntertainment,
                                    boolean checkBoxScience, boolean checkBoxSports, boolean checkBoxBusiness,
                                    boolean checkBoxInvestigative, boolean checkBoxLifestyle) {

        // Create a new document for User_Preferences
        Document preferencesDoc = new Document("username", username)
                .append("Technology", checkBoxTechnology ? 5 : 0)
                .append("Education", checkBoxEducation ? 5 : 0)
                .append("Politics", checkBoxPolitics ? 5 : 0)
                .append("Healthcare", checkBoxHealthcare ? 5 : 0)
                .append("Entertainment", checkBoxEntertainment ? 5 : 0)
                .append("Science", checkBoxScience ? 5 : 0)
                .append("Sports", checkBoxSports ? 5 : 0)
                .append("Business", checkBoxBusiness ? 5 : 0)
                .append("Investigative", checkBoxInvestigative ? 5 : 0)
                .append("Lifestyle", checkBoxLifestyle ? 5 : 0);

        // Insert the document into the "User_Preferences" collection
        mongoDBConnection.insertDocument("User_Preferences", preferencesDoc);
    }

    public List<Articles> fetchArticlesBasedOnPoints(String username) {
        List<Articles> articles = new ArrayList<>();
        try {
            MongoCollection<Document> articlesCollection = mongoDBConnection.getCollection("Articles");
            MongoCollection<Document> userPointsCollection = mongoDBConnection.getCollection("User_Preferences");

            // Retrieve the user's points document
            Document userPointsDoc = userPointsCollection.find(eq("username", username)).first();
            if (userPointsDoc == null) {
                System.out.println("No points found for user: " + username);
                return articles; // Return an empty list if no points are found
            }

            // Calculate the total points across all categories
            Map<String, Integer> categoryPoints = new HashMap<>();
            int totalPoints = 0;

            for (String key : userPointsDoc.keySet()) {
                if (!key.equals("_id") && !key.equals("username")) {
                    int points = userPointsDoc.getInteger(key, 0);
                    categoryPoints.put(key, points);
                    totalPoints += points;
                }
            }

            // If total points are zero, return empty list
            if (totalPoints == 0) {
                System.out.println("No points assigned to any category for user: " + username);
                return articles;
            }

            // Calculate the proportional distribution of articles per category
            Map<String, Integer> categoryArticleQuota = new HashMap<>();
            int totalQuota = 20; // Number of articles to display
            for (Map.Entry<String, Integer> entry : categoryPoints.entrySet()) {
                int quota = Math.round((float) entry.getValue() / totalPoints * totalQuota);
                categoryArticleQuota.put(entry.getKey(), quota);
            }

            // Retrieve and shuffle articles for each category
            for (Map.Entry<String, Integer> entry : categoryArticleQuota.entrySet()) {
                String category = entry.getKey();
                int quota = entry.getValue();

                if (quota > 0) {
                    List<Document> categoryArticles = articlesCollection.find(eq("category", category))
                            .into(new ArrayList<>());

                    // Shuffle and limit the articles to the quota
                    Collections.shuffle(categoryArticles);
                    for (int i = 0; i < Math.min(quota, categoryArticles.size()); i++) {
                        Document doc = categoryArticles.get(i);
                        String heading = doc.getString("heading");
                        String date = doc.getString("date");
                        Articles article = new Articles(heading, date, category, categoryPoints.get(category));
                        articles.add(article);
                    }
                }
            }

            // Shuffle the final list to mix categories
            Collections.shuffle(articles);

            return articles;

        } catch (Exception e) {
            e.printStackTrace();
        }
        return articles;
    }

    public void updatePointsForViewing(String category, String currentUsername) {
        try {
            MongoDatabase database = mongoDBConnection.getDatabase();
            MongoCollection<Document> userCategoryCollection = database.getCollection("User_Preferences");

            Document userCategory = userCategoryCollection.find(eq("username", currentUsername)).first();
            if (userCategory != null) {
                int currentPoints = userCategory.getInteger(category, 0);
                int newPoints = currentPoints + 1; // Add 1 point for viewing the article

                userCategoryCollection.updateOne(
                        eq("username", currentUsername),
                        new Document("$set", new Document(category, newPoints))
                );
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void handleLikeClick(String currentUsername, Articles article) {
        updateInteraction(currentUsername, article, "liked", "disliked", 2);
    }

    public void handleDislikeClick(String currentUsername, Articles article) {
        updateInteraction(currentUsername, article, "disliked", "liked", -2);
    }

    private void updateInteraction(String currentUsername, Articles article, String targetList, String oppositeList, int pointsChange) {
        MongoDatabase database = mongoDBConnection.getDatabase();
        MongoCollection<Document> userCategoryCollection = database.getCollection("User_Preferences");
        MongoCollection<Document> userInteractionCollection = database.getCollection("User-Article-Interaction");

        // Ensure the "User-Article-Interaction" collection exists or create it if it doesn't
        if (userInteractionCollection.countDocuments() == 0) {
            Document newUserInteraction = new Document("username", currentUsername)
                    .append("liked", new ArrayList<>())
                    .append("disliked", new ArrayList<>())
                    .append("saved", new ArrayList<>());
            userInteractionCollection.insertOne(newUserInteraction);
        }

        Document userInteraction = userInteractionCollection.find(eq("username", currentUsername)).first();
        if (userInteraction == null || article == null) return;

        String articleHeading = article.getHeading();

        if (!userInteraction.containsKey(targetList)) {
            userInteraction.put(targetList, new ArrayList<String>());
        }

        if (!userInteraction.containsKey(oppositeList)) {
            userInteraction.put(oppositeList, new ArrayList<String>());
        }

        ArrayList<String> targetArray = (ArrayList<String>) userInteraction.get(targetList);
        ArrayList<String> oppositeArray = (ArrayList<String>) userInteraction.get(oppositeList);

        if (targetArray.contains(articleHeading)) {
            return;
        }

        int additionalPoints = 0;
        if (oppositeArray.contains(articleHeading)) {
            additionalPoints = targetList.equals("liked") ? 2 : -2;
            oppositeArray.remove(articleHeading);
        }

        targetArray.add(articleHeading);

        userInteractionCollection.updateOne(
                eq("username", currentUsername),
                new Document("$set", new Document(targetList, targetArray).append(oppositeList, oppositeArray))
        );

        if (userCategoryCollection.countDocuments() == 0) {
            Document newUserCategory = new Document("username", currentUsername)
                    .append(article.getCategory(), 0);
            userCategoryCollection.insertOne(newUserCategory);
        }

        Document userCategory = userCategoryCollection.find(eq("username", currentUsername)).first();
        if (userCategory != null) {
            int currentPoints = userCategory.getInteger(article.getCategory(), 0);
            int newPoints = currentPoints + pointsChange + additionalPoints;

            userCategoryCollection.updateOne(
                    eq("username", currentUsername),
                    new Document("$set", new Document(article.getCategory(), newPoints))
            );
        }
    }
}
