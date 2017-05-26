package com.luckybuy.adapter;

import android.content.Context;
import android.os.CountDownTimer;
import android.text.SpannableStringBuilder;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.luckybuy.LuckyBuy_Unveil;
import com.luckybuy.MainActivity;
import com.luckybuy.R;
import com.luckybuy.model.UnveilAwardModel;
import com.luckybuy.util.Constant;
import com.luckybuy.util.StringUtil;
import com.luckybuy.util.Utility;

import org.apache.commons.collections.Bag;
import org.xutils.common.util.DensityUtil;
import org.xutils.image.ImageOptions;
import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by zhiPeng.S on 2016/6/1.
 */
public class AwardUnveilAdapter extends ListBaseAdapter<UnveilAwardModel>{

    private UnveilAwardHolder holder;
    private LayoutInflater mInflater;
    private ImageOptions imageOptions;
    private LuckyBuy_Unveil fragment;
    public AwardUnveilAdapter(Context context, List<UnveilAwardModel> list, LuckyBuy_Unveil fragment) {
        super(context, list);
        this.fragment = fragment;
        mInflater = LayoutInflater.from(getContext());
        imageOptions = new ImageOptions.Builder()
                .setSize(DensityUtil.dip2px(71), DensityUtil.dip2px(71))
                .setRadius(DensityUtil.dip2px(5))
                // 如果ImageView的大小不是定义为wrap_content, 不要crop.
                .setCrop(true) // 很多时候设置了合适的scaleType也不需要它.
                // 加载中或错误图片的ScaleType
                //.setPlaceholderScaleType(ImageView.ScaleType.MATRIX)
                .setImageScaleType(ImageView.ScaleType.CENTER_CROP)
                .setLoadingDrawableId(R.mipmap.award_default)
                .setFailureDrawableId(R.mipmap.award_default)
                .build();
        timers = new ArrayList<>();

    }

    @Override
    public int getCount() {
        int i = getData().size();
        return i;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            viewSize++;
            convertView = mInflater.inflate(R.layout.item_award_unveil, null);
            holder = new UnveilAwardHolder();
            x.view().inject(holder, convertView);
            convertView.setTag(holder);
        }else {
            holder = (UnveilAwardHolder) convertView.getTag();
        }


        if(getData().size() == 0){
            return convertView;
        }

        final UnveilAwardModel unveilModel = getData().get(position);
        if (position == getData().size()-1) {
            lastId = unveilModel.getIdx();
        }

        x.image().bind(holder.awardIv,unveilModel.getHeadpic(),imageOptions);

        holder.name_tv.setText(unveilModel.getTitle());
        String issueStr = getContext().getString(R.string.issue);
        issueStr = String.format(issueStr,unveilModel.getTimeid());
        holder.issue.setText(issueStr);
        long diff = 0;
        String timeStr_u = "";

        try {
            String dateStr = unveilModel.getLucktime();
            timeStr_u = Utility.trimDate(dateStr);
            Date timeD = Utility.DATE_FORMAT.parse(timeStr_u);

            String curTime = Utility.DATE_FORMAT.format(System.currentTimeMillis());
            Date curTimeD = Utility.DATE_FORMAT.parse(curTime);
            //time zone difference
            //diff = (timeD.getTime() - curTimeD.getTime())/1000;
            diff = unveilModel.getSurplussecond();
        } catch (Exception e) {
            e.printStackTrace();
        }

        int state = diff > 0 ? 1 : unveilModel.getSurplussecond() <= 0 && unveilModel.getLuckuid() == 0 ? 2 : 3;
        switch (state){
            case 1:
                showUnveilInfo(holder,false);
                UnveilTimer(unveilModel,holder,position,diff);
                break;
            case 2:
                showUnveilInfo(holder,false);
                String cd_tip = getContext().getString(R.string.calculating);
                holder.cd_time_tv.setText(cd_tip);
                break;
            case 3:
                showUnveilInfo(holder,true);
                String winnerStr = getContext().getString(R.string.unveil_winner);
                String name = unveilModel.getNickname();
                winnerStr = String.format(winnerStr,name);
                ForegroundColorSpan fcSpan_blue = StringUtil.fcSpan(R.color.light_blue);
                SpannableStringBuilder builder_blue = StringUtil.singleSpan(winnerStr,name,fcSpan_blue);
                holder.winner_tv.setText(builder_blue);

                String personTimeStr = getContext().getString(R.string.unveil_participate_count);
                personTimeStr = String.format(personTimeStr,unveilModel.getCopies());
                holder.count.setText(personTimeStr);

                String luckyNumberStr = getContext().getString(R.string.lucky_number);
                luckyNumberStr = String.format(luckyNumberStr,unveilModel.getLuckuid());
                ForegroundColorSpan fcSpan_red = StringUtil.fcSpan(R.color.light_red);
                SpannableStringBuilder builder_red = StringUtil.singleSpan(luckyNumberStr,String.valueOf(unveilModel.getLuckuid()),fcSpan_red);
                holder.lucky_number.setText(builder_red);

                String timeStr = getContext().getString(R.string.detail_unveil_time);
                timeStr = String.format(timeStr,timeStr_u);
                holder.time.setText(timeStr);
                break;
        }

