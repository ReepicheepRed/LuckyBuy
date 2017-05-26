package com.luckybuy;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.luckybuy.adapter.ChargeAdapter;
import com.luckybuy.adapter.ListBaseAdapter;
import com.luckybuy.layout.Dialog_Charge_12Call;
import com.luckybuy.layout.Dialog_Charge_Happy;
import com.luckybuy.layout.Dialog_Charge_PaysBuy;
import com.luckybuy.login.LoginUserUtils;
import com.luckybuy.model.BaskSNSModel;
import com.luckybuy.model.OrderNumModel;
import com.luckybuy.model.PayMethodModel;
import com.luckybuy.network.ParseData;
import com.luckybuy.pay.BluePay;
import com.luckybuy.pay.BluePayActivity;
import com.luckybuy.pay.BluePayInit;
import com.luckybuy.pay.BluePayInitImpl;
import com.luckybuy.pay.Call_12Pay;
import com.luckybuy.pay.HappyPay;
import com.luckybuy.pay.MolPay;
import com.luckybuy.pay.PayPalActivity;
import com.luckybuy.pay.TrueMoney;
import com.luckybuy.presenter.ChargePresenter;
import com.luckybuy.presenter.Impl.ChargePresenterImpl;
import com.luckybuy.util.Constant;
import com.luckybuy.util.Utility;

import org.w3c.dom.Text;
import org.xutils.common.Callback;
import org.xutils.http.RequestParams;
import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.Event;
import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by zhiPeng.S on 2016/6/14.
 */
@ContentView(R.layout.activity_charge)
public class ChargeActivity extends BaseActivity implements AdapterView.OnItemClickListener, TextView.OnEditorActionListener {

    private String charge_money = "0";
    private int charge_method;

    private BluePay bluePay;
    private MolPay molPay;

    private SharedPreferences preferences;
    private SharedPreferences.Editor editor;

    private Bundle bundle;
    private boolean isPayment;

    private ChargePresenter chargePresenter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        init();
        obtainAvailablePay();
        initDialog();
        dealPaymentInfo();
    }

    private void init(){
        preferences = LoginUserUtils.getUserSharedPreferences(this);
        editor = preferences.edit();

        bluePayInit = new BluePayInitImpl(this);

        chargePresenter = new ChargePresenterImpl(this,bluePayInit);
        chargePresenter.checkPermission();

        bluePay = new BluePay(this,bluePayInit);
        molPay = new MolPay(this);
        initView();
    }

    private void dealPaymentInfo(){
        Intent intent = getIntent();
        if(!intent.hasExtra(Constant.PAYMENT)) return;
        bundle = intent.getExtras();
        isPayment = intent.getBooleanExtra(Constant.PAYMENT,false);

        long amount = bundle.getLong("amount");
        long money = bundle.getLong("money");
        charge_money = String.valueOf(amount - money);
    }

    public boolean isPayment() {
        return isPayment;
    }

    @ViewInject(R.id.title_activity)
    private TextView title_tv;

    @ViewInject(R.id.payment_amount_tv)
    private TextView total;

