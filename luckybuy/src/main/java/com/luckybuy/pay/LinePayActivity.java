package com.luckybuy.pay;

import android.R.integer;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.ViewGroup.LayoutParams;
import android.webkit.WebView;
import android.widget.TextView;

import com.bluepay.interfaceClass.BlueInitCallback;
import com.bluepay.pay.BlueMessage;
import com.bluepay.pay.BluePay;
import com.bluepay.pay.Client;
import com.bluepay.pay.IPayCallback;
import com.bluepay.pay.PublisherCode;


public class LinePayActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		TextView tv = new TextView(this);
		LayoutParams params = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
		tv.setLayoutParams(params);
		tv.setText("this page is the one which send the payment request.");
		
		setContentView(tv);
		
		
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		super.onActivityResult(requestCode, resultCode, data);
		
	}

	@Override
	protected void onNewIntent(Intent intent) {
		// TODO Auto-generated method stub
		super.onNewIntent(intent);
		Uri uri = getIntent().getData();
		
		if(null != uri && uri.getScheme().equals(""/*your scheme*/)){
			String t_id = com.luckybuy.pay.BluePay.billingID;
			BluePay.getInstance().queryTrans(LinePayActivity.this, new IPayCallback() {
				
				@Override
				public void onFinished( BlueMessage msg) {
					AlertDialog dialog = new AlertDialog.Builder(LinePayActivity.this).create();
					String title;
					if (msg.getCode() == 200) {
						title = "Success";
					}else {
						title = "Fail";
					}
					String msgStr = "Code:"+ msg.getCode()+ " price:"+msg.getPrice() ;
					dialog.setTitle(title);
					dialog.setMessage(msgStr);
					dialog.show();
				}
			}, t_id, PublisherCode.PUBLISHER_LINE, 4);
		}
		
	}
	
	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		
		
	}
	
	@Override
	public void onBackPressed() {
		super.onBackPressed();
		
		// Util.CheckSMSResult(mOrder, mOrder.getTransactionId(), 6);
	}
}
