package com.assist.presenter;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.SpinnerAdapter;

import com.assist.MainActivity;
import com.assist.R;
import com.assist.contract.RobotSettingContract;
import com.assist.model.CommodityModelImpl;
import com.assist.model.SelectUserModelImpl;
import com.assist.util.Constant;
import org.xutils.common.Callback;
import org.xutils.http.RequestParams;
import org.xutils.x;

import java.util.List;

/**
* Created by zhiPeng.S on 2016/11/02
*/

public class RobotSettingPresenterImpl implements RobotSettingContract.Presenter{

    public static final int RS_RequestCode = 0;
    private RobotSettingContract.View robotSettingView;
    private Activity mContext;
    public RobotSettingPresenterImpl(Activity context,RobotSettingContract.View robotSettingView) {
        this.robotSettingView = robotSettingView;
        mContext = context;
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        Object object = parent.getAdapter().getItem(position);
        String timeStr = (String)object;
        int interval,span;
        if(timeStr.substring(timeStr.length()-3).equals("sec")){
            span = Integer.valueOf(timeStr.substring(0,timeStr.length()-3));
            interval = span;
            ((MainActivity)mContext).setInterval(interval);
            return;
        }
        span = Integer.valueOf(timeStr.substring(0,timeStr.length()-3));
        interval = span*60;
        ((MainActivity)mContext).setInterval(interval);
    }

    @SuppressWarnings("unchecked")
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode){
            case RS_RequestCode:
                if(data == null) return;

                if(data.hasExtra("userCount")){
                    robotSettingView.showUserSelectedCount(mContext.getString(R.string.user_number_selected,data.getLongExtra("userCount",0)));

                    ((MainActivity)mContext).setDatas_user((List<SelectUserModelImpl.DetailInfo>)data.getSerializableExtra("userData"));
                }
                if(data.hasExtra("goodsCount")){
                    robotSettingView.showGoodsSelectedCount(mContext.getString(R.string.commodity_number_selected,data.getLongExtra("goodsCount",0)));
                    ((MainActivity)mContext).setDatas((List<CommodityModelImpl>)data.getSerializableExtra("goodsData"));
                }
                break;
        }

    }
}