package com.luckybuy.adapter;

import android.content.Context;
import android.content.SharedPreferences;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.luckybuy.R;
import com.luckybuy.layout.Dialog_Check_all;
import com.luckybuy.login.LoginUserUtils;
import com.luckybuy.model.BannerModel;
import com.luckybuy.model.ResultItemModel;
import com.luckybuy.util.Constant;

import org.xutils.common.Callback;
import org.xutils.http.RequestParams;
import org.xutils.image.ImageOptions;
import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

import java.util.List;

public class PayResultAdapter extends ListBaseAdapter<ResultItemModel> {

    private SharedPreferences preferences;
	private LayoutInflater mInflater;
	private Holder holder;

	public PayResultAdapter(Context context, List<ResultItemModel> list) {
		super(context,list);
		mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        preferences = LoginUserUtils.getUserSharedPreferences(getContext());
	}


	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		if (convertView == null) {
			convertView = mInflater.inflate(R.layout.item_pay_result, null);
			holder = new Holder();
			x.view().inject(holder, convertView);
			convertView.setTag(holder);
		}else {
			holder = (Holder) convertView.getTag();
		}

		if(getCount() == 0){
			return  convertView;
		}

        final ResultItemModel model = getData().get(position);

        View.OnClickListener onClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (v.getId()){
                    case R.id.result_check_all:
                        String issue = model.getTimesid()+"";
                        showCheckAll(issue);
                        break;
                }
            }
        };

        switch (model.getStatus()){
            case 1:
                String success = getContext().getString(R.string.pay_money);
                holder.status.setText(success);
                int success_c = getContext().getResources().getColor(R.color.text_3_c);
                holder.status.setTextColor(success_c);
                holder.luckyid_rl.setVisibility(View.VISIBLE);
                String luckyidStr = model.getLuckid();
                String[] luckyid = luckyidStr.split(",");
                String checkStr = getContext().getResources().getString(R.string.check_all);
                int num = luckyid.length > 2 ? 3 : luckyid.length == 2 ? 2: 1;
                int blue_c = getContext().getResources().getColor(R.color.light_blue);
                int text_c = getContext().getResources().getColor(R.color.text_3_c);
                switch(num){
                    case 1:
                        holder.check.setText(luckyid[0]);
                        holder.check.setTextColor(text_c);
                        holder.luckyid_1.setVisibility(View.GONE);
                        holder.luckyid_2.setVisibility(View.GONE);
                        break;
                    case 2:
                        holder.check.setText(luckyid[0]);
                        holder.check.setTextColor(text_c);
                        holder.luckyid_1.setText(luckyid[1]);
                        holder.luckyid_2.setVisibility(View.GONE);
                        break;
                    case 3:
                        holder.check.setText(checkStr);
                        holder.check.setTextColor(blue_c);
                        holder.luckyid_1.setText(luckyid[0]);
                        holder.luckyid_2.setText(luckyid[1]);
                        holder.check.setOnClickListener(onClickListener);
                        break;
                }

                break;
            case 0:
                String fail = getContext().getString(R.string.money_will_back);
                holder.status.setText(fail);
                int fail_c = getContext().getResources().getColor(R.color.text_3_c);
                holder.status.setTextColor(fail_c);
                holder.luckyid_rl.setVisibility(View.GONE);
                break;
        }


        double money_dou = model.getMoney()/1.0;
        String moneyStr = getContext().getResources().getString(R.string.pay_money_value,money_dou);
        holder.money.setText(moneyStr);


        holder.title.setText(model.getTitle());

        String countStr = getContext().getResources().getString(R.string.buy_number_value);
        countStr = String.format(countStr,model.getCopies());
        holder.count.setText(countStr);

        String issueStr = model.getTimesid() + "";
        holder.issue.setText(issueStr);


		return convertView;
	}

	private class Holder {
		@ViewInject(R.id.result_status_tv)
		private TextView status;

		@ViewInject(R.id.pay_money_value)
		private TextView money;

		@ViewInject(R.id.result_award_name)
		private TextView title;

		@ViewInject(R.id.result_issue_tv)
		private TextView issue;

		@ViewInject(R.id.buy_number_value)
		private TextView count;

		@ViewInject(R.id.result_check_all)
		private TextView check;

		@ViewInject(R.id.result_luckyid_1)
		private TextView luckyid_1;

		@ViewInject(R.id.result_luckyid_2)
		private TextView luckyid_2;

        @ViewInject(R.id.snatch_number_rl)
        private RelativeLayout  luckyid_rl;
	}

    private void showCheckAll(final String timesid){
        RequestParams params = new RequestParams(Constant.getBaseUrl() + "page/ucenter/playno.ashx");
        long uidx = preferences.getLong(Constant.USER_ID,0);
        if(uidx == 0) return;
        params.addQueryStringParameter("uidx",uidx+"");
        params.addQueryStringParameter("timesid",timesid);

        x.http().get(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                Gson gson = new GsonBuilder().serializeNulls().create();
                Long[] id = gson.fromJson(result, new TypeToken<Long[]>(){}.getType());
                String[] idStr = new String[id.length];
                for (int i = 0; i < id.length; i++) {
                    idStr[i] = id[i].toString();
                }
                showLuckyId(idStr);
            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {

            }

            @Override
            public void onCancelled(CancelledException cex) {

            }

            @Override
            public void onFinished() {

            }
        });
    }

    private void showLuckyId(String[] id ){
        Dialog_Check_all dialog = new Dialog_Check_all(getContext());
        TextView title = dialog.getTitle();
        GridView content = dialog.getContent();

        String titleStr = getContext().getString(R.string.check_all_title);
        String lengthStr = String.valueOf(id.length);
        titleStr = String.format(titleStr,id.length);
        int index = titleStr.indexOf(lengthStr);
        SpannableString spanStr = new SpannableString(titleStr);
        ForegroundColorSpan fcs = new ForegroundColorSpan(getContext().getResources().getColor(R.color.light_red));
        spanStr.setSpan(fcs,index,index+lengthStr.length(), Spanned.SPAN_INCLUSIVE_INCLUSIVE);
        title.setText(spanStr);

        ArrayAdapter<String> adapter = new ArrayAdapter<>(getContext(),R.layout.item_luckyid,id);
        content.setAdapter(adapter);
    }
}
