package com.luckybuy.adapter;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.luckybuy.Friends_Discover_Fragment;
import com.luckybuy.ImagePreviewActivity;
import com.luckybuy.LuckyBuy_Friends;
import com.luckybuy.MainActivity;
import com.luckybuy.R;
import com.luckybuy.SNS_FriendsActivity;
import com.luckybuy.login.LoginUserUtils;
import com.luckybuy.model.DiscoverModel;
import com.luckybuy.model.FriendsModel;
import com.luckybuy.share.ShareUtils;
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

import java.util.List;

public class DiscoverListAdapter extends ListBaseAdapter<DiscoverModel> {

	private LayoutInflater mInflater;
	private ImageOptions imageOptions;
	private Holder holder;
	private boolean isDiscover = true;
    private SharedPreferences preferences;
	private Friends_Discover_Fragment fragment;
	public DiscoverListAdapter(Context context, List<DiscoverModel> list) {
		super(context,list);
        preferences = LoginUserUtils.getUserSharedPreferences(context);
		mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		imageOptions = new ImageOptions.Builder()
				.setSize(DensityUtil.dip2px(50), DensityUtil.dip2px(50))
				.setRadius(DensityUtil.dip2px(25))
				// 如果ImageView的大小不是定义为wrap_content, 不要crop.
				.setCrop(true) // 很多时候设置了合适的scaleType也不需要它.
				// 加载中或错误图片的ScaleType
				.setPlaceholderScaleType(ImageView.ScaleType.MATRIX)
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
			convertView = mInflater.inflate(R.layout.item_friends_bask, null);
			holder = new Holder();
			x.view().inject(holder, convertView);
			convertView.setTag(holder);
		}else {
			holder = (Holder) convertView.getTag();
		}

		if(getData()==null || getData().size() == 0){
			return  convertView;
		}

		final DiscoverModel model = getData().get(position);
		if (position == getData().size()-1) {
			lastId = model.getSidx();
		}
		x.image().bind(holder.icon,model.getHeadpic(),imageOptions);

		holder.name.setText(model.getNickname());

		String timeStr = getContext().getResources().getString(R.string.bask_time);
		timeStr = String.format(timeStr, Utility.trimDate(model.getUdate()));
		holder.time.setText(timeStr);

		String titleStr = model.getTitle() + " " + model.getSubtitle();
		holder.title.setText(titleStr);

		String issueStr = getContext().getString(R.string.issue,model.getTimesid());
		holder.issue.setText(issueStr);

		holder.content.setText(model.getIntro());

		BaskImageAdapter adapter = new BaskImageAdapter(getContext(),model.getImages());
		holder.gridView.setAdapter(adapter);
        AdapterView.OnItemClickListener  onItemClickListener = new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                List<DiscoverModel.BaskImage>  images = model.getImages();
                String[] urls = new String[images.size()];
                for (int i = 0; i <images.size(); i++) {
                    urls[i] = images.get(i).getImg();
                }
                imageBrower(position,urls);
            }
        };
        holder.gridView.setOnItemClickListener(onItemClickListener);

		View.OnClickListener onClickListener = new View.OnClickListener(){
			@Override
			public void onClick(View v) {
				switch (v.getId()){
					case R.id.discover_try_ll:
						((MainActivity)getContext()).getViewPager().setCurrentItem(0);
						break;
					case R.id.discover_zan_ll:
                        fragment.clickZan(model);
						break;
					case R.id.discover_avatar_iv:
                        FriendsModel modelF = new FriendsModel();
                        modelF.setUidx(model.getUidx());
                        Bundle bundle = new Bundle();
                        bundle.putSerializable("bundle",modelF);
                        Intent intent = new Intent(getContext(),SNS_FriendsActivity.class);
                        intent.putExtras(bundle);
                        getContext().startActivity(intent);
						break;
				}
			}
		};

		holder.icon.setOnClickListener(onClickListener);

		holder.discover_try_ll.setOnClickListener(onClickListener);

		holder.discover_zan_ll.setOnClickListener(onClickListener);

        if(model.isliked())
		    holder.zan_iv.setSelected(true);
        else
            holder.zan_iv.setSelected(false);

		holder.zan_tv.setText(String.valueOf(model.getLikes()));

		if(!isDiscover)
			holder.communicate_rl.setVisibility(View.GONE);

		return convertView;
	}

	private void imageBrower(int position, String[] urls) {
		Intent intent = new Intent(getContext(), ImagePreviewActivity.class);
		// 图片url,为了演示这里使用常量，一般从数据库中或网络中获取
		intent.putExtra(ImagePreviewActivity.EXTRA_IMAGE_URLS, urls);
		intent.putExtra(ImagePreviewActivity.EXTRA_IMAGE_INDEX, position);
		getContext().startActivity(intent);
	}

	private long lastId;

	public long getLastid() {
		return lastId;
	}

	public void setDiscover(boolean discover) {
		isDiscover = discover;
	}

	public void setFragment(Friends_Discover_Fragment fragment) {
		this.fragment = fragment;
	}

	private class Holder {
		@ViewInject(R.id.discover_avatar_iv)
		private ImageView icon;

		@ViewInject(R.id.discover_user_name)
		private TextView name;

		@ViewInject(R.id.discover_time_tv)
		private TextView time;

		@ViewInject(R.id.discover_award_name_tv)
		private TextView title;

		@ViewInject(R.id.discover_award_issue_tv)
		private TextView issue;

		@ViewInject(R.id.discover_content_tv)
		private TextView content;

		@ViewInject(R.id.discover_picture_gv)
		private GridView gridView;

		@ViewInject(R.id.discover_try_ll)
		private LinearLayout discover_try_ll;

		@ViewInject(R.id.discover_zan_ll)
		private LinearLayout discover_zan_ll;

		@ViewInject(R.id.discover_zan_iv)
		private ImageView zan_iv;

		@ViewInject(R.id.discover_zn_tv)
		private TextView zan_tv;

		@ViewInject(R.id.discover_communicate_rl)
		private RelativeLayout communicate_rl;
	}

}
