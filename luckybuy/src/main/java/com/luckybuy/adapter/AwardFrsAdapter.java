package com.luckybuy.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.luckybuy.DetailActivity;
import com.luckybuy.MainActivity;
import com.luckybuy.R;
import com.luckybuy.SNS_FriendsActivity;
import com.luckybuy.db.DB_Config;
import com.luckybuy.layout.HorizontalListView;
import com.luckybuy.model.AwardFrsModel;
import com.luckybuy.model.AwardModel;
import com.luckybuy.model.FriendsModel;
import com.luckybuy.model.UserModel;
import com.luckybuy.util.Constant;
import com.luckybuy.util.Utility;

import org.apache.commons.collections.Bag;
import org.xutils.DbManager;
import org.xutils.common.util.DensityUtil;
import org.xutils.common.util.KeyValue;
import org.xutils.db.sqlite.WhereBuilder;
import org.xutils.ex.DbException;
import org.xutils.image.ImageOptions;
import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

import java.util.Iterator;
import java.util.List;

/**
 * Created by zhiPeng.S on 2016/6/1.
 */
public class AwardFrsAdapter extends ListBaseAdapter<AwardFrsModel>{

    protected LayoutInflater mInflater;
    protected ImageOptions imageOptions;
    protected Holder holder;
    protected DbManager db;
    private HolderClickListener mHolderClickListener;
    public AwardFrsAdapter(Context context, List<AwardFrsModel> list) {
        super(context, list);
        db = x.getDb(DB_Config.getDaoConfig());
        mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        imageOptions = new ImageOptions.Builder()
                .setSize(DensityUtil.dip2px(120), DensityUtil.dip2px(120))
                .setRadius(DensityUtil.dip2px(5))
                .setCrop(true)
                .setImageScaleType(ImageView.ScaleType.CENTER_CROP)
                .setLoadingDrawableId(R.mipmap.award_default)
                .setFailureDrawableId(R.mipmap.award_default)
                .build();
    }

