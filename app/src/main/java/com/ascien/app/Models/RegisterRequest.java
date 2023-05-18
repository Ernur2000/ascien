package com.ascien.app.Models;

public class RegisterRequest {
    private String email;
    private String name;
    private String password;

    public String getEmail() {
        return email;
    }
    public void setEmail(String email) {
        this.email = email;
    }
    public String getUsername() {
        return name;
    }
    public void setUsername(String username) {
        this.name = username;
    }
    public String getPassword() {
        return password;
    }
    public void setPassword(String password) {
        this.password = password;
    }
}
