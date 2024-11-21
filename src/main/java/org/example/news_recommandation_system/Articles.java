package org.example.news_recommandation_system;

import java.util.Date;

public class Articles {

    private String heading;
    private Date date;
    private String category;
    private String body;

    Articles(String heading, Date date, String category, String body) {
        this.heading = heading;
        this.date = date;
        this.category = category;
        this.body = body;
    }

    public String getHeading() {
        return heading;
    }

    public void setHeading(String heading) {
        this.heading = heading;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
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
