package com.luckybuy;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.luckybuy.layout.LoadingDialog;
import com.luckybuy.login.LoginUserUtils;
import com.luckybuy.model.UserModel;
import com.luckybuy.network.TokenVerify;
import com.luckybuy.util.Constant;
import com.luckybuy.util.FileUtil;
import com.luckybuy.util.Utility;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.xutils.common.Callback;
import org.xutils.common.util.DensityUtil;
import org.xutils.http.RequestParams;
import org.xutils.image.ImageOptions;
import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.Event;
import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

import java.io.File;
import java.util.Arrays;
import java.util.List;

/**
 * Created by zhiPeng.S on 2016/6/14.
 */
@ContentView(R.layout.activity_sns)
public class SNS_MineActivity extends BaseActivity {


    private SharedPreferences preferences;
    private SharedPreferences.Editor editor;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        title_tv.setText(R.string.title_sns);
        preferences = LoginUserUtils.getUserSharedPreferences(this);
        editor = preferences.edit();
        imageOptions = new ImageOptions.Builder()
                .setSize(DensityUtil.dip2px(45),DensityUtil.dip2px(45))
                .setRadius(DensityUtil.dip2px(30))
                .setLoadingDrawableId(R.mipmap.gerenxinxi_morentouxiang_2x)
                .setFailureDrawableId(R.mipmap.gerenxinxi_morentouxiang_2x)
                .build();

        init_facebook();
        long user_id_fb = preferences.getLong(Constant.USER_ID_FB, 0);
        bindFacebookState(user_id_fb != 0);

