package org.example.news_recommandation_system;

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

