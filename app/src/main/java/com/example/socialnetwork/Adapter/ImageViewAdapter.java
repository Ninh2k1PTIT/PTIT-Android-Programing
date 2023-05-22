package com.example.socialnetwork.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.viewpager.widget.PagerAdapter;

import com.example.socialnetwork.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class ImageViewAdapter extends PagerAdapter {
    private Context context;
    private LayoutInflater layoutInflater;
    private List<String> resources = new ArrayList<>();
    private static final int TYPE_VIEWONLY = 1;
    private int viewType;

    public ImageViewAdapter(Context context, List<String> resources, int viewType) {
        this.context = context;
        this.resources = resources;
        this.viewType = viewType;
        layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    public void addItem(String image) {
        resources.add(image);
        notifyDataSetChanged();
    }

    public List<String> getResources() {
        return resources;
    }

    public String getItem(int position) {
        return resources.get(position);
    }

    @Override
    public int getCount() {
        return resources.size();
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return view == object;
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {
        View view = layoutInflater.inflate(R.layout.image_item, container, false);
        ImageView imageView = view.findViewById(R.id.imageView);
        ImageButton imageButton = view.findViewById(R.id.imageButtonRemove);

        Picasso.get().load(resources.get(position)).into(imageView);

        if (viewType == TYPE_VIEWONLY)
            imageButton.setVisibility(View.GONE);
        else
            imageButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    resources.remove(position);
                    container.removeView(view);
                    notifyDataSetChanged();
                }
            });
        container.addView(view);

        return view;
    }

    @Override
    public int getItemPosition(@NonNull Object object) {
        return PagerAdapter.POSITION_NONE;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((View) object);
    }
}
