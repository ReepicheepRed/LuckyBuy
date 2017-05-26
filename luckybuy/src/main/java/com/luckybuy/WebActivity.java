package com.luckybuy;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.webkit.JavascriptInterface;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.TextView;


import com.facebook.CallbackManager;
import com.facebook.share.widget.MessageDialog;
import com.facebook.share.widget.ShareDialog;
import com.luckybuy.login.LoginUserUtils;
import com.luckybuy.model.AwardModel;
import com.luckybuy.model.OrderNumModel;
import com.luckybuy.share.FaceBookShare;
import com.luckybuy.util.Constant;
import com.luckybuy.util.Utility;

import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.Event;
import org.xutils.view.annotation.ViewInject;

/**
 * Created by zhiPeng.S on 2016/6/20.
 */
@ContentView(R.layout.activity_web)
public class WebActivity extends BaseActivity {

    //detail page
    private String photo_url =  Constant.getBaseUrl() + "page/good/intro.aspx?idx=";
    private String unveil_url = Constant.getBaseUrl() + "page/good/timespass.aspx?goodid=";
    private String calculate_url = Constant.getBaseUrl() + "Share/LotteryResults.aspx?timesid=";

    //register
    private String protocol_url = Constant.getBaseUrl() + "Html/Help/service.htm";

    //bask page
    private String bask_rule_url = Constant.getBaseUrl() + "Html/Help/singleSun.htm";

    //diamond
    private String diamond_url = Constant.getBaseUrl() + "Share/ExpensesRecord.aspx?uIDX=";

    //call center
    private String faq_url = Constant.getBaseUrl() + "Html/Help/problem.htm";

    private SharedPreferences preferences;

    enum Type{
        DETAIL_PHOTO,DETAIL_UNVEIL,DETAIL_BASK,DETAIL_CALCULATE
    }

    private String generalPaysbuyUrl(String ordernumber, long uidx, long amount){
        String accesstoken = preferences.getString(Constant.ACCESS_TOKEN,"");
        String refreshtoken = preferences.getString(Constant.REFRESH_TOKEN,"");
        return Constant.getBaseUrl() + "/Page/Pay/Paysbuy/Send.aspx?ordernumber=" +
                ordernumber + "&uidx=" + uidx + "&amount=" + amount +
                "&accesstoken" + accesstoken + "&refreshtoken" + refreshtoken;
    }


    private String content_url = "";

    private boolean isPayment;


    @ViewInject(R.id.title_web_activity)
    private TextView title_web;

    @ViewInject(R.id.content_wv)
    private WebView content_web;

    @Event({R.id.back_web_iv,R.id.refresh_web_iv})
    private void viewClick(View view){
        switch(view.getId()){
            case R.id.back_web_iv:
                this.finish();
                break;
            case R.id.refresh_web_iv:
                content_web.loadUrl(content_url);
                break;
        }
    }

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        preferences = LoginUserUtils.getUserSharedPreferences(this);
        initWebView();
        Intent intent = getIntent();
        int content_flag = intent.getIntExtra(Constant.WEB_H5,-1);

        Bundle bundle = intent.getExtras();
        String web_url = "";
        //Detail
        AwardModel model = null;
        OrderNumModel model_order = null;
        if(bundle != null) {
            if(bundle.containsKey("web_url"))
                web_url = bundle.getString("web_url");
            if(bundle.getSerializable("bundle") instanceof AwardModel)
                model = (AwardModel) bundle.getSerializable("bundle");
            if(bundle.getSerializable("bundle") instanceof OrderNumModel)
                model_order = (OrderNumModel) bundle.getSerializable("bundle");
        }
        switch (content_flag){
            case Constant.DIAMOND:
                long user_id = preferences.getLong(Constant.USER_ID,0);
                content_url = diamond_url + user_id;
                break;
            case Constant.BASK_RULE:
                content_url = bask_rule_url;
                break;
            case Constant.DETAIL_PHOTO:
                if (model != null)
                    content_url = photo_url + model.getIdx();
                break;
            case Constant.DETAIL_UNVEIL:
                if (model != null)
                    content_url = unveil_url+ model.getIdx();
                break;
            case Constant.DETAIL_CALCULATE:
                if (model != null)
                    content_url = calculate_url + model.getTimeid();
                break;
            case Constant.NOTIFY:
                content_url = intent.getStringExtra("content");
                break;
            case Constant.BANNER:
                content_url = intent.getStringExtra("link");
                break;
            case Constant.PROTOCOL:
                content_url = protocol_url;
                break;
            case Constant.FAQ:
                content_url = faq_url;
                break;
            case Constant.PAYSBUY:
                isPayment = intent.getBooleanExtra("isPayment",false);
                String orderNum = model_order == null? "" : model_order.getOrdernumber();
                long uidx = model_order == null ? 0 : model_order.getUidx();
                long amount = model_order == null ? 0 : model_order.getAmount();
                content_url = generalPaysbuyUrl(orderNum, uidx, amount);
                break;
            case Constant.WEB_URL:
                content_url = web_url;
                break;
            default:
                content_url = "http://www.baidu.com";
                break;
        }

