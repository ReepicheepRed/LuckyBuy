package com.luckybuy;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.luckybuy.adapter.AwardAdapter;
import com.luckybuy.adapter.ListBaseAdapter;
import com.luckybuy.adapter.PayResultAdapter;
import com.luckybuy.layout.Dialog_Check_all;
import com.luckybuy.layout.LoadingDialog;
import com.luckybuy.login.LoginUserUtils;
import com.luckybuy.model.AwardModel;
import com.luckybuy.model.PayResultModel;
import com.luckybuy.model.ResultItemModel;
import com.luckybuy.network.ParseData;
import com.luckybuy.network.TokenVerify;
import com.luckybuy.util.Constant;
import com.luckybuy.util.StringUtil;

import org.xutils.common.Callback;
import org.xutils.http.RequestParams;
import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.Event;
import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by zhiPeng.S on 2016/6/14.
 */
@ContentView(R.layout.activity_pay_result)
public class PayResultActivity extends BaseActivity{

    private SharedPreferences preferences;
    private boolean isCharge;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        title_tv.setText(R.string.title_pay_result);
        preferences = LoginUserUtils.getUserSharedPreferences(this);
        setShowList();

//        Intent intent = getIntent();
//        String result = intent.getStringExtra("pay_result");
//        Log.e("result", result);
//        showResult(result);

