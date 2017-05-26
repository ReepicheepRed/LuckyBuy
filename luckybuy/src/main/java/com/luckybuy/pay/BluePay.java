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
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ScrollView;

import com.adjust.sdk.Util;
import com.bluepay.data.Config;
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

public class BluePay {

	
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
	public BluePay(Activity context) {
		this.context = context;
        mBluePay = com.bluepay.pay.BluePay.getInstance();
	}

    public BluePay(Activity context,BluePayInit bluePayInit) {
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
		/***
		 * param0 context,(Activity)
		 * param1 transactionId,交易id
		 * param2 currency  "THB","VND","ID",或者使用Config.K_CURRENCY_TRF
		 * param3 price   泰国区域需要*100. 如果currency 使用Config.K_CURRENCY_TRF，则该参数需要使用best平台上面的计费ID
		 * param4 smsId   没有特殊要求。请使用0
		 * param5 propsName  金币或者钻石等的名称
		 * param6 showDialog  是否使用sdk提供的对话框,false则不使用
		 * param7 callback   回调接口
		 */
        Bundle bundle = intent.getExtras();
        final String order_number = bundle.getString("ordernumber");
        final long amount = bundle.getLong("amount");
		billingID = ClientHelper.generateTid();

        com.bluepay.pay.BluePay bluePay = com.bluepay.pay.BluePay.getInstance();
        bluePay.payBySMS(context, order_number, "THB", String.valueOf(amount*100),
                0, "测试短信支付", false, callback);

//        bluePay.payBySMS(context, billingID, Config.K_CURRENCY_TRF ,
//                "trx4n15j", 0, "测试短信支付", true, callback);
	}

	/***
	 * 银行支付的测试方法。只支持泰国
	 *
	 * @param intent
	 */
	public void payByBank(Intent intent) {
		Bundle bundle = intent.getExtras();
		final String order_number = bundle.getString("ordernumber");
		final long amount = bundle.getLong("amount");
		mBluePay.payByBank(context, order_number, "THB", String.valueOf(amount*100),"PropsName", false, callback);

	}

	public void payByLine(Intent intent){
		Bundle bundle = intent.getExtras();
		final String order_number = bundle.getString("ordernumber");
		final long amount = bundle.getLong("amount");

		billingID = ClientHelper.generateTid();
		lineT_id = billingID;
		userID = UUID.randomUUID().toString();
		/***
		 * 为了更好的体验scheme请确保独一无二。 "blue://pay" 对应LinePayActivity
		 * 的scheme，请参考manifest。
		 */
		mBluePay.payByWallet(context, userID, order_number,
				Config.K_CURRENCY_THB, String.valueOf(amount*100), "Red Diamond",
				PublisherCode.PUBLISHER_LINE,
				"bluepay://best.bluepay.asia", true, callback);
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
				if(loadingDialog != null)
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
