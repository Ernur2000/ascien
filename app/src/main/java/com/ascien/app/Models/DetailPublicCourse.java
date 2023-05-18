package com.ascien.app.Models;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;
public class DetailPublicCourse implements Serializable {
    @SerializedName("topCourse")
    public TopCourse topCourse;
    @SerializedName("sections")
    private List<Sections> sections;

    public DetailPublicCourse(TopCourse topCourse, List<Sections> sections) {
        this.topCourse = topCourse;
        this.sections = sections;

    }

    public TopCourse getTopCourse() {
        return topCourse;
    }

    public void setTopCourse(TopCourse topCourse) {
        this.topCourse = topCourse;
    }

    public List<Sections> getSections() {
        return sections;
    }

    public void setSections(List<Sections> sections) {
        this.sections = sections;
    }
}


