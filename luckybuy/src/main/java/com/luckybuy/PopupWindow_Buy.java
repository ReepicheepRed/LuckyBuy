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
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;

/**
 * All rights Reserved, Designed By GeofferySun 
 * @Title: 	PopupWindow_Share.java
 * @Package sun.geoffery.uploadpic 
 * @Description:�ӵײ������򻬳�ѡ��˵��򴰿�
 * @author:	Zhipeng.S
 * @date:	2015��1��15�� ����1:21:01 
 * @version	V1.0
 */
public class PopupWindow_Buy extends PopupWindow {

	private EditText single_num;
	private ImageButton minus,plus;
	private Button num1,num2,num3,num4;
	private Button cancelBtn;
	private View mMenuView;

	@SuppressLint("InflateParams")
	public PopupWindow_Buy(Context context, OnClickListener itemsOnClick) {
		super(context);
		LayoutInflater inflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		mMenuView = inflater.inflate(R.layout.commodity_detail_pop_buy, null);
		single_num = (EditText) mMenuView.findViewById(R.id.detail_single_num);
        single_num.setSelection(single_num.getText().length());
		plus = (ImageButton) mMenuView.findViewById(R.id.detail_plus_ib);
		minus = (ImageButton) mMenuView.findViewById(R.id.detail_minus_ib);
		num1 = (Button) mMenuView.findViewById(R.id.num1);
		num2 = (Button) mMenuView.findViewById(R.id.num2);
		num3 = (Button) mMenuView.findViewById(R.id.num3);
		num4 = (Button) mMenuView.findViewById(R.id.num4);
		cancelBtn = (Button) mMenuView.findViewById(R.id.determine_btn);
		// ���ð�ť����
		single_num.setOnClickListener(itemsOnClick);
		plus.setOnClickListener(itemsOnClick);
		minus.setOnClickListener(itemsOnClick);
		num1.setOnClickListener(itemsOnClick);
		num2.setOnClickListener(itemsOnClick);
		num3.setOnClickListener(itemsOnClick);
		num4.setOnClickListener(itemsOnClick);
		cancelBtn.setOnClickListener(itemsOnClick);
		
		// ����SelectPicPopupWindow��View
		this.setContentView(mMenuView);
		// ����SelectPicPopupWindow��������Ŀ�
		this.setWidth(LayoutParams.MATCH_PARENT);
		// ����SelectPicPopupWindow��������ĸ�
		this.setHeight(LayoutParams.WRAP_CONTENT);
		// ����SelectPicPopupWindow��������ɵ��
		this.setFocusable(true);
		// ����SelectPicPopupWindow�������嶯��Ч��
		this.setAnimationStyle(R.style.PopupAnimation);
		// ʵ����һ��ColorDrawable��ɫΪ��͸��
		ColorDrawable dw = new ColorDrawable(0x80000000);
		// ����SelectPicPopupWindow��������ı���
		this.setBackgroundDrawable(dw);
		// mMenuView���OnTouchListener�����жϻ�ȡ����λ�������ѡ������������ٵ�����
		mMenuView.setOnTouchListener(new OnTouchListener() {

			@Override
			@SuppressLint("ClickableViewAccessibility")
			public boolean onTouch(View v, MotionEvent event) {

				int height = mMenuView.findViewById(R.id.buy_commodity_rl).getTop();
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

	public EditText getSingle_num() {
		return single_num;
	}

	public Button getNum1() {
		return num1;
	}

	public Button getNum2() {
		return num2;
	}

	public Button getNum3() {
		return num3;
	}

	public Button getNum4() {
		return num4;
	}
}
