package com.luckybuy;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.text.SpannableStringBuilder;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.ViewFlipper;
import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.login.LoginManager;
import com.facebook.share.widget.MessageDialog;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.luckybuy.adapter.FriendsListAdapter2;
import com.luckybuy.adapter.ListBaseAdapter;
import com.luckybuy.login.FaceBookLogin;
import com.luckybuy.login.LoginUserUtils;
import com.luckybuy.model.FriendsModel;
import com.luckybuy.network.ParseData;
import com.luckybuy.presenter.FriendsListPresenter;
import com.luckybuy.share.FaceBookShare;
import com.luckybuy.util.Constant;
import com.luckybuy.util.RefreshTask;
import com.luckybuy.util.RefreshUtil;
import com.luckybuy.util.StringUtil;
import com.luckybuy.util.Utility;

import org.xutils.common.Callback;
import org.xutils.common.util.DensityUtil;
import org.xutils.ex.HttpException;
import org.xutils.http.RequestParams;
import org.xutils.image.ImageOptions;
import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.Event;
import org.xutils.view.annotation.ViewInject;
import org.xutils.x;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * Created by zhiPeng.S on 2016/10/11.
 */

@ContentView(R.layout.friends_frag)
public class Friends_fragment extends BaseFragment implements AdapterView.OnItemClickListener,FriendsListPresenter,RefreshUtil,PullToRefreshBase.OnRefreshListener2<ListView>{

    public static Friends_fragment newInstance(int sectionNumber) {
        Friends_fragment fragment = new Friends_fragment();
        Bundle args = new Bundle();
        args.putInt("section_number", sectionNumber);
        fragment.setArguments(args);
        return fragment;
    }

    private SharedPreferences preferences;
    private SharedPreferences.Editor editor;

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        init();
        setFriendsList();
        selectLayout();
        init_Share();
    }

    @Event({R.id.fb_login_rl,R.id.invite_friends_rl})
    private void viewClick(View view) {
        AccessToken accessToken = AccessToken.getCurrentAccessToken();
        switch(view.getId()){
            case R.id.fb_login_rl:
                if (!(accessToken == null || accessToken.isExpired())) {
                    LoginManager.getInstance().logOut();
                }
                LoginManager.getInstance().logInWithReadPermissions(this, Arrays.asList("public_profile", "user_friends"));
                break;
            case R.id.invite_friends_rl:
                isInvite = true;
                inviteFriends();
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
                case R.id.FRIENDS:
                    @SuppressWarnings("unchecked")
                    List<FriendsModel> showlist =
                            (List<FriendsModel>) result.get(Constant.FRIENDS_LIST);
                    listView.setVisibility(View.VISIBLE);
                    boolean orientation = (boolean) result.get("pull_up");
                    if (!orientation) datas.clear();

                    int size = showlist == null ? 0 : showlist.size();
                    for (int i = 0; i < size; i++) datas.add(showlist.get(i));

                    adapter.setData(datas);
                    adapter.notifyDataSetChanged();
                    ViewGroup.LayoutParams params = listView.getLayoutParams();
                    if(datas.size() > 1) {
                        params.height = ViewGroup.LayoutParams.MATCH_PARENT;
                        vf_friends.setVisibility(View.GONE);
                    }else {
                        params.height = Utility.measureItemHeight(adapter,0,listView.getRefreshableView());
                    }
                    listView.setLayoutParams(params);

            }
        }
    };

