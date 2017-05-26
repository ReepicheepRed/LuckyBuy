package com.luckybuy;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.luckybuy.adapter.DiscoverListAdapter;
import com.luckybuy.adapter.ListBaseAdapter;
import com.luckybuy.model.AwardModel;
import com.luckybuy.model.DiscoverModel;
import com.luckybuy.network.ParseData;
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
 * Created by zhiPeng.S on 2016/8/1.
 */
@ContentView(R.layout.activity_general)
public class DetailBaskActivity extends BaseActivity{

    @ViewInject(R.id.title_activity)
    private TextView title_tv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initView();
    }

    private String goodid = "";
    private void initView(){
        title_tv.setText(R.string.detail_with_share);
        setShowList();

        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        if (bundle.getSerializable("bundle") instanceof AwardModel) {
            AwardModel awardModel = (AwardModel) bundle.getSerializable("bundle");
            if(awardModel == null) return;
            goodid = awardModel.getIdx() + "";
        }
        getShowListInfo("0", "10",goodid, false);
    }

    @Event({R.id.back_iv})
    private void viewClick(View view) {
        switch (view.getId()) {
            case R.id.back_iv:
                this.finish();
                break;
        }
    }

    @ViewInject(R.id.activity_general_ll)
    private LinearLayout activity_general_ll;

    private void updateView(boolean hasData){
        RelativeLayout blank_bask_rl = (RelativeLayout) LayoutInflater.from(this).inflate(R.layout.blank_bask,null);
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.MATCH_PARENT);
        TextView tip = (TextView) blank_bask_rl.findViewById(R.id.no_bask_record_tv);
        tip.setText(R.string.no_bask_record2);
        activity_general_ll.addView(blank_bask_rl,layoutParams);
        blank_bask_rl.setVisibility(View.GONE);
        if(!hasData){
            blank_bask_rl.setVisibility(View.VISIBLE);
            listView.setVisibility(View.GONE);
            return;
        }

        blank_bask_rl.setVisibility(View.GONE);
        listView.setVisibility(View.VISIBLE);
    }

    @SuppressLint("HandlerLeak")
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
                        List<DiscoverModel> showlist =
                                (List<DiscoverModel>) result.get(Constant.DISCOVER_LIST);

                        boolean orientation = (boolean)result.get("pull_up");
                        if (!orientation)   datas.clear();

                        int size = showlist == null ? 0 : showlist.size();
                        for (int i = 0; i < size; i++)  datas.add(showlist.get(i));

                        adapter.setData(datas);
                        adapter.notifyDataSetChanged();

                        updateView(!datas.isEmpty());
                    } else {
                        String returnContent = (String) result.get(Constant.RETURN_CONTENT);
                        Utility.toastShow(x.app(), returnContent);
                    }
                    break;
            }
        }
    };

    @ViewInject(R.id.listView)
    private PullToRefreshListView listView;
    private ListBaseAdapter<DiscoverModel> adapter;
    private List<DiscoverModel> datas;

    public ListBaseAdapter<DiscoverModel> getAdapter() {
        return adapter;
    }

    private void setShowList(){
        listView.setAdapter(getShowListAdapter());
        setListViewListener();
    }

    private int RefreshType;
    private final int pull_up_refresh = 0;
    private final int pull_down_refresh = 1;
    private void setListViewListener(){
        listView.setMode(PullToRefreshBase.Mode.BOTH);
        listView.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener2<ListView>() {
            @Override
            public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {
                String label = DateUtils.formatDateTime(x.app(), System.currentTimeMillis(),
                        DateUtils.FORMAT_SHOW_TIME | DateUtils.FORMAT_SHOW_DATE | DateUtils.FORMAT_ABBREV_ALL);
                refreshView.getLoadingLayoutProxy().setLastUpdatedLabel(label);
                RefreshType = pull_down_refresh;
                new GetDataTask().execute();
            }

            @Override
            public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
                RefreshType = pull_up_refresh;
                new GetDataTask().execute();
            }

        });

        // Add an end-of-list listener
        listView.setOnLastItemVisibleListener(new PullToRefreshBase.OnLastItemVisibleListener() {

            @Override
            public void onLastItemVisible() {
                Toast.makeText(x.app(), "End of List!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private ListBaseAdapter<DiscoverModel> getShowListAdapter(){
        datas = new ArrayList<>();
        adapter = new DiscoverListAdapter(this, datas);
        ((DiscoverListAdapter)adapter).setDiscover(false);
        return adapter;
    }


    public void getShowListInfo(String lastsidx, String pagesize, String goodid, final boolean orientation){
        RequestParams params = new RequestParams(Constant.getBaseUrl() + "page/good/showlist.ashx");
        params.addQueryStringParameter("lastsidx",lastsidx);
        params.addQueryStringParameter("pagesize",pagesize);
        params.addQueryStringParameter("goodid",goodid);
        x.http().get(params, new Callback.CommonCallback<String>() {

            @Override
            public void onSuccess(String result) {
                Map<String, Object> resultMap = ParseData.parseDiscoverInfo(result);
                resultMap.put("pull_up", orientation);
                mHandler.obtainMessage(R.id.AWARD_SUCCESS, resultMap).sendToTarget();
            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
                Toast.makeText(x.app(), ex.getMessage(), Toast.LENGTH_LONG).show();
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

    private class GetDataTask extends AsyncTask<Void, Void, String> {

        private long lastid;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            lastid = ((DiscoverListAdapter)adapter).getLastid();
        }

        @Override
        protected String doInBackground(Void... params) {
            switch (RefreshType){
                case pull_down_refresh:
                    getShowListInfo("0","10",goodid,false);
                    break;
                case pull_up_refresh:
                    getShowListInfo(lastid + "","10",goodid,true);
                    break;
            }


            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
            }
            return "";
        }

        @Override
        protected void onPostExecute(String result) {
            listView.onRefreshComplete();
            super.onPostExecute(result);
        }
    }
}
