package com.luckybuy.layout;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.PixelFormat;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.luckybuy.R;

/**
 * Created by zhiPeng.S on 2016/7/29.
 */
public class PrizeDialog extends Dialog {

    private TextView issue,title;
    private ImageView cancel_iv;

    public PrizeDialog(Context context) {
        super(context);
        this.setCancelable(false);
        this.setCanceledOnTouchOutside(false);

        View view = LayoutInflater.from(context).inflate(R.layout.popwin_win_prize,null);
        issue = (TextView) view.findViewById(R.id.win_prize_issue_tv);
        title = (TextView) view.findViewById(R.id.win_prize_title_tv);
        cancel_iv = (ImageView) view.findViewById(R.id.win_prize_cancel_iv);
        cancel_iv.setOnClickListener(onClickListener);

        Window window = this.getWindow();
        window.setBackgroundDrawableResource(android.R.color.transparent);
        window.setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.show();
        window.setContentView(view);
    }

    private View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            PrizeDialog.this.dismiss();
        }
    };

    public TextView getIssue() {
        return issue;
    }

    public TextView getTitle() {
        return title;
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
