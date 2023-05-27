package com.example.socialnetwork.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.ImageButton;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.socialnetwork.Adapter.ReactAdapter;
import com.example.socialnetwork.Model.PaginationResponse;
import com.example.socialnetwork.Model.React;
import com.example.socialnetwork.R;
import com.example.socialnetwork.Service.ApiUtils;
import com.example.socialnetwork.Service.TokenManager;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ReactActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private ReactAdapter reactAdapter;
    private ImageButton buttonBack;
    private TokenManager tokenManager;
    private int page = 0;
    private int totalPage = 0;
    private List<React> data = new ArrayList<>();
    private boolean isLoading = false;
    private int commentId;
    private int postId;
    private Call<PaginationResponse<React>> call;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_react);
        getSupportActionBar().hide();
        tokenManager = new TokenManager(this);
        reactAdapter = new ReactAdapter(this);
        Intent intent = getIntent();
        commentId = intent.getIntExtra("commentId", 0);
        postId = intent.getIntExtra("postId", 0);
        if (commentId != 0)
            call = ApiUtils.getCommentService().getReactByComment("Bearer " + tokenManager.getToken(), commentId, page, 15);
        else
            call = ApiUtils.getPostService().getReactByPost("Bearer " + tokenManager.getToken(), postId, page, 15);
        initView();
        initEventListener();
        getReacts();
    }

    private void initView() {
        recyclerView = findViewById(R.id.recyclerViewReact);
        recyclerView.setAdapter(reactAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        buttonBack = findViewById(R.id.buttonBack);
    }

    private void initEventListener() {
        recyclerView.getViewTreeObserver().addOnScrollChangedListener(new ViewTreeObserver.OnScrollChangedListener() {
            @Override
            public void onScrollChanged() {
                View view = recyclerView.getChildAt(recyclerView.getChildCount() - 1);

                if (view != null) {
                    //view.getBottom() = chieu cao so voi canh tren recyclerview
                    //recyclerView.getHeight() = chieu cao 1 man hinh recycler view (co dinh)
                    int diff = (view.getBottom() - recyclerView.getHeight());
                    if (diff == 0 && page <= totalPage && !isLoading)
                        getReacts();
                }
            }
        });

        buttonBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

    private void getReacts() {
        if (page <= totalPage && !isLoading) {
            reactAdapter.addLoadingEffect();
            isLoading = true;
        }
        call.clone().enqueue(new Callback<PaginationResponse<React>>() {
            @Override
            public void onResponse(Call<PaginationResponse<React>> call, Response<PaginationResponse<React>> response) {
                if (isLoading) {
                    reactAdapter.removeLoadingEffect();
                    isLoading = false;

                }
                if (response.isSuccessful() && response.body() != null) {
                    List<React> list = response.body().getData();
                    data.addAll(list);
                    reactAdapter.setResources(data);
                    totalPage = response.body().getTotalPages();
                    page++;
                }
            }

            @Override
            public void onFailure(Call<PaginationResponse<React>> call, Throwable t) {

            }
        });
    }
}