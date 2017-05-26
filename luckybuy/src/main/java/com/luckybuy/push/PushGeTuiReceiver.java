package com.luckybuy.push;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;
import com.igexin.sdk.PushConsts;
import com.igexin.sdk.PushManager;
import com.luckybuy.DetailActivity;
import com.luckybuy.PopupWindow_Win;
import com.luckybuy.R;
import com.luckybuy.WebActivity;
import com.luckybuy.layout.PrizeDialog;
import com.luckybuy.login.LoginUserUtils;
import com.luckybuy.model.AwardModel;
import com.luckybuy.model.NotifyModel;
import com.luckybuy.util.Constant;
import com.luckybuy.util.Utility;

import org.json.JSONException;
import org.json.JSONObject;
import org.xutils.x;

public class PushGeTuiReceiver extends BroadcastReceiver {

    /**
     * 应用未启动, 个推 service已经被唤醒,保存在该时间段内离线消息(此时 GetuiSdkDemoActivity.tLogView == null)
     */
    public static StringBuilder payloadData = new StringBuilder();

    private SharedPreferences preferences;
    private SharedPreferences.Editor editor;
    private Notification notification;
    private Context context;

    /**
     * @param context
     * @param intent
     */
    @Override
    public void onReceive(Context context, Intent intent) {
        preferences = LoginUserUtils.getUserSharedPreferences(context);
        editor = preferences.edit();
        this.context = context;
        builder = new Notification.Builder(context).setTicker("显示于屏幕顶端状态栏的文本").setSmallIcon(R.mipmap.push);
        mNotification = builder.build();

        Bundle bundle = intent.getExtras();
        Log.d("GetuiSdkDemo", "onReceive() action=" + bundle.getInt("action"));

        switch (bundle.getInt(PushConsts.CMD_ACTION)) {
            case PushConsts.GET_MSG_DATA:
                // 获取透传数据
                // String appid = bundle.getString("appid");
                byte[] payload = bundle.getByteArray("payload");

                String taskid = bundle.getString("taskid");
                String messageid = bundle.getString("messageid");

                // smartPush第三方回执调用接口，actionid范围为90000-90999，可根据业务场景执行
                boolean result = PushManager.getInstance().sendFeedbackMessage(context, taskid, messageid, 90001);
                System.out.println("第三方回执接口调用" + (result ? "成功" : "失败"));

                if (payload != null) {
                    String data = new String(payload);

                    Log.e("GetuiSdkDemo", "receiver payload : " + data);

                    payloadData.append(data);
                    payloadData.append("\n");
                    Gson gson = new GsonBuilder().serializeNulls().create();
                    try {
                        NotifyModel model = gson.fromJson(data,new TypeToken<NotifyModel>(){}.getType());

                        if(model.getType() == 1){
                            SharedPreferences loginFirstPreferences = LoginUserUtils.getAppSharedPreferences(context, Constant.PREFERENCES_LOGIN_FIRST);
//                            boolean isBackground = Utility.isBackground(x.app());
                            boolean isBackground = loginFirstPreferences.getInt(Constant.BACKGROUND,0) == 0;
                            if (!isBackground){
                                showPrizeInfo(context,model);
                            }else {
                                sendNotification(context,model);
                            }
                        }else{
                            sendNotification(context,model);
                        }

                    }catch (Exception e){

                    }
                }
                break;

            case PushConsts.GET_CLIENTID:
                // 获取ClientID(CID)
                // 第三方应用需要将CID上传到第三方服务器，并且将当前用户帐号和CID进行关联，以便日后通过用户帐号查找CID进行消息推送
                String cid = bundle.getString("clientid");
                Log.e("clientid= ", cid);
                editor.putString(Constant.CLIENT_ID, cid);
                editor.commit();
                break;
            case PushConsts.GET_SDKONLINESTATE:
                boolean online = bundle.getBoolean("onlineState");
                Log.d("GetuiSdkDemo", "online = " + online);
                break;

            case PushConsts.SET_TAG_RESULT:
                String sn = bundle.getString("sn");
                String code = bundle.getString("code");

                String text = "设置标签失败, 未知异常";
                switch (Integer.valueOf(code)) {
                    case PushConsts.SETTAG_SUCCESS:
                        text = "设置标签成功";
                        break;

                    case PushConsts.SETTAG_ERROR_COUNT:
                        text = "设置标签失败, tag数量过大, 最大不能超过200个";
                        break;

                    case PushConsts.SETTAG_ERROR_FREQUENCY:
                        text = "设置标签失败, 频率过快, 两次间隔应大于1s";
                        break;

                    case PushConsts.SETTAG_ERROR_REPEAT:
                        text = "设置标签失败, 标签重复";
                        break;

                    case PushConsts.SETTAG_ERROR_UNBIND:
                        text = "设置标签失败, 服务未初始化成功";
                        break;

                    case PushConsts.SETTAG_ERROR_EXCEPTION:
                        text = "设置标签失败, 未知异常";
                        break;

                    case PushConsts.SETTAG_ERROR_NULL:
                        text = "设置标签失败, tag 为空";
                        break;

                    case PushConsts.SETTAG_NOTONLINE:
                        text = "还未登陆成功";
                        break;

                    case PushConsts.SETTAG_IN_BLACKLIST:
                        text = "该应用已经在黑名单中,请联系售后支持!";
                        break;

                    case PushConsts.SETTAG_NUM_EXCEED:
                        text = "已存 tag 超过限制";
                        break;

                    default:
                        break;
                }

                Log.d("GetuiSdkDemo", "settag result sn = " + sn + ", code = " + code);
                Log.d("GetuiSdkDemo", "settag result sn = " + text);
                break;
            case PushConsts.THIRDPART_FEEDBACK:
                /*
                 * String appid = bundle.getString("appid"); String taskid =
                 * bundle.getString("taskid"); String actionid = bundle.getString("actionid");
                 * String result = bundle.getString("result"); long timestamp =
                 * bundle.getLong("timestamp");
                 *
                 * Log.d("GetuiSdkDemo", "appid = " + appid); Log.d("GetuiSdkDemo", "taskid = " +
                 * taskid); Log.d("GetuiSdkDemo", "actionid = " + actionid); Log.d("GetuiSdkDemo",
                 * "result = " + result); Log.d("GetuiSdkDemo", "timestamp = " + timestamp);
                 */
                break;

            default:
                break;
        }
    }

//-------------------Win prize congratulation notification---------------------
    private Notification mNotification;
    private Notification.Builder builder;
    private final String TAG = getClass().getName();
    protected void sendNotification(Context mContext,NotifyModel model) {
        Log.e(TAG, "-----sendNotifications----");
        //      1.实例化intent和获取通知的服务。
        Intent mIntent = new Intent();
        NotificationManager notificationManager = (NotificationManager) mContext.getSystemService(Context.NOTIFICATION_SERVICE);
        switch (model.getType()){
            case 1:
                AwardModel awardModel = model.getContent();
                Bundle bundle = new Bundle();
                bundle.putSerializable("bundle", awardModel);
                mIntent.putExtra("notify",1);
                mIntent.putExtras(bundle);
                mIntent.setClass(mContext, DetailActivity.class);
                break;
            case 2:
                mIntent.putExtra(Constant.WEB_H5, Constant.NOTIFY);
                mIntent.putExtra("content", model.getContent());
                mIntent.setClass(mContext, WebActivity.class);
                break;
        }
        PendingIntent mPendingIntent = PendingIntent.getActivity(mContext, 0, mIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        //      2.设置通知的相应属性
		//mNotification.fullScreenIntent = mPendingIntent;//这句话的作用是决定通知是否自动弹出那个可跳转的activity。
        mNotification = builder.setContentIntent(mPendingIntent)
                .setSmallIcon(R.drawable.logo_200)
                .setLargeIcon(BitmapFactory.decodeResource(mContext.getResources(), R.drawable.logo_200))
                .setContentTitle(model.getTitle())
                .setContentText(model.getCtext())
                .setAutoCancel(true)
                .build();
        mNotification.defaults = Notification.DEFAULT_SOUND;
        //		3.唤醒通知
        notificationManager.notify(10,mNotification);
    }

//-------------------Win prize congratulation popup window---------------------
    private void congratulation(String issue, String title){
        menuWindow_win = new PopupWindow_Win(context, itemsOnClick_win);
        menuWindow_win.showAtLocation(null,
                Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, 0);

        TextView issue_tv = menuWindow_win.getIssue();
        TextView title_tv = menuWindow_win.getTitle();
        long issueL = Long.valueOf(issue);
        String issuefam = context.getString(R.string.issue);
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

//-------------------Win prize congratulation dialog---------------------
    private void showPrizeInfo(Context context, NotifyModel model){
        AwardModel awardModel = model.getContent();
        PrizeDialog dialog = new PrizeDialog(x.app());


        TextView issue_tv = dialog.getIssue();
        TextView title_tv = dialog.getTitle();

        long issueL = awardModel.getTimeid();
        String issuefam = context.getString(R.string.issue);
        String issueStr = String.format(issuefam,issueL);
        issue_tv.setText(issueStr);

        String title = awardModel.getTitle() + awardModel.getSubtitle();
        title_tv.setText(title);
    }
}
