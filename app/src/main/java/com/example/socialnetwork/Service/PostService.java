package com.example.socialnetwork.Service;

import com.example.socialnetwork.Model.PaginationResponse;
import com.example.socialnetwork.Model.Post;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface PostService {
    @GET("/posts?")
    Call<PaginationResponse<Post>> getAll(@Header("Authorization") String token, @Query("page") Integer page, @Query("size") Integer size);

    @GET("/post/{id}")
    Call<Post> getById(@Header("Authorization") String token, @Path("id") Integer id);

    @POST("/post")
    Call<Post> create(@Header("Authorization") String token, @Body Post post);

    @POST("/post/{id}/react")
    Call<Boolean> react(@Header("Authorization") String token, @Path("id") Integer id);
}
