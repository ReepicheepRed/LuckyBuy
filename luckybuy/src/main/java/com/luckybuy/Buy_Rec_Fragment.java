package com.luckybuy;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.luckybuy.adapter.F_SNS_SnatchAdapter;
import com.luckybuy.adapter.ListBaseAdapter;
import com.luckybuy.login.LoginUserUtils;
import com.luckybuy.model.AwardModel;
import com.luckybuy.model.SnatchAwardModel;
import com.luckybuy.network.ParseData;
import com.luckybuy.util.Constant;
import com.luckybuy.util.Utility;

import org.xutils.common.Callback;
import org.xutils.ex.HttpException;
import org.xutils.http.RequestParams;
import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by zhiPeng.S on 2016/5/16.
 */
@ContentView(R.layout.fragment_snatch)
public class Buy_Rec_Fragment extends BaseFragment implements AdapterView.OnItemClickListener {



    private SharedPreferences preferences;
    private String listType;
    public static Buy_Rec_Fragment newInstance(String type) {
        Buy_Rec_Fragment fragment = new Buy_Rec_Fragment();
        Bundle args = new Bundle();
        args.putString("listType", type);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        preferences = LoginUserUtils.getUserSharedPreferences(getActivity());
        long user_id = preferences.getLong(Constant.USER_ID,0);
        setShowList();
        Bundle bundle = getArguments();
        listType = bundle.getString("listType");
        getShowListInfo(listType,user_id+"","","10",false);
    }


    @ViewInject(R.id.blank_buy_record_rl)
    private RelativeLayout blank_buy_record_rl;

    private void updateView(boolean hasData){
        if(!hasData){
            blank_buy_record_rl.setVisibility(View.VISIBLE);
            listView.setVisibility(View.GONE);
            return;
        }

        blank_buy_record_rl.setVisibility(View.GONE);
        listView.setVisibility(View.VISIBLE);
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
                        List<SnatchAwardModel> showlist =
                                (List<SnatchAwardModel>) result.get(Constant.AWARD_LIST);

                        boolean orientation = (boolean)result.get("pull_up");
                        if (!orientation)   datas.clear();

                        int size = showlist == null? 0 : showlist.size();
                        for (int i = 0; i < size; i++) {
                            datas.add(showlist.get(i));
                        }
                        adapter.setData(datas);
                        adapter.notifyDataSetChanged();

                        updateView(!datas.isEmpty());
                    }
                    break;
            }
        }
    };

    @ViewInject(R.id.snatch_lv)
    private PullToRefreshListView listView;
    private ListBaseAdapter<SnatchAwardModel> adapter;
    private List<SnatchAwardModel> datas;
    private void setShowList(){
        listView.setAdapter(getShowListAdapter());
        listView.setOnItemClickListener(this);
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

    private ListBaseAdapter<SnatchAwardModel> getShowListAdapter(){
        datas = new ArrayList<>();
        adapter = new F_SNS_SnatchAdapter(getActivity(), datas);
        ((F_SNS_SnatchAdapter)adapter).setSelf(true);
        return adapter;
    }

    private void getShowListInfo(String listtype, String uidx, String lasttimeid, String pagesize,final boolean orientation){
        RequestParams params = new RequestParams(Constant.getBaseUrl() +"page/ucenter/playlist.ashx");
        params.addQueryStringParameter("listtype", listtype);
        params.addQueryStringParameter("uidx", uidx);
        params.addQueryStringParameter("lasttimeid", lasttimeid);
        params.addQueryStringParameter("pagesize", pagesize);
        x.http().get(params, new Callback.CommonCallback<String>() {

            @Override
            public void onSuccess(String result) {
                Map<String, Object> resultMap = ParseData.parseSnatchAwardInfo(result);
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
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Bundle bundle = new Bundle();
        AwardModel model = new AwardModel();
        model.setTitle(datas.get(position-1).getTitle());
        model.setSubtitle(datas.get(position-1).getSubtitle());
        model.setIdx(datas.get(position-1).getGoodid());
        model.setTimeid(datas.get(position-1).getTimesid());
        model.setTotal(datas.get(position-1).getTotal());
        model.setSaled(datas.get(position-1).getSaled());
        model.setHeadpic(datas.get(position-1).getHeadpic());
        model.setPersize(datas.get(position-1).getPersize());
        bundle.putSerializable("bundle", model);
        Intent intent = new Intent(getActivity(), DetailActivity.class);
        intent.putExtras(bundle);
        startActivity(intent);
    }

    private class GetDataTask extends AsyncTask<Void, Void, String> {

        @Override
        protected String doInBackground(Void... params) {
            long user_id = preferences.getLong(Constant.USER_ID,0);
            if(user_id != 0)
            switch (RefreshType){
                case pull_down_refresh:
                    getShowListInfo(listType,user_id+"","","10",false);
                    break;
                case pull_up_refresh:
                    long lastid = (adapter).getLastid();
                    getShowListInfo(listType,user_id+"",lastid + "","10",true);
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
