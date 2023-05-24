package com.example.socialnetwork.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.widget.ViewPager2;

import com.example.socialnetwork.Adapter.MainViewPagerAdapter;
import com.example.socialnetwork.R;
import com.example.socialnetwork.Service.TokenManager;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationBarView;

public class MainActivity extends AppCompatActivity {
    private ViewPager2 viewPager;
    private BottomNavigationView bottomNavigationView;
    private MainViewPagerAdapter mainViewPagerAdapter;
    private FloatingActionButton openUploadButton;
    private TokenManager tokenManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();
        tokenManager = new TokenManager(this);
        if (tokenManager.get() == null) {
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
        mainViewPagerAdapter = new MainViewPagerAdapter(getSupportFragmentManager(), getLifecycle());
        viewPager.setAdapter(mainViewPagerAdapter);
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
                        bottomNavigationView.getMenu().findItem(R.id.mContact).setChecked(true);
                        break;
                    case 2:
                        bottomNavigationView.getMenu().findItem(R.id.mNotification).setChecked(true);
                        break;
                    case 3:
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
                    case R.id.mContact:
                        viewPager.setCurrentItem(1);
                        break;
                    case R.id.mNotification:
                        viewPager.setCurrentItem(2);
                        break;
                    case R.id.mAccount:
                        viewPager.setCurrentItem(3);
                        break;
                }
                return true;
            }
        });

        openUploadButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this, UploadActivity.class));
                overridePendingTransition(R.anim.slide_in_up, R.anim.slide_stay);
            }
        });
    }
}