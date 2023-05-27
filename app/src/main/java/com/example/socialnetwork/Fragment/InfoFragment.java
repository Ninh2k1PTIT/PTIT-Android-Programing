package com.example.socialnetwork.Fragment;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.socialnetwork.Activity.ProfileActivity;
import com.example.socialnetwork.Model.User;
import com.example.socialnetwork.R;
import com.example.socialnetwork.Service.ApiUtils;
import com.example.socialnetwork.Service.TokenManager;
import com.google.android.material.textfield.TextInputLayout;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class InfoFragment extends Fragment {
    private TextInputLayout inputEmail, inputUsername, inputPhone;
    private Button buttonSave;
    private RadioButton radioButtonMale, radioButtonFemale;
    private TokenManager tokenManager;

    public InfoFragment() {
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        tokenManager = new TokenManager(getActivity());
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initView(view);
        initEventListener();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_info, container, false);
    }

    private void initView(View view) {
        inputEmail = view.findViewById(R.id.inputEmail);
        inputUsername = view.findViewById(R.id.inputUsername);
        inputPhone = view.findViewById(R.id.inputPhone);
        buttonSave = view.findViewById(R.id.buttonSave);
        radioButtonMale = view.findViewById(R.id.radioButtonMale);
        radioButtonFemale = view.findViewById(R.id.radioButtonFemale);


        Intent intent = getActivity().getIntent();
        int userId = intent.getIntExtra("userId", 0);
        if (userId == 0 || userId == tokenManager.getUserId()) {
            inputEmail.getEditText().setText(tokenManager.getEmail());
            inputUsername.getEditText().setText(tokenManager.getUsername());
            inputPhone.getEditText().setText(tokenManager.getPhoneNumber());
            if (tokenManager.getGender().equals("FEMALE"))
                radioButtonFemale.setChecked(true);
        } else {
            ApiUtils.getUserService().getById("Bearer " + tokenManager.getToken(), userId).enqueue(new Callback<User>() {
                @Override
                public void onResponse(Call<User> call, Response<User> response) {
                    User user = response.body();
                    inputEmail.getEditText().setText(user.getEmail());
                    inputUsername.getEditText().setText(user.getUsername());
                    inputPhone.getEditText().setText(user.getPhoneNumber());
                    if (tokenManager.getGender().equals("FEMALE"))
                        radioButtonFemale.setChecked(true);
                    inputUsername.getEditText().setInputType(InputType.TYPE_NULL);
                    inputPhone.getEditText().setInputType(InputType.TYPE_NULL);
                    radioButtonMale.setEnabled(false);
                    radioButtonFemale.setEnabled(false);
                    buttonSave.setVisibility(View.INVISIBLE);
                }

                @Override
                public void onFailure(Call<User> call, Throwable t) {

                }
            });
        }
    }

    private void initEventListener() {
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

        buttonSave.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
                String username = inputUsername.getEditText().getText().toString();
                String phone = inputPhone.getEditText().getText().toString();

                if (username.isEmpty()) {
                    inputUsername.setError("Không được để trống");
                } else {
                    User user = new User();
                    user.setUsername(username);
                    user.setPhoneNumber(phone);
                    if (radioButtonMale.isChecked())
                        user.setGender("MALE");
                    else user.setGender("FEMALE");

                    ApiUtils.getUserService().updateInfo("Bearer " + tokenManager.getToken(), user).enqueue(new Callback<User>() {
                        @Override
                        public void onResponse(Call<User> call, Response<User> response) {
                            if (response.code() == 200) {
                                Toast.makeText(getActivity(), "Cập nhật thông tin thành công", Toast.LENGTH_SHORT).show();
                                tokenManager.setUsername(response.body().getUsername());
                                tokenManager.setPhoneNumber(response.body().getPhoneNumber());
                                tokenManager.setGender(response.body().getGender());
                                ((ProfileActivity) getActivity()).textViewUsername.setText(tokenManager.getUsername());

                            } else
                                Toast.makeText(getActivity(), "Đã xảy ra lỗi.Hãy thử lại", Toast.LENGTH_SHORT).show();
                        }

                        @Override
                        public void onFailure(Call<User> call, Throwable t) {
                            Toast.makeText(getActivity(), "Đã xảy ra lỗi. Vui lòng thử lại", Toast.LENGTH_SHORT).show();
                        }
                    });
                }

            }
        });
    }
}