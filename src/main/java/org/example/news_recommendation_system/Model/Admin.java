package org.example.news_recommendation_system.Model;

public class Admin extends Person {
    private String id;

    // Constructor for Admin
    public Admin(String name, String email, int age, String gender, String password, String id) {
        super(name, email, age, gender, password);
        this.id = id;
    }

    // Getter and Setter for Code
    public String getCode() {
        return id;
    }

    public void setCode(String code) {
        this.id = id;
    }
}
