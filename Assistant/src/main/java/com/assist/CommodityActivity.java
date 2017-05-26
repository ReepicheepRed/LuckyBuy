package com.assist;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;
import com.assist.adapter.CommodityAdapter;
import com.assist.adapter.ListBaseAdapter;
import com.assist.contract.CommodityContract;
import com.assist.model.CommodityModelImpl;
import com.assist.presenter.CommodityPresenterImpl;
import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.Event;
import org.xutils.view.annotation.ViewInject;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by zhiPeng.S on 2016/11/3.
 */
@ContentView(R.layout.activity_commodity)
public class CommodityActivity extends BaseActivity implements CommodityContract.View{
    private CommodityContract.Presenter commodityPresenters;

    @ViewInject(R.id.title_tv)
    private TextView title;

    @ViewInject(R.id.commodity_lv)
    private ListView listView;

    private List<CommodityModelImpl> datas;
    private ListBaseAdapter<CommodityModelImpl> adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        init();
    }

    @Event({
            R.id.back_btn
    })
    private void viewClick(View view){
        Intent intent = new Intent();
        switch (view.getId()){
            case R.id.back_btn:
                intent.putExtra("goodsData", (Serializable) commodityPresenters.getDatas());
                intent.putExtra("goodsCount",commodityPresenters.getSelectedGoodsCount());
                setResult(0,intent);
                finish();
                break;
        }
    }

    private void init(){
        commodityPresenters = new CommodityPresenterImpl(this);
        title.setText(R.string.commodity_selected);
        datas = new ArrayList<>();
        adapter = new CommodityAdapter(this,datas);
        listView.setAdapter(adapter);
        commodityPresenters.getShowList();
    }


    @Override
    public void showCommodityList(List<CommodityModelImpl> datas) {
        adapter.setData(datas);
        adapter.notifyDataSetChanged();
    }
}
