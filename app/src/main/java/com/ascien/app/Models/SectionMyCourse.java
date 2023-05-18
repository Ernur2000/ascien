package com.ascien.app.Models;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

public class SectionMyCourse implements Serializable {
    @SerializedName("id")
    public int id;
    @SerializedName("course_id")
    public int course_id;
    @SerializedName("title")
    public String title;
    @SerializedName("description")
    public String description;
    @SerializedName("lessonPurchased")
    public List<LessonPurchased> lessonPurchased;

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
    public List<LessonPurchased> getLessonPurchased() {
        return lessonPurchased;
    }
    public void setLessonPurchased(List<LessonPurchased> lessonPurchased) {
        this.lessonPurchased = lessonPurchased;
    }
}
