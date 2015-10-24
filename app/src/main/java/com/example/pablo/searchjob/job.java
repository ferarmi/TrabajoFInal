package com.example.pablo.searchjob;

/**
 * Created by DOCENTES on 20/10/2015.
 */
public class job {
    private int id;
    private String title;
    private String description;
    private String posted_date;

    public job(int id, String title, String description, String posted_date) {
        this.setId(id);
        this.setTitle(title);
        this.setDescription(description);
        this.setPosted_date(posted_date);
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getPosted_date() {
        return posted_date;
    }

    public void setPosted_date(String posted_date) {
        this.posted_date = posted_date;
    }

}

