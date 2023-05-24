package com.example.socialnetwork.Service;

import com.example.socialnetwork.Model.Comment;
import com.example.socialnetwork.Model.PaginationResponse;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface CommentService {
    @GET("/post/{id}/comments?")
    Call<PaginationResponse<Comment>> getByPost(@Header("Authorization") String token, @Path("id") Integer id, @Query("page") Integer page, @Query("size") Integer size);

    @POST("/post/{id}/comment")
    Call<Comment> create(@Header("Authorization") String token, @Path("id") Integer id, @Body Comment comment);
}
