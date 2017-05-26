package com.assist;

import android.os.Bundle;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;
import com.assist.adapter.ListBaseAdapter;
import com.assist.adapter.SelectUserAdapter;
import com.assist.contract.SelectUserContract;
import com.assist.model.SelectUserModelImpl;
import com.assist.presenter.SelectUserPresenterImpl;
import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.Event;
import org.xutils.view.annotation.ViewInject;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by zhiPeng.S on 2016/11/3.
 */
@ContentView(R.layout.activity_user_select)
public class SelectUserActivity extends BaseActivity implements SelectUserContract.View{
    private SelectUserContract.Presenter selectUserPresenter;

    @ViewInject(R.id.title_tv)
    private TextView title;

    @ViewInject(R.id.uSelect_total_tv)
    private TextView uSelect_total_tv;
    @ViewInject(R.id.uSelect_unengaged_tv)
    private TextView uSelect_unengaged_tv;

    @ViewInject(R.id.uSelect_big_buyer_tv)
    private TextView uSelect_big_buyer_tv;
    @ViewInject(R.id.uSelect_med_buyer_tv)
    private TextView uSelect_med_buyer_tv;
    @ViewInject(R.id.uSelect_sma_buyer_tv)
    private TextView uSelect_sma_buyer_tv;

    @ViewInject(R.id.uSelect_big_buyerS_tv)
    private TextView uSelect_big_buyerS_tv;
    @ViewInject(R.id.uSelect_med_buyerM_tv)
    private TextView uSelect_med_buyerM_tv;
    @ViewInject(R.id.uSelect_sma_buyerS_tv)
    private TextView uSelect_sma_buyerS_tv;

    @ViewInject(R.id.user_lv)
    private ListView listView;
    private List<SelectUserModelImpl.DetailInfo> datas;
    private ListBaseAdapter<SelectUserModelImpl.DetailInfo> adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        init();
    }

    @Event({
            R.id.back_btn,
            R.id.uSelect_refresh_btn,
            R.id.uSelect_smart_btn
    })
    private void viewClick(View view){

        switch (view.getId()){
            case R.id.back_btn:
            case R.id.uSelect_smart_btn:
                selectUserPresenter.getAvailableUserInfo();
                break;
            case R.id.uSelect_refresh_btn:
                selectUserPresenter.getUserInfo();
                break;

        }
    }

    private void init(){
        selectUserPresenter = new SelectUserPresenterImpl(this,this);
        title.setText(R.string.user_selected);
        datas = new ArrayList<>();
        adapter = new SelectUserAdapter(this,datas);
        listView.setAdapter(adapter);
        selectUserPresenter.getUserInfo();
    }


    @Override
    public void showUserInfo(String[] userStrA) {
        uSelect_total_tv.setText(userStrA[0]);
        uSelect_unengaged_tv.setText(userStrA[1]);
        uSelect_big_buyer_tv.setText(userStrA[2]);
        uSelect_med_buyer_tv.setText(userStrA[3]);
        uSelect_sma_buyer_tv.setText(userStrA[4]);
    }

    @Override
    public void showUserDetailInfo(String[] smaStrA, List<SelectUserModelImpl.DetailInfo> datas) {
        uSelect_big_buyerS_tv.setText(smaStrA[0]);
        uSelect_med_buyerM_tv.setText(smaStrA[1]);
        uSelect_sma_buyerS_tv.setText(smaStrA[2]);
        adapter.setData(datas);
        adapter.notifyDataSetChanged();
    }
}
