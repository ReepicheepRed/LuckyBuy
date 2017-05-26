package com.luckybuy.adapter;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.SpannableStringBuilder;
import android.text.style.ForegroundColorSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import com.luckybuy.BaskEvaluateActivity;
import com.luckybuy.BaskMineActivity;
import com.luckybuy.ImagePreviewActivity;
import com.luckybuy.R;
import com.luckybuy.model.BaskSNSModel;
import com.luckybuy.model.DiscoverModel;
import com.luckybuy.util.Constant;
import com.luckybuy.util.StringUtil;
import com.luckybuy.util.Utility;

import org.xutils.common.util.DensityUtil;
import org.xutils.image.ImageOptions;
import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

import java.util.List;

public class BaskMineAdapter extends ListBaseAdapter<BaskSNSModel> {

	private LayoutInflater mInflater;
	private ImageOptions imageOptions;
    private ImageOptions imageOptions_avatar;
	private Holder holder;

	public BaskMineAdapter(Context context, List<BaskSNSModel> list) {
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
        imageOptions_avatar = new ImageOptions.Builder()
                .setSize(DensityUtil.dip2px(50), DensityUtil.dip2px(50))
                .setRadius(DensityUtil.dip2px(25))
                // 如果ImageView的大小不是定义为wrap_content, 不要crop.
                .setCrop(true) // 很多时候设置了合适的scaleType也不需要它.
                // 加载中或错误图片的ScaleType
                //.setPlaceholderScaleType(ImageView.ScaleType.MATRIX)
                .setImageScaleType(ImageView.ScaleType.CENTER_CROP)
                .setLoadingDrawableId(R.mipmap.faxian_gerenzhongxin)
                .setFailureDrawableId(R.mipmap.faxian_gerenzhongxin)
                .build();
	}


	@Override
	public Object getItem(int position) {
		return position;
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		if (convertView == null) {
			convertView = mInflater.inflate(R.layout.item_bask_mine, null);
			holder = new Holder();
			x.view().inject(holder, convertView);
			convertView.setTag(holder);
		}else {
			holder = (Holder) convertView.getTag();
		}

		if(getCount() == 0){
			return  convertView;
		}

		final BaskSNSModel model = getData().get(position);
		if(position == getCount()-1){
			lastId = model.getPos();
		}

        long sidx = model.getSidx();

        if(sidx == 0){
            holder.bask_soon_rl.setVisibility(View.VISIBLE);
            holder.bask_general_ll.setVisibility(View.GONE);

            x.image().bind(holder.icon,model.getGoodheadpic(),imageOptions);

            holder.title.setText(model.getTitle());

            String issueStr = getContext().getString(R.string.issue);
            issueStr = String.format(issueStr,model.getTimesid());
            holder.issue.setText(issueStr);

            String luckyidStr = getContext().getString(R.string.lucky_number);
            luckyidStr = String.format(luckyidStr,model.getLuckid());
			ForegroundColorSpan fcSpan_red = StringUtil.fcSpan(R.color.light_red);
            long luckyNum = model.getLuckid();
            SpannableStringBuilder builder_luckyId = StringUtil.singleSpan(luckyidStr,String.valueOf(luckyNum),fcSpan_red);
            holder.luckyid.setText(builder_luckyId);

            View.OnClickListener onClickListener = new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    switch (v.getId()){
                        case R.id.bask_add_list_btn:
                            Bundle bundle = new Bundle();
                            bundle.putSerializable("bundle", getData().get(position));
                            Intent intent = new Intent();
                            intent.putExtras(bundle);
                            intent.setClass(getContext(),BaskEvaluateActivity.class);
							((BaskMineActivity)getContext()).startActivityForResult(intent, Constant.REQUEST_CODE);
                            break;
                    }
                }
            };
            holder.button.setOnClickListener(onClickListener);
        }else {
            holder.bask_soon_rl.setVisibility(View.GONE);
            holder.bask_general_ll.setVisibility(View.VISIBLE);

            x.image().bind(holder.icon_avatar,model.getHeadpic(),imageOptions_avatar);

            holder.name.setText(model.getNickname());

            //String timeStr = getContext().getResources().getString(R.string.bask_time);
			String timeStr = Utility.trimDate(model.getUdate());
            holder.time.setText(timeStr);

			String titleStr = model.getTitle() + " " + model.getSubtitle();
			holder.title_d.setText(titleStr);

			String issueStr = getContext().getString(R.string.issue,model.getTimesid());
			holder.issue_d.setText(issueStr);

            holder.content.setText(model.getIntro());

            BaskImageAdapter adapter = new BaskImageAdapter(getContext(),model.getImgUrl());
            holder.gridView.setAdapter(adapter);
			AdapterView.OnItemClickListener  onItemClickListener = new AdapterView.OnItemClickListener() {
				@Override
				public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
					List<DiscoverModel.BaskImage>  images = model.getImgUrl();
					String[] urls = new String[images.size()];
					for (int i = 0; i <images.size(); i++) {
						urls[i] = images.get(i).getImg();
					}
					imageBrower(position,urls);
				}
			};
			holder.gridView.setOnItemClickListener(onItemClickListener);
        }
		return convertView;
	}

	private void imageBrower(int position, String[] urls) {
		Intent intent = new Intent(getContext(), ImagePreviewActivity.class);
		// 图片url,为了演示这里使用常量，一般从数据库中或网络中获取
		intent.putExtra(ImagePreviewActivity.EXTRA_IMAGE_URLS, urls);
		intent.putExtra(ImagePreviewActivity.EXTRA_IMAGE_INDEX, position);
		getContext().startActivity(intent);
	}

	private class Holder {
		@ViewInject(R.id.bask_award_iv)
		private ImageView icon;

		@ViewInject(R.id.bask_award_name_tv)
		private TextView title;

		@ViewInject(R.id.bask_issue)
		private TextView issue;

		@ViewInject(R.id.bask_lucky_number)
		private TextView luckyid;

		@ViewInject(R.id.bask_add_list_btn)
		private Button button;

        @ViewInject(R.id.bask_soon_rl)
        private RelativeLayout bask_soon_rl;

        @ViewInject(R.id.bask_general_ll)
        private LinearLayout bask_general_ll;

		@ViewInject(R.id.discover_avatar_iv)
		private ImageView icon_avatar;

		@ViewInject(R.id.discover_user_name)
		private TextView name;

		@ViewInject(R.id.discover_time_tv)
		private TextView time;

		@ViewInject(R.id.discover_award_name_tv)
		private TextView title_d;

		@ViewInject(R.id.discover_award_issue_tv)
		private TextView issue_d;

		@ViewInject(R.id.discover_content_tv)
		private TextView content;

		@ViewInject(R.id.discover_picture_gv)
		private GridView gridView;
	}
}
