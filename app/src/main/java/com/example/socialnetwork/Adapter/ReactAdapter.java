package com.example.socialnetwork.Adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.socialnetwork.Activity.ProfileActivity;
import com.example.socialnetwork.Model.React;
import com.example.socialnetwork.R;
import com.example.socialnetwork.Service.TokenManager;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class ReactAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private Context context;
    private List<React> resources = new ArrayList<>();
    private static final int TYPE_ITEM = 1;
    private static final int TYPE_LOADING = 2;
    private TokenManager tokenManager;

    public ReactAdapter(Context context) {
        this.context = context;
        tokenManager = new TokenManager(context);
    }

    public void setResources(List<React> resources) {
        this.resources = resources;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == TYPE_ITEM) {
            View view = LayoutInflater.from(context).inflate(R.layout.react_item, parent, false);
            return new ReactViewHolder(view);
        } else {
            View view = LayoutInflater.from(context).inflate(R.layout.loading_item, parent, false);
            return new LoadingViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (holder.getItemViewType() == TYPE_ITEM) {
            React react = resources.get(position);
            ReactViewHolder reactViewHolder = (ReactViewHolder) holder;
            reactViewHolder.textView.setText(react.getUser().getUsername());
            Picasso.get().load(react.getUser().getAvatar()).into(reactViewHolder.imageView);
            reactViewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(context.getApplicationContext(), ProfileActivity.class);
                    intent.putExtra("userId", react.getUser().getId());
                    intent.putExtra("username", react.getUser().getUsername());
                    intent.putExtra("avatar", react.getUser().getAvatar());
                    context.startActivity(intent);
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return resources.size();
    }


    public class ReactViewHolder extends RecyclerView.ViewHolder {
        private ImageView imageView;
        private TextView textView;

        public ReactViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.imageViewAvatar);
            textView = itemView.findViewById(R.id.textViewUsername);
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
        React react = resources.get(position);
        if (react == null) {
            resources.remove(position);
            notifyDataSetChanged();
        }
    }

    @Override
    public int getItemViewType(int position) {
        return resources.get(position) == null ? TYPE_LOADING : TYPE_ITEM;
    }
}
