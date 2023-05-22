package com.example.socialnetwork.Fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;

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
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class NewsFragment extends Fragment {
    private RecyclerView recyclerView;
    private PostAdapter postAdapter;
    private BottomNavigationView bottomNavigationView;
    NestedScrollView nestedScrollView;
    private TokenManager tokenManager;
    private int page = 0;
    private int totalPage = 0;
    private boolean isLoading = false;
    private List<Post> list = new ArrayList<>();

    public NewsFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        tokenManager = new TokenManager(getActivity());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_news, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initView(view);
        initEventListener();
        getPosts(page);
    }

    private void initView(View view) {
        recyclerView = view.findViewById(R.id.recyclerViewPost);
        postAdapter = new PostAdapter(getActivity());
        recyclerView.setAdapter(postAdapter);
        LinearLayoutManager manager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(manager);
        nestedScrollView = view.findViewById(R.id.fragment_news);
        bottomNavigationView = getActivity().findViewById(R.id.navigation);
    }

    private void initEventListener() {
        bottomNavigationView.setOnItemReselectedListener(new NavigationBarView.OnItemReselectedListener() {
            @Override
            public void onNavigationItemReselected(@NonNull MenuItem item) {
                if (item.getItemId() == R.id.mNews)
                    if (nestedScrollView.getScrollY() == 0)
                        refreshPosts();
                    else
                        nestedScrollView.smoothScrollTo(0, 0);
            }
        });

        nestedScrollView.getViewTreeObserver().addOnScrollChangedListener(new ViewTreeObserver.OnScrollChangedListener() {
            @Override
            public void onScrollChanged() {
                View view = (View) nestedScrollView.getChildAt(nestedScrollView.getChildCount() - 1);

                int diff = (view.getBottom() + nestedScrollView.getPaddingBottom() - (nestedScrollView.getHeight() + nestedScrollView
                        .getScrollY()));
                if (diff == 0 && page <= totalPage && !isLoading)
                    getPosts(page);
            }
        });
    }

    private void getPosts(int page) {
        if (page <= totalPage && !isLoading) {
            postAdapter.addLoadingEffect();
            isLoading = true;
        }
        ApiUtils.getPostService().getAll("Bearer " + tokenManager.get(), page, 5).enqueue(new Callback<PaginationResponse<Post>>() {
            @Override
            public void onResponse(Call<PaginationResponse<Post>> call, Response<PaginationResponse<Post>> response) {
                if (isLoading) {
                    postAdapter.removeLoadingEffect();
                    isLoading = false;

                }
                if (response.isSuccessful() && response.body() != null) {
                    List<Post> list = response.body().getData();
                    NewsFragment.this.list.addAll(list);
                    postAdapter.setList(NewsFragment.this.list);
                    totalPage = response.body().getTotalPages();
                    NewsFragment.this.page++;
                }
            }

            @Override
            public void onFailure(Call<PaginationResponse<Post>> call, Throwable t) {

            }
        });
    }

    private void refreshPosts() {
        page = 0;
        totalPage = 0;
        list = new ArrayList<>();
        postAdapter.setList(list);
        getPosts(page);
    }
}