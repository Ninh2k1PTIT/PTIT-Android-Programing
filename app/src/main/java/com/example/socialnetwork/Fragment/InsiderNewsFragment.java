package com.example.socialnetwork.Fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.widget.NestedScrollView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.socialnetwork.Adapter.PostAdapter;
import com.example.socialnetwork.Model.PaginationResponse;
import com.example.socialnetwork.Model.Post;
import com.example.socialnetwork.R;
import com.example.socialnetwork.Service.ApiUtils;
import com.example.socialnetwork.Service.TokenManager;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class InsiderNewsFragment extends Fragment {
    private RecyclerView recyclerView;
    private PostAdapter postAdapter;
    private NestedScrollView nestedScrollView;
    private TokenManager tokenManager;
    private int page = 0;
    private int totalPage = 0;
    private boolean isLoading = false;
    private List<Post> data = new ArrayList<>();
    Call<PaginationResponse<Post>> call;

    public InsiderNewsFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        tokenManager = new TokenManager(getActivity());
        postAdapter = new PostAdapter(getActivity());
        Intent intent = getActivity().getIntent();
        int userId = intent.getIntExtra("userId", 0);
        if (userId == 0 || userId == tokenManager.getUserId())
            call = ApiUtils.getPostService().getByCurrentUser("Bearer " + tokenManager.getToken(), page, 10);
        else
            call = ApiUtils.getPostService().getByUserId("Bearer " + tokenManager.getToken(), userId, page, 10);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_insider_news, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initView(view);
        initEventListener();
        getPosts();
    }

    private void initView(View view) {
        recyclerView = view.findViewById(R.id.recyclerViewPost);
        recyclerView.setAdapter(postAdapter);
        LinearLayoutManager manager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(manager);
        nestedScrollView = view.findViewById(R.id.fragment_news);
    }

    private void initEventListener() {
        nestedScrollView.setOnScrollChangeListener(new NestedScrollView.OnScrollChangeListener() {
            @Override
            public void onScrollChange(@NonNull NestedScrollView v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {
                //scrollY = da cuon xuong duoi duoc bao nhieu
                //v.getChildAt(0).getMeasuredHeight() = canh tren phan tu dau tien so voi canh duoi phan tu cuoi cung
                //v.getMeasuredHeight() = chieu cao 1 man hinh cua scroll
                if (scrollY == (v.getChildAt(0).getMeasuredHeight() - v.getMeasuredHeight())) {
                    if (page <= totalPage && !isLoading)
                        getPosts();
                }
            }
        });
    }

    private void getPosts() {
        if (page <= totalPage && !isLoading) {
            postAdapter.addLoadingEffect();
            isLoading = true;
        }
        call.clone().enqueue(new Callback<PaginationResponse<Post>>() {
            @Override
            public void onResponse(Call<PaginationResponse<Post>> call, Response<PaginationResponse<Post>> response) {
                if (isLoading) {
                    postAdapter.removeLoadingEffect();
                    isLoading = false;

                }
                if (response.isSuccessful() && response.body() != null) {
                    List<Post> list = response.body().getData();
                    data.addAll(list);
                    postAdapter.setList(data);
                    totalPage = response.body().getTotalPages();
                    page++;
                }
            }

            @Override
            public void onFailure(Call<PaginationResponse<Post>> call, Throwable t) {

            }
        });
    }

    public void refreshPosts() {
        page = 0;
        totalPage = 0;
        data = new ArrayList<>();
        postAdapter.setList(data);
        recyclerView.setAdapter(postAdapter);
        getPosts();
    }
}