        return convertView;
    }

    private long lastId;

    public long getLastid() {
        return lastId;
    }

    private class UnveilAwardHolder {
        @ViewInject(R.id.unveil_award_iv)
        private ImageView awardIv;

        @ViewInject(R.id.unveil_award_name_tv)
        private TextView name_tv;

        @ViewInject(R.id.unveil_issue)
        private TextView issue;

        @ViewInject(R.id.cd_time_tv)
        private TextView cd_time_tv;

        @ViewInject(R.id.unveil_winner)
        private TextView winner_tv;

        @ViewInject(R.id.unveil_count)
        private TextView count;

        @ViewInject(R.id.unveil_lucky_number)
        private TextView lucky_number;

        @ViewInject(R.id.unveil_time)
        private TextView time;

        @ViewInject(R.id.unveil_winner_ll)
        private LinearLayout winner_ll;

        @ViewInject(R.id.unveil_cd_ll)
        private LinearLayout cd_ll;
    }

    private void showUnveilInfo(UnveilAwardHolder holder, boolean isUnveil){
        if(isUnveil){
            holder.cd_ll.setVisibility(View.GONE);
            holder.winner_ll.setVisibility(View.VISIBLE);
        }else {
            holder.cd_ll.setVisibility(View.VISIBLE);
            holder.winner_ll.setVisibility(View.GONE);
        }
    }


    /**
     * @param model
     * @param holder
     * @param position
     */
    private void UnveilTimer(UnveilAwardModel model, UnveilAwardHolder holder, int position,long diff){
        try {
            //long diff = 0;
            if(diff > 0) {
                UnveilCountDownTimer timer = null;

                if (timers.size() > position) {
                    timer = timers.get(position);
                }

                if (timer == null) {
                    timer = new UnveilCountDownTimer(diff * 1000, 1);
                    for (UnveilCountDownTimer cdTimer: timers) {
                        if(cdTimer.getCountdown_tv() != null && cdTimer.getCountdown_tv().equals(holder.cd_time_tv))
                            cdTimer.setCountdown_tv(null);
                    }
                    timer.setCountdown_tv(holder.cd_time_tv);
                    timer.start();
                    timers.add(position, timer);
                } else {
                    long mLeftSecond = timer.getLeftSecond();
                    if(mLeftSecond > 0){
                        holder.cd_time_tv.setVisibility(View.VISIBLE);
                        for (UnveilCountDownTimer cdTimer: timers) {
                            if(cdTimer.getCountdown_tv() != null && cdTimer.getCountdown_tv().equals(holder.cd_time_tv))
                                cdTimer.setCountdown_tv(null);
                        }
                        timer.setCountdown_tv(holder.cd_time_tv);
                    }else {
                        timers.remove(position);
                        holder.cd_time_tv.setText("00:00:00");
                    }
                }
            }else {
                if (timers.size() > position) {
                    timers.remove(position);
                }

                if  (timers.size() > position % viewSize)
                    timers.get(position % viewSize).setCountdown_tv(null);

                holder.cd_time_tv.setText("00:00:00");
            }
        }catch (Exception e){
            e.printStackTrace();
        }

    }

    private int viewSize = 0;

    private List<UnveilCountDownTimer> timers = null;

    class UnveilCountDownTimer extends CountDownTimer {

        private long mLeftSecond;
        private TextView countdown_tv;
        public UnveilCountDownTimer(long millisInFuture,long countDownInterval) {
            super(millisInFuture, countDownInterval);
        }

        public long getLeftSecond() {
            return mLeftSecond;
        }

        public void setCountdown_tv(TextView countdown_tv) {
            this.countdown_tv = countdown_tv;
        }

        public TextView getCountdown_tv() {
            return countdown_tv;
        }

        @Override
        public void onTick(long millisUntilFinished) {
            mLeftSecond = millisUntilFinished/1000;
            String minStr = "";
            String secStr = "";
            String mscStr = "";
            long minute = millisUntilFinished / (1000 * 60);
            if (minute >= 10) {
                minStr = minute + "";
            } else {
                minStr = "0" + minute;
            }

            long second = (millisUntilFinished - minute * 1000 * 60) / 1000;
            if (second >= 10) {
                secStr =  second+ "";
            } else {
                secStr = "0" + second;
            }

            long msecond = (millisUntilFinished - (minute* 60 + second)*1000) / 10;
            if ( msecond >= 10) {
                mscStr = msecond + "";
            } else {
                mscStr = "0" + msecond;
            }

            String str =  minStr + ":" + secStr + ":" + mscStr;
            if (countdown_tv == null) {
                return;
            }
            countdown_tv.setText(str);
        }

        @Override
        public void onFinish() {
            this.cancel();
            if (countdown_tv == null) {
                return;
            }
            String cd_tip = getContext().getString(R.string.calculating);
            countdown_tv.setText(cd_tip);
            //countdown_tv.setText("00:00:00");
            /*try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }*/
            fragment.getShowListInfo("0","10",false);
        }
    }
}