    @Override
    public int getCount() {
        return getData().size();
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.friends_item, null);
            holder = new Holder();
            x.view().inject(holder, convertView);
            convertView.setTag(holder);
        }else {
            holder = (Holder) convertView.getTag();
        }

        if(getData().size() == 0){
            return convertView;
        }

         final AwardFrsModel awardModel = getData().get(position);
        if (position == getData().size()-1) {
             lastId = awardModel.getIdx();
        }

        x.image().bind(holder.awardIv,awardModel.getHeadpic(),imageOptions);

        String titleCur = awardModel.getTitle()+ awardModel.getSubtitle();
        holder.awardTitle.setText(titleCur);

        String totalStr = holder.awardTotal.getText().toString();
        String totalCur = totalStr.substring(0,3) + awardModel.getTotal();
        holder.awardTotal.setText(totalCur);

        String surplusStr = holder.awardSurplus.getText().toString();
        long surplus = awardModel.getTotal() - awardModel.getSaled();
        String surplusCur = surplusStr.substring(0,3) + surplus;
        holder.awardSurplus.setText(surplusCur);
        double progress_pri = Double.valueOf(awardModel.getSaled()) / Double.valueOf(awardModel.getTotal());
        int progress = (int)(progress_pri*100);
        holder.currentPb.setProgress(progress);

        holder.addListBtn.setOnClickListener(new View.OnClickListener() {
            ImageView imageView = holder.awardIv;
            @Override
            public void onClick(View v) {
                AwardModel model = new AwardModel();
                model.setIdx(awardModel.getIdx());
                model.setTitle(awardModel.getTitle());
                model.setSubtitle(awardModel.getSubtitle());
                model.setHeadpic(awardModel.getHeadpic());
                model.setTotal(awardModel.getTotal());
                model.setSaled(awardModel.getSaled());
                model.setTimeid(awardModel.getTimeid());
                model.setCopies(awardModel.getCopies());
                try {
                    AwardModel model_db = db.selector(AwardModel.class).where("timesid", "=", awardModel.getTimeid()).findFirst();
                    Log.e("model_db", model_db + "");
                    if (model_db == null) {
                        model.setCopies(1);
                        db.save(model);
                    } else {
                        long count = model_db.getCopies();
                        count++;
                        db.update(AwardModel.class, WhereBuilder.b("timesid", "=", model.getTimeid()),new KeyValue("copies", count));
                    }

                    List<AwardModel> data = db.findAll(AwardModel.class);
                    int count = data.size();
                    Activity activity = (Activity)getContext();
                    if(activity instanceof MainActivity){
                        ((MainActivity)getContext()).getBadgeView().setText(String.valueOf(count));
                    }
                } catch (DbException e) {
                    e.printStackTrace();
                }

                //animation for adding cart
                if(mHolderClickListener!=null){
                    int[] start_location = new int[2];
                    imageView.getLocationInWindow(start_location);//获取点击商品图片的位置
                    Drawable drawable = imageView.getDrawable();//复制一个新的商品图标
                    mHolderClickListener.onHolderClick(drawable,start_location);
                }
                Utility.toastShow(getContext(),R.string.add_success);
                AwardFrsAdapter.this.notifyDataSetChanged();
            }
        });

        final List<FriendsModel> friendsList = awardModel.getFriends();
        final HorizontalListViewAdapter adapter = new HorizontalListViewAdapter(getContext(),friendsList);
        holder.friends_hlv.setAdapter(adapter);

        AdapterView.OnItemClickListener onItemClickListener = new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Bundle bundle = new Bundle();
                bundle.putSerializable("bundle",friendsList.get(position));
                Intent intent = new Intent(getContext(), SNS_FriendsActivity.class);
                intent.putExtras(bundle);
                getContext().startActivity(intent);
            }
        };
        holder.friends_hlv.setOnItemClickListener(onItemClickListener);

        if (awardModel.isFold()){
            fold_friends(holder.divider, holder.unfold_btn_ll, holder.unfold_list_ll);
        }else{
            unfold_friends(holder.divider, holder.unfold_btn_ll, holder.unfold_list_ll);
        }

        View.OnClickListener onClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch(v.getId()){
                    case R.id.unfold_btn_ll:
                        awardModel.setFold(false);
                        AwardFrsAdapter.this.notifyDataSetChanged();
                        break;
                    case R.id.fold_btn_ll:
                        awardModel.setFold(true);
                        AwardFrsAdapter.this.notifyDataSetChanged();
                        break;
                    case R.id.award_item_rl:
                        Bundle bundle = new Bundle();
                        bundle.putSerializable("bundle", getData().get(position));
                        Intent intent = new Intent(getContext(), DetailActivity.class);
                        intent.putExtras(bundle);
                        ((MainActivity)getContext()).startActivityForResult(intent, Constant.REQUEST_CODE_DETAIL);
                        break;
                }
            }
        };

        holder.unfold_btn_ll.setOnClickListener(onClickListener);

        holder.fold_btn_ll.setOnClickListener(onClickListener);

        holder.award_item_rl.setOnClickListener(onClickListener);

        return convertView;
    }

    private void unfold_friends(View divider,View unfoldHeader, View unfoldView){
        divider.setVisibility(View.GONE);
        unfoldHeader.setVisibility(View.GONE);
        unfoldView.setVisibility(View.VISIBLE);
        AwardFrsAdapter.this.notifyDataSetChanged();
    }

    private void fold_friends(View divider,View unfoldHeader, View unfoldView){
        divider.setVisibility(View.VISIBLE);
        unfoldHeader.setVisibility(View.VISIBLE);
        unfoldView.setVisibility(View.GONE);
        AwardFrsAdapter.this.notifyDataSetChanged();
    }


    public void setOnSetHolderClickListener(HolderClickListener holderClickListener){
        this.mHolderClickListener = holderClickListener;
    }
    public interface HolderClickListener{
        void onHolderClick(Drawable drawable, int[] start_location);
    }

    protected class Holder {
        @ViewInject(R.id.divider_home)
        private View divider;

        @ViewInject(R.id.unfold_btn_ll)
        private LinearLayout unfold_btn_ll;

        @ViewInject(R.id.fold_btn_ll)
        private LinearLayout fold_btn_ll;

        @ViewInject(R.id.unfold_list_ll)
        private LinearLayout unfold_list_ll;

        @ViewInject(R.id.friends_hlv)
        private HorizontalListView friends_hlv;

        @ViewInject(R.id.home_award_iv)
        private ImageView awardIv;

        @ViewInject(R.id.award_name_tv)
        private TextView awardTitle;

        @ViewInject(R.id.total_tv)
        private TextView awardTotal;

        @ViewInject(R.id.surplus_tv)
        private TextView awardSurplus;

        @ViewInject(R.id.home_add_list_btn)
        private Button addListBtn;

        @ViewInject(R.id.current_pb)
        private ProgressBar currentPb;

        @ViewInject(R.id.award_item_rl)
        private RelativeLayout award_item_rl;
    }
}
