package com.luckybuy.adapter;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.luckybuy.R;
import com.luckybuy.model.BannerModel;

import org.xutils.image.ImageOptions;
import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

import java.util.List;

public class BannerDetailAdapter extends ListBaseAdapter<BannerModel> {

	private LayoutInflater mInflater;
	private ImageOptions imageOptions;
	private BannerHolder holder;

	public BannerDetailAdapter(Context context, List<BannerModel> list) {
		super(context,list);
		mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		imageOptions = new ImageOptions.Builder()
				//.setSize(DensityUtil.dip2px(120), DensityUtil.dip2px(120))
				//.setRadius(DensityUtil.dip2px(5))
				// 如果ImageView的大小不是定义为wrap_content, 不要crop.
				//.setCrop(true) // 很多时候设置了合适的scaleType也不需要它.
				// 加载中或错误图片的ScaleType
				//.setPlaceholderScaleType(ImageView.ScaleType.MATRIX)
				.setImageScaleType(ImageView.ScaleType.CENTER_CROP)
				.setLoadingDrawableId(R.mipmap.shangpingxiangqing)
				.setFailureDrawableId(R.mipmap.shangpingxiangqing)
				.build();
	}

	@Override
	public int getCount() {
		return Integer.MAX_VALUE;   //设置成最大值来无限循环
	}

	@Override
	public Object getItem(int position) {
		return position;
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		if (convertView == null) {
			convertView = mInflater.inflate(R.layout.banner_image, null);
			holder = new BannerHolder();
			x.view().inject(holder, convertView);
			convertView.setTag(holder);
		}else {
			holder = (BannerHolder) convertView.getTag();
		}

		if(getData()==null || getData().size() == 0){
			return  convertView;
		}

		x.image().bind(holder.bannerIv,getData().get(position%getData().size()).getImg().toString(),imageOptions);

		convertView.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(getContext(),null);
				Bundle bundle = new Bundle();
				intent.putExtras(bundle);
				getContext().startActivity(intent);
				switch(0){
					case 0:
						break;
				}
			}
		});
		return convertView;
	}

	private class BannerHolder {
		@ViewInject(R.id.bannerIv)
		public ImageView bannerIv;
	}
}
