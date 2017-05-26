package com.luckybuy;

import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.View;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.luckybuy.adapter.DiscoverListAdapter;
import com.luckybuy.login.LoginUserUtils;
import com.luckybuy.model.DiscoverModel;
import com.luckybuy.model.FriendsModel;
import com.luckybuy.network.ParseData;
import com.luckybuy.network.TokenVerify;
import com.luckybuy.util.Constant;
import com.luckybuy.util.Utility;

import org.json.JSONException;
import org.json.JSONObject;
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
 * Created by zhiPeng.S on 2016/10/12.
 */
@ContentView(R.layout.friends_discover_frag)
public class Friends_Discover_Fragment extends BaseFragment {

    public static Friends_Discover_Fragment newInstance(int sectionNumber) {
        Friends_Discover_Fragment fragment = new Friends_Discover_Fragment();
        Bundle args = new Bundle();
        args.putInt("section_number", sectionNumber);
        fragment.setArguments(args);
        return fragment;
    }

    private SharedPreferences preferences;

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        preferences = LoginUserUtils.getUserSharedPreferences(getActivity());
        long user_id = preferences.getLong(Constant.USER_ID,0);
        setDiscoverList();
        getDiscoverListInfo("0","10",user_id+"",false);
        no_bask_record_tv.setText(R.string.no_bask_record_dsc);
        blank_bask_rl.setVisibility(View.GONE);
    }

    Handler mHandler = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            @SuppressWarnings("unchecked")
            Map<String, Object> result = (Map<String, Object>) msg.obj;
            switch (msg.what) {
                case R.id.DISCOVER:
                    if (!result.isEmpty()) {
                        @SuppressWarnings("unchecked")
                        List<DiscoverModel> showlist_dsc =
                                (List<DiscoverModel>) result.get(Constant.DISCOVER_LIST);
                        boolean orientation = (boolean) result.get("pull_up");
                        if(!orientation) datas_discover.clear();

                        int size = showlist_dsc == null ? 0 : showlist_dsc.size();
                        for (int i = 0; i < size; i++) datas_discover.add(showlist_dsc.get(i));
                        adapter_dsc.setData(datas_discover);
                        adapter_dsc.notifyDataSetChanged();

                        //Utility.updateView(discover_list,blank_bask_rl,!datas_discover.isEmpty());
                    }
                    break;
            }
        }
    };

    @ViewInject(R.id.blank_bask_rl)
    private RelativeLayout blank_bask_rl;

    @ViewInject(R.id.no_bask_record_tv)
    private TextView no_bask_record_tv;

    @ViewInject(R.id.discover_list)
    private PullToRefreshListView discover_list;

    private DiscoverListAdapter adapter_dsc;

    private List<DiscoverModel> datas_discover;
    private void setDiscoverList(){
        discover_list.setAdapter(getdscListAdapter());
        setListViewListener();
    }

    private DiscoverListAdapter getdscListAdapter(){
        datas_discover = new ArrayList<>();
        adapter_dsc = new DiscoverListAdapter(getActivity(), datas_discover);
        adapter_dsc.setFragment(this);
        return adapter_dsc;
    }

    private void getDiscoverListInfo(String lastsidx, String pagesize, String uidx,final boolean orientation){
        RequestParams params = new RequestParams(Constant.getBaseUrl() + "page/good/showlist.ashx");
        params.addQueryStringParameter("lastsidx",lastsidx);
        params.addQueryStringParameter("pagesize",pagesize);
        params.addQueryStringParameter("uidx",uidx);
        x.http().get(params, new Callback.CommonCallback<String>() {

            @Override
            public void onSuccess(String result) {
                Map<String, Object> resultMap = ParseData.parseDiscoverInfo(result);
                resultMap.put("pull_up", orientation);
                mHandler.obtainMessage(R.id.DISCOVER, resultMap).sendToTarget();
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

    public void clickZan(final DiscoverModel model){
        RequestParams params = new RequestParams(Constant.getBaseUrl() + "Page/Ucenter/ShowLiked.ashx");
        TokenVerify.addToken(getActivity(),params);
        long user_id = preferences.getLong(Constant.USER_ID,0);
        if (user_id == 0) return;
        String sidx = model.getSidx() + "";
        params.addBodyParameter("uidx", user_id+"");
        params.addBodyParameter("sidx", sidx);

        x.http().post(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                try {
                    TokenVerify.saveCookie(getActivity());
                    JSONObject jso = new JSONObject(result);
                    String flag = jso.getString("result");
                    if(flag.toLowerCase().equals("success")){
                        long user_id = preferences.getLong(Constant.USER_ID,0);
                        getDiscoverListInfo("0","10",user_id+"",false);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
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

    private int RefreshType;
    private final int pull_up_refresh = 0;
    private final int pull_down_refresh = 1;
    private void setListViewListener(){
        discover_list.setMode(PullToRefreshBase.Mode.BOTH);
        discover_list.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener2<ListView>() {
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
        discover_list.setOnLastItemVisibleListener(new PullToRefreshBase.OnLastItemVisibleListener() {

            @Override
            public void onLastItemVisible() {
                RefreshType = pull_up_refresh;
                new GetDataTask().execute();
            }
        });
    }

    private class GetDataTask extends AsyncTask<Void, Void, String> {

        private String lastId;
        private String user_id;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            lastId = adapter_dsc.getLastid() + "";
            user_id = preferences.getLong(Constant.USER_ID,0) + "";
        }

        @Override
        protected String doInBackground(Void... params) {
            switch (RefreshType){
                case pull_down_refresh:
                    getDiscoverListInfo("","10", user_id, false);
                    break;
                case pull_up_refresh:
                    getDiscoverListInfo(lastId,"10",user_id,true);
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
            discover_list.onRefreshComplete();
            super.onPostExecute(result);
        }
    }
}
