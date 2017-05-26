package com.luckybuy.adapter;

import android.content.Context;
import android.text.SpannableStringBuilder;
import android.text.style.ForegroundColorSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.luckybuy.R;
import com.luckybuy.WinRecordActivity;
import com.luckybuy.model.WinRecordModel;
import com.luckybuy.model.WinRecordModel;
import com.luckybuy.util.StringUtil;
import com.luckybuy.util.Utility;

import org.xutils.common.util.DensityUtil;
import org.xutils.image.ImageOptions;
import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

import java.util.List;

public class WinPrizeAdapter extends ListBaseAdapter<WinRecordModel> {

	private LayoutInflater mInflater;
	private ImageOptions imageOptions;
    private Holder holder;
    private boolean isfriends;


	public WinPrizeAdapter(Context context, List<WinRecordModel> list) {
		super(context,list);
		mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        imageOptions = new ImageOptions.Builder()
                .setSize(DensityUtil.dip2px(120), DensityUtil.dip2px(120))
                .setRadius(DensityUtil.dip2px(5))
                // 如果ImageView的大小不是定义为wrap_content, 不要crop.
                .setCrop(true) // 很多时候设置了合适的scaleType也不需要它.
                // 加载中或错误图片的ScaleType
                //.setPlaceholderScaleType(ImageView.ScaleType.MATRIX)
                .setImageScaleType(ImageView.ScaleType.CENTER_CROP)
                .setLoadingDrawableId(R.mipmap.award_default_120)
                .setFailureDrawableId(R.mipmap.award_default_120)
                .build();
	}

	@Override
	public int getCount() {
		return getData().size();
	}

	@Override
	public Object getItem(int position) {
		return position;
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		if (convertView == null) {
			convertView = mInflater.inflate(R.layout.item_win_record, null);
			holder = new Holder();
			x.view().inject(holder, convertView);
			convertView.setTag(holder);
		}else {
			holder = (Holder) convertView.getTag();
		}

		if(getData()==null || getData().size() == 0){
			return  convertView;
		}

        final WinRecordModel model = getData().get(position);
        if (position == getData().size()-1) {
            lastId = model.getTimesid();
        }

        x.image().bind(holder.icon,model.getHeadpic(),imageOptions);
        String title = model.getTitle() + model.getSubtitle();
        holder.title.setText(title);
        String issue = getContext().getResources().getString(R.string.issue);
        issue = String.format(issue,model.getTimesid());
        holder.issue.setText(issue);
        ForegroundColorSpan fcSpan_red = StringUtil.fcSpan(R.color.light_red);
        String count = getContext().getResources().getString(R.string.detail_unveil_part_count);
        count = String.format(count,model.getCopies());
        long copies = model.getCopies();
        SpannableStringBuilder builder_count = StringUtil.singleSpan(count,String.valueOf(copies),fcSpan_red);
        holder.count.setText(builder_count);
        String time = getContext().getResources().getString(R.string.detail_unveil_time);
		time = String.format(time, Utility.trimDate(model.getLuckytime()));
        holder.time.setText(time);
        String demand = getContext().getResources().getString(R.string.total_demand);
		demand = String.format(demand,model.getTotal());
        holder.demand.setText(demand);
        String lucky_id = getContext().getResources().getString(R.string.lucky_number);
        lucky_id = String.format(lucky_id,model.getLuckyid());
        long luckyNum = model.getLuckyid();
        SpannableStringBuilder builder_luckyId = StringUtil.singleSpan(lucky_id,String.valueOf(luckyNum),fcSpan_red);
        holder.lucky_id.setText(builder_luckyId);

        int state = model.isvirtual() ? 3 : model.iscomplete() ? 2 : model.isHasaddress() ?  1 : 0;
        if(isfriends) {
            state = -1;
            holder.share.setVisibility(View.GONE);
        }
        switch (state){
            case 0:
                holder.confirm.setVisibility(View.VISIBLE);
                holder.shipments.setVisibility(View.GONE);
                break;
            case 1:
                holder.confirm.setVisibility(View.GONE);
                holder.shipments.setVisibility(View.VISIBLE);
                String shipments = getContext().getResources().getString(R.string.wait_shipments);
                int text_3_c = getContext().getResources().getColor(R.color.text_3_c);
                holder.shipments.setText(shipments);
                holder.shipments.setTextColor(text_3_c);
                break;
            case 2:
                holder.confirm.setVisibility(View.GONE);
                holder.shipments.setVisibility(View.VISIBLE);
                String deal_success = getContext().getResources().getString(R.string.deal_success);
                int light_red = getContext().getResources().getColor(R.color.light_red);
                holder.shipments.setText(deal_success);
                holder.shipments.setTextColor(light_red);
                break;
            case 3:
                holder.confirm.setVisibility(View.GONE);
                holder.shipments.setVisibility(View.VISIBLE);
                String view_detail = getContext().getResources().getString(R.string.check_detail);
                int light_red2 = getContext().getResources().getColor(R.color.light_red);
                holder.shipments.setText(view_detail);
                holder.shipments.setTextColor(light_red2);
                break;
            default:
                holder.state_rl.setVisibility(View.GONE);
                break;
        }

        View.OnClickListener onClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (v.getId()){
                    case R.id.win_record_share_iv:
                        ((WinRecordActivity)getContext()).popShare(position);
                        break;
                }
            }
        };
        holder.share.setOnClickListener(onClickListener);

		return convertView;
	}

    public void setIsfriends(boolean isfriends) {
        this.isfriends = isfriends;
    }

    private class Holder {
		@ViewInject(R.id.win_record_icon)
		private ImageView icon;

		@ViewInject(R.id.win_record_title)
		private TextView title;

		@ViewInject(R.id.win_record_issue_tv)
		private TextView issue;

		@ViewInject(R.id.win_record_count_tv)
		private TextView count;

		@ViewInject(R.id.win_record_total_demand_tv)
		private TextView demand;

		@ViewInject(R.id.win_record_unveil_time_tv)
		private TextView time;

        @ViewInject(R.id.win_record_lucky_id_tv)
        private TextView lucky_id;

        @ViewInject(R.id.win_confirm_tv)
        private TextView confirm;

        @ViewInject(R.id.win_shipments_tv)
        private TextView shipments;

        @ViewInject(R.id.win_record_state_rl)
        private RelativeLayout state_rl;

        @ViewInject(R.id.win_record_share_iv)
        private ImageView share;
	}
}
