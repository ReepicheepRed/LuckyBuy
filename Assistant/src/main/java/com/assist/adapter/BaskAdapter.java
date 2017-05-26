package com.assist.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.assist.BaskMaterialActivity;
import com.assist.R;
import com.assist.model.BaskModelImpl;
import org.xutils.view.annotation.ViewInject;
import org.xutils.x;
import java.util.List;

/**
 * Created by zhiPeng.S on 2016/11/3.
 */

public class BaskAdapter extends ListBaseAdapter<BaskModelImpl>implements View.OnClickListener{

    private Holder holder;
    private int optype;
    public BaskAdapter(Context context, List<BaskModelImpl> list) {
        super(context, list);
        mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.item_bask, null);
            holder = new Holder();
            x.view().inject(holder, convertView);
            convertView.setTag(holder);
        }else {
            holder = (Holder) convertView.getTag();
        }
        BaskModelImpl model = getData().get(position);

        x.image().bind(holder.icon,model.getHeadpic());
        holder.name.setText(model.getTitle());
        holder.issue.setText(getContext().getString(R.string.issue,model.getTimeid()));
        holder.count.setText(getContext().getString(R.string.buy_count,model.getBuycopies()));
        holder.userName.setText(getContext().getString(R.string.win_user,model.getNickname()));
        holder.userId.setText(getContext().getString(R.string.user_id,model.getUidx()));
        switch (optype){
            case 0:
                holder.stateBtn.setText(R.string.immediate_bask);
                holder.stateBtn.setClickable(false);
                break;
            case 1:
                holder.stateBtn.setText(R.string.waiting_check);
                holder.stateBtn.setClickable(false);
                break;
        }
        return convertView;
    }

    public void setOptype(int optype) {
        this.optype = optype;
    }

    public int getOptype() {
        return optype;
    }

    @Override
    public void onClick(View v) {

    }

    private class Holder{
        @ViewInject(R.id.bask_iv)
        private ImageView icon;
        @ViewInject(R.id.bask_title)
        private TextView name;
        @ViewInject(R.id.bask_issue)
        private TextView issue;
        @ViewInject(R.id.bask_count)
        private TextView count;
        @ViewInject(R.id.bask_win_user)
        private TextView userName;
        @ViewInject(R.id.bask_user_id)
        private TextView userId;
        @ViewInject(R.id.bask_btn)
        private Button stateBtn;
    }
}
