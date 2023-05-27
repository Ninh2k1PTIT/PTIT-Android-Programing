package com.example.socialnetwork.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.widget.ViewPager2;

import com.example.socialnetwork.Adapter.ViewPager2Adapter;
import com.example.socialnetwork.Fragment.AccountFragment;
import com.example.socialnetwork.Fragment.NewsFragment;
import com.example.socialnetwork.R;
import com.example.socialnetwork.Service.RequestCodeUtils;
import com.example.socialnetwork.Service.TokenManager;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationBarView;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private ViewPager2 viewPager;
    private BottomNavigationView bottomNavigationView;
    private ViewPager2Adapter viewPager2Adapter;
    private FloatingActionButton openUploadButton;
    private TokenManager tokenManager;
    private NewsFragment newsFragment = new NewsFragment();
    private AccountFragment accountFragment = new AccountFragment();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();
        tokenManager = new TokenManager(this);
        if (tokenManager.getToken() == null) {
            startActivity(new Intent(MainActivity.this, LoginActivity.class));
            finish();
        } else {
            setContentView(R.layout.activity_main);
            initView();
            initEventListener();
        }

    }

    private void initView() {
        viewPager = findViewById(R.id.main_view_pager);
        bottomNavigationView = findViewById(R.id.navigation);
        viewPager2Adapter = new ViewPager2Adapter(getSupportFragmentManager(), getLifecycle());
        List<Fragment> fragments = new ArrayList<>();
        fragments.add(newsFragment);
        fragments.add(accountFragment);
        viewPager2Adapter.setFragmentList(fragments);
        viewPager.setAdapter(viewPager2Adapter);
        openUploadButton = findViewById(R.id.create_post_button);
    }

    private void initEventListener() {
        viewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                super.onPageScrolled(position, positionOffset, positionOffsetPixels);
            }

            @Override
            public void onPageSelected(int position) {
                switch (position) {
                    case 0:
                        bottomNavigationView.getMenu().findItem(R.id.mNews).setChecked(true);
                        break;
                    case 1:
                        bottomNavigationView.getMenu().findItem(R.id.mAccount).setChecked(true);
                        break;
                }
            }
        });

        bottomNavigationView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.mNews:
                        viewPager.setCurrentItem(0);
                        break;
                    case R.id.mAccount:
                        viewPager.setCurrentItem(1);
                        break;
                }
                return true;
            }
        });

        openUploadButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, UploadActivity.class);
                startActivityForResult(intent, RequestCodeUtils.CREATE_POST);
                overridePendingTransition(R.anim.slide_in_up, R.anim.slide_stay);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RequestCodeUtils.CREATE_POST && resultCode == RESULT_OK)
            newsFragment.refreshPosts();
        else if (requestCode == RequestCodeUtils.UPDATE_COMMENT && resultCode == RESULT_OK)
            newsFragment.refreshPostInPosition(data.getIntExtra("postId", 0), data.getIntExtra("position", 0));
    }
}