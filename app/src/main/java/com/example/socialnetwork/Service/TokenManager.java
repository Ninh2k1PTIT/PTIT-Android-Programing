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

    public void save(String accessToken) {
        editor.putString("accessToken", accessToken);
        editor.apply();
    }

    public String get() {
        return sharedPreferences.getString("accessToken", null);
    }

    public void remove() {
        editor.remove("accessToken");
        editor.apply();
    }
}
