package com.example.socialnetwork.Activity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.widget.ViewPager2;

import com.example.socialnetwork.Adapter.ViewPager2Adapter;
import com.example.socialnetwork.Fragment.InfoFragment;
import com.example.socialnetwork.Fragment.InsiderNewsFragment;
import com.example.socialnetwork.Model.User;
import com.example.socialnetwork.R;
import com.example.socialnetwork.Service.ApiUtils;
import com.example.socialnetwork.Service.RequestCodeUtils;
import com.example.socialnetwork.Service.TokenManager;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ProfileActivity extends AppCompatActivity {
    private ViewPager2 viewPager;
    private ViewPager2Adapter viewPager2Adapter;
    private Button buttonCreatePost, buttonUpdateAvatar;
    private ImageButton buttonBack;
    private ImageView imageViewAvatar;
    public TextView textViewUsername;
    private TabLayout tabLayout;
    private TokenManager tokenManager;
    InsiderNewsFragment insiderNewsFragment = new InsiderNewsFragment();
    private FirebaseStorage firebaseStorage;
    private StorageReference storageReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        getSupportActionBar().hide();
        firebaseStorage = FirebaseStorage.getInstance();
        storageReference = firebaseStorage.getReference();
        tokenManager = new TokenManager(this);
        initView();
        initEventListener();
    }

    private void initView() {
        buttonBack = findViewById(R.id.buttonBack);
        textViewUsername = findViewById(R.id.textViewUsername);
        imageViewAvatar = findViewById(R.id.imageViewAvatar);
        buttonCreatePost = findViewById(R.id.buttonCreatePost);
        buttonUpdateAvatar = findViewById(R.id.buttonUpdateAvatar);
        viewPager = findViewById(R.id.viewPager);
        viewPager2Adapter = new ViewPager2Adapter(getSupportFragmentManager(), getLifecycle());
        List<Fragment> fragments = new ArrayList<>();
        fragments.add(insiderNewsFragment);
        fragments.add(new InfoFragment());
        String[] name = new String[]{"BÀI VIẾT", "HỒ SƠ"};
        viewPager2Adapter.setFragmentList(fragments);
        viewPager.setAdapter(viewPager2Adapter);
        tabLayout = findViewById(R.id.tabLayout);
        new TabLayoutMediator(tabLayout, viewPager,
                (tab, position) -> tab.setText(name[position])
        ).attach();

        Intent intent = getIntent();
        int userId = intent.getIntExtra("userId", 0);
        if (userId == 0 || userId == tokenManager.getUserId()) {
            textViewUsername.setText(tokenManager.getUsername());
            Picasso.get().load(tokenManager.getAvatar()).into(imageViewAvatar);
        } else {
            buttonCreatePost.getLayoutParams().height = 0;
            buttonUpdateAvatar.getLayoutParams().height = 0;
            textViewUsername.setText(intent.getStringExtra("username"));
            Picasso.get().load(intent.getStringExtra("avatar")).into(imageViewAvatar);
        }
    }

    private void initEventListener() {
        buttonCreatePost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ProfileActivity.this, UploadActivity.class);
                startActivityForResult(intent, RequestCodeUtils.CREATE_POST);
                overridePendingTransition(R.anim.slide_in_up, R.anim.slide_stay);
            }
        });

        buttonUpdateAvatar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
                startActivityForResult(Intent.createChooser(intent, "Select Image from here..."), RequestCodeUtils.UPDATE_AVATAR);
            }
        });

        buttonBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RequestCodeUtils.CREATE_POST && resultCode == RESULT_OK)
            insiderNewsFragment.refreshPosts();

        else if (requestCode == RequestCodeUtils.UPDATE_AVATAR && resultCode == RESULT_OK && data != null) {
            if (data.getClipData() != null)
                updateAvatar(data.getClipData().getItemAt(0).getUri());
            else if (data.getData() != null)
                updateAvatar(data.getData());
        }
    }

    private void updateAvatar(Uri uri) {
        StorageReference uploadTask = storageReference.child(UUID.randomUUID().toString());
        uploadTask.putFile(uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                uploadTask.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        User user = new User();
                        user.setAvatar(uri.toString());
                        ApiUtils.getUserService().updateAvatar("Bearer " + tokenManager.getToken(), user).enqueue(new Callback<User>() {
                            @Override
                            public void onResponse(Call<User> call, Response<User> response) {
                                tokenManager.setAvatar(response.body().getAvatar());
                                Picasso.get().load(tokenManager.getAvatar()).into(imageViewAvatar);
                                insiderNewsFragment.refreshPosts();
                                Toast.makeText(getApplicationContext(), "Đổi ảnh đại diện thành công", Toast.LENGTH_SHORT).show();
                            }

                            @Override
                            public void onFailure(Call<User> call, Throwable t) {
                            }
                        });
                    }
                });
            }
        });
    }
}