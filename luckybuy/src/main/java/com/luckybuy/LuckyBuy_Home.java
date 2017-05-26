package com.luckybuy;





import android.annotation.TargetApi;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Matrix;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.NestedScrollView;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.Toast;
import android.os.Handler;

import com.facebook.AccessToken;
import com.facebook.AccessTokenTracker;
import com.facebook.CallbackManager;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshScrollView;
import com.luckybuy.adapter.AwardAdapter;
import com.luckybuy.adapter.AwardAdapter_GV;
import com.luckybuy.adapter.AwardFrsAdapter;
import com.luckybuy.adapter.BannerAdapter;
import com.luckybuy.adapter.BulletinAdapter;
import com.luckybuy.adapter.ListBaseAdapter;
import com.luckybuy.layout.BulletinView;
import com.luckybuy.layout.CircleFlowIndicator;
import com.luckybuy.layout.ViewFlow;
import com.luckybuy.layout.ViewFlowForViewPager;
import com.luckybuy.login.LoginUserUtils;
import com.luckybuy.model.AwardModel;
import com.luckybuy.model.BannerModel;
import com.luckybuy.model.BulletinModel;
import com.luckybuy.network.ParseData;
import com.luckybuy.util.BasicConfig;
import com.luckybuy.util.Constant;
import com.luckybuy.util.Utility;


import junit.framework.Test;

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
@ContentView(R.layout.fragment_home)
public class LuckyBuy_Home extends BaseFragment{

    private static final String ARG_SECTION_NUMBER = "section_number";
    private static final String AWARD_KEY = "AWARD_KEY";

    private static final int AWARDS = 0;
    private static final int LATEST = 2;
    private static final int FRIENDS = 1;
    private static final int FRAGMENT_COUNT = LATEST +1;

    private Fragment[] fragments = new Fragment[FRAGMENT_COUNT];
    private boolean isResumed = false;
    private boolean userSkippedLogin = false;
    private AccessTokenTracker accessTokenTracker;
    private CallbackManager callbackManager;
    private SharedPreferences preferences;


    public static LuckyBuy_Home newInstance(int sectionNumber) {
        LuckyBuy_Home fragment = new LuckyBuy_Home();
        Bundle args = new Bundle();
        args.putInt(ARG_SECTION_NUMBER, sectionNumber);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initView();
    }

    private void initView(){
        preferences = LoginUserUtils.getUserSharedPreferences(getActivity());
        setScrollViewListener();

        setBanner();
        getBannerInfo();

        getBulletinInfo();

        initImageViewCursor();
        initFragment();
    }

    @ViewInject(R.id.pull_refresh_scrollview)
    private PullToRefreshScrollView mPullRefreshScrollView;


    private void setScrollViewListener(){
        mPullRefreshScrollView.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener2<ScrollView>() {


            @Override
            public void onPullDownToRefresh(PullToRefreshBase<ScrollView> refreshView) {
                new GetDataTask_Refresh().execute();
            }

            @Override
            public void onPullUpToRefresh(PullToRefreshBase<ScrollView> refreshView) {
                new GetDataTask_Loading().execute();
            }
        });
        mPullRefreshScrollView.setMode(PullToRefreshBase.Mode.BOTH);
//        mPullRefreshScrollView.getLoadingLayoutProxy(false, true).setPullLabel(getString(R.string.pull_to_load));
//        mPullRefreshScrollView.getLoadingLayoutProxy(false, true).setRefreshingLabel(getString(R.string.loading));
//        mPullRefreshScrollView.getLoadingLayoutProxy(false, true).setReleaseLabel(getString(R.string.release_to_load));

        final ScrollView scrollView = mPullRefreshScrollView.getRefreshableView();
        contentView = scrollView.getChildAt(0);
        onBorderListener = new OnBorderListener() {
            @Override
            public void onBottom() {
                new GetDataTask_Loading().execute();
            }

            @Override
            public void onTop() {

            }
        };
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            scrollView.setOnScrollChangeListener(new View.OnScrollChangeListener(){

                @Override
                public void onScrollChange(View v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {
                    doOnBorderListener(scrollView);
                }
            });
            return;
        }

