package com.example.socialnetwork.Service;

import com.example.socialnetwork.Model.User;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.PUT;
import retrofit2.http.Path;

public interface UserService {
    @PUT("/updateAvatar")
    Call<User> updateAvatar(@Header("Authorization") String token, @Body User user);

    @PUT("/updateBasicInfo")
    Call<User> updateInfo(@Header("Authorization") String token, @Body User user);

    @GET("/user/{id}")
    Call<User> getById(@Header("Authorization") String token, @Path("id") Integer id);
}
