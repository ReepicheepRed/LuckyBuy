package com.luckybuy.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import com.luckybuy.R;
import com.luckybuy.model.FriendsModel;
import org.xutils.common.util.DensityUtil;
import org.xutils.image.ImageOptions;
import org.xutils.view.annotation.ViewInject;
import org.xutils.x;
import java.util.List;

public class HorizontalListViewAdapter extends BaseAdapter{
    private int[] mIconIDs;
    private String[] mTitles;
    private Context mContext;
    private LayoutInflater mInflater;
    Bitmap iconBitmap;
    private int selectIndex = -1;

    private List<FriendsModel> mData;
    protected ImageOptions imageOptions;
    private ViewHolder holder;
    public HorizontalListViewAdapter(Context context, String[] titles, int[] ids){
        this.mContext = context;
        this.mIconIDs = ids;
        this.mTitles = titles;
        mInflater=(LayoutInflater)mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);//LayoutInflater.from(mContext);
    }

    public HorizontalListViewAdapter(Context context, List<FriendsModel> list){
        this.mContext = context;
        this.mData = list;
        mInflater=(LayoutInflater)mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);//LayoutInflater.from(mContext);

        imageOptions = new ImageOptions.Builder()
                .setSize(DensityUtil.dip2px(34), DensityUtil.dip2px(34))
                .setRadius(DensityUtil.dip2px(17))
                .setCrop(true)
                .setImageScaleType(ImageView.ScaleType.CENTER_CROP)
                .setLoadingDrawableId(R.mipmap.haoyoudouzaiqiang)
                .setFailureDrawableId(R.mipmap.haoyoudouzaiqiang)
                .build();
    }

    @Override
    public int getCount() {
        return mData.size();
    }
    @Override
    public Object getItem(int position) {
        return position;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if(convertView==null){
            holder = new ViewHolder();
            convertView = mInflater.inflate(R.layout.horizontal_list_item, null);
            x.view().inject(holder, convertView);
            convertView.setTag(holder);
        }else{
            holder = (ViewHolder)convertView.getTag();
        }
        if(position == selectIndex){
            convertView.setSelected(true);
        }else{
            convertView.setSelected(false);
        }

        /*holder.mTitle.setText(mTitles[position]);
        iconBitmap = getPropThumnail(mIconIDs[position]);
        holder.mImage.setImageBitmap(iconBitmap);*/

        if (mData.size() == 0) {
            return convertView;
        }

        final FriendsModel model = mData.get(position);
        x.image().bind(holder.mImage,model.getHeadpic(),imageOptions);

        return convertView;
    }

    private static class ViewHolder {
        @ViewInject(R.id.img_list_item)
        private ImageView mImage;
    }
/*    private Bitmap getPropThumnail(int id){
        Drawable d = mContext.getResources().getDrawable(id);
        Bitmap b = BitmapUtil.drawableToBitmap(d);
//      Bitmap bb = BitmapUtil.getRoundedCornerBitmap(b, 100);
        int w = mContext.getResources().getDimensionPixelOffset(R.dimen.thumnail_default_width);
        int h = mContext.getResources().getDimensionPixelSize(R.dimen.thumnail_default_height);

        Bitmap thumBitmap = ThumbnailUtils.extractThumbnail(b, w, h);

        return thumBitmap;
    }*/
    public void setSelectIndex(int i){
        selectIndex = i;
    }
}
