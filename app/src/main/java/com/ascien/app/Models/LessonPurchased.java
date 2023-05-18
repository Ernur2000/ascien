package com.ascien.app.Models;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class LessonPurchased implements Serializable {
    private int id;
    private int section_id;
    private String title;
    private String description;
    private String content_type;
    private String video_url;
    private String presentation_file;
    private String article_text;
    private String resourses;
    private int quiz_id;
    private String created_at;
    private String updated_at;
    private String duration;
    private int percentage;

    public LessonPurchased(int id, int section_id, String title, String description, String content_type, String video_url, String presentation_file, String article_text, String resourses, int quiz_id, String created_at, String updated_at, String duration,int percentage) {
        this.id = id;
        this.section_id = section_id;
        this.title = title;
        this.description = description;
        this.content_type = content_type;
        this.video_url = video_url;
        this.presentation_file = presentation_file;
        this.article_text = article_text;
        this.resourses = resourses;
        this.quiz_id = quiz_id;
        this.created_at = created_at;
        this.updated_at = updated_at;
        this.duration = duration;
        this.percentage = percentage;
    }

    public int getId() {
        return id;
    }
    public void setId(int id) {
        this.id = id;
    }
    public int getSection_id() {
        return section_id;
    }
    public void setSection_id(int section_id) {
        this.section_id = section_id;
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
    public String getContent_type() {
        return content_type;
    }
    public void setContent_type(String content_type) {
        this.content_type = content_type;
    }
    public String getVideo_url() {
        return video_url;
    }
    public void setVideo_url(String video_url) {
        this.video_url = video_url;
    }
    public String getPresentation_file() {
        return presentation_file;
    }
    public void setPresentation_file(String presentation_file) {
        this.presentation_file = presentation_file;
    }
    public String getArticle_text() {
        return article_text;
    }
    public void setArticle_text(String article_text) {
        this.article_text = article_text;
    }
    public String getResourses() {
        return resourses;
    }
    public void setResourses(String resourses) {
        this.resourses = resourses;
    }
    public int getQuiz_id() {
        return quiz_id;
    }
    public void setQuiz_id(int quiz_id) {
        this.quiz_id = quiz_id;
    }
    public String getCreated_at() {
        return created_at;
    }
    public void setCreated_at(String created_at) {
        this.created_at = created_at;
    }
    public String getUpdated_at() {
        return updated_at;
    }
    public void setUpdated_at(String updated_at) {
        this.updated_at = updated_at;
    }
    public String getDuration() {
        return duration;
    }
    public void setDuration(String duration) {
        this.duration = duration;
    }
    public int getPercentage() {
        return percentage;
    }

    public void setPercentage(int percentage) {
        this.percentage = percentage;
    }
}
