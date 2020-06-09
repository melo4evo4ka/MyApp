package com.example.myapp;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import com.example.myapp.fragment.MyEventsFragment;
import com.example.myapp.fragment.MyTopEventsFragment;

public class PageAdapter extends FragmentPagerAdapter {
private int numOfTabs;

    PageAdapter(FragmentManager fm,int numOfTabs){
        super(fm);
        this.numOfTabs=numOfTabs;
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {
        switch (position){
            case 0:
                return new MyEventsFragment();
            case 1:
                return new MyTopEventsFragment();
            case 2:
                return new MyEventsFragment();
             //  return new Frag3();
            default: return null;
        }
    }

    @Override
    public int getCount() {
        return numOfTabs;
    }
}
