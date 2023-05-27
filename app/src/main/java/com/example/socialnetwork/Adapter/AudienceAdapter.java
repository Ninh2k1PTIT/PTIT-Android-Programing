package com.example.socialnetwork.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.socialnetwork.Model.EAudience;
import com.example.socialnetwork.R;

public class AudienceAdapter extends BaseAdapter {
    private int[] imageList = {R.drawable.ic_baseline_public_24, R.drawable.ic_private};
    private EAudience[] value = {EAudience.PUBLIC, EAudience.PRIVATE};
    private String[] name = {"Công khai", "Riêng tư"};
    private Context context;

    public AudienceAdapter(Context context) {
        this.context = context;
    }

    @Override
    public int getCount() {
        return imageList.length;
    }

    @Override
    public Object getItem(int i) {
        return value[i];
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    public int getPosition(EAudience audience) {
        for (int i = 0; i < value.length; i++)
            if (value[i] == audience)
                return i;
        return 0;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        View item = LayoutInflater.from(context).inflate(R.layout.audience_item, viewGroup, false);
        ImageView imageView = item.findViewById(R.id.imageViewAudience);
        TextView textView = item.findViewById(R.id.textViewAudience);
        imageView.setImageResource(imageList[i]);
        textView.setText(name[i]);
        return item;
    }
}