        long user_id = preferences.getLong(Constant.USER_ID,0);
        String orderNum = preferences.getString(Constant.ORDER_NUM,"");
        isCharge = orderNum.substring(0,3).equals("CHG");
        getShowList(user_id+"", orderNum);
    }

    @ViewInject(R.id.title_activity)
    private TextView title_tv;

    @Event({R.id.back_iv,R.id.result_snatch_continue_tv,R.id.result_snatch_record_tv,R.id.reloading_btn,R.id.result_back_snatch_btn})
    private void viewClick(View view){
        Intent intent = new Intent();
        switch(view.getId()){
            case R.id.back_iv:
            case R.id.result_back_snatch_btn:
            case R.id.result_snatch_continue_tv:
                intent.setClass(this,MainActivity.class);
                //intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                this.finish();
                break;
            case R.id.result_snatch_record_tv:
                intent.setClass(this,BuyRecordActivity.class);
                startActivity(intent);
                this.finish();
                break;
            case R.id.reloading_btn:
                long user_id = preferences.getLong(Constant.USER_ID,0);
                String orderNum = preferences.getString(Constant.ORDER_NUM,"");
                getShowList(user_id+"", orderNum);
                break;

        }
    }

    @ViewInject(R.id.result_tag_iv)
    private ImageView result_tag_iv;

    @ViewInject(R.id.result_title)
    private TextView result_title;

    @ViewInject(R.id.result_subtitle)
    private TextView result_subtitle;

    @ViewInject(R.id.result_payment_btn_ll)
    private LinearLayout result_payment_btn_ll;

    @ViewInject(R.id.result_back_snatch_btn)
    private Button result_back_snatch_btn;

    @ViewInject(R.id.payresult_lv)
    private ListView listView;
    private List<ResultItemModel> datas;
    private ListBaseAdapter<ResultItemModel> adapter;

    private void setShowList(){
        listView.setAdapter(getShowListAdapter());
    }

    private ListBaseAdapter<ResultItemModel> getShowListAdapter(){
        datas = new ArrayList<>();
        adapter = new PayResultAdapter(this, datas);
        return adapter;
    }


    private void showResult(String result){
        Map<String, Object> resultMap = ParseData.parsePayResultdInfo(result);
        if(resultMap == null) return;
        if(!resultMap.isEmpty()){
            @SuppressWarnings("unchecked")
            PayResultModel model = (PayResultModel) resultMap.get(Constant.AWARD_LIST);

            //Charge
            if(isCharge){
                long money = model.getSuccessobject().getSuccessmoney();
                int state = money > 0 ? 1 : -1;
                switch (state){
                    case 1:
                        result_title.setText(R.string.charge_success_title);
                        String success_subtitle = getResources().getString(R.string.charge_success_subtitle,money);
                        ForegroundColorSpan fcSpan = StringUtil.fcSpan(R.color.light_red);
                        SpannableStringBuilder builder = StringUtil.singleSpan(success_subtitle,money+"",fcSpan);
                        result_subtitle.setText(builder);
                        break;
                    case -1:
                        break;
                }
                result_back_snatch_btn.setVisibility(View.VISIBLE);
                result_payment_btn_ll.setVisibility(View.GONE);
                return;
            }

            //Payment
            List<ResultItemModel> showList = model.getSuccessobject().getSuccesslist();
            for (int i = 0; i < showList.size(); i++) {
                showList.get(i).setStatus(1);
            }
            List<ResultItemModel> fail = model.getFailobject().getFaillist();
            for (int i = 0; i < fail.size(); i++) {
                fail.get(i).setStatus(0);
            }

            int status = fail.size() == 0 ? 1 : showList.size() == 0 ? -1 : 0;
            switch (status){
                case 1:
                    result_tag_iv.setImageResource(R.mipmap.payment_successful_iconicon);
                    String success_title = getResources().getString(R.string.pay_result_success);
                    result_title.setText(success_title);
                    String success_subtitle = getResources().getString(R.string.wait_result_unveil);
                    result_subtitle.setText(success_subtitle);
                    break;
                case 0:
                    result_tag_iv.setImageResource(R.mipmap.tishi_icon_2x);
                    String sf_title = getResources().getString(R.string.pay_result_part_fail);
                    result_title.setText(sf_title);
                    String sf_subtitle = getResources().getString(R.string.pay_result_part_fail_subtitle);
                    sf_subtitle = String.format(sf_subtitle,fail.size());
                    result_subtitle.setText(sf_subtitle);
                    break;
                case -1:
                    result_tag_iv.setImageResource(R.mipmap.failure_to_pay_icon);
                    String fail_title = getResources().getString(R.string.pay_result_fail);
                    result_title.setText(fail_title);
                    String fail_subtitle = getResources().getString(R.string.money_will_back);
                    result_subtitle.setText(fail_subtitle);
                    break;
            }

            showList.addAll(fail);

            if(showList == null){
                updateView(false);
                return;
            }

            boolean hasdata = !showList.isEmpty();
            updateView(hasdata);

            if(!showList.isEmpty()){
                datas.clear();
                datas = showList;
                adapter.setData(datas);
                adapter.notifyDataSetChanged();
            }
        }
    }

    @ViewInject(R.id.result_et)
    private EditText editText;
    LoadingDialog loadingDialog;
    private void getShowList(String uidx, String ordernumber){
        loadingDialog = new LoadingDialog(this);
        loadingDialog.showDialog();
        RequestParams params = new RequestParams(Constant.getBaseUrl() + "Page/Ucenter/OrderCheck.ashx");
        TokenVerify.addToken(this,params);
        params.addBodyParameter("uidx",uidx);
        params.addBodyParameter("ordernumber",ordernumber);
        params.addBodyParameter("isandroid","true");

        x.http().post(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                TokenVerify.saveCookie(PayResultActivity.this);
                loadingDialog.dismiss();
                editText.setText(result);
                showResult(result);
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

    @ViewInject(R.id.result_exist_ll)
    private LinearLayout result_exist_ll;

    @ViewInject(R.id.loading_fail_rl)
    private RelativeLayout loading_fail_rl;

    private void updateView(boolean hasData){
        if(hasData){
            result_exist_ll.setVisibility(View.VISIBLE);
            loading_fail_rl.setVisibility(View.GONE);
        }else {
            result_exist_ll.setVisibility(View.GONE);
            loading_fail_rl.setVisibility(View.VISIBLE);
        }
    }

}
