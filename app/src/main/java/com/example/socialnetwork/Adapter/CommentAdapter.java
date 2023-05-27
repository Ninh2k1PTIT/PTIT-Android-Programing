package com.example.socialnetwork.Adapter;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.socialnetwork.Activity.ProfileActivity;
import com.example.socialnetwork.Activity.ReactActivity;
import com.example.socialnetwork.Model.Comment;
import com.example.socialnetwork.R;
import com.example.socialnetwork.Service.ApiUtils;
import com.example.socialnetwork.Service.TokenManager;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CommentAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private Context context;
    private List<Comment> resources = new ArrayList<>();
    private static final int TYPE_ITEM = 1;
    private static final int TYPE_LOADING = 2;
    private TokenManager tokenManager;

    public CommentAdapter(Context context) {
        this.context = context;
        tokenManager = new TokenManager(context);
    }

    public void setResources(List<Comment> resources) {
        this.resources = resources;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == TYPE_ITEM) {
            View view = LayoutInflater.from(context).inflate(R.layout.comment_item, parent, false);
            return new CommentViewHolder(view);
        } else {
            View view = LayoutInflater.from(context).inflate(R.layout.loading_item, parent, false);
            return new LoadingViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position, List<Object> payloads) {
        if (payloads.size() > 0) {
            Object[] changes = (Object[]) payloads.get(0);
            Comment comment = resources.get(position);
            CommentViewHolder commentViewHolder = (CommentViewHolder) holder;
            comment.setReact((Boolean) changes[0]);
            comment.setTotalReact((Integer) changes[1]);
            commentViewHolder.textViewTotalReact.setText(comment.getTotalReact() + "");
            TypedValue typedValue = new TypedValue();
            Resources.Theme theme = context.getTheme();
            theme.resolveAttribute(android.R.attr.colorPrimary, typedValue, true);
            theme.resolveAttribute(android.R.attr.textColorPrimary, typedValue, true);
            TypedArray arr = context.obtainStyledAttributes(typedValue.data, new int[]{android.R.attr.colorPrimary, android.R.attr.textColorPrimary});
            if (comment.getReact())
                commentViewHolder.textViewReact.setTextColor(arr.getColor(0, -1));
            else
                commentViewHolder.textViewReact.setTextColor(arr.getColor(1, -1));
        } else {
            super.onBindViewHolder(holder, position, payloads);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (holder.getItemViewType() == TYPE_ITEM) {
            Comment comment = resources.get(position);
            CommentViewHolder commentViewHolder = (CommentViewHolder) holder;
            commentViewHolder.textViewUsername.setText(comment.getUser().getUsername());
            commentViewHolder.textViewComment.setText(comment.getContent());
            commentViewHolder.textViewTotalReact.setText(comment.getTotalReact() + "");
            commentViewHolder.textViewDate.setText(new SimpleDateFormat("dd/MM/yyyy").format(comment.getCreatedAt()));
            Picasso.get().load(comment.getUser().getAvatar()).into(commentViewHolder.imageViewAvatar);

            if (tokenManager.getUserId() == comment.getUser().getId()) {
                commentViewHolder.textViewDelete.setVisibility(View.VISIBLE);
                commentViewHolder.textViewDelete.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        AlertDialog.Builder builder = new AlertDialog.Builder(context);
                        builder.setMessage("Bình luận sẽ bị xóa")
                                .setPositiveButton("Đồng ý", new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        ApiUtils.getCommentService().delete("Bearer " + tokenManager.getToken(), comment.getId()).enqueue(new Callback<Boolean>() {
                                            @Override
                                            public void onResponse(Call<Boolean> call, Response<Boolean> response) {
                                                if(response.code() == 200)
                                                    Toast.makeText(context, "Xóa bình luận thành công", Toast.LENGTH_SHORT).show();
                                                resources.remove(position);
                                                notifyItemRemoved(position);
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
            }


            if (comment.getReact()) {
                TypedValue typedValue = new TypedValue();
                Resources.Theme theme = context.getTheme();
                theme.resolveAttribute(android.R.attr.colorPrimary, typedValue, true);
                TypedArray arr =
                        context.obtainStyledAttributes(typedValue.data, new int[]{
                                android.R.attr.colorPrimary});
                int primaryColor = arr.getColor(0, -1);
                commentViewHolder.textViewReact.setTextColor(primaryColor);
            }

            commentViewHolder.textViewReact.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    ApiUtils.getCommentService().react("Bearer " + tokenManager.getToken(), comment.getId()).enqueue(new Callback<Boolean>() {
                        @Override
                        public void onResponse(Call<Boolean> call, Response<Boolean> response) {
                            ApiUtils.getCommentService().getById("Bearer " + tokenManager.getToken(), comment.getId()).enqueue(new Callback<Comment>() {
                                @Override
                                public void onResponse(Call<Comment> call, Response<Comment> response) {
                                    notifyItemChanged(holder.getAdapterPosition(), new Object[]{response.body().getReact(), response.body().getTotalReact()});
                                }

                                @Override
                                public void onFailure(Call<Comment> call, Throwable t) {
                                }
                            });
                        }

                        @Override
                        public void onFailure(Call<Boolean> call, Throwable t) {
                        }
                    });
                }
            });

            commentViewHolder.imageViewReact.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(context.getApplicationContext(), ReactActivity.class);
                    intent.putExtra("commentId", comment.getId());
                    context.startActivity(intent);
                }
            });
            commentViewHolder.textViewTotalReact.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(context.getApplicationContext(), ReactActivity.class);
                    intent.putExtra("commentId", comment.getId());
                    context.startActivity(intent);
                }
            });

            commentViewHolder.textViewUsername.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(context.getApplicationContext(), ProfileActivity.class);
                    intent.putExtra("userId", comment.getUser().getId());
                    intent.putExtra("username", comment.getUser().getUsername());
                    intent.putExtra("avatar", comment.getUser().getAvatar());
                    context.startActivity(intent);
                }
            });

            commentViewHolder.imageViewAvatar.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(context.getApplicationContext(), ProfileActivity.class);
                    intent.putExtra("userId", comment.getUser().getId());
                    intent.putExtra("username", comment.getUser().getUsername());
                    intent.putExtra("avatar", comment.getUser().getAvatar());
                    context.startActivity(intent);
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return resources.size();
    }

    public class CommentViewHolder extends RecyclerView.ViewHolder {
        private TextView textViewUsername, textViewComment, textViewDate, textViewReact, textViewTotalReact,textViewDelete;
        private ImageView imageViewAvatar, imageViewReact;

        public CommentViewHolder(@NonNull View itemView) {
            super(itemView);
            textViewUsername = itemView.findViewById(R.id.textViewUsername);
            textViewComment = itemView.findViewById(R.id.textViewComment);
            textViewDate = itemView.findViewById(R.id.textViewDate);
            textViewReact = itemView.findViewById(R.id.textViewReact);
            textViewDelete = itemView.findViewById(R.id.textViewDelete);
            textViewTotalReact = itemView.findViewById(R.id.textViewTotalReact);
            imageViewAvatar = itemView.findViewById(R.id.imageViewAvatar);
            imageViewReact = itemView.findViewById(R.id.imageViewReact);
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
        Comment comment = resources.get(position);
        if (comment == null) {
            resources.remove(position);
            notifyDataSetChanged();
        }
    }

    @Override
    public int getItemViewType(int position) {
        return resources.get(position) == null ? TYPE_LOADING : TYPE_ITEM;
    }
}
