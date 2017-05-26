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
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.luckybuy.adapter.AwardAdapter;
import com.luckybuy.adapter.AwardUnveilAdapter;
import com.luckybuy.adapter.DiscoverListAdapter;
import com.luckybuy.adapter.FriendsListAdapter;
import com.luckybuy.adapter.ListBaseAdapter;
import com.luckybuy.login.LoginUserUtils;
import com.luckybuy.model.AwardModel;
import com.luckybuy.model.DiscoverModel;
import com.luckybuy.model.BulletinModel;
import com.luckybuy.model.FriendsModel;
import com.luckybuy.network.ParseData;
import com.luckybuy.network.TokenVerify;
import com.luckybuy.util.Constant;
import com.luckybuy.util.Utility;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.xutils.common.Callback;
import org.xutils.ex.HttpException;
import org.xutils.http.RequestParams;
import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.Event;
import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by zhiPeng.S on 2016/5/16.
 */
@ContentView(R.layout.fragment_friends)
public class LuckyBuy_Friends2 extends BaseFragment implements AdapterView.OnItemClickListener{

    public static LuckyBuy_Friends newInstance(int sectionNumber) {
        LuckyBuy_Friends fragment = new LuckyBuy_Friends();
        Bundle args = new Bundle();
        args.putInt("section_number", sectionNumber);
        fragment.setArguments(args);
        return fragment;
    }


    Handler mHandler = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            @SuppressWarnings("unchecked")
            Map<String, Object> result = (Map<String, Object>) msg.obj;
            switch (msg.what) {
                case R.id.FRIENDS:
                    if (!result.isEmpty()) {
                        @SuppressWarnings("unchecked")
                        List<FriendsModel> showlist =
                                (List<FriendsModel>) result.get(Constant.FRIENDS_LIST);
                        if(showlist == null) return;
                        if (!showlist.isEmpty()) {
                            datas_friends.clear();
                            for (int i = 0; i < showlist.size(); i++) {
                                datas_friends.add(showlist.get(i));
                            }
                        }

                        updateFriends(!datas_friends.isEmpty());

                        adapter.setData(datas_friends);
                        adapter.notifyDataSetChanged();
                    } else {
                        String returnContent = (String) result.get(Constant.RETURN_CONTENT);
                        Utility.toastShow(x.app(), returnContent);
                    }
                    break;
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

    private SharedPreferences preferences;

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        preferences = LoginUserUtils.getUserSharedPreferences(getActivity());
        setFriendsList();
        long user_id = preferences.getLong(Constant.USER_ID,0);
        if(user_id != 0)
            getFriendsListInfo(user_id+"","10","0");
        setDiscoverList();
        getDiscoverListInfo("0","10",user_id+"",false);
        setFriendsState(true);
        no_bask_record_tv.setText(R.string.no_bask_record_dsc);
        blank_bask_rl.setVisibility(View.GONE);
    }

    @ViewInject(R.id.friends_tv)
    private TextView friends_tv;

    @ViewInject(R.id.discover_tv)
    private TextView discover_tv;

    @ViewInject(R.id.friends_selected_iv)
    private ImageView fri_sel_iv;

    @ViewInject(R.id.discover_selected_iv)
    private ImageView dcv_sel_iv;

    @Event({R.id.friends_tv,R.id.discover_tv})
    private void onClick_F(View view){
        switch(view.getId()){
            case R.id.friends_tv:
                setFriendsState(true);
                break;
            case R.id.discover_tv:
                setFriendsState(false);
                break;
        }
    }

    private void setFriendsState(boolean flag){
        int state = flag? 0 : 1;
        switch (state){
            case 0:
                friends_tv.setTextColor(getResources().getColor(R.color.light_red));
                fri_sel_iv.setVisibility(View.VISIBLE);
                discover_tv .setTextColor(getResources().getColor(R.color.text_3_c));
                dcv_sel_iv.setVisibility(View.INVISIBLE);
                //setFriendsList();
                adapter.notifyDataSetChanged();
                friends_list.setVisibility(View.VISIBLE);
                discover_list.setVisibility(View.GONE);
                updateFriends(false);
                break;
            case 1:
                friends_tv.setTextColor(getResources().getColor(R.color.text_3_c));
                fri_sel_iv.setVisibility(View.INVISIBLE);
                discover_tv .setTextColor(getResources().getColor(R.color.light_red));
                dcv_sel_iv.setVisibility(View.VISIBLE);
                //setDiscoverList();
                adapter_dsc.notifyDataSetChanged();
                friends_list.setVisibility(View.GONE);
                discover_list.setVisibility(View.VISIBLE);
                //Utility.updateView(discover_list,blank_bask_rl,false);
                break;
        }
    }


    @ViewInject(R.id.friends_list)
    private ListView friends_list;

    private ListBaseAdapter<FriendsModel> adapter;

    private List<FriendsModel> datas_friends;

    private void setFriendsList(){

        friends_list.setAdapter(getFriendsListAdapter());
        friends_list.setOnItemClickListener(this);
    }

    private ListBaseAdapter<FriendsModel> getFriendsListAdapter(){
        datas_friends = new ArrayList<>();
        adapter = new FriendsListAdapter(getActivity(), datas_friends);
        return adapter;
    }

    private void getFriendsListInfo(String uidx, String pagesize, String lastpos){
        RequestParams params = new RequestParams(Constant.getBaseUrl() + "page/ucenter/friends.ashx");

        params.addQueryStringParameter("uidx", uidx);
        params.addQueryStringParameter("pagesize", pagesize);
        params.addQueryStringParameter("lastpos", lastpos);

        x.http().get(params, new Callback.CommonCallback<String>() {

            @Override
            public void onSuccess(String result) {
                Map<String, Object> resultMap = ParseData.parseFriendsInfo(result);
                mHandler.obtainMessage(R.id.FRIENDS, resultMap).sendToTarget();
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
//        adapter_dsc.setFragment(this);
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

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Bundle bundle = new Bundle();
        bundle.putSerializable("bundle",datas_friends.get(position));
        Intent intent = new Intent(getActivity(), SNS_FriendsActivity.class);
        intent.putExtras(bundle);
        startActivity(intent);
    }

    @ViewInject(R.id.friends_no_one_ll)
    private LinearLayout friends_no_one_ll;

    @ViewInject(R.id.invite_friends_rl)
    private RelativeLayout invite_friends_rl;

    private void updateFriends(boolean hasFriends){
        if(hasFriends){
            friends_no_one_ll.setVisibility(View.GONE);
        }else {
            invite_friends_rl.setVisibility(View.GONE);
            friends_no_one_ll.setVisibility(View.VISIBLE);
        }
    }

    @ViewInject(R.id.blank_bask_rl)
    private RelativeLayout blank_bask_rl;

    @ViewInject(R.id.no_bask_record_tv)
    private TextView no_bask_record_tv;
}
