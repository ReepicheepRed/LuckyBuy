package com.luckybuy;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.luckybuy.adapter.AddressAdapter;
import com.luckybuy.adapter.F_SNS_SnatchAdapter;
import com.luckybuy.adapter.ListBaseAdapter;
import com.luckybuy.login.LoginUserUtils;
import com.luckybuy.model.AddressModel;
import com.luckybuy.model.UnveilAwardModel;
import com.luckybuy.network.ParseData;
import com.luckybuy.network.TokenVerify;
import com.luckybuy.util.Constant;
import com.luckybuy.util.Utility;

import org.xutils.common.Callback;
import org.xutils.ex.HttpException;
import org.xutils.http.RequestParams;
import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.Event;
import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by zhiPeng.S on 2016/6/14.
 */
@ContentView(R.layout.activity_address_manager)
public class ManagerAddressActivity extends BaseActivity implements AdapterView.OnItemClickListener{

    private SharedPreferences preferences;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        title_tv.setText(R.string.title_address_manager);
        preferences = LoginUserUtils.getUserSharedPreferences(this);
        setShowList();

        long user_id = preferences.getLong(Constant.USER_ID,0);
        if(user_id != 0)
            getShowListInfo(user_id + "");
    }

    @ViewInject(R.id.title_activity)
    private TextView title_tv;

    @Event({R.id.back_iv,R.id.add_new_address_btn,R.id.add_address_btn})
    private void viewClick(View view){
        switch(view.getId()){
            case R.id.back_iv:
                this.finish();
                break;
            case R.id.add_new_address_btn:
            case R.id.add_address_btn:
                Intent intent = new Intent(this,ManagerAddressEditActivity.class);
                startActivityForResult(intent,Constant.REQUEST_CODE);
                break;
        }
    }

    @ViewInject(R.id.address_list_rl)
    private RelativeLayout address_list_rl;

    @ViewInject(R.id.blank_address_rl)
    private RelativeLayout blank_address_rl;

    private void updateView(boolean hasData){
        if(!hasData){
            blank_address_rl.setVisibility(View.VISIBLE);
            address_list_rl.setVisibility(View.GONE);
            return;
        }

        blank_address_rl.setVisibility(View.GONE);
        address_list_rl.setVisibility(View.VISIBLE);
    }

    Handler mHandler = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            @SuppressWarnings("unchecked")
            Map<String, Object> result = (Map<String, Object>) msg.obj;
            switch (msg.what) {
                case R.id.AWARD_SUCCESS:
                    if (!result.isEmpty()) {
                        @SuppressWarnings("unchecked")
                        List<AddressModel> showlist =
                                (List<AddressModel>) result.get(Constant.AWARD_LIST);
                        if(showlist == null) return;
                        if(showlist.size() > 0)
                            updateView(true);
                        else
                            updateView(false);
                        datas.clear();
                        for (int i = 0; i < showlist.size(); i++) {
                            datas.add(showlist.get(i));
                        }
                        adapter.setData(datas);
                        adapter.notifyDataSetChanged();
                    } else {
                        String returnContent = (String) result.get(Constant.RETURN_CONTENT);
                        Utility.toastShow(x.app(), returnContent);
                    }
                    break;
            }
        }
    };

    @ViewInject(R.id.address_lv)
    private ListView listView;
    private ListBaseAdapter<AddressModel> adapter;
    private List<AddressModel> datas;
    private void setShowList(){
        listView.setAdapter(getShowListAdapter());
        listView.setOnItemClickListener(this);
    }

    private ListBaseAdapter<AddressModel> getShowListAdapter(){
        datas = new ArrayList<>();
        adapter = new AddressAdapter(this, datas);
        return adapter;
    }

    private void getShowListInfo(String uidx){
        RequestParams params = new RequestParams(Constant.getBaseUrl() +"Page/Ucenter/AddressList.ashx");
        TokenVerify.addToken(this,params);
        params.addBodyParameter("uidx",uidx);
        x.http().post(params, new Callback.CommonCallback<String>() {

            @Override
            public void onSuccess(String result) {
                TokenVerify.saveCookie(ManagerAddressActivity.this);
                Map<String, Object> resultMap = ParseData.parseAddressInfo(result);
                mHandler.obtainMessage(R.id.AWARD_SUCCESS, resultMap).sendToTarget();
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

    public void handleAddress(int position, int modifytype){
        AddressModel model = datas.get(position);
        long user_id = preferences.getLong(Constant.USER_ID,0);
        RequestParams params = new RequestParams(Constant.getBaseUrl() +"Page/Ucenter/AddressDelete.ashx");
        TokenVerify.addToken(this,params);
        params.addBodyParameter("uidx", user_id + "");
        params.addBodyParameter("addressidx", model.getAddressidx()+"");
        //modifytype  1表示设默认地址，2表示删除
        params.addBodyParameter("modifytype", modifytype + "");

        x.http().post(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                if(result.toUpperCase().equals("SUCCESS")){
                    TokenVerify.saveCookie(ManagerAddressActivity.this);
                    long user_id = preferences.getLong(Constant.USER_ID, 0);
                    if (user_id != 0)
                        getShowListInfo(user_id + "");
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
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == Constant.RESULT_CODE_UPDATE) {
            long user_id = preferences.getLong(Constant.USER_ID, 0);
            if (user_id != 0)
                getShowListInfo(user_id + "");
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Bundle bundle = new Bundle();
        bundle.putSerializable("bundle",datas.get(position));
        Intent intent = new Intent();
        intent.putExtras(bundle);
        setResult(Constant.RESULT_CODE,intent);
        finish();
    }
}
