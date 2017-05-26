package com.luckybuy;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.style.ForegroundColorSpan;
import android.text.style.TextAppearanceSpan;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Switch;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.luckybuy.layout.LoadingDialog;
import com.luckybuy.login.LoginUserUtils;
import com.luckybuy.model.OrderNumModel;
import com.luckybuy.model.PayMethodModel;
import com.luckybuy.model.UserModel;
import com.luckybuy.network.ParseData;
import com.luckybuy.network.TokenVerify;
import com.luckybuy.pay.BluePay;
import com.luckybuy.pay.BluePayActivity;
import com.luckybuy.pay.PayPalActivity;
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

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by zhiPeng.S on 2016/6/14.
 */
@ContentView(R.layout.activity_payment)
public class PaymentActivity extends BaseActivity implements CompoundButton.OnCheckedChangeListener{
    
    private SharedPreferences preferences;
    private SharedPreferences.Editor editor;
    private BluePay bluePay;
    Bundle bundle = new Bundle();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        title_tv.setText(R.string.title_payment);
        preferences = LoginUserUtils.getUserSharedPreferences(this);
        editor = preferences.edit();
        bluePay = new BluePay(this);
        initOrderInfo();
        initTypeOfPayment();
        initAvailableType();
    }

    private void initOrderInfo(){
        Intent intent = getIntent();
        bundle = intent.getExtras();
        initIndentInfo();
    }

    private void initIndentInfo(){
        String order_number = bundle.getString("ordernumber");
        long amount = bundle.getLong("amount");
        long money = bundle.getLong("money");
        long coin = bundle.getLong("coin");
        Log.e(TAG,order_number + "&" + amount);
        //order number
        String order_numStr = getString(R.string.payment_indent_number,order_number);
        order_number_tv.setText(order_numStr);
        editor.putString(Constant.ORDER_NUM,order_number);
        editor.commit();

        //available balance
        double money_dou = money/1.0;
        String moneyStr  = getString(R.string.available_balance,money_dou);
        avail_balance_tv.setText(moneyStr);

        //order amount
        double amount_dou = amount/1.0;
        pay_amount_tv.setText(getString(R.string.pay_money_value,amount_dou));

        String symbol = getString(R.string.currency_symbol);
//        DecimalFormat douF = new DecimalFormat("#0.00");
//        String douS = douF.format(amount_dou);
//        douS = symbol + douS;
//
//        String order_amountStr = getString(R.string.payment_amount_text,amount_dou);
//        ForegroundColorSpan fgcSpan = StringUtil.fcSpan(R.color.light_red);
//        SpannableStringBuilder builder = StringUtil.singleSpan(order_amountStr,douS,fgcSpan);
//        payment_amount_tv.setText(getString(R.string.payment_amount_text));


        aSwitch.setOnCheckedChangeListener(this);
        aSwitch.setChecked(false);
        aSwitch.setClickable(coin >= 100);
        isAvailable = aSwitch.isChecked();
        isBalance = balance_cb.isChecked();
        setPayMoney();
        setPayMethod();

    }

    private boolean isAvailable,isBalance;
    private void setPayMoney(){
        String payStr = getString(R.string.pay_money_value);
        long money = bundle.getLong("money");
        long amount = bundle.getLong("amount");
        long coin = bundle.getLong("coin");
        long diamond = coin/100*100;
        double coin_a = diamond/10.0;
        double pay_b,pay_o;
        final int all = 0, dia = 1, bal = 2, none = 3;
        int state = !isAvailable ? bal : all;

        switch (state){
            case all:
                break;
            case bal:
                coin_a = 0;
                break;
        }

        pay_b = amount - coin_a > money ? money : amount > coin_a ? amount - coin_a : 0;
        pay_o = amount > (coin_a + money) ? amount - (coin_a + money) : 0;

        String pay_b_str = getString(R.string.pay_money_value2,pay_b);
        pay_balance_tv.setText(pay_b_str);
        String pay_o_str = String.format(payStr,pay_o);
        payment_money_value_tv.setText(pay_o_str);
        payment_amount_tv.setText(getString(R.string.payment_amount_text,pay_o));

    }

    private boolean isMixed;
    private void setPayMethod(){
        long money = bundle.getLong("money");
        long amount = bundle.getLong("amount");
        long coin = bundle.getLong("coin");
        long diamond = coin/100*100;
        double coin_a = diamond/10.0;
        final  int single_oth = 0, single_bal = 1, mixed = 2;
        if(!isAvailable) coin_a = 0;
//        int type = money <= 0 ? single_oth : money + coin_a >= amount ? single_bal : money + coin_a < amount ? mixed : -1;
        int type = money + coin_a <= 0 ? single_oth : money + coin_a >= amount ? single_bal : money + coin_a < amount ? mixed : -1;
        switch (type){
            case single_oth:
                isMixed = false;
//                paypal_cb.setChecked(true);
//                balance_rl.setEnabled(false);
//                payment_type_ll.setVisibility(View.VISIBLE);

                balance_cb.setChecked(false);
                balance_cb.setChecked(true);
                payment_type_ll.setVisibility(View.GONE);
                payment_immediate_btn.setText(R.string.recharge);
                payment_amount_tv.setText(R.string.balance_lack_tip);
                break;
            case single_bal:
                isMixed = false;
                balance_cb.setChecked(false);
                balance_cb.setChecked(true);
                payment_type_ll.setVisibility(View.GONE);
                payment_immediate_btn.setText(R.string.payment_immediate);
                payment_amount_tv.setText("");
                break;
            case mixed:
//                isMixed = true;
//                balance_cb.setChecked(true);
//                paypal_cb.setChecked(true);
//                payment_type_ll.setVisibility(View.VISIBLE);

                isMixed = false;
                balance_cb.setChecked(false);
                balance_cb.setChecked(true);
                payment_type_ll.setVisibility(View.GONE);
                payment_immediate_btn.setText(R.string.recharge);
                payment_amount_tv.setText(R.string.balance_lack_tip);
                break;
        }

    }



    @ViewInject(R.id.title_activity)
    private TextView title_tv;

    @ViewInject(R.id.payment_indent_number_tv)
    private TextView order_number_tv;

    @ViewInject(R.id.avail_balance_tv)
    private TextView avail_balance_tv;

    @ViewInject(R.id.payment_amount_tv)
    private TextView payment_amount_tv;

    @ViewInject(R.id.payment_available_diamond_tv)
    private TextView available_diamond_tv;

    @ViewInject(R.id.payment_switch)
    private Switch aSwitch;

    @ViewInject(R.id.balance_rl)
    private RelativeLayout  balance_rl;

    @ViewInject(R.id.payment_type_ll)
    private LinearLayout payment_type_ll;

    @ViewInject(R.id.payment_type_more_ll)
    private LinearLayout payment_type_more_ll;

    @ViewInject(R.id.payment_other_rl)
    private RelativeLayout payment_other_rl;

    @ViewInject(R.id.pay_balance_tv)
    private TextView pay_balance_tv;

    @ViewInject(R.id.payment_money_value_tv)
    private TextView payment_money_value_tv;

    @ViewInject(R.id.pay_amount_tv)
    private TextView pay_amount_tv;

    @ViewInject(R.id.payment_immediate_btn)
    private Button payment_immediate_btn;

    @Event({R.id.back_iv,R.id.payment_immediate_btn,R.id.payment_other_rl,R.id.payment_get_diamond_rl,
            R.id.balance_rl,R.id.visa_rl,R.id.paypal_rl,R.id.line_pay_rl,
            R.id.true_money_rl,R.id.mol_rl,R.id.bluepay_rl,R.id.alipay_rl,
            R.id.paysbuy_rl})
    private void viewClick(View view){
        Intent intent = new Intent();
        switch(view.getId()){
            case R.id.back_iv:
                this.finish();
                break;
            case R.id.payment_get_diamond_rl:
                intent.setClass(this,DiamondMissionActivity.class);
                startActivity(intent);
                break;
            case R.id.payment_immediate_btn:
                //if(isMixed) payment(0);
                if(payment_immediate_btn.getText().toString().equals(getString(R.string.recharge))){
                    intent.setClass(this,ChargeActivity.class);
                    intent.putExtra(Constant.PAYMENT,true);
                    intent.putExtras(bundle);
                    startActivityForResult(intent,Constant.REQUEST_CODE);
                    return;
                }

                payment(type_selected);
                break;
            case R.id.payment_other_rl:
                payment_type_more_ll.setVisibility(View.VISIBLE);
                payment_other_rl.setVisibility(View.GONE);
                break;
            case R.id.balance_rl:
                balance_cb.setChecked(true);
                break;
            case R.id.visa_rl:
                visa_cb.setChecked(true);
                break;
            case R.id.paypal_rl:
                paypal_cb.setChecked(true);
                break;
            case R.id.line_pay_rl:
                line_pay_cb.setChecked(true);
                break;
            case R.id.true_money_rl:
                true_money_cb.setChecked(true);
                break;
            case R.id.mol_rl:
                mol_cb.setChecked(true);
                break;
            case R.id.bluepay_rl:
                bluepay_cb.setChecked(true);
                break;
            case R.id.alipay_rl:
                alipay_cb.setChecked(true);
                break;
            case R.id.paysbuy_rl:
                paysbuy_cb.setChecked(true);
                break;
        }
    }


    @SuppressLint("HandlerLeak")
    Handler mHandler = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);

            switch (msg.what) {
                case R.id.SETTLEMENT_SUCCESS:
                    if(type_selected == 0){
                        Intent intent = new Intent(PaymentActivity.this,PayResultActivity.class);
                        startActivity(intent);
                    }
//                    String result = (String) msg.obj;
//                    Intent intent = new Intent();
//                    intent.putExtra("pay_result", result);
//                    intent.setClass(PaymentActivity.this, PayResultActivity.class);
//                    startActivityForResult(intent,Constant.REQUEST_CODE);
                    break;
                case R.id.AWARD_SUCCESS:
                    @SuppressWarnings("unchecked")
                    Map<String, Object> result_type = (Map<String, Object>) msg.obj;
                    if (!result_type.isEmpty()) {
                        @SuppressWarnings("unchecked")
                        List<PayMethodModel> showlist =
                                (List<PayMethodModel>) result_type.get(Constant.AWARD_LIST);
                        if(showlist == null) return;
                        if (!showlist.isEmpty()) {
                            for (int i = 1; i < 9; i++) {
                                String key = "5" + i;
                                RelativeLayout type_rl = (RelativeLayout) map_type.get(key);
                                type_rl.setVisibility(View.GONE);
                            }

                            for (PayMethodModel model : showlist) {
                                String payType = String.valueOf(model.getPayidx());
                                if(map_type.containsKey(payType)) {
                                    RelativeLayout type_rl = (RelativeLayout) map_type.get(payType);
                                    type_rl.setVisibility(View.VISIBLE);
                                }
                            }
                        }

                    }
                    break;
            }
        }
    };

    private void payment_balance(String order_id, String user_id, String coin) {
        RequestParams params = new RequestParams(Constant.getBaseUrl() + "page/ucenter/Orderpay.ashx");
        TokenVerify.addToken(this,params);
        params.addBodyParameter("ordernumber", order_id);
        params.addBodyParameter("uidx", user_id);
        params.addBodyParameter("coin", coin);

        x.http().post(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                try {
                    TokenVerify.saveCookie(PaymentActivity.this);
                    JSONObject jsonObject = new JSONObject(result);
                    mHandler.obtainMessage(R.id.SETTLEMENT_SUCCESS, jsonObject.toString()).sendToTarget();
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


    private void payment(int payMethod){
        String order_number = bundle.getString("ordernumber");
        final int BALANCE = 0,VISA = 1,PAYPAL = 2,LINEPAY = 3,BLUEPAY = 6,PAYSBUY = 8;
        Intent intent = new Intent();
        switch (payMethod){
            case BALANCE:
                long userid = preferences.getLong(Constant.USER_ID,0);
                if (userid != 0){
                    long amount = bundle.getLong("amount");
                    long money = bundle.getLong("money");
                    long coin = bundle.getLong("coin");
                    long diamond = 0;
                    if(isAvailable)
                        diamond = coin/100*100;
                    //single pay method
                    /*if(amount > money){
                        Utility.toastShow(PaymentActivity.this,R.string.balance_lack);
                        return;
                    }*/
                    payment_balance(order_number,userid+"",diamond+"");
                }
                break;
            case PAYPAL:
                intent.setClass(this, PayPalActivity.class);
                intent.putExtras(bundle);
                startActivityForResult(intent,Constant.REQUEST_CODE);
                break;
            case LINEPAY:
                break;
            case BLUEPAY:
//                intent.setClass(this, BluePayActivity.class);
//                intent.putExtras(bundle);
//                startActivityForResult(intent,Constant.REQUEST_CODE);

                intent.putExtras(bundle);
                bluePay.pay_5_THB(intent);
                break;
            case PAYSBUY:
                OrderNumModel model = new OrderNumModel();
                Bundle bundle_order = new Bundle();
                model.setOrdernumber(order_number);
                model.setAmount(bundle.getLong("amount"));
                model.setUidx(preferences.getLong(Constant.USER_ID,0));
                bundle_order.putSerializable("bundle",model);

                intent.setClass(this, WebActivity.class);
                intent.putExtra(Constant.WEB_H5,Constant.PAYSBUY);
                intent.putExtras(bundle_order);
                startActivity(intent);
                break;
            default:
                Utility.toastShow(this,"no open");
                break;
        }
    }

    @ViewInject(R.id.pay_cb_0)
    private CheckBox balance_cb;

    @ViewInject(R.id.pay_cb_1)
    private CheckBox visa_cb;

    @ViewInject(R.id.pay_cb_2)
    private CheckBox paypal_cb;

    @ViewInject(R.id.pay_cb_3)
    private CheckBox line_pay_cb;

    @ViewInject(R.id.pay_cb_4)
    private CheckBox true_money_cb;

    @ViewInject(R.id.pay_cb_5)
    private CheckBox mol_cb;

    @ViewInject(R.id.pay_cb_6)
    private CheckBox bluepay_cb;

    @ViewInject(R.id.pay_cb_7)
    private CheckBox alipay_cb;

    @ViewInject(R.id.pay_cb_8)
    private CheckBox paysbuy_cb;


    private int type_selected;
    private List<CheckBox> types = new ArrayList<>();
    private void initTypeOfPayment(){
        balance_cb.setChecked(true);
        types.add(balance_cb);
        types.add(visa_cb);
        types.add(paypal_cb);
        types.add(line_pay_cb);
        types.add(true_money_cb);
        types.add(mol_cb);
        types.add(bluepay_cb);
        types.add(alipay_cb);
        types.add(paysbuy_cb);
        for (CheckBox type : types) {
            type.setClickable(false);
        }
        setListener();
        initViewOfType();
    }

    private void setListener(){
        for (CheckBox type : types) {
            type.setOnCheckedChangeListener(this);
        }
    }

    @ViewInject(R.id.visa_rl)
    private RelativeLayout visa_rl;
    @ViewInject(R.id.paypal_rl)
    private RelativeLayout paypal_rl;
    @ViewInject(R.id.line_pay_rl)
    private RelativeLayout line_pay_rl;
    @ViewInject(R.id.true_money_rl)
    private RelativeLayout true_money_rl;
    @ViewInject(R.id.mol_rl)
    private RelativeLayout mol_rl;
    @ViewInject(R.id.bluepay_rl)
    private RelativeLayout bluepay_rl;
    @ViewInject(R.id.alipay_rl)
    private RelativeLayout alipay_rl;
    @ViewInject(R.id.paysbuy_rl)
    private RelativeLayout paysbuy_rl;

    private Map<String,Object> map_type ;
    private void initViewOfType(){
        map_type = new HashMap<>();
        map_type.put("51",visa_rl);
        map_type.put("52",paypal_rl);
        map_type.put("53",line_pay_rl);
        map_type.put("54",true_money_rl);
        map_type.put("55",mol_rl);
        map_type.put("56",bluepay_rl);
        map_type.put("57",alipay_rl);
        map_type.put("58",paysbuy_rl);
    }

    private void initAvailableType(){
        RequestParams params = new RequestParams(Constant.getBaseUrl() + "Handle/Pay/PayList.ashx");

        x.http().get(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                Map<String, Object> resultMap = ParseData.parsePayMethodInfo(result);
                mHandler.obtainMessage(R.id.AWARD_SUCCESS, resultMap).sendToTarget();
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

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        final int checkboxT = 0, switchT = 1;
        int viewType = buttonView instanceof CheckBox ? checkboxT : buttonView instanceof Switch ? switchT : -1;
        switch (viewType){
            case checkboxT:
                if (isChecked) {
                    for (CheckBox type : types) {
                        if (type != buttonView){
                            if(!isMixed)
                                type.setChecked(false);
                            else {
                                if(!type.equals(balance_cb))
                                    type.setChecked(false);
                            }

                        }

                    }
                    type_selected = types.indexOf(buttonView);

                    isBalance = buttonView.getId() == R.id.pay_cb_0;
                    setPayMoney();
                }
                break;
            case switchT:
                isAvailable = isChecked;
                setPayMoney();
                setPayMethod();
                long amount = bundle.getLong("amount");
                long coin = bundle.getLong("coin");
                long diamond = coin/100*100;
                double coin_a = diamond/10.0;
                double pledge = coin_a < amount/1.0 ? coin_a : amount/1.0;
                String diamondStr = isChecked ?
                        getString(R.string.available_diamond,(long)pledge*10,pledge) :
                        getString(R.string.diamond_less_than_constant_value,coin);
                available_diamond_tv.setText(diamondStr);
                break;
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (resultCode){
            case Constant.RESULT_CODE:
                setResult(Constant.RESULT_CODE);
                this.finish();
                break;
            case Constant.RESULT_CODE_UPDATE:
                obtain_user_information(String.valueOf(preferences.getLong(Constant.USER_ID,0)));
                break;
            default:
                this.finish();
                break;
        }
    }

    private LoadingDialog loadingDialog;
    private void obtain_user_information(String uidx){
        loadingDialog = new LoadingDialog(this);
        loadingDialog.showDialog();
        RequestParams params = new RequestParams(Constant.getBaseUrl() + "page/ucenter/MemberInfo.ashx");
        params.addQueryStringParameter("uidx", uidx);
        TokenVerify.addToken(this,params);
        x.http().get(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                loadingDialog.dismiss();
                try {
                    TokenVerify.saveCookie(PaymentActivity.this);
                    Gson gson = new GsonBuilder().serializeNulls().create();
                    List<UserModel> modelData = gson.fromJson(result, new TypeToken<List<UserModel>>(){}.getType());
                    UserModel model = modelData.get(0);

                    bundle.putLong("money",model.getMoney());
                    initIndentInfo();
//                    String order_number = bundle.getString("ordernumber");
//                    long amount = bundle.getLong("amount");
//                    long money = bundle.getLong("money");
//                    long coin = bundle.getLong("coin");
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
}
