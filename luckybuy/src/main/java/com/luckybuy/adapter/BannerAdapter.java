package com.luckybuy.adapter;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;

import java.util.List;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.luckybuy.DetailActivity;
import com.luckybuy.WebActivity;
import com.luckybuy.model.AwardModel;
import com.luckybuy.model.BannerModel;
import com.luckybuy.R;
import com.luckybuy.util.Constant;
import com.luckybuy.util.Utility;

import org.json.JSONException;
import org.json.JSONObject;
import org.xutils.common.Callback;
import org.xutils.common.util.DensityUtil;
import org.xutils.http.RequestParams;
import org.xutils.image.ImageOptions;
import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

public class BannerAdapter extends ListBaseAdapter<BannerModel> {

	private LayoutInflater mInflater;
	private ImageOptions imageOptions;
	private BannerHolder holder;
	private int datasSize;

	public BannerAdapter(Context context, List<BannerModel> list) {
		super(context,list);
		mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		imageOptions = new ImageOptions.Builder()
				//.setSize(DensityUtil.dip2px(120), DensityUtil.dip2px(120))
				//.setRadius(DensityUtil.dip2px(5))
				// 如果ImageView的大小不是定义为wrap_content, 不要crop.
				//.setCrop(true) // 很多时候设置了合适的scaleType也不需要它.
				// 加载中或错误图片的ScaleType
				//.setPlaceholderScaleType(ImageView.ScaleType.MATRIX)
				//.setImageScaleType(ImageView.ScaleType.CENTER_CROP)
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
        datasSize = getData().size();
		if(getData()==null || getData().size() == 0 || datasSize == 0){
			return  convertView;
		}

		final BannerModel model = getData().get(position % datasSize);


		String picUrl = getData().get(position%datasSize).getImg();
		x.image().bind(holder.bannerIv,picUrl,imageOptions);


		convertView.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
                int fdi = model.getFdi();
				switch(fdi){
					case 1009:
                        obtainDetailWebInfo(model.getLink());
                        break;
                    case 1010:
                        obtainDetailInfo(String.valueOf(model.getGoodid()));
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

	private void obtainDetailInfo(final String idx){
		RequestParams params = new RequestParams(Constant.getBaseUrl() + "page/good/detail.ashx");
		params.addQueryStringParameter("idx",idx);
		x.http().get(params, new Callback.CommonCallback<String>() {
			@Override
			public void onSuccess(String result) {
				try {
//					JSONObject jsonObject = new JSONObject(result);
//					long goodid = jsonObject.getLong("goodid");
//					long timesid = jsonObject.getLong("timesid");
//					AwardModel model = new AwardModel();
//					model.setIdx(goodid);
//					model.setTimeid(timesid);
					Gson gson = new GsonBuilder().serializeNulls().create();
					AwardModel model = gson.fromJson(result, new TypeToken<AwardModel>(){}.getType());
					if(model.getTimeid() == 0){
						Utility.toastShow(x.app(),R.string.commodity_pull_out_shelves);
						return;
					}

					model.setIdx(Long.valueOf(idx));
					Bundle bundle = new Bundle();
                    bundle.putSerializable("bundle",model);
					Intent intent = new Intent(getContext(), DetailActivity.class);
                    intent.putExtras(bundle);
					getContext().startActivity(intent);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}

			@Override
			public void onError(Throwable ex, boolean isOnCallback) {

			}

			@Override
			public void onCancelled(CancelledException cex) {

			}

			@Override
			public void onFinished() {

			}
		});
	}

	private void obtainDetailWebInfo(String url){
		Intent intent = new Intent(getContext(), WebActivity.class);
		intent.putExtra(Constant.WEB_H5,Constant.BANNER);
		intent.putExtra("link",url);
		getContext().startActivity(intent);
	}
}
