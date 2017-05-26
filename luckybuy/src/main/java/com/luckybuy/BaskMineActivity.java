package com.luckybuy;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.luckybuy.adapter.BaskMineAdapter;
import com.luckybuy.adapter.ListBaseAdapter;
import com.luckybuy.login.LoginUserUtils;
import com.luckybuy.model.BaskSNSModel;
import com.luckybuy.network.ParseData;
import com.luckybuy.network.TokenVerify;
import com.luckybuy.util.Constant;
import com.luckybuy.util.RefreshTask;
import com.luckybuy.util.RefreshUtil;
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
 * Created by zhiPeng.S on 2016/5/16.
 */
@ContentView(R.layout.activity_bask_mine)
public class BaskMineActivity extends BaseActivity implements AdapterView.OnItemClickListener,RefreshUtil,PullToRefreshBase.OnRefreshListener2<ListView>{

    private SharedPreferences preferences;

    @ViewInject(R.id.title_activity)
    private TextView title_tv;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        title_tv.setText(R.string.sun_share);
        preferences = LoginUserUtils.getUserSharedPreferences(this);
        setShowList();

        long user_id = preferences.getLong(Constant.USER_ID,0);
        if(user_id != 0)
            getShowListInfo("0","10", user_id+"",false);
    }

    @Event({R.id.back_iv,R.id.bask_rule_tv})
    private void viewClick(View view){
        switch (view.getId()){
            case R.id.back_iv:
                this.finish();
                break;
            case R.id.bask_rule_tv:
                Intent intent = new Intent();
                intent.putExtra(Constant.WEB_H5,Constant.BASK_RULE);
                intent.setClass(this,WebActivity.class);
                startActivity(intent);
                break;
        }
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
                        List<BaskSNSModel> showlist =
                                (List<BaskSNSModel>) result.get(Constant.AWARD_LIST);
                        boolean orientation = (boolean) result.get("pull_up");
                        if(!orientation) datas.clear();

                        int size = showlist == null ? 0 : showlist.size();
                        for (int i = 0; i < size; i++) datas.add(showlist.get(i));

                        adapter.setData(datas);
                        adapter.notifyDataSetChanged();
                        Utility.updateView(listView,mine_bask_ll,!datas.isEmpty());
                    } else {
                        String returnContent = (String) result.get(Constant.RETURN_CONTENT);
                        Utility.toastShow(x.app(), returnContent);
                    }
                    break;
            }
        }
    };

    @ViewInject(R.id.mine_bask_ll)
    private LinearLayout mine_bask_ll;

    @ViewInject(R.id.bask_mine_lv)
    private PullToRefreshListView listView;
    private ListBaseAdapter<BaskSNSModel> adapter;
    private List<BaskSNSModel> datas;
    private void setShowList(){
        listView.setAdapter(getShowListAdapter());
        listView.setMode(PullToRefreshBase.Mode.BOTH);
        listView.setOnRefreshListener(this);
    }

    private ListBaseAdapter<BaskSNSModel> getShowListAdapter(){
        datas = new ArrayList<>();
        adapter = new BaskMineAdapter(this, datas);
        return adapter;
    }

    @Override
    public void getShowListInfo(String lastid, String pagesize, String uidx, final boolean orientation){
    RequestParams params = new RequestParams(Constant.getBaseUrl() +"Page/Ucenter/MyShowList.ashx?pageable=1");
        TokenVerify.addToken(this,params);
        params.setMultipart(true);
        params.addBodyParameter("pagesize", pagesize);
        params.addBodyParameter("lastpos", lastid);
        params.addBodyParameter("uidx", uidx);
        x.http().post(params, new Callback.CommonCallback<String>() {

            @Override
            public void onSuccess(String result) {
                TokenVerify.saveCookie(BaskMineActivity.this);
                Log.e(TAG, result);
                Map<String, Object> resultMap = ParseData.parseBaskInfo(result);
                resultMap.put("pull_up", orientation);
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (resultCode){
            case Constant.RESULT_CODE_UPDATE:
                long user_id = preferences.getLong(Constant.USER_ID,0);
                if(user_id != 0)
                    getShowListInfo("0", "10", user_id+"",false);
                break;
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

    }

    private RefreshTask refreshTask;
    @Override
    public void onPullDownToRefresh(PullToRefreshBase refreshView) {
        String lastid = "";
        refreshTask = new RefreshTask(this,listView,this);
        refreshTask.setLoadOrientation(lastid,false);
        refreshTask.execute();
    }

    @Override
    public void onPullUpToRefresh(PullToRefreshBase refreshView) {
        String lastid = String.valueOf(adapter.getLastid());
        refreshTask = new RefreshTask(this,listView,this);
        refreshTask.setLoadOrientation(lastid,true);
        refreshTask.execute();
    }
}
