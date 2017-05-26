package com.luckybuy.pay;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;
import android.widget.Toast;

import com.luckybuy.PayResultActivity;
import com.luckybuy.login.LoginUserUtils;
import com.luckybuy.util.Constant;
import com.luckybuy.util.Utility;
import com.mol.seaplus.CodeList;
import com.mol.seaplus.Language;
import com.mol.seaplus.sdk.MOLSEAPlus;
import com.mol.seaplus.sdk.MOLSEAPlusListener;
import com.mol.seaplus.sdk.ResultHolder;
import com.mol.seaplus.tool.dialog.ToolAlertDialog;

/**
 * Created by zhiPeng.S on 2016/11/7.
 */

public class MolPay implements MOLSEAPlusListener {
    private Activity context;
    private String userId ="0000000001";
    private String serviceId ="4198";
    private String secretKey ="b7592712f8fff903850a251e6a524b23";
    private SharedPreferences preferences;

    private MOLSEAPlus mMolSdk;

    public MolPay(Activity context) {
        this.context = context;
        preferences = LoginUserUtils.getUserSharedPreferences(context);
        userId = String.valueOf(preferences.getLong(Constant.USER_ID,0));
    }

    /**
     *
     * @param ptxId Partner's reference ID
     * @param userId App/Game's user ID
     * @param priceId Easy2Pay price ID
     */
    private void makePurchasePSMS(String ptxId, String userId, String priceId,String serviceId,String secretKey)
    {
        Log.w("Easy2Pay", "Easy2Pay is initializing (" + ptxId + ", " + userId + ", " + priceId + ") ...");
        mMolSdk = new MOLSEAPlus(context, serviceId, secretKey, Language.TH);
        mMolSdk.enableTest(false);
        mMolSdk.purchaseByPSMS(ptxId, userId, priceId, "", "", this);

    }

    public void makePurchasePSMS(String ptxId,String priceId){
        this.makePurchasePSMS("200" + ptxId,userId,priceId,serviceId,secretKey);
    }

    @Override
    public void onRequestError(int errorCode, String error) {
        // TODO Auto-generated method stub
        //ToolAlertDialog.alert(context, "Request fail : " + error);		

    }

    @Override
    public void onRequestEvent(int pEventCode) {
        // TODO Auto-generated method stub
        String message = null;
        switch (pEventCode){
            case CodeList.EVENT_CHARGING:
                message = "Charging";
                break;
            case CodeList.EVENT_CHARGING_IN_BACKGROUND:
                message = "Charging in background";
                break;
        }
        if(message != null)
        {

            Toast.makeText(context,message, Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onUserCancel() {
        // TODO Auto-generated method stub
        ToolAlertDialog.alert(context, "User cancel");
    }

    @Override
    public void onRequestSuccess(MOLSEAPlus.PaymentChannel pPaymentChannel, ResultHolder pResultHolder) {
        // TODO Auto-generated method stub
//        ToolAlertDialog.alert(context, "Request success : ptxId :"+pResultHolder.getPartnerTransactionId()+"\n"
//                +"txid : "+pResultHolder.getTransactionId()+"\n"
//                +"price : "+pResultHolder.getPriceId());
        //}
        try{
        Utility.toastShow(context,"charge success");
        if(Utility.chargeResultForPayment(context)) {
            context.setResult(Constant.RESULT_CODE_UPDATE);
            context.finish();
            return;
        }

        Utility.toastShow(context,"going on pay result");
        Intent intent = new Intent(context, PayResultActivity.class);
        context.startActivity(intent);
        }catch (Exception e){
            Utility.toastShow(context,e.getMessage());
        }
    }
}
