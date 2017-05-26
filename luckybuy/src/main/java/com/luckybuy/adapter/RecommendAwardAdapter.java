package com.luckybuy.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.luckybuy.R;
import com.luckybuy.model.AwardModel;

import org.xutils.common.util.DensityUtil;
import org.xutils.image.ImageOptions;
import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

import java.util.List;

/**
 * Created by zhiPeng.S on 2016/7/4.
 */
public class RecommendAwardAdapter extends ListBaseAdapter<AwardModel> {

    private Holder holder;
    private LayoutInflater inflater;
    protected ImageOptions imageOptions;

    public RecommendAwardAdapter(Context context, List<AwardModel> list) {
        super(context, list);
        inflater = LayoutInflater.from(context);
        imageOptions = new ImageOptions.Builder()
                .setSize(DensityUtil.dip2px(60),DensityUtil.dip2px(60))
                .setImageScaleType(ImageView.ScaleType.CENTER_CROP)
                .setLoadingDrawableId(R.mipmap.commodity_image_120)
                .setFailureDrawableId(R.mipmap.commodity_image_120)
                .build();
    }

    @Override
    public int getCount() {
        return getData().size();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.item_award_cart_recommend, null);
            holder = new Holder();
            x.view().inject(holder, convertView);
            convertView.setTag(holder);
        }else {
            holder = (Holder) convertView.getTag();
        }


        if (getData().size() == 0) {
            return convertView;
        }
        final AwardModel awardModel = getData().get(position);
        x.image().bind(holder.icon,awardModel.getHeadpic(),imageOptions);
        String titleCur = awardModel.getTitle()+ awardModel.getSubtitle();
        holder.title.setText(titleCur);
        double progress_pri = Double.valueOf(awardModel.getSaled()) / Double.valueOf(awardModel.getTotal());
        int progress = (int)(progress_pri*100);
        holder.progress.setProgress(progress);

        return convertView;
    }

    private class Holder{
        @ViewInject(R.id.cart_recommend_iv)
        private ImageView icon;
        @ViewInject(R.id.cart_recommend_name_tv)
        private TextView title;
        @ViewInject(R.id.cart_recommend_pb)
        private ProgressBar progress;
    }
}
