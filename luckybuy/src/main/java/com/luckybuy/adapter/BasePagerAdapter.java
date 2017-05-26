package com.luckybuy.adapter;

/**
 * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
 * one of the sections/tabs/pages.
 * Created by zhiPeng.S on 2016/5/16.
 */

import android.app.Activity;
import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.luckybuy.BuyRecordActivity;
import com.luckybuy.Buy_Rec_Fragment;
import com.luckybuy.PlaceholderFragment;
import com.luckybuy.R;
import com.luckybuy.SNS_F_Bask_Fragment;
import com.luckybuy.SNS_F_Lucky_Fragment;
import com.luckybuy.SNS_F_Snatch_Fragment;

import org.xutils.x;

public class BasePagerAdapter extends FragmentPagerAdapter {

    private int PAGE_COUNT = 3;
    private String[] tabs_name = x.app().getResources().getStringArray(R.array.fsns_record);
    CurrentActivity activity = CurrentActivity.BuyRecordActivity;
    private enum CurrentActivity{
        BuyRecordActivity(0);

        CurrentActivity(int i) {
        }
    }

    public BasePagerAdapter(Context context, FragmentManager fm) {
        this(fm);
        if(context.getClass().equals(BuyRecordActivity.class)){
            PAGE_COUNT = 3;
            tabs_name = x.app().getResources().getStringArray(R.array.buy_record);
            activity = CurrentActivity.BuyRecordActivity;
        } else if(context.getClass().equals(BuyRecordActivity.class)){

        }
    }

    public BasePagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        // getItem is called to instantiate the fragment for the given page.
        // Return a PlaceholderFragment (defined as a static inner class below).

        switch (activity){
            case BuyRecordActivity:
                switch (position){
                    case 0:
                        return Buy_Rec_Fragment.newInstance("all");
                    case 1:
                        return Buy_Rec_Fragment.newInstance("ing");
                    case 2:
                        return Buy_Rec_Fragment.newInstance("ed");
                }
            default:
                return PlaceholderFragment.newInstance(position);
        }

    }

    @Override
    public int getCount() {
        return PAGE_COUNT;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return tabs_name[position];
    }


}
