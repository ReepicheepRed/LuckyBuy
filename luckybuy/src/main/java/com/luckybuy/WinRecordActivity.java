package com.luckybuy;

import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.facebook.CallbackManager;
import com.facebook.share.widget.MessageDialog;
import com.facebook.share.widget.ShareDialog;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.luckybuy.adapter.WinPrizeAdapter;
import com.luckybuy.adapter.ListBaseAdapter;
import com.luckybuy.login.LoginUserUtils;
import com.luckybuy.model.WinRecordModel;
import com.luckybuy.network.ParseData;
import com.luckybuy.network.TokenVerify;
import com.luckybuy.share.FaceBookShare;
import com.luckybuy.share.ShareUtils;
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
@ContentView(R.layout.activity_win_record)
public class WinRecordActivity extends BaseActivity implements AdapterView.OnItemClickListener,PullToRefreshBase.OnRefreshListener2<ListView>,RefreshUtil{

    private SharedPreferences preferences;

    @ViewInject(R.id.title_activity)
    private TextView title_tv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        preferences = LoginUserUtils.getUserSharedPreferences(this);
        title_tv.setText(R.string.prize_history);
        init_Share();
        setShowList();
        long user_id = preferences.getLong(Constant.USER_ID,0);
        if(user_id != 0)
            getShowListInfo("","10", user_id+"",false);


    }

    @Event({R.id.back_iv,R.id.win_go_buy_btn})
    private void viewClick(View view){
        switch(view.getId()){
            case R.id.back_iv:
                setResult(Constant.RESULT_CODE_MINE);
                this.finish();
                break;
            case R.id.win_go_buy_btn:
                setResult(Constant.RESULT_CODE);
                this.finish();
                break;
        }
    }


    @ViewInject(R.id.blank_win_prize_rl)
    private RelativeLayout blank_win_prize_rl;

    private void updateView(boolean hasData){
        if(!hasData){
            blank_win_prize_rl.setVisibility(View.VISIBLE);
            listView.setVisibility(View.GONE);
            return;
        }

        blank_win_prize_rl.setVisibility(View.GONE);
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
                        List<WinRecordModel> showlist =
                                (List<WinRecordModel>) result.get(Constant.AWARD_LIST);
                        boolean orientation = (boolean) result.get("pull_up");
                        if(!orientation) datas.clear();

                        int size = showlist == null ? 0 : showlist.size();
                        for (int i = 0; i < size; i++) datas.add(showlist.get(i));

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

    @ViewInject(R.id.win_record_lv)
    private PullToRefreshListView listView;
    private ListBaseAdapter<WinRecordModel> adapter;
    private List<WinRecordModel> datas;
    private void setShowList(){
        listView.setAdapter(getShowListAdapter());
        listView.setOnItemClickListener(this);
        listView.setMode(PullToRefreshBase.Mode.BOTH);
        listView.setOnRefreshListener(this);
    }

    private ListBaseAdapter<WinRecordModel> getShowListAdapter(){
        datas = new ArrayList<>();
        adapter = new WinPrizeAdapter(this, datas);
        ((WinPrizeAdapter)adapter).setIsfriends(false);
        return adapter;
    }

    public void getShowListInfo(String lastid, String pagesize, String uidx, final boolean orientation){
        RequestParams params = new RequestParams(Constant.getBaseUrl() + "page/ucenter/win.ashx?pageable=1");
        TokenVerify.addToken(this,params);
        params.addBodyParameter("uidx",uidx,"form-data");
        params.addBodyParameter("pageSize",pagesize,"form-data");
        params.addBodyParameter("lasttimeid",lastid,"form-data");
        x.http().post(params, new Callback.CommonCallback<String>() {

            @Override
            public void onSuccess(String result) {
                TokenVerify.saveCookie(WinRecordActivity.this);
                Map<String, Object> resultMap = ParseData.parseWinRecordInfo(result);
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
                    getShowListInfo("","10", user_id+"",false);
                break;
        }
        FaceBookShare.callbackManager.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Bundle bundle = new Bundle();
        bundle.putSerializable("bundle",datas.get(position-1));
        Intent intent = new Intent();
        intent.setClass(this, ConfirmInfoActivity.class);
        intent.putExtras(bundle);
        startActivityForResult(intent,Constant.REQUEST_CODE);
    }

    private void init_Share(){
        FaceBookShare.callbackManager = CallbackManager.Factory.create();
        FaceBookShare.shareDialog = new ShareDialog(this);
        FaceBookShare.shareDialog.registerCallback(FaceBookShare.callbackManager, FaceBookShare.facebookCallback_share);
        FaceBookShare.messageDialog = new MessageDialog(this);
        FaceBookShare.messageDialog.registerCallback(FaceBookShare.callbackManager, FaceBookShare.facebookCallback_messenger);
    }

    private String commodityName;
    private String pictureUrl;
    public void popShare(int position){
        commodityName = datas.get(position).getTitle();
        pictureUrl = datas.get(position).getHeadpic();

        menuWindow = new PopupWindow_Share(this, itemsOnClick);
        menuWindow.showAtLocation(findViewById(R.id.activity_win_record_ll),
                Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, 0);
    }

    private PopupWindow_Share menuWindow;

    private View.OnClickListener itemsOnClick = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            // 隐藏弹出窗口
            menuWindow.dismiss();
            String content = getString(R.string.win_record_share_content,commodityName,"");
            String appUrl = getString(R.string.app_url_share);

            ShareUtils.setPictureUrl(pictureUrl);
            ShareUtils.setContent(commodityName,appUrl,true);
            switch (v.getId()) {
                case R.id.share_facebook_rl:
                    FaceBookShare.share_facebook(content,pictureUrl,appUrl);
                    //ShareUtils.showShare(DetailActivity.this,"Facebook",false);
                    break;
                case R.id.share_messenger_rl:
                    FaceBookShare.share_messenger(content,pictureUrl,appUrl);
                    //ShareUtils.showShare(DetailActivity.this,"FacebookMessenger",false);
                    break;
                case R.id.share_twitter_rl:
                    ShareUtils.showShare(WinRecordActivity.this,"Twitter",false);
                    break;
                case R.id.share_copy_rl:
                    ClipboardManager cm = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
                    // 将文本内容放到系统剪贴板里。
                    cm.setText(getString(R.string.copy_complete));
                    Utility.toastShow(x.app(),R.string.copy_complete);
                    break;
                case R.id.share_cancel_btn:
                    break;
            }
        }
    };

    private RefreshTask refreshTask;
    @Override
    public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {
        String lastid = "";
        refreshTask = new RefreshTask(this,listView,this);
        refreshTask.setLoadOrientation(lastid,false);
        refreshTask.execute();
    }

    @Override
    public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
        String lastid = String.valueOf(adapter.getLastid());
        refreshTask = new RefreshTask(this,listView,this);
        refreshTask.setLoadOrientation(lastid,true);
        refreshTask.execute();
    }


}
