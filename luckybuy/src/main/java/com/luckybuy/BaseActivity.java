package com.luckybuy;

import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Gravity;
import android.view.View;
import android.widget.TextView;

import com.luckybuy.login.LoginUserUtils;
import com.luckybuy.pay.BluePayInit;
import com.luckybuy.util.Constant;
import com.luckybuy.util.Utility;

import org.xutils.x;

/**
 * Created by zhiPeng.S on 2016/5/6.
 */
public class BaseActivity extends AppCompatActivity {
    protected final String TAG = getClass().getName();
    private SharedPreferences loginFirstPreferences;
    private SharedPreferences.Editor editor;
    protected BluePayInit bluePayInit;
    @Override
    protected void onCreate( Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        x.view().inject(this);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        loginFirstPreferences = LoginUserUtils.getAppSharedPreferences(this, Constant.PREFERENCES_LOGIN_FIRST);
        editor = loginFirstPreferences.edit();
    }

    @Override
    protected void onStart() {
        super.onStart();
        Utility.activityCount ++ ;
        editor.putInt(Constant.BACKGROUND,Utility.activityCount);
        editor.commit();
    }

    @Override
    protected void onStop() {
        super.onStop();
        Utility.activityCount -- ;
        editor.putInt(Constant.BACKGROUND,Utility.activityCount);
        editor.commit();
    }
}
