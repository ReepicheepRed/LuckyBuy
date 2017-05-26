package com.luckybuy.adapter;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.SpannableStringBuilder;
import android.text.style.ForegroundColorSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.luckybuy.DetailActivity;
import com.luckybuy.R;
import com.luckybuy.model.AwardModel;
import com.luckybuy.model.BulletinModel;
import com.luckybuy.layout.BulletinView;
import com.luckybuy.util.StringUtil;

import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2016/3/20.
 * 公告栏数据适配器
 *
 */

public class BulletinAdapter extends ListBaseAdapter<BulletinModel>{
    private List<BulletinModel> mDatas;
    public BulletinAdapter(Context context, List<BulletinModel> mDatas) {
        super(context, mDatas);
        this.mDatas = mDatas;
    }

    /**
     * 获取数据的条数
     * @return
     */
    public int getCount() {
        return mDatas == null ? 0 : mDatas.size();
    }

    /**
     * 获取摸个数据
     * @param position
     * @return
     */
    public BulletinModel getItem(int position) {
        return mDatas.get(position);
    }

    /**
     * 条目数据适配
     * @param view
     * @param data
     */
    public void setItem(final View view, final BulletinModel data) {
        TextView title_tv = (TextView) view.findViewById(R.id.bulletin_title);
        String name = data.getNickname();
        String title = data.getTitle();
        String win_info = getContext().getString(R.string.win_bulletin,name,title);
        List<String> str_l = new ArrayList<>();
        str_l.add(name);
        str_l.add(title);
        ForegroundColorSpan fcSpan_3 = StringUtil.fcSpan(R.color.light_blue);
        ForegroundColorSpan fcSpan_r = StringUtil.fcSpan(R.color.light_red);
        List<Object> obj_l = new ArrayList<>();
        obj_l.add(fcSpan_3);
        obj_l.add(fcSpan_r);
        SpannableStringBuilder builder = StringUtil.mmStrSpan(win_info,str_l,obj_l);
        title_tv.setText(builder);
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AwardModel awardModel = new AwardModel();
                awardModel.setIdx(data.getGoodid());
                awardModel.setTimeid(data.getTimesid());
                Bundle bundle = new Bundle();
                bundle.putSerializable("bundle",awardModel);
                Intent intent = new Intent(getContext(), DetailActivity.class);
                intent.putExtras(bundle);
                getContext().startActivity(intent);
            }
        });
    }

    /**
     * 获取条目布局
     * @param parent
     * @return
     */
    /*public View getView(BulletinView parent) {
        return LayoutInflater.from(parent.getContext()).inflate(R.layout.bulletin_item, null);
    }*/

    @Override
    public View getView(final int position, View convertView, ViewGroup parent){
        return LayoutInflater.from(parent.getContext()).inflate(R.layout.bulletin_item, null);
    }

}
