package com.luckybuy.share;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.View;
import android.view.View.OnClickListener;
import cn.sharesdk.onekeyshare.OnekeyShare;
import cn.sharesdk.onekeyshare.OnekeyShareTheme;
import com.luckybuy.R;

import org.xutils.x;

public class ShareUtils {

	/**
	 * 演示调用ShareSDK执行分享
	 *
	 * @param context
	 * @param platformToShare  指定直接分享平台名称（一旦设置了平台名称，则九宫格将不会显示）
	 * @param showContentEdit  是否显示编辑页
	 */
	public static void showShare(Context context, String platformToShare, boolean showContentEdit) {
		OnekeyShare oks = new OnekeyShare();
		oks.setSilent(!showContentEdit);
		if (platformToShare != null) {
			oks.setPlatform(platformToShare);
		}
		//ShareSDK快捷分享提供两个界面第一个是九宫格 CLASSIC  第二个是SKYBLUE
		oks.setTheme(OnekeyShareTheme.CLASSIC);
		// 令编辑页面显示为Dialog模式
		oks.setDialogMode();
		// 在自动授权时可以禁用SSO方式
		oks.disableSSOWhenAuthorize();
		//oks.setAddress("12345678901"); //分享短信的号码和邮件的地址
		oks.setTitle("ShareSDK--Title");
		oks.setTitleUrl("http://mob.com");
		oks.setText("ShareSDK--文本");
        oks.setText(content);
		//oks.setImagePath("/sdcard/test-pic.jpg");  //分享sdcard目录下的图片
		oks.setImageUrl(randomPic()[0]);
        oks.setImageUrl(pictureUrl);
		oks.setUrl("http://www.mob.com"); //微信不绕过审核分享链接
		//oks.setFilePath("/sdcard/test-pic.jpg");  //filePath是待分享应用程序的本地路劲，仅在微信（易信）好友和Dropbox中使用，否则可以不提供
		oks.setComment("分享"); //我对这条分享的评论，仅在人人网和QQ空间使用，否则可以不提供
		oks.setSite("ShareSDK");  //QZone分享完之后返回应用时提示框上显示的名称
		oks.setSiteUrl("http://mob.com");//QZone分享参数
		oks.setVenueName("ShareSDK");
		oks.setVenueDescription("This is a beautiful place!");
		// 将快捷分享的操作结果将通过OneKeyShareCallback回调
		//oks.setCallback(new OneKeyShareCallback());
		// 去自定义不同平台的字段内容
		//oks.setShareContentCustomizeCallback(new ShareContentCustomizeDemo());
		// 在九宫格设置自定义的图标
		 Bitmap logo = BitmapFactory.decodeResource(context.getResources(), R.mipmap.logo_2x);
		 String label = "ShareSDK";
		 OnClickListener listener = new OnClickListener() {
		 	public void onClick(View v) {

		 	}
		 };
		 oks.setCustomerLogo(logo, label, listener);

		// 为EditPage设置一个背景的View
		//oks.setEditPageBackground(getPage());
		// 隐藏九宫格中的新浪微博
		// oks.addHiddenPlatform(SinaWeibo.NAME);

		// String[] AVATARS = {
				// 		"http://99touxiang.com/public/upload/nvsheng/125/27-011820_433.jpg",
				// 		"http://img1.2345.com/duoteimg/qqTxImg/2012/04/09/13339485237265.jpg",
				// 		"http://diy.qqjay.com/u/files/2012/0523/f466c38e1c6c99ee2d6cd7746207a97a.jpg",
				// 		"http://diy.qqjay.com/u2/2013/0422/fadc08459b1ef5fc1ea6b5b8d22e44b4.jpg",
				// 		"http://img1.2345.com/duoteimg/qqTxImg/2012/04/09/13339510584349.jpg",
				// 		"http://diy.qqjay.com/u2/2013/0401/4355c29b30d295b26da6f242a65bcaad.jpg" };
				// oks.setImageArray(AVATARS);              //腾讯微博和twitter用此方法分享多张图片，其他平台不可以

		// 启动分享
		oks.show(context);
	}

	private static String pictureUrl;

	private static String content;

    private String appUrl;

    public static void setPictureUrl(String pictureUrl) {
        ShareUtils.pictureUrl = pictureUrl;
    }

    public static void setContent(String content, String url) {
        ShareUtils.setContent(content,url,false);
    }

    public static void setContent(String content,String url,boolean isWin) {
        String contentStr = x.app().getString(R.string.detail_share_content,content,url);
        if(isWin)
            contentStr = x.app().getString(R.string.win_record_share_content,content,url);
        ShareUtils.content = contentStr;
    }

    public void setAppUrl(String appUrl) {
        this.appUrl = appUrl;
    }

    public static String[] randomPic() {
		String url = "http://git.oschina.net/alexyu.yxj/MyTmpFiles/raw/master/kmk_pic_fld/";
		String urlSmall = "http://git.oschina.net/alexyu.yxj/MyTmpFiles/raw/master/kmk_pic_fld/small/";
		String[] pics = new String[] {
				"120.JPG",
				"127.JPG",
				"130.JPG",
				"18.JPG",
				"184.JPG",
				"22.JPG",
				"236.JPG",
				"237.JPG",
				"254.JPG",
				"255.JPG",
				"263.JPG",
				"265.JPG",
				"273.JPG",
				"37.JPG",
				"39.JPG",
				"IMG_2219.JPG",
				"IMG_2270.JPG",
				"IMG_2271.JPG",
				"IMG_2275.JPG",
				"107.JPG"
		};
		int index = (int) (System.currentTimeMillis() % pics.length);
		return new String[] {
				url + pics[index],
				urlSmall + pics[index]
		};
	}

}
