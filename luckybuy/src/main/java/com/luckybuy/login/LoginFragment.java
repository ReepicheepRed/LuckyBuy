package com.luckybuy.login;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.luckybuy.BaseFragment;
import com.luckybuy.LuckyBuy_Mine;
import com.luckybuy.R;
import com.luckybuy.layout.LoadingDialog;
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
 * Created by zhiPeng.S on 2016/5/30.
 */
@ContentView(R.layout.activity_login)
public class LoginFragment extends BaseFragment{

    private SharedPreferences preferences;
    private SharedPreferences.Editor editor;
    
    
   /* @ViewInject(R.id.login_button)
    private LoginButton loginButton;*/
    private CallbackManager callbackManager;

/*    public static LoginFragment newInstance(int sectionNumber) {
        LoginFragment fragment = new LoginFragment();
        Bundle args = new Bundle();
        args.putInt("section_number", sectionNumber);
        fragment.setArguments(args);
        return fragment;
    }*/

    public static LoginFragment newInstance(LuckyBuy_Mine mine_fragment) {
        LoginFragment fragment = new LoginFragment();
        /*Bundle args = new Bundle();
        args.putInt("section_number", sectionNumber);
        fragment.setArguments(args);*/
        fragment.setFragment(mine_fragment);
        return fragment;
    }

    private LuckyBuy_Mine mine_fragment;
    public void setFragment(LuckyBuy_Mine mine_fragment){
        this.mine_fragment = mine_fragment;
    }



    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initView();
        init_facebook();
    }

    private void initView(){
        preferences = LoginUserUtils.getUserSharedPreferences(getActivity());
        editor = preferences.edit();
        phone_et.addTextChangedListener(textWatcher);
        cancel_iv.setVisibility(View.INVISIBLE);
        mine_fragment = (LuckyBuy_Mine) getParentFragment();
    }

   /* private void initFacebook(){
                callbackManager = CallbackManager.Factory.create();
        loginButton.setReadPermissions("user_friends");
        loginButton.setFragment(this);
        loginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                Toast.makeText(x.app(), "Login successful", Toast.LENGTH_SHORT).show();
                Log.e("FriendsList",loginResult.toString());
                GraphRequest request = GraphRequest.newMeRequest(
                        AccessToken.getCurrentAccessToken(),
                        new GraphRequest.GraphJSONObjectCallback() {
                            @Override
                            public void onCompleted(
                                    JSONObject object,
                                    GraphResponse response) {
                                Toast.makeText(x.app(),response.toString(),Toast.LENGTH_SHORT).show();
                                Log.e("userInfo", response.toString());
                            }
                        });
                Bundle parameters = new Bundle();
                parameters.putString("fields", "id,name,link");
                request.setParameters(parameters);
                request.executeAsync();
            }

            @Override
            public void onCancel() {
                Toast.makeText(x.app(), "Login canceled", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onError(FacebookException exception) {
                Toast.makeText(x.app(), "Login error", Toast.LENGTH_SHORT).show();
            }
        });
    }*/

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);
    }

    @ViewInject(R.id.cancel_login_iv)
    private ImageView cancel_iv;

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
                if (!(accessToken == null || accessToken.isExpired())) {
                    LoginManager.getInstance().logOut();
                    loginOutBtn.setVisibility(View.GONE);
                }
                break;
            case R.id.register_ib:
                intent.setClass(getActivity(),RegisterActivity.class);
                startActivityForResult(intent,Constant.REQUEST_CODE);
                break;
            case R.id.login_btn:
                check_login_information();
                break;
            case R.id.cancel_login_iv:
                //x.app().finish();
                break;
            case R.id.forget_pwd_tv:
                intent.setClass(getActivity(),UpdatePwd.class);
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

    LoadingDialog loadingDialog;

    private void login(String mobile, String password){
        this.login(mobile,password,null);
    }

    private void login(String id, String name, String picture){
        loadingDialog = new LoadingDialog(getActivity());
        loadingDialog.showDialog();
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
                loadingDialog.dismiss();
                Log.e(TAG, "onSuccess: " + result);
                //Toast.makeText(x.app(), "Success：" + result, Toast.LENGTH_SHORT).show();
                try {
                    long user_id = Long.valueOf(result);
                    if (user_id == 0) {
                        Utility.toastShow(getActivity(), R.string.login_fail);
                        return;
                    }
                    editor.putLong(Constant.USER_ID, user_id);
                    editor.commit();
                } catch (Exception e) {

                }

                //save cookie
                TokenVerify.saveCookie(getActivity());

                InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                if (imm.isActive()) {
                    imm.hideSoftInputFromWindow(pwd_et.getApplicationWindowToken(), 0);
                }
                phone_et.setText("");
                pwd_et.setText("");

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



    private void sendClientId(){
        String cid = preferences.getString(Constant.CLIENT_ID,"");
        long user_id = preferences.getLong(Constant.USER_ID,0);

        if(cid.equals("") && user_id == 0) return;

        RequestParams params = new RequestParams(Constant.getBaseUrl() + "Handle/Getui/GetID.ashx");
        TokenVerify.addToken(getActivity(),params);
        params.addBodyParameter("geituiclientid", cid);

        params.addBodyParameter("uidx", user_id + "");

        x.http().post(params, new Callback.CommonCallback<String>() {

            @Override
            public void onSuccess(String result) {
                TokenVerify.saveCookie(getActivity());
                if (mine_fragment != null)
                    mine_fragment.updateFragment();
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
    private void init_facebook(){
        callbackManager = CallbackManager.Factory.create();
        progressDialog = new ProgressDialog(getActivity());
        progressDialog.setMessage(getActivity().getString(R.string.friends_login_fb));

        LoginManager.getInstance().registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                Toast.makeText(x.app(), "facebook_account_oauth_Success", Toast.LENGTH_SHORT).show();
                Log.e(TAG, "token: " + loginResult.getAccessToken().getToken());
                progressDialog.show();
                /*AccessToken accessToken = AccessToken.getCurrentAccessToken();
                if (accessToken == null || accessToken.isExpired()) {
                    loginOutBtn.setVisibility(View.GONE);
                    return;
                }

                loginOutBtn.setVisibility(View.VISIBLE);*/
                Log.e("FriendsList",loginResult.toString());

                GraphRequest request = GraphRequest.newMeRequest(
                        AccessToken.getCurrentAccessToken(),
                        new GraphRequest.GraphJSONObjectCallback() {
                            @Override
                            public void onCompleted(JSONObject object,GraphResponse response) {
                                //Toast.makeText(x.app(),response.toString(),Toast.LENGTH_SHORT).show();
                                Log.e("userInfo", response.toString());
                                progressDialog.dismiss();
                                if (object != null) {
                                    try {
                                        String facebook_idStr = object.getString("id");
                                        long facebook_id = Long.valueOf(facebook_idStr);
                                        editor.putLong(Constant.USER_ID_FB,facebook_id);
                                        editor.commit();
                                        String pictureUrl = object.getJSONObject("picture").getJSONObject("data").getString("url");
                                        login(facebook_idStr, object.getString("name"), pictureUrl);
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
                            public void onCompleted(JSONArray jsonArray, GraphResponse response) {
                                //Toast.makeText(x.app(),response.toString(),Toast.LENGTH_SHORT).show();
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
                Toast.makeText(x.app(), "facebook_account_oauth_Cancel", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onError(FacebookException e) {
                Toast.makeText(x.app(), "facebook_account_oauth_Error", Toast.LENGTH_SHORT).show();
                Log.e(TAG, "e: " + e);
            }
        });
    }


}
