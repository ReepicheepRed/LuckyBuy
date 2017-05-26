package com.luckybuy;

import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.TextView;
import android.widget.Toast;

import com.luckybuy.adapter.BaskPictureAdapter;
import com.luckybuy.adapter.ListBaseAdapter;
import com.luckybuy.layout.CustomAlertDialog;
import com.luckybuy.layout.LoadingDialog;
import com.luckybuy.util.Constant;
import com.luckybuy.util.Utility;

import org.xutils.common.Callback;
import org.xutils.ex.HttpException;
import org.xutils.http.RequestParams;
import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.Event;
import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by zhiPeng.S on 2016/6/14.
 */
@ContentView(R.layout.activity_feedback)
public class FeedbackActivity extends BaseActivity implements AdapterView.OnItemClickListener,View.OnClickListener{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        title_tv.setText(R.string.setting_feedback);
        commit.setVisibility(View.VISIBLE);
    }

    @ViewInject(R.id.title_activity)
    private TextView title_tv;

    @ViewInject(R.id.right_view)
    private TextView commit;

    CustomAlertDialog dialog;
    @Event({R.id.back_iv,R.id.right_view})
    private void viewClick(View view){
        switch(view.getId()){
            case R.id.back_iv:
                dialog = new CustomAlertDialog(this,this);
                dialog.setSubTitle(R.string.bask_dialog_subtitle);
                dialog.show();
                break;
            case R.id.right_view:
                break;
        }
    }


    Handler mHandler = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            @SuppressWarnings("unchecked")
            Map<String, Object> result = (Map<String, Object>) msg.obj;
            switch (msg.what) {
                case R.id.AWARD_SUCCESS:
                    if (!result.isEmpty()) {
                        @SuppressWarnings("unchecked")
                        List<String> showlist =
                                (List<String>) result.get(Constant.AWARD_LIST);
                        if(showlist == null){ return;}
                        if (!showlist.isEmpty()) {
                            datas.clear();
                            for (int i = 0; i < showlist.size(); i++) {
                                datas.add(showlist.get(i));
                            }
                        }
                        adapter.setData(datas);
                        adapter.notifyDataSetChanged();
                    } else {
                        String returnContent = (String) result.get(Constant.RETURN_CONTENT);
                        Utility.toastShow(x.app(), returnContent);
                    }
                    break;
            }
        }
    };

    @ViewInject(R.id.bask_picture_gv)
    private GridView gridView;
    private ListBaseAdapter<String> adapter;
    private List<String> datas;
    private void setShowList(){
        gridView.setAdapter(getShowListAdapter());
        gridView.setOnItemClickListener(this);
    }

    private ListBaseAdapter<String> getShowListAdapter(){
        datas = new ArrayList<>();
        datas.add("");
        adapter = new BaskPictureAdapter(this, datas);
        return adapter;
    }

    LoadingDialog loadingDialog;
    private void getShowListInfo(String uidx, String timesid, String content){
        loadingDialog = new LoadingDialog(this);
        loadingDialog.showDialog();
        RequestParams params = new RequestParams(Constant.getBaseUrl() +"Page/Ucenter/Bask.ashx?a=001");

        // 使用multipart表单上传文件
        params.setMultipart(true);
        for (int i = 0; i < datas.size(); i++) {
            if(!datas.get(i).equals("")){
                Utility.toastShow(this,datas.get(i));
                params.addBodyParameter(
                        "file"+i,
                        //new File("/sdcard/test.jpg"),
                        new File(datas.get(i)),
                        null); // 如果文件没有扩展名, 最好设置contentType参数.
            }
        }

        /*try {
            params.addBodyParameter(
                    "file2",
                    new FileInputStream(new File("/sdcard/test2.jpg")),
                    "image/jpeg",
                    // 测试中文文件名
                    "你+& \" 好.jpg"); // InputStream参数获取不到文件名, 最好设置, 除非服务端不关心这个参数.
        } catch (FileNotFoundException ex) {
            ex.printStackTrace();
        }*/

        params.addBodyParameter("uidx",uidx);
        params.addBodyParameter("timesid",timesid);
        params.addBodyParameter("content",content);

        x.http().post(params, new Callback.CommonCallback<String>() {

            @Override
            public void onSuccess(String result) {
                loadingDialog.dismiss();
                if(result.toUpperCase().equals("SUCCESS")){
                    Utility.toastShow(x.app(),R.string.bask_success);
                    FeedbackActivity.this.finish();
                }else{
                    Utility.toastShow(x.app(),R.string.bask_fail);
                }


                //Map<String, Object> resultMap = ParseData.parseUnveilAwardInfo(result);
                //mHandler.obtainMessage(R.id.AWARD_SUCCESS, resultMap).sendToTarget();
            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
                Toast.makeText(x.app(), ex.getMessage(), Toast.LENGTH_LONG).show();
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
                loadingDialog.dismiss();
            }
        });
    }


    private Uri photoUri;
    /** 使用照相机拍照获取图片 */
    public static final int SELECT_PIC_BY_TACK_PHOTO = 1;
    /** 使用相册中的图片 */
    public static final int SELECT_PIC_BY_PICK_PHOTO = 2;
    /** 获取到的图片路径 */
    private String picPath = "";
    private static ProgressDialog pd;

    private PopupWindow_Bask menuWindow;


    //为弹出窗口实现监听类
    private View.OnClickListener itemsOnClick = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            // 隐藏弹出窗口
            menuWindow.dismiss();

            switch (v.getId()) {
                case R.id.takePhotoBtn:// 拍照
                    takePhoto();
                    break;
                case R.id.pickPhotoBtn:// 相册选择图片
                    pickPhoto();
                    break;
                case R.id.cancelBtn:// 取消
                    break;
                default:
                    break;
            }
        }
    };


    /**
     * 拍照获取图片
     */
    private void takePhoto() {
        // 执行拍照前，应该先判断SD卡是否存在
        String SDState = Environment.getExternalStorageState();
        if (SDState.equals(Environment.MEDIA_MOUNTED)) {

            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            /***
             * 需要说明一下，以下操作使用照相机拍照，拍照后的图片会存放在相册中的
             * 这里使用的这种方式有一个好处就是获取的图片是拍照后的原图
             * 如果不使用ContentValues存放照片路径的话，拍照后获取的图片为缩略图不清晰
             */
            ContentValues values = new ContentValues();
            photoUri = getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
            intent.putExtra(android.provider.MediaStore.EXTRA_OUTPUT, photoUri);
            startActivityForResult(intent, SELECT_PIC_BY_TACK_PHOTO);
        } else {
            Toast.makeText(this, "内存卡不存在", Toast.LENGTH_LONG).show();
        }
    }

    /***
     * 从相册中取图片
     */
    private void pickPhoto() {
        Intent intent = new Intent();
        // 如果要限制上传到服务器的图片类型时可以直接写如："image/jpeg 、 image/png等的类型"
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, SELECT_PIC_BY_PICK_PHOTO);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // 点击取消按钮
        if(resultCode == RESULT_CANCELED){
            return;
        }

        // 可以使用同一个方法，这里分开写为了防止以后扩展不同的需求
        switch (requestCode) {
            case SELECT_PIC_BY_PICK_PHOTO:// 如果是直接从相册获取
                doPhoto(requestCode, data);
                break;
            case SELECT_PIC_BY_TACK_PHOTO:// 如果是调用相机拍照时
                doPhoto(requestCode, data);
                break;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    /**
     * 选择图片后，获取图片的路径
     *
     * @param requestCode
     * @param data
     */
    private void doPhoto(int requestCode, Intent data) {

        // 从相册取图片，有些手机有异常情况，请注意
        if (requestCode == SELECT_PIC_BY_PICK_PHOTO) {
            if (data == null) {
                Toast.makeText(this, "选择图片文件出错", Toast.LENGTH_LONG).show();
                return;
            }
            photoUri = data.getData();
            if (photoUri == null) {
                Toast.makeText(this, "选择图片文件出错", Toast.LENGTH_LONG).show();
                return;
            }
        }

        String[] pojo = { MediaStore.MediaColumns.DATA };
        // The method managedQuery() from the type Activity is deprecated
        //Cursor cursor = managedQuery(photoUri, pojo, null, null, null);
        Cursor cursor = FeedbackActivity.this.getContentResolver().query(photoUri, pojo, null, null, null);
        if (cursor != null) {
            int columnIndex = cursor.getColumnIndexOrThrow(pojo[0]);
            cursor.moveToFirst();
            picPath = cursor.getString(columnIndex);

            // 4.0以上的版本会自动关闭 (4.0--14;; 4.0.3--15)
            if (Build.VERSION.SDK_INT < 14) {
                cursor.close();
            }
        }

        // 如果图片符合要求将其上传到服务器
        if (picPath != null && (	picPath.endsWith(".png") ||
                picPath.endsWith(".PNG") ||
                picPath.endsWith(".jpg") ||
                picPath.endsWith(".JPG"))) {


            datas.remove(datas.size()-1);
            datas.add(picPath);
            if (datas.size() < 3)
                datas.add("");
            adapter.setData(datas);
            adapter.notifyDataSetChanged();



            BitmapFactory.Options option = new BitmapFactory.Options();
            // 压缩图片:表示缩略图大小为原始图片大小的几分之一，1为原图
            option.inSampleSize = 1;
            // 根据图片的SDCard路径读出Bitmap
            Bitmap bm = BitmapFactory.decodeFile(picPath, option);
            // 显示在图片控件上
            //picImg.setImageBitmap(bm);

            //pd = ProgressDialog.show(mContext, null, "正在上传图片，请稍候...");
            //new Thread(uploadImageRunnable).start();
        } else {
            Toast.makeText(this, "选择图片文件不正确", Toast.LENGTH_LONG).show();
        }

    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        if(position < 3 && position == datas.size()-1) {
            menuWindow = new PopupWindow_Bask(this, itemsOnClick);
            menuWindow.showAtLocation(findViewById(R.id.activity_bask_evaluate_ll),
                    Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, 0);
        }
    }

    @Override
    public void onClick(View v) {
        switch(v.getId()) {
            case R.id.alertDialog_ok_btn:
                this.finish();
                break;
            case R.id.alertDialog_cancel_btn:
                break;
        }
        dialog.dismiss();
    }


}
