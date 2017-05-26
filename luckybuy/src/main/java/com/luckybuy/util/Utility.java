package com.luckybuy.util;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.ActivityManager;
import android.app.Service;
import android.content.ComponentName;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffXfermode;
import android.graphics.RectF;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Toast;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.luckybuy.ChargeActivity;
import com.luckybuy.R;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.xutils.x;

final public class Utility {

    public static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.CHINA);

    public static final SimpleDateFormat FORMAT_TAKE_FOOD = new SimpleDateFormat("MM月dd日 HH:mm", Locale.CHINA);

    public static final SimpleDateFormat FORMAT_NUM = new SimpleDateFormat("yyyyMMddHHmmss", Locale.CHINA);

    public static int activityCount = 0;
    /**
     * 生成圆形头像图片
     *
     * @param context
     * @param source
     * @return
     */
    public static Bitmap getHeaderCircleImage(Context context, Bitmap source) {
        int min = (int) context.getResources().getDimension(R.dimen.image_header);
        return createCircleImage(source, min);
    }

    /**
     * 转成圆形图片，默认背景色
     *
     * @param source
     * @param min
     * @return
     */
    public static Bitmap createCircleBackGroundImage(Bitmap source, int min) {
        return createCircleBackGroundImage(source, min, -1);
    }

    /**
     * 转成圆形图片，指定背景色
     *
     * @param source
     * @param min
     * @return
     */
    public static Bitmap createCircleBackGroundImage(Bitmap source, int min, int color) {
        if (source != null) {
            final Paint paint = new Paint();
            paint.setAntiAlias(true);
            if (color != -1) {
                paint.setColor(color);
            } else {
                paint.setColor(Color.WHITE);
            }
            Bitmap target = Bitmap.createBitmap(min, min, Config.ARGB_8888);
            /**
             * 产生�?个同样大小的画布
             */
            Canvas canvas = new Canvas(target);
            /**
             * 首先绘制圆形
             */
            canvas.drawCircle(min / 2, min / 2, min / 2, paint);

            /**
             * 绘制图片
             */
            int w = source.getWidth();
            int h = source.getHeight();
            float scale = 1.0f;
            boolean scaled = false;
            if (w > h) {
                if (w > min) {
                    scale = (1.0f * w) / min;
                    scaled = true;
                }

            } else {
                if (h > min) {
                    scale = (1.0f * h) / min;
                    scaled = true;
                }
            }
            float cx;
            float cy;
            if (scaled) {
                Bitmap zoomed = zoomBitmapByScale(source, scale);
                w = zoomed.getWidth();
                h = zoomed.getHeight();
                cx = (1.0f * (min - w)) / 2.0f;
                cy = (1.0f * (min - h)) / 2.0f;
                canvas.drawBitmap(zoomed, cx, cy, paint);
            } else {
                cx = (1.0f * (min - w)) / 2.0f;
                cy = (1.0f * (min - h)) / 2.0f;
                canvas.drawBitmap(source, cx, cy, paint);
            }
            return target;
        }
        return null;
    }

    /**
     * 转成圆形图片，指定背景色
     *
     * @param source
     * @param min
     * @return
     */
    public static Bitmap createCircleImage(Bitmap source, int min) {
        if (source != null) {

            Bitmap target = Bitmap.createBitmap(min, min, Config.ARGB_8888);
            /**
             * 产生�?个同样大小的画布
             */
            Canvas canvas = new Canvas(target);

            /**
             * 绘制图片
             */
            int w = source.getWidth();
            int h = source.getHeight();
            float scale = 1.0f;
            boolean scaled = false;
            // float roundPx = 0;
            if (w > h) {
                /*
                 * if(w > min){ roundPx = h; }else{ roundPx = min; }
				 */
                scale = (1.0f * min) / w;
                scaled = true;

            } else {
                /*
                 * if(h > min){ roundPx = w; }else{ roundPx = min; }
				 */
                scale = (1.0f * min) / h;
                scaled = true;
            }

            Bitmap displayBitmap = source;
            if (scaled) {
                Bitmap zoomed = zoomBitmapByScale(source, scale);
                displayBitmap = zoomed;
            }

            final Paint paint = new Paint();
            paint.setColor(Color.GRAY);
            // 设置边缘光滑，去掉锯�?
            paint.setAntiAlias(true);
            // 宽高相等，即正方�?
            final RectF rect = new RectF(0, 0, min, min);
            // 通过制定的rect画一个圆角矩形，当圆角X轴方向的半径等于Y轴方向的半径时，
            // 且都等于r/2时，画出来的圆角矩形就是圆形
            canvas.drawRoundRect(rect, min / 2, min / 2, paint);
            // 设置当两个图形相交时的模式，SRC_IN为取SRC图形相交的部分，多余的将被去�?
            paint.setXfermode(new PorterDuffXfermode(Mode.SRC_IN));
            // canvas将bitmap画在backgroundBmp�?
            canvas.drawBitmap(displayBitmap, null, rect, paint);
            return target;
        }
        return null;
    }

    /**
     * 使用长宽缩放比缩�?
     *
     * @param srcBitmap
     * @return
     */
    public static Bitmap zoomBitmapByScale(Bitmap srcBitmap, float scale) {
        int srcWidth = srcBitmap.getWidth();
        int srcHeight = srcBitmap.getHeight();
        Matrix matrix = new Matrix();
        matrix.postScale(scale, scale);
        Bitmap resizedBitmap = Bitmap.createBitmap(srcBitmap, 0, 0, srcWidth, srcHeight, matrix, true);
        if (resizedBitmap != null) {
            return resizedBitmap;
        } else {
            return srcBitmap;
        }
    }

    /**
     * rotate imageView for specify angle
     * @author Reepicheep
     * Created at 2016/6/1 18:06
     */
    public static void rotateImage(ImageView img, float angle){
        Matrix matrix=new Matrix();
        img.setScaleType(ImageView.ScaleType.MATRIX);
        matrix.postRotate(angle);
        img.setImageMatrix(matrix);
    }

    /**
     * <p>
     * </p>
     *
     * @param context
     * @param resId   2015�?2�?2�? 下午4:41:38
     * @author: z```s
     */
    @SuppressLint("InflateParams")
    public static void toastShow(Context context, int resId) {
        Toast toast = Toast.makeText(context, "", Toast.LENGTH_SHORT);
        toast.setText(resId);
        toast.show();
    }

    public static void toastShow(Context context, CharSequence text) {
        Toast toast = Toast.makeText(context, "", Toast.LENGTH_SHORT);
        toast.setText(text);
        toast.show();
    }


    /**
     * <p/>
     * get approximate number for double
     * <p/>
     *
     * @param v
     * @param scale
     * @return
     * @data 2016-1-2 下午10:59:17
     * @author zhiPeng.s
     */
    public static double round(double v, int scale) {

        if (scale < 0) {
            throw new IllegalArgumentException("The scale must be a positive integer or zero");
        }


        BigDecimal b = new BigDecimal(Double.toString(v));

        BigDecimal one = new BigDecimal("1");

        return b.divide(one, scale, BigDecimal.ROUND_HALF_UP).doubleValue();

    }


    /**
     * <p/>
     * Get assets resource
     * <p/>
     *
     * @param context
     * @param fileName
     * @return 2016-1-12 上午10:33:29
     * @author zhiPeng.S
     */
    public static String getFileContent(Context context, String fileName) {
        String Content = "";
        try {
            InputStream in = context.getResources().getAssets().open(fileName);
            BufferedReader br = new BufferedReader(new InputStreamReader(in));
            StringBuffer sb = new StringBuffer();
            String str;
            while ((str = br.readLine()) != null) {
                sb.append(str);
            }

            Content = sb.toString();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return Content;

    }


    /**
     * 获取目录文件大小
     *
     * @param dir
     * @return
     */
    public static long getDirSize(File dir) {
        if (dir == null) {
            return 0;
        }
        if (!dir.isDirectory()) {
            return 0;
        }
        long dirSize = 0;
        File[] files = dir.listFiles();
        for (File file : files) {
            if (file.isFile()) {
                dirSize += file.length();
            } else if (file.isDirectory()) {
                dirSize += file.length();
                dirSize += getDirSize(file); // 递归调用继续统计
            }
        }
        return dirSize;
    }

    /**
     * 判断当前版本是否兼容目标版本的方法
     * @param VersionCode
     * @return
     */
    public static boolean isMethodsCompat(int VersionCode) {
        int currentVersion = android.os.Build.VERSION.SDK_INT;
        return currentVersion >= VersionCode;
    }

    @TargetApi(8)
    public static File getExternalCacheDir(Context context) {

        // return context.getExternalCacheDir(); API level 8

        // e.g. "<sdcard>/Android/data/<package_name>/cache/"

        return context.getExternalCacheDir();
    }

    /**
     * 转换文件大小
     *
     * @param fileS
     * @return B/KB/MB/GB
     */
    public static String formatFileSize(long fileS) {
        java.text.DecimalFormat df = new java.text.DecimalFormat("#.00");
        String fileSizeString = "";
        if (fileS < 1024) {
            fileSizeString = df.format((double) fileS) + "B";
        } else if (fileS < 1048576) {
            fileSizeString = df.format((double) fileS / 1024) + "KB";
        } else if (fileS < 1073741824) {
            fileSizeString = df.format((double) fileS / 1048576) + "MB";
        } else {
            fileSizeString = df.format((double) fileS / 1073741824) + "G";
        }
        return fileSizeString;
    }

    /**
     * 清除app缓存
     *
     * @param activity
     */
    public static void clearAppCache(Activity activity) {
        final Context ac = activity.getApplication();
        final Handler handler = new Handler() {
            public void handleMessage(Message msg) {
                if (msg.what == 1) {
                    toastShow(ac, R.string.clear_cache_success);
                } else {
                    toastShow(ac, R.string.clear_cache_fail);
                }
            }
        };
        Thread thread = new Thread() {
            public void run() {
                Message msg = new Message();
                try {
                    clearAppCache(ac);
                    msg.what = 1;
                } catch (Exception e) {
                    e.printStackTrace();
                    msg.what = -1;
                }
                handler.sendMessage(msg);
            }
        };
        thread.start();
    }

    //在项目中经常会使用到WebView 控件,当加载html 页面时,会在/data/data/package_name目录下生成database与cache 两个文件夹。请求的url 记录是保存在WebViewCache.db,而url 的内容是保存在WebViewCache 文件夹下

    /**
     * 清除app缓存
     */
    public static void clearAppCache(Context context)
    {
        //清除webview缓存
        @SuppressWarnings("deprecation")
        File file = new File(context.getCacheDir().getAbsolutePath() + "/WebViewCache");

        //先删除WebViewCache目录下的文件

        if (file != null && file.exists() && file.isDirectory()) {
            for (File item : file.listFiles()) {
                item.delete();
            }
            file.delete();
        }
        context.deleteDatabase("webview.db");
        context.deleteDatabase("webview.db-shm");
        context.deleteDatabase("webview.db-wal");
        context.deleteDatabase("webviewCache.db");
        context.deleteDatabase("webviewCache.db-shm");
        context.deleteDatabase("webviewCache.db-wal");
        //清除数据缓存
        clearCacheFolder(context.getFilesDir(),System.currentTimeMillis());
        clearCacheFolder(context.getCacheDir(),System.currentTimeMillis());
        //2.2版本才有将应用缓存转移到sd卡的功能
        if(isMethodsCompat(android.os.Build.VERSION_CODES.FROYO)){
            clearCacheFolder(getExternalCacheDir(context),System.currentTimeMillis());
        }

    }

    /**
     * 清除缓存目录
     * @param dir 目录
     * @param curTime 当前系统时间
     * @return
     */
    private static int clearCacheFolder(File dir, long curTime) {
        int deletedFiles = 0;
        if (dir!= null && dir.isDirectory()) {
            try {
                for (File child:dir.listFiles()) {
                    if (child.isDirectory()) {
                        deletedFiles += clearCacheFolder(child, curTime);
                    }
                    if (child.lastModified() < curTime) {
                        if (child.delete()) {
                            deletedFiles++;
                        }
                    }
                }
            } catch(Exception e) {
                e.printStackTrace();
            }
        }
        return deletedFiles;
    }

    public static void changeAppLanguage(Resources resources, String lanAtr) {
        Configuration config = resources.getConfiguration();
        DisplayMetrics dm = resources.getDisplayMetrics();
        switch (lanAtr) {
            case "th_TH":
                config.locale = new Locale("th", "TH");
                break;
            case "en_US":
                config.locale = Locale.ENGLISH;
                break;
            case "zh_CN":
                config.locale = Locale.CHINA;
                break;
            default:
                config.locale = Locale.getDefault();
                break;
        }
        resources.updateConfiguration(config, dm);
    }


    //版本名
    public static String getVersionName(Context context) {
        return getPackageInfo(context).versionName;
    }

    //版本号
    public static int getVersionCode(Context context) {
        return getPackageInfo(context).versionCode;
    }

    private static PackageInfo getPackageInfo(Context context) {
        PackageInfo pi = null;

        try {
            PackageManager pm = context.getPackageManager();
            pi = pm.getPackageInfo(context.getPackageName(),
                    PackageManager.GET_CONFIGURATIONS);

            return pi;
        } catch (Exception e) {
            e.printStackTrace();
        }

        return pi;
    }

    /**
     *判断当前应用程序处于前台还是后台
     */
    public static boolean isBackground(Context context) {
        ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningTaskInfo> tasks = am.getRunningTasks(1);
        if (!tasks.isEmpty()) {
            ComponentName topActivity = tasks.get(0).topActivity;
            if (!topActivity.getPackageName().equals(context.getPackageName())) {
                return true;
            }
        }
        return false;

    }

    public static boolean isAppBackground(Context context) {

        ActivityManager activityManager = (ActivityManager) context .getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningAppProcessInfo> appProcesses = activityManager.getRunningAppProcesses();
        for (ActivityManager.RunningAppProcessInfo appProcess : appProcesses) {
            if (appProcess.processName.equals(context.getPackageName())) {
                if (appProcess.importance == ActivityManager.RunningAppProcessInfo.IMPORTANCE_BACKGROUND) {
                    Log.i("Background App:", appProcess.processName);
                    return true;
                }else{
                    Log.i("Foreground App:", appProcess.processName);
                    return false;
                }
            }
        }
        return false;
    }

    /**
     * 判断app是否处于前台
     * @param context
     * @return
     */
    public static boolean isRunningForeground (Context context) {
        ActivityManager am = (ActivityManager)context.getSystemService(Context.ACTIVITY_SERVICE);
        ComponentName cn = am.getRunningTasks(1).get(0).topActivity;
        String currentPackageName = cn.getPackageName();
        if(!TextUtils.isEmpty(currentPackageName) && currentPackageName.equals(context.getPackageName())) {
            return true ;
        }
        return false ;
    }

    /**
     * 判断app是否处于前台
     * @param context
     * @return
     */
    public static boolean isAppForeground(Context context){
        ActivityManager activityManager = (ActivityManager) context.getSystemService(Service.ACTIVITY_SERVICE);
        List<ActivityManager.RunningAppProcessInfo> runningAppProcessInfoList = activityManager.getRunningAppProcesses();
        if (runningAppProcessInfoList==null){
            return false;
        }
        for (ActivityManager.RunningAppProcessInfo processInfo : runningAppProcessInfoList) {
            if (processInfo.processName.equals(context.getPackageName()) &&
                    processInfo.importance==ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND){
                return true;
            }
        }
        return false;
    }

    public static class VerifyCountDownTimer extends CountDownTimer {
        private String verifyCode;
        private String verifyTimer;
        private Button verify_code_btn;

        public VerifyCountDownTimer(long millisInFuture, long countDownInterval) {
            super(millisInFuture, countDownInterval);
            verifyCode = x.app().getString(R.string.verify_code);
        }

        @Override
        public void onTick(long millisUntilFinished) {
            verifyTimer = verifyCode + "(" + millisUntilFinished/1000 + ")";
            verify_code_btn.setText(verifyTimer);
        }

        @Override
        public void onFinish() {
            this.cancel();
            verify_code_btn.setText(verifyCode);
            verify_code_btn.setEnabled(true);
        }

        public void setButton(Button verify_code_btn) {
            this.verify_code_btn = verify_code_btn;
        }
    }

    /**
     * trim date format
     * @author Reepicheep
     * Created at 2016/9/2 11:39
     */
    public static String trimDate(String original_date){
        String date_trim = original_date;
        try{
            if(original_date.contains(".")){
                String[] date = original_date.split("\\.");
                date_trim = date[0].replace("T", " ");
            }else {
                date_trim = original_date.replace("T", " ");
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        return date_trim;
    }

    /**
     * update show view
     * @author Reepicheep
     * Created at 2016/9/2 11:39
     */
    public static void updateView(View view1, View view2, boolean hasData){
        if(hasData){
            view1.setVisibility(View.VISIBLE);
            view2.setVisibility(View.GONE);
        }else {
            view1.setVisibility(View.GONE);
            view2.setVisibility(View.VISIBLE);
        }
    }

    public static void updateView(View hideView, ViewGroup viewGroup,boolean hasData){
        RelativeLayout blank_bask_rl = (RelativeLayout) LayoutInflater.from(x.app()).inflate(R.layout.blank_bask,null);
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT);
        viewGroup.addView(blank_bask_rl,layoutParams);
        blank_bask_rl.setVisibility(View.GONE);
        if(!hasData){
            blank_bask_rl.setVisibility(View.VISIBLE);
            hideView.setVisibility(View.GONE);
            return;
        }

        blank_bask_rl.setVisibility(View.GONE);
        hideView.setVisibility(View.VISIBLE);
    }

    /**
     * obtain screen density
     * @author Reepicheep
     * Created at 2016/9/6 11:20
     */
    public static float obtainDensity(Activity activity){
        DisplayMetrics metrics = new DisplayMetrics();
        Display display = activity.getWindowManager().getDefaultDisplay();
        display.getMetrics(metrics);
        return metrics.density; // 屏幕密度（0.75 / 1.0 / 1.5）
    }

    /**
     * check if is payment
     * @author Reepicheep
     * Created at 2016/9/12 16:00
     */
    public static boolean chargeResultForPayment(Context context){
        Activity activity = (Activity)context;
        if(activity instanceof ChargeActivity){
            boolean isPayment = ((ChargeActivity)context).isPayment();
            if(isPayment)
                Utility.toastShow(x.app(),R.string.charge_success_title);
            return isPayment;
        }
        return false;
    }

    /**
     * check if is payment
     * @author Reepicheep
     * Created at 2016/9/12 16:00
     */
    public static boolean chargeResultForPayment(boolean isPayment){
        if(isPayment)
            Utility.toastShow(x.app(),R.string.charge_success_title);
        return isPayment;
    }

    public static boolean isAvilible( Context context, String packageName )
    {
        final PackageManager packageManager = context.getPackageManager();
        // 获取所有已安装程序的包信息
        List<PackageInfo> pinfo = packageManager.getInstalledPackages(0);
        for ( int i = 0; i < pinfo.size(); i++ )
        {
            Log.i("packageName", "isAvilible: " + pinfo.get(i).packageName);
            if(pinfo.get(i).packageName.equalsIgnoreCase(packageName))
                return true;
        }
        return false;
    }

    //jsonObject to hashMap
    public static void JsonObject2HashMap(JSONObject jo, List<Map<?, ?>> rstList) {
        for (Iterator<String> keys = jo.keys(); keys.hasNext();) {
            try {
                String key1 = keys.next();
                System.out.println("key1---" + key1 + "------" + jo.get(key1)
                        + (jo.get(key1) instanceof JSONObject) + jo.get(key1)
                        + (jo.get(key1) instanceof JSONArray));
                if (jo.get(key1) instanceof JSONObject) {

                    JsonObject2HashMap((JSONObject) jo.get(key1), rstList);
                    continue;
                }
                if (jo.get(key1) instanceof JSONArray) {
                    JsonArray2HashMap((JSONArray) jo.get(key1), rstList);
                    continue;
                }
                System.out.println("key1:" + key1 + "----------jo.get(key1):"
                        + jo.get(key1));
                json2HashMap(key1, jo.get(key1), rstList);

            } catch (JSONException e) {
                e.printStackTrace();
            }

        }

    }
    public static void JsonArray2HashMap(JSONArray joArr,
                                         List<Map<?, ?>> rstList) {
        for (int i = 0; i < joArr.length(); i++) {
            try {
                if (joArr.get(i) instanceof JSONObject) {

                    JsonObject2HashMap((JSONObject) joArr.get(i), rstList);
                    continue;
                }
                if (joArr.get(i) instanceof JSONArray) {

                    JsonArray2HashMap((JSONArray) joArr.get(i), rstList);
                    continue;
                }
                System.out.println("Excepton~~~~~");

            } catch (JSONException e) {
                e.printStackTrace();
            }

        }

    }

    public static void json2HashMap(String key, Object value,
                                    List<Map<?, ?>> rstList) {
        LinkedHashMap<String, Object> map = new LinkedHashMap<String, Object>();
        map.put(key, value);
        rstList.add(map);
    }

    public static Map<String, LinkedHashMap<String, Object>> parseData(String data)
    {

        try
        {
            return JSON.parseObject(data, new TypeReference< Map<String, LinkedHashMap<String, Object>>>(){});
        } catch (Exception e)
        {
            System.out.println(e);
        }

        return null;
    }

    public static int measureItemHeight(BaseAdapter adapter,int position,ListView view){
        try {
            View listItem = adapter.getView(position, null, view);
            listItem.measure(0, 0); // 计算子项View 的宽高
            return listItem.getMeasuredHeight()+ view.getDividerHeight();
        }catch (Exception e){
            e.printStackTrace();
            return 0;
        }

    }

}
