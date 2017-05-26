package com.assist.presenter;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.design.widget.Snackbar;

import com.assist.MainActivity;
import com.assist.R;
import com.assist.contract.RobotContract;
import com.assist.contract.RobotContract;
import com.assist.model.CommodityModelImpl;
import com.assist.model.RobotModelImpl;
import com.assist.model.SelectUserModelImpl;
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

public class RobotPresenterImpl implements RobotContract.Presenter{

    private RobotContract.View robotView;
    private Activity mContext;
    public RobotPresenterImpl(Activity context,RobotContract.View robotView) {
        this.robotView = robotView;
        mContext = context;
    }


    @Override
    public void launchRobot(Bundle bundle) {
        RequestParams params = new RequestParams(Constant.getBaseUrl() + "Robot/Good/submit.ashx");
        params.addQueryStringParameter("goodid",bundle.getString("goodid"));
        params.addQueryStringParameter("timesid",bundle.getString("timesid"));
        params.addQueryStringParameter("copies",bundle.getString("copies"));
        params.addQueryStringParameter("uidx",bundle.getString("uidx"));
        params.addQueryStringParameter("operation","andoridshi");

        x.http().get(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                robotView.showLog(result);
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
    public void queryState(String goodId, String uidx) {
        RequestParams params = new RequestParams(Constant.getBaseUrl() + "Robot/Good/status.ashx");
        params.addQueryStringParameter("goodid",goodId);
        params.addQueryStringParameter("uidx",uidx);

        x.http().get(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                Gson gson = new GsonBuilder().serializeNulls().create();
                RobotModelImpl model = gson.fromJson(result,new TypeToken<RobotModelImpl>(){}.getType());
                String goodId = String.valueOf(model.getGoodinfo().getGoodid());
                String timesId = String.valueOf(model.getGoodinfo().getTimesid());
                String uidx = String.valueOf(model.getMemberinfo().getUidx());
                Bundle bundle = new Bundle();
                bundle.putString("goodid",goodId);
                bundle.putString("timesid",timesId);
                bundle.putString("copies","200");
                bundle.putString("uidx",uidx);
                launchRobot(bundle);
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


    public void randomBuy(){
        List<CommodityModelImpl> datas = ((MainActivity)mContext).getDatas();
        List<SelectUserModelImpl.DetailInfo> datas_user = ((MainActivity)mContext).getDatas_user();
        int randomGoods =(int)(Math.random()*datas.size());
        int randomUsers =(int)(Math.random()*datas_user.size());
        CommodityModelImpl model_goods = datas.get(randomGoods);
        SelectUserModelImpl.DetailInfo model_user = datas_user.get(randomUsers);
        String goodId = String.valueOf(model_goods.getGoodid());
        String uidx = String.valueOf(model_user.getUidx());
        queryState(goodId,uidx);

    }

    public void sequenceBuy(int position){
        try{
            List<CommodityModelImpl> datas = ((MainActivity)mContext).getDatas();
            List<SelectUserModelImpl.DetailInfo> datas_user = ((MainActivity)mContext).getDatas_user();

            CommodityModelImpl model_goods = datas.get(position);
            SelectUserModelImpl.DetailInfo model_user = datas_user.get(position);
            String goodId = String.valueOf(model_goods.getGoodid());
            String uidx = String.valueOf(model_user.getUidx());
            queryState(goodId,uidx);
        }catch (Exception e){
            e.printStackTrace();
        }

    }

    public void getCount(){
        try{
            List<CommodityModelImpl> datas = ((MainActivity)mContext).getDatas();
            List<SelectUserModelImpl.DetailInfo> datas_user = ((MainActivity)mContext).getDatas_user();
            long count_goods = datas.size();
            long count_user = 0;
            for (SelectUserModelImpl.DetailInfo data:datas_user) {
                if(data.isIsused()){
                    count_user ++;
                }
            }

            String gStr = mContext.getString(R.string.commodity_number,count_goods);
            String uStr = mContext.getString(R.string.user_number,count_user);
            robotView.showCount(uStr,gStr);
        }catch (Exception e){
            e.printStackTrace();
        }
    }
    BuyCountDownTimer timer;
    public void launchRobot_interval(int time){
        List<CommodityModelImpl> datas = ((MainActivity)mContext).getDatas();
        List<SelectUserModelImpl.DetailInfo> datas_user = ((MainActivity)mContext).getDatas_user();
        timer = new BuyCountDownTimer(24*60*60*1000,time*1000);
        if(datas != null && !datas.isEmpty() && datas_user != null && !datas_user.isEmpty()) {
            timer.setDatas(datas);
            timer.setDatas_user(datas_user);
            timer.start();
            robotView.setLaunchState(mContext.getString(R.string.pause));
        }else {
            Snackbar.make(mContext.getCurrentFocus(),"There are no user and goods",Snackbar.LENGTH_SHORT).show();
        }
    }

    public void cancelRobot(){
        if(timer != null)
            timer.cancel();
        robotView.setLaunchState(mContext.getString(R.string.initiate));
    }

    @Override
    public void sendLog() {
        Intent i = new Intent(Intent.ACTION_SEND);
        // i.setType("text/plain"); //模拟器请使用这行
        i.setType("message/rfc822"); // 真机上使用这行
        i.putExtra(Intent.EXTRA_EMAIL,new String[] { "FxMarginTrading@feib.com.tw" });
        i.putExtra(Intent.EXTRA_SUBJECT, "您的建议");
        i.putExtra(Intent.EXTRA_TEXT, robotView.getLog());
        mContext.startActivity(Intent.createChooser(i,"Select email application."));
    }

    class  BuyCountDownTimer extends CountDownTimer{
        List<CommodityModelImpl> datas;
        List<SelectUserModelImpl.DetailInfo> datas_user;
        int count = 0;
        int count_goods = 0;
        int count_user = 0;
        public BuyCountDownTimer(long millisInFuture, long countDownInterval) {
            super(millisInFuture, countDownInterval);

        }

        public void setDatas(List<CommodityModelImpl> datas) {
            this.datas = datas;
            count_goods = datas.size();
        }

        public void setDatas_user(List<SelectUserModelImpl.DetailInfo> datas_user) {
            this.datas_user = datas_user;
            for (SelectUserModelImpl.DetailInfo data:datas_user) {
                if(data.isIsused()){
                    count_user ++;
                }
            }
        }

        @Override
        public void onTick(long millisUntilFinished) {
            if(count < count_user && count< count_goods ){
                if(((MainActivity)mContext).isRandom())
                    randomBuy();
                else
                    sequenceBuy(count);
                count++;
            }else{
                this.cancel();
                robotView.setLaunchState(mContext.getString(R.string.initiate));
            }
        }

        @Override
        public void onFinish() {
            this.cancel();
            robotView.setLaunchState(mContext.getString(R.string.initiate));
        }
    }

}