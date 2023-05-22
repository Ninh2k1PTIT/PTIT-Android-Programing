package com.example.socialnetwork.Service;

public class ApiUtils {
    public static PostService getPostService() {
        return ApiClient.getClient().create(PostService.class);
    }

    public static AuthService getAuthService() {
        return ApiClient.getClient().create(AuthService.class);
    }
}
