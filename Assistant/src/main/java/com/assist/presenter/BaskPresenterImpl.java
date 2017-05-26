package com.assist.presenter;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.widget.AdapterView;

import com.assist.BaskFrgment;
import com.assist.BaskMaterialActivity;
import com.assist.adapter.BaskAdapter;
import com.assist.contract.BaskContract;
import com.assist.model.BaskModelImpl;
import com.assist.util.Constant;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import org.xutils.common.Callback;
import org.xutils.http.RequestParams;
import org.xutils.x;

import java.util.List;

/**
* Created by zhiPeng.S on 2016/11/07
*/

public class BaskPresenterImpl implements BaskContract.Presenter{

    private BaskContract.View baskContractView;
    private BaskFrgment fragment;
    private Activity context;
    private List<BaskModelImpl> data;

    public BaskPresenterImpl(Activity context, BaskContract.View baskContractView) {
        this.baskContractView = baskContractView;
        this.context = context;
    }

    @Override
    public void setFragment(BaskFrgment fragment) {
        this.fragment = fragment;
    }

    public void getWillBaskListInfo(final int optype){
        RequestParams params = new RequestParams(Constant.getBaseUrl() + "Robot/Show/List.ashx");
        params.addQueryStringParameter("optype", String.valueOf(optype));
        x.http().get(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                Gson gson = new GsonBuilder().serializeNulls().create();
                data = gson.fromJson(result,new TypeToken<List<BaskModelImpl>>(){}.getType());
                baskContractView.showBaskList(data,optype);
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
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        int state = ((BaskAdapter)baskContractView.getAdapter()).getOptype();
        if(state == 0){
            Intent intent = new Intent(context, BaskMaterialActivity.class);
            BaskModelImpl model = data.get(position);
            intent.putExtra("goodid",model.getGoodid());
            intent.putExtra("uidx",model.getUidx());
            intent.putExtra("timesid",model.getTimeid());
            fragment.startActivityForResult(intent,0);
        }
    }
}