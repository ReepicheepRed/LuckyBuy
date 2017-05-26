package com.luckybuy;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.widget.TextView;
import com.facebook.AccessToken;
import com.facebook.AccessTokenTracker;
import com.facebook.CallbackManager;
import com.facebook.FacebookSdk;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.igexin.sdk.PushManager;
import com.luckybuy.adapter.SectionsPagerAdapter;
import com.luckybuy.db.DB_Config;
import com.luckybuy.layout.BadgeView;
import com.luckybuy.layout.Dialog_FirstEnter;
import com.luckybuy.login.LoginUserUtils;
import com.luckybuy.model.AppUpdateModel;
import com.luckybuy.model.AwardModel;
import com.luckybuy.network.TokenVerify;
import com.luckybuy.pay.BluePayInitImpl;
import com.luckybuy.util.Constant;
import com.luckybuy.util.UpdateManager;
import com.luckybuy.util.Utility;
import org.xutils.DbManager;
import org.xutils.common.Callback;
import org.xutils.common.util.DensityUtil;
import org.xutils.ex.DbException;
import org.xutils.http.RequestParams;
import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

import java.text.ParseException;
import java.util.Date;
import java.util.List;

import cn.sharesdk.framework.ShareSDK;
import cn.smssdk.SMSSDK;

@ContentView(R.layout.activity_main)
public class MainActivity extends BaseActivity implements ViewPager.OnPageChangeListener,TabLayout.OnTabSelectedListener,DialogInterface.OnClickListener{

    private SectionsPagerAdapter mSectionsPagerAdapter;

    @ViewInject(R.id.container)
    private ViewPager mViewPager;

    public ViewPager getViewPager() {
        return mViewPager;
    }

    @ViewInject(R.id.tabs)
    private TabLayout mTabLayout;

    public TabLayout getTabLayout() {
        return mTabLayout;
    }

    private BadgeView badgeView;

    public BadgeView getBadgeView() {
        return badgeView;
    }

    private AccessTokenTracker accessTokenTracker;
    private CallbackManager callbackManager;
    private boolean isResumed = false;

