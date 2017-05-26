package com.assist.presenter;
import android.app.Activity;
import android.content.Intent;

import com.assist.R;
import com.assist.contract.SelectUserContract;
import com.assist.model.SelectUserModelImpl;
import com.assist.util.Constant;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.xutils.common.Callback;
import org.xutils.http.RequestParams;
import org.xutils.x;

import java.io.Serializable;
import java.util.List;

/**
* Created by zhiPeng.S on 2016/11/03
*/

public class SelectUserPresenterImpl implements SelectUserContract.Presenter{
    private SelectUserContract.View selectUserView;
    private Activity mContext;
    private List<SelectUserModelImpl.DetailInfo> datas;
    private long sUserCount;
    public SelectUserPresenterImpl(Activity context,SelectUserContract.View selectUserView) {
        mContext = context;
        this.selectUserView = selectUserView;
    }

    @Override
    public void getUserInfo() {
        RequestParams params = new RequestParams(Constant.getBaseUrl() + "Robot/member/list.ashx?usertype=0");
        params.addQueryStringParameter("operation","andoridshi");
        x.http().get(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                Gson gson = new GsonBuilder().serializeNulls().create();
                SelectUserModelImpl model = gson.fromJson(result,new TypeToken<SelectUserModelImpl>(){}.getType());
                String[] userStrA = formatUserInfo(model);
                selectUserView.showUserInfo(userStrA);
                String[] smaStrA = availableSmartUser(model.getList());
                selectUserView.showUserDetailInfo(smaStrA,model.getList());
                datas = model.getList();
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
    public void getAvailableUserInfo() {
        RequestParams params = new RequestParams(Constant.getBaseUrl() + "Robot/Member/MemberLock.ashx");
        JSONArray jsonArray = new JSONArray();
        try {
            for (SelectUserModelImpl.DetailInfo model: datas) {
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("uidx",model.getUidx());
                jsonArray.put(jsonObject);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        params.addQueryStringParameter("data",jsonArray.toString());
        params.addQueryStringParameter("operation","andoridshi");
        x.http().get(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                Gson gson = new GsonBuilder().serializeNulls().create();
                List<SelectUserModelImpl.DetailInfo> model = gson.fromJson(result,new TypeToken<List<SelectUserModelImpl.DetailInfo>>(){}.getType());
                datas = model;
                Intent intent = new Intent();
                intent.putExtra("userData", (Serializable) getDatas());
                intent.putExtra("userCount",getSelectedUserCount());
                mContext.setResult(0,intent);
                mContext.finish();
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
    public long getSelectedUserCount() {
        return sUserCount;
    }

    private String[] formatUserInfo(SelectUserModelImpl model){
        String[] userStrA = new String[5];
        userStrA[0] = mContext.getString(R.string.user_number_virtual,model.getUsercount());
        userStrA[1] = mContext.getString(R.string.user_unengaged,model.getFreeusercount());
        userStrA[2] = mContext.getString(R.string.big_buyer,model.getLeveL3USERCOUNT());
        userStrA[3] = mContext.getString(R.string.mediate_buyer,model.getLeveL2COUNTUSER());
        userStrA[4] = mContext.getString(R.string.small_buyer,model.getLeveL1USERCOUNT());
        return userStrA;
    }

    private String[] availableSmartUser(List<SelectUserModelImpl.DetailInfo> datas){
        String[] smaStrA = new String[3];
        long[] userCount = new long[3];
        for (SelectUserModelImpl.DetailInfo users :datas) {
            if (users.isIsused()) sUserCount++;
            int type = users.getUsetype();
            switch (type){
                case 1:
                    if (!users.isIsused()) userCount[0]++;
                    break;
                case 2:
                    if (!users.isIsused()) userCount[1]++;
                    break;
                case 3:
                    if (!users.isIsused()) userCount[2]++;
                    break;
            }
        }
        smaStrA[0] = mContext.getString(R.string.big_buyer_s,userCount[2]);
        smaStrA[1] = mContext.getString(R.string.mediate_buyer_s,userCount[1]);
        smaStrA[2] = mContext.getString(R.string.small_buyer_s,userCount[0]);
        return smaStrA;
    }

    public List<SelectUserModelImpl.DetailInfo> getDatas() {
        return datas;
    }
}