package com.ascien.app.Models;

import java.util.List;

public class Sections {
    private int id;
    private int course_id;
    private String title;
    private String description;
    public List<Lessons> lessons;

    public Sections(int id, int course_id, String title, String description, List<Lessons> lessons) {
        this.id = id;
        this.course_id = course_id;
        this.title = title;
        this.description = description;
        this.lessons = lessons;
    }



    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getCourse_id() {
        return course_id;
    }

    public void setCourse_id(int course_id) {
        this.course_id = course_id;
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

    public List getLessons() {
        return lessons;
    }

}