        scrollView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_UP:
                        doOnBorderListener(scrollView);
                        break;
                }
                return false;
            }
        });
    }
    View contentView;
    OnBorderListener onBorderListener;
    private void doOnBorderListener(ScrollView scrollView) {
        if (contentView != null && contentView.getMeasuredHeight() <= scrollView.getScrollY() + scrollView.getHeight()) {
            if (onBorderListener != null) {
                onBorderListener.onBottom();
            }
        } else if (scrollView.getScrollY() == 0) {
            if (onBorderListener != null) {
                onBorderListener.onTop();
            }
        }
    }

    /**
     * OnBorderListener, Called when scroll to top or bottom
     *
     * @author Trinea 2013-5-22
     */
    public interface OnBorderListener {

        /**
         * Called when scroll to bottom
         */
        public void onBottom();

        /**
         * Called when scroll to top
         */
        public void onTop();
    }


   @Event({R.id.friends_btn,R.id.hot_btn,R.id.latest_btn,R.id.nav_search_ib,R.id.nav_mission_iv })
    private void onClickTabs(View v){
       Intent intent = new Intent();
        switch (v.getId()){
            case R.id.friends_btn :
                showFragment(FRIENDS,false);
                setIndicator(1);
                break;
            case R.id.hot_btn :
                showFragment(AWARDS,false);
                setIndicator(0);
                break;
            case R.id.latest_btn :
                showFragment(LATEST,false);
                setIndicator(2);
                break;
            case R.id.nav_search_ib :
                intent.setClass(getActivity(),SearchActivity.class);
                startActivity(intent);
                break;
            case R.id.nav_mission_iv :
                goMisssion();
                break;
        }

    }

    private void goMisssion(){
        long user_id = preferences.getLong(Constant.USER_ID,0);
        if(user_id == 0) {
            ((MainActivity)getActivity()).getViewPager().setCurrentItem(4);
            return;
        }
        Intent intent = new Intent();
        intent.setClass(getActivity(),DiamondMissionActivity.class);
        startActivityForResult(intent,0);
    }


    @ViewInject(R.id.indicator_iv)
    private ImageView cursorIV;
    private int currIndex = 0;
    private int cursorWidth = 0;
    private int delta = 0;
    private void initImageViewCursor() {
        cursorWidth = cursorIV.getLayoutParams().width;
        Matrix matrix = new Matrix();
        matrix.postTranslate(0, 0);
        cursorIV.setImageMatrix(matrix);
    }
    private void setIndicator(int nextIndex){
        delta = cursorWidth;
        Animation animation = new TranslateAnimation(currIndex * delta,
                nextIndex * delta, 0, 0);
        animation.setFillAfter(true);
        animation.setDuration(300);
        cursorIV.startAnimation(animation);

        currIndex = nextIndex;
    }

    Home_Awards_Fragment home_awards_fragment;
    Home_Friends_Fragment home_friends_fragment;
    Home_Awards_Latest_Fragment awards_latest_fragment;
    private void initFragment(){
        try{
        FragmentManager fm = getActivity().getSupportFragmentManager();

        home_awards_fragment = new Home_Awards_Fragment();
        home_friends_fragment = new Home_Friends_Fragment();
        awards_latest_fragment = new Home_Awards_Latest_Fragment();

        fragments[AWARDS] = home_awards_fragment;
        fragments[FRIENDS] = home_friends_fragment;
        fragments[LATEST] = awards_latest_fragment;

        FragmentTransaction transaction = fm.beginTransaction();
        for(int i = 0; i < fragments.length; i++) {
            transaction.add(R.id.content_fl,fragments[i]);
            transaction.hide(fragments[i]);
        }
        transaction.commit();
        showFragment(AWARDS,false);
        }catch (Exception e){
            e.printStackTrace();
        }
    }
    private void showFragment(int fragmentIndex, boolean addToBackStack) {
        FragmentManager fm = getActivity().getSupportFragmentManager();
        FragmentTransaction transaction = fm.beginTransaction();
        for (int i = 0; i < fragments.length; i++) {
            transaction.hide(fragments[i]);
            if (i == fragmentIndex) {
                transaction.show(fragments[i]);
            }
        }
        if (addToBackStack) {
            transaction.addToBackStack(null);
        }
        transaction.commit();
    }

    private void setMuteBanner(){
        String[] temp = new String[]{

                "http://images4.c-ctrip.com/target/hhtravel/055/644/362/964b0a7a0d1646298582306ec515b0ae.jpg",
                "http://pic.qiantucdn.com/58pic/12/81/37/24b58PICHJA.jpg",
                "http://hiphotos.baidu.com/lvpics/pic/item/6a21123338629d911a4cffb5.jpg"
        };
        data.clear();
        for (int i = 0; i <= 2; i++) {

            BannerModel bannnerUrl = new BannerModel();
            bannnerUrl.setImg(temp[i]);
            data.add(bannnerUrl);
        }
        updateBanner(data);

    }

    Handler mHandler = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            @SuppressWarnings("unchecked")
            Map<String, Object> result = (Map<String, Object>) msg.obj;
            switch (msg.what) {
                case R.id.BANNER_SUCCESS:
                        if (!result.isEmpty()) {
                            @SuppressWarnings("unchecked")
                            List<BannerModel> showlist =
                                    (List<BannerModel>) result.get(Constant.BANNER_LIST);
                            if (!showlist.isEmpty()) {
                                data.clear();
                                data = showlist;
                            }
                            updateBanner(data);
                        } else {
                            Utility.toastShow(x.app(), "network error");
                        }
                    break;
                case R.id.BULLETIN_SUCCESS:
                        if (!result.isEmpty()) {
                            @SuppressWarnings("unchecked")
                            List<BulletinModel> showlist =
                                    (List<BulletinModel>) result.get(Constant.BULLETIN_LIST);
                            if (showlist == null) return;

                            //change by zyy 2016/8/31
                            if (!showlist.isEmpty()) {
                                getBulletinAdapter(showlist);
                                setBulletin(bulletinAdapter);
                            }
//                            setBulletin(bulletinAdapter);
                        } else {
                            String returnContent = (String) result.get(Constant.RETURN_CONTENT);
                            Utility.toastShow(x.app(), returnContent);
                        }

                    break;
            }
        }
    };
    /**
     *   
     * @author Reepicheep
     * Created at 2016/5/26 17:42
     */
    @ViewInject(R.id.bannerVf)
    private ViewFlowForViewPager viewFlow;
    @ViewInject(R.id.bannerFi)
    private CircleFlowIndicator indic;
    private void setBanner() {
        viewFlow.setViewPager(((MainActivity)getActivity()).getViewPager());
        viewFlow.setFlowIndicator(indic);
        viewFlow.setAdapter(getBannerAdapter());
        viewFlow.setTimeSpan(4500);
        viewFlow.setSelection(3 * 1000); // 设置初始位置
        viewFlow.startAutoFlowTimer(); // 启动自动播放
    }
    
    /**
     * Update banner after getting the new banner information
     * @author Reepicheep
     * Created at 2016/5/26 17:34
     */
    private void updateBanner(List<BannerModel> urls){
        if (urls != null) {
            viewFlow.setmSideBuffer(urls.size()); // 实际图片张数，
            bannerAdapter.setData(urls);
            //bannerAdapter.notifyDataSetChanged();
            viewFlow.setAdapter(bannerAdapter);
            viewFlow.startAutoFlowTimer();
        }
    }

    private List<BannerModel> data;
    private BannerAdapter bannerAdapter;
    ListBaseAdapter<BannerModel> getBannerAdapter() {
        data = new ArrayList<>();
        bannerAdapter = new BannerAdapter(getActivity(), data);
        return bannerAdapter;
    }


    /**
     * Get banner information from server by internet
     * @author Reepicheep
     * Created at 2016/5/26 17:26
     */
    private void getBannerInfo() {
        RequestParams params = new RequestParams(Constant.getBaseUrl() + "handle/lblist/ad.ashx");
        x.http().post(params, new Callback.CommonCallback<String>() {

            @Override
            public void onSuccess(String result) {
                Map<String, Object> resultMap = ParseData.parseBannerInfo(result);
                mHandler.obtainMessage(R.id.BANNER_SUCCESS, resultMap).sendToTarget();
            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
                //Toast.makeText(x.app(), R.string.connect_fail, Toast.LENGTH_LONG).show();
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

    @ViewInject(R.id.bulletin_bv)
    private BulletinView bulletinView;
    private  List<BulletinModel> datas_bulletin;
    private BulletinAdapter bulletinAdapter;
    private void setBulletin(BulletinAdapter adapter){
        bulletinView.setAdapter(adapter);
        //开启线程滚东
        bulletinView.start();
    }

    BulletinAdapter getBulletinAdapter(List<BulletinModel> data) {
        datas_bulletin = data;
        bulletinAdapter = new BulletinAdapter(getActivity(), datas_bulletin);
        return bulletinAdapter;
    }


    private void getBulletinInfo(){
        RequestParams params = new RequestParams(Constant.getBaseUrl() + "page/good/LatestLucky.ashx");
        x.http().get(params, new Callback.CommonCallback<String>() {

            @Override
            public void onSuccess(String result) {
                Map<String, Object> resultMap = ParseData.parseBulletinInfo(result);
                mHandler.obtainMessage(R.id.BULLETIN_SUCCESS, resultMap).sendToTarget();
            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
                //Toast.makeText(x.app(), ex.getMessage(), Toast.LENGTH_LONG).show();
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


    private class GetDataTask_Refresh extends AsyncTask<Void, Void, String[]> {

        @Override
        protected String[] doInBackground(Void... params) {
            // Simulates a background job.


            FragmentManager fm = getActivity().getSupportFragmentManager();
            List<Fragment> fragments = fm.getFragments();
            switch (currIndex){
                case AWARDS:
                    for (int i = 0; i < fragments.size(); i++) {
                        Log.e("fragment_name", fragments.get(i).getClass().getName());
                        if (fragments.get(i).getClass().getName().equals("com.luckybuy.Home_Awards_Fragment")){
                            Home_Awards_Fragment fragment = (Home_Awards_Fragment)fragments.get(i);
                            fragment.getShowListInfo("hot", "10", "",false);
                        }
                    }
                    break;
                case LATEST:
                    for (int i = 0; i < fragments.size(); i++) {
                        Log.e("fragment_name", fragments.get(i).getClass().getName());
                        if (fragments.get(i).getClass().getName().equals("com.luckybuy.Home_Awards_Latest_Fragment")){
                            Home_Awards_Latest_Fragment fragment = (Home_Awards_Latest_Fragment)fragments.get(i);
                            fragment.getShowListInfo("fresh", "10", "",false);
                        }
                    }
                    break;
                case FRIENDS:
                    long user_id_fb = preferences.getLong(Constant.USER_ID_FB,0);
                    if(user_id_fb != 0) {
                        for (int i = 0; i < fragments.size(); i++) {
                            Log.e("fragment_name", fragments.get(i).getClass().getName());
                            if (fragments.get(i).getClass().getName().equals("com.luckybuy.Home_Friends_Fragment")) {
                                Home_Friends_Fragment fragment = (Home_Friends_Fragment) fragments.get(i);
                                long user_id = preferences.getLong(Constant.USER_ID, 0);
                                if (user_id != 0)
                                    fragment.getShowListInfo(user_id + "", "10", "", false);
                            }
                        }
                    }
                    break;
            }
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
            }
            return null;
        }

        @Override
        protected void onPostExecute(String[] result) {
            // Do some stuff here

            // Call onRefreshComplete when the list has been refreshed.
            mPullRefreshScrollView.onRefreshComplete();

            super.onPostExecute(result);
        }
    }

    private class GetDataTask_Loading extends AsyncTask<Void, Void, String[]> {

        @Override
        protected String[] doInBackground(Void... params) {
            // Simulates a background job.


            FragmentManager fm = getActivity().getSupportFragmentManager();
            List<Fragment> fragments = fm.getFragments();
            switch (currIndex){
                case AWARDS:
                    for (int i = 0; i < fragments.size(); i++) {
                        Log.e("fragment_name", fragments.get(i).getClass().getName());
                        if (fragments.get(i).getClass().getName().equals("com.luckybuy.Home_Awards_Fragment")){
                            Home_Awards_Fragment fragment = (Home_Awards_Fragment)fragments.get(i);
                            String lastId = (fragment.getAdapter()).getLastid() +"";
                            fragment.getShowListInfo("hot", "10", lastId,true);
                        }
                    }
                    break;
                case LATEST:
                    for (int i = 0; i < fragments.size(); i++) {
                        Log.e("fragment_name", fragments.get(i).getClass().getName());
                        if (fragments.get(i).getClass().getName().equals("com.luckybuy.Home_Awards_Latest_Fragment")){
                            Home_Awards_Latest_Fragment fragment = (Home_Awards_Latest_Fragment)fragments.get(i);
                            String lastId = (fragment.getAdapter()).getLastid() +"";
                            fragment.getShowListInfo("fresh", "10", lastId,true);
                        }
                    }
                    break;
                case FRIENDS:
                    long user_id_fb = preferences.getLong(Constant.USER_ID_FB,0);
                    if(user_id_fb != 0) {
                        for (int i = 0; i < fragments.size(); i++) {
                            Log.e("fragment_name", fragments.get(i).getClass().getName());
                            if (fragments.get(i).getClass().getName().equals("com.luckybuy.Home_Friends_Fragment")) {
                                Home_Friends_Fragment fragment = (Home_Friends_Fragment) fragments.get(i);
                                String lastId = (fragment.getAdapter()).getLastid() + "";
                                long user_id = preferences.getLong(Constant.USER_ID, 0);
                                if (user_id != 0)
                                    fragment.getShowListInfo(user_id + "", "10", lastId, true);
                            }
                        }
                    }
                    break;
            }
            try {

                Thread.sleep(1000);
            } catch (InterruptedException e) {
            }
            return null;
        }

        @Override
        protected void onPostExecute(String[] result) {
            // Do some stuff here

            // Call onRefreshComplete when the list has been refreshed.
            mPullRefreshScrollView.onRefreshComplete();

            super.onPostExecute(result);
        }
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (resultCode){
            case 4:
                goMisssion();
                break;
        }
    }
}
