package com.luckybuy;

import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Message;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.text.style.TextAppearanceSpan;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.share.Sharer;
import com.facebook.share.model.ShareLinkContent;
import com.facebook.share.widget.MessageDialog;
import com.facebook.share.widget.ShareDialog;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshScrollView;
import com.luckybuy.adapter.BannerAdapter;
import com.luckybuy.adapter.DetailHistoryAdapter;
import com.luckybuy.adapter.ListBaseAdapter;
import com.luckybuy.db.DB_Config;
import com.luckybuy.layout.BadgeView;
import com.luckybuy.view.CircleFlowIndicator;
import com.luckybuy.layout.Dialog_Check_all;
import com.luckybuy.view.ViewFlow;
import com.luckybuy.login.LoginUserUtils;
import com.luckybuy.model.AwardModel;
import com.luckybuy.model.BannerModel;
import com.luckybuy.model.DetailHistoryModel;
import com.luckybuy.model.DetailUnveilModel;
import com.luckybuy.model.DetailWaitModel;
import com.luckybuy.model.FriendsModel;
import com.luckybuy.network.ParseData;
import com.luckybuy.share.ShareUtils;
import com.luckybuy.util.Constant;
import com.luckybuy.util.StringUtil;
import com.luckybuy.util.Utility;
import org.json.JSONException;
import org.xutils.DbManager;
import org.xutils.common.Callback;
import org.xutils.common.util.DensityUtil;
import org.xutils.common.util.KeyValue;
import org.xutils.db.sqlite.WhereBuilder;
import org.xutils.ex.DbException;
import org.xutils.ex.HttpException;
import org.xutils.http.RequestParams;
import org.xutils.image.ImageOptions;
import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.Event;
import org.xutils.view.annotation.ViewInject;
import org.xutils.x;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by zhiPeng.S on 2016/6/8.
 */
@ContentView(R.layout.activity_detail)
public class DetailActivity extends BaseActivity implements TextView.OnEditorActionListener,AdapterView.OnItemClickListener{

    private SharedPreferences preferences;
    private DbManager db;


