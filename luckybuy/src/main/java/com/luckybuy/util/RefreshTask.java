package com.luckybuy.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;

import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.luckybuy.login.LoginUserUtils;

import org.xutils.x;

/**
 * Created by zhiPeng.S on 2016/10/25.
 */

public class RefreshTask extends AsyncTask<Void, Void, String>{

    private RefreshUtil refreshUtil;
    private PullToRefreshListView listView;

    private String position;
    private String pagesize = "10";
    private String user_id;
    private boolean orientation;

    private SharedPreferences preferences;
    private Context mContext;

    public RefreshTask(Context context,PullToRefreshListView listView,RefreshUtil refreshUtil) {
        preferences = LoginUserUtils.getUserSharedPreferences(x.app());
        user_id = String.valueOf(preferences.getLong(Constant.USER_ID,0));
        mContext = context;
        this.listView = listView;
        this.refreshUtil = refreshUtil;
    }

    public void setLoadOrientation(String position,boolean orientation){
        this.position = position;
        this.orientation = orientation;
    }

    public void setUserId(String userId){
        this.user_id = userId;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    @Override
    protected String doInBackground(Void... params) {

        refreshUtil.getShowListInfo(position,pagesize,user_id,orientation);


        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return "";
    }

    @Override
    protected void onPostExecute(String result) {
        listView.onRefreshComplete();
        super.onPostExecute(result);
    }

}
