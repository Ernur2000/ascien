package com.ascien.app.Models;

public class Lessons {
    public int id;
    private String title;
    private String content_type;
    private String description;
    private String duration;
    private int section_id;

    public Lessons(int id, String title, String content_type, String description, String duration, int section_id) {
        this.id = id;
        this.title = title;
        this.content_type = content_type;
        this.description = description;
        this.duration = duration;
        this.section_id = section_id;
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
    public String getContent_type() {
        return content_type;
    }
    public void setContent_type(String content_type) {
        this.content_type = content_type;
    }
    public String getDescription() {
        return description;
    }
    public void setDescription(String description) {
        this.description = description;
    }
    public String getDuration() {
        return duration;
    }
    public void setDuration(String duration) {
        this.duration = duration;
    }
    public int getSection_id() {
        return section_id;
    }
    public void setSection_id(int section_id) {
        this.section_id = section_id;
    }
}



