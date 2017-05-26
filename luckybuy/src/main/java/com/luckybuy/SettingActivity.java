package com.luckybuy;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.facebook.AccessToken;
import com.facebook.login.LoginManager;
import com.igexin.sdk.PushManager;
import com.luckybuy.login.LoginUserUtils;
import com.luckybuy.util.Constant;
import com.luckybuy.util.Utility;

import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.Event;
import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

import java.io.File;

/**
 * Created by zhiPeng.S on 2016/6/14.
 */
@ContentView(R.layout.activity_setting)
public class SettingActivity extends BaseActivity {

    private SharedPreferences preferences;
    private SharedPreferences.Editor editor;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        title_tv.setText(R.string.title_setting);
        preferences = LoginUserUtils.getUserSharedPreferences(this);
        editor = preferences.edit();
        initClearCache();
        initVersionInfo();
    }

    @ViewInject(R.id.title_activity)
    private TextView title_tv;

    @Event({R.id.back_iv,R.id.exit_btn,
            R.id.setting_language_rl,R.id.setting_help_center_rl,R.id.setting_feedback_rl,
            R.id.setting_clear_rl,R.id.setting_protocol_rl,R.id.setting_about_rl})
    private void viewClick(View view){
        AccessToken accessToken = AccessToken.getCurrentAccessToken();
        Intent intent = new Intent();
        switch(view.getId()){
            case R.id.back_iv:
                setResult(Constant.RESULT_CODE_MINE);
                this.finish();
                break;
            case R.id.exit_btn:
                if (accessToken != null && !accessToken.isExpired()) {
                    LoginManager.getInstance().logOut();
                }
                editor.clear();
                editor.commit();
                PushManager.getInstance().initialize(x.app());
                setResult(Constant.RESULT_CODE_UPDATE);
                this.finish();
                break;
            case R.id.setting_language_rl:
                intent.setClass(this,SettingLanguageActivity.class);
                startActivity(intent);
                break;
            case R.id.setting_help_center_rl:
                intent.setClass(this,WebActivity.class);
                intent.putExtra(Constant.WEB_H5,Constant.FAQ);
                startActivity(intent);
                break;
            case R.id.setting_feedback_rl:
                intent.setClass(this,FeedbackActivity.class);
                startActivity(intent);
                break;
            case R.id.setting_clear_rl:
                Utility.clearAppCache(this);
                //initClearCache();
                cache_size_tv.setText("0KB");
                break;
            case R.id.setting_protocol_rl:
                intent.setClass(this,WebActivity.class);
                startActivity(intent);
                break;
            case R.id.setting_about_rl:
                intent.setClass(this,AboutActivity.class);
                startActivity(intent);
                break;
        }
    }


    @ViewInject(R.id.cache_size_tv)
    private TextView cache_size_tv;
    // 计算缓存大小
    private void initClearCache(){
        long fileSize = 0;
        String cacheSize = "0KB";
        File filesDir = x.app().getFilesDir();// /data/data/package_name/files
        File cacheDir = x.app().getCacheDir();// /data/data/package_name/cache

        fileSize += Utility.getDirSize(filesDir);
        fileSize += Utility.getDirSize(cacheDir);

        // 2.2版本才有将应用缓存转移到sd卡的功能
        if(Utility.isMethodsCompat(android.os.Build.VERSION_CODES.FROYO)){
            File externalCacheDir = getExternalCacheDir();//"<sdcard>/Android/data/<package_name>/cache/"
            fileSize += Utility.getDirSize(externalCacheDir);
        }

        if (fileSize > 0)
            cacheSize = Utility.formatFileSize(fileSize);
        cache_size_tv.setText(cacheSize);
    }

    @ViewInject(R.id.setting_ver_name_tv)
    private TextView ver_name_tv;

    private void initVersionInfo(){
       String version_name = getResources().getString(R.string.version);
        int end_index = version_name.indexOf("V");
        version_name = version_name.substring(0,end_index);
        version_name = version_name + Utility.getVersionName(this);
        ver_name_tv.setText(version_name);
    }
}