    Handler mHandler = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            @SuppressWarnings("unchecked")
            Map<String, Object> result = (Map<String, Object>) msg.obj;
            switch (msg.what) {
                case R.id.DETAIL_HISTORY:
                    if(result == null) return;
                    if (!result.isEmpty()) {
                        @SuppressWarnings("unchecked")
                        List<DetailHistoryModel> showlist =
                                (List<DetailHistoryModel>) result.get(Constant.DETAIL_HISTORY_LIST);
                        boolean orientation = (boolean)result.get("pull_up");
                        if(!orientation) datas.clear();

                        int size = showlist == null ? 0 : showlist.size();
                        for (int i = 0; i < size; i++) datas.add(showlist.get(i));
                        adapter.setData(datas);
                        adapter.notifyDataSetChanged();
                    }
                    break;
                case R.id.DETAIL_HEADER:
                    if(result == null) return;
                    if (!result.isEmpty()) {
                        @SuppressWarnings("unchecked")
                        List<BannerModel> showlist =
                                (List<BannerModel>) result.get(Constant.BANNER_LIST);
                        if (!showlist.isEmpty()) {
                            data.clear();
                            for (int i = 0; i < showlist.size(); i++) {
                                data.add(showlist.get(i));
                            }
                        }
                        updateBanner(data);
                        commodityName = result.get(Constant.TITLE) + " " + result.get(Constant.SUBTITLE);
                        award_name_tv.setText(commodityName);
                        String checkin_str = getString(R.string.history_start);
                        String dateStr = (String) result.get(Constant.CHECK_IN_DATE);
                        checkin_str = String.format(checkin_str,Utility.trimDate(dateStr));
                        checkIn_time_tv.setText(checkin_str);
                    }
                    break;
                case R.id.DETAIL_UNVEIL:
                    if(result == null) return;
                    if (!result.isEmpty()) {
                        int status = (int) result.get(Constant.STATUS);
                        long user_id = preferences.getLong(Constant.USER_ID, 0);
                        final int no_login = 0;
                        final int no_participate = 2;
                        final int participate = 1;
                        int userStatus;
                        ForegroundColorSpan fcSpan_red = StringUtil.fcSpan(R.color.light_red);
                        switch (status) {
                            case 0:
                                //Commodity Going
                                showUnveilView(0);
                                showBottomBtn(true);
                                @SuppressWarnings("unchecked")
                                List<DetailWaitModel> mList = (List<DetailWaitModel>) result.get(Constant.DETAIL_UNVEIL_SOON);
                                DetailWaitModel dw_model = mList.get(0);
                                String totalStr = getString(R.string.total_demand);
                                totalStr = String.format(totalStr,dw_model.getTotal());
                                String timeIdStr = getString(R.string.issue);
                                timeIdStr = String.format(timeIdStr,dw_model.getTimeid());
                                String surplur = getString(R.string.detail_surplus);
                                String surplusStr = String.format(surplur,dw_model.getTotal() - dw_model.getSaled());
                                double progress_pri = Double.valueOf(dw_model.getSaled())/Double.valueOf(dw_model.getTotal());
                                int progress = (int) (progress_pri*100);
                                demand_tv.setText(totalStr);
                                issue_tv.setText(timeIdStr);
                                surplus_tv.setText(surplusStr);
                                detail_pb.setProgress(progress);

                                //user status
                                userStatus = user_id==0 ? no_login : (dw_model.getBuycopies() == 0 ? no_participate : participate);
                                switch (userStatus) {
                                    case no_login:
                                        tip_state.setText(getResources().getString(R.string.no_login));
                                        showStateView(no_login);
                                        break;
                                    case no_participate:
                                        showStateView(no_login);
                                        break;
                                    case participate:
                                        showStateView(participate);

                                        long count_ass = dw_model.getBuycopies();
                                        String countStr = getString(R.string.buyNumber,count_ass) ;
                                        SpannableStringBuilder builder_num = StringUtil.singleSpan(countStr,count_ass + "",fcSpan_red);
                                        count_tv.setText(builder_num);
                                        List<String> luckyId = new ArrayList<>();
                                        String seizeStr = getResources().getString(R.string.seize_number);
                                        luckyId.add(seizeStr);

                                        buyid = dw_model.getBuyid().split(",");
                                        for (int i = 0; i < buyid.length; i++) {
                                            luckyId.add(buyid[i]);
                                        }

                                        if(luckyId.size() > 10) detail_check_all_tv.setVisibility(View.VISIBLE);
                                        else detail_check_all_tv.setVisibility(View.GONE);

                                        @SuppressWarnings("unchecked")
                                        ArrayAdapter adapter = new ArrayAdapter(DetailActivity.this, R.layout.item_luckyid,
                                                luckyId);
                                        number_gv.setAdapter(adapter);
                                        break;
                                }
                                break;
                            case 1:
                                //Commodity Count Down
                                showUnveilView(1);
                                showBottomBtn(false);
                                @SuppressWarnings("unchecked")
                                List<DetailUnveilModel> mList_unveil = (List<DetailUnveilModel>) result.get(Constant.DETAIL_UNVEIL_ING);
                                DetailUnveilModel du_model = mList_unveil.get(0);
                                String cd_timeIdStr = getString(R.string.issue);
                                cd_timeIdStr = String.format(cd_timeIdStr,du_model.getTimeid());
                                cd_issue_tv.setText(cd_timeIdStr);
                                //countdown_tv.setText("");
                                long diff = Long.valueOf(du_model.getLucktime());
                                UnveilCountDownTimer timer = new UnveilCountDownTimer(diff*1000, 1);
                                timer.start();

                                //user status
                                userStatus = user_id==0 ? no_login : (du_model.getBuycopies() == 0 ? no_participate : participate);
                                switch (userStatus) {
                                    case no_login:
                                        tip_state.setText(getResources().getString(R.string.no_login));
                                        showStateView(no_login);
                                        break;
                                    case no_participate:
                                        showStateView(no_login);
                                        break;
                                    case participate:
                                        showStateView(participate);
                                        long count_ass = du_model.getBuycopies();
                                        String countStr = getString(R.string.buyNumber,count_ass) ;
                                        SpannableStringBuilder builder_num = StringUtil.singleSpan(countStr,count_ass+"",fcSpan_red);
                                        count_tv.setText(builder_num);

                                        List<String> luckyId = new ArrayList<>();
                                        String seizeStr = getResources().getString(R.string.seize_number);
                                        luckyId.add(seizeStr);

                                        buyid = du_model.getBuyid().split(",");
                                        for (int i = 0; i < buyid.length; i++) {
                                            luckyId.add(buyid[i]);
                                        }

                                        if(luckyId.size()  > 10) detail_check_all_tv.setVisibility(View.VISIBLE);
                                        else detail_check_all_tv.setVisibility(View.GONE);

                                        @SuppressWarnings("unchecked")
                                        ArrayAdapter adapter = new ArrayAdapter(DetailActivity.this, R.layout.item_luckyid,
                                                luckyId);
                                        number_gv.setAdapter(adapter);
                                        break;
                                    }
                                    break;
                            case 2:
                                //Commodity Unveil
                                        showUnveilView(2);
                                        showBottomBtn(false);
                                        @SuppressWarnings("unchecked")
                                        List<DetailUnveilModel> mList_unveil_end = (List<DetailUnveilModel>) result.get(Constant.DETAIL_UNVEIL_END);
                                        DetailUnveilModel du_model_end = mList_unveil_end.get(0);
                                        ImageOptions imageOptions = new ImageOptions.Builder()
                                                .setSize(DensityUtil.dip2px(50), DensityUtil.dip2px(50))
                                                .setRadius(DensityUtil.dip2px(25))
                                                // 如果ImageView的大小不是定义为wrap_content, 不要crop.
                                                .setCrop(true) // 很多时候设置了合适的scaleType也不需要它.
                                                // 加载中或错误图片的ScaleType
//                                                .setPlaceholderScaleType(ImageView.ScaleType.MATRIX)
//                                                .setImageScaleType(ImageView.ScaleType.CENTER_CROP)
//                                                .setLoadingDrawableId(R.mipmap._jiazai_img)
//                                                .setFailureDrawableId(R.mipmap._jiazai_img)
                                                .build();
                                        x.image().bind(head_iv, du_model_end.getHeadpic(), imageOptions);

                                        ForegroundColorSpan fcSpan_blue = StringUtil.fcSpan(R.color.light_blue);
                                        ForegroundColorSpan fcSpan_3c = StringUtil.fcSpan(R.color.text_3_c);
                                        TextAppearanceSpan textSpan = StringUtil.textSpan(DetailActivity.this,R.style.TextAppearanc_LuckyID);

                                        String nameStr = du_model_end.getNickname();
                                        String winnerStr = getString(R.string.winner,nameStr);
                                        SpannableStringBuilder builder_name = StringUtil.singleSpan(winnerStr,nameStr,fcSpan_blue);
                                        winner_tv.setText(builder_name);

                                        user_id_winner = du_model_end.getUidx();
                                        String userIdStr = getString(R.string.detail_unveil_userId,user_id_winner);
                                        userId_tv.setText(userIdStr);

                                        long issueL = du_model_end.getTimeid();
                                        String unveil_issueStr = getString(R.string.issue,issueL);
                                        SpannableStringBuilder builder_issue = StringUtil.singleSpan(unveil_issueStr,issueL+"",fcSpan_3c);
                                        unveil_issue_tv.setText(builder_issue);

                                        long countL = du_model_end.getCopies();
                                        String part_count_ass = countL+"";
                                        String part_countStr = getString(R.string.detail_unveil_part_count,countL);
                                        SpannableStringBuilder builder_count = StringUtil.singleSpan(part_countStr,part_count_ass,fcSpan_red);
                                        part_count_tv.setText(builder_count);

                                        String dateStr = Utility.trimDate(du_model_end.getLucktime());
                                        String deadlineStr = getString(R.string.detail_unveil_time,dateStr);
                                        SpannableStringBuilder builder_deadline = StringUtil.singleSpan(deadlineStr,dateStr,fcSpan_3c);
                                        deadline_tv.setText(builder_deadline);

                                        long lucky_num = du_model_end.getLuckid();
                                        String lucky_numStr = getString(R.string.lucky_number,lucky_num);
                                        SpannableStringBuilder builder_luckyNum = StringUtil.singleSpan(lucky_numStr,lucky_num+"",textSpan);
                                        lucky_num_tv.setText(builder_luckyNum);

                                        //user status
                                        userStatus = user_id==0 ? no_login : (du_model_end.getBuycopies() == 0 ? no_participate : participate);
                                        switch (userStatus) {
                                            case no_login:
                                                tip_state.setText(getResources().getString(R.string.no_login));
                                                showStateView(no_login);
                                                break;
                                            case no_participate:
                                                showStateView(no_login);
                                                break;
                                            case participate:
                                                showStateView(participate);
                                                long count_ass = du_model_end.getBuycopies();
                                                String countStr = getString(R.string.buyNumber,count_ass) ;
                                                SpannableStringBuilder builder_num = StringUtil.singleSpan(countStr,count_ass + "",fcSpan_red);
                                                count_tv.setText(builder_num);

                                                List<String> luckyId = new ArrayList<>();
                                                String seizeStr = getResources().getString(R.string.seize_number);
                                                luckyId.add(seizeStr);

                                                buyid = du_model_end.getBuyid().split(",");
                                                for (int i = 0; i < buyid.length; i++) {
                                                    luckyId.add(buyid[i]);
                                                }

                                                if(luckyId.size() > 10) detail_check_all_tv.setVisibility(View.VISIBLE);
                                                else detail_check_all_tv.setVisibility(View.GONE);

                                                @SuppressWarnings("unchecked")
                                                ArrayAdapter adapter = new ArrayAdapter(DetailActivity.this, R.layout.item_luckyid,
                                                        luckyId);
                                                number_gv.setAdapter(adapter);
                                                break;
                                        }
                                        break;
                                }

                            }
                            break;
                    }
            }
        };

        private String[] buyid;
        private long user_id_winner = 0;
        @Event({R.id.detail_back_iv, R.id.detail_share_ib, R.id.detail_cart_iv,
                R.id.participate_btn,R.id.detail_add_list_btn,R.id.go_latest_issue_btn,
                R.id.detail_with_pic_rl,R.id.detail_unveil_with_his_rl,R.id.detail_with_share_rl,
                R.id.detail_calculate_btn,R.id.detail_unveil_caculate_btn,
                R.id.detail_unveil_user_head_iv,R.id.detail_check_all_tv})
        private void viewClick(View view) {
            Intent intent = new Intent();
            switch (view.getId()) {
                case R.id.detail_back_iv:
                    setResult(Constant.RESULT_CODE_UPDATE);
                    this.finish();
                    break;
                case R.id.detail_share_ib:
                    menuWindow = new PopupWindow_Share(this, itemsOnClick);
                    menuWindow.showAtLocation(findViewById(R.id.activity_detail_rl),
                            Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, 0);
                    break;
                case R.id.detail_cart_iv:
                    setResult(Constant.RESULT_CODE_CART);
                    this.finish();
                    break;
                case R.id.participate_btn:
                    menuWindow_buy = new PopupWindow_Buy(this, itemsOnClick_buy);
                    menuWindow_buy.showAtLocation(findViewById(R.id.activity_detail_rl),
                            Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, 0);
                    initCount();
                    break;
                case R.id.detail_add_list_btn:
                    try {
                        AwardModel awardModel = new AwardModel();

                        if(bundle.isEmpty()) return;

                        if( bundle.getSerializable("bundle") instanceof AwardModel){
                            awardModel = (AwardModel) bundle.getSerializable("bundle");
                        }
                        if(awardModel == null) return;
                        AwardModel model_db = db.selector(AwardModel.class).where("timesid", "=", awardModel.getTimeid()).findFirst();
                        Log.e("model_db", model_db + "");
                        if (model_db == null) {
                            awardModel.setCopies(1);
                            db.save(awardModel);
                        } else {
                            long count = model_db.getCopies();
                            count++;
                            db.update(AwardModel.class, WhereBuilder.b("timesid", "=", awardModel.getTimeid()),new KeyValue("copies", count));
                        }
                        updateCart();
                        Utility.toastShow(DetailActivity.this,R.string.add_success);
                    } catch (DbException e) {
                        e.printStackTrace();
                    }
                    break;
                case R.id.detail_with_pic_rl:
                    intent.setClass(this,WebActivity.class);
                    intent.putExtra(Constant.WEB_H5,Constant.DETAIL_PHOTO);
                    intent.putExtras(bundle);
                    startActivity(intent);
                    break;
                case R.id.detail_unveil_with_his_rl:
                    intent.setClass(this,WebActivity.class);
                    intent.putExtra(Constant.WEB_H5,Constant.DETAIL_UNVEIL);
                    intent.putExtras(bundle);
                    startActivity(intent);
                    break;
                case R.id.detail_with_share_rl:
                    intent.setClass(this,DetailBaskActivity.class);
                    intent.putExtras(bundle);
                    startActivity(intent);
                    break;
                case R.id.detail_calculate_btn:
                case R.id.detail_unveil_caculate_btn:
                    intent.setClass(this,WebActivity.class);
                    intent.putExtra(Constant.WEB_H5,Constant.DETAIL_CALCULATE);
                    intent.putExtras(bundle);
                    startActivity(intent);
                    break;
                case R.id.go_latest_issue_btn:
                    AwardModel awardModel = (AwardModel) bundle.getSerializable("bundle");
                    if(awardModel == null) return;
                    String goodid = awardModel.getIdx() + "";
                    obatainLatestIssueAward(goodid);
                    break;
                case R.id.detail_unveil_user_head_iv:
                    FriendsModel model = new FriendsModel();
                    model.setUidx(user_id_winner);
                    Bundle bundle = new Bundle();
                    bundle.putSerializable("bundle",model);
                    intent.setClass(this,SNS_FriendsActivity.class);
                    intent.putExtras(bundle);
                    startActivity(intent);
                    break;
                case R.id.detail_check_all_tv:
                    showLuckyId(buyid);
                    break;

            }
        }

    //---------------------Share commodity popup window-----------------------
        private CallbackManager callbackManager;
        private ShareDialog shareDialog;
        private MessageDialog messageDialog;
        private String commodityName;
        private void share_facebook(String content,String pictureUrl,String appUrl){

           if (ShareDialog.canShow(ShareLinkContent.class)) {
                ShareLinkContent linkContent = new ShareLinkContent.Builder()
                    .setContentTitle(getString(R.string.app_name))
                    .setContentDescription(content)
                    .setImageUrl(Uri.parse(pictureUrl))
                    .setContentUrl(Uri.parse(appUrl))
                    .build();

                shareDialog.show(linkContent);
            }
        }

        FacebookCallback<Sharer.Result> facebookCallback_share = new FacebookCallback<Sharer.Result>() {

            @Override
            public void onSuccess(Sharer.Result result) {

            }

            @Override
            public void onCancel() {

            }

            @Override
            public void onError(FacebookException error) {

            }
        };

        private void share_messenger(String content,String pictureUrl,String appUrl){
            if(!Utility.isAvilible(this,"com.facebook.orca")) {
                Utility.toastShow(x.app(), R.string.messenger_not_install);
                return;
            }

            if (ShareDialog.canShow(ShareLinkContent.class)) {
                ShareLinkContent linkContent = new ShareLinkContent.Builder()
                    .setContentTitle(getString(R.string.app_name))
                    .setContentDescription(content)
                    .setImageUrl(Uri.parse(pictureUrl))
                    .setContentUrl(Uri.parse(appUrl))
                    .build();

                messageDialog.show(linkContent);
            }
        }

        FacebookCallback<Sharer.Result> facebookCallback_messenger = new FacebookCallback<Sharer.Result>() {

            @Override
            public void onSuccess(Sharer.Result result) {
                    Utility.toastShow(x.app(),R.string.share_success);
            }

            @Override
            public void onCancel() {
                String str = "cancel";
            }

            @Override
            public void onError(FacebookException error) {
                String str = error.getMessage();
                Log.e(TAG, "onError: "+ str );
               // Utility.toastShow(x.app(),R.string.messenger_not_install);
            }
        };

        private PopupWindow_Share menuWindow;

        private View.OnClickListener itemsOnClick = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 隐藏弹出窗口
                menuWindow.dismiss();
                String content = getString(R.string.detail_share_content,commodityName,"");
                String pictureUrl = data.get(0).getImg();
                String appUrl = getString(R.string.app_url_share);

                ShareUtils.setPictureUrl(pictureUrl);
                ShareUtils.setContent(commodityName,appUrl);
                switch (v.getId()) {
                    case R.id.share_facebook_rl:
                        share_facebook(content,pictureUrl,appUrl);
                        //ShareUtils.showShare(DetailActivity.this,"Facebook",false);
                        break;
                    case R.id.share_messenger_rl:
                        share_messenger(content,pictureUrl,appUrl);
                        //ShareUtils.showShare(DetailActivity.this,"FacebookMessenger",false);
                        break;
                    case R.id.share_twitter_rl:
                        ShareUtils.showShare(DetailActivity.this,"Twitter",false);
                        break;
                    case R.id.share_copy_rl:
                        ClipboardManager cm = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
                        // 将文本内容放到系统剪贴板里。
                        cm.setText(getString(R.string.copy_complete));
                        Utility.toastShow(x.app(),R.string.copy_complete);
                        break;
                    case R.id.share_cancel_btn:
                        break;
                }
            }
        };

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);
        switch (resultCode){
            case Constant.RESULT_CODE_UPDATE:
                updateCart();
                break;
        }
    }

    //--------------------Buy commodity popup window-------------------------------
        private EditText detail_single_num;
        private Button num1,num2, num3,num4;

        private long persize = 10;

        private void initCount(){
            detail_single_num = menuWindow_buy.getSingle_num();
            detail_single_num.setOnEditorActionListener(this);
            num1 = menuWindow_buy.getNum1();
            num2 = menuWindow_buy.getNum2();
            num3 = menuWindow_buy.getNum3();
            num4 = menuWindow_buy.getNum4();
            Button[] num = {num1,num2,num3,num4};
            for (int i = 0; i < num.length ; i++) {
                long size = persize*(i+1);
                num[i].setText(size + "");
            }
        }
        private void setCount(int multiple){
            long size = persize*multiple;
            String numStr = size + "";
            detail_single_num.setText(numStr);
        }
        private void plusCount(){
            AwardModel model = (AwardModel) bundle.getSerializable("bundle");
            if(model == null) return;
            long size = Long.valueOf(detail_single_num.getText().toString());
            if(size < model.getTotal()-model.getSaled())
                size++;
            String numStr = size + "";
            detail_single_num.setText(numStr);
        }
        private void minusCount(){
            long size = Long.valueOf(detail_single_num.getText().toString());
            if(size > 1)
                size--;
            String numStr = size + "";
            detail_single_num.setText(numStr);
        }

        private PopupWindow_Buy menuWindow_buy;

        private View.OnClickListener itemsOnClick_buy = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 隐藏弹出窗口
                //menuWindow_buy.dismiss();
                String etStr ;
                switch (v.getId()) {
                    case R.id.determine_btn:
                        try {
                            AwardModel awardModel = new AwardModel();

                            if(bundle.isEmpty()) return;

                            if( bundle.getSerializable("bundle") instanceof AwardModel){
                                awardModel = (AwardModel) bundle.getSerializable("bundle");
                            }
                            if(awardModel == null) return;
                            AwardModel model_db = db.selector(AwardModel.class).where("timesid", "=", awardModel.getTimeid()).findFirst();
                            Log.e("model_db", model_db + "");
                            //input content
                            String numStr = detail_single_num.getText().toString();
                            numStr = numStr.equals("") ? "0" : numStr;
                            long size = Long.valueOf(numStr);
                            if(size == 0) return;
                            long surplus = awardModel.getTotal()-awardModel.getSaled();
                            if(size > surplus)
                                size = surplus;
                            if (model_db == null) {
                                awardModel.setCopies(size);
                                db.save(awardModel);
                            } else {
                                db.update(AwardModel.class, WhereBuilder.b("timesid", "=", awardModel.getTimeid()),new KeyValue("copies", size));
                            }
                            updateCart();
                        } catch (DbException e) {
                            e.printStackTrace();
                        }
                        DetailActivity.this.setResult(Constant.RESULT_CODE_UPDATE);
                        DetailActivity.this.finish();
                        break;
                    case R.id.num1:
                        setCount(1);
                        break;
                    case R.id.num2:
                        setCount(2);
                        break;
                    case R.id.num3:
                        setCount(3);
                        break;
                    case R.id.num4:
                        setCount(4);
                        break;
                    case R.id.detail_plus_ib:
                        plusCount();
                        break;
                    case R.id.detail_minus_ib:
                        minusCount();
                        break;
                }
            }
        };

    //-------------------Win prize congratulation popup window---------------------
    private void congratulation(String issue, String title){
        menuWindow_win = new PopupWindow_Win(this, itemsOnClick_win);
        menuWindow_win.showAtLocation(findViewById(R.id.activity_detail_rl),
                Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, 0);
        TextView issue_tv = menuWindow_win.getIssue();
        TextView title_tv = menuWindow_win.getTitle();
        long issueL = Long.valueOf(issue);
        String issuefam = getString(R.string.issue);
        String issueStr = String.format(issuefam,issueL);
        issue_tv.setText(issueStr);
        title_tv.setText(title);
    }

    private PopupWindow_Win menuWindow_win;

    private View.OnClickListener itemsOnClick_win = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            // 隐藏弹出窗口
            menuWindow_win.dismiss();

            switch (v.getId()) {
                case R.id.win_prize_cancel_iv:
                    break;

            }
        }
    };

    Intent intent = new Intent();
    private void show_congratulation(){
        String goodid = "",timeid = "",title = "",subtitle = "";
        if (bundle.getSerializable("bundle") instanceof AwardModel) {
            AwardModel awardModel = (AwardModel) bundle.getSerializable("bundle");
            if(awardModel == null) return;
            goodid = awardModel.getIdx() + "";
            timeid = awardModel.getTimeid() + "";
            title = awardModel.getTitle();
            subtitle = awardModel.getSubtitle();
        }/*else if (bundle.getSerializable("bundle") instanceof UnveilAwardModel){
            UnveilAwardModel awardModel = (UnveilAwardModel) bundle.getSerializable("bundle");
            if(awardModel == null) return;
            goodid = awardModel.getIdx() + "";
            timeid = awardModel.getTimeid() + "";
        }*/

        //-----congratulation--
        if(intent.hasExtra("notify") && intent.getIntExtra("notify",-1) == 1){
            title = title + subtitle;
            congratulation(timeid,title);
            intent.putExtra("notify",-1);
        }
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if(hasFocus)
            show_congratulation();

    }

    //----------------------------------onCreate()----------------------------------
        private Bundle bundle = new Bundle();
        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            preferences = LoginUserUtils.getUserSharedPreferences(this);
            db = x.getDb(DB_Config.getDaoConfig());
            init_Share();
            init_Detail();
        }

    private void init_Share(){
        FacebookSdk.sdkInitialize(getApplicationContext());
        callbackManager = CallbackManager.Factory.create();
        shareDialog = new ShareDialog(this);
        shareDialog.registerCallback(callbackManager, facebookCallback_share);
        messageDialog = new MessageDialog(this);
        messageDialog.registerCallback(callbackManager, facebookCallback_messenger);
    }

    private void init_Detail(){
        //setScrollViewListener();

        String goodid = "",timeid = "",lastidx = "",pagesize = "";
        Intent intent = getIntent();
        this.intent = intent;
        Bundle bundle = intent.getExtras();
        this.bundle = bundle;

        if (bundle.getSerializable("bundle") instanceof AwardModel) {
            AwardModel awardModel = (AwardModel) bundle.getSerializable("bundle");
            if(awardModel == null) return;
            goodid = awardModel.getIdx() + "";
            timeid = awardModel.getTimeid() + "";
        }/*else if (bundle.getSerializable("bundle") instanceof UnveilAwardModel){
            UnveilAwardModel awardModel = (UnveilAwardModel) bundle.getSerializable("bundle");
            goodid = awardModel.getIdx() + "";
            timeid = awardModel.getTimeid() + "";
        }*/
        //-------- banner----
        setBanner();
        getAwardDetailHeader(goodid, timeid);

        //----- history-------
        if (datas == null) {
            lastidx = 0 + "";
        } else {
            lastidx = (datas.get(datas.size() - 1)).getIdx() + "";
        }

        pagesize = "10";
        setHistoryList();
        getHistoryInfo(goodid,timeid,lastidx,pagesize,false);

        //--commodity status---
        String user_id = preferences.getLong(Constant.USER_ID,0) + "";
        getDetailUnveilTnfo(user_id,goodid,timeid);

        updateCart();
        // add Scroll View Listener
        setScrollViewListener();
    }
    @ViewInject(R.id.detail_cart_amount)
    private BadgeView detail_cart_amount;
    private void updateCart(){
        try {
            List<AwardModel>  list = db.selector(AwardModel.class).findAll();
            int amount = list == null ? 0 : list.size();
            if(amount > 0){
                detail_cart_amount.setVisibility(View.VISIBLE);
                detail_cart_amount.setText(String.valueOf(amount));
            }else {
                detail_cart_amount.setVisibility(View.GONE);
            }
        } catch (DbException e) {
            e.printStackTrace();
        }
    }

    private void updateDetailUnveilInfo(){
        String goodid = "",timeid = "";
        if (bundle.getSerializable("bundle") instanceof AwardModel) {
            AwardModel awardModel = (AwardModel) bundle.getSerializable("bundle");
            if(awardModel == null) return;
            goodid = awardModel.getIdx() + "";
            timeid = awardModel.getTimeid() + "";
        }/*else if (bundle.getSerializable("bundle") instanceof UnveilAwardModel){
            UnveilAwardModel awardModel = (UnveilAwardModel) bundle.getSerializable("bundle");
            if(awardModel == null) return;
            goodid = awardModel.getIdx() + "";
            timeid = awardModel.getTimeid() + "";
        }*/
        String user_id = preferences.getLong(Constant.USER_ID,0) + "";
        getDetailUnveilTnfo(user_id,goodid,timeid);
    }

