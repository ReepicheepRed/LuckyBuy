package com.assist.util;

import android.annotation.SuppressLint;
import android.content.Context;
import android.widget.Toast;

/**
 * Created by zhiPeng.S on 2016/11/9.
 */

public class Utility {

    @SuppressLint("InflateParams")
    public static void toastShow(Context context, int resId) {
        Toast toast = Toast.makeText(context, "", Toast.LENGTH_SHORT);
        toast.setText(resId);
        toast.show();
    }

    public static void toastShow(Context context, CharSequence text) {
        Toast toast = Toast.makeText(context, "", Toast.LENGTH_SHORT);
        toast.setText(text);
        toast.show();
    }
}
