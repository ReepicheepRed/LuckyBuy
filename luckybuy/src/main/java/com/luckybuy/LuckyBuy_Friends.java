package com.luckybuy;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
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
import com.luckybuy.login.LoginFragment;
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
public class LuckyBuy_Friends extends BaseFragment {

    public static LuckyBuy_Friends newInstance(int sectionNumber) {
        LuckyBuy_Friends fragment = new LuckyBuy_Friends();
        Bundle args = new Bundle();
        args.putInt("section_number", sectionNumber);
        fragment.setArguments(args);
        return fragment;
    }


    private static final int FRAGMENT_COUNT = 2;
    private Fragment[] fragments = new Fragment[FRAGMENT_COUNT];
    private  void init_fragment(){
        Friends_fragment friends_fragment =  Friends_fragment.newInstance(0);
        Friends_Discover_Fragment discover_fragment = Friends_Discover_Fragment.newInstance(1);
        fragments[0] = friends_fragment;
        fragments[1] = discover_fragment;

        FragmentManager fm = getChildFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        for (int i = 0; i < fragments.length; i++) {
            ft.add(R.id.friends_fl,fragments[i]);
            ft.hide(fragments[i]);
        }
        ft.commit();
    }

    public void showFragment(int fragmentIndex, boolean addToBackStack) {
        FragmentManager fm = getChildFragmentManager();
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
        transaction.commitAllowingStateLoss();
    }


    private SharedPreferences preferences;

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        preferences = LoginUserUtils.getUserSharedPreferences(getActivity());
        long user_id = preferences.getLong(Constant.USER_ID,0);
        init_fragment();
        setFriendsState(true);
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
                showFragment(0,false);
                //setFriendsList();
//                adapter.notifyDataSetChanged();
//                friends_list.setVisibility(View.VISIBLE);
//                discover_list.setVisibility(View.GONE);
//                updateFriends(false);
                break;
            case 1:
                friends_tv.setTextColor(getResources().getColor(R.color.text_3_c));
                fri_sel_iv.setVisibility(View.INVISIBLE);
                discover_tv .setTextColor(getResources().getColor(R.color.light_red));
                dcv_sel_iv.setVisibility(View.VISIBLE);
                showFragment(1,false);
                //setDiscoverList();
//                adapter_dsc.notifyDataSetChanged();
//                friends_list.setVisibility(View.GONE);
//                discover_list.setVisibility(View.VISIBLE);
                //Utility.updateView(discover_list,blank_bask_rl,false);
                break;
        }
    }


}
