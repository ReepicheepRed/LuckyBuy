package com.luckybuy.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.luckybuy.MainActivity;
import com.luckybuy.R;
import com.luckybuy.model.AwardModel;
import com.luckybuy.model.DetailHistoryModel;
import com.luckybuy.util.Utility;

import org.apache.commons.collections.Bag;
import org.xutils.common.util.DensityUtil;
import org.xutils.image.ImageOptions;
import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

import java.util.Iterator;
import java.util.List;

/**
 * Created by zhiPeng.S on 2016/6/1.
 */
public class DetailHistoryAdapter extends ListBaseAdapter<DetailHistoryModel>{

    protected LayoutInflater mInflater;
    protected ImageOptions imageOptions;
    protected Holder holder;
    public DetailHistoryAdapter(Context context, List<DetailHistoryModel> list) {
        super(context, list);
        mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        imageOptions = new ImageOptions.Builder()
                .setSize(DensityUtil.dip2px(32), DensityUtil.dip2px(32))
                .setRadius(DensityUtil.dip2px(16))
                // 如果ImageView的大小不是定义为wrap_content, 不要crop.
                .setCrop(true) // 很多时候设置了合适的scaleType也不需要它.
                // 加载中或错误图片的ScaleType
                //.setPlaceholderScaleType(ImageView.ScaleType.MATRIX)
                .setImageScaleType(ImageView.ScaleType.CENTER_CROP)
                .setLoadingDrawableId(R.mipmap.yonghucanyujilu)
                .setFailureDrawableId(R.mipmap.yonghucanyujilu)
                .build();
    }

    @Override
    public int getCount() {
        return getData().size();
    }

    /**
     * @param position
     * @param convertView
     * @param parent
     * @return
     */
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.item_detail_history, null);
            holder = new Holder();
            x.view().inject(holder, convertView);
            convertView.setTag(holder);
        }else {
            holder = (Holder) convertView.getTag();
        }

        if(getData().size() == 0){
            return convertView;
        }

         final DetailHistoryModel model = getData().get(position);

        x.image().bind(holder.avatar_iv ,model.getHeadpic(),imageOptions);
        holder.title_tv.setText(model.getNickname());
        String dateStr = Utility.trimDate(model.getOrderdate());
        holder.time_tv.setText(dateStr);
        holder.ip_tv.setText(model.getIp());
        String countStr = getContext().getString(R.string.history_userPartCount);
        countStr = String.format(countStr,model.getCopies());
        holder.count_tv.setText(countStr);
        return convertView;
    }

    private class Holder {
        @ViewInject(R.id.his_header_iv)
        public ImageView avatar_iv;

        @ViewInject(R.id.his_name)
        public TextView title_tv;

        @ViewInject(R.id.his_time)
        public TextView time_tv;

        @ViewInject(R.id.his_ip)
        public TextView ip_tv;

        @ViewInject(R.id.his_number)
        public TextView count_tv;
    }
}
