package com.luckybuy.adapter;

/**
 * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
 * one of the sections/tabs/pages.
 * Created by zhiPeng.S on 2016/5/16.
 */

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.view.ViewGroup;

import com.facebook.AccessToken;
import com.luckybuy.BaseFragment;
import com.luckybuy.LuckyBuy_Cart;
import com.luckybuy.LuckyBuy_Friends;
import com.luckybuy.LuckyBuy_Home;
import com.luckybuy.LuckyBuy_Mine;
import com.luckybuy.LuckyBuy_Unveil;
import com.luckybuy.PlaceholderFragment;
import com.luckybuy.R;
import com.luckybuy.SNS_F_Bask_Fragment;
import com.luckybuy.SNS_F_Lucky_Fragment;
import com.luckybuy.SNS_F_Snatch_Fragment;
import com.luckybuy.SNS_FriendsActivity;

import org.xutils.x;

public class FriendsSNSPagerAdapter extends FragmentPagerAdapter {

    private int PAGE_COUNT = 3;
    private String[] tabs_name = x.app().getResources().getStringArray(R.array.fsns_record);
    private Long user_id_friends;

    public FriendsSNSPagerAdapter(Context context, FragmentManager fm) {
        super(fm);
        user_id_friends = ((SNS_FriendsActivity)context).getUser_id_friends();
    }

    @Override
    public Fragment getItem(int position) {
        // getItem is called to instantiate the fragment for the given page.
        // Return a PlaceholderFragment (defined as a static inner class below).
        switch (position){
            case 0:
                return SNS_F_Snatch_Fragment.newInstance(user_id_friends);
            case 1:
                return SNS_F_Lucky_Fragment.newInstance(user_id_friends);
            case 2:
                return SNS_F_Bask_Fragment.newInstance(user_id_friends);
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
