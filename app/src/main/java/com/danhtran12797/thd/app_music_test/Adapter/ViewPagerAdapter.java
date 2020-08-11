package com.danhtran12797.thd.app_music_test.Adapter;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import java.util.ArrayList;
import java.util.List;

public class ViewPagerAdapter extends FragmentPagerAdapter {
    private final List<Fragment> lstFragments = new ArrayList<>();

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

    public void addFragment(Fragment fragment) {
        lstFragments.add(fragment);
    }
//
//    @Nullable
//    @Override
//    public CharSequence getPageTitle(int position) {
//        return lstTtile.get(position);
//    }
}
