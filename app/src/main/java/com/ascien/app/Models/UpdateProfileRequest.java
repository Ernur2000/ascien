package com.ascien.app.Models;

public class UpdateProfileRequest {
    private String name;
    private String password;
    //private String avatar;

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getPassword() {
        return password;
    }
    public void setPassword(String password) {
        this.password = password;
    }
//    public String getAvatar() { return avatar; }
//    public void setAvatar(String avatar) { this.avatar = avatar; }
}