//------------------------setting banner------------------------------------------
        /**
         * @author Reepicheep
         * Created at 2016/5/26 17:42
         */
        @ViewInject(R.id.cmd_bannerVf)
        private ViewFlow viewFlow;
        @ViewInject(R.id.cmd_bannerFi)
        private CircleFlowIndicator indic;

        private void setBanner() {
            viewFlow.setFlowIndicator(indic);
            viewFlow.setAdapter(getBannerAdapter());
            viewFlow.setTimeSpan(4500);
            viewFlow.setSelection(3 * 1000); // 设置初始位置
            viewFlow.startAutoFlowTimer(); // 启动自动播放
        }

        /**
         * Update banner after getting the new banner information
         *
         * @author Reepicheep
         * Created at 2016/5/26 17:34
         */
        private void updateBanner(List<BannerModel> urls) {
            if (urls != null) {
                viewFlow.setmSideBuffer(urls.size()); // 实际图片张数，
                bannerAdapter.setData(urls);
                viewFlow.setAdapter(bannerAdapter);
                viewFlow.startAutoFlowTimer();
            }
        }

        private List<BannerModel> data;
        private BannerAdapter bannerAdapter;

        ListBaseAdapter<BannerModel> getBannerAdapter() {
            data = new ArrayList<>();
            bannerAdapter = new BannerAdapter(this, data);
            return bannerAdapter;
        }

        @ViewInject(R.id.detail_award_name_tv)
        private TextView award_name_tv;

        private void getAwardDetailHeader(String idx, String timeidx) {
            RequestParams params = new RequestParams(Constant.getBaseUrl() +
                    "page/good/times.ashx");
            params.addQueryStringParameter("idx", idx);
            params.addQueryStringParameter("timeidx", timeidx);
            Log.e("detail_header-param", params.toString());
            x.http().get(params, new Callback.CommonCallback<String>() {

                @Override
                public void onSuccess(String result) {
                    Map<String, Object> resultMap = null;
                    try {
                        resultMap = ParseData.parseDetailHeaderInfo(result);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    mHandler.obtainMessage(R.id.DETAIL_HEADER, resultMap).sendToTarget();
                }

                @Override
                public void onError(Throwable ex, boolean isOnCallback) {
                    Log.e("error", ex.getMessage());
                    if (ex instanceof HttpException) { // 网络错误
                        HttpException httpEx = (HttpException) ex;
                        int responseCode = httpEx.getCode();
                        String responseMsg = httpEx.getMessage();
                        String errorResult = httpEx.getResult();
                        // ...
                    } else { // 其他错误
                        // ...
                    }
                }

                @Override
                public void onCancelled(CancelledException cex) {

                }

                @Override
                public void onFinished() {

                }
            });
        }

        //==============Going============================
        @ViewInject(R.id.detail_progress_ll)
        private LinearLayout detail_progress_ll;

        @ViewInject(R.id.detail_total_demand_tv)
        private TextView demand_tv;

        @ViewInject(R.id.detail_issue_tv)
        private TextView issue_tv;

        @ViewInject(R.id.detail_surplus_tv)
        private TextView surplus_tv;

        @ViewInject(R.id.detail_pb)
        private ProgressBar detail_pb;

        //===============CountDown=========================
        @ViewInject(R.id.detail_countdown_rl)
        private RelativeLayout detail_countdown_rl;

        @ViewInject(R.id.detail_countdown_issue_tv)
        private TextView cd_issue_tv;

        @ViewInject(R.id.detail_countdown_tv)
        private TextView countdown_tv;

        @ViewInject(R.id.detail_calculate_btn)
        private Button calculate_btn;

        //================Unveil============================
        @ViewInject(R.id.detail_unveil_rl)
        private RelativeLayout detail_unveil_rl;

        @ViewInject(R.id.detail_unveil_user_head_iv)
        private ImageView head_iv;

        @ViewInject(R.id.winner_tv)
        private TextView winner_tv;

        @ViewInject(R.id.detail_unveil_userId_tv)
        private TextView userId_tv;

        @ViewInject(R.id.detail_unveil_issue_tv)
        private TextView unveil_issue_tv;

        @ViewInject(R.id.detail_unveil_part_count_tv)
        private TextView part_count_tv;

        @ViewInject(R.id.detail_unveil_time_tv)
        private TextView deadline_tv;

        @ViewInject(R.id.lucky_number_tv)
        private TextView lucky_num_tv;

        @ViewInject(R.id.detail_calculate_btn)
        private Button unveil_cal_btn;

        private View[] unveilViews = new View[3];

        private void showUnveilView(int index) {

                unveilViews[0] = detail_progress_ll;
                unveilViews[1] = detail_countdown_rl;
                unveilViews[2] = detail_unveil_rl;

            for (int i = 0; i < unveilViews.length; i++) {
                unveilViews[i].setVisibility(View.GONE);
                if (i == index) {
                    unveilViews[i].setVisibility(View.VISIBLE);
                }

            }
        }

        //================User State============================
        @ViewInject(R.id.detail_number_ll)
        private LinearLayout detail_number_ll;

        @ViewInject(R.id.detail_purchase_count_tv)
        private TextView count_tv;

        @ViewInject(R.id.detail_check_all_tv)
        private TextView detail_check_all_tv;

        @ViewInject(R.id.detail_rob_number_gv)
        private GridView number_gv;

        @ViewInject(R.id.detail_tip_ll)
        private LinearLayout detail_tip_ll;

        @ViewInject(R.id.tip_no_login_or_part)
        private TextView tip_state;

        private View[] userStateViews = new View[2];

        private void showStateView(int state) {

                userStateViews[0] = detail_tip_ll;
                userStateViews[1] = detail_number_ll;

            for (int i = 0; i < userStateViews.length; i++) {
                userStateViews[i].setVisibility(View.GONE);

                if (i == state) {
                    userStateViews[i].setVisibility(View.VISIBLE);
                }
            }
        }

        private void getDetailUnveilTnfo(String uidx, String idx, String timeidx) {
            RequestParams params = new RequestParams(Constant.getBaseUrl() +
                    "page/good/times.ashx");

            params.addQueryStringParameter("isprize", "true");
            params.addQueryStringParameter("uidx", uidx);
            params.addQueryStringParameter("idx", idx);
            params.addQueryStringParameter("timeidx", timeidx);
            x.http().get(params, new Callback.CommonCallback<String>() {

                @Override
                public void onSuccess(String result) {
                    Map<String, Object> resultMap = ParseData.parseDetailUnveilInfo(result);
                    mHandler.obtainMessage(R.id.DETAIL_UNVEIL, resultMap).sendToTarget();
                }

                @Override
                public void onError(Throwable ex, boolean isOnCallback) {
                    Log.e("error", ex.getMessage());
                    if (ex instanceof HttpException) { // 网络错误
                        HttpException httpEx = (HttpException) ex;
                        int responseCode = httpEx.getCode();
                        String responseMsg = httpEx.getMessage();
                        String errorResult = httpEx.getResult();
                        // ...
                    } else { // 其他错误
                        // ...
                    }
                }

                @Override
                public void onCancelled(CancelledException cex) {

                }

                @Override
                public void onFinished() {

                }
            });
        }

        //=================History=============================
        @ViewInject(R.id.detail_checkin_time_tv)
        private TextView checkIn_time_tv;

        @ViewInject(R.id.detail_his_lv)
        private ListView listView;
        private ListBaseAdapter<DetailHistoryModel> adapter;
        private List<DetailHistoryModel> datas;

        private void setHistoryList() {
            listView.setAdapter(getListAdapter());
            listView.setOnItemClickListener(this);
        }

        private ListBaseAdapter<DetailHistoryModel> getListAdapter() {
            datas = new ArrayList<>();
            adapter = new DetailHistoryAdapter(this, datas);
            return adapter;
        }

        private void getHistoryInfo(String goodid, String timeid, String lastidx, String pagesize,final boolean orientation) {
            RequestParams params = new RequestParams(Constant.getBaseUrl() + "page/good/joinlist.ashx");
            params.addQueryStringParameter("goodid", goodid);
            params.addQueryStringParameter("timeid", timeid);
            params.addQueryStringParameter("lastidx", lastidx);
            params.addQueryStringParameter("pagesize", pagesize);
            Log.e("history-param", params.toString());
            x.http().get(params, new Callback.CommonCallback<String>() {

                @Override
                public void onSuccess(String result) {
                    Log.e("Detail", result);
                    Map<String, Object> resultMap = ParseData.parseDetailHistoryInfo(result);
                    resultMap.put("pull_up", orientation);
                    mHandler.obtainMessage(R.id.DETAIL_HISTORY, resultMap).sendToTarget();
                }

                @Override
                public void onError(Throwable ex, boolean isOnCallback) {
                    Log.e("error", ex.getMessage());
                    if (ex instanceof HttpException) { // 网络错误
                        HttpException httpEx = (HttpException) ex;
                        int responseCode = httpEx.getCode();
                        String responseMsg = httpEx.getMessage();
                        String errorResult = httpEx.getResult();
                        // ...
                    } else { // 其他错误
                        // ...
                    }
                }

                @Override
                public void onCancelled(CancelledException cex) {

                }

                @Override
                public void onFinished() {

                }
            });
        }

        @Override
        public void onStart() {
            super.onStart();

        }

        @Override
        public void onStop() {
            super.onStop();

        }

    @Override
    public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
        if (actionId == EditorInfo.IME_ACTION_DONE) {
           /*隐藏软键盘*/
            InputMethodManager imm = (InputMethodManager) v.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
            if (imm.isActive()) {
                imm.hideSoftInputFromWindow(v.getApplicationWindowToken(), 0);
            }
            AwardModel model = (AwardModel) bundle.getSerializable("bundle");
            if(model == null) return true;
            long size = Long.valueOf(v.getText().toString());
            long surplus = model.getTotal()-model.getSaled();
            if(size> surplus)
                size = surplus;
            String numStr = size + "";
            detail_single_num.setText(numStr);
            return true;
        }
        return false;
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        FriendsModel model = new FriendsModel();
        model.setUidx(datas.get(position).getUidx());
        Bundle bundle = new Bundle();
        bundle.putSerializable("bundle",model);
        Intent intent = new Intent(this,SNS_FriendsActivity.class);
        intent.putExtras(bundle);
        startActivity(intent);
    }

    class UnveilCountDownTimer extends CountDownTimer {

        String cdStr = getResources().getString(R.string.detail_countdown);

        public UnveilCountDownTimer(long millisInFuture,long countDownInterval) {
            super(millisInFuture, countDownInterval);
        }

        @Override
        public void onTick(long millisUntilFinished) {
            String minStr = "";
            String secStr = "";
            String mscStr = "";
            long minute = millisUntilFinished / (1000 * 60);
            if (minute >= 10) {
                minStr = minute + "";
            } else {
                minStr = "0" + minute;
            }

            long second = (millisUntilFinished - minute * 1000 * 60) / 1000;
            if (second >= 10) {
                secStr =  second+ "";
            } else {
                secStr = "0" + second;
            }

            long msecond = (millisUntilFinished - (minute* 60 + second)*1000) / 10;
            if ( msecond >= 10) {
                mscStr = msecond + "";
            } else {
                mscStr = "0" + msecond;
            }

            String str =  minStr + ":" + secStr + ":" + mscStr;

            String totalStr =  String.format(cdStr,str);
            TextAppearanceSpan textSpan = StringUtil.textSpan(DetailActivity.this,R.style.TextAppearanc_CountDown);

            int start = totalStr.indexOf("：")+1;
            int end = totalStr.length();
            SpannableStringBuilder builder = StringUtil.singleSpan(totalStr,start,end,textSpan);
            countdown_tv.setText(builder);
        }

        @Override
        public void onFinish() {
            String cd_tip = getString(R.string.calculating);
            countdown_tv.setText(cd_tip);
            updateDetailUnveilInfo();
            this.cancel();
        }
    }

    @ViewInject(R.id.single_btn_ll)
    private LinearLayout single_btn_ll;
    @ViewInject(R.id.double_btn_ll)
    private LinearLayout double_btn_ll;


    private void showBottomBtn(boolean ishow){
        int show = ishow ? 0 :1;
        switch(show){
            case 0:
                // immediately participate
                single_btn_ll.setVisibility(View.GONE);
                double_btn_ll.setVisibility(View.VISIBLE);
                break;
            case 1:
                // going next issue
                single_btn_ll.setVisibility(View.VISIBLE);
                double_btn_ll.setVisibility(View.GONE);
                break;
        }
    }

    private int RefreshType;
    private final int pull_up_refresh = 0;
    private final int pull_down_refresh = 1;

    @ViewInject(R.id.detail_scrollview)
    private PullToRefreshScrollView mPullRefreshScrollView;

    private void setScrollViewListener(){
        mPullRefreshScrollView.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener2<ScrollView>() {


            @Override
            public void onPullDownToRefresh(PullToRefreshBase<ScrollView> refreshView) {
                RefreshType = pull_down_refresh;
                new GetDataTask().execute();
            }

            @Override
            public void onPullUpToRefresh(PullToRefreshBase<ScrollView> refreshView) {
                RefreshType = pull_up_refresh;
                new GetDataTask().execute();
            }
        });
        mPullRefreshScrollView.setMode(PullToRefreshBase.Mode.BOTH);
