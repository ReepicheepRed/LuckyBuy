package com.luckybuy.layout;

import android.app.Activity;
import android.app.Dialog;
import android.content.res.Resources;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.luckybuy.R;
import com.luckybuy.util.Utility;

/**
 * Created by zhiPeng.S on 2016/7/29.
 */
public class Dialog_Charge_PaysBuy extends Dialog {

    private TextView title;
    private ImageView cancel_iv;
    private Button confirmBtn;
    private EditText paysbuyEt, PwdEt;

    private Activity activity;
    private View view;

    private TextView  valueOneTextView;
    private TextView  valueTwoTextView;
    private TextView  valueThreeTextView;
    private TextView  valueFourTextView;
    private TextView  valueFiveTextView;
    private TextView  valueSixTextView;
    private TextView  valueSevenTextView;


    //上次选中的Textview
    private View  previousTextView;

    public Dialog_Charge_PaysBuy(Activity context) {
        super(context);
        this.setCancelable(false);
        this.setCanceledOnTouchOutside(false);

        activity = context;
        view = LayoutInflater.from(context).inflate(R.layout.dialog_charge_paysbuy,null);
        title = (TextView) view.findViewById(R.id.charge_paysbuy_dialog_title);
        cancel_iv = (ImageView) view.findViewById(R.id.charge_paysbuy_dialog_cancel_iv);
        confirmBtn = (Button) view.findViewById(R.id.charge_paysbuy_dialog_charge_btn);

        valueOneTextView = (TextView) view.findViewById(R.id.money_tv_0);
        valueTwoTextView = (TextView) view.findViewById(R.id.money_tv_1);
        valueThreeTextView = (TextView) view.findViewById(R.id.money_tv_2);
        valueFourTextView = (TextView) view.findViewById(R.id.money_tv_3);
        valueFiveTextView = (TextView) view.findViewById(R.id.money_tv_4);
        valueSixTextView = (TextView) view.findViewById(R.id.money_tv_5);
        valueSevenTextView = (TextView) view.findViewById(R.id.money_tv_6);

    }

    public Dialog_Charge_PaysBuy(Activity context, View.OnClickListener onClickListener){
        this(context);
        title.setOnClickListener(onClickListener);
        cancel_iv.setOnClickListener(onClickListener);
        confirmBtn.setOnClickListener(onClickListener);
    }

    public void showWindow(){

        this.setMoney(0);
        if (previousTextView!=null){
            previousTextView.setBackground(activity.getResources().getDrawable(R.drawable.jine_btn_default));
        }

        Window window = this.getWindow();
        window.setBackgroundDrawableResource(android.R.color.transparent);
//        window.setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);
        this.show();
        window.setContentView(view);
        window.setGravity(Gravity.CENTER_HORIZONTAL | Gravity.TOP);
        float density = Utility.obtainDensity(activity);
        WindowManager.LayoutParams lp = window.getAttributes();
        lp.y = (int) (50*density);
        window.setAttributes(lp);
    }
    private String orderNum;
    private long money;

    public String getOrderNum() {
        return orderNum;
    }

    public void setOrderNum(String orderNum) {
        this.orderNum = orderNum;
    }

    public long getMoney(){
        return money;
    }

    public void setMoney(long money) {
        this.money = money;
    }

//    private boolean isPaypal;
//
//    public boolean isPaypal() {
//        return isPaypal;
//    }
//
//    public void setPaypal(boolean paypal) {
//        isPaypal = paypal;
//    }
//
//    private boolean isSMS;
//
//    public boolean isSMS() {
//        return isSMS;
//    }
//
//    public void setSMS(boolean SMS) {
//        isSMS = SMS;
//
//    }


    private int method;

    public int getMethod() {
        return method;
    }

    public void setMethod(int method) {
        this.method = method;
        selectShowMoney(method);
    }

    public static final int sms = 0, paypal = 1, paysbuy = 2, bank = 3, linePay = 4;
    private void selectShowMoney(int method){
        switch (method){
            case sms:
                //            1.短信 SMS  10  20  30  50  100 150 200 250
//                valueOneTextView.setText("10");
//                valueTwoTextView.setText("20");
//                valueThreeTextView.setText("30");
//                valueFourTextView.setText("50");
//                valueFiveTextView.setText("100");
//                valueSixTextView.setText("200");
//                valueSevenTextView.setText("250");
//            1.短信mol SMS  10,30,50,60,90,100,150,200,300,500
                valueOneTextView.setText("10");
                valueTwoTextView.setText("50");
                valueThreeTextView.setText("90");
                valueFourTextView.setText("150");
                valueFiveTextView.setText("200");
                valueSixTextView.setText("300");
                valueSevenTextView.setText("500");
                break;
            case paypal:
            case paysbuy:
            case linePay:
                //            50 100 200 500 1000 2000 5000
                valueOneTextView.setText("50");
                valueTwoTextView.setText("100");
                valueThreeTextView.setText("200");
                valueFourTextView.setText("500");
                valueFiveTextView.setText("1000");
                valueSixTextView.setText("2000");
                valueSevenTextView.setText("5000");
                break;
            case bank:
                valueOneTextView.setText("200");
                valueTwoTextView.setText("300");
                valueThreeTextView.setText("500");
                valueFourTextView.setText("800");
                valueFiveTextView.setText("1000");
                valueSixTextView.setText("2000");
                valueSevenTextView.setText("5000");
                break;
        }
    }

    public void setTitle(int resId) {
        title.setText(resId);
    }

    public void setTitle(CharSequence c) {
        title.setText(c);
    }
    public  void setViewSelected(View view){
        if (previousTextView!=null){
            previousTextView.setBackground(activity.getResources().getDrawable(R.drawable.jine_btn_default));
        }
        previousTextView = view;
        previousTextView.setBackground(activity.getResources().getDrawable(R.drawable.jine_btn_selected));
    }
}
