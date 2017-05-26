package com.luckybuy.adapter;

/**
 * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
 * one of the sections/tabs/pages.
 * Created by zhiPeng.S on 2016/5/16.
 */

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.facebook.AccessToken;
import com.luckybuy.BaseFragment;
import com.luckybuy.LuckyBuy_Cart;
import com.luckybuy.LuckyBuy_Friends;
import com.luckybuy.LuckyBuy_Home;
import com.luckybuy.LuckyBuy_Mine;
import com.luckybuy.LuckyBuy_Unveil;
import com.luckybuy.PlaceholderFragment;
import com.luckybuy.R;

import org.xutils.x;

public class SectionsPagerAdapter extends FragmentPagerAdapter {

    private int PAGE_COUNT = 5;
    private String[] nav_name = x.app().getResources().getStringArray(R.array.nav_name);
    private int[] nav_icon = {R.drawable.background_selector_home,R.drawable.background_selector_unveil,
                            R.drawable.background_selector_friends,R.drawable.background_selector_cart,
                            R.drawable.background_selector_mine};
    private Context mContext;
    public SectionsPagerAdapter(FragmentManager fm, Context context) {
        super(fm);
        mContext = context;
    }

    @Override
    public Fragment getItem(int position) {
        // getItem is called to instantiate the fragment for the given page.
        // Return a PlaceholderFragment (defined as a static inner class below).
        switch (position){
            case 0:
                return LuckyBuy_Home.newInstance(position);
            case 1:
                return LuckyBuy_Unveil.newInstance(position);
            case 2:
                return LuckyBuy_Friends.newInstance(position);
            case 3:
                return LuckyBuy_Cart.newInstance(position);
            case 4:
                //if (currentAccessToken == null) {
                    return LuckyBuy_Mine.newInstance(position);
                //} else {
                    //return  LoginFragment.newInstance(position);
                //}

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
        return nav_name[position];
    }


    public View getTabView(int position) {
        View v = LayoutInflater.from(mContext).inflate(R.layout.item_main_nav, null);
        TextView tv = (TextView) v.findViewById(R.id.nav_name_tv);
        tv.setText(nav_name[position]);
        ImageView img = (ImageView) v.findViewById(R.id.nav_icon_iv);
        img.setImageResource(nav_icon[position]);
        return v;
    }


    public Fragment getCurrentFragment() {
        return currentFragment;
    }

    Fragment currentFragment;

    @Override
    public void setPrimaryItem(ViewGroup container, int position, Object object) {
        currentFragment = (BaseFragment) object;
        super.setPrimaryItem(container, position, object);
    }

    AccessToken currentAccessToken;

    public AccessToken getCurrentAccessToken() {
        return currentAccessToken;
    }

    public void setCurrentAccessToken(AccessToken currentAccessToken) {
        this.currentAccessToken = currentAccessToken;
    }
}
