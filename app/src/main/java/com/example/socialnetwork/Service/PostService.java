package com.example.socialnetwork.Service;

import com.example.socialnetwork.Model.PaginationResponse;
import com.example.socialnetwork.Model.Post;
import com.example.socialnetwork.Model.React;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface PostService {
    @GET("/posts?")
    Call<PaginationResponse<Post>> getAll(@Header("Authorization") String token, @Query("page") Integer page, @Query("size") Integer size, @Query("content") String content, @Query("sort") String sort);

    @GET("/my-posts?")
    Call<PaginationResponse<Post>> getByCurrentUser(@Header("Authorization") String token, @Query("page") Integer page, @Query("size") Integer size);

    @GET("/friend/{id}/posts?")
    Call<PaginationResponse<Post>> getByUserId(@Header("Authorization") String token, @Path("id") Integer userId, @Query("page") Integer page, @Query("size") Integer size);

    @GET("/post/{id}")
    Call<Post> getById(@Header("Authorization") String token, @Path("id") Integer id);

    @POST("/post")
    Call<Post> create(@Header("Authorization") String token, @Body Post post);

    @POST("/post/{id}/react")
    Call<Boolean> react(@Header("Authorization") String token, @Path("id") Integer id);

    @GET("/post/{id}/reacts")
    Call<PaginationResponse<React>> getReactByPost(@Header("Authorization") String token, @Path("id") Integer id, @Query("page") Integer page, @Query("size") Integer size);

    @DELETE("post/{id}")
    Call<Boolean> delete(@Header("Authorization") String token, @Path("id") Integer id);

    @PUT("post/{id}")
    Call<Post> update(@Header("Authorization") String token, @Body Post post, @Path("id") Integer id);

}
