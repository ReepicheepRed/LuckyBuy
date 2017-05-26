package com.assist;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.assist.contract.BaskMaterialContract;
import com.assist.model.BaskMaterialModelImpl;
import com.assist.presenter.BaskMaterialPresenterImpl;

import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.ViewInject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by zhiPeng.S on 2016/11/8.
 */
@ContentView(R.layout.activity_bask_material)
public class BaskMaterialActivity extends BaseActivity implements BaskMaterialContract.View,AdapterView.OnItemClickListener{
    private BaskMaterialContract.Presenter baskMaterialPresenter;

    @ViewInject(R.id.bask_material_lv)
    private ListView listView;
    private List<Long> datas;
    private ArrayAdapter<Long> adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        init();
    }

    private void init(){
        baskMaterialPresenter = new BaskMaterialPresenterImpl(this,this);
        datas = new ArrayList<>();

        listView.setOnItemClickListener(this);
        baskMaterialPresenter.getBaskMaterialInfo();
    }


    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        baskMaterialPresenter.onItemClick( parent,  view,  position,  id);
    }

    @Override
    public void showBaskMaterialList(List<Long> data) {
        datas = data;
        adapter = new ArrayAdapter<>(this,android.R.layout.simple_list_item_1,datas);
        listView.setAdapter(adapter);
    }
}