        content_web.loadUrl(content_url);
        init_Share();
    }


    @SuppressLint({"SetJavaScriptEnabled", "AddJavascriptInterface"})
    private void initWebView(){
        WebSettings webSettings = content_web.getSettings();
        webSettings.setSavePassword(false);
        webSettings.setSaveFormData(false);
        webSettings.setJavaScriptEnabled(true);
        webSettings.setSupportZoom(false);

        WebChromeClient wvcc = new WebChromeClient() {
            @Override
            public void onReceivedTitle(WebView view, String title) {
                super.onReceivedTitle(view, title);
                Log.d("ANDROID_LAB", "TITLE=" + title);
                title_web.setText(title);
            }

        };
        // 设置setWebChromeClient对象
        content_web.setWebChromeClient(wvcc);


        // 创建WebViewClient对象
        WebViewClient wvc = new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                // 使用自己的WebView组件来响应Url加载事件，而不是使用默认浏览器器加载页面
                content_web.loadUrl(url);
                // 消耗掉这个事件。Android中返回True的即到此为止吧,事件就会不会冒泡传递了，我们称之为消耗掉
                return true;
            }
        };
        content_web.setWebViewClient(wvc);

        content_web.addJavascriptInterface(new JsOperation(this), "client");
    }

    class JsOperation {

        Activity mActivity;

        public JsOperation(Activity activity) {
            mActivity = activity;
        }

        @JavascriptInterface
        public void psbPayAction(String status,String payMoney) {
            Log.d("psbPayAction", status + " " + payMoney);

            if(Utility.chargeResultForPayment(isPayment)) {
                WebActivity.this.setResult(Constant.RESULT_CODE_UPDATE);
                WebActivity.this.finish();
                return;
            }
            Intent intent = new Intent(WebActivity.this,PayResultActivity.class);
            startActivity(intent);
        }

        @JavascriptInterface
        public void shareFacebook() {
//            这个地方 弹出facebook分享
//            ฉันกำลังเล่น LuckyBuy อยู่ แค่ 10 บาทก็มีโอกาสถูกรางวัล iPhone 7 แล้ว รีบมาลองเล่นด้วยกันสิ http://api.10bbuy.com/Html/Help/AppDownload.htm
//            Facebook活动分享：我在玩LuckyBuy 只要10泰铢就有机会中得iPhone7哦 快来一起体验吧  图片用Logo

            String appUrl = getString(R.string.app_url_share);
            String content = getString(R.string.facebook_share_web) + appUrl;
            String pictureUrl = Constant.getBaseUrl() + "common/image/10BBUY_logo.png";
            FaceBookShare.share_facebook(content,pictureUrl,appUrl);
        }

        @JavascriptInterface
        public void chargeDiscount(){
            Intent intent = new Intent(WebActivity.this,ChargeActivity.class);
            startActivity(intent);
        }
    }


    private void init_Share(){
        FaceBookShare.callbackManager = CallbackManager.Factory.create();
        FaceBookShare.shareDialog = new ShareDialog(this);
        FaceBookShare.shareDialog.registerCallback(FaceBookShare.callbackManager, FaceBookShare.facebookCallback_share);
    }



    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        FaceBookShare.callbackManager.onActivityResult(requestCode, resultCode, data);
    }
}
