package com.example.socialnetwork.Activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.socialnetwork.Model.GoogleLoginRequest;
import com.example.socialnetwork.Model.LoginRequest;
import com.example.socialnetwork.Model.LoginResponse;
import com.example.socialnetwork.R;
import com.example.socialnetwork.Service.ApiUtils;
import com.example.socialnetwork.Service.RequestCodeUtils;
import com.example.socialnetwork.Service.TokenManager;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;

import java.util.regex.Pattern;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginActivity extends AppCompatActivity {
    private TextInputLayout inputEmail, inputPassword;
    private Button buttonLogin, buttonSignup, buttonGoogle;
    private TokenManager tokenManager;
    private RelativeLayout loading;
    private static final String EMAIL_PATTERN = "^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@"
            + "[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";

    GoogleSignInOptions googleSignInOptions;
    GoogleSignInClient googleSignInClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        getSupportActionBar().hide();
        tokenManager = new TokenManager(this);
        googleSignInOptions = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                //Nhập Web OAuth 2.0 Client IDs trên https://console.cloud.google.com/apis/credentials
                .requestIdToken("xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx.apps.googleusercontent.com")
                .requestEmail()
                .build();
        googleSignInClient = GoogleSignIn.getClient(this, googleSignInOptions);

        initView();
        initEventListener();
    }

    private void initView() {
        inputEmail = findViewById(R.id.inputEmail);
        inputPassword = findViewById(R.id.inputPassword);
        buttonLogin = findViewById(R.id.buttonLogin);
        buttonSignup = findViewById(R.id.buttonSignup);
        buttonGoogle = findViewById(R.id.buttonGoogle);
        loading = findViewById(R.id.loadingPanel);
    }

    public void initEventListener() {
        buttonLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(view.getWindowToken(), 0);

                String email = inputEmail.getEditText().getText().toString();
                String password = inputPassword.getEditText().getText().toString();

                if (email.isEmpty()
                        || password.isEmpty()
                        || !Pattern.compile(EMAIL_PATTERN).matcher(email).matches()
                ) {
                    if (email.isEmpty())
                        inputEmail.setError("Không được để trống");
                    if (!Pattern.compile(EMAIL_PATTERN).matcher(email).matches())
                        inputEmail.setError("Email không đúng định dạng");
                    if (password.isEmpty())
                        inputPassword.setError("Không được để trống");
                } else {
                    loading.setVisibility(View.VISIBLE);
                    LoginRequest loginRequest = new LoginRequest();
                    loginRequest.setEmail(email);
                    loginRequest.setPassword(password);
                    ApiUtils.getAuthService().login(loginRequest).enqueue(new Callback<LoginResponse>() {
                        @Override
                        public void onResponse(Call<LoginResponse> call, Response<LoginResponse> response) {
                            loading.setVisibility(View.INVISIBLE);
                            if (response.code() == 200) {
                                Toast.makeText(getApplicationContext(), "Chào mừng " + response.body().getUsername(), Toast.LENGTH_SHORT).show();
                                tokenManager.save(response.body().getToken(), response.body().getId(), response.body().getUsername(), response.body().getPhoneNumber(), response.body().getEmail(), response.body().getAvatar(), response.body().getGender());
                                startActivity(new Intent(LoginActivity.this, MainActivity.class));
                                finish();
                            } else
                                Toast.makeText(getApplicationContext(), "Email hoặc mật khẩu chưa chính xác", Toast.LENGTH_SHORT).show();
                        }

                        @Override
                        public void onFailure(Call<LoginResponse> call, Throwable t) {
                            loading.setVisibility(View.INVISIBLE);
                            Toast.makeText(getApplicationContext(), "Đã xảy ra lỗi. Vui lòng thử lại", Toast.LENGTH_SHORT).show();
                        }
                    });
                }

            }
        });

        buttonSignup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(LoginActivity.this, SignupActivity.class));
            }
        });

        buttonGoogle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent signInIntent = googleSignInClient.getSignInIntent();
                startActivityForResult(signInIntent, RequestCodeUtils.LOGIN_GOOGLE);
            }
        });

        inputEmail.getEditText().addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                String s = charSequence.toString().trim();
                if (s.isEmpty())
                    inputEmail.setError("Không được để trống");
                else if (!Pattern.compile(EMAIL_PATTERN).matcher(s).matches())
                    inputEmail.setError("Email không đúng định dạng");
                else inputEmail.setError(null);
            }

            @Override
            public void afterTextChanged(Editable editable) {
            }
        });

        inputPassword.getEditText().addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                String s = charSequence.toString();
                if (s.isEmpty())
                    inputPassword.setError("Không được để trống");
                else inputPassword.setError(null);
            }

            @Override
            public void afterTextChanged(Editable editable) {
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RequestCodeUtils.LOGIN_GOOGLE) {
            loading.setVisibility(View.VISIBLE);
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                GoogleSignInAccount account = task.getResult(ApiException.class);
                GoogleLoginRequest googleLoginRequest = new GoogleLoginRequest();
                googleLoginRequest.setCredential(account.getIdToken());
                googleSignInClient.signOut();
                ApiUtils.getAuthService().googleSignIn(googleLoginRequest).enqueue(new Callback<LoginResponse>() {
                    @Override
                    public void onResponse(Call<LoginResponse> call, Response<LoginResponse> response) {
                        loading.setVisibility(View.INVISIBLE);
                        if (response.code() == 200) {
                            Toast.makeText(getApplicationContext(), "Chào mừng " + response.body().getUsername(), Toast.LENGTH_SHORT).show();
                            tokenManager.save(response.body().getToken(), response.body().getId(), response.body().getUsername(), response.body().getPhoneNumber(), response.body().getEmail(), response.body().getAvatar(), response.body().getGender());
                            startActivity(new Intent(LoginActivity.this, MainActivity.class));
                            finish();
                        } else {
                            Toast.makeText(getApplicationContext(), "Đã xảy ra lỗi. Vui lòng thử lại", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<LoginResponse> call, Throwable t) {
                        loading.setVisibility(View.INVISIBLE);
                        Toast.makeText(getApplicationContext(), "Đã xảy ra lỗi. Vui lòng thử lại", Toast.LENGTH_SHORT).show();
                    }
                });

            } catch (ApiException e) {
                loading.setVisibility(View.INVISIBLE);
            }
        }
    }
}