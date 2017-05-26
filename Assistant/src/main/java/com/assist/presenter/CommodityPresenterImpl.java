package com.assist.presenter;
import com.assist.adapter.ListBaseAdapter;
import com.assist.contract.CommodityContract;
import com.assist.model.CommodityModelImpl;
import com.assist.util.Constant;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import org.xutils.common.Callback;
import org.xutils.http.RequestParams;
import org.xutils.x;

import java.util.List;

/**
* Created by zhiPeng.S on 2016/11/03
*/

public class CommodityPresenterImpl implements CommodityContract.Presenter{
    private CommodityContract.View commodityView;
    private List<CommodityModelImpl> datas;
    private long sGoodsCount;
    public CommodityPresenterImpl(CommodityContract.View commodityView) {
        this.commodityView = commodityView;
    }

    @Override
    public void getShowList() {
        RequestParams params = new RequestParams(Constant.getBaseUrl() + "Robot/Good/list.ashx");
        x.http().get(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                Gson gson = new GsonBuilder().serializeNulls().create();
                datas = gson.fromJson(result, new TypeToken<List<CommodityModelImpl>>(){}.getType());
                commodityView.showCommodityList(datas);
                sGoodsCount = datas.size();
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
    public long getSelectedGoodsCount() {
        return sGoodsCount;
    }

    public List<CommodityModelImpl> getDatas() {
        return datas;
    }
}