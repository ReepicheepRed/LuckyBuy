/**
 * com.rightoo.li.login
 * LoginUserUtis.java
 * 2015年2月4日 下午3:36:30
 * @author: z```s
 */
package com.luckybuy.login;

import android.content.Context;
import android.content.SharedPreferences;

import com.facebook.AccessToken;
import com.facebook.login.LoginManager;
import com.igexin.sdk.PushManager;
import com.luckybuy.util.Constant;
import com.luckybuy.util.StringUtil;

import org.xutils.x;


/**
 * <p></p>
 * 2015年2月4日 Administrator
 * @author: z```s
 */
public class LoginUserUtils {

	
	/**
	 * <p>获取共享文件SharedPreferences</p>
	 * @param context
	 * @param name 文件名
	 * @return
	 * 2015年2月4日 下午3:40:20
	 * @author: z```s
	 */
	public static SharedPreferences getAppSharedPreferences(Context context, String name) {
		if (context == null || StringUtil.isNullOrEmpty(name)) {
			return null;
		}
		return context.getSharedPreferences(name, Context.MODE_APPEND);
	}
	

	/**
	 *<p>Get User Information<p>
	 * @param context
	 * @return
	 * @data 2016-1-17 下午10:12:54
	 * @author zhiPeng.s
	 */
	public static SharedPreferences getUserSharedPreferences(Context context) {
		return getAppSharedPreferences(context, Constant.USER_PREFERENCES_NAME);
	}

	public static void LoginOut() {
        SharedPreferences.Editor editor = LoginUserUtils.getUserSharedPreferences(x.app()).edit();
        AccessToken accessToken = AccessToken.getCurrentAccessToken();
        if (accessToken != null && !accessToken.isExpired()) {
            LoginManager.getInstance().logOut();
        }
        editor.clear();
        editor.commit();
        PushManager.getInstance().initialize(x.app());
	}
	
}