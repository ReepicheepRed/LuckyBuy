package com.luckybuy;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.View;
import com.luckybuy.login.LoginFragment;
import com.luckybuy.login.LoginUserUtils;
import com.luckybuy.util.Constant;
import com.luckybuy.util.Utility;

import org.xutils.view.annotation.ContentView;
import org.xutils.x;

/**
 * Created by zhiPeng.S on 2016/5/16.
 */
@ContentView(R.layout.fragment_mine)
public class LuckyBuy_Mine extends BaseFragment{

    private static final int LOGIN = 0;
    private static final int LOGGED = 1;
    private static final int FRAGMENT_COUNT = LOGGED + 1;

    private SharedPreferences preferences;

    public static LuckyBuy_Mine newInstance(int sectionNumber) {
        LuckyBuy_Mine fragment = new LuckyBuy_Mine();
        Bundle args = new Bundle();
        args.putInt("section_number", sectionNumber);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        preferences = LoginUserUtils.getUserSharedPreferences(getActivity());
        init_fragment();
        updateFragment();
    }



    private Fragment[] fragments = new Fragment[FRAGMENT_COUNT];
    private  void init_fragment(){
        LoginFragment login_fragment =  LoginFragment.newInstance(this);
        Mine_Logged_In_Fragment mine_fragment = Mine_Logged_In_Fragment.newInstance(1);
        fragments[LOGIN] = login_fragment;
        fragments[LOGGED] = mine_fragment;

        FragmentManager fm = getChildFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        for (int i = 0; i < fragments.length; i++) {
            ft.add(R.id.mine_fl,fragments[i]);
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

    public void updateFragment(){
        long user_id = preferences.getLong(Constant.USER_ID,0);
        if (user_id != 0) {
            showFragment(LOGGED,false);
        }else {
            showFragment(LOGIN,false);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        //Utility.toastShow(x.app(),"mine_mine");
    }
}
