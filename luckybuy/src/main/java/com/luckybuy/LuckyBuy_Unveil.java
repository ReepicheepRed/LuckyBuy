package com.luckybuy;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.luckybuy.adapter.AwardUnveilAdapter;
import com.luckybuy.adapter.ListBaseAdapter;
import com.luckybuy.layout.LoadingDialog;
import com.luckybuy.model.AwardModel;
import com.luckybuy.model.UnveilAwardModel;
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

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * Created by zhiPeng.S on 2016/5/16.
 */
@ContentView(R.layout.fragment_unveil)
public class LuckyBuy_Unveil extends LazyFragment implements AdapterView.OnItemClickListener{


    public static LuckyBuy_Unveil newInstance(int sectionNumber) {
        LuckyBuy_Unveil fragment = new LuckyBuy_Unveil();
        Bundle args = new Bundle();
        args.putInt("section_number", sectionNumber);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setShowList();
//        loadingDialog = new LoadingDialog(getActivity());
        isPrepared = true;
        lazyLoad();
    }

    // 标志位，标志已经初始化完成。
    private boolean isPrepared;

    private int pageIndex = 1;

    @Override
    protected void lazyLoad() {
        if(!isPrepared || !isVisible) {
            return;
        }
        //填充各控件的数据

        getShowListInfo("1","10",false);
    }


    @Override
    public void onResume() {
        super.onResume();

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
                        List<UnveilAwardModel> showlist =
                                (List<UnveilAwardModel>) result.get(Constant.AWARD_LIST);
                        boolean orientation = (boolean)result.get("pull_up");
                        if (!showlist.isEmpty()) {
                            if (!orientation)
                                datas.clear();
                            for (int i = 0; i < showlist.size(); i++) {
                                datas.add(showlist.get(i));
                            }
                        }
                        adapter.setData(datas);
                        adapter.notifyDataSetChanged();

                    }
                    break;
            }
        }
    };

    @Event({R.id.reloading_btn})
    private void viewClick(View v){
        switch (v.getId()){
            case R.id.reloading_btn:
                getShowListInfo("1","10",false);
                break;
        }
    }

    @ViewInject(R.id.loading_fail_rl)
    private RelativeLayout loading_fail_rl;

    @ViewInject(R.id.unveil_lv)
    private PullToRefreshListView listView;

    private ListBaseAdapter<UnveilAwardModel> adapter;
    private List<UnveilAwardModel> datas;
    private void setShowList(){
        listView.setAdapter(getShowListAdapter());
        listView.setOnItemClickListener(this);
        setListViewListener();
    }

    private ListBaseAdapter<UnveilAwardModel> getShowListAdapter(){
        datas = new ArrayList<>();
        adapter = new AwardUnveilAdapter(getActivity(), datas,this);
        return adapter;
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
                RefreshType = pull_up_refresh;
                new GetDataTask().execute();
            }
        });
    }

//    private LoadingDialog loadingDialog;
    public void getShowListInfo(String pageindex, String pagesize,final boolean orientation){
//        if(!loadingDialog.isShowing())
//            loadingDialog.showDialog();
        RequestParams params = new RequestParams(Constant.getBaseUrl() + "page/good/waitlist.ashx");
        params.addQueryStringParameter("pageindex",pageindex);
        params.addQueryStringParameter("pagesize",pagesize);
        x.http().get(params, new Callback.CommonCallback<String>() {

            @Override
            public void onSuccess(String result) {
//                loadingDialog.dismiss();
                Log.e(TAG, "onSuccess: " + result );
                Map<String, Object> resultMap = ParseData.parseUnveilAwardInfo(result);
                resultMap.put("pull_up", orientation);
                mHandler.obtainMessage(R.id.AWARD_SUCCESS, resultMap).sendToTarget();
            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
                Utility.updateView(listView,loading_fail_rl,!datas.isEmpty());
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
//                loadingDialog.dismiss();
            }
        });
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Bundle bundle = new Bundle();
        AwardModel model = new AwardModel();
        model.setIdx(datas.get(position-1).getGoodid());
        model.setTimeid(datas.get(position-1).getTimeid());
        bundle.putSerializable("bundle", model);
        Intent intent = new Intent(getActivity(), DetailActivity.class);
        intent.putExtras(bundle);
        startActivityForResult(intent,Constant.REQUEST_CODE_DETAIL);
    }

    private class GetDataTask extends AsyncTask<Void, Void, String> {

        private String lastId;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            lastId = ((AwardUnveilAdapter)adapter).getLastid() + "";
        }

        @Override
        protected String doInBackground(Void... params) {

            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
            }
            return "";
        }

        @Override
        protected void onPostExecute(String result) {
            switch (RefreshType){
                case pull_down_refresh:
                    pageIndex = 1;
                    getShowListInfo(pageIndex+"","10",false);
                    break;
                case pull_up_refresh:
                    pageIndex++;
                    getShowListInfo(pageIndex+"","10",true);
                    break;
            }
            listView.onRefreshComplete();
            super.onPostExecute(result);
        }
    }

