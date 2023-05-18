package com.ascien.app.Models;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class WishListCourse implements Serializable {
    private int id;
    private int user_id;
    private int course_id;
    private String created_at;
    private String updated_at;
    @SerializedName("courses")
    private TopCourse topCourse;

    public WishListCourse(int id, int user_id, int course_id, String created_at, String updated_at, TopCourse topCourse) {
        this.id = id;
        this.user_id = user_id;
        this.course_id = course_id;
        this.created_at = created_at;
        this.updated_at = updated_at;
        this.topCourse = topCourse;
    }


    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getUser_id() {
        return user_id;
    }

    public void setUser_id(int user_id) {
        this.user_id = user_id;
    }

    public int getCourse_id() {
        return course_id;
    }

    public void setCourse_id(int course_id) {
        this.course_id = course_id;
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

    public TopCourse getTopCourse() {
        return topCourse;
    }

    public void setTopCourse(TopCourse topCourse) {
        this.topCourse = topCourse;
    }
}
