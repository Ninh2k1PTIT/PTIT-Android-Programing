package com.example.socialnetwork.Activity;

import android.content.Context;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.socialnetwork.Model.SignupRequest;
import com.example.socialnetwork.Model.SignupResponse;
import com.example.socialnetwork.R;
import com.example.socialnetwork.Service.ApiUtils;
import com.google.android.material.textfield.TextInputLayout;
import com.google.gson.GsonBuilder;

import java.io.IOException;
import java.util.regex.Pattern;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SignupActivity extends AppCompatActivity {
    private TextInputLayout inputEmail, inputUsername, inputPassword, inputConfirmPassword, inputPhone;
    private Button buttonSignup, buttonLogin;
    private RadioButton radioButtonMale, radioButtonFemale;
    private RelativeLayout loading;
    private static final String EMAIL_PATTERN = "^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@"
            + "[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);
        getSupportActionBar().hide();
        initView();
        initEventListener();
    }

    private void initView() {
        inputEmail = findViewById(R.id.inputEmail);
        inputUsername = findViewById(R.id.inputUsername);
        inputPassword = findViewById(R.id.inputPassword);
        inputConfirmPassword = findViewById(R.id.inputConfirmPassword);
        inputPhone = findViewById(R.id.inputPhone);
        buttonSignup = findViewById(R.id.buttonSignup);
        buttonLogin = findViewById(R.id.buttonLogin);
        radioButtonMale = findViewById(R.id.radioButtonMale);
        radioButtonFemale = findViewById(R.id.radioButtonFemale);
        loading = findViewById(R.id.loadingPanel);
    }

    private void initEventListener() {
        buttonLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        buttonSignup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(view.getWindowToken(), 0);

                String email = inputEmail.getEditText().getText().toString().trim();
                String username = inputUsername.getEditText().getText().toString().trim();
                String password = inputPassword.getEditText().getText().toString();
                String confirmPassword = inputConfirmPassword.getEditText().getText().toString();
                String phone = inputPhone.getEditText().getText().toString().trim();
                if (email.isEmpty()
                        || username.isEmpty()
                        || password.isEmpty()
                        || confirmPassword.isEmpty()
                        || !Pattern.compile(EMAIL_PATTERN).matcher(email).matches()
                        || !password.equals(confirmPassword)) {
                    if (email.isEmpty())
                        inputEmail.setError("Không được để trống");
                    if (!Pattern.compile(EMAIL_PATTERN).matcher(email).matches())
                        inputEmail.setError("Email không đúng định dạng");
                    if (username.isEmpty())
                        inputUsername.setError("Không được để trống");
                    if (password.isEmpty())
                        inputPassword.setError("Không được để trống");
                    if (confirmPassword.isEmpty())
                        inputConfirmPassword.setError("Không được để trống");
                    if (!password.equals(confirmPassword))
                        inputConfirmPassword.setError("Chưa khớp với Mật khẩu");
                } else {
                    loading.setVisibility(View.VISIBLE);
                    SignupRequest signupRequest = new SignupRequest();
                    signupRequest.setEmail(email);
                    signupRequest.setUsername(username);
                    signupRequest.setPassword(password);
                    signupRequest.setPhoneNumber(phone);
                    if (radioButtonMale.isChecked())
                        signupRequest.setGender("MALE");
                    else signupRequest.setGender("FEMALE");
                    ApiUtils.getAuthService().signup(signupRequest).enqueue(new Callback<SignupResponse>() {
                        @Override
                        public void onResponse(Call<SignupResponse> call, Response<SignupResponse> response) {
                            loading.setVisibility(View.INVISIBLE);
                            if (response.code() == 200) {
                                Toast.makeText(getApplicationContext(), response.body().getMessage(), Toast.LENGTH_SHORT).show();
                                finish();
                            } else {
                                try {
                                    SignupResponse signupResponse = new GsonBuilder().create().fromJson(response.errorBody().string(), SignupResponse.class);
                                    Toast.makeText(getApplicationContext(), signupResponse.getMessage(), Toast.LENGTH_SHORT).show();
                                } catch (IOException e) {
                                    throw new RuntimeException(e);
                                }
                            }
                        }

                        @Override
                        public void onFailure(Call<SignupResponse> call, Throwable t) {
                            loading.setVisibility(View.INVISIBLE);
                            Toast.makeText(getApplicationContext(), "Đã xảy ra lỗi. Vui lòng thử lại", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
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

        inputUsername.getEditText().addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                String s = charSequence.toString().trim();
                if (s.isEmpty())
                    inputUsername.setError("Không được để trống");
                else inputUsername.setError(null);
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

        inputConfirmPassword.getEditText().addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                String s = charSequence.toString();
                if (s.isEmpty())
                    inputConfirmPassword.setError("Không được để trống");
                else if (!s.equals(inputPassword.getEditText().getText().toString()))
                    inputConfirmPassword.setError("Chưa khớp với Mật khẩu");
                else inputConfirmPassword.setError(null);
            }

            @Override
            public void afterTextChanged(Editable editable) {
            }
        });
    }
}