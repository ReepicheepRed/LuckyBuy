package com.luckybuy.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.luckybuy.R;
import com.luckybuy.model.UnveilAwardModel;

import org.xutils.common.util.DensityUtil;
import org.xutils.image.ImageOptions;
import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

import java.util.List;

/**
 * Created by zhiPeng.S on 2016/6/1.
 */
public class F_SNS_LuckyAdapter extends ListBaseAdapter<UnveilAwardModel>{

    private Holder holder;
    private LayoutInflater mInflater;
    private ImageOptions imageOptions;
    public F_SNS_LuckyAdapter(Context context, List<UnveilAwardModel> list) {
        super(context, list);
        mInflater = LayoutInflater.from(getContext());
        imageOptions = new ImageOptions.Builder()
                .setSize(DensityUtil.dip2px(120), DensityUtil.dip2px(120))
                .setRadius(DensityUtil.dip2px(5))
                // 如果ImageView的大小不是定义为wrap_content, 不要crop.
                //.setCrop(true) // 很多时候设置了合适的scaleType也不需要它.
                // 加载中或错误图片的ScaleType
                //.setPlaceholderScaleType(ImageView.ScaleType.MATRIX)
                .setImageScaleType(ImageView.ScaleType.CENTER_CROP)
                .setLoadingDrawableId(R.mipmap.ic_launcher)
                .setFailureDrawableId(R.mipmap.ic_launcher)
                .build();
    }

    @Override
    public int getCount() {
        return 10;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.item_friends_sns, null);
            holder = new Holder();
            x.view().inject(holder, convertView);
            convertView.setTag(holder);
        }else {
            holder = (Holder) convertView.getTag();
        }

        if(getData().size() == 0){
            return convertView;
        }

        final UnveilAwardModel unveilModel = getData().get(position);

        x.image().bind(holder.icon,unveilModel.getHeadpic(),imageOptions);

        holder.title.setText(unveilModel.getTitle());
        String issueStr = getContext().getString(R.string.issue);
        issueStr = String.format(issueStr,unveilModel.getTimeid());
        holder.issue.setText(issueStr);

        return convertView;
    }

    private class Holder {
        @ViewInject(R.id.fsns_award_iv)
        public ImageView icon;

        @ViewInject(R.id.fsns_award_title_tv)
        public TextView title;

        @ViewInject(R.id.fsns_award_issue_tv)
        public TextView issue;

        @ViewInject(R.id.fsns_person_count_tv)
        public TextView count;

        @ViewInject(R.id.fsns_check_detail_tv)
        public TextView check;

        @ViewInject(R.id.fsns_demand_tv)
        public TextView demand;

        @ViewInject(R.id.fsns_surplus_tv)
        public TextView surplus;

        @ViewInject(R.id.fsns_winner_tv)
        public TextView winner;

        @ViewInject(R.id.fsns_buycount_tv)
        public TextView number;

        @ViewInject(R.id.fsns_gobuy_btn)
        public Button goBuyBtn;

        @ViewInject(R.id.fsns_buy_btn)
        public Button buyBtn;

        @ViewInject(R.id.fsns_pb)
        public ProgressBar progressBar;

    }
}
