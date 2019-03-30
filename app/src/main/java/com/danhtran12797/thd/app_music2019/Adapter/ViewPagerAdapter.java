package com.danhtran12797.thd.app_music2019.Adapter;

import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import java.util.ArrayList;
import java.util.List;

public class ViewPagerAdapter extends FragmentPagerAdapter {
    private final List<Fragment> lstFragments = new ArrayList<>();
    private final List<String> lstTtile = new ArrayList<>();

    public ViewPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int i) {
        return lstFragments.get(i);
    }

    @Override
    public int getCount() {
        return lstFragments.size();
    }

    public void addFragment(Fragment fragment, String title) {
        lstFragments.add(fragment);
        lstTtile.add(title);
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        return lstTtile.get(position);
    }
}
