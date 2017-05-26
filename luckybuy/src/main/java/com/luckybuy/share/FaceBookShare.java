package com.luckybuy.share;

import android.content.SharedPreferences;
import android.net.Uri;
import android.util.Log;

import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.share.Sharer;
import com.facebook.share.model.ShareLinkContent;
import com.facebook.share.widget.MessageDialog;
import com.facebook.share.widget.ShareDialog;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.luckybuy.R;
import com.luckybuy.login.LoginUserUtils;
import com.luckybuy.model.UserModel;
import com.luckybuy.network.TokenVerify;
import com.luckybuy.presenter.DiamondMissionPresenter;
import com.luckybuy.util.Constant;
import com.luckybuy.util.Utility;

import org.xutils.common.Callback;
import org.xutils.ex.HttpException;
import org.xutils.http.RequestParams;
import org.xutils.x;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by zhiPeng.S on 2016/8/26.
 */
public class FaceBookShare {

    public static CallbackManager callbackManager;
    public static ShareDialog shareDialog;
    public static MessageDialog messageDialog;
    private static DiamondMissionPresenter diamondMissionPresenter;

    public static void share_facebook(String content,String pictureUrl,String appUrl){

        if (ShareDialog.canShow(ShareLinkContent.class)) {
            ShareLinkContent linkContent = new ShareLinkContent.Builder()
                    .setContentTitle(x.app().getString(R.string.app_name))
                    .setContentDescription(content)
                    .setImageUrl(Uri.parse(pictureUrl))
                    .setContentUrl(Uri.parse(appUrl))
                    .build();

            shareDialog.show(linkContent);
        }
    }

    public static FacebookCallback<Sharer.Result> facebookCallback_share = new FacebookCallback<Sharer.Result>() {

        @Override
        public void onSuccess(Sharer.Result result) {
            if(diamondMissionPresenter != null){
                diamondMissionPresenter.commitShareRsult();
            }
        }

        @Override
        public void onCancel() {

        }

        @Override
        public void onError(FacebookException error) {

        }
    };

    public static void share_messenger(String content,String pictureUrl,String appUrl){
        if(!Utility.isAvilible(x.app(),"com.facebook.orca")) {
            Utility.toastShow(x.app(), R.string.messenger_not_install);
            return;
        }
        if (ShareDialog.canShow(ShareLinkContent.class)) {
            ShareLinkContent linkContent = new ShareLinkContent.Builder()
                    .setContentTitle(x.app().getString(R.string.app_name))
                    .setContentDescription(content)
                    .setImageUrl(Uri.parse(pictureUrl))
                    .setContentUrl(Uri.parse(appUrl))
                    .build();

            messageDialog.show(linkContent);
        }
    }
    private static boolean isInvite;

    public static void setInvite(boolean invite) {
        isInvite = invite;
    }

    public static FacebookCallback<Sharer.Result> facebookCallback_messenger = new FacebookCallback<Sharer.Result>() {

        @Override
        public void onSuccess(Sharer.Result result) {
            if(!isInvite)
                Utility.toastShow(x.app(),R.string.share_success);
            isInvite = false;

        }

        @Override
        public void onCancel() {
            String str = "cancel";
        }

        @Override
        public void onError(FacebookException error) {
            String str = error.getMessage();
            Log.e("MessengerError", "onError: "+ str );
            //Utility.toastShow(x.app(),R.string.messenger_not_install);
        }
    };

    public static void setDiamondMissionPresenter(DiamondMissionPresenter mDiamondMissionPresenter) {
        diamondMissionPresenter = mDiamondMissionPresenter;
    }
}
