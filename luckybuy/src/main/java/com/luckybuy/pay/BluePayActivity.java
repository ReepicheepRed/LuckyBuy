package com.luckybuy.pay;

import android.Manifest;
import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.Toast;

import com.bluepay.data.Config;
import com.bluepay.interfaceClass.BlueInitCallback;
import com.bluepay.pay.BlueMessage;
import com.bluepay.pay.BluePay;
import com.bluepay.pay.Client;
import com.bluepay.pay.ClientHelper;
import com.bluepay.pay.IPayCallback;
import com.bluepay.pay.LoginResult;
import com.bluepay.pay.PublisherCode;

import java.io.File;
import java.util.UUID;


public class BluePayActivity extends Activity {

	private String TAG = "BluePayDemo";
	static StringBuilder stateString = null;
	private ProgressDialog mProgressDialog;
	// 交易id，这是默认值，可以使用自己生成的id，但是不要超过20位。可以通过ClientHelper.generateId()生成
	static String billingID = "adbsexsgweasrgds";

	static String userID = "useridbluepayaaaabbbb";

	static final int EXIT_ID = -1000;
	static final int AD_ERROR = 1;
	protected static final int REQUEST_CODE_ASK_CALL_PHONE = 100026;

	public static String fila = Environment.getExternalStorageDirectory()
			+ File.separator + "cert/dbbillkey";
	private BluePay mBluePay;