    private DbManager db;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initSDK();
        initFacebook();
        initRateDialog();
        initView();
        Utility.isAvilible(this,"com.facebook.orca");
        updateLoginDate();
    }

    private void initSDK(){
        ShareSDK.initSDK(this,"158b5db8318e5");
        SMSSDK.initSDK(this, "14a989e04db6b", "34df0fba679d2b013fb451929389008c");
        PushManager.getInstance().initialize(x.app());
        FacebookSdk.sdkInitialize(x.app());
    }

    private void initView(){
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager(),this);
        mViewPager.setAdapter(mSectionsPagerAdapter);
        mTabLayout.setupWithViewPager(mViewPager);

        for (int i = 0; i < mTabLayout.getTabCount(); i++) {
            TabLayout.Tab tab = mTabLayout.getTabAt(i);
            if (tab != null) {
                tab.setCustomView(mSectionsPagerAdapter.getTabView(i));
                if(i == 0){
                    View view = tab.getCustomView();
                    TextView txt_title = (TextView) view.findViewById(R.id.nav_name_tv);
                    txt_title.setTextColor(getResources().getColor(R.color.light_red));
                }
                if(i == 3) {
                    View view = mTabLayout.getTabAt(i).getCustomView();
                    badgeView = new BadgeView(this, view);
                    badgeView.setHeight(DensityUtil.dip2px(18));
                    badgeView.setBadgePosition(BadgeView.POSITION_TOP_RIGHT);
                    badgeView.setText(0 + "");
                    badgeView.setBadgeMargin(0);
                    badgeView.show(true);
                }
            }
        }
        mTabLayout.addOnTabSelectedListener(this);
        mViewPager.addOnPageChangeListener(this);
        db = x.getDb(DB_Config.getDaoConfig());
        updateBadgeView();
        showFirstEnterDialog();
        CheckUpdateTime();
        rateApp();
    }

    private void initFacebook(){
        callbackManager = CallbackManager.Factory.create();

        accessTokenTracker = new AccessTokenTracker() {
            @Override
            protected void onCurrentAccessTokenChanged(AccessToken oldAccessToken,
                                                       AccessToken currentAccessToken) {
                /*if (isResumed) {
                    FragmentManager manager = getFragmentManager();
                    int backStackSize = manager.getBackStackEntryCount();
                    for (int i = 0; i < backStackSize; i++) {
                        manager.popBackStack();
                    }*/
                mSectionsPagerAdapter.setCurrentAccessToken(currentAccessToken);
                if(mSectionsPagerAdapter.getItemPosition(mSectionsPagerAdapter.getCurrentFragment()) == 4){
                    if (currentAccessToken != null) {
                        mSectionsPagerAdapter.notifyDataSetChanged();
                    } else {
                        mSectionsPagerAdapter.notifyDataSetChanged();
                    }
                }

            }
            //}
        };
    }

    private void updateBadgeView(){
        try {
            List<AwardModel> data = db.findAll(AwardModel.class);
            badgeView.setVisibility(View.GONE);
            if(data == null) return;
            int count = data.size();
            if(count > 0){
                badgeView.setVisibility(View.VISIBLE);
                badgeView.setText(String.valueOf(count));
            }
        } catch (DbException e) {
            e.printStackTrace();
        }
    }

    private Dialog_FirstEnter dialog_firstEnter;
    private void showFirstEnterDialog(){
        SharedPreferences loginFirstPreferences = LoginUserUtils.getAppSharedPreferences(
                this, Constant.PREFERENCES_LOGIN_FIRST);
        boolean isFirst = loginFirstPreferences.getBoolean("isFirst", true);
        dialog_firstEnter = new Dialog_FirstEnter(this,onClickListener);
        dialog_firstEnter.requestWindowFeature(Window.FEATURE_NO_TITLE);
        if(isFirst)
            dialog_firstEnter.showWindow();


    }

    private View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            dialog_firstEnter.dismiss();
            SharedPreferences loginFirstPreferences = LoginUserUtils.getAppSharedPreferences(
                    MainActivity.this, Constant.PREFERENCES_LOGIN_FIRST);
            SharedPreferences.Editor editor = loginFirstPreferences.edit();
            editor.putBoolean("isFirst", false);
            editor.commit();

            switch (v.getId()){
                case R.id.first_enter_issue_tv:
//                    mViewPager.setCurrentItem(4);
                    break;
                case R.id.first_enter_cancel_iv:
                    break;
            }
        }
    };

    private SharedPreferences loginFirstPreferences;
    private SharedPreferences.Editor editor;
    private void CheckUpdateTime(){
        String curTime = Utility.DATE_FORMAT.format(System.currentTimeMillis());
        String previousUpdateTime = loginFirstPreferences.getString("previous","1970-01-01 00:00:00");

        try {
            Date curTimeD = Utility.DATE_FORMAT.parse(curTime);
            Date preTimeD = Utility.DATE_FORMAT.parse(previousUpdateTime);
            long diff = (curTimeD.getTime() - preTimeD.getTime())/1000;
            if(diff >= 2*24*3600) {
                editor.putString("previous",curTime);
                editor.apply();
                updateApp();
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }

    }

    private AlertDialog.Builder rateDialog;
    private void initRateDialog(){
        loginFirstPreferences =
                LoginUserUtils.getAppSharedPreferences(MainActivity.this, Constant.PREFERENCES_LOGIN_FIRST);
        editor = loginFirstPreferences.edit();
        rateDialog = new AlertDialog.Builder(this)
                .setTitle("Rate LuckyBuy")
                .setMessage("if you enjoy using this LuckyBuy.please take a moment\n" +
                        "to rate it.thanks for your support!")
                .setPositiveButton("Rate it Now", this)
                .setNegativeButton("No,thanks", this);
    }

    private void rateApp(){
        String curTime = Utility.DATE_FORMAT.format(System.currentTimeMillis());
        String rateTime = loginFirstPreferences.getString("rate_time",curTime);
        if(rateTime.equals("2100-01-01 00:00:00")) return;
        try {
            Date curTimeD = Utility.DATE_FORMAT.parse(curTime);
            Date preTimeD = Utility.DATE_FORMAT.parse(rateTime);
            long diff = (curTimeD.getTime() - preTimeD.getTime())/1000;
            if(diff >= 5*24*3600) {
                rateDialog.show();
                editor.putString("rate_time","2100-01-01 00:00:00");
                editor.apply();
            }else{
                editor.putString("rate_time",curTime);
                editor.apply();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private UpdateManager manager = UpdateManager.getInstance();
    private void updateApp(){
        manager.setMainPage(true);
        RequestParams params = new RequestParams(Constant.getBaseUrl() + "Handle/LBList/Version.ashx");
        params.addQueryStringParameter("ostype","android");
        x.http().get(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                try {
                    Gson gson = new GsonBuilder().serializeNulls().create();
                    List<AppUpdateModel> model = gson.fromJson(result, new TypeToken<List<AppUpdateModel>>(){}.getType());
                    manager.compareVersion(MainActivity.this,model.get(0));
                }catch (Exception e){
                    e.printStackTrace();
                }
            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {

            }

            @Override
            public void onCancelled(CancelledException cex) {

            }

            @Override
            public void onFinished() {

            }
        });
    }


    private long firstTime = 0L;
    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            long secondTime = System.currentTimeMillis();
            if (secondTime - firstTime > 800L) {
                Utility.toastShow(x.app(), "Press once again to exit program");
                firstTime = secondTime;
                return true;
            } else {
                Intent startMain = new Intent(Intent.ACTION_MAIN);
                startMain.addCategory(Intent.CATEGORY_HOME);
                startMain.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(startMain);
                System.exit(0);
            }
        }
        return super.onKeyUp(keyCode, event);
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if(hasFocus){
            updateBadgeView();
        }
    }



    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        //Utility.toastShow(x.app(),"main_activity");
        callbackManager.onActivityResult(requestCode, resultCode, data);
        switch (resultCode){
            case Constant.RESULT_CODE_CART:
                mViewPager.setCurrentItem(3);
                break;
            case Constant.RESULT_CODE:
                mViewPager.setCurrentItem(0);
                break;
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        accessTokenTracker.stopTracking();
    }


    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {

    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }

    @Override
    public void onTabSelected(TabLayout.Tab tab) {
        View view = tab.getCustomView();
        TextView txt_title = (TextView) view.findViewById(R.id.nav_name_tv);
        txt_title.setTextColor(getResources().getColor(R.color.light_red));
    }

    @Override
    public void onTabUnselected(TabLayout.Tab tab) {
        View view = tab.getCustomView();
        TextView txt_title = (TextView) view.findViewById(R.id.nav_name_tv);
        txt_title.setTextColor(getResources().getColor(R.color.text_4_c));
    }

    @Override
    public void onTabReselected(TabLayout.Tab tab) {

    }



    @Override
    public void onClick(DialogInterface dialog, int which) {
        dialog.dismiss();
        switch (which){
            case -1:
                final String appPackageName = getPackageName(); // getPackageName() from Context or Activity object
                try {
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + appPackageName)));
//                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + appPackageName)));
                } catch (android.content.ActivityNotFoundException anfe) {
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + appPackageName)));
                }
                break;
            case -2:
                editor.putString("rate_time","2100-01-01 00:00:00");
                editor.apply();
                break;
        }
    }

    private void updateLoginDate(){
        RequestParams params = new RequestParams(Constant.getBaseUrl() + "Page/Ucenter/LoginTimeUpdate.ashx");
        SharedPreferences preferences = LoginUserUtils.getUserSharedPreferences(this);
        String uidxStr = String.valueOf(preferences.getLong(Constant.USER_ID,0));
        if(uidxStr.equals("0")) return;
        TokenVerify.addToken(this,params);
        params.addQueryStringParameter("uidx", uidxStr);
        x.http().get(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {

            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {

            }

            @Override
            public void onCancelled(CancelledException cex) {

            }

            @Override
            public void onFinished() {

            }
        });
    }

}
