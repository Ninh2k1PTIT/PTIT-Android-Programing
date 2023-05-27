package com.example.socialnetwork.Activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

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
    private ImageButton imageButtonComment, imageButtonBack;
    private ImageView imageViewPostReact;
    private TextView textViewTotalPostReact;
    private RelativeLayout loading;
    private CommentAdapter commentAdapter;
    private TokenManager tokenManager;
    private int page = 0;
    private int totalPage = 0;
    private boolean isLoading = false;
    private List<Comment> data = new ArrayList<>();
    private int postId;
    private int totalPostReact;
    private int position;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comment);
        getSupportActionBar().hide();
        tokenManager = new TokenManager(this);
        commentAdapter = new CommentAdapter(this);
        Intent intent = getIntent();
        postId = intent.getIntExtra("postId", 0);
        totalPostReact = intent.getIntExtra("totalPostReact", 0);
        position = intent.getIntExtra("position", 0);
        initView();
        initEventListener();
        getComments();
    }

    private void initView() {
        recyclerView = findViewById(R.id.recyclerViewComment);
        recyclerView.setAdapter(commentAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        editText = findViewById(R.id.editTextComment);
        imageButtonComment = findViewById(R.id.buttonComment);
        imageButtonBack = findViewById(R.id.buttonBack);
        loading = findViewById(R.id.loadingPanel);
        imageViewPostReact = findViewById(R.id.imageViewPostReact);
        textViewTotalPostReact = findViewById(R.id.textViewTotalPostReact);
        textViewTotalPostReact.setText(totalPostReact + "");
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
                        getComments();
                }
            }
        });

        imageButtonComment.setOnClickListener(new View.OnClickListener() {
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

        textViewTotalPostReact.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(CommentActivity.this, ReactActivity.class);
                intent.putExtra("postId", postId);
                startActivity(intent);
            }
        });

        imageButtonBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

    private void getComments() {
        if (page <= totalPage && !isLoading) {
            commentAdapter.addLoadingEffect();
            isLoading = true;
        }
        ApiUtils.getCommentService().getByPost("Bearer " + tokenManager.getToken(), postId, page, 10).enqueue(new Callback<PaginationResponse<Comment>>() {
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
        loading.setVisibility(View.VISIBLE);
        ApiUtils.getCommentService().create("Bearer " + tokenManager.getToken(), postId, comment).enqueue(new Callback<Comment>() {
            @Override
            public void onResponse(Call<Comment> call, Response<Comment> response) {
                loading.setVisibility(View.INVISIBLE);
                data.add(response.body());
                commentAdapter.setResources(data);
                editText.getText().clear();
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(getWindow().getDecorView().getWindowToken(), 0);
            }

            @Override
            public void onFailure(Call<Comment> call, Throwable t) {
                loading.setVisibility(View.INVISIBLE);
                Toast.makeText(getApplicationContext(), "Đã xảy ra lỗi. Vui lòng kiểm tra lại kết nối", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void finish() {
        Intent intent = new Intent();
        intent.putExtra("postId", postId);
        intent.putExtra("position", position);
        setResult(RESULT_OK, intent);
        super.finish();
        overridePendingTransition(R.anim.slide_stay, R.anim.slide_out_up);
    }
}