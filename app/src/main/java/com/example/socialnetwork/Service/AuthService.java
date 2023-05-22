package com.example.socialnetwork.Service;

import com.example.socialnetwork.Model.JwtResponse;
import com.example.socialnetwork.Model.LoginRequest;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface AuthService {
    @POST("/auth/login")
    Call<JwtResponse> login(@Body LoginRequest loginRequest);
}
