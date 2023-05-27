package com.example.socialnetwork.Service;

import com.example.socialnetwork.Model.Comment;
import com.example.socialnetwork.Model.PaginationResponse;
import com.example.socialnetwork.Model.React;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface CommentService {
    @GET("/post/{id}/comments?")
    Call<PaginationResponse<Comment>> getByPost(@Header("Authorization") String token, @Path("id") Integer id, @Query("page") Integer page, @Query("size") Integer size);

    @GET("/comment/{id}")
    Call<Comment> getById(@Header("Authorization") String token, @Path("id") Integer id);

    @POST("/post/{id}/comment")
    Call<Comment> create(@Header("Authorization") String token, @Path("id") Integer id, @Body Comment comment);

    @POST("/comment/{id}/react")
    Call<Boolean> react(@Header("Authorization") String token, @Path("id") Integer id);

    @GET("/comment/{id}/reacts")
    Call<PaginationResponse<React>> getReactByComment(@Header("Authorization") String token, @Path("id") Integer id, @Query("page") Integer page, @Query("size") Integer size);

    @DELETE("comment/{id}")
    Call<Boolean> delete(@Header("Authorization") String token, @Path("id") Integer id);
}
