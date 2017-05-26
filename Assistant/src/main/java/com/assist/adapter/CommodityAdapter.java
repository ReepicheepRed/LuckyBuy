package com.assist.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import com.assist.R;
import com.assist.model.CommodityModelImpl;
import org.xutils.view.annotation.ViewInject;
import org.xutils.x;
import java.util.List;

/**
 * Created by zhiPeng.S on 2016/11/3.
 */

public class CommodityAdapter extends ListBaseAdapter<CommodityModelImpl>{

    private Holder holder;
    public CommodityAdapter(Context context, List<CommodityModelImpl> list) {
        super(context, list);
        mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.item_select_commodity, null);
            holder = new Holder();
            x.view().inject(holder, convertView);
            convertView.setTag(holder);
        }else {
            holder = (Holder) convertView.getTag();
        }
        CommodityModelImpl model = getData().get(position);

        x.image().bind(holder.icon,model.getHeadpic());
        holder.name.setText(model.getTitle());
        holder.issue.setText(getContext().getString(R.string.commodity_good_id,model.getGoodid()));
        holder.total.setText(getContext().getString(R.string.commodity_total,model.getPrice()));
        holder.surplus.setText(getContext().getString(R.string.commodity_single_price,model.getPresize()));
        return convertView;
    }

    private class Holder{
        @ViewInject(R.id.commodity_iv)
        private ImageView icon;
        @ViewInject(R.id.c_name_tv)
        private TextView name;
        @ViewInject(R.id.c_issue_tv)
        private TextView issue;
        @ViewInject(R.id.c_total_tv)
        private TextView total;
        @ViewInject(R.id.c_surplus_tv)
        private TextView surplus;
    }
}
