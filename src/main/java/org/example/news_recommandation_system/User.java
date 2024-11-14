package org.example.news_recommandation_system;

import java.util.Collections;
import java.util.List;

public class User extends Person {
    private List<String> preferences;

    public User(String name, String email, int age, String gender, String password, List<String> preferences) {
        super(name, email, age, gender, password);
        this.preferences = preferences;
    }

    public List<String> getPreferences() {
        return preferences;
    }

    public void setPreferences(String preferences) {
        this.preferences = Collections.singletonList(preferences);
    }
}
