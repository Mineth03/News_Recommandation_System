package org.example.news_recommandation_system;

import java.util.Date;

public class Articles {

    private String heading;
    private String date;
    private String category;
    private String body;

    Articles(String heading, String date, String category, String body) {
        this.heading = heading;
        this.date = date;
        this.category = category;
        this.body = body;
    }

    Articles(String heading, String category, String date) {
        this.heading = heading;
        this.category = category;
        this.date = date;
    }

    public String getHeading() {
        return heading;
    }

    public void setHeading(String heading) {
        this.heading = heading;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }
}