        long user_id = preferences.getLong(Constant.USER_ID,0);
        if (user_id != 0)
            obtain_user_information(user_id+"");
        bindMission();
    }

    private void bindMission(){
        Intent intent = getIntent();
        boolean isBindMission = intent.getBooleanExtra("bindMission",false);
        if(isBindMission){
            AccessToken accessToken = AccessToken.getCurrentAccessToken();
            if (accessToken == null || accessToken.isExpired()) {
                LoginManager.getInstance().logInWithReadPermissions(this, Arrays.asList("public_profile", "user_friends"));
            }
        }

    }

    @ViewInject(R.id.title_activity)
    private TextView title_tv;

    @Event({R.id.back_iv,R.id.sns_address_rl,R.id.sns_nickname_rl,R.id.sns_phone_rl,R.id.sns_avatar_rl,R.id.sns_facebook_rl})
    private void viewClick(View view){
        AccessToken accessToken = AccessToken.getCurrentAccessToken();
        Intent intent = new Intent();
        int updateInfo = -1;
        switch(view.getId()){
            case R.id.back_iv:
                setResult(Constant.RESULT_CODE_MINE);
                this.finish();
                break;
            case R.id.sns_address_rl:
                intent.setClass(this,ManagerAddressActivity.class);
                startActivity(intent);
                break;
            case R.id.sns_nickname_rl:
                updateInfo = 0;
                intent.putExtra("updateInfo",updateInfo);
            case R.id.sns_phone_rl:
                if(updateInfo != 0) updateInfo = 1;
                intent.putExtra("updateInfo",updateInfo);
                intent.setClass(this,SNS_UpdateInfoActivity.class);
                startActivityForResult(intent,Constant.REQUEST_CODE);
                break;
            case R.id.sns_avatar_rl:
                menuWindow = new PopupWindow_Bask(this, itemsOnClick);
                menuWindow.showAtLocation(findViewById(R.id.activity_sns_rl),
                    Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, 0);
                break;
            case R.id.sns_facebook_rl:
                if(isBind) return;
                if (accessToken == null || accessToken.isExpired()) {
                    LoginManager.getInstance().logInWithReadPermissions(this, Arrays.asList("public_profile", "user_friends"));
                }
                break;
        }
    }

    @ViewInject(R.id.sns_avatar_iv)
    private ImageView avatar_iv;

    @ViewInject(R.id.sns_id_tv)
    private TextView id_tv;

    @ViewInject(R.id.sns_account_tv)
    private TextView account_tv;

    @ViewInject(R.id.sns_nickame_tv)
    private TextView nickname_tv;

    @ViewInject(R.id.sns_phone_tv)
    private TextView sns_phone_tv;

    private ImageOptions imageOptions;


    private void obtain_user_information(String uidx){
        RequestParams params = new RequestParams(Constant.getBaseUrl() + "page/ucenter/MemberInfo.ashx");
        TokenVerify.addToken(this,params);
        params.addQueryStringParameter("uidx", uidx);
        x.http().get(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                try {
                    TokenVerify.saveCookie(SNS_MineActivity.this);
                    Gson gson = new GsonBuilder().serializeNulls().create();
                    List<UserModel> modelData = gson.fromJson(result, new TypeToken<List<UserModel>>(){}.getType());
                    UserModel model = modelData.get(0);

                    x.image().bind(avatar_iv,model.getHeadpic(),imageOptions);
                    nickname_tv.setText(model.getNickname());
                    String idStr = model.getUidx()+"";
                    id_tv.setText(idStr);
                    sns_phone_tv.setText(model.getMobile());

                    editor.putString(Constant.USER_NAME,model.getNickname());
                    editor.putString(Constant.USER_PHONE,model.getMobile());
                    editor.commit();
                } catch (Exception e) {
                    e.printStackTrace();
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


    private static final String IMAGE_FILE_NAME = "avatarImage.jpg";// 头像文件名称
    private String urlpath;			// 图片本地路径
    private String resultStr = "";	// 服务端返回结果集
    private static ProgressDialog pd;// 等待进度圈
    private static final int REQUESTCODE_PICK = 0;		// 相册选图标记
    private static final int REQUESTCODE_TAKE = 1;		// 相机拍照标记
    private static final int REQUESTCODE_CUTTING = 2;	// 图片裁切标记

    private PopupWindow_Bask menuWindow;


    //为弹出窗口实现监听类
    private View.OnClickListener itemsOnClick = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            menuWindow.dismiss();
            switch (v.getId()) {
                // 拍照
                case R.id.takePhotoBtn:
                    Intent takeIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    //下面这句指定调用相机拍照后的照片存储的路径
                    takeIntent.putExtra(MediaStore.EXTRA_OUTPUT,
                            Uri.fromFile(new File(Environment.getExternalStorageDirectory() , IMAGE_FILE_NAME)));
                    startActivityForResult(takeIntent, REQUESTCODE_TAKE);
                    break;
                // 相册选择图片
                case R.id.pickPhotoBtn:
                    Intent pickIntent = new Intent(Intent.ACTION_PICK, null);
                    // 如果朋友们要限制上传到服务器的图片类型时可以直接写如："image/jpeg 、 image/png等的类型"
                    pickIntent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");
                    startActivityForResult(pickIntent, REQUESTCODE_PICK);
                    break;
                default:
                    break;
            }
        }
    };

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case REQUESTCODE_PICK:// 直接从相册获取
                try {
                    startPhotoZoom(data.getData());
                } catch (NullPointerException e) {
                    e.printStackTrace();// 用户点击取消操作
                }
                break;
            case REQUESTCODE_TAKE:// 调用相机拍照
                File temp = new File(Environment.getExternalStorageDirectory()  + "/" + IMAGE_FILE_NAME);
                startPhotoZoom(Uri.fromFile(temp));
                break;
            case REQUESTCODE_CUTTING:// 取得裁剪后的图片
                if (data != null) {
                    setPicToView(data);
                }
                break;

        }
        switch (resultCode){
            case Constant.RESULT_CODE_UPDATE:
                long user_id = preferences.getLong(Constant.USER_ID,0);
                if (user_id != 0)
                    obtain_user_information(user_id+"");
                break;
        }
        super.onActivityResult(requestCode, resultCode, data);
        if(callbackManager != null)
            callbackManager.onActivityResult(requestCode, resultCode, data);
    }

    private Uri uritempFile;
    /**
     * 裁剪图片方法实现
     * @param uri
     */
    public void startPhotoZoom(Uri uri) {
        Intent intent = new Intent("com.android.camera.action.CROP");
        intent.setDataAndType(uri, "image/*");
        // crop=true是设置在开启的Intent中设置显示的VIEW可裁剪
        intent.putExtra("crop", "true");
        // aspectX aspectY 是宽高的比例
        intent.putExtra("aspectX", 1);
        intent.putExtra("aspectY", 1);
        // outputX outputY 是裁剪图片宽高
        intent.putExtra("outputX", 300);
        intent.putExtra("outputY", 300);
        intent.putExtra("return-data", true);

        startActivityForResult(intent, REQUESTCODE_CUTTING);
    }

    /**
     * 保存裁剪之后的图片数据
     * @param picdata
     */
    private void setPicToView(Intent picdata) {
        Bundle extras = picdata.getExtras();
        if (extras != null) {
            // 取得SDCard图片路径做显示
            Bitmap photo = extras.getParcelable("data");
            Drawable drawable = new BitmapDrawable(null, photo);
            String dateStr = Utility.FORMAT_NUM.format(System.currentTimeMillis());
            int random =(int)(Math.random()*900)+100;
            urlpath = FileUtil.saveFile(SNS_MineActivity.this, "avatar" + dateStr + random + ".jpg", photo);
            //avatar_iv.setImageDrawable(drawable);
            x.image().bind(avatar_iv,urlpath,imageOptions);
            long user_id = preferences.getLong(Constant.USER_ID,0);
            if(user_id != 0)
                updateSNSInfo(user_id+"",urlpath);
            // 新线程后台上传服务端
            //pd = ProgressDialog.show(mContext, null, "正在上传图片，请稍候...");
            //new Thread(uploadImageRunnable).start();
        }
    }

    LoadingDialog loadingDialog;
    private void updateSNSInfo(String uidx,String headpicUrl){
        loadingDialog = new LoadingDialog(this);
        loadingDialog.showDialog();
        RequestParams params = new RequestParams(Constant.getBaseUrl() + "Page/Ucenter/membermodify.ashx");
        TokenVerify.addToken(this,params);
        params.addBodyParameter("uidx", uidx);
        params.addBodyParameter("modify", "headpic");
        params.setMultipart(true);
        params.addBodyParameter("file",new File(headpicUrl),null);

        x.http().post(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                loadingDialog.dismiss();
                Log.e(TAG, "onSuccess: " + result);
                if(result.toUpperCase().equals("SUCCESS")){
                    TokenVerify.saveCookie(SNS_MineActivity.this);
                    Utility.toastShow(x.app(),R.string.updateInfo_success);
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

    @ViewInject(R.id.bind_state_tv)
    private TextView bind_state_tv;
    private boolean isBind;

    private void bindFacebookState(boolean isBind){
        this.isBind = isBind;
        if(isBind)
            bind_state_tv.setText(R.string.facebook_bind_state);
        else
            bind_state_tv.setText(R.string.facebook_no_bind_state);
    }

    private void bindFaceBook(String uidx,String facebookInfo){
        RequestParams params = new RequestParams(Constant.getBaseUrl() + "Page/Ucenter/MemberBindFB.ashx");
        TokenVerify.addToken(this,params);
        params.addBodyParameter("uidx", uidx);
        params.addBodyParameter("data",facebookInfo);
        x.http().post(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                if(result.toUpperCase().equals("SUCCESS")){
                    TokenVerify.saveCookie(SNS_MineActivity.this);
                    bindFacebookState(true);
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

    private CallbackManager callbackManager;
    private ProgressDialog progressDialog;
    private void init_facebook(){
        callbackManager = CallbackManager.Factory.create();
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage(getString(R.string.facebook_bind));
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

                                        JSONObject jsonObject = new JSONObject();
                                        jsonObject.put("name", object.getString("name"));
                                        jsonObject.put("id", facebook_id);
                                        jsonObject.put("picture", pictureUrl);

                                        bindFaceBook(user_id+"",jsonObject.toString());
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

    private void commit_fb_friends(JSONArray jsonArray){
        RequestParams params = new RequestParams(Constant.getBaseUrl() + "page/ucenter/FB_Firend_Add.ashx");
        long user_id_fb = preferences.getLong(Constant.USER_ID_FB,0);
        if(user_id_fb == 0) return;
        params.addQueryStringParameter("id", user_id_fb+"");
        params.addQueryStringParameter("data", jsonArray.toString());

        x.http().get(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                Utility.toastShow(x.app(),"onSuccess: " + result );
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
