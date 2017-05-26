package com.assist;

import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import com.assist.adapter.SectionsPagerAdapter;
import com.assist.model.CommodityModelImpl;
import com.assist.model.SelectUserModelImpl;

import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.ViewInject;

import java.util.List;

@ContentView(R.layout.activity_main)
public class MainActivity extends BaseActivity {

    private SectionsPagerAdapter mSectionsPagerAdapter;
    @ViewInject(R.id.container)
    private ViewPager mViewPager;
    @ViewInject(R.id.tabs)
    private TabLayout mTabLayout;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        init();
    }

    private void init(){
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());
        mViewPager.setAdapter(mSectionsPagerAdapter);
        mTabLayout.setupWithViewPager(mViewPager);
    }

    public ViewPager getmViewPager() {
        return mViewPager;
    }

    private List<CommodityModelImpl> datas;

    private List<SelectUserModelImpl.DetailInfo> datas_user;

    public List<CommodityModelImpl> getDatas() {
        return datas;
    }

    public void setDatas(List<CommodityModelImpl> datas) {
        this.datas = datas;
    }

    public List<SelectUserModelImpl.DetailInfo> getDatas_user() {
        return datas_user;
    }

    public void setDatas_user(List<SelectUserModelImpl.DetailInfo> datas_user) {
        this.datas_user = datas_user;
    }

    private int interval_for_snatch = 10;

    public int getInterval() {
        return interval_for_snatch;
    }

    public void setInterval(int interval) {
        this.interval_for_snatch = interval;
    }

    private boolean isRandom;

    public boolean isRandom() {
        return isRandom;
    }

    public void setRandom(boolean random) {
        isRandom = random;
    }
}
