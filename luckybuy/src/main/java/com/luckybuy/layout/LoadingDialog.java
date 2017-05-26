package com.luckybuy.layout;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import com.luckybuy.R;

public class LoadingDialog extends ProgressDialog{

	private View view;
	private LayoutInflater inflater;
	private ImageView spaceshipImage;
	private Activity activity;

	
	
	public LoadingDialog(Activity activity) {
		super(activity, R.style.loading_dialog);
		// TODO Auto-generated constructor stub
		this.activity = activity;
        inflater = LayoutInflater.from(activity);
        view = inflater.inflate(R.layout.loading_dialog, null);
        spaceshipImage = (ImageView) view.findViewById(R.id.loading_img);  
        this.setCancelable(true);
        
	}

    public void setDialogCancelable(boolean cancelable){
        this.setCancelable(cancelable);
    }

    public void showDialog() {

        Animation hyperspaceJumpAnimation = 
        		AnimationUtils.loadAnimation(activity, R.anim.loading_animation);
        spaceshipImage.startAnimation(hyperspaceJumpAnimation);  
        this.show();

        DisplayMetrics metrics = new DisplayMetrics();
        Display display = activity.getWindowManager().getDefaultDisplay();
        display.getMetrics(metrics);
        float density = metrics.density; // 屏幕密度（0.75 / 1.0 / 1.5） 
        
        Window dialogWindow = this.getWindow();  
        dialogWindow.setContentView(view);
        dialogWindow.setGravity(Gravity.CENTER_HORIZONTAL | Gravity.CENTER_VERTICAL);
        
        WindowManager.LayoutParams lp = dialogWindow.getAttributes();  

        lp.y = (int) (-55*density);
        lp.width = (int) (90*density); // 宽度  
        lp.height = (int) (100*density); // 高度  
 
        dialogWindow.setAttributes(lp);  

    } 
}
