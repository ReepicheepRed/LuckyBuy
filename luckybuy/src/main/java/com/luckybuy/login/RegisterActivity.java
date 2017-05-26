package com.luckybuy.login;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Paint;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.Editable;
import android.text.SpannableString;
import android.text.TextWatcher;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.luckybuy.BaseActivity;
import com.luckybuy.R;
import com.luckybuy.WebActivity;
import com.luckybuy.model.VerifyCodeModel;
import com.luckybuy.network.ParseData;
import com.luckybuy.network.TokenVerify;
import com.luckybuy.util.Constant;
import com.luckybuy.util.StringUtil;
import com.luckybuy.util.Utility;

import org.json.JSONException;
import org.json.JSONObject;
import org.xutils.common.Callback;
import org.xutils.http.RequestParams;
import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.Event;
import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

import java.util.HashMap;
import java.util.Map;

import cn.smssdk.EventHandler;
import cn.smssdk.SMSSDK;
import cn.smssdk.gui.RegisterPage;

/**
 * Created by zhiPeng.S on 2016/6/28.
 */
@ContentView(R.layout.activity_register)
public class RegisterActivity extends BaseActivity {


    private SharedPreferences preferences;
    private SharedPreferences.Editor editor;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initView();
    }

    EventHandler eh=new EventHandler(){

        @Override
        public void afterEvent(int event, int result, Object data) {

            if (result == SMSSDK.RESULT_COMPLETE) {
                //回调完成
                if (event == SMSSDK.EVENT_SUBMIT_VERIFICATION_CODE) {
                    //提交验证码成功
                    Log.e(TAG, "afterEvent: " + data.toString());
                    check_register_information();
                }else if (event == SMSSDK.EVENT_GET_VERIFICATION_CODE){
                    //获取验证码成功
                    //Utility.toastShow(x.app(),R.string.send_verify_code_success);
                    //Log.e(TAG, "afterEvent: " + data.toString());
                }else if (event ==SMSSDK.EVENT_GET_SUPPORTED_COUNTRIES){
                    //返回支持发送验证码的国家列表
                    String phone = register_et.getText().toString();
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

    private void initView(){
        SMSSDK.registerEventHandler(eh); //注册短信回调
        preferences = LoginUserUtils.getUserSharedPreferences(this);
        editor = preferences.edit();
        EditText[] editTexts = {register_et,verifyCode_et,pwd_reg_et};
        this.editTexts = editTexts.clone();
        for (int i = 0; i < editTexts.length; i++) {
            editTexts[i].addTextChangedListener(textWatcher);
        }

        protocol_tv.getPaint().setFlags(Paint. UNDERLINE_TEXT_FLAG ); //下划线
        protocol_tv.getPaint().setAntiAlias(true);//抗锯齿

    }


    @ViewInject(R.id.register_treaty_tv)
    private TextView protocol_tv;

    @ViewInject(R.id.verify_code_btn)
    private Button verify_code_btn;

    private String country = "";
    @Event({R.id.register_back_iv, R.id.register_btn,R.id.verify_code_btn,R.id.register_treaty_tv})
    private void viewClick(View view){
        switch (view.getId()){
            case R.id.register_back_iv:
                this.finish();
                break;
            case R.id.register_btn:
//                String phone = register_et.getText().toString();
//                String code = verifyCode_et.getText().toString();
//                SMSSDK.submitVerificationCode(country, phone, code);
                check_register_information();
                break;
            case R.id.verify_code_btn:
//                SMSSDK.getSupportedCountries();
                String phone = register_et.getText().toString();
                if(phone.length() != 10 && phone.length() != 11)  {
                    Utility.toastShow(x.app(),R.string.phone_length_error);
                    return;
                }
                verify_code_btn.setEnabled(false);
                getVerifyCode(phone);
                VerifyCountDownTimer timer = new VerifyCountDownTimer(60*1000,1000);
                timer.start();
                break;
            case R.id.register_treaty_tv:
                Intent intent = new Intent(RegisterActivity.this, WebActivity.class);
                intent.putExtra(Constant.WEB_H5,Constant.PROTOCOL);
                startActivity(intent);
                break;
        }
    }

    TextWatcher textWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {

        }

        @Override
        public void afterTextChanged(Editable s) {
            for (int i = 0; i < editTexts.length; i++) {
               if(editTexts[i].hasFocus())
                   switch (i){
                       case 0:
                           if(editTexts[i].getText().toString().length()>=11)
                               editTexts[i+1].requestFocus();
                           break;
                       case 1:
                           if(editTexts[i].getText().toString().length()>=4)
                               editTexts[i+1].requestFocus();
                           break;
                       case 2:
                           if(editTexts[i].getText().toString().length()>=12)
                           break;
                   }
            }
        }
    };

    @ViewInject(R.id.phone_register_et)
    private EditText register_et;

    @ViewInject(R.id.verifyCode_et)
    private EditText verifyCode_et;

    @ViewInject(R.id.pwd_reg_et)
    private EditText pwd_reg_et;

    private EditText[] editTexts = new EditText[3];

    private void check_register_information(){
        String phone = register_et.getText().toString();
        String verifycode = verifyCode_et.getText().toString();
        String pwd = pwd_reg_et.getText().toString();

        if(phone.length() != 11 && phone.length() != 10) {
            Utility.toastShow(x.app(),R.string.phone_length_error);
            return;
        }
        if(!verifycode.equals(model.getVerifycode())) {
            Utility.toastShow(x.app(),R.string.verifyCode_error);
            return;
        }
        if(pwd.length() < 6 || pwd.length() > 12) {
            Utility.toastShow(x.app(),R.string.pwd_length_error);
            return;
        }

        commit_register_information(phone,pwd,verifycode);
    }

    private void commit_register_information(String phone, String pwd, String verifycode){
        RequestParams params = new RequestParams(Constant.getBaseUrl() + "page/ucenter/register.ashx");

        params.addBodyParameter("mobile", phone,"form-data");
        params.addBodyParameter("password", pwd,"form-data");
        params.addBodyParameter("verifycode",verifycode,"form-data");


        x.http().post(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                Log.e(TAG, "onSuccess: " +result);
                try {
                    TokenVerify.saveCookie(RegisterActivity.this);
                    JSONObject jsonObject = new JSONObject(result);
                    boolean flag = jsonObject.has("success");
                    if(!flag) {
                        Utility.toastShow(RegisterActivity.this,R.string.register_fail);
                        return;
                    }
                    long user_id = jsonObject.getLong("success");
                    editor.putLong(Constant.USER_ID,user_id);
                    editor.commit();
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                sendClientId();
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

    private void sendClientId(){
        String cid = preferences.getString(Constant.CLIENT_ID,"");
        long user_id = preferences.getLong(Constant.USER_ID,0);

        if(cid.equals("") && user_id == 0) return;

        RequestParams params = new RequestParams(Constant.getBaseUrl() + "Handle/Getui/GetID.ashx");
        params.addBodyParameter("geituiclientid", cid);
        params.addBodyParameter("uidx", user_id + "");

        x.http().post(params, new Callback.CommonCallback<String>() {

            @Override
            public void onSuccess(String result) {
                RegisterActivity.this.setResult(Constant.RESULT_CODE);
                RegisterActivity.this.finish();
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

    private class VerifyCountDownTimer extends CountDownTimer {
        private String verifyCode;
        private String verifyTimer;
        public VerifyCountDownTimer(long millisInFuture, long countDownInterval) {
            super(millisInFuture, countDownInterval);
            verifyCode = getString(R.string.verify_code);
        }

        @Override
        public void onTick(long millisUntilFinished) {
            verifyTimer = verifyCode + "(" + millisUntilFinished/1000 + ")";
            verify_code_btn.setText(verifyTimer);
        }

        @Override
        public void onFinish() {
            this.cancel();
            verify_code_btn.setText(verifyCode);
            verify_code_btn.setEnabled(true);
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
