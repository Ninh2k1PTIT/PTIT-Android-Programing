package com.example.socialnetwork.Service;

import com.example.socialnetwork.Model.GoogleLoginRequest;
import com.example.socialnetwork.Model.LoginResponse;
import com.example.socialnetwork.Model.LoginRequest;
import com.example.socialnetwork.Model.SignupRequest;
import com.example.socialnetwork.Model.SignupResponse;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface AuthService {
    @POST("/auth/login")
    Call<LoginResponse> login(@Body LoginRequest loginRequest);

    @POST("/auth/signup")
    Call<SignupResponse> signup(@Body SignupRequest signupRequest);

    @POST("/auth/googleSignIn")
    Call<LoginResponse> googleSignIn(@Body GoogleLoginRequest signupRequest);
}
