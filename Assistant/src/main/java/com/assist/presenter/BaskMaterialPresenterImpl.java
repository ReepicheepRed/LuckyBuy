package com.assist.presenter;
import android.app.Activity;
import android.content.Intent;
import android.view.View;
import android.widget.AdapterView;

import com.assist.contract.BaskMaterialContract;
import com.assist.model.BaskMaterialModelImpl;
import com.assist.util.Constant;
import com.assist.util.Utility;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.annotations.Until;
import com.google.gson.reflect.TypeToken;

import org.json.JSONException;
import org.json.JSONObject;
import org.xutils.common.Callback;
import org.xutils.http.RequestParams;
import org.xutils.x;

import java.util.ArrayList;
import java.util.List;

/**
* Created by zhiPeng.S on 2016/11/08
*/

public class BaskMaterialPresenterImpl implements BaskMaterialContract.Presenter{

    private Activity context;
    private BaskMaterialContract.View baskMaterialView;
    private long goodId = 1021;
    private long uidx = 0;
    private long timesid = 0;
    private List<BaskMaterialModelImpl> data;
    public BaskMaterialPresenterImpl(Activity context, BaskMaterialContract.View baskMaterialView) {
        this.context = context;
        this.baskMaterialView = baskMaterialView;
        Intent intent = context.getIntent();
        uidx = intent.getLongExtra("uidx",0);
        timesid = intent.getLongExtra("timesid",0);
        goodId = intent.getLongExtra("goodid",0);
    }

    public void getBaskMaterialInfo(){
        RequestParams params = new RequestParams(Constant.getBaseUrl() + "Robot/Show/Library.ashx");
        params.addQueryStringParameter("goodid", String.valueOf(goodId));
        x.http().get(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                Gson gson = new GsonBuilder().serializeNulls().create();
                data = gson.fromJson(result,new TypeToken<List<BaskMaterialModelImpl>>(){}.getType());
                List<Long> data_l = new ArrayList<>();
                for (BaskMaterialModelImpl model: data) {
                    data_l.add(model.getLbidx());
                }
                baskMaterialView.showBaskMaterialList(data_l);
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

    private void commitBaskMaterialInfo(long timsid, long uidx, long lbidx){
        RequestParams params = new RequestParams(Constant.getBaseUrl() + "Robot/Show/submit.ashx");
        params.addQueryStringParameter("timesid", String.valueOf(timsid));
        params.addQueryStringParameter("uidx", String.valueOf(uidx));
        params.addQueryStringParameter("lbidx", String.valueOf(lbidx));
        params.addQueryStringParameter("operation", "andoridshi");
        x.http().get(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                try {
                    JSONObject jsonObject = new JSONObject(result);
                    String resultStr = jsonObject.getString("resultcode");
                    Utility.toastShow(context,resultStr);
                    if(resultStr.toLowerCase().equals("success")){
                        context.setResult(0);
                        context.finish();
                    }
                } catch (JSONException e) {
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

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        BaskMaterialModelImpl model = data.get(position);
        commitBaskMaterialInfo(timesid,uidx,model.getLbidx());
    }
}