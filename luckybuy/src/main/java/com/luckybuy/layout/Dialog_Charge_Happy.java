package com.luckybuy.layout;

import android.app.Activity;
import android.app.Dialog;
import android.graphics.PixelFormat;
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
public class Dialog_Charge_Happy extends Dialog {

    private TextView title;
    private ImageView cancel_iv;
    private Button confirmBtn;
    private EditText cardEt, PwdEt;

    private Activity activity;
    private View view;

    public Dialog_Charge_Happy(Activity context) {
        super(context);
        this.setCancelable(false);
        this.setCanceledOnTouchOutside(false);

        activity = context;
        view = LayoutInflater.from(context).inflate(R.layout.dialog_charge_happy,null);
        title = (TextView) view.findViewById(R.id.charge_card_dialog_title);
        cancel_iv = (ImageView) view.findViewById(R.id.charge_card_dialog_cancel_iv);
        confirmBtn = (Button) view.findViewById(R.id.charge_card_dialog_charge_btn);
        cardEt = (EditText) view.findViewById(R.id.charge_card_dialog_card_no_et);
        PwdEt = (EditText) view.findViewById(R.id.charge_card_dialog_card_pwd_et);
    }

    public Dialog_Charge_Happy(Activity context, View.OnClickListener onClickListener){
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


    public TextView getTitle() {
        return title;
    }

    public EditText getCardEt() {
        return cardEt;
    }

    public EditText getPwdEt() {
        return PwdEt;
    }
}
