package com.luckybuy;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.luckybuy.adapter.InformationAdapter;
import com.luckybuy.adapter.ListBaseAdapter;
import com.luckybuy.model.InformationModel;
import com.luckybuy.presenter.Impl.InformationPresenterImpl;
import com.luckybuy.presenter.InformationPresenter;
import com.luckybuy.util.Constant;
import com.luckybuy.view.InformationView;

import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.Event;
import org.xutils.view.annotation.ViewInject;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by zhiPeng.S on 2016/10/14.
 */

@ContentView(R.layout.activity_information)
public class InformationActivity extends BaseActivity implements InformationView,AdapterView.OnItemClickListener{

    private InformationPresenter informationPresenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initView();
        initDate();
    }

    @Event({R.id.back_iv})
    private void viewClick(View view){
        switch (view.getId()){
            case R.id.back_iv:
                this.finish();
                break;
        }
    }

    private void initView(){
        informationPresenter = new InformationPresenterImpl(this);
        title.setText(R.string.title_information_center);
        listView.setAdapter(getShowListAdapter());
        listView.setOnItemClickListener(this);
    }

    private boolean isContent;
    private void initDate(){
        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        long idx = bundle != null ? bundle.getLong("idx",0) : 0;
        isContent = idx != 0;
        informationPresenter.getShowListInfo(idx);
    }

    @ViewInject(R.id.title_activity)
    private TextView title;

    @ViewInject(R.id.info_detail_ll)
    private LinearLayout info_detail_ll;
    @ViewInject(R.id.info_detail_title)
    private TextView detail_title;
    @ViewInject(R.id.info_detail_title)
    private TextView detail_content;

    @ViewInject(R.id.notification_lv)
    private ListView listView;
    private List<InformationModel> datas;
    private ListBaseAdapter<InformationModel> adapter;

    private ListBaseAdapter<InformationModel> getShowListAdapter(){
        datas = new ArrayList<>();
        adapter = new InformationAdapter(this,datas);
        return adapter;
    }

    @Override
    public void updateData(Map<String, Object> result) {
        if (!result.isEmpty()) {
            @SuppressWarnings("unchecked")
            List<InformationModel> showlist =
                    (List<InformationModel>) result.get(Constant.AWARD_LIST);

            if(isContent){
                info_detail_ll.setVisibility(View.VISIBLE);
                listView.setVisibility(View.GONE);
                InformationModel model = showlist.get(0);
                detail_title.setText(model.getTitle());
                detail_content.setText(model.getContent());
                return;
            }

//            boolean orientation = (boolean) result.get("pull_up");
//            if (!orientation) datas.clear();

            int size = showlist == null ? 0 : showlist.size();
            for (int i = 0; i < size; i++) datas.add(showlist.get(i));

            adapter.setData(datas);
            adapter.notifyDataSetChanged();
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Intent intent = new Intent();
        Bundle bundle = new Bundle();
        InformationModel model = datas.get(position);
        int type = (int)model.getCtype();
        switch (type){
            case 1:
                bundle.putLong("idx",model.getIdx());
                intent.setClass(this,InformationActivity.class);
                intent.putExtras(bundle);
                break;
            case 2:
                bundle.putString("web_url",model.getLink());
                intent.setClass(this,WebActivity.class);
                intent.putExtra(Constant.WEB_H5,Constant.WEB_URL);
                intent.putExtras(bundle);
                break;
        }
        startActivity(intent);

    }
}