	/**
	 * Called when the activity is first created.
	 */
	@TargetApi(Build.VERSION_CODES.GINGERBREAD)
	@SuppressLint("NewApi")
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		requestWindowFeature(Window.FEATURE_NO_TITLE);
		

		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);
		//初始化View
		initView();
		/**
		 * 初始化SDK，在使用接口前，你需要调用该方法，并且初始化成功。 在UI线程下使用 如果初始化失败，请检查以下几样配置： 权限
		 * ref文件与best平台配置是否一致 网络
		 * */
		
		
		// 特别关注!!
		// 特别关注!!
		// 特别关注!!
		mBluePay = BluePay.getInstance();
		if (Build.VERSION.SDK_INT >= 23) {// 如果是android 6.0 需要特殊处理
			int readPhoneState = BluePayActivity.this
					.checkSelfPermission(Manifest.permission.READ_PHONE_STATE);
			int sendSms = BluePayActivity.this
					.checkSelfPermission(Manifest.permission.SEND_SMS);
			if (readPhoneState != PackageManager.PERMISSION_GRANTED
					) {
				// 如果没有授权，则初始化的代码放在授权成功那里。否则会初始化失败。
				BluePayActivity.this.requestPermissions(new String[] {
						Manifest.permission.READ_PHONE_STATE},
						REQUEST_CODE_ASK_CALL_PHONE);
			} else{
				initBlueSDK();
			}
			if( sendSms != PackageManager.PERMISSION_GRANTED)
			{
				// 如果没有授权，短代会计费失败，请主动请求权限
				BluePayActivity.this.requestPermissions(new String[] {
						Manifest.permission.SEND_SMS },
						REQUEST_CODE_ASK_CALL_PHONE);
			}
		}else {
			initBlueSDK();
			
		}
		
		
		


	}
	
	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		// TODO Auto-generated method stub
		super.onConfigurationChanged(newConfig);
	}

	private void initView() {
		mProgressDialog = new ProgressDialog(this);
		mProgressDialog.setCanceledOnTouchOutside(false);
		mProgressDialog.setCancelable(false);
		mProgressDialog.setIndeterminate(true);
		ScrollView contentView = new ScrollView(
				BluePayActivity.this);
		contentView.setLayoutParams(new LayoutParams(
				ScrollView.LayoutParams.MATCH_PARENT,
				ScrollView.LayoutParams.MATCH_PARENT));

		LinearLayout root = new LinearLayout(
				BluePayActivity.this);
		contentView.addView(root);
		showMainView(root);
		setContentView(contentView);
	}

	@Override
	public void onResume() {
		super.onResume();
	}

	@Override
	public void finish() {
		// TODO Auto-generated method stub
		Client.exit();
		super.finish();
	}

	/**
	 * 退出游戏、或者退出支付页面，需要调用Client.exit();方法释放资源
	 *
	 * */
	public void showExitDialog() {
		// 寮规纭鏄惁锟�锟斤拷
		Builder builder = new Builder(BluePayActivity.this);
		builder.setMessage("Are you Exit this app?");
		builder.setTitle("tips");
		builder.setPositiveButton("OK", new OnClickListener() {

			public void onClick(DialogInterface dialog, int which) {
				BluePayActivity.this.finish();
				dialog.dismiss();
				System.exit(0);
			}
		});

		builder.setNegativeButton("Cancel", new OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
			}
		});
		builder.create().show();
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			// 锟�锟斤拷瀵硅瘽锟�
			showExitDialog();
			return true;
		}

		return super.onKeyDown(keyCode, event);
	}

	/**
	 * 鏄剧ず渚嬪瓙鐣岄潰
	 *
	 * @param root
	 */
	public void showMainView(LinearLayout root) {

		root.setLayoutParams(new FrameLayout.LayoutParams(
				LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
		root.setBackgroundColor(Color.argb(100, 255, 255, 255));
		root.setOrientation(LinearLayout.VERTICAL);

		// BankCharge

		// unipin

		/*
		 * payByAD
		 */

		makeBankButton(root);
		makeSMSButton(BluePayActivity.this, root);
		makeBlueCoinButton(root);
		// 12call
		make12CallButton(root);

		// trueMoney cashcard
		makeTrueMoneyButton(root);
		makeHappyButton(root);
		makeWalletButton(root);
		/***
		 * queryTrans for payByWallet。 LINEPay，如果你需要在前端获取支付结果，则需要使用该方法查询支付状态。
		 */
		makeLineQueryTrans(root);
		makeSchemeTest(root);
		makeDeleveryView(root);

		// // viettel cashcard 越南
		makeViettelButton(root);
		//
		// // vinafone cashcard 越南
		makeVinafoneButton(root);
		//
		// // mobifone cashcard 越南
		makeMobifoneButton(root);
		makeVTCButton(root);

		makeDeleveryView(root);

		// telkonsel voucher
		makeSMSForTelkomsel(root);
		// makeUnipinButton(root);
		makeMogPlayButton(root);
		// 短代测试，只支持泰国和国内测试

		// payByOffline 印尼。该接口需要用户到OTC或者ATM完成支付
		makeOfflineATMButton(root);
		makeOfflineOTCButton(root);
		makeOfflineQueryTrans(root);
		makeDeleveryView(root);
		/***
		 * payByUI，BluePay 为您提供的UI页面，里面支持所有的支付方式。在各个国家测试会显示不同的支付方式
		 */
		makeUIButton(root);
		makeUINoPirceButton(root);
		
		
		/**
		 * payByWallet 泰国 当前只有LINEPay接入。
		 */

	}

	private void makeDeleveryView(LinearLayout root) {
		// TODO Auto-generated method stub
		View view = new View(BluePayActivity.this);
		LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
				LayoutParams.MATCH_PARENT, 10);
		params.leftMargin = 10;
		params.rightMargin = 10;
		view.setLayoutParams(params);
		view.setBackgroundColor(Color.BLUE);
		root.addView(view);
	}

	private void makeWalletButton(LinearLayout root) {
		Button button = new Button(this);
		button.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT,
				LayoutParams.WRAP_CONTENT));
		button.setText("Pay by LINEPay(泰国)");
		button.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				billingID = ClientHelper.generateTid();
				lineT_id = billingID;
				userID = UUID.randomUUID().toString();
				/***
				 * 为了更好的体验scheme请确保独一无二。 "blue://pay" 对应LinePayActivity
				 * 的scheme，请参考manifest。
				 */
				mBluePay.payByWallet(BluePayActivity.this, userID, billingID,
						Config.K_CURRENCY_THB, "100", "Red Diamond",
						PublisherCode.PUBLISHER_LINE,
						"bluepay://best.bluepay.asia", true, callback);
			}
		});
		root.addView(button);

	}

	/***
	 * 银行支付的测试方法。只支持泰国
	 * 
	 * @param root
	 */
	public void makeBankButton(LinearLayout root) {

		Button button = new Button(this);
		button.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT,
				LayoutParams.WRAP_CONTENT));
		button.setText("Pay by Bank(泰国)");
		button.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {

				// BluePay<p>: 浣跨敤SDK鐨刄I锟�璁剧疆UI鏄剧ず妯″紡涓烘í锟�
				billingID = ClientHelper.generateTid();

				userID = UUID.randomUUID().toString();
				if (userID.length() > 10)
					userID = userID.substring(0, 10);
				mBluePay.payByBank(BluePayActivity.this, billingID, "THB", "500",
						"PropsName", false, callback);

			}
		});
		root.addView(button);
	}

	/**
	 * happy 泰国 的cashcard
	 * 
	 * @param root
	 */
	private void makeHappyButton(LinearLayout root) {

		Button button = new Button(this);
		button.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT,
				LayoutParams.WRAP_CONTENT));
		button.setText("Pay by Happy(泰国)");
		button.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {

				billingID = ClientHelper.generateTid();

				userID = UUID.randomUUID().toString();
				if (userID.length() > 10)
					userID = userID.substring(0, 10);
				// mBluePay.PA
				mBluePay.payByCashcard(BluePayActivity.this, userID + "",
						billingID + "", "PropsName",
						PublisherCode.PUBLISHER_HAPPY, "", null, callback);

			}
		});

		root.addView(button);
	}

	/**
	 * uinipin 印尼的cashcard
	 * 
	 * @param root
	 */
	/*
	 * private void makeUnipinButton(LinearLayout root) {
	 * 
	 * Button button = new Button(this); button.setLayoutParams(new
	 * LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
	 * button.setText("Pay by Unipin(印尼)"); button.setOnClickListener(new
	 * View.OnClickListener() {
	 * 
	 * @Override public void onClick(View v) {
	 * 
	 * BluePay.setLandscape(false);
	 * 
	 * BluePay.setShowCardLoading(true);
	 * 
	 * billingID = ClientHelper.generateTid();
	 * 
	 * userID = UUID.randomUUID().toString(); if (userID.length() > 10) userID =
	 * userID.substring(0, 10);
	 * 
	 * mBluePay.payByCashcard(BluePayActivity.this, userID + "", billingID + "",
	 * "PropsName", PublisherCode.PUBLISHER_UNIPIN, null, null, callback); } });
	 * 
	 * root.addView(button); }
	 */
	/**
	 * uinipin 印尼的cashcard
	 * 
	 * @param root
	 */
	private void makeMogPlayButton(LinearLayout root) {

		Button button = new Button(this);
		button.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT,
				LayoutParams.WRAP_CONTENT));
		button.setText("Pay by MogPlay(印尼)");
		button.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {

				BluePay.setLandscape(false);

				BluePay.setShowCardLoading(true);

				billingID = ClientHelper.generateTid();

				userID = UUID.randomUUID().toString();
				if (userID.length() > 10)
					userID = userID.substring(0, 10);

				mBluePay.payByCashcard(BluePayActivity.this, userID + "",
						billingID + "", "PropsName",
						PublisherCode.PUBLISHER_MOGPLAY, null, null, callback);

			}
		});

		root.addView(button);
	}

	/**
	 * vinaphone鏀粯鏂瑰紡鐨勮皟鐢ㄤ緥锟� *
	 * 
	 * @param root
	 */
	private void makeViettelButton(LinearLayout root) {

		Button button = new Button(this);
		button.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT,
				LayoutParams.WRAP_CONTENT));
		button.setText("Pay by viettel(越南)");
		button.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {

				// BluePay<p>: 浣跨敤SDK鐨刄I锟�璁剧疆UI鏄剧ず妯″紡涓烘í锟�
				// BluePay.setLandscape(false);

				BluePay.setShowCardLoading(true);

				billingID = ClientHelper.generateTid();

				userID = UUID.randomUUID().toString();
				if (userID.length() > 10)
					userID = userID.substring(0, 10);

				// BluePay<m>: TrueMoney鐨勪娇鐢ㄥ畬鏁存帴锟� // 锟�
				// 锟�锟斤拷锟�锟斤拷鎺ュ彛涓篢rueMoney鐨勫崱锟� //
				// BluePay.payBy12Call(BluePayActivity.this, handler, userID+"",
				// billingID+"", "PropsName", "");

				mBluePay.payByCashcard(BluePayActivity.this, userID + "",
						billingID + "", "PropsName",
						PublisherCode.PUBLISHER_VIETTEL, null, null, callback);

				// BluePay.payBy12Call(BluePayActivity.this, handler, userID+"",
				// billingID+"", "PropsName", null);
			}
		});

		root.addView(button);
	}

	/**
	 * vinaphone 越南的cashcard
	 * 
	 * @param root
	 */
	private void makeVinafoneButton(LinearLayout root) {

		Button button = new Button(this);
		button.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT,
				LayoutParams.WRAP_CONTENT));
		button.setText("Pay by vinafone(越南)");
		button.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {

				BluePay.setLandscape(false);

				BluePay.setShowCardLoading(true);

				billingID = ClientHelper.generateTid();

				userID = UUID.randomUUID().toString();
				if (userID.length() > 10)
					userID = userID.substring(0, 10);

				mBluePay.payByCashcard(BluePayActivity.this, userID + "",
						billingID + "", "PropsName",
						PublisherCode.PUBLISHER_VINAPHONE, null, null, callback);
			}
		});

		root.addView(button);
	}

	/**
	 * mobifone 越南cashcard
	 * 
	 * @param root
	 */
	private void makeMobifoneButton(LinearLayout root) {

		Button button = new Button(this);
		button.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT,
				LayoutParams.WRAP_CONTENT));
		button.setText("Pay by mobifone(越南)");
		button.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {

				BluePay.setLandscape(false);

				BluePay.setShowCardLoading(true);

				billingID = ClientHelper.generateTid();

				userID = UUID.randomUUID().toString();
				if (userID.length() > 10)
					userID = userID.substring(0, 10);

				mBluePay.payByCashcard(BluePayActivity.this, userID + "",
						billingID + "", "PropsName",
						PublisherCode.PUBLISHER_MOBIFONE, null, null, callback);

				// BluePay.payBy12Call(BluePayActivity.this, handler, userID+"",
				// billingID+"", "PropsName", null);
			}
		});

		root.addView(button);
	}

	/**
	 * 12call泰国cashcard
	 * 
	 * @param root
	 */
	private void make12CallButton(LinearLayout root) {

		Button button = new Button(this);
		button.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT,
				LayoutParams.WRAP_CONTENT));
		button.setText("Pay by 12call(泰国)");
		button.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {

				BluePay.setShowCardLoading(false);// 不使用 sdk loading框

				billingID = ClientHelper.generateTid();

				userID = UUID.randomUUID().toString();
				if (userID.length() > 10)
					userID = userID.substring(0, 10);
				mBluePay.payByCashcard(BluePayActivity.this, userID + "",
						billingID + "", "中文",
						PublisherCode.PUBLISHER_12CALL, null, null, callback);
			}
		});

		root.addView(button);
	}

	/**
	 * trueMoney 泰国cashcard
	 * 
	 * @param root
	 */
	private void makeTrueMoneyButton(LinearLayout root) {

		Button button = new Button(this);
		button.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT,
				LayoutParams.WRAP_CONTENT));
		button.setText("Pay by TrueMoney(泰国)");
		button.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {

				billingID = ClientHelper.generateTid();

				userID = UUID.randomUUID().toString();
				if (userID.length() > 10)
					userID = userID.substring(0, 10);
				mBluePay.payByCashcard(BluePayActivity.this, userID + "",
						billingID + "", "PropsName",
						PublisherCode.PUBLISHER_TRUEMONEY, "", null, callback);
			}
		});

		root.addView(button);
	}

	private void makeVTCButton(LinearLayout root) {

		Button button = new Button(this);
		button.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT,
				LayoutParams.WRAP_CONTENT));
		button.setText("Pay by VTC(越南)");
		button.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {

				billingID = ClientHelper.generateTid();
				BluePay.setShowCardLoading(true);
				BluePay.setLandscape(true);
				userID = UUID.randomUUID().toString();
				if (userID.length() > 10)
					userID = userID.substring(0, 10);
				mBluePay.payByCashcard(BluePayActivity.this, userID + "",
						billingID + "", "PropsName",
						PublisherCode.PUBLISHER_VTC, "", null, callback);
			}
		});

		root.addView(button);
	}
	
	/***
	 * 使用sdk提供的支付页面，在页面里提供所有的支付方式给用户。
	 * 
	 * @param root
	 */
	private void makeUIButton(LinearLayout root) {

		Button button = new Button(this);
		button.setText("pay by UI");
		button.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT,
				LayoutParams.WRAP_CONTENT));
		billingID = ClientHelper.generateTid();
		button.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {

				mBluePay.payByUI(BluePayActivity.this, billingID,
						Config.K_CURRENCY_THB, "customreId", "100",
						"redDiamond", 0, "bluepay://best.bluepay.asia",
						callback);
			};
		});
		root.addView(button);
	}

	private void makeUINoPirceButton(LinearLayout root) {

		Button button = new Button(this);
		button.setText("pay by UI   no price");
		button.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT,
				LayoutParams.WRAP_CONTENT));
		billingID = ClientHelper.generateTid();
		button.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				mBluePay.payByUI(BluePayActivity.this, billingID, "TRF",
						"customreId", "", "redDiamond", 0,
						"bluepay://best.bluepay.asia", callback);
			};
		});
		root.addView(button);
	}

	private void makeSchemeTest(LinearLayout root) {
		Button button = new Button(this);
		button.setText("test scheme for line");
		button.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT,
				LayoutParams.WRAP_CONTENT));
		billingID = ClientHelper.generateTid();
		button.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				try {
					Intent intent = new Intent();
					intent.setData(Uri.parse("bluepay://best.bluepay.asia"));
					intent.setAction(Intent.ACTION_VIEW);
					startActivity(intent);
				} catch (Exception e) {
					Toast.makeText(BluePayActivity.this, "Activity not found",
							Toast.LENGTH_LONG).show();
				}
			};
		});
		root.addView(button);
	}

	private void makeOfflineATMButton(LinearLayout root) {

		Button button = new Button(this);
		button.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT,
				LayoutParams.WRAP_CONTENT));
		button.setText("Offline--ATM(印尼)");
		button.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				offlineId = billingID = ClientHelper.generateTid();
				mBluePay.payByOffline(BluePayActivity.this, billingID, "Customer",
						"IDR", 50000 + "", "hellor", PublisherCode.PUBLISHER_OFFLINE_ATM,"08811234567", true, callback);
			}
		});
		root.addView(button);
	}

	private void makeOfflineOTCButton(LinearLayout root) {

		Button button = new Button(this);
		button.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT,
				LayoutParams.WRAP_CONTENT));
		button.setText("Offline--OTC(印尼)");
		button.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				offlineId = billingID = ClientHelper.generateTid();
				mBluePay.payByOffline(BluePayActivity.this, billingID, "Customer",
						"IDR", 50000 + "", "hellor", PublisherCode.PUBLISHER_OFFLINE_OTC, "08811234567",true, callback);
			}
		});
		root.addView(button);
	}

	/**
	 * sms鏀粯鏂瑰紡鐨勮皟鐢ㄤ緥锟� *
	 * 
	 * @param context
	 * @param handler
	 * @param price
	 * @param root
	 */
	private void makeSMSButton(Context context, LinearLayout root) {

		Button button = new Button(this);
		button.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT,
				LayoutParams.WRAP_CONTENT));

		button.setText("Pay 5 THB(泰国)");

		button.setOnClickListener(new View.OnClickListener() {

			@TargetApi(23)
			@Override
			public void onClick(View v) {

				/***
				 * param0 context,(Activity) param1 transactionId,交易id param2
				 * currency "THB","VND","ID",或者使用Config.K_CURRENCY_TRF param3
				 * price 泰国区域需要*100. 如果currency
				 * 使用Config.K_CURRENCY_TRF，则该参数需要使用best平台上面的计费ID param4 smsId
				 * 没有特殊要求。请使用0 param5 propsName 金币或者钻石等的名称 param6 showDialog
				 * 是否使用sdk提供的对话框,false则不使用 param7 callback 回调接口
				 */
				billingID = ClientHelper.generateTid();

				BluePay bluePay = BluePay.getInstance();
				bluePay.payBySMS(BluePayActivity.this, billingID,
						Config.K_CURRENCY_TRF, "trx4n15j", 0, "测试短信支付", true,
						callback);
			}

		});

		root.addView(button);
	}

	public void onRequestPermissionsResult(int requestCode,
			String[] permissions, int[] grantResults) {
		if (requestCode == REQUEST_CODE_ASK_CALL_PHONE) {
			for (int i = 0;i < permissions.length;i++) {
				String permission = permissions[i];
				if (permission.equals(Manifest.permission.READ_PHONE_STATE) && grantResults[i] == PackageManager.PERMISSION_GRANTED) {
					initBlueSDK();	
				}
				//
				if (permission.equals(Manifest.permission.SEND_SMS) && grantResults[i] == PackageManager.PERMISSION_GRANTED) {
					
				}
			
			}
		}
		

	};

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
			} else if (msg.getCode() == 603) {
				title = "user cancel";// 请求失败
			} else {
				title = "Fail";
			}
			AlertDialog dialog = new Builder(BluePayActivity.this).create();
			dialog.setTitle(title);
			dialog.setMessage(message);
			dialog.setButton(DialogInterface.BUTTON_POSITIVE, "OK",
					new OnClickListener() {

						@Override
						public void onClick(DialogInterface dialog, int which) {

						}
					});
			
			dialog.setCancelable(true);
			dialog.show();
		}

	}

	private void initBlueSDK(){
		mProgressDialog.show();
		Client.init(this, new BlueInitCallback() {

			@Override
			public void initComplete(String loginResult,
					String resultDesc) {
				mProgressDialog.dismiss();
				String result = null;
				try {
					Log.i("BluePay", resultDesc);
					if (loginResult.equals(LoginResult.LOGIN_SUCCESS)) {
					
						BluePay.setLandscape(false);
						BluePay.setShowCardLoading(true);// 该方法设置使用cashcard时是否使用sdk的loading框

						result = "User Login Success!";

					} else if (loginResult
							.equals(LoginResult.LOGIN_FAIL)) {

						result = "User Login Failed!";
					} else {
						StringBuilder sbStr = new StringBuilder(
								"Fail! The code is:")
								.append(loginResult)
								.append(" desc is:").append(resultDesc);
						stateString.append(sbStr.toString());
						result = sbStr.toString();
					}
				} catch (Exception e) {
					result = e.getMessage();
				}

			}
		});
	
	}
	
	/**
	 * 
	 * @param root
	 */
	private void makeBlueCoinButton(LinearLayout root) {

		Button button = new Button(this);
		button.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT,
				LayoutParams.WRAP_CONTENT));
		button.setText(" BlueCoins(泰国)");
		button.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {

				BluePay.setLandscape(false);
				billingID = ClientHelper.generateTid();

				userID = UUID.randomUUID().toString();
				if (userID.length() > 10)
					userID = userID.substring(0, 10);
//				 mBluePay.payByCashcard(BluePayActivity.this, userID + "",
//				 billingID + "", "PropsName",
//				 PublisherCode.PUBLISHER_BLUECOIN, "", null,
//				 callback);

			}
		});
		root.addView(button);
	}

	/**
	 * for LINE
	 * 
	 * @param root
	 */
	private void makeLineQueryTrans(LinearLayout root) {
		Button button = new Button(this);
		button.setText("queryTrans for LINE Pay（泰国）");
		;
		root.addView(button);
		button.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stu
				if (lineT_id != null) {
					mBluePay.queryTrans(BluePayActivity.this, callback, lineT_id,
							PublisherCode.PUBLISHER_LINE, 3);
				} else {
					Toast.makeText(BluePayActivity.this,
							"please call pay by LINE Pay first",
							Toast.LENGTH_SHORT).show();
				}
			}
		});
	}

	private void makeSMSForTelkomsel(LinearLayout root) {
		Button button = new Button(this);
		button.setText(" sms(5000)（印尼）");
		;
		root.addView(button);
		button.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stu
				mBluePay.payBySMS(BluePayActivity.this, billingID,
						Config.K_CURRENCY_THB, "10000", 0,
						"12345678912345678912", true, callback);
			}
		});
	}

	private Object offlineId;

	private void makeOfflineQueryTrans(LinearLayout root) {
		Button button = new Button(this);
		button.setText("queryTrans for  offline（印尼）");
		;
		root.addView(button);
		button.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stu
				if (offlineId != null) {
//					mBluePay.queryTrans(BluePayActivity.this, callback, lineT_id,
//							PublisherCode.PUBLISHER_OFFLINE_ATM, 3);
				} else {
					Toast.makeText(BluePayActivity.this,
							"please call offline  first", Toast.LENGTH_SHORT)
							.show();
				}
			}
		});

	}

}
