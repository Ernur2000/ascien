package com.ascien.app.Models;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;
import java.util.ListIterator;

public class MyPurchasedCourse implements Serializable {
    @SerializedName("topCourse")
    public TopCourse topCourse;
    @SerializedName("sections")
    public List<SectionMyCourse> sectionMyCourse;


    public MyPurchasedCourse(TopCourse topCourse, List<SectionMyCourse> sectionMyCourse) {
        this.topCourse = topCourse;
        this.sectionMyCourse = sectionMyCourse;
    }

    public TopCourse getTopCourse() {
        return topCourse;
    }
    public void setTopCourse(TopCourse topCourse) {
        this.topCourse = topCourse;
    }
    public List<SectionMyCourse> getSection() {
        return sectionMyCourse;
    }
    public void setSection(List<SectionMyCourse> section) {
        this.sectionMyCourse = section;
    }



}
