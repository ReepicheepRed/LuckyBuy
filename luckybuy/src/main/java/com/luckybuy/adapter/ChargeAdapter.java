package com.luckybuy.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.luckybuy.R;
import com.luckybuy.model.AwardModel;
import com.luckybuy.model.PayMethodModel;

import org.xutils.common.util.DensityUtil;
import org.xutils.image.ImageOptions;
import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

import java.util.List;

/**
 * Created by zhiPeng.S on 2016/8/9.
 */
public class ChargeAdapter extends ListBaseAdapter<PayMethodModel>{

    private LayoutInflater mInflater;
    private Holder holder;
    private boolean[] isSelect;
    private int COUNT = 20;
    private ImageOptions imageOptions;

    public ChargeAdapter(Context context, List<PayMethodModel> list) {
        super(context, list);
        mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        isSelect = new boolean[COUNT];
        for (int i = 0; i < COUNT; i++) {
            isSelect[i] = false;
            if(i == 0){
                isSelect[i] = true;
            }
        }

        imageOptions = new ImageOptions.Builder()
                .setSize(DensityUtil.dip2px(28), DensityUtil.dip2px(28))
                .setRadius(DensityUtil.dip2px(3))
                .setCrop(true)
                .setImageScaleType(ImageView.ScaleType.CENTER_CROP)
                .setLoadingDrawableId(R.mipmap.paytype_icon)
                .setFailureDrawableId(R.mipmap.paytype_icon)
                .build();
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.item_charge_type2, null);
            holder = new Holder();
            x.view().inject(holder, convertView);
            convertView.setTag(holder);
        }else {
            holder = (Holder) convertView.getTag();
        }

        if (getCount() == 0) {
            return convertView;
        }

        final PayMethodModel model = getData().get(position);

        x.image().bind(holder.icon,model.getHeadpic(),imageOptions);
        String nameStr = model.getTitle();
        holder.name.setText(nameStr);

        x.image().bind(holder.privilege_iv,model.getBodypic());

        if(position == getCount()-1)
            holder.divider.setVisibility(View.GONE);
        else
            holder.divider.setVisibility(View.VISIBLE);

        return convertView;
    }

    public boolean[] getIsSelect() {
        return isSelect;
    }

    private class Holder {
        @ViewInject(R.id.charge_icon)
        private ImageView icon;

        @ViewInject(R.id.charge_type_tv)
        private TextView name;

        @ViewInject(R.id.privilege_iv)
        private ImageView privilege_iv;

        @ViewInject(R.id.charge_divider_v)
        private View divider;
    }
}
