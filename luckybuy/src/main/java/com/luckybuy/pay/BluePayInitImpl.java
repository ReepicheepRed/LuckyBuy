package com.luckybuy.pay;

import android.app.Activity;
import android.content.Context;

import com.bluepay.interfaceClass.BlueInitCallback;
import com.bluepay.pay.Client;
import com.bluepay.pay.LoginResult;
import com.luckybuy.layout.LoadingDialog;
import com.luckybuy.util.Utility;

import org.xutils.x;

/**
 * Created by zhiPeng.S on 2016/10/27.
 */


public class BluePayInitImpl implements BluePayInit {
    static StringBuilder stateString = null;
    private Activity mContext;
    private boolean initComplete = false;
    private LoadingDialog loadingDialog;

    public BluePayInitImpl(Activity context) {
        mContext = context;
    }

    @Override
    public void initBluePaySdk(){
        /**
         * 初始化SDK，在使用接口前，你需要调用该方法，并且初始化成功。
         * 在UI线程下使用
         * 如果初始化失败，请检查以下几样配置：
         * 		权限
         * 		ref文件与best平台配置是否一致
         * 		网络
         * */
        loadingDialog = new LoadingDialog(mContext);
        loadingDialog.setDialogCancelable(false);
        loadingDialog.showDialog();
        Client.init(mContext, new BlueInitCallback() {

            @Override
            public void initComplete(String loginResult, String resultDesc) {
                String result = null;
                try{

                    if (loginResult.equals(LoginResult.LOGIN_SUCCESS)) {

                        com.bluepay.pay.BluePay.setLandscape(false);
                        com.bluepay.pay.BluePay.setShowCardLoading(true);//该方法设置使用cashcard时是否使用sdk的loading框
                        result = "User Login Success!";

                        initComplete = true;
                        loadingDialog.dismiss();
                        Utility.toastShow(x.app(), " Init has completed");
                    } else if (loginResult.equals(LoginResult.LOGIN_FAIL)) {

                        result = "User Login Failed!";
                    } else {
                        StringBuilder sbStr = new StringBuilder(
                                "Fail! The code is:").append(loginResult)
                                .append(" desc is:").append(resultDesc);
                        stateString.append(sbStr.toString());
                        result = sbStr.toString();
                    }
                }catch(Exception e){
                    result = e.getMessage();
                }

            }
        });
    }

    public boolean isInitComplete() {
        return initComplete;
    }
}
