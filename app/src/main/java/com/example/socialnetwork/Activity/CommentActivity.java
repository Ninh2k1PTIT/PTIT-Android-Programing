package com.example.socialnetwork.Activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.socialnetwork.Adapter.CommentAdapter;
import com.example.socialnetwork.Model.Comment;
import com.example.socialnetwork.Model.PaginationResponse;
import com.example.socialnetwork.R;
import com.example.socialnetwork.Service.ApiUtils;
import com.example.socialnetwork.Service.TokenManager;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CommentActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private EditText editText;
    private ImageButton imageButton;
    private CommentAdapter commentAdapter;
    private TokenManager tokenManager;
    private int page = 0;
    private int totalPage = 0;
    private boolean isLoading = false;
    private List<Comment> data = new ArrayList<>();
    private int postId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comment);
        getSupportActionBar().hide();
        tokenManager = new TokenManager(this);
        commentAdapter = new CommentAdapter(this);
        Intent intent = getIntent();
        postId = intent.getIntExtra("postId", 0);
        initView();
        initEventListener();
        getComments();
    }

    private void initView() {
        recyclerView = findViewById(R.id.recyclerViewComment);
        recyclerView.setAdapter(commentAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        editText = findViewById(R.id.editTextComment);
        imageButton = findViewById(R.id.imageButtonComment);
    }

    private void initEventListener() {
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if (!recyclerView.canScrollVertically(1) && newState == RecyclerView.SCROLL_STATE_IDLE)
                    if (page <= totalPage)
                        getComments();
            }
        });

        imageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String s = editText.getText().toString().trim();
                if (s.isEmpty())
                    Toast.makeText(getApplicationContext(), "Cần nhập nội dung", Toast.LENGTH_SHORT).show();
                else {
                    Comment comment = new Comment();
                    comment.setContent(s);
                    upload(comment);
                }
            }
        });
    }

    private void getComments() {
        if (page <= totalPage && !isLoading) {
            commentAdapter.addLoadingEffect();
            isLoading = true;
        }
        ApiUtils.getCommentService().getByPost("Bearer " + tokenManager.get(), postId, page, 3).enqueue(new Callback<PaginationResponse<Comment>>() {
            @Override
            public void onResponse(Call<PaginationResponse<Comment>> call, Response<PaginationResponse<Comment>> response) {
                if (isLoading) {
                    commentAdapter.removeLoadingEffect();
                    isLoading = false;

                }
                if (response.isSuccessful() && response.body() != null) {
                    List<Comment> list = response.body().getData();
                    data.addAll(list);
                    commentAdapter.setResources(data);
                    totalPage = response.body().getTotalPages();
                    page++;
                }
            }

            @Override
            public void onFailure(Call<PaginationResponse<Comment>> call, Throwable t) {

            }
        });
    }

    private void upload(Comment comment) {
        ApiUtils.getCommentService().create("Bearer " + tokenManager.get(), postId, comment).enqueue(new Callback<Comment>() {
            @Override
            public void onResponse(Call<Comment> call, Response<Comment> response) {
                data.add(response.body());
                commentAdapter.setResources(data);
                editText.getText().clear();
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(getWindow().getDecorView().getWindowToken(), 0);
            }

            @Override
            public void onFailure(Call<Comment> call, Throwable t) {
                Toast.makeText(getApplicationContext(), "Đã xảy ra lỗi. Vui lòng kiểm tra lại kết nối", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(R.anim.slide_stay, R.anim.slide_out_up);
    }
}