package com.luckybuy;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import com.luckybuy.login.LoginUserUtils;
import com.luckybuy.network.TokenVerify;
import com.luckybuy.util.Constant;
import com.luckybuy.util.Utility;

import org.json.JSONArray;
import org.json.JSONObject;
import org.xutils.common.Callback;
import org.xutils.ex.HttpException;
import org.xutils.http.RequestParams;
import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.Event;
import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by zhiPeng.S on 2016/6/14.
 */
@ContentView(R.layout.activity_charge_record)
public class ChargeRecordActivity extends BaseActivity{
    private SharedPreferences preferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        title_tv.setText(R.string.recharge_history);
        charge_tv.setText(R.string.recharge);
        charge_tv.setVisibility(View.VISIBLE);

        preferences = LoginUserUtils.getUserSharedPreferences(this);
        long user_id = preferences.getLong(Constant.USER_ID,0);
        if(user_id != 0)
            getListInfo(user_id + "","0","10");
    }

    private void mock(){
        for(int i =0; i < 10; i++) {
            //JSONObject jsonObject = jsonArray.getJSONObject(i);
            Map<String,Object> item = new HashMap<String,Object>();
                        /*item.put("time", jsonObject.getString(""));
                        item.put("money", jsonObject.getString(""));
                        item.put("state", jsonObject.getString(""));*/
            item.put("time", getResources().getString(R.string.history_time));
            item.put("money", "100元");
            item.put("state", "成功");
            mData.add(item);
        }

        SimpleAdapter adapter = new SimpleAdapter(
                ChargeRecordActivity.this,
                mData,
                R.layout.item_charge_record,
                new String[]{"time","money","state"},
                new int[]{R.id.charge_time_tv,R.id.charge_money_tv,R.id.charge_state_tv});
        listView.setAdapter(adapter);
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        charge_tv.setVisibility(View.INVISIBLE);
    }

    @ViewInject(R.id.title_activity)
    private TextView title_tv;

    @ViewInject(R.id.right_view)
    private TextView charge_tv;

    @Event({R.id.back_iv,R.id.right_view})
    private void viewClick(View view){
        Intent intent = new Intent();
        switch(view.getId()){
            case R.id.back_iv:
                this.finish();
                break;
            case R.id.right_view:
                intent.setClass(this,ChargeActivity.class);
                startActivity(intent);
                break;
        }
    }

    @ViewInject(R.id.charge_record_ll)
    private LinearLayout charge_record_ll;

    @ViewInject(R.id.blank_charge_rl)
    private RelativeLayout blank_charge_rl;

    private void updateView(boolean hasData){
        if(!hasData){
            blank_charge_rl.setVisibility(View.VISIBLE);
            charge_record_ll.setVisibility(View.GONE);
            return;
        }

        blank_charge_rl.setVisibility(View.GONE);
        charge_record_ll.setVisibility(View.VISIBLE);
    }

    @ViewInject(R.id.charge_record_lv)
    private ListView listView;
    List<Map<String,Object>> mData= new ArrayList<Map<String,Object>>();

    private void getListInfo(String uidx, String pageindex, String pagesize){
        RequestParams params = new RequestParams(Constant.getBaseUrl() + "page/ucenter/credit.ashx");
        TokenVerify.addToken(this,params);
        params.addQueryStringParameter("uidx", uidx);
        params.addQueryStringParameter("pageindex", pageindex);
        params.addQueryStringParameter("pagesize", pagesize);
        x.http().get(params, new Callback.CommonCallback<String>() {

            @Override
            public void onSuccess(String result) {
                try {
                    TokenVerify.saveCookie(ChargeRecordActivity.this);
                    mData.clear();
                    JSONArray jsonArray = new JSONArray(result);
                    for(int i =0; i < jsonArray.length(); i++) {
                        JSONObject jsonObject = jsonArray.getJSONObject(i);
                        Map<String,Object> item = new HashMap<>();
                        item.put("money", jsonObject.getString("money"));
                        item.put("time", Utility.trimDate(jsonObject.getString("udate")));
                        item.put("state", jsonObject.getBoolean("issuccess"));
                        mData.add(item);
                    }

                    if(mData.size() > 0)
                        updateView(true);
                    else
                        updateView(false);

                    SimpleAdapter adapter = new SimpleAdapter(
                            ChargeRecordActivity.this,
                            mData,
                            R.layout.item_charge_record,
                            new String[]{"time","money","state"},
                            new int[]{R.id.charge_time_tv,R.id.charge_money_tv,R.id.charge_state_tv});
                    listView.setAdapter(adapter);
                }catch (Exception e){

                }
            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
                //Toast.makeText(x.app(), ex.getMessage(), Toast.LENGTH_LONG).show();
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
