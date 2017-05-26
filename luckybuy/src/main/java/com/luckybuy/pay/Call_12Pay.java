package com.luckybuy.pay;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.text.TextUtils;
import android.util.Log;

import com.bluepay.interfaceClass.BlueInitCallback;
import com.bluepay.pay.BlueMessage;
import com.bluepay.pay.Client;
import com.bluepay.pay.ClientHelper;
import com.bluepay.pay.IPayCallback;
import com.bluepay.pay.LoginResult;
import com.bluepay.pay.PublisherCode;
import com.luckybuy.PayResultActivity;
import com.luckybuy.layout.LoadingDialog;
import com.luckybuy.util.Constant;
import com.luckybuy.util.Utility;

import org.xutils.x;

import java.io.File;
import java.util.UUID;

public class Call_12Pay {


	private String TAG = "BluePayDemo";
	static StringBuilder stateString = null;

	// 交易id，这是默认值，可以使用自己生成的id，但是不要超过20位。可以通过ClientHelper.generateId()生成
	static String billingID = "adbsexsgweasrgds";

	static String userID = "useridbluepayaaaabbbb";


	static final int EXIT_ID = -1000;
	static final int AD_ERROR = 1;

	public static String fila = Environment.getExternalStorageDirectory()
			+ File.separator + "cert/dbbillkey";
	private com.bluepay.pay.BluePay mBluePay;

	private Activity context;
    private BluePayInit bluePayInit;
	public Call_12Pay(Activity context) {
		this.context = context;
        mBluePay = com.bluepay.pay.BluePay.getInstance();
	}

    public Call_12Pay(Activity context,BluePayInit bluePayInit ) {
        this(context);
        this.bluePayInit = bluePayInit;
    }

    private LoadingDialog loadingDialog;
	public void pay_5_THB(Intent intent){
        if(!bluePayInit.isInitComplete()) {
            Utility.toastShow(x.app(), " Init not complete");
            return;
        }

		loadingDialog = new LoadingDialog(context);
		loadingDialog.showDialog();

        Bundle bundle = intent.getExtras();
		String order_number = bundle.getString("ordernumber");
        String cardNum = bundle.getString("cardNum");
		billingID = ClientHelper.generateTid();

		userID = UUID.randomUUID().toString();
		if (userID.length() > 10)
			userID = userID.substring(0, 10);

		mBluePay.payByCashcard(context, userID + "",
				order_number, "中文",
				PublisherCode.PUBLISHER_12CALL, cardNum, null, callback);
	}



	PayCallback callback = new PayCallback();

	protected String lineT_id;

	class PayCallback extends IPayCallback {

		@Override
		public void onFinished(BlueMessage msg) {
			Log.i(TAG, " message:" + msg.getDesc() + " code :" + msg.getCode()
					+ " prop's name:" + msg.getPropsName());
			String message = "result code:" + msg.getCode() + " message:"
					+ msg.getDesc() + " code :" + msg.getCode() + "   price:"
					+ msg.getPrice() + " Payment channel:" + msg.getPublisher();

			if (!TextUtils.isEmpty(msg.getOfflinePaymentCode())) {// offline
				// payment
				// code
				// 不为空，说明这个是印尼的offline，可以展示paymentCode给用户
				message += ", " + msg.getOfflinePaymentCode()
						+ ". please go to " + msg.getPublisher()
						+ " to finish this payment";
			}
			String title = "";

			if (msg.getCode() == 200 || msg.getCode() == 201) {
				// request success 不代表计费成功，代表请求成功。（因为有些接口是异步的）计费是否成功，通过getCode
				// 的返回值来判断
				title = " Success";

                loadingDialog.dismiss();
				if(Utility.chargeResultForPayment(context)) {
					context.setResult(Constant.RESULT_CODE_UPDATE);
					context.finish();
					return;
				}
                Intent intent = new Intent(context, PayResultActivity.class);
                context.startActivity(intent);
			} else if (msg.getCode() == 603) {
				title = "user cancel";// 请求失败
			} else {
				title = "Fail";
			}
			AlertDialog dialog = new Builder(context).create();
			dialog.setTitle(title);
			dialog.setMessage(message);
			dialog.setButton(DialogInterface.BUTTON_POSITIVE, "OK",
					new OnClickListener() {

						@Override
						public void onClick(DialogInterface dialog, int which) {

						}
					});

			dialog.setCancelable(true);
			if(!(msg.getCode() == 200 || msg.getCode() == 201))
				dialog.show();
		}

	}


}
