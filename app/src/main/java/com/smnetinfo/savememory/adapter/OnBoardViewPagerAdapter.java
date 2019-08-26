package com.smnetinfo.savememory.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.smnetinfo.savememory.fragments.OnBoard1Fragment;
import com.smnetinfo.savememory.fragments.OnBoard2Fragment;
import com.smnetinfo.savememory.fragments.OnBoard3Fragment;
import com.smnetinfo.savememory.fragments.OnBoard4Fragment;
import com.smnetinfo.savememory.fragments.OnBoard5Fragment;


/**
 * Created by Siva on 10-Jul-17.
 */

public class OnBoardViewPagerAdapter extends FragmentPagerAdapter {

    public OnBoardViewPagerAdapter(FragmentManager fragmentManager) {
        super(fragmentManager);
    }

    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                return new OnBoard1Fragment();
            case 1:
                return new OnBoard2Fragment();
            case 2:
                return new OnBoard3Fragment();
            case 3:
                return new OnBoard4Fragment();
            case 4:
                return new OnBoard5Fragment();
            default:
                return null;
        }
    }

    @Override
    public int getCount() {
        return 5;
    }
}
