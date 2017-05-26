package com.luckybuy.login;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.luckybuy.R;
import com.luckybuy.network.TokenVerify;
import com.luckybuy.presenter.FriendsListPresenter;
import com.luckybuy.util.Constant;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.xutils.common.Callback;
import org.xutils.http.RequestParams;
import org.xutils.x;

/**
 * Created by zhiPeng.S on 2016/10/11.
 */

public class FaceBookLogin {
    private static final String TAG = "FaceBookLogin";
    private static SharedPreferences preferences;
    private static SharedPreferences.Editor editor;
    public static CallbackManager callbackManager;
    private static ProgressDialog progressDialog;
    private static boolean hasFBaccount;
    private static FriendsListPresenter friendsListPresenter;

    public boolean hasFBaccount() {
        return hasFBaccount;
    }

    public static FriendsListPresenter getFriendsListPresenter() {
        return friendsListPresenter;
    }

    public static void setFriendsListPresenter(FriendsListPresenter friendsListPresenter) {
        FaceBookLogin.friendsListPresenter = friendsListPresenter;
    }

    public static void init_facebook(Context context){
        callbackManager = CallbackManager.Factory.create();
        preferences = LoginUserUtils.getUserSharedPreferences(context);
        editor = preferences.edit();
        progressDialog = new ProgressDialog(context);
        progressDialog.setMessage(context.getString(R.string.friends_login_fb));
        LoginManager.getInstance().registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                Toast.makeText(x.app(), "facebook_account_oauth_Success", Toast.LENGTH_SHORT).show();
                Log.e(TAG, "token: " + loginResult.getAccessToken().getToken());
                Log.e("FriendsList",loginResult.toString());
                progressDialog.show();

                GraphRequest request = GraphRequest.newMeRequest(
                        AccessToken.getCurrentAccessToken(),
                        new GraphRequest.GraphJSONObjectCallback() {
                            @Override
                            public void onCompleted(JSONObject object, GraphResponse response) {
                                Log.e("userInfo", response.toString());
                                progressDialog.dismiss();
                                if (object != null) {
                                    try {
                                        long facebook_id = Long.valueOf(object.getString("id"));
                                        editor.putLong(Constant.USER_ID_FB,facebook_id);
                                        editor.commit();
                                        String pictureUrl = object.getJSONObject("picture").getJSONObject("data").getString("url");
                                        long user_id = preferences.getLong(Constant.USER_ID,0);
                                        if(user_id != 0){
                                            //bind facebook account
                                            JSONObject jsonObject = new JSONObject();
                                            jsonObject.put("name", object.getString("name"));
                                            jsonObject.put("id", facebook_id);
                                            jsonObject.put("picture", pictureUrl);
                                            bindFaceBook(user_id+"",jsonObject.toString());
                                        }else{
                                            login(object.getString("id"), object.getString("name"), pictureUrl);
                                        }
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

    public static void login(String id, String name, String picture){

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
                try {
                    long user_id = Long.valueOf(result);
                    editor.putLong(Constant.USER_ID, user_id);
                    editor.commit();
                }catch (Exception e){
                    e.printStackTrace();
                }

                TokenVerify.saveCookie(x.app());
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

    public static void commit_fb_friends(JSONArray jsonArray){
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

    public static void sendClientId(){
        String cid = preferences.getString(Constant.CLIENT_ID,"");
        long user_id = preferences.getLong(Constant.USER_ID,0);

        if(cid.equals("") || user_id == 0) return;

        RequestParams params = new RequestParams(Constant.getBaseUrl() + "Handle/Getui/GetID.ashx");
        TokenVerify.addToken(x.app(),params);
        params.addBodyParameter("getuiclientid", cid);
        params.addBodyParameter("uidx", user_id + "");

        x.http().post(params, new Callback.CommonCallback<String>() {

            @Override
            public void onSuccess(String result) {
                TokenVerify.saveCookie(x.app());
//                init();
                friendsListPresenter.selectLayout();
                hasFBaccount = true;
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

    public static void bindFaceBook(String uidx,String facebookInfo){
        RequestParams params = new RequestParams(Constant.getBaseUrl() + "Page/Ucenter/MemberBindFB.ashx");
        TokenVerify.addToken(x.app(),params);
        params.addBodyParameter("uidx", uidx);
        params.addBodyParameter("data",facebookInfo);
        Log.d(TAG, "bindFaceBook: " + uidx + "&" + facebookInfo);
        x.http().post(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                Log.d(TAG, "bindFaceBook: "  + result);
                if(result.toUpperCase().equals("SUCCESS")){
                    Log.d(TAG, "bindFaceBook: " + "SUCCESS"  + result);
                    TokenVerify.saveCookie(x.app());
//                    init();
                    friendsListPresenter.selectLayout();
                    hasFBaccount = true;
                }
            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
                Log.d(TAG, "bindFaceBook: onError");
            }

            @Override
            public void onCancelled(CancelledException cex) {
                Log.d(TAG, "bindFaceBook: onCancelled");
            }

            @Override
            public void onFinished() {
                Log.d(TAG, "bindFaceBook: onFinished");
            }
        });
    }

}
