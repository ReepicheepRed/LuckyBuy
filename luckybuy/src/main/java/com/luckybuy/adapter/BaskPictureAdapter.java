package com.luckybuy.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import com.luckybuy.R;
import org.xutils.common.util.DensityUtil;
import org.xutils.image.ImageOptions;
import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

import java.util.List;

/**
 * Created by zhiPeng.S on 2016/7/5.
 */
public class BaskPictureAdapter extends ListBaseAdapter<String>{

    private LayoutInflater mInflater;
    private ImageOptions imageOptions;
    private Holder holder;
    public BaskPictureAdapter(Context context, List<String> list) {
        super(context, list);
        mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        imageOptions = new ImageOptions.Builder()
                .setSize(DensityUtil.dip2px(65), DensityUtil.dip2px(65))
                .setRadius(DensityUtil.dip2px(6))
                .setImageScaleType(ImageView.ScaleType.CENTER_CROP)
                //.setLoadingDrawableId(R.mipmap.commodity_image)
                //.setFailureDrawableId(R.mipmap.commodity_image)
                .build();
    }

    @Override
    public int getCount() {
        return getData().size();
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.item_add_picture, null);
            holder = new Holder();
            x.view().inject(holder, convertView);
            convertView.setTag(holder);
        }else {
            holder = (Holder) convertView.getTag();
        }

        if(getData()==null || getData().size() == 0){
            return  convertView;
        }

        String picPath = getData().get(position);
        x.image().bind(holder.icon,picPath,imageOptions);
        holder.delete.setVisibility(View.VISIBLE);
        if(position == getData().size()-1){
            holder.delete.setVisibility(View.GONE);
        }
        holder.delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getData().remove(position);
                if(getData().size() < 3){
                    getData().set(getData().size()-1,"");
                }
                BaskPictureAdapter.this.notifyDataSetChanged();
            }
        });

        return convertView;
    }

    private class Holder {
        @ViewInject(R.id.bask_picture_iv)
        private ImageView icon;

        @ViewInject(R.id.delete_picture_iv)
        private ImageView delete;
    }
}
