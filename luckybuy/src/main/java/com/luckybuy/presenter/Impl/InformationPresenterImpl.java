package com.luckybuy.presenter.Impl;

import android.app.Activity;
import android.util.Log;

import com.luckybuy.R;
import com.luckybuy.network.ParseData;
import com.luckybuy.presenter.InformationPresenter;
import com.luckybuy.util.Constant;
import com.luckybuy.view.InformationView;

import org.xutils.common.Callback;
import org.xutils.ex.HttpException;
import org.xutils.http.RequestParams;
import org.xutils.x;

import java.util.Map;

/**
 * Created by zhiPeng.S on 2016/10/14.
 */

public class InformationPresenterImpl implements InformationPresenter {

    private InformationView informationView;

    public InformationPresenterImpl(InformationView view) {
        informationView = view;
    }


    @Override
    public void getShowListInfo(long idx){
        RequestParams params = new RequestParams(Constant.getBaseUrl() +"Handle/LBList/Notice.ashx");
        if(idx != 0)
            params.addQueryStringParameter("idx", String.valueOf(idx));
        x.http().get(params, new Callback.CommonCallback<String>() {

            @Override
            public void onSuccess(String result) {
                Map<String, Object> resultMap = ParseData.parseNotificationInfo(result);
                informationView.updateData(resultMap);
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
}
