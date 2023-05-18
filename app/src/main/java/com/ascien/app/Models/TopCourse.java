package com.ascien.app.Models;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class TopCourse implements Serializable {
    @SerializedName("id")
    private int id;
    @SerializedName("user_id")
    private int user_id;
    @SerializedName("title")
    private String title;
    @SerializedName("category_id")
    private int category_id;
    @SerializedName("short_description")
    private String short_description;
    @SerializedName("language")
    private String language;
    @SerializedName("description")
    private String description;
    @SerializedName("level")
    private String level;
    @SerializedName("image")
    private String image;
    @SerializedName("intro_video")
    private String intro_video;
    @SerializedName("requirements")
    private String requirements;
    @SerializedName("what_will_learn")
    private String what_will_learn;
    @SerializedName("is_free")
    private int is_free;
    @SerializedName("price")
    private String price;
    @SerializedName("sale_price")
    private String sale_price;
    @SerializedName("certificate")
    private String certificate;
    @SerializedName("created_at")
    private String created_at;
    @SerializedName("updated_at")
    private String updated_at;
    @SerializedName("status")
    private String status;
    @SerializedName("instructor")
    public Instructor instructor;

    public TopCourse(int id, int user_id, String title, int category_id, String short_description, String language, String description, String level, String image, String intro_video, String requirements, String what_will_learn, int is_free, String price, String sale_price, String certificate, String created_at, String updated_at, String status,Instructor instructor) {
        this.id = id;
        this.user_id = user_id;
        this.title = title;
        this.category_id = category_id;
        this.short_description = short_description;
        this.language = language;
        this.description = description;
        this.level = level;
        this.image = image;
        this.intro_video = intro_video;
        this.requirements = requirements;
        this.what_will_learn = what_will_learn;
        this.is_free = is_free;
        this.price = price;
        this.sale_price = sale_price;
        this.certificate = certificate;
        this.created_at = created_at;
        this.updated_at = updated_at;
        this.status = status;
        this.instructor = instructor;
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

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getCategory_id() {
        return category_id;
    }

    public void setCategory_id(int category_id) {
        this.category_id = category_id;
    }

    public String getShort_description() {
        return short_description;
    }

    public void setShort_description(String short_description) {
        this.short_description = short_description;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getLevel() {
        return level;
    }

    public void setLevel(String level) {
        this.level = level;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getIntro_video() {
        return intro_video;
    }

    public void setIntro_video(String intro_video) {
        this.intro_video = intro_video;
    }

    public String getRequirements() {
        return requirements;
    }

    public void setRequirements(String requirements) {
        this.requirements = requirements;
    }

    public String getWhat_will_learn() {
        return what_will_learn;
    }

    public void setWhat_will_learn(String what_will_learn) {
        this.what_will_learn = what_will_learn;
    }

    public int getIs_free() {
        return is_free;
    }

    public void setIs_free(int is_free) {
        this.is_free = is_free;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getSale_price() {
        return sale_price;
    }

    public void setSale_price(String sale_price) {
        this.sale_price = sale_price;
    }

    public String getCertificate() {
        return certificate;
    }

    public void setCertificate(String certificate) {
        this.certificate = certificate;
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

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
    public Instructor getInstructor() {
        return instructor;
    }

    public void setInstructor(Instructor instructor) {
        this.instructor = instructor;
    }
    public static class Instructor implements Serializable{
        private int id;
        private String name;
        private String email;
        private String avatar;

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

        public String getEmail() {
            return email;
        }

        public void setEmail(String email) {
            this.email = email;
        }

        public String getAvatar() {
            return avatar;
        }

        public void setAvatar(String avatar) {
            this.avatar = avatar;
        }
    }
}
