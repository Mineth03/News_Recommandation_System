package org.example.news_recommandation_system;

public class Admin extends Person {
    private String code;

    // Constructor for Admin
    public Admin(String name, String email, int age, String gender, String password, String code) {
        super(name, email, age, gender, password);
        this.code = code;
    }

    // Getter and Setter for Code
    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }
}
