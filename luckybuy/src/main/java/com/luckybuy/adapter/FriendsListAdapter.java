package com.luckybuy.adapter;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextClock;
import android.widget.TextView;

import com.luckybuy.R;
import com.luckybuy.model.BannerModel;
import com.luckybuy.model.FriendsModel;

import org.xutils.common.util.DensityUtil;
import org.xutils.image.ImageOptions;
import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

import java.util.List;

public class FriendsListAdapter extends ListBaseAdapter<FriendsModel> {

	private LayoutInflater mInflater;
	private ImageOptions imageOptions;
	private Holder holder;

	public FriendsListAdapter(Context context, List<FriendsModel> list) {
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
			convertView = mInflater.inflate(R.layout.item_friends_list, null);
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

		x.image().bind(holder.avatar_iv,getData().get(position).getHeadpic(),imageOptions);

		holder.nickname_tv.setText(model.getNickname());

        String diamondStr = getContext().getString(R.string.diamond_count,model.getLuckcoin());
        holder.diamond_tv.setText(diamondStr);

		if (getData().get(position).getIshot() == 1){
			holder.state_tv.setText(R.string.robbing_award);
		}else {
			holder.state_tv.setText("");
		}

		return convertView;
	}

	private class Holder {
		@ViewInject(R.id.friends_avatar_iv)
		public ImageView avatar_iv;

		@ViewInject(R.id.friends_nickname_tv)
		public TextView nickname_tv;

		@ViewInject(R.id.friends_id_tv)
		public TextView id_tv;

		@ViewInject(R.id.friends_diamond_tv)
		public TextView diamond_tv;

		@ViewInject(R.id.friends_state_tv)
		public TextView state_tv;
	}
}
