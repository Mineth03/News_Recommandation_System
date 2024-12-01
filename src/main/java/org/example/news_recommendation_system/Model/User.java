package org.example.news_recommendation_system.Model;

import java.util.Collections;
import java.util.List;

public class User extends Person {
    private List<String> preferences;
    private String username;

    public User(String name, String email, int age, String gender, String password, List<String> preferences, String username) {
        super(name, email, age, gender, password);
        this.preferences = preferences;
        this.username = username;
    }

    public List<String> getPreferences() {
        return preferences;
    }

    public void setPreferences(String preferences) {
        this.preferences = Collections.singletonList(preferences);
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }
}
