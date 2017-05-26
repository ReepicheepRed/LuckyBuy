package com.luckybuy;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.RotateAnimation;
import android.view.animation.ScaleAnimation;
import android.view.animation.TranslateAnimation;
import android.widget.AdapterView;
import android.widget.FrameLayout;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;

import com.luckybuy.adapter.AwardAdapter;
import com.luckybuy.adapter.AwardAdapter_GV;
import com.luckybuy.adapter.ListBaseAdapter;
import com.luckybuy.model.AwardModel;
import com.luckybuy.model.BulletinModel;
import com.luckybuy.network.ParseData;
import com.luckybuy.util.Constant;
import com.luckybuy.util.Utility;

import org.xutils.common.Callback;
import org.xutils.ex.HttpException;
import org.xutils.http.RequestParams;
import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by zhiPeng.S on 2016/6/2.
 */
@ContentView(R.layout.award_part_frag)
public class Home_Awards_Fragment extends BaseFragment implements AdapterView.OnItemClickListener{


    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initView();
    }

    private void initView(){
        if(!isGridview)
            setShowList();
        else
            setShowList_gv();

        String REQUEST_TYPE = "hot",REQUEST_COUNT = "10",LAST_ID = "0";
        getShowListInfo(REQUEST_TYPE, REQUEST_COUNT, LAST_ID,false);
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
                            List<AwardModel> showlist =
                                    (List<AwardModel>) result.get(Constant.AWARD_LIST);
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
                        } else {
                            String returnContent = (String) result.get(Constant.RETURN_CONTENT);
                            Utility.toastShow(x.app(), returnContent);
                        }
                    break;
            }
        }
    };

    @ViewInject(R.id.award_Lv)
    private ListView listView;
    private ListBaseAdapter<AwardModel> adapter;
    private List<AwardModel> datas;

    public ListBaseAdapter<AwardModel> getAdapter() {
        return adapter;
    }

    private void setShowList(){
        listView.setAdapter(getShowListAdapter());
        listView.setOnItemClickListener(this);
    }

    private ListBaseAdapter<AwardModel> getShowListAdapter(){
        datas = new ArrayList<>();
        adapter = new AwardAdapter(getActivity(), datas);
        //animation for adding cart
        animation_viewGroup = createAnimLayout();
        ((AwardAdapter)adapter).setOnSetHolderClickListener(new AwardAdapter.HolderClickListener(){
            @Override
            public void onHolderClick(Drawable drawable,int[] start_location) {
                doAnim(drawable,start_location);
            }

        });
        return adapter;
    }

    private boolean isGridview = true;

    @ViewInject(R.id.award_part_frag2_ll)
    private LinearLayout award_part_frag2_ll;

    @ViewInject(R.id.award_Gv)
    private GridView gridView;

    private void setShowList_gv(){
        award_part_frag2_ll.setVisibility(View.VISIBLE);
        gridView.setAdapter(getShowListAdapter_gv());
        gridView.setOnItemClickListener(this);
    }

    private ListBaseAdapter<AwardModel> getShowListAdapter_gv(){
        datas = new ArrayList<>();
        adapter = new AwardAdapter_GV(getActivity(), datas);
        //animation for adding cart
        animation_viewGroup = createAnimLayout();
        ((AwardAdapter_GV)adapter).setOnSetHolderClickListener(new AwardAdapter_GV.HolderClickListener(){
            @Override
            public void onHolderClick(Drawable drawable,int[] start_location) {
                doAnim(drawable,start_location);
            }

        });
        return adapter;
    }


    public void getShowListInfo(String type, String pagesize, String lastidx,final boolean orientation){
        RequestParams params = new RequestParams(Constant.getBaseUrl() +"page/good/list.ashx");
        params.addQueryStringParameter("type",type);
        params.addQueryStringParameter("pagesize",pagesize);
        params.addQueryStringParameter("lastidx",lastidx);
        x.http().get(params, new Callback.CommonCallback<String>() {

            @Override
            public void onSuccess(String result) {
                Map<String, Object> resultMap = ParseData.parseAwardInfo(result);
                resultMap.put("pull_up", orientation);
                mHandler.obtainMessage(R.id.AWARD_SUCCESS, resultMap).sendToTarget();
            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
                Toast.makeText(x.app(), R.string.connect_fail, Toast.LENGTH_LONG).show();
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
        bundle.putSerializable("bundle", datas.get(position));
        Intent intent = new Intent(getActivity(), DetailActivity.class);
        intent.putExtras(bundle);
        startActivityForResult(intent,Constant.REQUEST_CODE_DETAIL);
    }

    //动画时间
    private int AnimationDuration = 1000;
    //正在执行的动画数量
    private int number = 0;
    //是否完成清理
    private boolean isClean = false;
    private FrameLayout animation_viewGroup;
    private Handler myHandler = new Handler(){
        public void handleMessage(Message msg){
            switch(msg.what){
                case 0:
                    //用来清除动画后留下的垃圾
                    try{
                        animation_viewGroup.removeAllViews();
                    }catch(Exception e){

                    }

                    isClean = false;

                    break;
                default:
                    break;
            }
        }
    };

    private void doAnim(Drawable drawable, int[] start_location){
        if(!isClean){
            setAnim(drawable,start_location);
        }else{
            try{
                animation_viewGroup.removeAllViews();
                isClean = false;
                setAnim(drawable,start_location);
            }catch(Exception e){
                e.printStackTrace();
            }
            finally{
                isClean = true;
            }
        }
    }
    /**
     * @Description: 创建动画层
     * @param
     * @return void
     * @throws
     */
    private FrameLayout createAnimLayout(){
        ViewGroup rootView = (ViewGroup)getActivity().getWindow().getDecorView();
        FrameLayout animLayout = new FrameLayout(getActivity());
        FrameLayout.LayoutParams lp = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT,FrameLayout.LayoutParams.MATCH_PARENT);
        animLayout.setLayoutParams(lp);
        animLayout.setBackgroundResource(android.R.color.transparent);
        rootView.addView(animLayout);
        return animLayout;

    }

    /**
     * @deprecated 将要执行动画的view 添加到动画层
     * @param vg
     *        动画运行的层 这里是frameLayout
     * @param view
     *        要运行动画的View
     * @param location
     *        动画的起始位置
     * @return
     */
    private View addViewToAnimLayout(ViewGroup vg,View view,int[] location){
        int x = location[0];
        int y = location[1];
        vg.addView(view);
        FrameLayout.LayoutParams lp = new FrameLayout.LayoutParams(
                dip2px(getActivity(),90),dip2px(getActivity(),90));
        lp.leftMargin = x;
        lp.topMargin = y;
        view.setPadding(5, 5, 5, 5);
        view.setLayoutParams(lp);

        return view;
    }
    /**
     * dip，dp转化成px 用来处理不同分辨路的屏幕
     * @param context
     * @param dpValue
     * @return
     */
    private int dip2px(Context context, float dpValue){
        float scale = context.getResources().getDisplayMetrics().density;
        return (int)(dpValue*scale +0.5f);
    }

    /**
     * 动画效果设置
     * @param drawable
     *       将要加入购物车的商品
     * @param start_location
     *        起始位置
     */
    private void setAnim(Drawable drawable,int[] start_location){


        Animation mScaleAnimation = new ScaleAnimation(1.5f,0.0f,1.5f,0.0f,Animation.RELATIVE_TO_SELF,0.1f,Animation.RELATIVE_TO_SELF,0.1f);
        mScaleAnimation.setDuration(AnimationDuration);
        mScaleAnimation.setFillAfter(true);


        final ImageView iview = new ImageView(getActivity());
        iview.setImageDrawable(drawable);
        final View view = addViewToAnimLayout(animation_viewGroup,iview,start_location);
        view.setAlpha(0.6f);

        int[] end_location = new int[2];
        //cart location
        TabLayout.Tab tab = ((MainActivity)getActivity()).getTabLayout().getTabAt(3);
        if (tab == null) return;
        View cartView = tab.getCustomView();
        if (cartView == null) return;
        cartView.getLocationInWindow(end_location);
        int endX = end_location[0]-start_location[0];
        int endY = end_location[1]-start_location[1];

        Animation mTranslateAnimation = new TranslateAnimation(0,endX,0,endY);
        Animation mRotateAnimation = new RotateAnimation(0, 180, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        mRotateAnimation.setDuration(AnimationDuration);
        mTranslateAnimation.setDuration(AnimationDuration);
        AnimationSet mAnimationSet = new AnimationSet(true);

        mAnimationSet.setFillAfter(true);
        mAnimationSet.addAnimation(mRotateAnimation);
        mAnimationSet.addAnimation(mScaleAnimation);
        mAnimationSet.addAnimation(mTranslateAnimation);

        mAnimationSet.setAnimationListener(new Animation.AnimationListener(){

            @Override
            public void onAnimationStart(Animation animation) {
                // TODO Auto-generated method stub
                number++;
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                // TODO Auto-generated method stub

                number--;
                if(number==0){
                    isClean = true;
                    myHandler.sendEmptyMessage(0);
                }

            }

            @Override
            public void onAnimationRepeat(Animation animation) {
                // TODO Auto-generated method stub

            }

        });
        view.startAnimation(mAnimationSet);

    }
    /**
     * 内存过低时及时处理动画产生的未处理冗余
     */
    @Override
    public void onLowMemory() {
        // TODO Auto-generated method stub
        isClean = true;
        try{
            animation_viewGroup.removeAllViews();
        }catch(Exception e){
            e.printStackTrace();
        }
        isClean = false;
        super.onLowMemory();
    }

}
