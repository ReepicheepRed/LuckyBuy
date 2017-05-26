package com.luckybuy.login;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.appevents.AppEventsLogger;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.luckybuy.BaseActivity;
import com.luckybuy.R;
import com.luckybuy.network.TokenVerify;
import com.luckybuy.util.Constant;
import com.luckybuy.util.StringUtil;
import com.luckybuy.util.Utility;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.xutils.common.Callback;
import org.xutils.http.RequestParams;
import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.Event;
import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

import java.util.Arrays;

/**
 * Created by zhiPeng.S on 2016/6/17.
 */
@ContentView(R.layout.activity_login)
public class LoginActivity extends BaseActivity{

    private CallbackManager callbackManager;
    private SharedPreferences preferences;
    private SharedPreferences.Editor editor;

    @ViewInject(R.id.login_button)
    private LoginButton loginButton;

    @ViewInject(R.id.tempExitBtn)
    private Button loginOutBtn;

    @Event({R.id.fb_login,R.id.tempExitBtn,R.id.register_ib,
            R.id.login_btn,R.id.cancel_login_iv,R.id.forget_pwd_tv})
    private void viewClick(View view){
        AccessToken accessToken = AccessToken.getCurrentAccessToken();
        Intent intent = new Intent();
        switch (view.getId()){
            case R.id.fb_login:
                if (!(accessToken == null || accessToken.isExpired())) {
                    LoginManager.getInstance().logOut();
                }
                LoginManager.getInstance().logInWithReadPermissions(this, Arrays.asList("public_profile", "user_friends"));
                break;
            case R.id.tempExitBtn:
                if (accessToken != null && !accessToken.isExpired()) {
                    LoginManager.getInstance().logOut();
                    loginOutBtn.setVisibility(View.GONE);
                }
                break;
            case R.id.register_ib:
                intent.setClass(this,RegisterActivity.class);
                startActivityForResult(intent,Constant.REQUEST_CODE);
                break;
            case R.id.login_btn:
                check_login_information();
                break;
            case R.id.cancel_login_iv:
                LoginActivity.this.finish();
                break;
            case R.id.forget_pwd_tv:
                intent.setClass(this,UpdatePwd.class);
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
           if(phone_et.hasFocus() && StringUtil.isPhoneNo(phone_et.getText().toString())){
               pwd_et.requestFocus();
           }
        }
    };

    private void check_login_information(){
        String phoneStr = phone_et.getText().toString();
        String pwdStr = pwd_et.getText().toString();
        //if(StringUtil.isPhoneNo(phoneStr)){
                //if(pwdStr.length()==12){
                    login(phoneStr,pwdStr);
                //}
        //}
    }

    @ViewInject(R.id.phone_login_et)
    private EditText phone_et;

    @ViewInject(R.id.pwd_login_et)
    private EditText pwd_et;

    private void login(String mobile, String password){
        this.login(mobile,password,null);
    }

