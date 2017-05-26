package com.luckybuy.adapter;

import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.text.SpannableStringBuilder;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.luckybuy.MainActivity;
import com.luckybuy.R;
import com.luckybuy.db.DB_Config;
import com.luckybuy.layout.BadgeView;
import com.luckybuy.model.AwardModel;
import com.luckybuy.util.StringUtil;
import com.luckybuy.util.Utility;

import org.xutils.DbManager;
import org.xutils.common.util.DensityUtil;
import org.xutils.common.util.KeyValue;
import org.xutils.db.sqlite.WhereBuilder;
import org.xutils.image.ImageOptions;
import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

import java.util.List;

/**
 * Created by zhiPeng.S on 2016/6/1.
 */
public class AwardAdapter_GV extends ListBaseAdapter<AwardModel>{

    protected LayoutInflater mInflater;
    protected ImageOptions imageOptions;
    protected Holder holder;
    protected DbManager db;
    private HolderClickListener mHolderClickListener;
    public AwardAdapter_GV(Context context, List<AwardModel> list) {
        super(context, list);
        db = x.getDb(DB_Config.getDaoConfig());


        mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        imageOptions = new ImageOptions.Builder()
                .setSize(DensityUtil.dip2px(128), DensityUtil.dip2px(128))
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
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.item_award_home2, null);
            holder = new Holder();
            x.view().inject(holder, convertView);
            convertView.setTag(holder);
        }else {
            holder = (Holder) convertView.getTag();
        }

        if(getData().size() == 0){
            return convertView;
        }

         final AwardModel awardModel = getData().get(position);
        if (position == getData().size()-1) {
             lastId = awardModel.getIdx();
        }

        x.image().bind(holder.awardIv,awardModel.getHeadpic(),imageOptions);

        String titleCur = awardModel.getTitle()+ awardModel.getSubtitle();
        holder.awardTitle.setText(titleCur);

        String lotteryStr = getContext().getString(R.string.lottery);
        double progress_pri = Double.valueOf(awardModel.getSaled()) / Double.valueOf(awardModel.getTotal());
        int progress = (int)(progress_pri*100);
        String lotteryCur = progress+"%";
        lotteryStr = String.format(lotteryStr,lotteryCur);
        ForegroundColorSpan fc_blue = StringUtil.fcSpan(R.color.light_blue);
        SpannableStringBuilder builder_lot = StringUtil.singleSpan(lotteryStr,lotteryCur,fc_blue);
        holder.awardTotal.setText(builder_lot);
        holder.currentPb.setProgress(progress);


        holder.addListBtn.setOnClickListener(new View.OnClickListener() {
            ImageView imageView = holder.awardIv;
            @Override
            public void onClick(View v) {
                try {
                    //long persize = awardModel.getPersize();
                    AwardModel model_db = db.selector(AwardModel.class).where("timesid", "=", awardModel.getTimeid()).findFirst();
                    Log.e("model_db", model_db + "");
                    if (model_db == null) {
                        awardModel.setCopies(1);
                        db.save(awardModel);
                    } else {
                        long count = model_db.getCopies();
                        count ++;
                        db.update(AwardModel.class, WhereBuilder.b("timesid", "=", awardModel.getTimeid()),new KeyValue("copies", count));
                    }

                    List<AwardModel> data = db.findAll(AwardModel.class);
                    int count = data.size();
                    Activity activity = (Activity)getContext();
                    if(activity instanceof MainActivity){
                        BadgeView badgeView = ((MainActivity)getContext()).getBadgeView();
                        badgeView.setVisibility(View.GONE);
                        if(count > 0){
                            badgeView.setVisibility(View.VISIBLE);
                            badgeView.setText(String.valueOf(count));
                        }
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
                //animation for adding cart
                if(mHolderClickListener!=null){
                    int[] start_location = new int[2];
                    imageView.getLocationInWindow(start_location);//获取点击商品图片的位置
                    Drawable drawable = imageView.getDrawable();//复制一个新的商品图标
                    mHolderClickListener.onHolderClick(drawable,start_location);
                }
                if(isSearch)
                    Utility.toastShow(getContext(),R.string.add_success);
                AwardAdapter_GV.this.notifyDataSetChanged();
            }
        });

        return convertView;
    }

    public void setOnSetHolderClickListener(HolderClickListener holderClickListener){
        this.mHolderClickListener = holderClickListener;
    }
    public interface HolderClickListener{
         void onHolderClick(Drawable drawable, int[] start_location);
    }

    private boolean isSearch;

    public boolean isSearch() {
        return isSearch;
    }

    public void setSearch(boolean search) {
        isSearch = search;
    }

    protected class Holder {
        @ViewInject(R.id.home_award_iv2)
        private ImageView awardIv;

        @ViewInject(R.id.award_name_tv2)
        private TextView awardTitle;

        @ViewInject(R.id.total_tv2)
        private TextView awardTotal;

//        @ViewInject(R.id.surplus_tv)
//        private TextView awardSurplus;

        @ViewInject(R.id.home_add_list_btn2)
        private Button addListBtn;

        @ViewInject(R.id.current_pb2)
        private ProgressBar currentPb;
    }
}
