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
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.TextView;

/**
 * All rights Reserved, Designed By GeofferySun 
 * @Title: 	PopupWindow_Bask.java
 * @Package sun.geoffery.uploadpic 
 * @Description:从底部弹出或滑出选择菜单或窗口
 * @author:	GeofferySun   
 * @date:	2015年1月15日 上午1:21:01 
 * @version	V1.0
 */
public class PopupWindow_Win extends PopupWindow {

	private TextView issue,title;
	private ImageView cancelBtn;
	private View mMenuView;


	@SuppressLint("InflateParams")
	public PopupWindow_Win(Context context, OnClickListener itemsOnClick) {
		super(context);
		LayoutInflater inflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		mMenuView = inflater.inflate(R.layout.popwin_win_prize, null);
        issue = (TextView) mMenuView.findViewById(R.id.win_prize_issue_tv);
        title = (TextView) mMenuView.findViewById(R.id.win_prize_title_tv);
		cancelBtn = (ImageView) mMenuView.findViewById(R.id.win_prize_cancel_iv);
		// 设置按钮监听
		cancelBtn.setOnClickListener(itemsOnClick);
		
		// 设置SelectPicPopupWindow的View
		this.setContentView(mMenuView);
		// 设置SelectPicPopupWindow弹出窗体的宽
		this.setWidth(LayoutParams.MATCH_PARENT);
		// 设置SelectPicPopupWindow弹出窗体的高
		this.setHeight(LayoutParams.MATCH_PARENT);
		// 设置SelectPicPopupWindow弹出窗体可点击
		this.setFocusable(true);
		// 设置SelectPicPopupWindow弹出窗体动画效果
		this.setAnimationStyle(R.style.PopupAnimation);
		// 实例化一个ColorDrawable颜色为半透明
		ColorDrawable dw = new ColorDrawable(0x80000000);
		// 设置SelectPicPopupWindow弹出窗体的背景
		this.setBackgroundDrawable(dw);
		// mMenuView添加OnTouchListener监听判断获取触屏位置如果在选择框外面则销毁弹出框
		/*mMenuView.setOnTouchListener(new OnTouchListener() {

			@Override
			@SuppressLint("ClickableViewAccessibility")
			public boolean onTouch(View v, MotionEvent event) {

				int height = mMenuView.findViewById(R.id.pop_layout).getTop();
				int y = (int) event.getY();
				if (event.getAction() == MotionEvent.ACTION_UP) {
					if (y < height) {
						dismiss();
					}
				}
				return true;
			}
		});*/
	}

    public TextView getIssue() {
        return issue;
    }

    public void setIssue(TextView issue) {
        this.issue = issue;
    }

    public TextView getTitle() {
        return title;
    }

    public void setTitle(TextView title) {
        this.title = title;
    }
}
