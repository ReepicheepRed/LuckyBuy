/**
 * com.rightoo.util
 * StringUtil.java
 * 2014年10月29日 下午3:27:27
 * @author: z```s
 */
package com.luckybuy.util;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.text.style.StyleSpan;
import android.text.style.TextAppearanceSpan;
import android.text.style.URLSpan;
import android.text.style.UnderlineSpan;
import android.widget.TextView;

import com.luckybuy.R;

import org.xutils.x;

import java.text.DecimalFormat;
import java.util.List;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * <p>字符串工具类</p>
 * 2014年10月29日 下午3:27:27
 * @author: z```s
 */
public class StringUtil {

	/**
	 * <p>判断是否为空</p>
	 * @param arg
	 * @return
	 * 2014年10月29日 下午3:32:51
	 * @author: z```s
	 */
	public static boolean isNullOrEmpty(String arg) {
		boolean rel = false;
		if (arg == null || arg.isEmpty()) {
			rel = true;
		}
		return rel;
	}
	
	/**
	 * <p>验证手机号</p>
	 * @param phoneNo
	 * @return
	 * 2014年10月29日 下午3:28:24
	 * @author: z```s
	 */
	public static boolean isPhoneNo(String phoneNo) {
		if (isNullOrEmpty(phoneNo)) {
			return false;
		}
		//Pattern p = Pattern.compile("^((13[0-9])|(15[^4,\\D])|(18[0,5-9]))\\d{8}$");
		Pattern p = Pattern.compile("^0[\\d]{9}$");
        Matcher m = p.matcher(phoneNo);
        return m.matches();
	}
	
	/**
	 * <p>验证邮箱</p>
	 * @param email
	 * @return
	 * 2014年10月29日 下午3:29:33
	 * @author: z```s
	 */
	public static boolean isEmail(String email) {
		if (isNullOrEmpty(email)) {
			return false;
		}
		String regex = "^([a-zA-Z0-9]*[-_]?[a-zA-Z0-9]+)*@([a-zA-Z0-9]*[-_]?[a-zA-Z0-9]+)+[\\.][A-Za-z]{2,3}([\\.][A-Za-z]{2})?$";
        Pattern p = Pattern.compile(regex);
        Matcher m = p.matcher(email);
        return m.matches();
	}
	
	/**
	 * <p>验证是否为纯数字</p>
	 * @param number
	 * @return
	 * 2014年10月29日 下午3:33:26
	 * @author: z```s
	 */
	public static boolean isNumber(String number) {
		if (isNullOrEmpty(number)) {
			return false;
		}
		Pattern pattern = Pattern.compile("[0-9]*");
		return pattern.matcher(number).matches();
	}
	/*
	 * 保留三 位 小数
	 */
	public static String getDouble(String doubleStr){
		Double d=Double.valueOf(doubleStr);
		DecimalFormat df = new DecimalFormat("#.##"); 
		return df.format(d);
	}
	
	/**
	 * author administrator2016-4-30
     * 提取出城市或者县
     * @param city
     * @param district
     * @return
     */
    public static String extractLocation(final String city, final String district){
        return district.contains("县") ? district.substring(0, district.length() - 1) : city.substring(0, city.length() - 1);
    }


    public static ForegroundColorSpan fcSpan(int color){
        int fcs_color = x.app().getResources().getColor(color);
		return new ForegroundColorSpan(fcs_color);
    }

    public static StyleSpan stySpan(int type){
        int styleType = 0;
        switch (type){
            case 0:
                styleType = Typeface.NORMAL;
                break;
            case 1:
                styleType = Typeface.BOLD;
                break;
            case 2:
                styleType = Typeface.ITALIC;
                break;
            case 3:
                styleType = Typeface.BOLD_ITALIC;
                break;
        }
        return new StyleSpan(styleType);
    }

	public static TextAppearanceSpan textSpan(Context context,int appearance){
		return new TextAppearanceSpan(context,appearance);
	}

    public static URLSpan urlSpan(String string){
        return new URLSpan(string);
    }

    public static UnderlineSpan ulSpan(){
        return new UnderlineSpan();
    }

	public static SpannableStringBuilder singleSpan(String totalStr, String partStr, Object what){
        int start,end;
		SpannableStringBuilder builder = new SpannableStringBuilder(totalStr);
		try{
			start = totalStr.indexOf(partStr);
			end = start + partStr.length();
			builder.setSpan(what,start,end, Spanned.SPAN_INCLUSIVE_INCLUSIVE);
		}catch (Exception e){
			e.printStackTrace();
		}

        return builder;
	}

    public static SpannableStringBuilder singleSpan(String totalStr, int start, int end, Object what){
        SpannableStringBuilder builder = new SpannableStringBuilder(totalStr);
        builder.setSpan(what,start,end, Spanned.SPAN_INCLUSIVE_INCLUSIVE);
        return builder;
    }

	public static SpannableStringBuilder multiStrSpan(String totalStr, Object what, String... partStr){
		SpannableStringBuilder builder = new SpannableStringBuilder(totalStr);
		int[] start = new int[partStr.length],end = new int[partStr.length];
		for (int i = 0; i < partStr.length; i++) {
			start[i] = totalStr.indexOf(partStr[i]);
			end[i] = start[i] + partStr[i].length();
			builder.setSpan(what,start[i],end[i], Spanned.SPAN_INCLUSIVE_INCLUSIVE);
		}
		return builder;
	}

	public static SpannableStringBuilder mmStrSpan(String totalStr, List<String> partStr, List<Object> what) {
		SpannableStringBuilder builder = new SpannableStringBuilder(totalStr);
		int[] start = new int[partStr.size()],end = new int[partStr.size()];
		int size = what.size();
		for (int i = 0; i < partStr.size(); i++) {
			start[i] = totalStr.indexOf(partStr.get(i));
			end[i] = start[i] + partStr.get(i).length();
			if(i < size)
				builder.setSpan(what.get(i),start[i],end[i], Spanned.SPAN_INCLUSIVE_INCLUSIVE);
			else
				builder.setSpan(what.get(size-1),start[i],end[i], Spanned.SPAN_INCLUSIVE_INCLUSIVE);
		}
		return builder;
	}
}
