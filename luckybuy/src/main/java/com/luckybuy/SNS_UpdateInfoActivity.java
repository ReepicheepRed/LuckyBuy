package com.luckybuy;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.luckybuy.layout.CustomAlertDialog;
import com.luckybuy.layout.LoadingDialog;
import com.luckybuy.login.LoginUserUtils;
import com.luckybuy.model.ResultItemModel;
import com.luckybuy.model.VerifyCodeModel;
import com.luckybuy.network.ParseData;
import com.luckybuy.network.TokenVerify;
import com.luckybuy.util.Constant;
import com.luckybuy.util.Utility;

import org.xutils.common.Callback;
import org.xutils.http.RequestParams;
import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.Event;
import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

import java.util.Map;

import cn.smssdk.EventHandler;
import cn.smssdk.SMSSDK;

/**
 * Created by zhiPeng.S on 2016/6/14.
 */
@ContentView(R.layout.activity_sns_update)
public class SNS_UpdateInfoActivity extends BaseActivity implements View.OnClickListener{

    private SharedPreferences preferences;
    //if updateInfo=0 update nickname;
    //if updateInfo=1 update phone;
    private int updateInfo;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SMSSDK.registerEventHandler(eh); //注册短信回调
        preferences = LoginUserUtils.getUserSharedPreferences(this);
        confirm_tv.setVisibility(View.VISIBLE);
        confirm_tv.setText(R.string.determine);
        Intent intent = getIntent();
        updateInfo = intent.getIntExtra("updateInfo",-1);
        showInterface(updateInfo);
    }

    private void showInterface(int info){
        switch (info){
            case 0:
                //update nickname
                title_tv.setText(R.string.update_nickname);
                nickname_update_ll.setVisibility(View.VISIBLE);
                name_et.addTextChangedListener(textWatcher);
                String user_name = preferences.getString(Constant.USER_NAME,"");
                name_et.setText(user_name);

                break;
            case 1:
                //update phone
                title_tv.setText(R.string.update_phone);
                phone_update_ll.setVisibility(View.VISIBLE);
                phone_et.addTextChangedListener(textWatcher);
                String user_phone = preferences.getString(Constant.USER_PHONE,"");
                phone_et.setText(user_phone);

                break;
        }
    }

    @ViewInject(R.id.title_activity)
    private TextView title_tv;

    @ViewInject(R.id.right_view)
    private TextView confirm_tv;

    @ViewInject(R.id.nickname_update_ll)
    private LinearLayout nickname_update_ll;

    @ViewInject(R.id.phone_update_ll)
    private LinearLayout phone_update_ll;

    @ViewInject(R.id.nickname_update_et)
    private EditText name_et;

    @ViewInject(R.id.phone_update_et)
    private EditText phone_et;

    @ViewInject(R.id.sms_verify_code_et)
    private EditText verify_code_et;

    @ViewInject(R.id.sms_veify_code_btn)
    private TextView sms_veify_code_btn;

    @ViewInject(R.id.nickname_clear_iv)
    private ImageView name_clear_iv;

    @ViewInject(R.id.phone_clear_iv)
    private ImageView phone_clear_iv;

    CustomAlertDialog dialog;
    @Event({R.id.back_iv,R.id.right_view,R.id.sms_veify_code_btn,R.id.nickname_clear_iv,R.id.phone_clear_iv})
    private void viewClick(View view){
        switch(view.getId()){
            case R.id.back_iv:
                dialog = new CustomAlertDialog(this,this);
                switch (updateInfo){
                    case 0:
                        dialog.setSubTitle(R.string.nickname_dialog_subtitle);
                        break;
                    case 1:
                        dialog.setSubTitle(R.string.phone_dialog_subtitle);
                        break;
                }
                dialog.show();
                break;
            case R.id.right_view:
                long user_id = preferences.getLong(Constant.USER_ID,0);
                String user_idStr = user_id + "";
                if(user_id == 0) return;
                String modify ;
                String updateInfoStr;
                switch (updateInfo){
                    case 0:
                        modify = "nickname";
                        updateInfoStr = name_et.getText().toString();
                        updateSNSInfo(user_idStr,modify,updateInfoStr,null);
                        break;
                    case 1:
                        updateInfoStr = phone_et.getText().toString();
                        String code = verify_code_et.getText().toString();
                        check_phone_info(updateInfoStr,code);
//                        SMSSDK.submitVerificationCode(country, updateInfoStr, code);
                        break;
                }
                break;
            case R.id.sms_veify_code_btn:
//                SMSSDK.getSupportedCountries();
                String phone = phone_et.getText().toString();
                if(phone.length() != 10 && phone.length() != 11)  {
                    Utility.toastShow(x.app(),R.string.phone_length_error);
                    return;
                }
                getVerifyCode(phone);
                VerifyCountDownTimer timer = new VerifyCountDownTimer(60*1000,1000);
                timer.start();
                sms_veify_code_btn.setEnabled(true);
                break;
            case R.id.nickname_clear_iv:
                name_et.setText("");
                break;
            case R.id.phone_clear_iv:
                phone_et.setText("");
                break;
        }
    }

    private String country = "";
    EventHandler eh=new EventHandler(){

        @Override
        public void afterEvent(int event, int result, Object data) {

            if (result == SMSSDK.RESULT_COMPLETE) {
                //回调完成
                if (event == SMSSDK.EVENT_SUBMIT_VERIFICATION_CODE) {
                    //提交验证码成功
                    Log.e(TAG, "afterEvent: " + data.toString());
                    //check_phone_info();
                }else if (event == SMSSDK.EVENT_GET_VERIFICATION_CODE){
                    //获取验证码成功
                    Utility.toastShow(x.app(),R.string.send_verify_code_success);
                    //Log.e(TAG, "afterEvent: " + data.toString());
                }else if (event ==SMSSDK.EVENT_GET_SUPPORTED_COUNTRIES){
                    //返回支持发送验证码的国家列表
                    String phone = phone_et.getText().toString();
                    if(phone.length() == 11){
                        SMSSDK.getVerificationCode("86", phone);
                        country = "86";
                    }else if(phone.length() == 10){
                        SMSSDK.getVerificationCode("66", phone);
                        country = "66";
                    }
                    //Log.e(TAG, "afterEvent: " + data.toString());
                }
            }else{
                ((Throwable)data).printStackTrace();
            }
        }
    };

    private void check_phone_info(String phone,String verifycode) {
        long user_id = preferences.getLong(Constant.USER_ID,0);
        String user_idStr = user_id + "";
        String modify = "mobile";

        if(phone.length() != 11 && phone.length() != 10) {
            Utility.toastShow(x.app(),R.string.phone_length_error);
            return;
        }
        if(!verifycode.equals(model.getVerifycode())) {
            Utility.toastShow(x.app(),R.string.verifyCode_error);
            return;
        }

        updateSNSInfo(user_idStr,modify,null,phone);

    }


    TextWatcher textWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            name_clear_iv.setVisibility(View.GONE);
            phone_clear_iv.setVisibility(View.GONE);
            if(s.length() > 0){
                name_clear_iv.setVisibility(View.VISIBLE);
                phone_clear_iv.setVisibility(View.VISIBLE);
            }
        }

        @Override
        public void afterTextChanged(Editable s) {

        }
    };

    LoadingDialog loadingDialog;
    private void updateSNSInfo(String uidx,String modify,String nickname,String mobile){
        loadingDialog = new LoadingDialog(this);
        loadingDialog.showDialog();
        RequestParams params = new RequestParams(Constant.getBaseUrl() + "Page/Ucenter/membermodify.ashx");
        TokenVerify.addToken(this,params);
        params.addBodyParameter("uidx", uidx);
        params.addBodyParameter("modify", modify);
        if(nickname != null)
            params.addBodyParameter("nickname", nickname);
        else if(mobile != null)
            params.addBodyParameter("mobile", mobile);
        x.http().post(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                loadingDialog.dismiss();
                if(result.toUpperCase().equals("SUCCESS")){
                    TokenVerify.saveCookie(SNS_UpdateInfoActivity.this);
                    Utility.toastShow(x.app(),R.string.updateInfo_success);
                    SNS_UpdateInfoActivity.this.setResult(Constant.RESULT_CODE_UPDATE);
                    SNS_UpdateInfoActivity.this.finish();
                }else{
                    Utility.toastShow(x.app(),R.string.updateInfo_fail);
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


    @Override
    public void onClick(View v) {
        switch(v.getId()) {
            case R.id.alertDialog_ok_btn:
                this.finish();
                break;
            case R.id.alertDialog_cancel_btn:
                break;
        }
        dialog.dismiss();
    }


    private class VerifyCountDownTimer extends CountDownTimer{
        private String verifyCode;
        private String verifyTimer;
        public VerifyCountDownTimer(long millisInFuture, long countDownInterval) {
            super(millisInFuture, countDownInterval);
            verifyCode = getString(R.string.verify_code);
        }

        @Override
        public void onTick(long millisUntilFinished) {
            verifyTimer = verifyCode + "(" + millisUntilFinished/1000 + ")";
            sms_veify_code_btn.setText(verifyTimer);
        }

        @Override
        public void onFinish() {
            this.cancel();
            sms_veify_code_btn.setText(verifyCode);
            sms_veify_code_btn.setEnabled(false);
        }
    }

    private VerifyCodeModel model;
    private void getVerifyCode(String mobile){
        RequestParams params = new RequestParams(Constant.getBaseUrl() + "Page/Ucenter/PSWVerify.ashx");
        params.addQueryStringParameter("mobile", mobile);
        params.addQueryStringParameter("verifytype", "password");
        x.http().get(params, new Callback.CommonCallback<String>() {

            @Override
            public void onSuccess(String result) {
                Map<String, Object> resultMap = ParseData.parseVerifyCodeInfo(result);
                if (!result.isEmpty()) {
                    model = (VerifyCodeModel) resultMap.get(Constant.AWARD_LIST);
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
}
