package com.luckybuy.guide;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import com.luckybuy.BaseActivity;
import com.luckybuy.MainActivity;
import com.luckybuy.R;
import com.luckybuy.login.LoginUserUtils;
import com.luckybuy.util.Constant;
import java.util.ArrayList;
import java.util.List;

public class GuideActivity extends BaseActivity implements OnPageChangeListener {

	private ViewPager mViewPager;
	private ViewPagerAdapter mViewPagerAdapter;
	private List<View> mViews;

	// 底部小点图片
	private ImageView[] mDots;

	// 记录当前选中位置
	private int mCurrentIndex;
	
	private boolean isScrolled ;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);		
		setContentView(R.layout.guide);

		// 初始化页面
		initViews();

		// 初始化底部小点
		//initDots();
	}

	private void initViews() {
		LayoutInflater inflater = LayoutInflater.from(this);

		mViews = new ArrayList<View>();
		// 初始化引导图片列表
		mViews.add(inflater.inflate(R.layout.guide_new_one, null));
		mViews.add(inflater.inflate(R.layout.guide_new_two, null));
		mViews.add(inflater.inflate(R.layout.guide_new_three, null));

		// 初始化Adapter
		mViewPagerAdapter = new ViewPagerAdapter(mViews, this);
		
		mViewPager = (ViewPager) findViewById(R.id.viewpager);
		mViewPager.setAdapter(mViewPagerAdapter);
		// 绑定回调
		mViewPager.setOnPageChangeListener(this);
	}

	private void initDots() {
		LinearLayout ll = (LinearLayout) findViewById(R.id.ll);

		mDots = new ImageView[mViews.size()];

		// 循环取得小点图片
		for (int i = 0; i < mViews.size(); i++) {
			mDots[i] = (ImageView) ll.getChildAt(i);
			mDots[i].setEnabled(true);// 都设为灰色
		}

		mCurrentIndex = 0;
		mDots[mCurrentIndex].setEnabled(false);// 设置为白色，即选中状态
	}

	private void setCurrentDot(int position) {
		if (position < 0 || position > mViews.size()
				|| mCurrentIndex == position) {
			return;
		}

		mDots[position].setEnabled(false);
		mDots[mCurrentIndex].setEnabled(true);

		mCurrentIndex = position;
	}

	// 当滑动状态改变时调用
	@Override
	public void onPageScrollStateChanged(int arg0) {
		
		if(arg0 == ViewPager.SCROLL_STATE_IDLE 
				&& mViewPager.getCurrentItem() == (mViews.size()-1)
				&& !isScrolled){

				goHome();

		}else if(arg0 == ViewPager.SCROLL_STATE_DRAGGING){
			isScrolled = false;
		}else if(arg0 == ViewPager.SCROLL_STATE_SETTLING){
			isScrolled = true;
		}
		
	}

	// 当当前页面被滑动时调用
	@Override
	public void onPageScrolled(int arg0, float arg1, int arg2) {
	}

	// 当新的页面被选中时调用
	@Override
	public void onPageSelected(int arg0) {
		// 设置底部小点选中状态
		//setCurrentDot(arg0);
	}
	
	private void goHome() {
		Intent intent = new Intent(this, MainActivity.class);
		startActivity(intent);
		finish();
	}

	private void goLogin() {
		/*Intent intent = new Intent(this, LoginActivity.class);
		startActivity(intent);
		finish();*/
	}

	@Override
	protected void onStop() {
		// TODO Auto-generated method stub
		super.onStop();
//		SharedPreferences loginFirstPreferences = LoginUserUtils.getAppSharedPreferences(
//				this, Constant.PREFERENCES_LOGIN_FIRST);
//		Editor editor = loginFirstPreferences.edit();
//		editor.putBoolean("isFirst", false);
//		editor.commit();
	}
	
	

}
