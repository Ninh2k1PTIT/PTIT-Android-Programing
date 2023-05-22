package com.example.socialnetwork.Activity;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import com.example.socialnetwork.Adapter.AudienceAdapter;
import com.example.socialnetwork.Adapter.ImageViewAdapter;
import com.example.socialnetwork.Model.EAudience;
import com.example.socialnetwork.Model.Photo;
import com.example.socialnetwork.Model.Post;
import com.example.socialnetwork.R;
import com.example.socialnetwork.Service.ApiClient;
import com.example.socialnetwork.Service.ApiUtils;
import com.example.socialnetwork.Service.TokenManager;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.ArrayList;
import java.util.List;
import java.util.Observable;
import java.util.UUID;
import java.util.stream.Collectors;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class UploadActivity extends AppCompatActivity {
    private Button buttonImageUpload, buttonSubmit;
    private ImageButton buttonBack;
    private Spinner spinner;
    private EditText editText;
    private ViewPager viewPager;
    private TabLayout tabLayout;
    RelativeLayout loading;
    private AudienceAdapter audienceAdapter;
    private ImageViewAdapter imageViewAdapter;
    private FirebaseStorage firebaseStorage;
    private StorageReference storageReference;
    private TokenManager tokenManager;
    int totalPhoto = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload);
        getSupportActionBar().hide();
        tokenManager = new TokenManager(this);
        firebaseStorage = FirebaseStorage.getInstance();
        storageReference = firebaseStorage.getReference();
        audienceAdapter = new AudienceAdapter(this);
        imageViewAdapter = new ImageViewAdapter(this, new ArrayList<>(), 0);
        initView();
        initEventListener();
    }

    public void initView() {
        editText = findViewById(R.id.editTextContent);
        buttonImageUpload = findViewById(R.id.buttonImageUpload);
        buttonSubmit = findViewById(R.id.buttonSubmit);
        buttonBack = findViewById(R.id.buttonBack);
        spinner = findViewById(R.id.spinnerAudience);
        spinner.setAdapter(audienceAdapter);
        viewPager = findViewById(R.id.viewPagerImageUpload);
        viewPager.setAdapter(imageViewAdapter);
        tabLayout = findViewById(R.id.tabDots);
        tabLayout.setupWithViewPager(viewPager, true);
        loading = findViewById(R.id.loadingPanel);
    }

    public void initEventListener() {
        buttonImageUpload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
                startActivityForResult(
                        Intent.createChooser(
                                intent,
                                "Select Image from here..."),
                        22);
            }
        });

        buttonBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        buttonSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (editText.getText().toString().trim().isEmpty() && imageViewAdapter.getResources().isEmpty()) {
                    Toast.makeText(getApplicationContext(), "Cần thêm nội dung hoặc ảnh", Toast.LENGTH_SHORT).show();
                    return;
                }
                loading.setVisibility(View.VISIBLE);
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
                Post post = new Post();
                post.setContent(editText.getText().toString().trim());
                post.setAudience(EAudience.valueOf(spinner.getSelectedItem().toString()));
                totalPhoto = imageViewAdapter.getCount();

                if (totalPhoto == 0)
                    upload(post);
                else
                    for (int i = 0; i < imageViewAdapter.getCount(); i++) {
                        StorageReference uploadTask = storageReference.child(UUID.randomUUID().toString());
                        uploadTask.putFile(Uri.parse(imageViewAdapter.getItem(i))).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                uploadTask.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                    @Override
                                    public void onSuccess(Uri uri) {
                                        totalPhoto--;
                                        Photo photo = new Photo();
                                        photo.setContent(String.valueOf(uri));
                                        post.getPhotos().add(photo);
                                        if (totalPhoto == 0)
                                            upload(post);
                                    }
                                });
                            }
                        });
                    }

            }
        });
    }

    public void upload(Post post) {
        ApiUtils.getPostService().create("Bearer " + tokenManager.get(), post).enqueue(new Callback<Post>() {
            @Override
            public void onResponse(Call<Post> call, Response<Post> response) {
                loading.setVisibility(View.INVISIBLE);
                Toast.makeText(getApplicationContext(), "Đăng bài thành công", Toast.LENGTH_SHORT).show();
                finish();
            }

            @Override
            public void onFailure(Call<Post> call, Throwable t) {
                loading.setVisibility(View.INVISIBLE);
                Toast.makeText(getApplicationContext(), "Đã xảy ra lỗi. Vui lòng kiểm tra lại kết nối", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 22 && resultCode == RESULT_OK && data != null) {
            if (data.getClipData() != null)
                for (int i = 0; i < data.getClipData().getItemCount(); i++) {
                    imageViewAdapter.addItem(data.getClipData().getItemAt(i).getUri().toString());
                }
            else if (data.getData() != null)
                imageViewAdapter.addItem(data.getData().toString());
        }
    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(R.anim.slide_stay, R.anim.slide_out_up);
    }
}