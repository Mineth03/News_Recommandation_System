package org.example.news_recommendation_system.Model;

public class LoginRecord {
    private final String date;
    private final String time;

    public LoginRecord(String date, String time) {
        this.date = date;
        this.time = time;
    }

    public String getDate() {
        return date;
    }

    public String getTime() {
        return time;
    }
}
