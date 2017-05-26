package com.luckybuy.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.luckybuy.R;
import com.luckybuy.model.InformationModel;
import com.luckybuy.model.WinRecordModel;
import com.luckybuy.util.Utility;

import org.xutils.image.ImageOptions;
import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

import java.util.List;

/**
 * Created by zhiPeng.S on 2016/10/14.
 */

public class InformationAdapter extends ListBaseAdapter<InformationModel> {

    private LayoutInflater mInflater;
    private Holder holder;
    public InformationAdapter(Context context, List<InformationModel> list) {
        super(context, list);
        mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.item_information, null);
            holder = new Holder();
            x.view().inject(holder, convertView);
            convertView.setTag(holder);
        }else {
            holder = (Holder) convertView.getTag();
        }

        if(getData()==null || getData().size() == 0){
            return  convertView;
        }

        final InformationModel model = getData().get(position);
        String dateStr = Utility.trimDate(model.getUdate());
        String[] dateArr = dateStr.split(" ");
        holder.date.setText(dateArr[0]);
        holder.time.setText(dateArr[1]);
        holder.title.setText(model.getTitle());
        return convertView;
    }

    private class Holder{
        @ViewInject(R.id.info_date_tv)
        private TextView date;
        @ViewInject(R.id.info_time_tv)
        private TextView time;
        @ViewInject(R.id.info_title_tv)
        private TextView title;
    }
}
