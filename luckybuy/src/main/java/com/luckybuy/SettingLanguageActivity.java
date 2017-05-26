package com.luckybuy;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.luckybuy.login.LoginUserUtils;
import com.luckybuy.util.Constant;
import com.luckybuy.util.Utility;

import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.Event;
import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

import java.util.Locale;

/**
 * Created by zhiPeng.S on 2016/6/14.
 */
@ContentView(R.layout.activity_language_setting)
public class SettingLanguageActivity extends BaseActivity{

    private SharedPreferences preferences;
    private SharedPreferences.Editor editor;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        title_tv.setText(R.string.setting_language);
        preferences = LoginUserUtils.getUserSharedPreferences(this);
        editor = preferences.edit();
        initFlag();
    }

    @ViewInject(R.id.title_activity)
    private TextView title_tv;


    @Event({R.id.back_iv,R.id.chinese_rl,R.id.english_rl,R.id.thai_rl})
    private void viewClick(View view){
        switch(view.getId()){
            case R.id.back_iv:
                this.finish();
                break;
            case R.id.chinese_rl:
                selecteLanguage(chiness);
                break;
            case R.id.english_rl:
                selecteLanguage(english);
                break;
            case R.id.thai_rl:
                selecteLanguage(thai);
                break;
        }
    }

    private final int chiness = 0, english = 1, thai = 2;
    @ViewInject(R.id.chinese_flag_iv)
    private ImageView chinese_flag;
    @ViewInject(R.id.english_flag_iv)
    private ImageView english_flag;
    @ViewInject(R.id.thai_flag_iv)
    private ImageView thai_flag;

    private String chineseAtr = "zh_CN", englishAtr = "en_US", thaiAtr = "th_TH";

    private void initFlag(){
        ImageView[] flag = {chinese_flag,english_flag,thai_flag};
        for (int i = 0; i < flag.length ; i++) {
            flag[i].setVisibility(View.GONE);

            String language = preferences.getString(Constant.LANGUAGE,"");
            int languageAtr = language.equals(chineseAtr) ? chiness : language.equals(thaiAtr) ? thai : english;
            switch (languageAtr){
                case chiness:
                    flag[chiness].setVisibility(View.VISIBLE);
                    break;
                case english:
                    flag[english].setVisibility(View.VISIBLE);
                    break;
                case thai:
                    flag[thai].setVisibility(View.VISIBLE);
                    break;
            }
        }
    }

    private void selecteLanguage(int index){
        ImageView[] flag = {chinese_flag,english_flag,thai_flag};
        for (int i = 0; i < flag.length ; i++) {
            flag[i].setVisibility(View.GONE);
            if (i == index){
                flag[i].setVisibility(View.VISIBLE);
                switch (i){
                    case chiness:
                        Utility.changeAppLanguage(x.app().getResources(),chineseAtr);
                        editor.putString(Constant.LANGUAGE,chineseAtr);
                        editor.commit();
                        break;
                    case english:
                        Utility.changeAppLanguage(x.app().getResources(),englishAtr);
                        editor.putString(Constant.LANGUAGE,englishAtr);
                        editor.commit();
                        break;
                    case thai:
                        Utility.changeAppLanguage(x.app().getResources(),thaiAtr);
                        editor.putString(Constant.LANGUAGE,thaiAtr);
                        editor.commit();
                        break;

                }
                Intent intent = new Intent(this,MainActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
            }
        }
    }


}
