package com.ascien.app.Models;

public class CategoryCourse {
    private int id;
    private String name;
    private String image;
    private String description;
    private int courses_count;

    public CategoryCourse(int id, String name, String image, String description,int courses_count) {
        this.id = id;
        this.name = name;
        this.image = image;
        this.description = description;
        this.courses_count = courses_count;
    }

    public int getId() {
        return id;
    }
    public void setId(int id) {
        this.id = id;
    }
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public String getImage() {
        return image;
    }
    public void setImage(String image) {
        this.image = image;
    }
    public String getDescription() {
        return description;
    }
    public void setDescription(String description) {
        this.description = description;
    }
    public int getCourses_count() { return courses_count; }
    public void setCourses_count(int courses_count) { this.courses_count = courses_count; }
}