//        mPullRefreshScrollView.getLoadingLayoutProxy(false, true).setPullLabel(getString(R.string.pull_to_load));
//        mPullRefreshScrollView.getLoadingLayoutProxy(false, true).setRefreshingLabel(getString(R.string.loading));
//        mPullRefreshScrollView.getLoadingLayoutProxy(false, true).setReleaseLabel(getString(R.string.release_to_load));
    }

    private class GetDataTask extends AsyncTask<Void, Void, String> {
        private String goodid,timeid,lastidx,pagesize;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            AwardModel awardModel = (AwardModel) bundle.getSerializable("bundle");
            if(awardModel == null) return;
            goodid = awardModel.getIdx() + "";
            timeid = awardModel.getTimeid() + "";

            long lastidxL = datas == null || datas.isEmpty() ? 0 : (datas.get(datas.size() - 1)).getIdx();
            lastidx = String.valueOf(lastidxL);
            pagesize = "10";
        }

        @Override
        protected String doInBackground(Void... params) {
            switch (RefreshType){
                case pull_down_refresh:
                    lastidx = "0";
                    getHistoryInfo(goodid,timeid,lastidx,pagesize,false);
                    break;
                case pull_up_refresh:
                    getHistoryInfo(goodid,timeid,lastidx,pagesize,true);
                    break;
            }


            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
            }
            return "";
        }

        @Override
        protected void onPostExecute(String result) {
            mPullRefreshScrollView.onRefreshComplete();
            super.onPostExecute(result);
        }
    }

    private void obatainLatestIssueAward(final String idx){
        RequestParams params = new RequestParams(Constant.getBaseUrl() + "page/good/detail.ashx");

        params.addQueryStringParameter("idx",idx);
        x.http().get(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                try {
                    Gson gson = new GsonBuilder().serializeNulls().create();
                    AwardModel model = gson.fromJson(result, new TypeToken<AwardModel>(){}.getType());
                    if(model.getTimeid() == 0){
                        Utility.toastShow(x.app(),R.string.commodity_pull_out_shelves);
                        return;
                    }
                    Bundle bundle = new Bundle();
                    bundle.putSerializable("bundle", model);
                    Intent intent = new Intent(DetailActivity.this, DetailActivity.class);
                    intent.putExtras(bundle);
                    startActivityForResult(intent,Constant.REQUEST_CODE);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {

            }

            @Override
            public void onCancelled(CancelledException cex) {

            }

            @Override
            public void onFinished() {

            }
        });
    }

    private void showCheckAll(final String timesid){
        RequestParams params = new RequestParams(Constant.getBaseUrl() + "page/ucenter/playno.ashx");
        long uidx = preferences.getLong(Constant.USER_ID,0);
        if(uidx == 0) return;
        params.addQueryStringParameter("uidx",uidx+"");
        params.addQueryStringParameter("timesid",timesid);

        x.http().get(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                Gson gson = new GsonBuilder().serializeNulls().create();
                Long[] id = gson.fromJson(result, new TypeToken<Long[]>(){}.getType());
                //showLuckyId(id);
            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {

            }

            @Override
            public void onCancelled(CancelledException cex) {

            }

            @Override
            public void onFinished() {

            }
        });
    }

    private void showLuckyId(String[] id ){
        Dialog_Check_all dialog = new Dialog_Check_all(DetailActivity.this);
        TextView title = dialog.getTitle();
        GridView content = dialog.getContent();

        String titleStr = getString(R.string.check_all_title);
        String lengthStr = String.valueOf(id.length);
        titleStr = String.format(titleStr,id.length);
        int index = titleStr.indexOf(lengthStr);
        SpannableString spanStr = new SpannableString(titleStr);
        ForegroundColorSpan fcs = new ForegroundColorSpan(getResources().getColor(R.color.light_red));
        spanStr.setSpan(fcs,index,index+lengthStr.length(), Spanned.SPAN_INCLUSIVE_INCLUSIVE);
        title.setText(spanStr);

        ArrayAdapter<String> adapter = new ArrayAdapter<>(DetailActivity.this,R.layout.item_luckyid,id);
        content.setAdapter(adapter);
    }
}

