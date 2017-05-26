package com.assist;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.assist.adapter.BaskAdapter;
import com.assist.adapter.ListBaseAdapter;
import com.assist.contract.BaskContract;
import com.assist.model.BaskModelImpl;
import com.assist.presenter.BaskPresenterImpl;

import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.Event;
import org.xutils.view.annotation.ViewInject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by zhiPeng.S on 2016/11/7.
 */
@ContentView(R.layout.fragment_bask)
public class BaskFrgment extends BaseFragment implements BaskContract.View,AdapterView.OnItemClickListener{
    private BaskContract.Presenter baskContractPresenter;

    @ViewInject(R.id.bask_lv)
    private ListView listView;
    private List<BaskModelImpl> datas;
    private ListBaseAdapter<BaskModelImpl> adapter;

    public static BaskFrgment newInstance(Bundle bundle) {
        BaskFrgment fragment = new BaskFrgment();
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        init();
    }

    @Event({
            R.id.bask_not_btn,
            R.id.bask_btn
    })
    private void viewClick(View view){
        switch (view.getId()){
            case R.id.bask_not_btn:
                baskContractPresenter.getWillBaskListInfo(0);
                break;
            case R.id.bask_btn:
                baskContractPresenter.getWillBaskListInfo(1);
                break;
        }
    }

    private void init(){
        baskContractPresenter = new BaskPresenterImpl(getActivity(),this);
        baskContractPresenter.setFragment(this);
        datas = new ArrayList<>();
        adapter = new BaskAdapter(getActivity(),datas);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(this);
        baskContractPresenter.getWillBaskListInfo(0);
    }

    public void showBaskList(List<BaskModelImpl> data, int optype){
        datas.clear();
        datas = data;
        ((BaskAdapter)adapter).setOptype(optype);
        adapter.setData(datas);
        adapter.notifyDataSetChanged();
    }

    @Override
    public List<BaskModelImpl> getData() {
        return datas;
    }

    @Override
    public ListBaseAdapter<BaskModelImpl> getAdapter() {
        return adapter;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        baskContractPresenter.getWillBaskListInfo(1);

    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        baskContractPresenter.onItemClick(parent, view, position,id);
    }
}
