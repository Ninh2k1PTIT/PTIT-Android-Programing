package com.example.socialnetwork.Model;

public class LoginResponse {
    private String token;
    private Integer id;
    private String username;
    private String email;
    private String avatar;
    private String phoneNumber;
    private String gender;

    public LoginResponse() {
    }

    public String getToken() {
        return token;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
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

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public String getGender() {
        return gender;
    }
}
