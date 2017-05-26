package com.luckybuy;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.luckybuy.login.LoginUserUtils;
import com.luckybuy.util.Constant;
import com.luckybuy.util.Utility;

import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.Event;
import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

/**
 * Created by zhiPeng.S on 2016/7/7.
 */
@ContentView(R.layout.activity_call_center)
public class CallCenterActivity extends BaseActivity{


    private String email = "luckybuy@deedeefun.com";
    //App issues report + 用户手机号 或者facebook账号
    private String subject = "App issues report %1$s";
    //content： 手机型号  OS版本  app版本
    //Device type：iPhone 5   Device OS ：9.3.4  App Version：2.1.3
    private String content = "Device type：%1$s  Device OS ：%2$s  App Version：%3$s";

    private SharedPreferences preferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        title_tv.setText(R.string.call_center);
        preferences = LoginUserUtils.getUserSharedPreferences(this);
        initEmail();
    }

    @ViewInject(R.id.title_activity)
    private TextView title_tv;

    @ViewInject(R.id.email_account_tv)
    private TextView email_account_tv;

    @Event({R.id.back_iv,R.id.call_center_faq_tv,R.id.call_center_email_us_tv})
    private void viewClick(View view){
        Intent intent = new Intent();
        switch (view.getId()){
            case R.id.back_iv:
                this.finish();
                break;
            case R.id.call_center_faq_tv:
                intent.setClass(this,WebActivity.class);
                intent.putExtra(Constant.WEB_H5,Constant.FAQ);
                startActivity(intent);
                break;
            case R.id.call_center_email_us_tv:
                /*Intent data = new Intent(Intent.ACTION_SEND);
                data.setData(Uri.parse(email));
                data.setType("text/plain");
                data.putExtra(Intent.EXTRA_SUBJECT, subject);
                data.putExtra(Intent.EXTRA_TEXT, content);
                if (data.resolveActivity(getPackageManager()) != null) {
                    startActivity(data);
                }else{
                    Utility.toastShow(x.app(),"no email app");
                }*/
                sendEmail();
                break;
        }
    }

    private void initEmail(){
        email_account_tv.setText(email);
        //subject
        String user_phone = preferences.getString(Constant.USER_PHONE,"");
        long user_id_fb = preferences.getLong(Constant.USER_ID_FB,0);
        if(!user_phone.equals(""))
            subject = String.format(subject,user_phone);
        else
            subject = String.format(subject,user_id_fb+"");
        //content
        String device_model = Build.MODEL; // 设备型号 。
        String version_sdk = Build.VERSION.SDK; // 设备SDK版本  。
        String version_release = Build.VERSION.RELEASE; // 设备的系统版本 。
        String version_name =  Utility.getVersionName(this);
        content = String.format(content,device_model,version_release,version_name);
    }

    private void sendEmail() {
        Log.i("Send email", "");

        String[] TO = {"amrood.admin@gmail.com"};
        String[] CC = {"mcmohd@gmail.com"};
        Intent emailIntent = new Intent(Intent.ACTION_SEND);
        emailIntent.setData(Uri.parse(email));
        emailIntent.setType("text/plain");


        emailIntent.putExtra(Intent.EXTRA_EMAIL, TO);
        emailIntent.putExtra(Intent.EXTRA_CC, CC);
        emailIntent.putExtra(Intent.EXTRA_SUBJECT, subject);
        emailIntent.putExtra(Intent.EXTRA_TEXT, content);

        try {
            startActivity(Intent.createChooser(emailIntent, "Send mail..."));

            Log.i("Finished sending email", "");
        } catch (android.content.ActivityNotFoundException ex) {
            Toast.makeText(CallCenterActivity.this,
                    "There is no email client installed.", Toast.LENGTH_SHORT).show();
        }
    }

    private void sendEmail2() {
        Log.i("Send email", "");

        String[] TO = {"amrood.admin@gmail.com"};
        String[] CC = {"mcmohd@gmail.com"};
        Intent emailIntent = new Intent(Intent.ACTION_SEND);
        emailIntent.setData(Uri.parse("mailto:"));
        emailIntent.setType("text/plain");


        emailIntent.putExtra(Intent.EXTRA_EMAIL, TO);
        emailIntent.putExtra(Intent.EXTRA_CC, CC);
        emailIntent.putExtra(Intent.EXTRA_SUBJECT, "Your subject");
        emailIntent.putExtra(Intent.EXTRA_TEXT, "Email message goes here");

        try {
            startActivity(Intent.createChooser(emailIntent, "Send mail..."));

            Log.i("Finished sending email", "");
        } catch (android.content.ActivityNotFoundException ex) {
            Toast.makeText(CallCenterActivity.this,
                    "There is no email client installed.", Toast.LENGTH_SHORT).show();
        }
    }

}
