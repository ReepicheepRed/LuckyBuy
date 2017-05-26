package com.luckybuy.util;
import java.util.Locale;

public class Constant {

	public static final String VERSION = BasicConfig.VERSION;

	//Rquest Code & Result Code
	public static final int REQUEST_CODE = 0;
	public static final int RESULT_CODE = 0;
	public static final int REQUEST_CODE_DETAIL = 0;
	public static final int RESULT_CODE_CART = 1;
	public static final int RESULT_CODE_MINE = 2;
	public static final int RESULT_CODE_UPDATE = 3;

	//DATA KEY AFTER PARSING
	public static final String RETURN_CODE = "returnCode";
	public static final String RETURN_CONTENT = "returnContent";
	public static final String BANNER_LIST = "bannerList";
	public static final String BULLETIN_LIST = "bulletinList";
	public static final String AWARD_LIST = "awardList";
	public static final String DETAIL_HISTORY_LIST = "detailHistoryList";
	public static final String FRIENDS_LIST = "friendsList";
	public static final String DISCOVER_LIST = "discoverList";

    //DETAIL HEADER
	public static final String IDX = "idx";
	public static final String TIME_ID = "timesid";
	public static final String GOOD_ID = "goodid";
	public static final String SALED = "saled";
	public static final String TOTAL = "total";
	public static final String TITLE = "title";
	public static final String SUBTITLE = "subtitle";
	public static final String HEADPIC = "headpic";
	public static final String CHECK_IN_DATE = "checkindate";
	public static final String GOOD_IMG = "goodIMG";
	public static final String PERSIZE = "persize";


	//DETAIL STATUS
	public static final String STATUS = "status";
	public static final String DETAIL_UNVEIL_SOON = "unveil_soon";
	public static final String DETAIL_UNVEIL_ING = "unveil_ing";
	public static final String DETAIL_UNVEIL_END = "unveil_end";

	//WEB H5
	public static final String WEB_H5= "web_h5";
	public static final int DETAIL_PHOTO = 0;
	public static final int DETAIL_UNVEIL = 1;
	public static final int FAQ = 2;
	public static final int DETAIL_CALCULATE = 3;
	public static final int NOTIFY = 4;
	public static final int BASK_RULE = 5;
	public static final int BANNER = 6;
	public static final int PROTOCOL = 7;
	public static final int DIAMOND = 8;
	public static final int PAYSBUY = 9;
	public static final int WEB_URL = 10;




	// 共享数据SharedPreferences文件名
	public static final String USER_PREFERENCES_NAME = "application_user";
	public static final String PREFERENCES_RECORD = "application_record";
	public static final String PREFERENCES_LOGIN_FIRST = "login_first";

	//User Key
	public static final String USER_NAME = "user_name";
	public static final String USER_ID = "user_id";
	public static final String USER_ID_FB = "user_id_fb";
	public static final String USER_PHONE = "user_phone";
	public static final String CLIENT_ID = "client_id";
	public static final String LANGUAGE = "language";
    public static final String ORDER_NUM = "order_num";

    //Record Key
    public static final String RECORD = "record";
	public static final String BACKGROUND = "background";
	public static final String PAYMENT = "payment";

	//Cookie Key
	public static final String COOKIE_NAME = "LuckBuyCookies";
    public static final String ACCESS_TOKEN = "accesstoken";
    public static final String REFRESH_TOKEN = "refreshtoken";

	/**
	 * <p>
	 * 根据版本获取服务基础页面
	 * </p>
	 * 
	 * @return 2015年12月17日 下午5:44:33
	 * @author: z```s
	 */
	public static String getBaseUrl() {
		String baseUrl = "";
		if (VERSION.toLowerCase(Locale.ENGLISH).equals("debug")) {
			baseUrl = BasicConfig.DEBUG_BASE;
		} else {
			if (VERSION.toLowerCase(Locale.ENGLISH).equals("release")) {
				baseUrl = BasicConfig.RELEASE_BASE;
			} else {
				baseUrl = BasicConfig.DEMO_BASE;
			}
		}
		return baseUrl;
	}
}
