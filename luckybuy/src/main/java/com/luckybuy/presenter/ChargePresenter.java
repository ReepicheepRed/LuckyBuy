package com.luckybuy.presenter;

import android.support.annotation.NonNull;

/**
 * Created by zhiPeng.S on 2016/10/28.
 */

public interface ChargePresenter {
    boolean isResponse();
    void checkPermission();
    void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults);
}
