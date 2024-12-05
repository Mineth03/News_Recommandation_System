package org.example.news_recommendation_system.DataBase;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoDatabase;
import com.mongodb.MongoClientException;
import com.mongodb.client.MongoCollection;
import org.bson.Document;


public class MongoDBConnection {
    private MongoClient mongoClient;
    private MongoDatabase database;

    private static final String DATABASE_NAME = "News_Recommendation_System";
    private static final String URI = "mongodb+srv://admin:12345678%40mineth@cluster0.s0ovw.mongodb.net/?retryWrites=true&w=majority&appName=Cluster0";

    // Constructor to establish connection
    public MongoDBConnection() {
        try {
            mongoClient = MongoClients.create(URI);
            database = mongoClient.getDatabase(DATABASE_NAME);
            System.out.println("MongoDB Connection Established!");
        } catch (MongoClientException e) {
            System.err.println("Failed to connect to MongoDB: " + e.getMessage());
        }
    }

    // Get the database
    public MongoDatabase getDatabase() {
        return database;
    }

    // Get a collection from the database
    public MongoCollection<Document> getCollection(String collectionName) {
        return database.getCollection(collectionName);
    }

    // Insert a document into a collection
    public void insertDocument(String collectionName, Document document) {
        try {
            MongoCollection<Document> collection = getCollection(collectionName);
            collection.insertOne(document);
        } catch (MongoClientException e) {
            System.err.println("Failed to insert document: " + e.getMessage());
        }
    }

    // Find a document in a collection
    public Document findDocument(String collectionName, Document query) {
        try {
            MongoCollection<Document> collection = getCollection(collectionName);
            return collection.find(query).first();
        } catch (MongoClientException e) {
            return null;
        }
    }

    public void updateDocument(String collectionName, Document query, Document update) {
        try {
            MongoCollection<Document> collection = getCollection(collectionName);
            collection.updateOne(query, new Document("$set", update));
        } catch (MongoClientException e) {
            System.err.println("Failed to update document: " + e.getMessage());
        }
    }

    // Close the MongoDB connection
    public void closeConnection() {
        if (mongoClient != null) {
            mongoClient.close();
            System.out.println("MongoDB Connection Closed!");
        }
    }
}