//    @ViewInject(R.id.payment_immediate_btn)
//    private Button chargeBtn;

    @Event({R.id.back_iv})
    private void viewClick(View view){
        switch(view.getId()){
            case R.id.back_iv:
                setResult(Constant.RESULT_CODE_MINE);
                this.finish();
                break;
            case R.id.payment_immediate_btn:
                //charge(charge_money,charge_method);
                break;
        }
    }


    Handler mHandler = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            @SuppressWarnings("unchecked")
            Map<String, Object> result = (Map<String, Object>) msg.obj;
            switch (msg.what) {
                case R.id.AWARD_SUCCESS:
                    if (!result.isEmpty()) {
                        @SuppressWarnings("unchecked")
                        List<PayMethodModel> showlist =
                                (List<PayMethodModel>) result.get(Constant.AWARD_LIST);
                        if (!showlist.isEmpty()) {
                            datas.clear();
                            datas = showlist;
                        }
                        adapter.setData(datas);
                        adapter.notifyDataSetChanged();
                    } else {
                        String returnContent = (String) result.get(Constant.RETURN_CONTENT);
                        Utility.toastShow(x.app(), returnContent);
                    }
                    break;
            }
        }
    };


    private void initView(){
        title_tv.setText(R.string.recharge);
        initPayMethod();
    }

    private enum type{
        visa(51),payPal(69),linePay(71),trueMoney(61),mol(55),bluePay(60),aliPay(57),paysBuy(68),happy(62),one2call(63),bank(70),defaultPay(-1);

        private int id;

        type(int id) {
            this.id = id;
        }

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }
    }


    private void charge(String amountStr, int method){

        Log.d("ChargeActivity","charge "+amountStr+" "+method);

        String orderNum = generalOrderNum();
        long amount = Long.valueOf(amountStr);
        editor.putString(Constant.ORDER_NUM,orderNum);
        editor.commit();
        type payType = type.defaultPay;
        for (type t: type.values()) {
            if(t.getId() == method)
                payType = t;
        }

        Intent intent = new Intent();
        Bundle bundle = new Bundle();
//        bundle.putString("ordernumber",orderNum);
//        bundle.putLong("amount",amount);

        Log.d("ChargeActivity","charge switch payType:"+payType);
        switch (payType){
            case visa:
                break;
            case linePay:
                dialog_charge_paysBuy.setMethod(Dialog_Charge_PaysBuy.linePay);
                dialog_charge_paysBuy.setTitle("LinePay");
                if(amount != 0) {
                    dialog_charge_paysBuy.setMoney(amount);
                    paysbuy_pay(orderNum,amount,intent,bundle);
                    return;
                }
                dialog_charge_paysBuy.showWindow();
                break;
            case mol:
                dialog_charge_paysBuy.setMethod(Dialog_Charge_PaysBuy.sms);
                dialog_charge_paysBuy.setTitle(R.string.dialog_title_SMS);
                dialog_charge_paysBuy.showWindow();
                break;
            case bluePay:
                dialog_charge_paysBuy.setMethod(Dialog_Charge_PaysBuy.sms);
                dialog_charge_paysBuy.setTitle(R.string.dialog_title_SMS);
                dialog_charge_paysBuy.showWindow();
                break;
            case aliPay:
                break;
            case payPal:
                dialog_charge_paysBuy.setMethod(Dialog_Charge_PaysBuy.paypal);
                dialog_charge_paysBuy.setTitle(R.string.paypal);
                if(amount != 0) {
                    dialog_charge_paysBuy.setMoney(amount);
                    paysbuy_pay(orderNum,amount,intent,bundle);
                    return;
                }

                dialog_charge_paysBuy.showWindow();
                break;
            case paysBuy:
                dialog_charge_paysBuy.setMethod(Dialog_Charge_PaysBuy.paysbuy);
                dialog_charge_paysBuy.setTitle(R.string.paysbuy_pay);
                if(amount != 0) {
                    dialog_charge_paysBuy.setMoney(amount);
                    paysbuy_pay(orderNum,amount,intent,bundle);
                    return;
                }
                dialog_charge_paysBuy.showWindow();
                break;
            case bank:
                dialog_charge_paysBuy.setMethod(Dialog_Charge_PaysBuy.bank);
                dialog_charge_paysBuy.setTitle("BankCard Pay");
                if(amount != 0) {
                    dialog_charge_paysBuy.setMoney(amount);
                    paysbuy_pay(orderNum,amount,intent,bundle);
                    return;
                }
                dialog_charge_paysBuy.showWindow();
                break;
            case happy:
                dialog_charge_happy.showWindow();
                break;
            case one2call:
                dialog_charge_12Call.setTrueMoney(false);
                dialog_charge_12Call.setTitle(R.string.dialog_title_12call);
                dialog_charge_12Call.setLogo(R.mipmap._one2_logo);
                dialog_charge_12Call.showWindow();
                break;
            case trueMoney:
                dialog_charge_12Call.setTrueMoney(true);
                dialog_charge_12Call.setTitle(R.string.true_money);
                dialog_charge_12Call.setLogo(R.mipmap._ture_logo);
                dialog_charge_12Call.showWindow();
                break;
            default:
                break;
        }
    }


    private String generalOrderNum(){
        String chargeNum;
        long user_id = preferences.getLong(Constant.USER_ID,0);
        String dateStr = Utility.FORMAT_NUM.format(System.currentTimeMillis());
        int random =(int)(Math.random()*900)+100;
        chargeNum = "CHG" + user_id + "T" + dateStr + random;
        return chargeNum;
    }

    private void settingAmount(String amount){
        String totalStr = getString(R.string.cart_amount_text);
        totalStr = totalStr + amount;
        total.setText(totalStr);
    }

    @ViewInject(R.id.charge_type_lv)
    private ListView listView;

    private ListBaseAdapter<PayMethodModel> adapter;

    private List<PayMethodModel> datas;

    boolean[] isSelect;

    private void initPayMethod(){
        datas = new ArrayList<>();
        adapter = new ChargeAdapter(this,datas);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(this);

        isSelect = ((ChargeAdapter)adapter).getIsSelect();
        for (int i = 0; i < datas.size(); i++) {
            if(isSelect[i]){
                charge_method = datas.get(i).getPayidx();
            }
        }
    }


    private void obtainAvailablePay(){
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
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Log.d("ChargeActivity","onItemClick");
        boolean[] isSelect = ((ChargeAdapter)adapter).getIsSelect();
        for (int i = 0; i < isSelect.length; i++) {
            isSelect[i] = false;
            if(i == position){
                isSelect[i] = true;
                charge_method = datas.get(i).getPayidx();
            }
        }
        adapter.notifyDataSetInvalidated();

        charge(charge_money,charge_method);
        Log.d("ChargeActivity","onItemClick--end");
    }

    @Override
    public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {

        if(actionId == EditorInfo.IME_ACTION_NEXT)
            actionId = EditorInfo.IME_ACTION_DONE;

        if (actionId == EditorInfo.IME_ACTION_DONE) {
            InputMethodManager imm = (InputMethodManager) v.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
            if (imm.isActive()) {
                imm.hideSoftInputFromWindow(v.getApplicationWindowToken(), 0);
            }
            return true;
        }
        return false;
    }

    private Dialog_Charge_Happy dialog_charge_happy;
    private HappyPay happyPay;

    private Dialog_Charge_12Call dialog_charge_12Call;
    private Call_12Pay call_12Pay;
    private TrueMoney trueMoney;

    private Dialog_Charge_PaysBuy dialog_charge_paysBuy;

    private void initDialog(){
        dialog_charge_happy = new Dialog_Charge_Happy(this,onClickListener);
        dialog_charge_happy.requestWindowFeature(Window.FEATURE_NO_TITLE);
        happyPay = new HappyPay(this,bluePayInit);

        dialog_charge_12Call = new Dialog_Charge_12Call(this,onClickListener);
        dialog_charge_12Call.requestWindowFeature(Window.FEATURE_NO_TITLE);
        call_12Pay = new Call_12Pay(this,bluePayInit);

        trueMoney = new TrueMoney(this,bluePayInit);

        dialog_charge_paysBuy = new Dialog_Charge_PaysBuy(this,onClickListener);
        dialog_charge_paysBuy.requestWindowFeature(Window.FEATURE_NO_TITLE);
    }

    public void onClick(View view){
        dialog_charge_paysBuy.setViewSelected(view);
        String moneyStr = ((TextView)view).getText().toString();
        dialog_charge_paysBuy.setMoney(Integer.valueOf(moneyStr));
    }

    View.OnClickListener onClickListener = new View.OnClickListener(){

        @Override
        public void onClick(View v) {
            Intent intent = new Intent();
            Bundle bundle = new Bundle();
            String orderNum = preferences.getString(Constant.ORDER_NUM,"");
            switch (v.getId()){
                case R.id.charge_card_dialog_charge_btn:
                    bundle.putString("ordernumber",orderNum);
                    bundle.putString("cardNum",dialog_charge_happy.getCardEt().getText().toString());
                    bundle.putString("snNum",dialog_charge_happy.getPwdEt().getText().toString());
                    intent.putExtras(bundle);
                    happyPay.pay_5_THB(intent);
                    dialog_charge_happy.dismiss();
                    break;
                case R.id.charge_12call_dialog_charge_btn:
                    bundle.putString("ordernumber",orderNum);
                    bundle.putString("cardNum",dialog_charge_12Call.getCardEt().getText().toString());
                    intent.putExtras(bundle);
                    if(dialog_charge_12Call.isTrueMoney()) {
                        trueMoney.pay_5_THB(intent);
                    }else {
                        call_12Pay.pay_5_THB(intent);
                    }
                    dialog_charge_12Call.dismiss();
                    break;
                case R.id.charge_paysbuy_dialog_charge_btn:

                    long amount = dialog_charge_paysBuy.getMoney();
                    if (amount==0){
                        Toast.makeText(getApplicationContext(), getResources().getString(R.string.paysbuy_money_tip), Toast.LENGTH_SHORT).show();
                        return;
                    }
                    paysbuy_pay(orderNum,amount,intent,bundle);
                    dialog_charge_paysBuy.dismiss();
                    break;
                case R.id.charge_card_dialog_cancel_iv:
                    dialog_charge_happy.dismiss();
                    break;
                case R.id.charge_12call_dialog_cancel_iv:
                    dialog_charge_12Call.dismiss();
                    break;
                case R.id.charge_paysbuy_dialog_cancel_iv:
                    dialog_charge_paysBuy.dismiss();
                    break;
            }
        }
    };

    private void paysbuy_pay(String orderNum,long amount,Intent intent,Bundle bundle){
        switch (dialog_charge_paysBuy.getMethod()){
            case Dialog_Charge_PaysBuy.sms:
                showMolSMSDialog(orderNum, amount);
                break;
            case Dialog_Charge_PaysBuy.paypal:
                dialog_charge_paysBuy.dismiss();
                bundle.putString("ordernumber",orderNum);
                bundle.putLong("amount",amount);
                intent.setClass(ChargeActivity.this, PayPalActivity.class);
                intent.putExtras(bundle);
                intent.putExtra("isPayment", isPayment());
                startActivityForResult(intent, Constant.REQUEST_CODE);
                break;
            case Dialog_Charge_PaysBuy.paysbuy:
                OrderNumModel model = new OrderNumModel();
                Bundle bundle_order = new Bundle();
                model.setOrdernumber(orderNum);
                model.setAmount(amount);
                model.setUidx(preferences.getLong(Constant.USER_ID,0));
                bundle_order.putSerializable("bundle",model);
                intent.setClass(ChargeActivity.this, WebActivity.class);
                intent.putExtra(Constant.WEB_H5,Constant.PAYSBUY);
                intent.putExtras(bundle_order);
                intent.putExtra("isPayment", isPayment());
                startActivity(intent);
                break;
            case Dialog_Charge_PaysBuy.bank:
                bundle.putString("ordernumber",orderNum);
                bundle.putLong("amount",amount);
                intent.putExtras(bundle);
                bluePay.payByBank(intent);
                break;
            case Dialog_Charge_PaysBuy.linePay:
                bundle.putString("ordernumber",orderNum);
                bundle.putLong("amount",amount);
                intent.putExtras(bundle);
                bluePay.payByLine(intent);
                break;
        }

    }

    //bluePay sms
    private void showSMSDialog(final String orderNum, final long amount){
        new AlertDialog.Builder(this)
                .setMessage(getString(R.string.sms_money_tip,amount))
                .setPositiveButton(R.string.confirm, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        Intent intent = new Intent();
                        Bundle bundle = new Bundle();
                        bundle.putString("ordernumber",orderNum);
                        bundle.putLong("amount",amount);
                        intent.putExtras(bundle);
                        bluePay.pay_5_THB(intent);
                        dialog.dismiss();
                        dialog_charge_paysBuy.dismiss();
                    }
                })
                .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .show();
    }

    //molPay sms
    private void showMolSMSDialog(final String orderNum, final long amount){
        new AlertDialog.Builder(this)
                .setMessage(getString(R.string.sms_money_tip,amount))
                .setPositiveButton(R.string.confirm, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        molPay.makePurchasePSMS(orderNum,String.valueOf(amount));
                        dialog.dismiss();
                        dialog_charge_paysBuy.dismiss();
                    }
                })
                .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (resultCode){
            case Constant.RESULT_CODE_UPDATE:
                this.setResult(Constant.RESULT_CODE_UPDATE);
                this.finish();
                break;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        chargePresenter.onRequestPermissionsResult(requestCode,permissions,grantResults);
        if(!chargePresenter.isResponse())
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }
}
