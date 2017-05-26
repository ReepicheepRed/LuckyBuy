package com.luckybuy.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.luckybuy.R;
import com.luckybuy.model.DiscoverModel;

import org.xutils.common.util.DensityUtil;
import org.xutils.image.ImageOptions;
import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

import java.util.List;

/**
 * Created by zhiPeng.S on 2016/7/5.
 */
public class BaskImageAdapter extends ListBaseAdapter<DiscoverModel.BaskImage>{

    private LayoutInflater mInflater;
    private ImageOptions imageOptions;
    private Holder holder;
    public BaskImageAdapter(Context context, List<DiscoverModel.BaskImage> list) {
        super(context, list);
        mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        imageOptions = new ImageOptions.Builder()
                .setSize(DensityUtil.dip2px(84), DensityUtil.dip2px(84))
                .setRadius(DensityUtil.dip2px(3))
                .setImageScaleType(ImageView.ScaleType.CENTER_CROP)
                .setLoadingDrawableId(R.mipmap.shaidan)
                .setFailureDrawableId(R.mipmap.shaidan)
                .build();
    }


    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.item_bask_picture, null);
            holder = new Holder();
            x.view().inject(holder, convertView);
            convertView.setTag(holder);
        }else {
            holder = (Holder) convertView.getTag();
        }

        if(getCount() == 0){
            return  convertView;
        }

        DiscoverModel.BaskImage model = getData().get(position);
        x.image().bind(holder.icon,model.getImg(),imageOptions);

        return convertView;
    }

    private class Holder {
        @ViewInject(R.id.discover_picture_iv)
        private ImageView icon;

    }
}
