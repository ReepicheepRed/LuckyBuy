package com.luckybuy;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.PopupWindow;

/**
 * All rights Reserved, Designed By GeofferySun 
 * @Title: 	PopupWindow_Share.java
 * @Package sun.geoffery.uploadpic 
 * @Description:从底部弹出或滑出选择菜单或窗口
 * @author:	Zhipeng.S
 * @date:	2015年1月15日 上午1:21:01 
 * @version	V1.0
 */
public class PopupWindow_Share extends PopupWindow {

	private RelativeLayout share_facebook, share_messenger, share_twitter, share_copy;
	private Button cancelBtn;
	private View mMenuView;

	@SuppressLint("InflateParams")
	public PopupWindow_Share(Context context, OnClickListener itemsOnClick) {
		super(context);
		LayoutInflater inflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		mMenuView = inflater.inflate(R.layout.commodity_detail_pop_share, null);
		share_facebook = (RelativeLayout) mMenuView.findViewById(R.id.share_facebook_rl);
		share_messenger = (RelativeLayout) mMenuView.findViewById(R.id.share_messenger_rl);
		share_twitter = (RelativeLayout) mMenuView.findViewById(R.id.share_twitter_rl);
		share_copy = (RelativeLayout) mMenuView.findViewById(R.id.share_copy_rl);
		cancelBtn = (Button) mMenuView.findViewById(R.id.share_cancel_btn);
		// 设置按钮监听
		cancelBtn.setOnClickListener(itemsOnClick);
		share_messenger.setOnClickListener(itemsOnClick);
		share_facebook.setOnClickListener(itemsOnClick);
		share_twitter.setOnClickListener(itemsOnClick);
		share_copy.setOnClickListener(itemsOnClick);
		
		// 设置SelectPicPopupWindow的View
		this.setContentView(mMenuView);
		// 设置SelectPicPopupWindow弹出窗体的宽
		this.setWidth(LayoutParams.MATCH_PARENT);
		// 设置SelectPicPopupWindow弹出窗体的高
		this.setHeight(LayoutParams.WRAP_CONTENT);
		// 设置SelectPicPopupWindow弹出窗体可点击
		this.setFocusable(true);
		// 设置SelectPicPopupWindow弹出窗体动画效果
		this.setAnimationStyle(R.style.PopupAnimation);
		// 实例化一个ColorDrawable颜色为半透明
		ColorDrawable dw = new ColorDrawable(0x80000000);
		// 设置SelectPicPopupWindow弹出窗体的背景
		this.setBackgroundDrawable(dw);
		// mMenuView添加OnTouchListener监听判断获取触屏位置如果在选择框外面则销毁弹出框
		mMenuView.setOnTouchListener(new OnTouchListener() {

			@Override
			@SuppressLint("ClickableViewAccessibility")
			public boolean onTouch(View v, MotionEvent event) {

				int height = mMenuView.findViewById(R.id.detail_share_pop_rl).getTop();
				int y = (int) event.getY();
				if (event.getAction() == MotionEvent.ACTION_UP) {
					if (y < height) {
						dismiss();
					}
				}
				return true;
			}
		});

	}

}
