package org.example.news_recommendation_system.Model;


public class Articles {

    private String heading;
    private String date;
    private String category;
    private String body;
    private String url;

    private int points;

    public Articles(String heading, String date, String category, String body) {
        this.heading = heading;
        this.date = date;
        this.category = category;
        this.body = body;
    }

    public Articles(String heading, String date, String category, int points) {
        this.heading = heading;
        this.date = date;
        this.category = category;
        this.points = points;
    }

    public Articles(String heading, String category, String date) {
        this.heading = heading;
        this.category = category;
        this.date = date;
    }

    public Articles(String heading, String date, String category, String body, String url) {
        this.heading = heading;
        this.date = date;
        this.category = category;
        this.body = body;
        this.url = url;
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

    public int getPoints() {
        return points;
    }

    public void setPoints(int points) {
        this.points = points;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}
