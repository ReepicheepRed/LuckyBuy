package com.luckybuy.adapter;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.luckybuy.DetailActivity;
import com.luckybuy.MainActivity;
import com.luckybuy.R;
import com.luckybuy.layout.Dialog_Check_all;
import com.luckybuy.login.LoginUserUtils;
import com.luckybuy.login.RegisterActivity;
import com.luckybuy.model.AwardModel;
import com.luckybuy.model.SnatchAwardModel;
import com.luckybuy.model.UnveilAwardModel;
import com.luckybuy.util.Constant;
import com.luckybuy.util.StringUtil;
import com.luckybuy.util.Utility;

import org.apache.commons.collections.Bag;
import org.xutils.common.Callback;
import org.xutils.common.util.DensityUtil;
import org.xutils.http.RequestParams;
import org.xutils.image.ImageOptions;
import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

import java.lang.reflect.Type;
import java.text.ParseException;
import java.util.Date;
import java.util.List;

/**
 * Created by zhiPeng.S on 2016/6/1.
 */
public class F_SNS_SnatchAdapter extends ListBaseAdapter<SnatchAwardModel>{


    private Holder holder;
    private LayoutInflater mInflater;
    private ImageOptions imageOptions;
    private SharedPreferences preferences;
    public F_SNS_SnatchAdapter(Context context, List<SnatchAwardModel> list) {
        super(context, list);
        preferences = LoginUserUtils.getUserSharedPreferences(getContext());
        mInflater = LayoutInflater.from(getContext());
        imageOptions = new ImageOptions.Builder()
                .setSize(DensityUtil.dip2px(120), DensityUtil.dip2px(120))
                .setRadius(DensityUtil.dip2px(5))
                // 如果ImageView的大小不是定义为wrap_content, 不要crop.
                .setCrop(true) // 很多时候设置了合适的scaleType也不需要它.
                // 加载中或错误图片的ScaleType
                .setPlaceholderScaleType(ImageView.ScaleType.MATRIX)
                .setImageScaleType(ImageView.ScaleType.CENTER_CROP)
                .setLoadingDrawableId(R.mipmap.award_default_120)
                .setFailureDrawableId(R.mipmap.award_default_120)
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

        final SnatchAwardModel model = getData().get(position);
        int size = getCount();
        if(position == size -1){
            lastId = model.getTimesid();
        }

        x.image().bind(holder.icon,model.getHeadpic(),imageOptions);

        holder.title.setText(model.getTitle());
        String issueStr = getContext().getString(R.string.issue);
        String issueCur = String.format(issueStr,model.getTimesid());
        holder.issue.setText(issueCur);
        String countStr = getContext().getString(R.string.detail_unveil_part_count);
        String countCur = String.format(countStr,model.getCopies());
        ForegroundColorSpan fcSpan_red = StringUtil.fcSpan(R.color.light_red);
        long copies = model.getCopies();
        SpannableStringBuilder builder_count = StringUtil.singleSpan(countCur,String.valueOf(copies),fcSpan_red);
        holder.count.setText(builder_count);

        View.OnClickListener onClickListener = new View.OnClickListener() {
            String timesid = model.getTimesid()+"";
            @Override
            public void onClick(View v) {
                switch (v.getId()){
                    case R.id.fsns_check_detail_tv:
                        //showCheckAll(timesid);
                        break;
                    case R.id.fsns_gobuy_btn:
                    case R.id.fsns_buy_btn:
                        Bundle bundle = new Bundle();
                        AwardModel awardModel = new AwardModel();
                        awardModel.setTitle(model.getTitle());
                        awardModel.setSubtitle(model.getSubtitle());
                        awardModel.setIdx(model.getGoodid());
                        awardModel.setTimeid(model.getTimesid());
                        awardModel.setTotal(model.getTotal());
                        awardModel.setSaled(model.getSaled());
                        awardModel.setHeadpic(model.getHeadpic());
                        awardModel.setPersize(model.getPersize());
                        bundle.putSerializable("bundle", awardModel);
                        Intent intent = new Intent(getContext(), DetailActivity.class);
                        intent.putExtras(bundle);
                        getContext().startActivity(intent);
                        break;
                }
            }
        };

        //holder.check.setOnClickListener(onClickListener);

        long sale = model.getSaled();
        long total = model.getTotal();
        long luckyidx = model.getLuckuidx();
        int state = sale < total ? 0 : (sale == total && luckyidx > 0 ? 1 : 2);
        switch (state){
            case 0:
                holder.going_rl.setVisibility(View.VISIBLE);
                holder.unveiled_rl.setVisibility(View.GONE);
                String demandStr = getContext().getString(R.string.total_demand);
                String demandCur = String.format(demandStr,model.getTotal());
                holder.demand.setText(demandCur);
                String surplusStr = getContext().getString(R.string.surplus);
                long surplusLon = model.getTotal() - model.getSaled();
                String surplusCur = String.format(surplusStr,surplusLon);
                holder.surplus.setText(surplusCur);
                double progress_pri = Double.valueOf(model.getSaled()) / Double.valueOf(model.getTotal());
                int progress = (int)(progress_pri*100);
                holder.progressBar.setProgress(progress);
                holder.lucky_tag.setVisibility(View.GONE);
                if(isSelf)
                    holder.goBuyBtn.setText(R.string.append);
                holder.goBuyBtn.setOnClickListener(onClickListener);
                break;
            case 1:
                holder.going_rl.setVisibility(View.GONE);
                holder.unveiled_rl.setVisibility(View.VISIBLE);
                String winnerStr = getContext().getString(R.string.friends_winner);
                String winnerCur = String.format(winnerStr,model.getNickname());
                ForegroundColorSpan fcSpan_3 = StringUtil.fcSpan(R.color.text_3_c);
                SpannableStringBuilder builder_winner = StringUtil.singleSpan(winnerCur,model.getNickname(),fcSpan_3);
                holder.winner.setText(builder_winner);
                String numberStr = getContext().getString(R.string.count_friends);
                long wcopies = model.getLuckcopies();
                String numberCur = String.format(numberStr,wcopies);
                SpannableStringBuilder builder_number = StringUtil.singleSpan(numberCur,String.valueOf(wcopies),fcSpan_red);
                holder.number.setText(builder_number);
                long userid = preferences.getLong(Constant.USER_ID,0);
                if (userid == model.getLuckuidx())
                    holder.lucky_tag.setVisibility(View.VISIBLE);
                if(isSelf)
                    holder.buyBtn.setText(R.string.again_buy);
                holder.buyBtn.setOnClickListener(onClickListener);
                break;
            case 2:
                holder.going_rl.setVisibility(View.GONE);
                holder.unveiled_rl.setVisibility(View.VISIBLE);
                holder.winner.setTextColor(getContext().getResources().getColor(R.color.light_red));
                holder.winner.setText(R.string.will_unveil);
                holder.number.setVisibility(View.GONE);
                holder.lucky_tag.setVisibility(View.GONE);
                if(isSelf)
                    holder.buyBtn.setText(R.string.again_buy);
                holder.buyBtn.setOnClickListener(onClickListener);
                break;
        }

        return convertView;
    }

    private boolean isSelf;

    public void setSelf(boolean self) {
        isSelf = self;
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
        public TextView goBuyBtn;

        @ViewInject(R.id.fsns_buy_btn)
        public TextView buyBtn;

        @ViewInject(R.id.fsns_pb)
        public ProgressBar progressBar;

        @ViewInject(R.id.fsns_going_rl)
        public RelativeLayout going_rl;

        @ViewInject(R.id.fsns_unveiled_rl)
        public RelativeLayout unveiled_rl;

        @ViewInject(R.id.lucky_tag)
        public ImageView lucky_tag;
    }


}