/*
 * 清除倒计时 -1清空所有, 大于等于0时清除指定位置
 */
    private void clearList(int location) {
        if (location == -1) {
            if (listTimer.size() > 0) {
                for (int i = 0; i < listTimer.size(); i++) {
                    if (listTimer.get(i) != null) {
                        listTimer.get(i).cancel();
                        listTimer.set(i, null);
                    }
                }
            }
        } else {
            listTimer.get(location).cancel();
            listTimer.set(location, null);
        }
    }

    private List<UnveilCountDownTimer> listTimer = new ArrayList<>();

    private void initCollection(int size) {
        for (int i = 0; i < size; i++) {
            listTimer.add(null);
        }
    }

    private void getTime() {
        if (adapter.getData().size() > 0) {
            int star = listView.getRefreshableView().getFirstVisiblePosition();
            int end = listView.getRefreshableView().getLastVisiblePosition();
            //star=star;
            initCollection(adapter.getData().size());

            for (int i = star; i < 4; i++) {
                try {
                    //long placeOrderTime = adapter.getData().get(i).getLucktime();
                    long diff = 0;
                    if (diff > 0) {

                        if (listTimer.get(i) != null) {
                            listTimer.get(i).cancel();
                        }
                        listTimer.set(i, new UnveilCountDownTimer(
                                diff*1000, 1, i));

                        if (diff>0) {
                            listTimer.get(i).start();
                        }
                    } else {

                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

        }

    }


    class UnveilCountDownTimer extends CountDownTimer {
        private View ll;
        private TextView tvn;
        private Integer mPositon;

        public UnveilCountDownTimer(long millisInFuture,
                                        long countDownInterval, Integer positon) {
            super(millisInFuture, countDownInterval);
            mPositon = positon;
        }

        @Override
        public void onTick(long millisUntilFinished) {
            String minStr = "";
            String secStr = "";
            String mscStr = "";
            long minute = millisUntilFinished / (1000 * 60);
            if (minute >= 10) {
                minStr = minute + "";
            } else {
                minStr = "0" + minute;
            }

            long second = (millisUntilFinished - minute * 1000 * 60) / 1000;
            if (second >= 10) {
                secStr =  second+ "";
            } else {
                secStr = "0" + second;
            }

            long msecond = (millisUntilFinished - (minute* 60 + second)*1000) / 10;
            if ( msecond >= 10) {
                mscStr = msecond + "";
            } else {
                mscStr = "0" + msecond;
            }

            String str =  minStr + ":" + secStr + ":" + mscStr;

            ll = listView.getRefreshableView().getChildAt(mPositon + 1);
            if (ll != null) {
                tvn = (TextView) ll.findViewById(R.id.cd_time_tv);
                if (tvn != null) {
                    tvn.setText(str);
                }
            }
        }

        @Override
        public void onFinish() {
            ll = listView.getRefreshableView().getChildAt(mPositon + 1);
            if (ll != null) {
                tvn = (TextView) ll.findViewById(R.id.cd_time_tv);
                tvn.setText("计算中...");
                clearList(mPositon);
                //new Thread(runnable).start();
            }
        }
    }
}
