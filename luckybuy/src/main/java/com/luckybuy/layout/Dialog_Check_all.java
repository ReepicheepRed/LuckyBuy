package com.luckybuy.layout;

import android.app.Dialog;
import android.content.Context;
import android.provider.ContactsContract;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import com.luckybuy.R;

/**
 * Created by zhiPeng.S on 2016/8/5.
 */
public class Dialog_Check_all extends Dialog{

    private TextView title;
    private ImageView cancel;
    private GridView content;

    public Dialog_Check_all(Context context) {
        super(context);
        this.setCancelable(false);
        this.setCanceledOnTouchOutside(false);
        this.show();

        Window window = this.getWindow();
        window.setBackgroundDrawableResource(android.R.color.transparent);
        window.setContentView(R.layout.dialog_checkall);

        title = (TextView) window.findViewById(R.id.check_all_title);
        cancel = (ImageView) window.findViewById(R.id.check_all_cancel);
        content = (GridView) window.findViewById(R.id.check_all_gv);

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Dialog_Check_all.this.dismiss();
            }
        });
    }

    public Dialog_Check_all(Context context, int themeResId) {
        super(context, themeResId);
    }

    protected Dialog_Check_all(Context context, boolean cancelable, OnCancelListener cancelListener) {
        super(context, cancelable, cancelListener);
    }

    public TextView getTitle() {
        return title;
    }

    public GridView getContent() {
        return content;
    }
}
