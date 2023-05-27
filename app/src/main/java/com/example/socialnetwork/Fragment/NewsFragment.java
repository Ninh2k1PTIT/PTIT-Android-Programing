package com.example.socialnetwork.Fragment;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;

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
import java.util.concurrent.TimeUnit;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.core.ObservableEmitter;
import io.reactivex.rxjava3.core.ObservableOnSubscribe;
import io.reactivex.rxjava3.core.Observer;
import io.reactivex.rxjava3.disposables.Disposable;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class NewsFragment extends Fragment {
    private Spinner spinner;
    private EditText editTextSearch;
    private RecyclerView recyclerView;
    private PostAdapter postAdapter;
    private BottomNavigationView bottomNavigationView;
    private NestedScrollView nestedScrollView;
    private TokenManager tokenManager;
    private int page = 0;
    private int totalPage = 0;
    private boolean isLoading = false;
    private List<Post> data = new ArrayList<>();
    private String sortType = "NONE";

    private String content = "";

    public NewsFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        tokenManager = new TokenManager(getActivity());
        postAdapter = new PostAdapter(getActivity());
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
        getPosts();
    }

    private void initView(View view) {
        spinner = view.findViewById(R.id.spinner);
        String[] values = {"Mới nhất", "Nhiều bình luận nhất", "Nhiều like nhất"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_spinner_item, values);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        editTextSearch = view.findViewById(R.id.editTextSearch);
        recyclerView = view.findViewById(R.id.recyclerViewPost);
        recyclerView.setAdapter(postAdapter);
        LinearLayoutManager manager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(manager);
        nestedScrollView = view.findViewById(R.id.fragment_news);
        bottomNavigationView = getActivity().findViewById(R.id.navigation);
    }

    private void initEventListener() {
        Observable.create(new ObservableOnSubscribe<String>() {
                    @Override
                    public void subscribe(ObservableEmitter<String> emitter) throws Exception {
                        editTextSearch.addTextChangedListener(new TextWatcher() {
                            @Override
                            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                            }

                            @Override
                            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                            }

                            @Override
                            public void afterTextChanged(Editable editable) {
                                emitter.onNext(editable.toString());
                            }
                        });
                    }
                })
                .debounce(1000, TimeUnit.MILLISECONDS)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<String>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                    }

                    @Override
                    public void onNext(String searchText) {

                        content = searchText;
                        refreshPosts();
                    }

                    @Override
                    public void onError(Throwable e) {
                    }

                    @Override
                    public void onComplete() {
                    }
                });

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

        nestedScrollView.setOnScrollChangeListener(new NestedScrollView.OnScrollChangeListener() {
            @Override
            public void onScrollChange(@NonNull NestedScrollView v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {
                //scrollY = da cuon xuong duoi duoc bao nhieu
                //v.getChildAt(0).getMeasuredHeight() = canh tren phan tu dau tien so voi canh duoi phan tu cuoi cung
                //v.getMeasuredHeight() = chieu cao 1 man hinh cua scroll
                if (scrollY == (v.getChildAt(0).getMeasuredHeight() - v.getMeasuredHeight() + v.getPaddingBottom())) {
                    if (page <= totalPage && !isLoading)
                        getPosts();
                }
            }
        });

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                String selectedValue = adapterView.getItemAtPosition(i).toString();
                if (!isLoading) {
                    if (selectedValue == "Nhiều bình luận nhất")
                        sortType = "COMMENT";
                    else if (selectedValue == "Nhiều like nhất")
                        sortType = "REACT";
                    else sortType = "NONE";
                    refreshPosts();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
    }

    private void getPosts() {
        if (page <= totalPage && !isLoading) {
            postAdapter.addLoadingEffect();
            isLoading = true;
        }
        ApiUtils.getPostService().getAll("Bearer " + tokenManager.getToken(), page, 10, content, sortType).enqueue(new Callback<PaginationResponse<Post>>() {
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
                    NewsFragment.this.page++;
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

    public void refreshPostInPosition(int postId, int position) {
        recyclerView.setAdapter(postAdapter);
        ApiUtils.getPostService().getById("Bearer " + tokenManager.getToken(), postId).enqueue(new Callback<Post>() {
            @Override
            public void onResponse(Call<Post> call, Response<Post> response) {
                Post post = response.body();
                data.set(position, post);
                postAdapter.setPostInPosition(post, position);
            }

            @Override
            public void onFailure(Call<Post> call, Throwable t) {
            }
        });
    }
}