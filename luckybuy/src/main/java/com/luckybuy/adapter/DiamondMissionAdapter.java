package com.luckybuy.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.luckybuy.R;
import com.luckybuy.model.DiamondMissionModel;

import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

import java.util.List;

/**
 * Created by zhiPeng.S on 2016/10/17.
 */

public class DiamondMissionAdapter extends ListBaseAdapter<DiamondMissionModel> {

    private LayoutInflater mInflater;
    private Holder holder;
    public DiamondMissionAdapter(Context context, List<DiamondMissionModel> list) {
        super(context, list);
        mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.item_diamond_mission, null);
            holder = new Holder();
            x.view().inject(holder, convertView);
            convertView.setTag(holder);
        }else {
            holder = (Holder) convertView.getTag();
        }

        if(getData()==null || getData().size() == 0){
            return  convertView;
        }

        DiamondMissionModel model = getData().get(position);
        int isComplete = (int)model.getIscomplete();
        switch (isComplete){
            case 0:
                x.image().bind(holder.diamondNum,model.getHeadpic());
                break;
            case 1:
                x.image().bind(holder.diamondNum,model.getHeadpic2());
                break;
        }

        holder.missionTip.setText(model.getTitle());

        return convertView;
    }

    private class Holder{
        @ViewInject(R.id.mission_icon)
        private ImageView diamondNum;
        @ViewInject(R.id.mission_tip)
        private TextView missionTip;
    }

}