//    @ViewInject(R.id.item_friends_list2_rl)
//    private RelativeLayout item_friends_list2_rl;

    @ViewInject(R.id.fb_login_rl)
    private RelativeLayout fb_login_rl;
    @ViewInject(R.id.viewFlipper_friends)
    private ViewFlipper vf_friends;

    @ViewInject(R.id.invite_friends_picture)
    private ImageView invite_friends_picture;
    @ViewInject(R.id.invite_friends_tip)
    private TextView invite_friends_tip;

    private void init(){
        preferences = LoginUserUtils.getUserSharedPreferences(x.app());
        editor = preferences.edit();
        imageOptions = new ImageOptions.Builder()
                .setSize(DensityUtil.dip2px(50), DensityUtil.dip2px(50))
                .setRadius(DensityUtil.dip2px(25))
                .setCrop(true)
                .setPlaceholderScaleType(ImageView.ScaleType.MATRIX)
                .setImageScaleType(ImageView.ScaleType.CENTER_CROP)
                .setLoadingDrawableId(R.mipmap.haoyouliebiaotouxiang)
                .setFailureDrawableId(R.mipmap.haoyouliebiaotouxiang)
                .build();
        invite_friends_picture.setImageResource(R.mipmap.pk_icon);
        invite_friends_tip.setText(R.string.invite_friends_tip);
        listView.setVisibility(View.GONE);
    }
    private long user_id;
    @Override
    public void selectLayout(){
        long user_id_fb = preferences.getLong(Constant.USER_ID_FB,0);
        user_id = preferences.getLong(Constant.USER_ID,0);
        if(user_id != 0){
            getShowListInfo("","10", user_id+"", false);
        }

        if(user_id_fb != 0){
            updateShowLayout(1);
        }else {
            updateShowLayout(0);
            FaceBookLogin.init_facebook(getActivity());
            FaceBookLogin.setFriendsListPresenter(this);
        }

    }

    private void updateShowLayout(int index){
        vf_friends.setDisplayedChild(index);
    }

    private void updateSelfLayout(boolean isLogin,long user_id){
        if(isLogin){
//            isself = true;
            getShowListInfo("","10", user_id+"", false);
        }
    }

    @ViewInject(R.id.friends_avatar_iv2)
    private ImageView friends_avatar_iv2;
    @ViewInject(R.id.friends_class_tv)
    private TextView friends_class_tv;
    @ViewInject(R.id.friends_nickname_tv2)
    private TextView friends_nickname_tv2;
    @ViewInject(R.id.friends_win_count_tv)
    private TextView friends_win_count_tv;
    @ViewInject(R.id.friends_win_earnings_tv)
    private TextView friends_win_earnings_tv;

    private ImageOptions imageOptions;
    private void showSelfInfo(FriendsModel model){
        x.image().bind(friends_avatar_iv2,model.getHeadpic(),imageOptions);
        int rank = (int)model.getRankpos();
        friends_class_tv.setText("");
        friends_class_tv.setBackgroundColor(getContext().getResources().getColor(R.color.background_transparent));
        switch (rank){
            case 1:
                friends_class_tv.setBackgroundResource(R.mipmap._jinpai);
                break;
            case 2:
                friends_class_tv.setBackgroundResource(R.mipmap._yinpai);
                break;
            case 3:
                friends_class_tv.setBackgroundResource(R.mipmap._tongpai);
                break;
            default:
                friends_class_tv.setText(String.valueOf(rank));
                break;
        }
        
        friends_nickname_tv2.setText(String.valueOf(model.getNickname()));
        ForegroundColorSpan fc_red = StringUtil.fcSpan(R.color.light_red);
        ForegroundColorSpan fc_blue = StringUtil.fcSpan(R.color.light_blue);
        long count = model.getLuckcount();
        String countStr = x.app().getString(R.string.win_count,count);
        SpannableStringBuilder builder_count = StringUtil.singleSpan(countStr,String.valueOf(count),fc_blue);
        friends_win_count_tv.setText(builder_count);

        long income = model.getIncome();
        String incomeStr = x.app().getString(R.string.win_earnings,income);
        int income_start = incomeStr.indexOf(String.valueOf(income));
        int income_end = incomeStr.length();
        SpannableStringBuilder builder_income = StringUtil.singleSpan(incomeStr,income_start,income_end,fc_red);
        friends_win_earnings_tv.setText(builder_income);
    }

    @ViewInject(R.id.friends_list)
    private PullToRefreshListView listView;

    private ListBaseAdapter<FriendsModel> adapter;

    private List<FriendsModel> datas;

    private void setFriendsList(){
        listView.setAdapter(getFriendsListAdapter());
        listView.setOnItemClickListener(this);
        listView.setMode(PullToRefreshBase.Mode.BOTH);
        listView.setOnRefreshListener(this);
    }

    private ListBaseAdapter<FriendsModel> getFriendsListAdapter(){
        datas = new ArrayList<>();
        adapter = new FriendsListAdapter2(getActivity(), datas);
        return adapter;
    }

//    private boolean isself;
    public void getShowListInfo(String lastpos, String pagesize, String uidx, final boolean orientation){
        RequestParams params = new RequestParams(Constant.getBaseUrl() + "page/ucenter/friends.ashx?pageable=1");

        params.addQueryStringParameter("uidx", uidx);
        params.addQueryStringParameter("pagesize", pagesize);
        params.addQueryStringParameter("lastpos", lastpos);
//        if(isself)
//            params.addQueryStringParameter("isself", "1");

        x.http().get(params, new Callback.CommonCallback<String>() {

            @Override
            public void onSuccess(String result) {
                Map<String, Object> resultMap = ParseData.parseFriendsInfo(result);
//                resultMap.put("isself", isself);
                resultMap.put("pull_up", orientation);
                mHandler.obtainMessage(R.id.FRIENDS, resultMap).sendToTarget();
            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
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


    private boolean isInvite;

    private void init_Share(){
        FaceBookShare.callbackManager = CallbackManager.Factory.create();
        FaceBookShare.messageDialog = new MessageDialog(this);
        FaceBookShare.setInvite(true);
        FaceBookShare.messageDialog.registerCallback(FaceBookShare.callbackManager, FaceBookShare.facebookCallback_messenger);
    }

    private void inviteFriends(){
        String content = "";
        String appUrl = getString(R.string.app_url);
        //String pictureUrl = "http://img.taopic.com/uploads/allimg/140222/240404-14022210562883.jpg";
        String pictureUrl = Constant.getBaseUrl() + "common/image/10BBUY_logo.png";
        FaceBookShare.share_messenger(content,pictureUrl,appUrl);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Bundle bundle = new Bundle();
        bundle.putSerializable("bundle",datas.get(position-1));
        Intent intent = new Intent(getActivity(), SNS_FriendsActivity.class);
        intent.putExtras(bundle);
        startActivity(intent);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(FaceBookLogin.callbackManager != null)
            FaceBookLogin.callbackManager.onActivityResult(requestCode, resultCode, data);
        if(isInvite) {
            FaceBookShare.callbackManager.onActivityResult(requestCode, resultCode, data);
            isInvite = false;
        }
    }

    private RefreshTask refreshTask;
    @Override
    public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {
        String lastid = "";
        refreshTask = new RefreshTask(getActivity(),listView,this);
        refreshTask.setLoadOrientation(lastid,false);
        refreshTask.execute();
    }

    @Override
    public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
        String lastid = String.valueOf(adapter.getLastid());
        refreshTask = new RefreshTask(getActivity(),listView,this);
        refreshTask.setLoadOrientation(lastid,true);
        refreshTask.execute();
    }

}
