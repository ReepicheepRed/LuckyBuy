package com.luckybuy.layout;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.PixelFormat;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.luckybuy.R;
import com.luckybuy.login.LoginUserUtils;
import com.luckybuy.util.Constant;
import com.luckybuy.util.Utility;

/**
 * Created by zhiPeng.S on 2016/7/29.
 */
public class Dialog_FirstEnter extends Dialog {

    private TextView issue;
    private ImageView cancel_iv;
    private Activity activity;
    private View view;

    public Dialog_FirstEnter(Activity context) {
        super(context);
        this.setCancelable(false);
        this.setCanceledOnTouchOutside(false);

        activity = context;
        view = LayoutInflater.from(context).inflate(R.layout.dialog_first_enter,null);
        issue = (TextView) view.findViewById(R.id.first_enter_issue_tv);
        cancel_iv = (ImageView) view.findViewById(R.id.first_enter_cancel_iv);

    }

    public Dialog_FirstEnter(Activity context, View.OnClickListener onClickListener){
        this(context);
        issue.setOnClickListener(onClickListener);
        cancel_iv.setOnClickListener(onClickListener);
    }


    public void showWindow(){
        Window window = this.getWindow();
        window.setBackgroundDrawableResource(android.R.color.transparent);
        //window.setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);
        this.show();
        window.setContentView(view);

        window.setGravity(Gravity.CENTER_HORIZONTAL | Gravity.TOP);

        float density = Utility.obtainDensity(activity);
        WindowManager.LayoutParams lp = window.getAttributes();
        lp.y = (int) (50*density);

        window.setAttributes(lp);
    }


    private WindowManager.LayoutParams Params(){
        WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams();
        layoutParams.gravity = Gravity.TOP | Gravity.LEFT;
        layoutParams.height = WindowManager.LayoutParams.MATCH_PARENT;
        layoutParams.width = WindowManager.LayoutParams.MATCH_PARENT;
        layoutParams.format = PixelFormat.RGBA_8888;
        layoutParams.type = WindowManager.LayoutParams.TYPE_SYSTEM_ALERT;
        layoutParams.flags |= WindowManager.LayoutParams.FLAG_FULLSCREEN;
        return layoutParams;
    }
}