    private void login(String id, String name, String picture){

        RequestParams params = new RequestParams(Constant.getBaseUrl() + "page/ucenter/login.ashx");
        JSONObject jsonObject = new JSONObject();
        try {
            if(picture == null){
                params.addQueryStringParameter("ismobile","true");
                params.addBodyParameter("mobile", id);
                params.addBodyParameter("password", name);
            }else {
                jsonObject.put("name", name);
                jsonObject.put("id", id);
                jsonObject.put("picture", picture);
                params.addBodyParameter("data",jsonObject.toString());
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

        x.http().post(params, new Callback.CommonCallback<String>() {

            @Override
            public void onSuccess(String result) {
                Log.e(TAG, "onSuccess: " + result);
                //Toast.makeText(LoginActivity.this, "Success："+ result, Toast.LENGTH_SHORT).show();
                try {
                    long user_id = Long.valueOf(result);
                    if(user_id == 0){
                        Utility.toastShow(LoginActivity.this,R.string.login_fail);
                        return;
                    }
                    editor.putLong(Constant.USER_ID, user_id);
                    editor.commit();
                }catch (Exception e){

                }

                TokenVerify.saveCookie(LoginActivity.this);
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

    private void commit_fb_friends(JSONArray jsonArray){
        RequestParams params = new RequestParams(Constant.getBaseUrl() + "page/ucenter/FB_Firend_Add.ashx");
        long user_id_fb = preferences.getLong(Constant.USER_ID_FB,0);
        if(user_id_fb == 0) return;
        params.addQueryStringParameter("id", user_id_fb+"");
        params.addQueryStringParameter("data", jsonArray.toString());

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

    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        phone_et.addTextChangedListener(textWatcher);

        preferences = LoginUserUtils.getUserSharedPreferences(this);
        editor = preferences.edit();

        callbackManager = CallbackManager.Factory.create();
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage(getString(R.string.friends_login_fb));

        LoginManager.getInstance().registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                Toast.makeText(LoginActivity.this, "facebook_account_oauth_Success", Toast.LENGTH_SHORT).show();
                Log.e(TAG, "token: " + loginResult.getAccessToken().getToken());
                progressDialog.show();

                AccessToken accessToken = AccessToken.getCurrentAccessToken();
                if (accessToken == null || accessToken.isExpired()) {
                    loginOutBtn.setVisibility(View.GONE);
                    return;
                }

                loginOutBtn.setVisibility(View.VISIBLE);
                Log.e("FriendsList",loginResult.toString());

                GraphRequest request = GraphRequest.newMeRequest(
                        AccessToken.getCurrentAccessToken(),
                        new GraphRequest.GraphJSONObjectCallback() {
                            @Override
                            public void onCompleted(JSONObject object,GraphResponse response) {
                                //Toast.makeText(LoginActivity.this,response.toString(),Toast.LENGTH_SHORT).show();
                                Log.e("userInfo", response.toString());
                                progressDialog.dismiss();

                                if (object != null) {
                                    try {
                                        //if (object.getInt("responseCode") == 200){
                                            //JSONObject userInfo = object.getJSONObject("graphObject");
                                        String facebook_idStr = object.getString("id");
                                        long facebook_id = Long.valueOf(facebook_idStr);
                                        editor.putLong(Constant.USER_ID_FB,facebook_id);
                                        editor.commit();
                                        String pictureUrl = object.getJSONObject("picture").getJSONObject("data").getString("url");
                                        login(facebook_idStr, object.getString("name"), pictureUrl);
                                       // }
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                }

                            }
                        });
                Bundle parameters = new Bundle();
                parameters.putString("fields", "id,name,picture");
                request.setParameters(parameters);
                request.executeAsync();


                GraphRequest request_friends = GraphRequest.newMyFriendsRequest(
                        AccessToken.getCurrentAccessToken(),
                        new GraphRequest.GraphJSONArrayCallback() {
                            @Override
                            public void onCompleted(JSONArray jsonArray,GraphResponse response) {
                                //Toast.makeText(LoginActivity.this,response.toString(),Toast.LENGTH_SHORT).show();
                                Log.e("appFriends",response.toString());
                                commit_fb_friends(jsonArray);
                            }
                        });
                Bundle parameters_friends = new Bundle();
                parameters_friends.putString("fields", "id,name,picture");
                request_friends.setParameters(parameters_friends);
                request_friends.executeAsync();
            }

            @Override
            public void onCancel() {
                Toast.makeText(LoginActivity.this, "facebook_account_oauth_Cancel", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onError(FacebookException e) {
                Toast.makeText(LoginActivity.this, "facebook_account_oauth_Error", Toast.LENGTH_SHORT).show();
                Log.e(TAG, "e: " + e);
            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Constant.RESULT_CODE){
            long user_id = preferences.getLong(Constant.USER_ID, 0);
            if (user_id != 0) {
                LoginActivity.this.finish();
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Logs 'install' and 'app activate' App Events.
        AppEventsLogger.activateApp(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        // Logs 'app deactivate' App Event.
        AppEventsLogger.deactivateApp(this);
    }

    private void sendClientId(){
        String cid = preferences.getString(Constant.CLIENT_ID,"");
        long user_id = preferences.getLong(Constant.USER_ID,0);

        if(cid.equals("") && user_id == 0) return;

        RequestParams params = new RequestParams(Constant.getBaseUrl() + "Handle/Getui/GetID.ashx");
        TokenVerify.addToken(this,params);
        params.addBodyParameter("geituiclientid", cid);
        params.addBodyParameter("uidx", user_id + "");

        x.http().post(params, new Callback.CommonCallback<String>() {

            @Override
            public void onSuccess(String result) {
                TokenVerify.saveCookie(LoginActivity.this);
                setResult(Constant.RESULT_CODE_UPDATE);
                LoginActivity.this.finish();
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
