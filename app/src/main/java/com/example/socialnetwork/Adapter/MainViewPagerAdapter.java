package com.example.socialnetwork.Adapter;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.Lifecycle;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.example.socialnetwork.Fragment.AccountFragment;
import com.example.socialnetwork.Fragment.ContactFragment;
import com.example.socialnetwork.Fragment.NewsFragment;
import com.example.socialnetwork.Fragment.NotificationFragment;

import java.util.ArrayList;
import java.util.List;

public class MainViewPagerAdapter extends FragmentStateAdapter {
    private List<Fragment> fragmentList = new ArrayList<>();

    public MainViewPagerAdapter(@NonNull FragmentManager fragmentManager, @NonNull Lifecycle lifecycle) {
        super(fragmentManager, lifecycle);
        fragmentList.add(new NewsFragment());
        fragmentList.add(new ContactFragment());
        fragmentList.add(new NotificationFragment());
        fragmentList.add(new AccountFragment());
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        return fragmentList.get(position);
    }

    @Override
    public int getItemCount() {
        return fragmentList.size();
    }
}
