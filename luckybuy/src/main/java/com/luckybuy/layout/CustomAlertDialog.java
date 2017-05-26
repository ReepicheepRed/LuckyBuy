package com.luckybuy.layout;

import com.luckybuy.R;

import android.app.AlertDialog;
import android.content.Context;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;

public class CustomAlertDialog extends AlertDialog{
	private Button positivityBtn;
	private Button negativityBtn;
	private Button knowBtn;
	private TextView subtitleTv;
	
	
	public CustomAlertDialog(Context context, View.OnClickListener onClickListener) {
		this(context, R.layout.alertdialog,onClickListener);
	}
	
	public CustomAlertDialog(Context context, int layoutId,View.OnClickListener onClickListener) {
		super(context);
		
		this.setCancelable(false);
		this.setCanceledOnTouchOutside(false);
		this.show();

		Window window = this.getWindow();
		window.setContentView(layoutId);
		window.getWindowManager().getDefaultDisplay().getWidth();

		if (layoutId == R.layout.alertdialog) {
			positivityBtn = (Button) window.findViewById(R.id.alertDialog_ok_btn);		
			negativityBtn = (Button) window.findViewById(R.id.alertDialog_cancel_btn);
			subtitleTv = (TextView) window.findViewById(R.id.alertDialog_subTitle);
			positivityBtn.setOnClickListener(onClickListener);
            negativityBtn.setOnClickListener(onClickListener);
		} else if (layoutId == R.layout.alertdialog_single){
			knowBtn = (Button) window.findViewById(R.id.alertDialog_cancel_btn);
			subtitleTv = (TextView) window.findViewById(R.id.alertDialog_subTitle_single);
            knowBtn.setOnClickListener(onClickListener);
		}
		
	}
	
	public Button getPositivityButton(){
		return positivityBtn;		
	}
	
	public Button getNegativityButton(){
		return negativityBtn;		
	}
	
	public Button getKnowButton(){
		return knowBtn;		
	}
	
	public void setSubTitle(String subtitle){
		subtitleTv.setText(subtitle);	
	}
	
	public void setSubTitle(int subtitle){
		subtitleTv.setText(subtitle);	
	}
}
