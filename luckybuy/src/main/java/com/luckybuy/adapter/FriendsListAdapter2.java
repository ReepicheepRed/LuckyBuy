package com.luckybuy.adapter;

import android.content.Context;
import android.text.SpannableStringBuilder;
import android.text.style.ForegroundColorSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.luckybuy.R;
import com.luckybuy.model.FriendsModel;
import com.luckybuy.util.StringUtil;

import org.xutils.common.util.DensityUtil;
import org.xutils.image.ImageOptions;
import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

import java.util.List;

public class FriendsListAdapter2 extends ListBaseAdapter<FriendsModel> {

	private LayoutInflater mInflater;
	private ImageOptions imageOptions;
	private Holder holder;

	public FriendsListAdapter2(Context context, List<FriendsModel> list) {
		super(context,list);
		mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		imageOptions = new ImageOptions.Builder()
				.setSize(DensityUtil.dip2px(50), DensityUtil.dip2px(50))
				.setRadius(DensityUtil.dip2px(25))
				// 如果ImageView的大小不是定义为wrap_content, 不要crop.
				.setCrop(true) // 很多时候设置了合适的scaleType也不需要它.
				// 加载中或错误图片的ScaleType
				.setPlaceholderScaleType(ImageView.ScaleType.MATRIX)
				.setImageScaleType(ImageView.ScaleType.CENTER_CROP)
				.setLoadingDrawableId(R.mipmap.haoyouliebiaotouxiang)
				.setFailureDrawableId(R.mipmap.haoyouliebiaotouxiang)
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

	/**
	 * @param position
	 * @param convertView
	 * @param parent
	 * @return
	 */
	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		if (convertView == null) {
			convertView = mInflater.inflate(R.layout.item_friends_list2, null);
			holder = new Holder();
			x.view().inject(holder, convertView);
			convertView.setTag(holder);
		}else {
			holder = (Holder) convertView.getTag();
		}

		if(getData()==null || getData().size() == 0){
			return  convertView;
		}

        FriendsModel model = getData().get(position);

		if(position == getCount()-1){
			lastId = model.getPos();
		}


		x.image().bind(holder.avatar_iv,getData().get(position).getHeadpic(),imageOptions);

		holder.nickname_tv.setText(model.getNickname());

        int rank = (int)model.getRankpos();
        holder.class_tv.setText("");
        holder.class_tv.setBackgroundColor(getContext().getResources().getColor(R.color.background_transparent));
        switch (rank){
            case 1:
                holder.class_tv.setBackgroundResource(R.mipmap._jinpai);
                break;
            case 2:
                holder.class_tv.setBackgroundResource(R.mipmap._yinpai);
                break;
            case 3:
                holder.class_tv.setBackgroundResource(R.mipmap._tongpai);
                break;
            default:
                holder.class_tv.setText(String.valueOf(rank));
                break;
        }


        ForegroundColorSpan fc_red = StringUtil.fcSpan(R.color.light_red);
        ForegroundColorSpan fc_blue = StringUtil.fcSpan(R.color.light_blue);
        long count = model.getLuckcount();
        String countStr = x.app().getString(R.string.win_count,count);
        SpannableStringBuilder builder_count = StringUtil.singleSpan(countStr,String.valueOf(count),fc_blue);
        holder.count_tv.setText(builder_count);

        long income = model.getIncome();
        String incomeStr = x.app().getString(R.string.win_earnings,income);
        int income_start = incomeStr.indexOf(String.valueOf(income));
        int income_end = incomeStr.length();
        SpannableStringBuilder builder_income = StringUtil.singleSpan(incomeStr,income_start,income_end,fc_red);
        holder.earnings_tv.setText(builder_income);
		return convertView;
	}

	private class Holder {
		@ViewInject(R.id.friends_avatar_iv2)
		public ImageView avatar_iv;

		@ViewInject(R.id.friends_nickname_tv2)
		public TextView nickname_tv;

		@ViewInject(R.id.friends_class_tv)
		public TextView class_tv;

		@ViewInject(R.id.friends_win_count_tv)
		public TextView count_tv;

		@ViewInject(R.id.friends_win_earnings_tv)
		public TextView earnings_tv;
	}
}
