package com.ascien.app.Models;

public class RegisterResponse {
    public boolean success;
    public Data data;
    public String message;
    public String status_code;
    public boolean isSuccess() {
        return success;
    }
    public void setSuccess(boolean success) {
        this.success = success;
    }
    public Data getData() {
        return data;
    }
    public void setData(Data data) {
        this.data = data;
    }
    public String getMessage() {
        return message;
    }
    public void setMessage(String message) {
        this.message = message;
    }
    public String getStatus_code() {
        return status_code;
    }
    public void setStatus_code(String status_code) {
        this.status_code = status_code;
    }

    static class Data{
        public User user;
        public User getUser() {
            return user;
        }
        public void setUser(User user) {
            this.user = user;
        }
    }

    static class User{
        public String email;
        public String name;
        public int id;
        public String getEmail() {
            return email;
        }
        public void setEmail(String email) {
            this.email = email;
        }
        public String getName() {
            return name;
        }
        public void setName(String name) {
            this.name = name;
        }
        public int getId() {
            return id;
        }
        public void setId(int id) {
            this.id = id;
        }
    }
}
