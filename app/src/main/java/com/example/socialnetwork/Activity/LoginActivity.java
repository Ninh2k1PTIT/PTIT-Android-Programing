package com.example.socialnetwork.Activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.socialnetwork.Model.JwtResponse;
import com.example.socialnetwork.Model.LoginRequest;
import com.example.socialnetwork.R;
import com.example.socialnetwork.Service.ApiUtils;
import com.example.socialnetwork.Service.TokenManager;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginActivity extends AppCompatActivity {
    EditText editTextEmail, editTextPassword;
    Button buttonLogin, buttonSignup;
    TokenManager tokenManager;
    RelativeLayout loading;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        getSupportActionBar().hide();
        tokenManager = new TokenManager(this);
        initView();
        initEventListener();
    }

    private void initView() {
        editTextEmail = findViewById(R.id.editTextEmail);
        editTextPassword = findViewById(R.id.editTextPassword);
        buttonLogin = findViewById(R.id.buttonLogin);
        buttonSignup = findViewById(R.id.buttonSignup);
        loading = findViewById(R.id.loadingPanel);
    }

    public void initEventListener() {
        buttonLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                loading.setVisibility(View.VISIBLE);
                setFocusable(false);
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
                LoginRequest loginRequest = new LoginRequest();
                String email = editTextEmail.getText().toString();
                String password = editTextPassword.getText().toString();
                loginRequest.setEmail(email);
                loginRequest.setPassword(password);
                ApiUtils.getAuthService().login(loginRequest).enqueue(new Callback<JwtResponse>() {
                    @Override
                    public void onResponse(Call<JwtResponse> call, Response<JwtResponse> response) {
                        loading.setVisibility(View.INVISIBLE);
                        setFocusable(true);
                        if (response.code() == 200) {
                            tokenManager.save(response.body().getToken());
                            startActivity(new Intent(LoginActivity.this, MainActivity.class));
                        } else
                            Toast.makeText(getApplicationContext(), "Email hoặc mật khẩu chưa chính xác!", Toast.LENGTH_SHORT).show();

                    }

                    @Override
                    public void onFailure(Call<JwtResponse> call, Throwable t) {
                        loading.setVisibility(View.INVISIBLE);
                        setFocusable(true);
                        Toast.makeText(getApplicationContext(), "Mất kết nối!", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }

    public void setFocusable(Boolean focusable) {
        buttonSignup.setFocusable(focusable);
        buttonLogin.setFocusable(focusable);
        editTextEmail.setFocusable(focusable);
        editTextEmail.setFocusableInTouchMode(focusable);
        editTextPassword.setFocusable(focusable);
        editTextPassword.setFocusableInTouchMode(focusable);
    }
}