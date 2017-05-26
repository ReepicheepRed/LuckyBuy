package com.luckybuy.layout;

import android.app.Activity;
import android.app.Dialog;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.luckybuy.R;
import com.luckybuy.util.Utility;

/**
 * Created by zhiPeng.S on 2016/7/29.
 */
public class Dialog_Charge_12Call extends Dialog {

    private TextView title;
    private ImageView cancel_iv, logo ;
    private Button confirmBtn;
    private EditText cardEt;

    private Activity activity;
    private View view;

    public Dialog_Charge_12Call(Activity context) {
        super(context);
        this.setCancelable(false);
        this.setCanceledOnTouchOutside(false);

        activity = context;
        view = LayoutInflater.from(context).inflate(R.layout.dialog_charge_12call,null);
        title = (TextView) view.findViewById(R.id.charge_12call_dialog_title);
        cancel_iv = (ImageView) view.findViewById(R.id.charge_12call_dialog_cancel_iv);
        confirmBtn = (Button) view.findViewById(R.id.charge_12call_dialog_charge_btn);
        cardEt = (EditText) view.findViewById(R.id.charge_12call_dialog_12call_no_et);
        logo = (ImageView) view.findViewById(R.id.charge_12call_dialog_logo);
    }

    public Dialog_Charge_12Call(Activity context, View.OnClickListener onClickListener){
        this(context);
        title.setOnClickListener(onClickListener);
        cancel_iv.setOnClickListener(onClickListener);
        confirmBtn.setOnClickListener(onClickListener);
    }


    public void showWindow(){
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

    private boolean isTrueMoney;

    public boolean isTrueMoney() {
        return isTrueMoney;
    }

    public void setTrueMoney(boolean trueMoney) {
        isTrueMoney = trueMoney;
        if (isTrueMoney()){
            cardEt.setHint("รหัสบัตรเติมเงิน (14 หลัก)");
        }else {
            cardEt.setHint("รหัสบัตรเติมเงิน (16 หลัก)");
        }
    }

    public void setTitle(int resId) {
        title.setText(resId);
    }

    public void setTitle(CharSequence c) {
        title.setText(c);
    }

    public void setLogo(int resId){
        logo.setImageResource(resId);
    }

    public EditText getCardEt() {
        return cardEt;
    }

}
