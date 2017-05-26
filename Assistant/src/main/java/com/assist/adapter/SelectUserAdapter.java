package com.assist.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.assist.R;
import com.assist.model.SelectUserModelImpl;
import com.assist.model.SelectUserModelImpl.DetailInfo;

import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

import java.util.List;

/**
 * Created by zhiPeng.S on 2016/11/3.
 */

public class SelectUserAdapter extends ListBaseAdapter<SelectUserModelImpl.DetailInfo>{

    private Holder holder;
    public SelectUserAdapter(Context context, List<SelectUserModelImpl.DetailInfo> list) {
        super(context, list);
        mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.item_user, null);
            holder = new Holder();
            x.view().inject(holder, convertView);
            convertView.setTag(holder);
        }else {
            holder = (Holder) convertView.getTag();
        }
        SelectUserModelImpl.DetailInfo model = getData().get(position);

        holder.name.setText(model.getNickname());
        holder.id.setText(String.valueOf(model.getUidx()));
        holder.pwd.setText(getContext().getString(R.string.balance,model.getMoney()));
        return convertView;
    }

    private class Holder{
//        @ViewInject(R.id.commodity_iv)
//        private ImageView icon;
        @ViewInject(R.id.user_name_tv)
        private TextView name;
        @ViewInject(R.id.user_id_tv)
        private TextView id;
        @ViewInject(R.id.user_pwd_tv)
        private TextView pwd;
    }
}
