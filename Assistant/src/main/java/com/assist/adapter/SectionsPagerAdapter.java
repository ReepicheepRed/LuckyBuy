package com.assist.adapter;

/**
 * Created by zhiPeng.S on 2016/11/2.
 */

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.assist.BaseFragment;
import com.assist.BaskFrgment;
import com.assist.PlaceholderFragment;
import com.assist.R;
import com.assist.RobotFragment;
import com.assist.RobotSettingFragment;

import org.xutils.x;

/**
 * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
 * one of the sections/tabs/pages.
 */
public class SectionsPagerAdapter extends FragmentPagerAdapter {

    private String[] nav_name = x.app().getResources().getStringArray(R.array.nav_name);

    public SectionsPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        Bundle bundle = new Bundle();
        switch(position){
            case 0:
                return RobotFragment.newInstance(bundle);
            case 1:
                return PlaceholderFragment.newInstance(position);
            case 2:
                return BaskFrgment.newInstance(bundle);
            case 3:
                return RobotSettingFragment.newInstance(bundle);
            default:
                return PlaceholderFragment.newInstance(position);
        }

    }

    @Override
    public int getCount() {
        return 4;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return nav_name[position];
    }
}
