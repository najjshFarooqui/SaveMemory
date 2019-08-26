package com.smnetinfo.savememory.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.smnetinfo.savememory.fragments.InheritorFragment;
import com.smnetinfo.savememory.fragments.MemoriesFragment;
import com.smnetinfo.savememory.fragments.WillFragment;


/**
 * Created by Siva on 10-Jul-17.
 */

public class MainViewPagerAdapter extends FragmentPagerAdapter {

    public MainViewPagerAdapter(FragmentManager fragmentManager) {
        super(fragmentManager);
    }

    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                return new MemoriesFragment();
            case 1:
                return new WillFragment();
            case 2:
                return new InheritorFragment();
            default:
                return null;
        }
    }

    @Override
    public int getCount() {
        return 3;
    }
}
