package com.example.socialnetwork.Adapter;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import com.example.socialnetwork.Activity.CommentActivity;
import com.example.socialnetwork.Activity.ProfileActivity;
import com.example.socialnetwork.Activity.UploadActivity;
import com.example.socialnetwork.Model.Photo;
import com.example.socialnetwork.Model.Post;
import com.example.socialnetwork.R;
import com.example.socialnetwork.Service.ApiUtils;
import com.example.socialnetwork.Service.RequestCodeUtils;
import com.example.socialnetwork.Service.TokenManager;
import com.google.android.material.tabs.TabLayout;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PostAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private Context context;
    private List<Post> resources = new ArrayList<>();
    private static final int TYPE_ITEM = 1;
    private static final int TYPE_LOADING = 2;
    private TokenManager tokenManager;

    public PostAdapter(Context context) {
        this.context = context;
        tokenManager = new TokenManager(context);
    }

    public void setList(List<Post> resources) {
        this.resources = resources;
        notifyDataSetChanged();
    }

    public void setPostInPosition(Post post, int position) {
        resources.set(position, post);
        notifyItemChanged(position, new Object[]{post.getReact(), post.getTotalReact(), post.getTotalComment()});
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == TYPE_ITEM) {
            View view = LayoutInflater.from(context).inflate(R.layout.post_item, parent, false);
            return new PostViewHolder(view);
        } else {
            View view = LayoutInflater.from(context).inflate(R.layout.loading_item, parent, false);
            return new LoadingViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position, List<Object> payloads) {
        if (payloads.size() > 0) {
            Object[] changes = (Object[]) payloads.get(0);
            Post post = resources.get(position);
            PostViewHolder postViewHolder = (PostViewHolder) holder;
            post.setReact((Boolean) changes[0]);
            post.setTotalReact((Integer) changes[1]);
            post.setTotalComment((Integer) changes[2]);
            postViewHolder.textViewTotalLike.setText(post.getTotalReact() + "");
            postViewHolder.textViewTotalComment.setText(post.getTotalComment() + " bình luận");
            TypedValue typedValue = new TypedValue();
            Resources.Theme theme = context.getTheme();
            theme.resolveAttribute(android.R.attr.colorPrimary, typedValue, true);
            theme.resolveAttribute(android.R.attr.textColorPrimary, typedValue, true);
            TypedArray arr = context.obtainStyledAttributes(typedValue.data, new int[]{android.R.attr.colorPrimary, android.R.attr.textColorPrimary});
            if (post.getReact()) {
                postViewHolder.buttonLike.setTextColor(arr.getColor(0, -1));
                postViewHolder.buttonLike.setCompoundDrawablesWithIntrinsicBounds(ContextCompat.getDrawable(context, R.drawable.ic_thumb_up_fill), null, null, null);
            } else {
                postViewHolder.buttonLike.setTextColor(arr.getColor(1, -1));
                postViewHolder.buttonLike.setCompoundDrawablesWithIntrinsicBounds(ContextCompat.getDrawable(context, R.drawable.ic_thumb_up), null, null, null);
            }

        } else {
            super.onBindViewHolder(holder, position, payloads);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (holder.getItemViewType() == TYPE_ITEM) {
            Post post = resources.get(position);
            PostViewHolder postViewHolder = (PostViewHolder) holder;
            postViewHolder.textViewUsername.setText(post.getUser().getUsername());
            Picasso.get().load(post.getUser().getAvatar()).into(postViewHolder.imageViewAvatar);
            postViewHolder.textViewContent.setText(post.getContent());
            postViewHolder.textViewTotalLike.setText(post.getTotalReact() + "");
            postViewHolder.textViewTotalComment.setText(post.getTotalComment() + " bình luận");
            postViewHolder.textViewCreatedAt.setText(new SimpleDateFormat("dd/MM/yyyy").format(post.getCreatedAt()));
            if (post.getReact()) {
                TypedValue typedValue = new TypedValue();
                Resources.Theme theme = context.getTheme();
                theme.resolveAttribute(android.R.attr.colorPrimary, typedValue, true);
                TypedArray arr =
                        context.obtainStyledAttributes(typedValue.data, new int[]{
                                android.R.attr.colorPrimary});
                int primaryColor = arr.getColor(0, -1);
                postViewHolder.buttonLike.setTextColor(primaryColor);
                postViewHolder.buttonLike.setCompoundDrawablesWithIntrinsicBounds(ContextCompat.getDrawable(context, R.drawable.ic_thumb_up_fill), null, null, null);
            }

            if (tokenManager.getUserId() == post.getUser().getId()) {
                postViewHolder.buttonEdit.setVisibility(View.VISIBLE);
                postViewHolder.buttonDelete.setVisibility(View.VISIBLE);
                postViewHolder.buttonDelete.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        AlertDialog.Builder builder = new AlertDialog.Builder(context);
                        builder.setMessage("Bài viết sẽ bị xóa")
                                .setPositiveButton("Đồng ý", new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        ApiUtils.getPostService().delete("Bearer " + tokenManager.getToken(), post.getId()).enqueue(new Callback<Boolean>() {
                                            @Override
                                            public void onResponse(Call<Boolean> call, Response<Boolean> response) {
                                                if (response.code() == 200)
                                                    Toast.makeText(context, "Xóa bài viết thành công", Toast.LENGTH_SHORT).show();
                                                resources.remove(position);
                                                notifyItemRemoved(position);
                                                notifyItemRangeChanged(position, getItemCount() - position);
                                            }

                                            @Override
                                            public void onFailure(Call<Boolean> call, Throwable t) {
                                            }
                                        });
                                    }
                                })
                                .setNegativeButton("Hủy", new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                    }
                                }).show();
                    }
                });

                postViewHolder.buttonEdit.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(context, UploadActivity.class);
                        intent.putExtra("postId", post.getId());
                        ((Activity) context).startActivityForResult(intent, RequestCodeUtils.CREATE_POST);
                    }
                });
            }

            postViewHolder.buttonLike.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    ApiUtils.getPostService().react("Bearer " + tokenManager.getToken(), post.getId()).enqueue(new Callback<Boolean>() {
                        @Override
                        public void onResponse(Call<Boolean> call, Response<Boolean> response) {
                            ApiUtils.getPostService().getById("Bearer " + tokenManager.getToken(), post.getId()).enqueue(new Callback<Post>() {
                                @Override
                                public void onResponse(Call<Post> call, Response<Post> response) {
                                    setPostInPosition(response.body(), holder.getAdapterPosition());
                                }

                                @Override
                                public void onFailure(Call<Post> call, Throwable t) {
                                }
                            });
                        }

                        @Override
                        public void onFailure(Call<Boolean> call, Throwable t) {
                        }
                    });

                }
            });

            postViewHolder.buttonComment.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(context, CommentActivity.class);
                    intent.putExtra("postId", post.getId());
                    intent.putExtra("position", position);
                    intent.putExtra("totalPostReact", post.getTotalReact());
                    ((Activity) context).startActivityForResult(intent, RequestCodeUtils.UPDATE_COMMENT);
                    ((Activity) context).overridePendingTransition(R.anim.slide_in_up, R.anim.slide_stay);
                }
            });

            List<Photo> photos = post.getPhotos();
            if (photos.size() > 0) {
                ImageViewAdapter imageViewAdapter = new ImageViewAdapter(context, photos, 1);
                postViewHolder.viewPager.getLayoutParams().height = (int) (300 * context.getResources().getDisplayMetrics().density);
                postViewHolder.viewPager.setAdapter(imageViewAdapter);
                postViewHolder.viewPager.setOnTouchListener(new View.OnTouchListener() {
                    private float startX = 0f;
                    private float startY = 0f;

                    @Override
                    public boolean onTouch(View view, MotionEvent motionEvent) {
                        switch (motionEvent.getAction()) {
                            case MotionEvent.ACTION_DOWN:
                                startX = motionEvent.getX();
                                startY = motionEvent.getY();
                                break;

                            case MotionEvent.ACTION_MOVE:
                                float endX = motionEvent.getX();
                                float endY = motionEvent.getY();

                                float deltaX = endX - startX;
                                float deltaY = endY - startY;

                                if (Math.abs(deltaX) > Math.abs(deltaY)) {
                                    // Cuộn theo trục X
                                    // Thực hiện các hành động cho cuộn theo trục X ở đây
                                    ((ViewGroup) view).requestDisallowInterceptTouchEvent(true);
                                } else {
                                    // Cuộn theo trục Y
                                    // Thực hiện các hành động cho cuộn theo trục Y ở đây
                                }
                                break;
                        }

                        return false;
                    }
                });
            }

            postViewHolder.imageViewAvatar.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(context.getApplicationContext(), ProfileActivity.class);
                    intent.putExtra("userId", post.getUser().getId());
                    intent.putExtra("username", post.getUser().getUsername());
                    intent.putExtra("avatar", post.getUser().getAvatar());
                    context.startActivity(intent);
                }
            });

            postViewHolder.textViewUsername.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(context.getApplicationContext(), ProfileActivity.class);
                    intent.putExtra("userId", post.getUser().getId());
                    intent.putExtra("username", post.getUser().getUsername());
                    intent.putExtra("avatar", post.getUser().getAvatar());
                    context.startActivity(intent);
                }
            });

        }
    }


    @Override
    public int getItemCount() {
        return resources.size();
    }

    public class PostViewHolder extends RecyclerView.ViewHolder {
        private TextView textViewUsername, textViewContent, textViewTotalLike, textViewTotalComment, textViewCreatedAt;
        private ImageView imageViewAvatar;
        private Button buttonLike, buttonComment;
        private ImageButton buttonDelete, buttonEdit;
        private ViewPager viewPager;
        private TabLayout tabLayout;

        public PostViewHolder(@NonNull View itemView) {
            super(itemView);
            textViewUsername = itemView.findViewById(R.id.textViewUsername);
            textViewContent = itemView.findViewById(R.id.textViewContent);
            textViewTotalLike = itemView.findViewById(R.id.textViewTotalLike);
            textViewTotalComment = itemView.findViewById(R.id.textViewTotalComment);
            textViewCreatedAt = itemView.findViewById(R.id.textViewCreatedAt);
            imageViewAvatar = itemView.findViewById(R.id.imageViewAvatar);
            viewPager = itemView.findViewById(R.id.viewPagerImage);
            tabLayout = itemView.findViewById(R.id.tabDots);
            tabLayout.setupWithViewPager(viewPager, true);
            buttonLike = itemView.findViewById(R.id.buttonLike);
            buttonComment = itemView.findViewById(R.id.buttonComment);
            buttonDelete = itemView.findViewById(R.id.buttonDelete);
            buttonEdit = itemView.findViewById(R.id.buttonEdit);
        }
    }

    public class LoadingViewHolder extends RecyclerView.ViewHolder {
        ProgressBar progressBar;

        public LoadingViewHolder(@NonNull View itemView) {
            super(itemView);
            progressBar = itemView.findViewById(R.id.progressBar);
        }
    }

    public void addLoadingEffect() {
        resources.add(null);
        notifyItemInserted(resources.size() - 1);
    }

    public void removeLoadingEffect() {
        int position = resources.size() - 1;
        Post post = resources.get(position);
        if (post == null) {
            resources.remove(position);
            notifyDataSetChanged();
        }
    }

    @Override
    public int getItemViewType(int position) {
        return resources.get(position) == null ? TYPE_LOADING : TYPE_ITEM;
    }
}
