package com.luckybuy;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.luckybuy.login.LoginUserUtils;
import com.luckybuy.model.AddressModel;
import com.luckybuy.model.BulletinModel;
import com.luckybuy.model.CardModel;
import com.luckybuy.model.WinInfoModel;
import com.luckybuy.model.WinRecordModel;
import com.luckybuy.network.TokenVerify;
import com.luckybuy.util.Constant;
import com.luckybuy.util.StringUtil;
import com.luckybuy.util.Utility;

import org.json.JSONException;
import org.json.JSONObject;
import org.xutils.common.Callback;
import org.xutils.common.util.DensityUtil;
import org.xutils.http.RequestParams;
import org.xutils.image.ImageOptions;
import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.Event;
import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

import java.util.List;

/**
 * Created by zhiPeng.S on 2016/6/14.
 */
@ContentView(R.layout.activity_confirm_information)
public class ConfirmInfoActivity extends BaseActivity{

    private SharedPreferences preferences;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        title_tv.setText(R.string.title_confirm_info);
        preferences = LoginUserUtils.getUserSharedPreferences(this);
        initInfo();

    }

    @ViewInject(R.id.title_activity)
    private TextView title_tv;


    private AddressModel aModel;
    private WinRecordModel wModel;
    @Event({R.id.back_iv,R.id.confirm_user_other_tv,R.id.confirm_info_address_tip_rl,
            R.id.confirm_ok_tv,R.id.confirm_ok_tv_3,R.id.card_not_use_rl,R.id.card_use_btn})
    private void viewClick(View view){
        Intent intent = new Intent();
        long user_id = preferences.getLong(Constant.USER_ID,0);
        switch(view.getId()){
            case R.id.back_iv:
                setResult(Constant.RESULT_CODE_UPDATE);
                this.finish();
                break;
            case R.id.confirm_user_other_tv:
                intent.setClass(this,ManagerAddressActivity.class);
                startActivityForResult(intent,Constant.REQUEST_CODE);
                break;
            case R.id.confirm_info_address_tip_rl:
                intent.setClass(this,ManagerAddressEditActivity.class);
                startActivityForResult(intent,Constant.REQUEST_CODE);
                break;
            case R.id.confirm_ok_tv:
                if(aModel != null && wModel != null){
                    confirmAddress(user_id+"", aModel.getAddressidx()+"", wModel.getTimesid()+"");
                }
                break;
            case R.id.confirm_ok_tv_3:
                if(wModel != null){
                    confirmReceipt(user_id + "", wModel.getTimesid() + "");
                }
                break;
            case R.id.confirm_ok_tv_4:
                break;
            case R.id.card_not_use_rl:
            case R.id.card_use_btn:
                card_not_use_rl.setVisibility(View.GONE);
                break;

        }
    }




    private String lucktime = "";
    private void initInfo(){
        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        WinRecordModel model = (WinRecordModel) bundle.getSerializable("bundle");
        if(model == null) return;
        wModel = model;
        lucktime = Utility.trimDate(wModel.getLuckytime());
        initView(model.isvirtual());
    }

    @ViewInject(R.id.card_virtual_rl)
    private RelativeLayout card_virtual_rl;

    @ViewInject(R.id.confirm_info_real_ll)
    private LinearLayout confirm_info_real_ll;

    private void initView(boolean isvirtual){
        final int real = 1, virtual = 2;
        long user_id = preferences.getLong(Constant.USER_ID,0);
        long issue = wModel.getTimesid();
        int type = !isvirtual ? 1 : 2;
        switch (type){
            case real:
                card_virtual_rl.setVisibility(View.GONE);
                confirm_info_real_ll.setVisibility(View.VISIBLE);

                showAwardInfo(wModel);
                if(user_id != 0)
                    showWinInfo(issue + "",user_id+"");
                break;
            case virtual:
                card_virtual_rl.setVisibility(View.VISIBLE);
                confirm_info_real_ll.setVisibility(View.GONE);
                if(user_id != 0)
                    showCardInfo(issue + "",user_id+"");
                break;
        }

    }

    @ViewInject(R.id.card_not_use_rl)
    private RelativeLayout card_not_use_rl;

    @ViewInject(R.id.card_virtual_iv)
    private ImageView card_virtual_iv;

    @ViewInject(R.id.card_number_tv)
    private TextView card_number_tv;

    @ViewInject(R.id.card_pwd_tv)
    private TextView card_pwd_tv;


    private void selectCardType(CardModel model){
        String cardType[] = {"12call","happy","truemoney"};
        int[] cardPic = {R.mipmap._ais,R.mipmap._dta,R.mipmap._true};
        String cardSn = model.getCardsn();
        String cardPsw = model.getCardpsw();
        String cardNum = getString(R.string.card_number,cardSn);
        String cardPwd = getString(R.string.card_pwd,cardPsw);
        ForegroundColorSpan fcs = new ForegroundColorSpan(Color.BLACK);

        int start,end;
        start = cardNum.indexOf(cardSn);
        end = start + cardSn.length();
        SpannableStringBuilder builderNum = new SpannableStringBuilder(cardNum);
        builderNum.setSpan(fcs,start,end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        int start_pwd,end_pwd;
        start_pwd = cardPwd.indexOf(cardPsw);
        end_pwd = start_pwd + cardPsw.length();
        SpannableStringBuilder builderPwd = new SpannableStringBuilder(cardPwd);
        builderPwd.setSpan(fcs,start_pwd,end_pwd, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);


        String cardType_cur = model.getCardtype();
        int type = cardType_cur.equals(cardType[0]) ? 0 :
                cardType_cur.equals(cardType[1]) ? 1 :
                        cardType_cur.equals(cardType[2]) ? 2 : -1;
        switch (type){
            case 0:
                title_tv.setText(model.getTitle());
                card_virtual_iv.setImageResource(cardPic[0]);
                card_number_tv.setText(builderNum);
                card_pwd_tv.setVisibility(View.GONE);
                break;
            case 1:
                title_tv.setText(model.getTitle());
                card_virtual_iv.setImageResource(cardPic[1]);
                card_number_tv.setText(builderNum);
                card_pwd_tv.setText(builderPwd);
                card_pwd_tv.setVisibility(View.VISIBLE);
                break;
            case 2:
                title_tv.setText(model.getTitle());
                card_virtual_iv.setImageResource(cardPic[2]);
                card_number_tv.setText(builderNum);
                card_pwd_tv.setVisibility(View.GONE);
                break;
        }
    }

    private void showCardInfo(String timesid, String uidx){
        RequestParams params = new RequestParams(Constant.getBaseUrl() + "Page/Ucenter/WinVirtualDetail.ashx");

        params.addBodyParameter("timesid",timesid);
        params.addBodyParameter("uidx",uidx);
        x.http().post(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                Gson gson = new GsonBuilder().serializeNulls().create();
                List<CardModel> datas = gson.fromJson(result,new TypeToken<List<CardModel>>(){}.getType());
                selectCardType(datas.get(0));
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



    private String logisticTime = "";
    private String signTime = "";
    private void showWinInfo(String timesid, String uidx){
        RequestParams params = new RequestParams(Constant.getBaseUrl() + "page/ucenter/WinDetail.ashx");

        params.addBodyParameter("timesid",timesid);
        params.addBodyParameter("uidx",uidx);
        x.http().post(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                Log.e(TAG, result);
                try {
                    Gson gson = new GsonBuilder().serializeNulls().create();
                    WinInfoModel model = gson.fromJson(result,new TypeToken<WinInfoModel>(){}.getType());
                    AddressModel addressModel = model.getAddressinfo();
                    int state = addressModel == null ? AddressNot : addressModel.getAddressidx() == 0 ? AddressConfirmed : AddressExist;
                    showAddressInfo(state,addressModel);

                    WinInfoModel.LogisticModel logisticModel = model.getLogisticinfo();
                    WinInfoModel.SignModel signModel = model.getSigninfo();
                    //WinInfoModel.CompleteModel completeModel = model.getCompleteinfo();
                    if(logisticModel != null)
                        logisticTime = Utility.trimDate(logisticModel.getLdate());
                    if(signModel != null)
                        signTime = Utility.trimDate(signModel.getCdate());

                    int state_l =  WaitAddress;
                    if(logisticModel != null && signModel != null)
                        state_l = logisticModel.getLdate() == null ? WaitDeliver :
                                signModel.getCdate() == null ? WaitConfirm : Complete;
                    showLogisticsInfo(state_l);

                    if(logisticModel != null && logisticModel.getHawb().length() > 5){
                        confirm_info_logistics_ll.setVisibility(View.VISIBLE);
                        showLogisticsInfo(logisticModel);
                    }else {
                        confirm_info_logistics_ll.setVisibility(View.GONE);
                    }


                } catch (Exception e) {
                    e.printStackTrace();
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

            }
        });
    }


    //Win Info address
    @ViewInject(R.id.confirm_user_name_tv)
    private TextView name;

    @ViewInject(R.id.confirm_user_address_tv)
    private TextView address;

    @ViewInject(R.id.confirm_phone_tv)
    private TextView phone;

    @ViewInject(R.id.confirm_info_address_rl)
    private RelativeLayout confirm_info_address_rl;

    @ViewInject(R.id.confirm_btn_ll)
    private LinearLayout confirm_btn_ll;

    @ViewInject(R.id.confirm_info_address_tip_rl)
    private RelativeLayout confirm_info_address_tip_rl;


    private final int AddressNot = 0, AddressExist = 1, AddressConfirmed = 2;
    private void showAddressInfo(int state, AddressModel model){
        switch (state){
            case AddressNot:
                //address not exist
                confirm_info_address_rl.setVisibility(View.GONE);
                confirm_btn_ll.setVisibility(View.GONE);
                confirm_info_address_tip_rl.setVisibility(View.VISIBLE);
                break;
            case AddressExist:
                //address exist
                confirm_info_address_rl.setVisibility(View.VISIBLE);
                confirm_btn_ll.setVisibility(View.VISIBLE);
                confirm_info_address_tip_rl.setVisibility(View.GONE);
                if(model != null){
                    name.setText(model.getFirstname());
                    phone.setText(model.getMobile());
                    String addressStr = model.getCity() + " " + model.getDistrict() + " " + model.getAddress();
                    address.setText(addressStr);
                    aModel = model;
                }
                break;
            case AddressConfirmed:
                //address has confirmed
                confirm_info_address_rl.setVisibility(View.VISIBLE);
                confirm_btn_ll.setVisibility(View.GONE);
                confirm_info_address_tip_rl.setVisibility(View.GONE);
                if(model != null){
                    name.setText(model.getFirstname());
                    phone.setText(model.getMobile());
                    String addressStr = model.getCity() + " " + model.getDistrict() + " " + model.getAddress();
                    address.setText(addressStr);
                    aModel = model;
                }
                break;
        }
    }
    //Logistics Info
    @ViewInject(R.id.tag_iv_1)
    private ImageView tag_iv_1;

    @ViewInject(R.id.tag_iv_2)
    private ImageView tag_iv_2;

    @ViewInject(R.id.tag_iv_3)
    private ImageView tag_iv_3;

    @ViewInject(R.id.tag_iv_4)
    private ImageView tag_iv_4;

    @ViewInject(R.id.state_tv_1)
    private TextView state_tv_1;

    @ViewInject(R.id.state_tv_2)
    private TextView state_tv_2;

    @ViewInject(R.id.state_tv_3)
    private TextView state_tv_3;

    @ViewInject(R.id.state_tv_4)
    private TextView state_tv_4;

    @ViewInject(R.id.info_tv_1)
    private TextView info_tv_1;

    @ViewInject(R.id.info_tv_2)
    private TextView info_tv_2;

    @ViewInject(R.id.info_tv_3)
    private TextView info_tv_3;

    @ViewInject(R.id.confirm_ok_tv_3)
    private TextView confirm_ok_tv_3;

    @ViewInject(R.id.confirm_ok_tv_4)
    private TextView confirm_ok_tv_4;

/*    private enum State{
        WaitAddress, WaitDeliver, WaitConfirm, Complete
    }*/

    private final int WaitAddress = 0,WaitDeliver = 1,WaitConfirm = 2,Complete = 3;

    private void showLogisticsInfo(int state){
        ImageView[] tag_iv = {tag_iv_1,tag_iv_2,tag_iv_3,tag_iv_4};
        TextView[] state_tv = {state_tv_1,state_tv_2,state_tv_3,state_tv_4};
        TextView[] info_tv = {info_tv_1,info_tv_2,info_tv_3};
        TextView[] confirm_ok_tv = {confirm_ok_tv_3,confirm_ok_tv_4};

        int text_3_c = getResources().getColor(R.color.text_3_c);
        int text_b_c = getResources().getColor(R.color.hint_c);


        switch (state){
            case WaitAddress:
                //wait confirm address
                for (int i = 0; i < tag_iv.length; i++) {
                    tag_iv[i].setImageResource(R.mipmap.shangpingxuanzedian_default);
                    if(i == 0){
                        tag_iv[i].setImageResource(R.mipmap.shangpingxuanzedian_selected);
                    }
                }

                for (int i = 0; i < state_tv.length; i++) {
                    state_tv[i].setTextColor(text_b_c);
                    if(i == 0){
                        state_tv[i].setTextColor(text_3_c);
                    }
                }

                for (int i = 0; i < info_tv.length; i++) {
                    info_tv[i].setVisibility(View.GONE);
                    if(i == 0){
                        info_tv[i].setVisibility(View.VISIBLE);
                        info_tv[i].setText(lucktime);
                    }
                }

                for (int i = 0; i < confirm_ok_tv.length; i++) {
                    confirm_ok_tv[i].setVisibility(View.GONE);
                }
                break;
            case WaitDeliver:
                //wait deliver
                for (int i = 0; i < tag_iv.length; i++) {
                    tag_iv[i].setImageResource(R.mipmap.shangpingxuanzedian_default);
                    if(i == 1){
                        tag_iv[i].setImageResource(R.mipmap.shangpingxuanzedian_selected);
                    }
                }

                for (int i = 0; i < state_tv.length; i++) {
                    state_tv[i].setTextColor(text_b_c);
                    if(i == 1){
                        state_tv[i].setTextColor(text_3_c);
                    }
                }

                for (int i = 0; i < info_tv.length; i++) {
                    info_tv[i].setVisibility(View.VISIBLE);
                    if (i == 0) {
                        info_tv[i].setText(lucktime);
                    }
                    if (i == 1) {
                        info_tv[i].setText(R.string.please_wait);
                    }
                    if (i == 2) {
                        info_tv[i].setVisibility(View.GONE);
                    }
                }

                for (TextView aConfirm_ok_tv : confirm_ok_tv) {
                    aConfirm_ok_tv.setVisibility(View.GONE);
                }

                break;
            case WaitConfirm:
                //wait confirm
                for (int i = 0; i < tag_iv.length; i++) {
                    tag_iv[i].setImageResource(R.mipmap.shangpingxuanzedian_default);
                    if(i == 2){
                        tag_iv[i].setImageResource(R.mipmap.shangpingxuanzedian_selected);
                    }
                }

                for (int i = 0; i < state_tv.length; i++) {
                    state_tv[i].setTextColor(text_b_c);
                    if(i == 2){
                        state_tv[i].setTextColor(text_3_c);
                    }
                }

                for (int i = 0; i < info_tv.length; i++) {
                    info_tv[i].setVisibility(View.VISIBLE);
                    if (i == 0) {
                        info_tv[i].setText(lucktime);
                    }
                    if (i == 1) {
                        info_tv[i].setText(logisticTime);
                    }
                    if (i == 2) {
                        info_tv[i].setVisibility(View.GONE);
                    }
                }

                for (int i = 0; i < confirm_ok_tv.length; i++) {
                    confirm_ok_tv[i].setVisibility(View.GONE);
                    if(i == 0){
                        confirm_ok_tv[i].setVisibility(View.VISIBLE);
                    }
                }

                break;
            case Complete:
                //complete
                for (int i = 0; i < tag_iv.length; i++) {
                    tag_iv[i].setImageResource(R.mipmap.shangpingxuanzedian_default);
                    if(i == 3){
                        tag_iv[i].setImageResource(R.mipmap.shangpingxuanzedian_selected);
                    }
                }

                for (int i = 0; i < state_tv.length; i++) {
                    state_tv[i].setTextColor(text_b_c);
                    if(i == 3){
                        state_tv[i].setTextColor(text_3_c);
                    }
                }

                for (int i = 0; i < info_tv.length; i++) {
                    info_tv[i].setVisibility(View.VISIBLE);
                    if (i == 0) {
                        info_tv[i].setText(lucktime);
                    }
                    if (i == 1) {
                        info_tv[i].setText(logisticTime);
                    }
                    if (i == 2) {
                        info_tv[i].setText(signTime);
                    }
                }

                for (int i = 0; i < confirm_ok_tv.length; i++) {
                    confirm_ok_tv[i].setVisibility(View.GONE);
                    /*if(i == 1){
                        confirm_ok_tv[i].setVisibility(View.VISIBLE);
                    }*/
                }

                break;
        }

    }


    //Award Info
    @ViewInject(R.id.confirm_icon_iv)
    private ImageView confirm_icon_iv;

    @ViewInject(R.id.confirm_name)
    private TextView confirm_name;

    @ViewInject(R.id.confirm_demand_tv)
    private TextView confirm_demand_tv;

    @ViewInject(R.id.confirm_issue)
    private TextView confirm_issue;

    @ViewInject(R.id.confirm_number_tv)
    private TextView confirm_number_tv;

    @ViewInject(R.id.confirm_unveil_time_tv)
    private TextView confirm_unveil_time_tv;

    private void showAwardInfo(WinRecordModel model){
        ImageOptions imageOptions = new ImageOptions.Builder()
                .setSize(DensityUtil.dip2px(120), DensityUtil.dip2px(120))
                .setRadius(DensityUtil.dip2px(5))
                .setCrop(true)
                .setImageScaleType(ImageView.ScaleType.CENTER_CROP)
                .setLoadingDrawableId(R.mipmap.commodity_image)
                .setFailureDrawableId(R.mipmap.commodity_image)
                .build();
        x.image().bind(confirm_icon_iv,model.getHeadpic(),imageOptions);

        String title = model.getTitle() + model.getSubtitle();
        confirm_name.setText(title);

        String issue = getResources().getString(R.string.issue).substring(0,3) + model.getTimesid();
        confirm_issue.setText(issue);

        String time = getResources().getString(R.string.detail_unveil_time);
        time = String.format(time,model.getLuckytime())		;
        confirm_unveil_time_tv.setText(time);

        String demand = getResources().getString(R.string.total_demand);
        demand = String.format(demand,model.getTotal());
        confirm_demand_tv.setText(demand);

        String lucky_id = getResources().getString(R.string.lucky_number).substring(0,5) + model.getLuckyid();
        confirm_number_tv.setText(lucky_id);
    }

    //confirm address
    private void confirmAddress(String uidx,String addressidx, String timesid){
        RequestParams params = new RequestParams(Constant.getBaseUrl() + "page/ucenter/WinAddress.ashx");
        TokenVerify.addToken(this,params);
        params.addBodyParameter("uidx",uidx,"form-data");
        params.addBodyParameter("addressidx",addressidx,"form-data");
        params.addBodyParameter("timesid",timesid,"form-data");

        x.http().post(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                if(result.toUpperCase().equals("SUCCESS")){
                    TokenVerify.saveCookie(ConfirmInfoActivity.this);
                    showAddressInfo(AddressConfirmed,null);

                    long issue = wModel.getTimesid();
                    long user_id = preferences.getLong(Constant.USER_ID,0);
                    if(user_id != 0)
                        showWinInfo(issue + "",user_id+"");
                }else {
                    Utility.toastShow(x.app(),R.string.confirm_fail);
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

            }
        });
    }

    //confirm receipt
    private void confirmReceipt(String uidx,String timesid){
        RequestParams params = new RequestParams(Constant.getBaseUrl() + "page/ucenter/WinSign.ashx");
        TokenVerify.addToken(this,params);
        params.addBodyParameter("uidx",uidx,"form-data");
        params.addBodyParameter("timesid",timesid,"form-data");

        x.http().post(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                try {
                    TokenVerify.saveCookie(ConfirmInfoActivity.this);
                    JSONObject jso = new JSONObject(result);
                    String returncode  = jso.getString("returncode");
                    if(returncode.toUpperCase().equals("SUCCESS")){
                        signTime = Utility.trimDate(jso.getString("cdate"));
                        showLogisticsInfo(Complete);
                    }else {
                        Utility.toastShow(x.app(),R.string.confirm_receipt_fail);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
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

            }
        });
    }

    @ViewInject(R.id.confirm_info_logistics_ll)
    private LinearLayout confirm_info_logistics_ll;
    @ViewInject(R.id.logistics_award_name)
    private TextView logistics_award_name;
    @ViewInject(R.id.logistics_company_tv)
    private TextView logistics_company_tv;
    @ViewInject(R.id.express_number_tv)
    private TextView express_number_tv;

    //show logistics information
    private void showLogisticsInfo(WinInfoModel.LogisticModel model){
        String award_name = wModel.getTitle() + " " + wModel.getSubtitle();
        logistics_award_name.setText(award_name);
        ForegroundColorSpan fc_3 = StringUtil.fcSpan(R.color.text_3_c);
        String companyStr = getString(R.string.logistics_company,model.getCompany());
        SpannableStringBuilder builder_company = StringUtil.singleSpan(companyStr,model.getCompany(),fc_3);
        logistics_company_tv.setText(builder_company);
        String hawbStr = getString(R.string.express_number,model.getHawb());
        SpannableStringBuilder builder_hawb = StringUtil.singleSpan(hawbStr,model.getHawb(),fc_3);
        express_number_tv.setText(builder_hawb);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(data == null) return;
        Bundle bundle = data.getExtras();
        AddressModel model = new AddressModel();
        if(bundle == null) return;
        switch (resultCode){
            case Constant.RESULT_CODE_UPDATE:
//                model.setUidx(bundle.getLong("uidx"));
//                model.setAddressidx(bundle.getLong("addressidx"));
//                model.setFirstname(bundle.getString("firstname"));
//                model.setMobile(bundle.getString("mobile"));
//                model.setCity(bundle.getString("city"));
//                model.setDistrict(bundle.getString("district"));
//                model.setAddress(bundle.getString("address"));
//                showAddressInfo(AddressExist,model);
                initView(false);
                break;
            case Constant.RESULT_CODE:
                model = (AddressModel) bundle.getSerializable("bundle");
                showAddressInfo(AddressExist,model);
                break;
        }
    }
}
