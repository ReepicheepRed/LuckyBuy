package com.luckybuy;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.luckybuy.adapter.BasePagerAdapter;
import com.luckybuy.adapter.FriendsSNSPagerAdapter;

import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.Event;
import org.xutils.view.annotation.ViewInject;

/**
 * Created by zhiPeng.S on 2016/6/13.
 */

@ContentView(R.layout.activity_buy_record)
public class BuyRecordActivity extends BaseActivity{

    private BasePagerAdapter mSectionsPagerAdapter;

    /**
     * The {@link ViewPager} that will host the section contents.
     */
    @ViewInject(R.id.container_friends_sns)
    private ViewPager mViewPager;

    @ViewInject(R.id.tabs_friends_sns)
    private TabLayout mTabLayout;

    @ViewInject(R.id.title_activity)
    private TextView title_tv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        title_tv.setText(R.string.purchase_history);
        mSectionsPagerAdapter = new BasePagerAdapter(this,getSupportFragmentManager());

        // Set up the ViewPager with the sections adapter.
        mViewPager.setAdapter(mSectionsPagerAdapter);
        mTabLayout.setupWithViewPager(mViewPager);
    }


    @Event(R.id.back_iv)
    private void viewClick(View view){
        switch (view.getId()){
            case R.id.back_iv:
                this.finish();
                break;

        }
    }
}
