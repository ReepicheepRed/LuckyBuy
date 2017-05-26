package com.luckybuy.presenter.Impl;

import android.Manifest;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import com.luckybuy.pay.BluePayInit;
import com.luckybuy.presenter.ChargePresenter;
import com.luckybuy.util.Constant;
import com.luckybuy.util.Utility;

/**
 * Created by zhiPeng.S on 2016/10/28.
 */

public class ChargePresenterImpl implements ChargePresenter {
    private static final int MY_PERMISSIONS_REQUEST_CALL_PHONE = 1;
    private Activity mContext;
    private BluePayInit bluePayInit;
    private boolean isResponse;
    private AlertDialog.Builder alertDialog;
    private String alertStr = "การเติมเงินของ LuckyBuy จะต้องมีการเข้าถึง Permissions ของแอพ ถ้ายุติการเข้าถึงจะทำให้ไม่สามารถใช้การเติมเงินได้ โปรดอนุญาตให้ LuckyBuy เข้าถึง Permissions ดังกล่าว";
    public ChargePresenterImpl(Activity context, BluePayInit bluePayInit) {
        mContext = context;
        this.bluePayInit = bluePayInit;
        alertDialog = new AlertDialog.Builder(context)
                .setMessage(alertStr)
                .setPositiveButton("อนุญาต",OnClickListener)
                .setNegativeButton("ไม่อนุญาต",OnClickListener);
    }

    public boolean isResponse() {
        return isResponse;
    }

    @Override
    public void checkPermission() {
       if (ContextCompat.checkSelfPermission(mContext,Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED){
//           if (ActivityCompat.shouldShowRequestPermissionRationale(mContext,Manifest.permission.READ_PHONE_STATE)) {
//               Utility.toastShow(mContext,alertStr);
//           } else {
               ActivityCompat.requestPermissions(mContext, new String[]{Manifest.permission.READ_PHONE_STATE}, MY_PERMISSIONS_REQUEST_CALL_PHONE);
//           }
        } else {
           bluePayInit.initBluePaySdk();
       }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == MY_PERMISSIONS_REQUEST_CALL_PHONE)
        {

            if (grantResults[0] == PackageManager.PERMISSION_GRANTED){
                bluePayInit.initBluePaySdk();
            } else{
                alertDialog.show();
                // Permission Denied
//                Utility.toastShow(mContext,"Permission Denied: " + grantResults[0]);
            }
            isResponse = true;
        }
    }

    private DialogInterface.OnClickListener OnClickListener = new DialogInterface.OnClickListener() {
        @Override
        public void onClick(DialogInterface dialog, int which) {
            dialog.dismiss();
            switch (which){
                case -1:
                    checkPermission();
                    break;
                case -2:
                    mContext.setResult(Constant.RESULT_CODE_MINE);
                    mContext.finish();
                    break;
            }
        }
    };
}
