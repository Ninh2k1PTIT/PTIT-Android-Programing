package com.example.socialnetwork.Service;

import android.content.Context;
import android.content.SharedPreferences;

public class TokenManager {
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;
    private Context context;

    public TokenManager(Context context) {
        this.context = context;
        sharedPreferences = context.getSharedPreferences("JWT_SHARED_PREFERENCES", Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
    }

    public void save(String accessToken, Integer userId, String username, String phoneNumber, String email, String avatar, String gender) {
        editor.putString("accessToken", accessToken);
        editor.putInt("userId", userId);
        editor.putString("username", username);
        editor.putString("phoneNumber", phoneNumber);
        editor.putString("email", email);
        editor.putString("avatar", avatar);
        editor.putString("gender", gender);
        editor.apply();
    }

    public String getToken() {
        return sharedPreferences.getString("accessToken", null);
    }

    public Integer getUserId() {
        return sharedPreferences.getInt("userId", 0);
    }

    public String getEmail() {
        return sharedPreferences.getString("email", null);
    }

    public String getUsername() {
        return sharedPreferences.getString("username", null);
    }

    public void setUsername(String username) {
        editor.putString("username", username);
        editor.apply();
    }

    public String getPhoneNumber() {
        return sharedPreferences.getString("phoneNumber", null);
    }

    public void setPhoneNumber(String phoneNumber) {
        editor.putString("phoneNumber", phoneNumber);
        editor.apply();
    }

    public String getAvatar() {
        return sharedPreferences.getString("avatar", null);
    }

    public void setAvatar(String avatar) {
        editor.putString("avatar", avatar);
        editor.apply();
    }

    public String getGender() {
        return sharedPreferences.getString("gender", null);
    }

    public void setGender(String gender) {
        editor.putString("gender", gender);
        editor.apply();
    }

    public void remove() {
        editor.remove("accessToken");
        editor.remove("userId");
        editor.remove("gender");
        editor.remove("avatar");
        editor.remove("email");
        editor.remove("phoneNumber");
        editor.remove("username");
        editor.apply();
    }
}
