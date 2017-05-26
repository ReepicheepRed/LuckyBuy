package com.luckybuy.network;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.luckybuy.login.LoginUserUtils;
import com.luckybuy.util.Constant;

import org.xutils.http.RequestParams;
import org.xutils.http.cookie.DbCookieStore;
import org.xutils.x;

import java.net.HttpCookie;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by zhiPeng.S on 2016/9/7.
 */
public class TokenVerify {

    private static final String TAG = "TokenVerify";

    public static boolean addToken(Context context,RequestParams params){
        try{
            SharedPreferences preferences = LoginUserUtils.getUserSharedPreferences(context);
            params.addHeader("uidx",String.valueOf(preferences.getLong(Constant.USER_ID,0)));
            params.addHeader(Constant.ACCESS_TOKEN,preferences.getString(Constant.ACCESS_TOKEN,""));
            params.addHeader(Constant.REFRESH_TOKEN,preferences.getString(Constant.REFRESH_TOKEN,""));
        }catch (Exception e){
            e.printStackTrace();
            return false;
        }

        return true;
    }


    public static boolean saveCookie(Context context){
        try{
            SharedPreferences preferences = LoginUserUtils.getUserSharedPreferences(context);
            SharedPreferences.Editor editor = preferences.edit();
            // 保存cookie的值
            DbCookieStore instance = DbCookieStore.INSTANCE;
            List<HttpCookie> cookies = instance.getCookies();
            String cookieValue = "";
            for (int i = 0; i < cookies.size(); i++) {
                HttpCookie cookie = cookies.get(i);
                if (cookie.getName() != null&&cookie.getName().equals(Constant.COOKIE_NAME)) {
                    cookieValue = cookie.getValue();
                }
                Log.i(TAG , ": cookie name --> " + cookie.getName());
                Log.i(TAG , ": cookie value --> " + cookie.getValue());
            }

            Map<String,String> map = new HashMap<>();
            String[] value = cookieValue.split("&");
            for (int i = 0; i < value.length; i++) {
                String[] valueChild = value[i].split("=");
                map.put(valueChild[0],valueChild[1]);
            }
            if (map.containsKey(Constant.ACCESS_TOKEN)) {
                editor.putString(Constant.ACCESS_TOKEN, map.get(Constant.ACCESS_TOKEN));
            }

            if (map.containsKey(Constant.REFRESH_TOKEN)) {
                editor.putString(Constant.REFRESH_TOKEN, map.get(Constant.REFRESH_TOKEN));
            }
            editor.commit();
        }catch (Exception e){
            e.printStackTrace();
            return false;
        }

        return true;
    }
}
