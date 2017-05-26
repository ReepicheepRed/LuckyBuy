package com.luckybuy.presenter.Impl;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.luckybuy.BaseActivity;
import com.luckybuy.BaskMineActivity;
import com.luckybuy.ChargeActivity;
import com.luckybuy.ManagerAddressActivity;
import com.luckybuy.R;
import com.luckybuy.SNS_MineActivity;
import com.luckybuy.WebActivity;
import com.luckybuy.login.LoginUserUtils;
import com.luckybuy.model.DiamondMissionModel;
import com.luckybuy.model.UserModel;
import com.luckybuy.network.ParseData;
import com.luckybuy.network.TokenVerify;
import com.luckybuy.presenter.DiamondMissionPresenter;
import com.luckybuy.share.FaceBookShare;
import com.luckybuy.util.Constant;
import com.luckybuy.view.DiamondMissionView;

import org.xutils.common.Callback;
import org.xutils.ex.HttpException;
import org.xutils.http.RequestParams;
import org.xutils.x;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by zhiPeng.S on 2016/10/14.
 */

public class DiamondMissionPresenterImpl implements DiamondMissionPresenter {

    private DiamondMissionView diamondMissionView;
    private SharedPreferences preferences;
    private BaseActivity mContext;
    public DiamondMissionPresenterImpl(BaseActivity context, DiamondMissionView view) {
        diamondMissionView = view;
        preferences = LoginUserUtils.getUserSharedPreferences(x.app());
        mContext = context;
    }


    @Override
    public void getShowListInfo(){
        RequestParams params = new RequestParams(Constant.getBaseUrl() +"Page/Ucenter/MemberTask.ashx");
        long uidx = preferences.getLong(Constant.USER_ID,0);
        params.addQueryStringParameter("uidx", String.valueOf(uidx));
        TokenVerify.addToken(x.app(),params);
        x.http().get(params, new Callback.CommonCallback<String>() {

            @Override
            public void onSuccess(String result) {
                    if(result.length() < 3)
                        diamondMissionView.loginOut(true);
                    TokenVerify.saveCookie(x.app());
                    Map<String, Object> resultMap = ParseData.parseDiamondMissionInfo(result);
                    @SuppressWarnings("unchecked")
                    List<DiamondMissionModel> showlist =
                            (List<DiamondMissionModel>) resultMap.get(Constant.AWARD_LIST);
                    diamondMissionView.updateMission(showlist);
            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
                Log.e("error", ex.getMessage());
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

    @Override
    public void getDiamondNumber() {
        RequestParams params = new RequestParams(Constant.getBaseUrl() + "page/ucenter/MemberInfo.ashx");
        long uidx = preferences.getLong(Constant.USER_ID,0);
        params.addQueryStringParameter("uidx", String.valueOf(uidx));
        TokenVerify.addToken(x.app(),params);
        x.http().get(params, new Callback.CommonCallback<String>() {

            @Override
            public void onSuccess(String result) {
                    TokenVerify.saveCookie(x.app());
                    Gson gson = new GsonBuilder().serializeNulls().create();
                    List<UserModel> modelData = gson.fromJson(result, new TypeToken<List<UserModel>>(){}.getType());
                    UserModel model = modelData.get(0);
                    String diamond = String.valueOf(model.getLuckcoin());
                    diamondMissionView.updateDiamond(diamond);
            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
                Log.e("error", ex.getMessage());
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

    public static final int REQUESTCODE_MISSION = 0;
    public void missionGo(){
        Intent intent = new Intent();
        Bundle bundle = new Bundle();
        switch (position){
            case 0:
                intent.setClass(mContext,SNS_MineActivity.class);
                intent.putExtra("bindMission",true);
                mContext.startActivityForResult(intent,REQUESTCODE_MISSION);
                break;
            case 1:
//                intent.setClass(mContext,WebActivity.class);
//                bundle.putString("web_url",Constant.getBaseUrl() + "Html/Help/shareHtml.htm");
//                intent.putExtras(bundle);
//                intent.putExtra(Constant.WEB_H5,Constant.WEB_URL);
//                mContext.startActivityForResult(intent,REQUESTCODE_MISSION);
                shareFacebook();
                break;
            case 2:
                intent.setClass(mContext,ChargeActivity.class);
                mContext.startActivityForResult(intent,REQUESTCODE_MISSION);
                break;
            case 3:
                intent.setClass(mContext,BaskMineActivity.class);
                mContext.startActivityForResult(intent,REQUESTCODE_MISSION);
                break;
            case 4:
                intent.setClass(mContext,SNS_MineActivity.class);
                mContext.startActivityForResult(intent,REQUESTCODE_MISSION);
                break;
            case 5:
                intent.setClass(mContext,ManagerAddressActivity.class);
                mContext.startActivityForResult(intent,REQUESTCODE_MISSION);
                break;
        }
    }

    private int position = -1;
    public void missionWill(List<DiamondMissionModel> datas, int position){
        this.position = position;
        DiamondMissionModel model = datas.get(position);
        if(model.getIscomplete() == 1) return;
        String comment = model.getComment();
        diamondMissionView.showIntroduction(comment);
    }

    private void shareFacebook() {
        String appUrl = x.app().getString(R.string.app_url_share);
        String content = x.app().getString(R.string.facebook_share_web) + appUrl;
        String pictureUrl = Constant.getBaseUrl() + "common/image/10BBUY_logo.png";
        FaceBookShare.share_facebook(content,pictureUrl,appUrl);
    }

    public void commitShareRsult(){
        RequestParams params = new RequestParams(Constant.getBaseUrl() + "Page/Ucenter/MemberShare.ashx");
        SharedPreferences preferences = LoginUserUtils.getUserSharedPreferences(x.app());
        long uidx = preferences.getLong(Constant.USER_ID,0);
        params.addQueryStringParameter("uidx", String.valueOf(uidx));
        TokenVerify.addToken(x.app(),params);
        x.http().get(params, new Callback.CommonCallback<String>() {

            @Override
            public void onSuccess(String result) {
                TokenVerify.saveCookie(x.app());
                diamondMissionView.updateInfo();
            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
                Log.e("error", ex.getMessage());

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
