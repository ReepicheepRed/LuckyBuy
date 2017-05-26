package com.luckybuy.adapter;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.luckybuy.ManagerAddressActivity;
import com.luckybuy.ManagerAddressEditActivity;
import com.luckybuy.R;
import com.luckybuy.login.LoginUserUtils;
import com.luckybuy.model.AddressModel;
import com.luckybuy.util.Constant;

import org.xutils.image.ImageOptions;
import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

import java.util.List;

public class AddressAdapter extends ListBaseAdapter<AddressModel> {


	private LayoutInflater mInflater;
	private Holder holder;
    private SharedPreferences preferences;
    private int language;
    private String chineseAtr = "zh_CN", englishAtr = "en_US", thaiAtr = "th_TH";

	public AddressAdapter(Context context, List<AddressModel> list) {
		super(context,list);
		mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        preferences = LoginUserUtils.getUserSharedPreferences(context);
        String languageStr = preferences.getString(Constant.LANGUAGE,"");
        language = languageStr.equals(thaiAtr) ? 0 : 1;
	}


	@Override
	public Object getItem(int position) {
		return position;
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		if (convertView == null) {
			convertView = mInflater.inflate(R.layout.item_address, null);
			holder = new Holder();
			x.view().inject(holder, convertView);
			convertView.setTag(holder);
		}else {
			holder = (Holder) convertView.getTag();
		}

		if(getCount() == 0){
			return  convertView;
		}

        final AddressModel model = getData().get(position);

        holder.name.setText(model.getFirstname());
        holder.phone.setText(model.getMobile());

        String address = model.getCity() + " " + model.getDistrict() + " " + model.getAddress();
        holder.address.setText(address);

        holder.defaultTag.setOnCheckedChangeListener(null);
        int isdefault = model.isdefault() ? 0 : 1;
        switch (isdefault){
            case 0:
                holder.defaultTag.setChecked(true);
                break;
            case 1:
                holder.defaultTag.setChecked(false);
                break;
        }

        CompoundButton.OnCheckedChangeListener onCheckedChangeListener = new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked)
                    ((ManagerAddressActivity)getContext()).handleAddress(position,1);
            }
        };
        holder.defaultTag.setOnCheckedChangeListener(onCheckedChangeListener);

        View.OnClickListener onClickListener = new OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (v.getId()){
                    case R.id.address_edit_ll:
                        Bundle bundle = new Bundle();
                        bundle.putSerializable("bundle",model);
                        Intent intent = new Intent(getContext(),ManagerAddressEditActivity.class);
                        intent.putExtras(bundle);
                        getContext().startActivity(intent);
                        break;
                    case R.id.address_delete_ll:
                        ((ManagerAddressActivity)getContext()).handleAddress(position,2);
                        break;
                }
            }
        };

        holder.address_edit_ll.setOnClickListener(onClickListener);
        holder.address_delete_ll.setOnClickListener(onClickListener);
		return convertView;
	}

	private class Holder {
		@ViewInject(R.id.address_user_name_tv)
		private TextView name;

		@ViewInject(R.id.phone_number_tv)
		private TextView phone;

		@ViewInject(R.id.user_address_tv)
		private TextView address;

		@ViewInject(R.id.set_default_address_cb)
		private CheckBox defaultTag;

        @ViewInject(R.id.address_delete_ll)
        private LinearLayout address_delete_ll;

        @ViewInject(R.id.address_edit_ll)
        private LinearLayout address_edit_ll;
	}
}
