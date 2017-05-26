package com.luckybuy;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.luckybuy.layout.CustomAlertDialog;
import com.luckybuy.layout.LoadingDialog;
import com.luckybuy.login.LoginUserUtils;
import com.luckybuy.model.AddressModel;
import com.luckybuy.network.TokenVerify;
import com.luckybuy.util.Constant;
import com.luckybuy.util.Utility;

import org.xutils.common.Callback;
import org.xutils.http.RequestParams;
import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.Event;
import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

/**
 * Created by zhiPeng.S on 2016/6/14.
 */
@ContentView(R.layout.activity_address_add)
public class ManagerAddressEditActivity extends BaseActivity implements View.OnClickListener{

    private SharedPreferences preferences;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        title_tv.setText(R.string.title_address_edit);
        preferences = LoginUserUtils.getUserSharedPreferences(this);
        initAddressInfo();
    }

    @ViewInject(R.id.title_activity)
    private TextView title_tv;

    private String[] street_key;
    private CustomAlertDialog dialog;
    @Event({R.id.back_iv,R.id.finish_btn,R.id.address_province_rl,R.id.address_street_rl})
    private void viewClick(View view){
        Intent intent = new Intent(this,AddressSelectActivity.class);
        Bundle bundle_address = new Bundle();
        switch(view.getId()){
            case R.id.back_iv:
                dialog = new CustomAlertDialog(this,this);
                dialog.setSubTitle(R.string.address_dialog_subtitle);
                dialog.show();
                break;
            case R.id.finish_btn:
                Bundle bundle = obtainAddressInfo();
                commitAddressInfo(bundle);
                break;
            case R.id.address_province_rl:
                bundle_address.putInt("level",0);
                intent.putExtras(bundle_address);
                startActivityForResult(intent,Constant.REQUEST_CODE);
                break;
            case R.id.address_street_rl:
                if(street_key == null || street_key.length != 2) return;
                bundle_address.putInt("level",1);
                bundle_address.putString("th",street_key[0]);
                bundle_address.putString("en",street_key[1]);
                intent.putExtras(bundle_address);
                startActivityForResult(intent,Constant.REQUEST_CODE);
                street_key = null;
                break;
        }
    }

    private Bundle bundle;
    private long addressid;
    private void initAddressInfo(){
        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        if(bundle != null){
            this.bundle = bundle;
            AddressModel model = (AddressModel) bundle.getSerializable("bundle");
            if(model == null) return;
            name.setText(model.getFirstname());
            phone.setText(model.getMobile());
            addressid = model.getAddressidx();
            province.setText(model.getCity());
            street.setText(model.getDistrict());
            detailInfo.setText(model.getAddress());
        }
    }

    @ViewInject(R.id.address_edit_name)
    private EditText name;

    @ViewInject(R.id.address_edit_phone)
    private EditText phone;

    @ViewInject(R.id.address_edit_province_tv)
    private EditText province;

    @ViewInject(R.id.address_edit_street_tv)
    private EditText street;

    @ViewInject(R.id.address_edit_detail_info)
    private EditText detailInfo;

    private Bundle obtainAddressInfo(){
        Bundle bundle = new Bundle();
        long user_id = preferences.getLong(Constant.USER_ID,0);
        if(user_id == 0) return null;
        bundle.putString("uidx",user_id+"");

        bundle.putString("addressidx",addressid+"");

        String firstname = name.getText().toString();
        bundle.putString("firstname",firstname);

        String mobile = phone.getText().toString();
        bundle.putString("mobile",mobile);

        String city = province.getText().toString();
        bundle.putString("city",city);

        String district = street.getText().toString();
        bundle.putString("district",district);

        String address = detailInfo.getText().toString();
        bundle.putString("address",address);
        if(firstname.equals("") || mobile.equals("") || city.equals("")
                || district.equals("") || address.equals(""))
            return null;
        return bundle;
    }


    private LoadingDialog loadingDialog;
    private void commitAddressInfo(final Bundle bundle){
        loadingDialog = new LoadingDialog(this);
        loadingDialog.showDialog();
        RequestParams params = new RequestParams(Constant.getBaseUrl() + "Page/Ucenter/AddressDetail.ashx");
        TokenVerify.addToken(this,params);
        if(bundle == null) {
            loadingDialog.dismiss();
            Utility.toastShow(this,R.string.address_not_complete);
            return;
        }
        params.addBodyParameter("uidx",bundle.getString("uidx"));
        params.addBodyParameter("addressidx",bundle.getString("addressidx"));
        params.addBodyParameter("firstname",bundle.getString("firstname"));
        params.addBodyParameter("mobile",bundle.getString("mobile"));
        params.addBodyParameter("city",bundle.getString("city"));
        params.addBodyParameter("district",bundle.getString("district"));
        params.addBodyParameter("address",bundle.getString("address"));
        x.http().post(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                if(result.toUpperCase().equals("SUCCESS")){
                    TokenVerify.saveCookie(ManagerAddressEditActivity.this);
                    Utility.toastShow(x.app(),R.string.commit_success);
                    Intent intent = new Intent();
                    intent.putExtras(bundle);
                    setResult(Constant.RESULT_CODE_UPDATE,intent);
                    ManagerAddressEditActivity.this.finish();
                }
            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {

            }

            @Override
            public void onCancelled(CancelledException cex) {

            }

            @Override
            public void onFinished() {
                loadingDialog.dismiss();
            }
        });

    }

    @Override
    public void onClick(View v) {
        dialog.dismiss();
        switch (v.getId()){
            case R.id.alertDialog_ok_btn:
                ManagerAddressEditActivity.this.finish();
                break;
            case R.id.alertDialog_cancel_btn:
                break;
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == Constant.RESULT_CODE){
            if(data == null) return;
            int level = data.getIntExtra("level",-1);
            String address = data.getStringExtra("address");
            switch (level){
                case 0:
                    province.setText(address);
                    street_key = address.split("/");
                    street.setText("");
                    break;
                case 1:
                    street.setText(address);
                    break;
            }


        }
    }
}
