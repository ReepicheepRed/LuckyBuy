/**
 * Copyright (c) 2014-present, Facebook, Inc. All rights reserved.
 *
 * You are hereby granted a non-exclusive, worldwide, royalty-free license to use,
 * copy, modify, and distribute this software in source code or binary form for use
 * in connection with the web services and APIs provided by Facebook.
 *
 * As with any software that integrates with the Facebook platform, your use of
 * this software is subject to the Facebook Developer Principles and Policies
 * [http://developers.facebook.com/policy/]. This copyright notice shall be
 * included in all copies or substantial portions of the software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS
 * FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR
 * COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER
 * IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN
 * CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package com.luckybuy;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Toast;
import android.widget.ViewFlipper;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.share.widget.MessageDialog;
import com.facebook.share.widget.ShareDialog;
import com.luckybuy.adapter.AwardFrsAdapter;
import com.luckybuy.adapter.ListBaseAdapter;
import com.luckybuy.login.LoginUserUtils;
import com.luckybuy.model.AwardFrsModel;
import com.luckybuy.model.AwardModel;
import com.luckybuy.network.ParseData;
import com.luckybuy.network.TokenVerify;
import com.luckybuy.share.FaceBookShare;
import com.luckybuy.util.Constant;
import com.luckybuy.util.Utility;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.xutils.common.Callback;
import org.xutils.ex.HttpException;
import org.xutils.http.RequestParams;
import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.Event;
import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

@ContentView(R.layout.friends_part_frag)
public class Home_Friends_Fragment extends BaseFragment{

    private SharedPreferences preferences;
    private SharedPreferences.Editor editor;

    @ViewInject(R.id.fb_login_rl)
    private RelativeLayout fb_login_rl;
    @ViewInject(R.id.viewFlipper_friends)
    private ViewFlipper vf_friends;

    private CallbackManager callbackManager;

    Handler mHandler = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            @SuppressWarnings("unchecked")
            Map<String, Object> result = (Map<String, Object>) msg.obj;
            switch (msg.what) {
                case R.id.AWARD_SUCCESS:
                    @SuppressWarnings("unchecked")
                    List<AwardFrsModel> showlist =
                            (List<AwardFrsModel>) result.get(Constant.AWARD_LIST);

                    boolean orientation = (boolean) result.get("pull_up");
                    if (!orientation) datas.clear();

                    int size = showlist == null ? 0 : showlist.size();
                    for (int i = 0; i < size; i++) datas.add(showlist.get(i));

                    adapter.setData(datas);
                    adapter.notifyDataSetChanged();

                    if (datas.isEmpty()) updateShowLayout(1);
                    break;
            }
        }
    };

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        /** init auth api**/
        //mShareAPI = UMShareAPI.get(x.app());

        preferences = LoginUserUtils.getUserSharedPreferences(getActivity());
        editor = preferences.edit();
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        //init listView
        setAwardList();

        init();

        init_Share();
    }

    private void init(){

        long user_id_fb = preferences.getLong(Constant.USER_ID_FB,0);
        if(user_id_fb != 0){
            updateShowLayout(2);
            long user_id = preferences.getLong(Constant.USER_ID,0);
            if(user_id != 0)
                getShowListInfo(user_id+"","10", "0", false);
        }else {
            updateShowLayout(0);
            init_facebook();
        }
    }

    private void updateShowLayout(int index){
        vf_friends.setDisplayedChild(index);
    }

    @Event({R.id.fb_login_rl,R.id.invite_friends_rl})
    private void viewClick(View view) {
        AccessToken accessToken = AccessToken.getCurrentAccessToken();
        switch(view.getId()){
            case R.id.fb_login_rl:
                if (!(accessToken == null || accessToken.isExpired())) {
                    LoginManager.getInstance().logOut();
                }
                LoginManager.getInstance().logInWithReadPermissions(this, Arrays.asList("public_profile", "user_friends"));
                break;
            case R.id.invite_friends_rl:
                isInvite = true;
                inviteFriends();
                break;
        }
    }

    private boolean isInvite;

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(callbackManager != null)
            callbackManager.onActivityResult(requestCode, resultCode, data);
        if(isInvite) {
            FaceBookShare.callbackManager.onActivityResult(requestCode, resultCode, data);
            isInvite = false;
        }
    }
    

    private void getUserInfo(){
        Toast.makeText(getActivity(), "Login successful", Toast.LENGTH_SHORT).show();
        GraphRequest request = GraphRequest.newMeRequest(
                AccessToken.getCurrentAccessToken(),
                new GraphRequest.GraphJSONObjectCallback() {
                    @Override
                    public void onCompleted(JSONObject object, GraphResponse response) {
                        Toast.makeText(getActivity(),response.toString(),Toast.LENGTH_SHORT).show();
                        Log.e("userInfo", response.toString());
                    }
                });
        Bundle parameters = new Bundle();
        parameters.putString("fields", "id,name,link");
        request.setParameters(parameters);
        request.executeAsync();
    }

    private void getFriendsInfo(){
        GraphRequest request = GraphRequest.newMyFriendsRequest(
                AccessToken.getCurrentAccessToken(),
                new GraphRequest.GraphJSONArrayCallback() {
                    @Override
                    public void onCompleted(JSONArray jsonArray,GraphResponse response) {
                        Toast.makeText(getActivity(),response.toString(),Toast.LENGTH_SHORT).show();
                        Log.e("appFriends","JsonArray :"+ jsonArray.toString()+ "response :" + response.toString());
                        if(jsonArray.length() == 0){
                            int vf_child_count = vf_friends.getChildCount();
                            for (int i = 0; i < vf_child_count; i++) {
                                if (i == 1) {
                                    vf_friends.setDisplayedChild(i);
                                }
                            }
                        }else{
                            vf_friends.setDisplayedChild(2);
                        }

                    }
                });
        Bundle parameters = new Bundle();
        parameters.putString("fields", "id,name,picture");
        request.setParameters(parameters);
        request.executeAsync();
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
                Log.e("FriendsList",loginResult.toString());
                progressDialog.show();

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
                            public void onCompleted(JSONArray jsonArray,GraphResponse response) {
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
                //Toast.makeText(x.app(), "Success："+ result, Toast.LENGTH_SHORT).show();
                try {
                    long user_id = Long.valueOf(result);
                    editor.putLong(Constant.USER_ID, user_id);
                    editor.commit();
                }catch (Exception e){

                }

                TokenVerify.saveCookie(getActivity());
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
                //Utility.toastShow(x.app(),"onSuccess: " + result );
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

        if(cid.equals("") || user_id == 0) return;

        RequestParams params = new RequestParams(Constant.getBaseUrl() + "Handle/Getui/GetID.ashx");
        TokenVerify.addToken(getActivity(),params);
        params.addBodyParameter("getuiclientid", cid);
        params.addBodyParameter("uidx", user_id + "");

        x.http().post(params, new Callback.CommonCallback<String>() {

            @Override
            public void onSuccess(String result) {
                TokenVerify.saveCookie(getActivity());
                init();
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


    @ViewInject(R.id.award_friends_Lv)
    private ListView listView;
    private AwardFrsAdapter adapter;
    private List<AwardFrsModel> datas;

    public ListBaseAdapter<AwardFrsModel> getAdapter() {
        return adapter;
    }

    private void setAwardList(){
        listView.setAdapter(getShowListAdapter());
    }

    private ListBaseAdapter<AwardFrsModel> getShowListAdapter(){
        datas = new ArrayList<>();
        adapter = new AwardFrsAdapter(getActivity(),datas);
        return adapter;
    }



    public void getShowListInfo(String idx, String pagesize, String pageindex, final boolean orientation){
        RequestParams params = new RequestParams(Constant.getBaseUrl() + "page/ucenter/FriendsBuy.ashx");
        params.addQueryStringParameter("idx", idx);
        params.addQueryStringParameter("pagesize", pagesize);
        params.addQueryStringParameter("pageindex", pageindex);
        x.http().get(params, new Callback.CommonCallback<String>() {

            @Override
            public void onSuccess(String result) {
                Map<String, Object> resultMap = ParseData.parseAwardFrsInfo(result);
                resultMap.put("pull_up", orientation);
                mHandler.obtainMessage(R.id.AWARD_SUCCESS, resultMap).sendToTarget();
            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
                //Toast.makeText(x.app(), ex.getMessage(), Toast.LENGTH_LONG).show();
                if (ex instanceof HttpException) { // 网络错误
                    HttpException httpEx = (HttpException) ex;
                    int responseCode = httpEx.getCode();
                    String responseMsg = httpEx.getMessage();
                    String errorResult = httpEx.getResult();
                    // ...
                } else { // 其他错误
                    // ...
                }
            }

            @Override
            public void onCancelled(CancelledException cex) {

            }

            @Override
            public void onFinished() {

            }
        });
    }

    private void init_Share(){
        FaceBookShare.callbackManager = CallbackManager.Factory.create();
        FaceBookShare.messageDialog = new MessageDialog(this);
        FaceBookShare.setInvite(true);
        FaceBookShare.messageDialog.registerCallback(FaceBookShare.callbackManager, FaceBookShare.facebookCallback_messenger);
    }

    private void inviteFriends(){
        String content = "";
        String appUrl = getString(R.string.app_url);
        //String pictureUrl = "http://img.taopic.com/uploads/allimg/140222/240404-14022210562883.jpg";
        String pictureUrl = Constant.getBaseUrl() + "common/image/10BBUY_logo.png";
        FaceBookShare.share_messenger(content,pictureUrl,appUrl);
    }

    private void bindFaceBook(String uidx,String facebookInfo){
        RequestParams params = new RequestParams(Constant.getBaseUrl() + "Page/Ucenter/MemberBindFB.ashx");
        TokenVerify.addToken(getActivity(),params);
        params.addBodyParameter("uidx", uidx);
        params.addBodyParameter("data",facebookInfo);
        Log.d(TAG, "bindFaceBook: " + uidx + "&" + facebookInfo);
        x.http().post(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                //Utility.toastShow(x.app(),"bindFaceBook: "  + result);
                Log.d(TAG, "bindFaceBook: "  + result);
                if(result.toUpperCase().equals("SUCCESS")){
                    Log.d(TAG, "bindFaceBook: " + "SUCCESS"  + result);
                    TokenVerify.saveCookie(getActivity());
                    init();
                }
            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
                //Utility.toastShow(x.app(),"bindFaceBook: onError");
                Log.d(TAG, "bindFaceBook: onError");
            }

            @Override
            public void onCancelled(CancelledException cex) {
                //Utility.toastShow(x.app(),"bindFaceBook: onCancelled");
                Log.d(TAG, "bindFaceBook: onCancelled");
            }

            @Override
            public void onFinished() {
                //Utility.toastShow(x.app(),"bindFaceBook: onFinished");
                Log.d(TAG, "bindFaceBook: onFinished");
            }
        });
    }
}

