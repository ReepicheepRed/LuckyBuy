package com.luckybuy;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.media.Image;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.luckybuy.model.AppUpdateModel;
import com.luckybuy.util.Constant;
import com.luckybuy.util.UpdateManager;
import com.luckybuy.util.Utility;

import org.xutils.common.Callback;
import org.xutils.common.util.DensityUtil;
import org.xutils.http.RequestParams;
import org.xutils.image.ImageOptions;
import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.Event;
import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

import java.io.File;
import java.util.List;

/**
 * Created by zhiPeng.S on 2016/6/14.
 */
@ContentView(R.layout.activity_about_us)
public class AboutActivity extends BaseActivity{
    public static int version,serverVersion;
    public static String versionName,serverVersionName;
    private ProgressDialog pd_update;
    public static receiveVersionHandler handler;
    private UpdateManager manager = UpdateManager.getInstance();
    private ImageOptions imageOptions;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initView();
        updateApp();
    }

    @ViewInject(R.id.title_activity)
    private TextView title_tv;

    @ViewInject(R.id.about_version_tv)
    private TextView version_tv;

    @ViewInject(R.id.appQR_iv)
    private ImageView qr_iv;

    @Event({R.id.back_iv,R.id.about_update_rl})
    private void viewClick(View view){
        switch(view.getId()){
            case R.id.back_iv:
                this.finish();
                break;
            case R.id.about_update_rl:
                isUpdate = true;
                updateApp();
                break;
        }
    }

    private void initView(){
        title_tv.setText(R.string.setting_about);

        version = manager.getVersion(this);
        versionName = manager.getVersionName(this);
        String versionStr = getString(R.string.version, versionName);
        version_tv.setText(versionStr);

        imageOptions = new ImageOptions.Builder()
                .setSize(DensityUtil.dip2px(85), DensityUtil.dip2px(85))
                .setImageScaleType(ImageView.ScaleType.CENTER_CROP)
                .setLoadingDrawableId(R.mipmap.erweima)
                .setFailureDrawableId(R.mipmap.erweima)
                .build();

        pd_update = new ProgressDialog(AboutActivity.this);
        pd_update.setTitle(R.string.download_progress);
        pd_update.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        pd_update.setIndeterminate(false);
        handler = new receiveVersionHandler();
    }

    private ProgressDialog progressDialog;
    private void updateApp(){
        progressDialog = new ProgressDialog(this);
        if(isUpdate)
            progressDialog.show();
        RequestParams params = new RequestParams(Constant.getBaseUrl() + "/Handle/LBList/Version.ashx");
        params.addQueryStringParameter("ostype","android");
        x.http().get(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                progressDialog.dismiss();
                try {
                    Gson gson = new GsonBuilder().serializeNulls().create();
                    List<AppUpdateModel> model = gson.fromJson(result, new TypeToken<List<AppUpdateModel>>(){}.getType());
                    if(!isUpdate) {
                        updateAppQR(model.get(0));
                        return;
                    }
                    manager.compareVersion(AboutActivity.this,model.get(0));
                }catch (Exception e){
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

    private boolean isUpdate;

    private void updateAppQR(AppUpdateModel model){
        x.image().bind(qr_iv,model.getDownloadpic(),imageOptions);
    }




    @SuppressLint("HandlerLeak")
    public class receiveVersionHandler extends Handler {

        @Override
        public void handleMessage(Message msg) {
            if(!pd_update.isShowing())
                pd_update.show();
            pd_update.setProgress(msg.arg1);
            if(msg.arg1 == 100){
                pd_update.dismiss();
                Intent intent = new Intent(Intent.ACTION_VIEW);
                String path = Environment.getExternalStorageDirectory()+getString(R.string.app_url_native);
                intent.setDataAndType(Uri.fromFile(new File(path)),"application/vnd.android.package-archive");
                startActivity(intent);
            }

        }

    }


}
