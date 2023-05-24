package com.example.socialnetwork.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.socialnetwork.Model.Comment;
import com.example.socialnetwork.R;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class CommentAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private Context context;
    private List<Comment> resources = new ArrayList<>();
    private static final int TYPE_ITEM = 1;
    private static final int TYPE_LOADING = 2;

    public CommentAdapter(Context context) {
        this.context = context;
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
            return new CommentAdapter.CommentViewHolder(view);
        } else {
            View view = LayoutInflater.from(context).inflate(R.layout.loading_item, parent, false);
            return new CommentAdapter.LoadingViewHolder(view);
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
        }
    }

    @Override
    public int getItemCount() {
        return resources.size();
    }

    public class CommentViewHolder extends RecyclerView.ViewHolder {
        private TextView textViewUsername, textViewComment, textViewDate, textViewReact, textViewTotalReact;
        private ImageView imageViewAvatar, imageViewReact;

        public CommentViewHolder(@NonNull View itemView) {
            super(itemView);
            textViewUsername = itemView.findViewById(R.id.textViewUsername);
            textViewComment = itemView.findViewById(R.id.textViewComment);
            textViewDate = itemView.findViewById(R.id.textViewDate);
            textViewReact = itemView.findViewById(R.id.textViewReact);
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
