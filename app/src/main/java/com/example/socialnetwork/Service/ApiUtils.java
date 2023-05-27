package com.example.socialnetwork.Service;

public class ApiUtils {
    public static PostService getPostService() {
        return ApiClient.getClient().create(PostService.class);
    }

    public static AuthService getAuthService() {
        return ApiClient.getClient().create(AuthService.class);
    }

    public static CommentService getCommentService() {
        return ApiClient.getClient().create(CommentService.class);
    }

    public static UserService getUserService() {
        return ApiClient.getClient().create(UserService.class);
    }
}
